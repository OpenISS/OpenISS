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
