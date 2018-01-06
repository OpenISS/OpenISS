#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

/*#include "libfreenect.h"*/
#include "kinect1.h"

#include <pthread.h>

#if defined(__APPLE__)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

#define _USE_MATH_DEFINES
#include <math.h>

//pthread_t freenect_thread;
//volatile int die = 0;

int g_argc;
char **g_argv;

int window;

//pthread_mutex_t gl_backbuf_mutex = PTHREAD_MUTEX_INITIALIZER;

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

//pthread_cond_t gl_frame_cond = PTHREAD_COND_INITIALIZER;
//int got_rgb = 0;
//int got_depth = 0;


void DrawGLScene()
{
	/* TODO call draw functions of the VFXs */
	
	/*
	kinect1_receive_rgb_depth_frames();
	
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
	*/
	
	glutSwapBuffers();
}

void keyPressed(unsigned char key, int x, int y)
{
	kinect1_keyPressed(key, x, y);
	
	if(key == 27)
	{
		//die = 1;
		//pthread_join(freenect_thread, NULL);
		glutDestroyWindow(window);
		//free(depth_mid);
		//free(depth_front);
		//free(rgb_back);
		//free(rgb_mid);
		//free(rgb_front);
		// Not pthread_exit because OSX leaves a thread lying around and doesn't exit
		exit(0);
	}
}

void ReSizeGLScene(int Width, int Height)
{
	glViewport(0,0,Width,Height);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0, 1280, 0, 480, -5.0f, 5.0f);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void InitGL(int Width, int Height)
{
	glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	//glClearDepth(0.0);
	//glDepthFunc(GL_LESS);
	//glDepthMask(GL_FALSE);
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_BLEND);
	glDisable(GL_ALPHA_TEST);
	
	glEnable(GL_TEXTURE_2D);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glShadeModel(GL_FLAT);

	glGenTextures(1, &gl_depth_tex);
	glBindTexture(GL_TEXTURE_2D, gl_depth_tex);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	glGenTextures(1, &gl_rgb_tex);
	glBindTexture(GL_TEXTURE_2D, gl_rgb_tex);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	ReSizeGLScene(Width, Height);
}

//void *gl_threadfunc(void *arg)
void initGLUT()
{
	//printf("GL thread\n");

	glutInit(&g_argc, g_argv);

	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_ALPHA | GLUT_DEPTH);
	glutInitWindowSize(1280, 480);
	glutInitWindowPosition(0, 0);

	window = glutCreateWindow("OpenISS-C GL2Viewer");

	glutDisplayFunc(&DrawGLScene);
	glutIdleFunc(&DrawGLScene);
	glutReshapeFunc(&ReSizeGLScene);
	glutKeyboardFunc(&keyPressed);

	InitGL(1280, 480);

	glutMainLoop();

	//return NULL;
}



int viewer_main(int argc, char **argv)
{
	g_argc = argc;
	g_argv = argv;

	int res = kinect1_init(argc, argv);

	if(res)
	{
		return res;
	}
	
	// OS X requires GLUT to run on the main thread
	initGLUT();

	return 0;
}
