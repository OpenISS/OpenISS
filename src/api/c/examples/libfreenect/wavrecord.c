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

#include "waverecordHeader.h"

int main(int argc, char** argv) {
	if (freenect_init(&f_ctx, NULL) < 0) {
		printf("freenect_init() failed\n");
		return 1;
	}
	freenect_set_log_level(f_ctx, FREENECT_LOG_SPEW);
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

	capture state;
	state.samples = 0;
	state.logfiles[0] = fopen("channel1.wav", "wb");
	state.logfiles[1] = fopen("channel2.wav", "wb");
	state.logfiles[2] = fopen("channel3.wav", "wb");
	state.logfiles[3] = fopen("channel4.wav", "wb");
	fwrite(wavheader, 1, 44, state.logfiles[0]);
	fwrite(wavheader, 1, 44, state.logfiles[1]);
	fwrite(wavheader, 1, 44, state.logfiles[2]);
	fwrite(wavheader, 1, 44, state.logfiles[3]);
	freenect_set_user(f_dev, &state);

	freenect_set_audio_in_callback(f_dev, in_callback);
	freenect_start_audio(f_dev);
	signal(SIGINT, cleanup);

	while(!die && freenect_process_events(f_ctx) >= 0) {
		// If we did anything else, it might go here.
		// Alternately, we might split off another thread
		// to do this loop while the main thread did something
		// interesting.
	}

	// Make the WAV header valid for each of the four files
	int i;
	for(i = 0; i < 4 ; i++) {
		char buf[4];
		fseek(state.logfiles[i], 4, SEEK_SET);
		// Write ChunkSize = 36 + subchunk2size
		int chunksize = state.samples * 4 + 36;
		buf[0] = (chunksize & 0x000000ff);
		buf[1] = (chunksize & 0x0000ff00) >> 8;
		buf[2] = (chunksize & 0x00ff0000) >> 16;
		buf[3] = (chunksize & 0xff000000) >> 24;
		fwrite(buf, 1, 4,state.logfiles[i]);

		fseek(state.logfiles[i], 40, SEEK_SET);
		// Write Subchunk2Size = NumSamples * NumChannels (1) * BitsPerSample/8 (4)
		int subchunk2size = state.samples * 4;
		buf[0] = (subchunk2size & 0x000000ff);
		buf[1] = (subchunk2size & 0x0000ff00) >> 8;
		buf[2] = (subchunk2size & 0x00ff0000) >> 16;
		buf[3] = (subchunk2size & 0xff000000) >> 24;
		fwrite(buf, 1, 4,state.logfiles[i]);
		fclose(state.logfiles[i]);
	}

	freenect_shutdown(f_ctx);
	return 0;
}
