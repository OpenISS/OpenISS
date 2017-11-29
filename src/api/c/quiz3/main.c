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
#define FILE_NAME_1 "/home/rosser/atsm/examples/c/syscalls/file/ifv/Makefile"
#define FILE_NAME_2 "/home/rosser/atsm/examples/c/syscalls/file/ifv/Makefile.orig"

void hex_to_image(char outputf[], int hexSize, char hexdump[])
{
	int fd;
	char num [13];
	int iter = 0;

	/*Create the file with the given ouput filename*/
	fd = open(outputf, O_RDWR | O_CREAT, S_IRUSR | S_IRGRP | S_IROTH);

	/* Assign the width and the height of the image */
	int height, width;
	width = hexSize / 2;
	
	if(hexSize % 2 == 0)
		width = hexSize / 2;
	else
		width = (hexSize / 2) -1;	
	
	/* Input the header and image size to the file */
	snprintf(num, 10, "P3\n%i %i\n", width, height);
	write(fd, num, strlen(num));

	int i;
	for (i = 0; i < hexSize; i++)
	{
		char toWrite[1] = {hexdump[i]};
		write(fd, toWrite, 1);
		
		/* if R, G, and B have been outputted, go to a new line */
		if(iter == 2)
		{	
			write(fd, (char*) '\n', 1);
			iter = 0;
		}		
		else
		{
			write(fd, (char*) ' ', 1);
			iter++;
		}
			
	}
	
	close(fd);
	
}


int main(int argc, char* argv[])
{

	int fd = 0, deviceFd = 0, readSize = 100, bytesRead, forkId;
	//the file name
	char buf[readSize];
	char* fileName;

//	hexDump("strtest", fp, lSize);


    //printf("%i", lSize);

	forkId = fork();

	if(forkId == 0)
	{
		fileName = FILE_NAME_1;
	}
	else
	{
		fileName = FILE_NAME_2;
	}

	if((deviceFd = open(DEVICE_NAME, O_RDWR)) >= 0) 
	{
		if((fd = open(fileName, O_RDWR)) >= 0)
		{
			bytesRead = read(fd, buf, readSize);
			write(deviceFd, buf, bytesRead);
			printf("%i\n", bytesRead);

			close(fd);
		}

		close(deviceFd);
	}
	else
	{
		perror("open(/dev/ISSIMAGEDRV) failed");
		return 1;
	}
	

	return 0;
}
