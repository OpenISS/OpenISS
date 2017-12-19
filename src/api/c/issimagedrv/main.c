#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <ctype.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#ifndef HEXDUMP_COLS
#define HEXDUMP_COLS 8
#endif

#define DEVICE_NAME "/dev/ISSIMAGEDRV"
#define FILE_NAME_1 "/boot/initramfs-4.14.0-1.el6.elrepo.x86_64.img"
#define FILE_NAME_2 "/bin/vi"

void hex_to_image(char outputf[], int hexSize, char hexdump[])
{
	int fd;
	int numSize = 17;
	char num [numSize];
	int iter = 0;

	/*Create the file with the given ouput filename*/
	fd = open(outputf, O_RDWR | O_CREAT, S_IRUSR | S_IRGRP | S_IROTH);

	/* Assign the width and the height of the image */
	int height = 0, width = 0;
	width = (int)(hexSize / 3.0);
	
	if(hexSize % 2 == 0)
		height = hexSize / 2;
	else
		height = (hexSize / 2) -1;	
	height = 1;
	
	/* Input the header and image size to the file */
	snprintf(num, numSize, "P6\n%i %i\n125\n", width, height);
	write(fd, num, strlen(num));

	int i;
	for (i = 0; i < width * height; i++)
	{
		char toWrite[1] = {hexdump[i]};
		write(fd, toWrite, 1);
		
		/* if R, G, and B have been outputted, go to a new line */
		if(iter == 2)
		{	
			/*write(fd, (char*) '\n', 1);*/
			iter = 0;
		}		
		else
		{
			/*write(fd, (char*) ' ', 1);*/
			iter++;
		}
			
	}

	while (iter != 0)
	{
		/*write(fd, "\0 ", 2);*/
		iter++;
		if (iter > 2)
			break;
	}
	
	close(fd);
	
}


int main(int argc, char* argv[])
{
	int fd = 0, deviceFd = 0, readSize = 99, bytesRead, forkId = 1;
	//the file name
	char buf[readSize];
	char* fileName;

	/*forkId = fork();*/

	if(forkId == 0)
	{
		fileName = FILE_NAME_1;
		return 0;
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

			close(fd);
		}
		else
		{
			perror("File couldn't be opened!!!!");
			return 1;
		}

		int bufferSize = 99;
		char readBuffer[bufferSize];
		int readSize;

		readSize = read(deviceFd, readBuffer, bufferSize);
		hex_to_image(forkId == 0 ? "child_image.ppd" : "parent_image.ppd", readSize, readBuffer);

		close(deviceFd);
	}
	else
	{
		perror("open(/dev/ISSIMAGEDRV) failed");
		return 1;
	}
	

	return 0;
}
