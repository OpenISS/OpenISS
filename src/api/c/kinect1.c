#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "kinect1.h"

#include <pthread.h>

#if defined(__APPLE__)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

#define _USE_MATH_DEFINES
#include <math.h>

pthread_t freenect_thread;
volatile int die = 0;

int g_argc;
char **g_argv;

//int window;

pthread_mutex_t gl_backbuf_mutex = PTHREAD_MUTEX_INITIALIZER;

// back: owned by libfreenect (implicit for depth)
// mid: owned by callbacks, "latest frame ready"
// front: owned by GL, "currently being drawn"
uint8_t *depth_mid, *depth_front;
uint8_t *rgb_back, *rgb_mid, *rgb_front;

//GLuint gl_depth_tex;
//GLuint gl_rgb_tex;
GLfloat camera_angle = 0.0;
int camera_rotate = 0;
int tilt_changed = 0;

freenect_context *f_ctx;
freenect_device *f_dev;
int freenect_angle = 0;
int freenect_led;

freenect_video_format requested_format = FREENECT_VIDEO_RGB;
freenect_video_format current_format = FREENECT_VIDEO_RGB;

pthread_cond_t gl_frame_cond = PTHREAD_COND_INITIALIZER;
int got_rgb = 0;
int got_depth = 0;

uint16_t t_gamma[2048];

void depth_cb(freenect_device *dev, void *v_depth, uint32_t timestamp)
{
	int i;
	uint16_t *depth = (uint16_t*)v_depth;

	pthread_mutex_lock(&gl_backbuf_mutex);
	for (i=0; i<640*480; i++) {
		int pval = t_gamma[depth[i]];
		int lb = pval & 0xff;
		switch (pval>>8) {
			case 0:
				depth_mid[3*i+0] = 255;
				depth_mid[3*i+1] = 255-lb;
				depth_mid[3*i+2] = 255-lb;
				break;
			case 1:
				depth_mid[3*i+0] = 255;
				depth_mid[3*i+1] = lb;
				depth_mid[3*i+2] = 0;
				break;
			case 2:
				depth_mid[3*i+0] = 255-lb;
				depth_mid[3*i+1] = 255;
				depth_mid[3*i+2] = 0;
				break;
			case 3:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 255;
				depth_mid[3*i+2] = lb;
				break;
			case 4:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 255-lb;
				depth_mid[3*i+2] = 255;
				break;
			case 5:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 0;
				depth_mid[3*i+2] = 255-lb;
				break;
			default:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 0;
				depth_mid[3*i+2] = 0;
				break;
		}
	}
	got_depth++;
	pthread_cond_signal(&gl_frame_cond);
	pthread_mutex_unlock(&gl_backbuf_mutex);
}

void rgb_cb(freenect_device *dev, void *rgb, uint32_t timestamp)
{
	pthread_mutex_lock(&gl_backbuf_mutex);

	// swap buffers
	assert (rgb_back == rgb);
	rgb_back = rgb_mid;
	freenect_set_video_buffer(dev, rgb_back);
	rgb_mid = (uint8_t*)rgb;

	got_rgb++;
	pthread_cond_signal(&gl_frame_cond);
	pthread_mutex_unlock(&gl_backbuf_mutex);
}

