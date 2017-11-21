#include <stdio.h>
#include <stdlib.h>

#include "libfreenect.h"
#include "libfreenect_sync.h"

FILE *open_dump(const char *filename)
{
	FILE* fp = fopen(filename, "w");
	if (fp == NULL) {
		fprintf(stderr, "Error: Cannot open file [%s]\n", filename);
		exit(1);
	}
	printf("%s\n", filename);
	return fp;
}

void dump_depth(FILE *fp, void *data, unsigned int width, unsigned int height)
{
	fprintf(fp, "P5 %u %u 65535\n", width, height);
	fwrite(data, width * height * 2, 1, fp);
}

void dump_rgb(FILE *fp, void *data, unsigned int width, unsigned int height)
{
	fprintf(fp, "P6 %u %u 255\n", width, height);
	fwrite(data, width * height * 3, 1, fp);
}

void no_kinect_quit(void)
{
	fprintf(stderr, "Error: Kinect not connected?\n");
	exit(1);
}
