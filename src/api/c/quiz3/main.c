#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>

int main(int argc, char* argv[])
{
	int fd = 0, i;
	char buf[10];

	if((fd = open("/dev/issimagedrv", O_RDWR)) >= 0)
	{
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
	}
	else
	{
		perror("open(/dev/issimagedrv) failed");
		return 1;
	}

	return 0;
}