/*
 * This file is part of the OpenKinect Project. http://www.openkinect.org
 *
 * Copyright (c) 2011 individual OpenKinect contributors. See the CONTRIB file
 * for details.
 *
 * This code is licensed to you under the terms of the Apache License, version
 * 2.0, or, at your option, the terms of the GNU General Public License,
 * version 2.0. See the APACHE20 and GPL2 files for the text of the licenses,
 * or the following URLs:
 * http://www.apache.org/licenses/LICENSE-2.0
 * http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * If you redistribute this file in source form, modified or unmodified, you
 * may:
 *   1) Leave this header intact and distribute it under the same terms,
 *      accompanying it with the APACHE20 and GPL20 files, or
 *   2) Delete the Apache 2.0 clause and accompany it with the GPL2 file, or
 *   3) Delete the GPL v2 clause and accompany it with the APACHE20 file
 * In all cases you must keep the copyright notice intact and include a copy
 * of the CONTRIB file.
 *
 * Binary distributions must follow the binary distribution requirements of
 * either License.
 */

#include "micviewHeader.h"

int main(int argc, char** argv) {
	if (freenect_init(&f_ctx, NULL) < 0) {
		printf("freenect_init() failed\n");
		return 1;
	}
	freenect_set_log_level(f_ctx, FREENECT_LOG_INFO);
	freenect_select_subdevices(f_ctx, FREENECT_DEVICE_AUDIO);

	int nr_devices = freenect_num_devices (f_ctx);
	printf ("Number of devices found: %d\n", nr_devices);
	if (nr_devices < 1) {
		freenect_shutdown(f_ctx);
		return 1;
	}

	int user_device_number = 0;
	if (freenect_open_device(f_ctx, &f_dev, user_device_number) < 0) {
		printf("Could not open device\n");
		freenect_shutdown(f_ctx);
		return 1;
	}

	state.max_samples = 256 * 60;
	state.current_idx = 0;
	state.buffers[0] = (int32_t*)malloc(state.max_samples * sizeof(int32_t));
	state.buffers[1] = (int32_t*)malloc(state.max_samples * sizeof(int32_t));
	state.buffers[2] = (int32_t*)malloc(state.max_samples * sizeof(int32_t));
	state.buffers[3] = (int32_t*)malloc(state.max_samples * sizeof(int32_t));
	memset(state.buffers[0], 0, state.max_samples * sizeof(int32_t));
	memset(state.buffers[1], 0, state.max_samples * sizeof(int32_t));
	memset(state.buffers[2], 0, state.max_samples * sizeof(int32_t));
	memset(state.buffers[3], 0, state.max_samples * sizeof(int32_t));
	freenect_set_user(f_dev, &state);

	freenect_set_audio_in_callback(f_dev, in_callback);
	freenect_start_audio(f_dev);

	int res = pthread_create(&freenect_thread, NULL, freenect_threadfunc, NULL);
	if (res) {
		printf("pthread_create failed\n");
		freenect_shutdown(f_ctx);
		return 1;
	}
	printf("This is the libfreenect microphone waveform viewer.  Press 'q' to quit or spacebar to pause/unpause the view.\n");

	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA | GLUT_ALPHA );
	glutInitWindowSize(800, 600);
	glutInitWindowPosition(0, 0);
	window = glutCreateWindow("Microphones");
	glClearColor(0.0, 0.0, 0.0, 0.0);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	Reshape(800, 600);
	glutReshapeFunc(Reshape);
	glutDisplayFunc(DrawMicData);
	glutIdleFunc(DrawMicData);
	glutKeyboardFunc(Keyboard);

	glutMainLoop();

	return 0;
}