void *freenect_threadfunc(void *arg)
{
	int accelCount = 0;

	freenect_set_tilt_degs(f_dev,freenect_angle);
	freenect_set_led(f_dev,LED_RED);
	freenect_set_depth_callback(f_dev, depth_cb);
	freenect_set_video_callback(f_dev, rgb_cb);
	freenect_set_video_mode(f_dev, freenect_find_video_mode(FREENECT_RESOLUTION_MEDIUM, current_format));
	freenect_set_depth_mode(f_dev, freenect_find_depth_mode(FREENECT_RESOLUTION_MEDIUM, FREENECT_DEPTH_11BIT));
	freenect_set_video_buffer(f_dev, rgb_back);

	freenect_start_depth(f_dev);
	freenect_start_video(f_dev);

	printf("'w' - tilt up, 's' - level, 'x' - tilt down, '0'-'6' - select LED mode, '+' & '-' - change IR intensity \n");
	printf("'f' - change video format, 'm' - mirror video, 'o' - rotate video with accelerometer \n");
	printf("'e' - auto exposure, 'b' - white balance, 'r' - raw color, 'n' - near mode (K4W only) \n");

	while (!die && freenect_process_events(f_ctx) >= 0) {
		//Throttle the text output
		if (accelCount++ >= 2000)
		{
			accelCount = 0;
			freenect_raw_tilt_state* state;
			freenect_update_tilt_state(f_dev);
			state = freenect_get_tilt_state(f_dev);
			double dx,dy,dz;
			freenect_get_mks_accel(state, &dx, &dy, &dz);
			printf("\r raw acceleration: %4d %4d %4d  mks acceleration: %4f %4f %4f", state->accelerometer_x, state->accelerometer_y, state->accelerometer_z, dx, dy, dz);
			fflush(stdout);
		}

		if (requested_format != current_format) {
			freenect_stop_video(f_dev);
			freenect_set_video_mode(f_dev, freenect_find_video_mode(FREENECT_RESOLUTION_MEDIUM, requested_format));
			freenect_start_video(f_dev);
			current_format = requested_format;
		}
	}

	printf("\nshutting down streams...\n");

	freenect_stop_depth(f_dev);
	freenect_stop_video(f_dev);

	freenect_close_device(f_dev);
	freenect_shutdown(f_ctx);

	printf("-- done!\n");
	return NULL;
}

void kinect1_receive_rgb_depth_frames()
{
	pthread_mutex_lock(&gl_backbuf_mutex);

	// When using YUV_RGB mode, RGB frames only arrive at 15Hz, so we shouldn't force them to draw in lock-step.
	// However, this is CPU/GPU intensive when we are receiving frames in lockstep.
	if (current_format == FREENECT_VIDEO_YUV_RGB) {
		while (!got_depth && !got_rgb) {
			pthread_cond_wait(&gl_frame_cond, &gl_backbuf_mutex);
		}
	} else {
		while ((!got_depth || !got_rgb) && requested_format != current_format) {
			pthread_cond_wait(&gl_frame_cond, &gl_backbuf_mutex);
		}
	}

	if (requested_format != current_format) {
		pthread_mutex_unlock(&gl_backbuf_mutex);
		return;
	}

	uint8_t *tmp;

	if (got_depth) {
		tmp = depth_front;
		depth_front = depth_mid;
		depth_mid = tmp;
		got_depth = 0;
	}
	if (got_rgb) {
		tmp = rgb_front;
		rgb_front = rgb_mid;
		rgb_mid = tmp;
		got_rgb = 0;
	}

	pthread_mutex_unlock(&gl_backbuf_mutex);
}

int kinect1_init(int argc, char** argv)
{
	int res;

	depth_mid = (uint8_t*)malloc(640*480*3);
	depth_front = (uint8_t*)malloc(640*480*3);
	rgb_back = (uint8_t*)malloc(640*480*3);
	rgb_mid = (uint8_t*)malloc(640*480*3);
	rgb_front = (uint8_t*)malloc(640*480*3);

	printf("Kinect camera test\n");

	int i;
	for (i=0; i<2048; i++)
	{
		float v = i/2048.0;
		v = powf(v, 3)* 6;
		t_gamma[i] = v*6*256;
	}

	if (freenect_init(&f_ctx, NULL) < 0)
	{
		printf("freenect_init() failed\n");
		return 1;
	}

	freenect_set_log_level(f_ctx, FREENECT_LOG_DEBUG);
	freenect_select_subdevices(f_ctx, (freenect_device_flags)(FREENECT_DEVICE_MOTOR | FREENECT_DEVICE_CAMERA));

	int user_device_number = 0;
	if (argc > 1)
		user_device_number = atoi(argv[1]);

	int nr_devices = freenect_num_devices(f_ctx);
	printf ("Number of devices found: %d\n", nr_devices);

	if (nr_devices < 1) {
		freenect_shutdown(f_ctx);
		return 1;
	}

	if (freenect_open_device(f_ctx, &f_dev, user_device_number) < 0) {
		printf("Could not open device\n");
		freenect_shutdown(f_ctx);
		return 1;
	}

	res = pthread_create(&freenect_thread, NULL, freenect_threadfunc, NULL);
	if (res) {
		printf("pthread_create failed\n");
		freenect_shutdown(f_ctx);
		return 1;
	}

	// OS X requires GLUT to run on the main thread
	//gl_threadfunc(NULL);

	return 0;
}

