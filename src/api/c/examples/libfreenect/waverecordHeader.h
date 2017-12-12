#include "libfreenect.h"
#include "libfreenect_audio.h"
#include <stdio.h>
#include <signal.h>

static freenect_context* f_ctx;
static freenect_device* f_dev;
int die = 0;

char wavheader[] = {
	0x52, 0x49, 0x46, 0x46, // ChunkID = "RIFF"
	0x00, 0x00, 0x00, 0x00, // Chunksize (will be overwritten later)
	0x57, 0x41, 0x56, 0x45, // Format = "WAVE"
	0x66, 0x6d, 0x74, 0x20, // Subchunk1ID = "fmt "
	0x10, 0x00, 0x00, 0x00, // Subchunk1Size = 16
	0x01, 0x00, 0x01, 0x00, // AudioFormat = 1 (linear quantization) | NumChannels = 1
	0x80, 0x3e, 0x00, 0x00, // SampleRate = 16000 Hz
	0x00, 0xfa, 0x00, 0x00, // ByteRate = SampleRate * NumChannels * BitsPerSample/8 = 64000
	0x04, 0x00, 0x20, 0x00, // BlockAlign = NumChannels * BitsPerSample/8 = 4 | BitsPerSample = 32
	0x64, 0x61, 0x74, 0x61, // Subchunk2ID = "data"
	0x00, 0x00, 0x00, 0x00, // Subchunk2Size = NumSamples * NumChannels * BitsPerSample / 8 (will be overwritten later)
};

typedef struct {
	FILE* logfiles[4];
	int samples;
} capture;

void in_callback(freenect_device* dev, int num_samples,
                 int32_t* mic1, int32_t* mic2,
                 int32_t* mic3, int32_t* mic4,
                 int16_t* cancelled, void *unknown) {
	capture* c = (capture*)freenect_get_user(dev);
	fwrite(mic1, 1, num_samples*sizeof(int32_t), c->logfiles[0]);
	fwrite(mic2, 1, num_samples*sizeof(int32_t), c->logfiles[1]);
	fwrite(mic3, 1, num_samples*sizeof(int32_t), c->logfiles[2]);
	fwrite(mic4, 1, num_samples*sizeof(int32_t), c->logfiles[3]);
	c->samples += num_samples;
	printf("Sample received.  Total samples recorded: %d\n", c->samples);
}

void cleanup(int sig) {
	printf("Caught SIGINT, cleaning up\n");
	die = 1;
}
