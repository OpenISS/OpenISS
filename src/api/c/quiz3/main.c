#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>

#define DEVICE_NAME "/dev/ISSIMAGEDRV"

int main(int argc, char* argv[])
{
	int fd = 0, i;
	char buf[10];

	if((fd = open(DEVICE_NAME, O_RDWR)) >= 0)
	{
		/*
		printf("aylmao\n");
		
		int j = read(fd, buf, i);
		
		if(j < 0)
		{
			perror("cant read");
		}
		else
		{
			printf("sick");
		}
		*/

		read(fd, buf, i);
		write(fd, buf, i);
		close(fd);
	}
	else
	{
		perror("open(/dev/ISSIMAGEDRV) failed");
		return 1;
	}

	return 0;
}
