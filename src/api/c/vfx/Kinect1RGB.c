#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
//#include "libfreenect.h"

#include <pthread.h>

#if defined(__APPLE__)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

#define _USE_MATH_DEFINES
#include <math.h>

#include "vfx.h"

/* Include standard headers */
#include <stdio.h>
#include <stdlib.h>

/* Include GLEW */
#include <GL/glew.h>


//pthread_t freenect_thread;
volatile int die = 0;

int g_argc;
char **g_argv;

int window;

pthread_mutex_t gl_backbuf_mutex = PTHREAD_MUTEX_INITIALIZER;

// back: owned by libfreenect (implicit for depth)
// mid: owned by callbacks, "latest frame ready"
// front: owned by GL, "currently being drawn"
//uint8_t *depth_mid, *depth_front;
//uint8_t *rgb_back, *rgb_mid, *rgb_front;

GLuint gl_depth_tex;
GLuint gl_rgb_tex;
//GLfloat camera_angle = 0.0;

//int camera_rotate = 0;
//int tilt_changed = 0;

//freenect_context *f_ctx;
//freenect_device *f_dev;
//int freenect_angle = 0;
//int freenect_led;

//freenect_video_format requested_format = FREENECT_VIDEO_RGB;
//freenect_video_format current_format = FREENECT_VIDEO_RGB;

pthread_cond_t gl_frame_cond = PTHREAD_COND_INITIALIZER;
//int got_rgb = 0;
//int got_depth = 0;


void DrawGLScene()
{
	kinect1_receive_rgb_depth_frames();

	/*
	glBindTexture(GL_TEXTURE_2D, gl_depth_tex);
	glTexImage2D(GL_TEXTURE_2D, 0, 3, 640, 480, 0, GL_RGB, GL_UNSIGNED_BYTE, depth_front);

	if(camera_rotate)
	{
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    freenect_raw_tilt_state* state;
	    freenect_update_tilt_state(f_dev);
	    state = freenect_get_tilt_state(f_dev);
	    GLfloat x_accel_raw, x_accel,y_accel_raw,y_accel;
	    x_accel_raw = (GLfloat)state->accelerometer_x/819.0;
	    y_accel_raw = (GLfloat)state->accelerometer_y/819.0;

	    // sloppy acceleration vector cleanup
	    GLfloat accel_length = sqrt(x_accel_raw * x_accel_raw + y_accel_raw * y_accel_raw);
	    x_accel = x_accel_raw/accel_length;
	    y_accel = y_accel_raw/accel_length;
	    camera_angle = atan2(y_accel,x_accel)*180/M_PI -90.0;
	}
	else
	{
		camera_angle = 0;
	}

	glLoadIdentity();

	glPushMatrix();
	  glTranslatef((640.0/2.0),(480.0/2.0) ,0.0);
	  glRotatef(camera_angle, 0.0, 0.0, 1.0);
	  glTranslatef(-(640.0/2.0),-(480.0/2.0) ,0.0);
	  glBegin(GL_TRIANGLE_FAN);
	    glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    glTexCoord2f(0, 1); glVertex3f(0,0,1.0);
	    glTexCoord2f(1, 1); glVertex3f(640,0,1.0);
	    glTexCoord2f(1, 0); glVertex3f(640,480,1.0);
	    glTexCoord2f(0, 0); glVertex3f(0,480,1.0);
	  glEnd();
	glPopMatrix();
*/
	glBindTexture(GL_TEXTURE_2D, gl_rgb_tex);
	
	if (current_format == FREENECT_VIDEO_RGB || current_format == FREENECT_VIDEO_YUV_RGB)
		glTexImage2D(GL_TEXTURE_2D, 0, 3, 640, 480, 0, GL_RGB, GL_UNSIGNED_BYTE, rgb_front);
	else
		glTexImage2D(GL_TEXTURE_2D, 0, 1, 640, 480, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, rgb_front+640*4);

	glPushMatrix();
	  glTranslatef(640+(640.0/2.0),(480.0/2.0) ,0.0);
	  glRotatef(camera_angle, 0.0, 0.0, 1.0);
	  glTranslatef(-(640+(640.0/2.0)),-(480.0/2.0) ,0.0);

	  glBegin(GL_TRIANGLE_FAN);
	    glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    glTexCoord2f(0, 1); glVertex3f(640,0,0);
	    glTexCoord2f(1, 1); glVertex3f(1280,0,0);
	    glTexCoord2f(1, 0); glVertex3f(1280,480,0);
	    glTexCoord2f(0, 0); glVertex3f(640,480,0);
	  glEnd();
	glPopMatrix();
	glutSwapBuffers();
}

void keyPressed(unsigned char key, int x, int y)
{
}

t_iss_vfx_ops p_oVFXEmptyOpenGLTest;

void vfx_init(void);
void vfx_draw(void);
void vfx_free(void);

void vfx_init()
{
}

void vfx_free()
{
}

void vfx_draw()
{
}

#ifdef MAIN_VFX_TEST
int main(int argc, char** argv)
{
    p_oVFXEmptyOpenGLTest.vfx_init = &vfx_init;
    p_oVFXEmptyOpenGLTest.vfx_init = &vfx_draw;
    p_oVFXEmptyOpenGLTest.vfx_init = &vfx_free;

    vfx_init();
    vfx_draw();
    vfx_free();
    return 0;
}
#endif