void kinect1_keyPressed(unsigned char key, int x, int y)
{
	if (key == 27) {
		die = 1;
		pthread_join(freenect_thread, NULL);
		//glutDestroyWindow(window);
		free(depth_mid);
		free(depth_front);
		free(rgb_back);
		free(rgb_mid);
		free(rgb_front);
		// Not pthread_exit because OSX leaves a thread lying around and doesn't exit
		//exit(0);
	}
	if (key == 'w') {
		freenect_angle++;
		if (freenect_angle > 30) {
			freenect_angle = 30;
		}
		tilt_changed++;
	}
	if (key == 's') {
		freenect_angle = 0;
		tilt_changed++;
	}
	if (key == 'f') {
		if (requested_format == FREENECT_VIDEO_IR_8BIT)
			requested_format = FREENECT_VIDEO_RGB;
		else if (requested_format == FREENECT_VIDEO_RGB)
			requested_format = FREENECT_VIDEO_YUV_RGB;
		else
			requested_format = FREENECT_VIDEO_IR_8BIT;
	}
	if (key == 'x') {
		freenect_angle--;
		if (freenect_angle < -30) {
			freenect_angle = -30;
		}
		tilt_changed++;
	}
	if (key == 'e') {
		static freenect_flag_value auto_exposure = FREENECT_ON;
		freenect_set_flag(f_dev, FREENECT_AUTO_EXPOSURE, auto_exposure);
		auto_exposure = auto_exposure ? FREENECT_OFF : FREENECT_ON;
	}
	if (key == 'b') {
		static freenect_flag_value white_balance = FREENECT_ON;
		freenect_set_flag(f_dev, FREENECT_AUTO_WHITE_BALANCE, white_balance);
		white_balance = white_balance ? FREENECT_OFF : FREENECT_ON;
	}
	if (key == 'r') {
		static freenect_flag_value raw_color = FREENECT_ON;
		freenect_set_flag(f_dev, FREENECT_RAW_COLOR, raw_color);
		raw_color = raw_color ? FREENECT_OFF : FREENECT_ON;
	}
	if (key == 'm') {
		static freenect_flag_value mirror = FREENECT_ON;
		freenect_set_flag(f_dev, FREENECT_MIRROR_DEPTH, mirror);
		freenect_set_flag(f_dev, FREENECT_MIRROR_VIDEO, mirror);
		mirror = mirror ? FREENECT_OFF : FREENECT_ON;
	}
	if (key == 'n') {
		static freenect_flag_value near_mode = FREENECT_ON;
		freenect_set_flag(f_dev, FREENECT_NEAR_MODE, near_mode);
		near_mode = near_mode ? FREENECT_OFF : FREENECT_ON;
	}

	if (key == '+') {
		uint16_t brightness = freenect_get_ir_brightness(f_dev) + 2;
		freenect_set_ir_brightness(f_dev, brightness);
	}
	if (key == '-') {
		uint16_t brightness = freenect_get_ir_brightness(f_dev) - 2;
		freenect_set_ir_brightness(f_dev, brightness);
	}

	if (key == '1') {
		freenect_set_led(f_dev,LED_GREEN);
	}
	if (key == '2') {
		freenect_set_led(f_dev,LED_RED);
	}
	if (key == '3') {
		freenect_set_led(f_dev,LED_YELLOW);
	}
	if (key == '4') {
		freenect_set_led(f_dev,LED_BLINK_GREEN);
	}
	if (key == '5') {
		// 5 is the same as 4
		freenect_set_led(f_dev,LED_BLINK_GREEN);
	}
	if (key == '6') {
		freenect_set_led(f_dev,LED_BLINK_RED_YELLOW);
	}
	if (key == '0') {
		freenect_set_led(f_dev,LED_OFF);
	}

	if (key == 'o') {
	    if (camera_rotate) {
	        camera_rotate = 0;
	        glDisable(GL_DEPTH_TEST);
	    }
	    else {
	        camera_rotate = 1;
	        glEnable(GL_DEPTH_TEST);
	    }
	}
	if (tilt_changed) {
	    freenect_set_tilt_degs(f_dev, freenect_angle);
	    tilt_changed = 0;
	}
}
