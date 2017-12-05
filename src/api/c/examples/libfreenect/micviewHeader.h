#include "libfreenect.h"
#include "libfreenect_audio.h"
#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <pthread.h>

#if defined(__APPLE__)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

pthread_t freenect_thread;
volatile int die = 0;

int window;

static freenect_context* f_ctx;
static freenect_device* f_dev;

typedef struct {
	int32_t* buffers[4];
	int max_samples;
	int current_idx;  // index to the oldest data in the buffer (equivalently, where the next new data will be placed)
	int new_data;
} capture;

capture state;

int paused = 0;

pthread_mutex_t audiobuf_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t audiobuf_cond = PTHREAD_COND_INITIALIZER;

int win_h, win_w;

void in_callback(freenect_device* dev, int num_samples,
                 int32_t* mic1, int32_t* mic2,
                 int32_t* mic3, int32_t* mic4,
                 int16_t* cancelled, void *unknown) {
	pthread_mutex_lock(&audiobuf_mutex);
	capture* c = (capture*)freenect_get_user(dev);
	if(num_samples < c->max_samples - c->current_idx) {
		memcpy(&(c->buffers[0][c->current_idx]), mic1, num_samples*sizeof(int32_t));
		memcpy(&(c->buffers[1][c->current_idx]), mic2, num_samples*sizeof(int32_t));
		memcpy(&(c->buffers[2][c->current_idx]), mic3, num_samples*sizeof(int32_t));
		memcpy(&(c->buffers[3][c->current_idx]), mic4, num_samples*sizeof(int32_t));
	} else {
		int first = c->max_samples - c->current_idx;
		int left = num_samples - first;
		memcpy(&(c->buffers[0][c->current_idx]), mic1, first*sizeof(int32_t));
		memcpy(&(c->buffers[1][c->current_idx]), mic2, first*sizeof(int32_t));
		memcpy(&(c->buffers[2][c->current_idx]), mic3, first*sizeof(int32_t));
		memcpy(&(c->buffers[3][c->current_idx]), mic4, first*sizeof(int32_t));
		memcpy(c->buffers[0], &mic1[first], left*sizeof(int32_t));
		memcpy(c->buffers[1], &mic2[first], left*sizeof(int32_t));
		memcpy(c->buffers[2], &mic3[first], left*sizeof(int32_t));
		memcpy(c->buffers[3], &mic4[first], left*sizeof(int32_t));
	}
	c->current_idx = (c->current_idx + num_samples) % c->max_samples;
	c->new_data = 1;
	pthread_cond_signal(&audiobuf_cond);
	pthread_mutex_unlock(&audiobuf_mutex);
}

void* freenect_threadfunc(void* arg) {
	while(!die && freenect_process_events(f_ctx) >= 0) {
		// If we did anything else in the freenect thread, it might go here.
	}
	freenect_stop_audio(f_dev);
	freenect_close_device(f_dev);
	freenect_shutdown(f_ctx);
	return NULL;
}

void DrawMicData() {
	if (paused)
		return;
	pthread_mutex_lock(&audiobuf_mutex);
	while(!state.new_data)
		pthread_cond_wait(&audiobuf_cond, &audiobuf_mutex);
	state.new_data = 0;
	// Draw:
	glClear(GL_COLOR_BUFFER_BIT);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	float xIncr = (float)win_w / state.max_samples;
	float x = 0.;
	int i;
	int base_idx = state.current_idx;

	// Technically, we should hold the lock until we're done actually drawing
	// the lines, but this is sufficient to ensure that the drawings align
	// provided we don't reallocate buffers.
	pthread_mutex_unlock(&audiobuf_mutex);

	// This is kinda slow.  It should be possible to compile each sample
	// window into a glCallList, but that's overly complex.
	int mic;
	for(mic = 0; mic < 4; mic++) {
		glBegin(GL_LINE_STRIP);
		glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
		for(x = 0, i = 0; i < state.max_samples; i++) {
			glVertex3f(x, ((float)win_h * (float)(2*mic + 1) / 8. ) + (float)(state.buffers[mic][(base_idx + i) % state.max_samples]) * ((float)win_h/4) /2147483647. , 0);
			x += xIncr;
		}
		glEnd();
	}
	glutSwapBuffers();
}

void Reshape(int w, int h) {
	win_w = w;
	win_h = h;
	glViewport(0, 0, w, h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0.0, (float)w, (float)h, 0.0, -1.0, 1.0);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void Keyboard(unsigned char key, int x, int y) {
	if(key == 'q') {
		die = 1;
		pthread_join(freenect_thread, NULL);
		glutDestroyWindow(window);
	}
	if(key == 32) {
		paused = !paused;
	}
}
