#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <ctype.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>

#ifndef HEXDUMP_COLS
#define HEXDUMP_COLS 8
#endif

#define DEVICE_NAME "/dev/ISSIMAGEDRV"

char hereItIs[] = "deez nuts";

//http://stackoverflow.com/questions/7775991/how-to-get-hexdump-of-a-structure-data
//thank you stack overflow
char hexDump (char *desc, void *addr, int len) {
    int i;
    unsigned char buff[17];
    unsigned char *pc = (unsigned char*)addr;

    // Output description if given.
    if (desc != NULL)
        printf ("%s:\n", desc);

    if (len == 0) {
        printf("  ZERO LENGTH\n");
        return;
    }
    if (len < 0) {
        printf("  NEGATIVE LENGTH: %i\n",len);
        return;
    }

    // Process every byte in the data.
    for (i = 0; i < len; i++) {
        // Multiple of 16 means new line (with line offset).

        if ((i % 16) == 0) {
            // Just don't print ASCII for the zeroth line.
            if (i != 0)
                printf ("  %s\n", buff);

            // Output the offset.
            printf ("  %04x ", i);
        }

        // Now the hex code for the specific character.
        printf (" %02x", pc[i]);

        // And store a printable ASCII character for later.
        if ((pc[i] < 0x20) || (pc[i] > 0x7e))
            buff[i % 16] = '.';
        else
            buff[i % 16] = pc[i];
        buff[(i % 16) + 1] = '\0';
    }

    // Pad out last line if not exactly 16 characters.
    while ((i % 16) != 0) {
        printf ("   ");
        i++;
    }

    // And print the final ASCII bit.
    printf ("  %s\n", buff);

    return buff;
}


int main(int argc, char* argv[])
{
	FILE* fp;
	int lSize;

	fp = fopen("./driver-test", "rb");
	if( !fp ) perror(fp),exit(1);
	fseek(fp, 0, SEEK_END);
	lSize = ftell(fp);
	rewind(fp);

	int fd = 0, i;
	//the file name
	char* buf;

	hexDump("strtest", fp, lSize);


    //printf("%i", lSize);


	if((fd = open(DEVICE_NAME, O_RDWR)) >= 0)
	{
		read(fd, buf, i);
		write(fd, buf, i);
		close(fd);
	}
	else
	{
		perror("open(/dev/ISSIMAGEDRV) failed");
		return 1;
	}

	fclose(fp);

	return 0;
}
