/* issimagedrv.c */

#include <linux/sched.h>

#include "issimagedrv.h"
#include <linux/fs.h>
#include <asm/segment.h>
#include <asm/uaccess.h>
#include <linux/buffer_head.h>

static int Major; /* Major number assigned to our device driver */
/*unsigned char* data[3];*/
unsigned char data[1];
unsigned int dataSize = 0;

/* Module initially loaded */
int init_module(void)
{
	/* Register the character device (atleast try) */
	Major = register_chrdev
	(
		0,
		DEVICE_NAME,
		&Fops
	);

	/* Negative values signify an error */
	if(Major < 0)
	{
		printk
		(
			"Sorry, registering the meme device failed with \n"
		);

		return Major;
	}

	printk
	(
		"Registration is a success. The major device number is %d.\n",
		Major
	);

	printk
	(
		"ISSIMG %d",
		Major
	);

	return SUCCESS;
}

/* Module unloaded */
void cleanup_module(void)
{
	printk("%s unregistering\n", DEVICE_NAME);
	unregister_chrdev(Major, DEVICE_NAME);
}

/* Process attempts to open device file */
static int device_open(struct inode *inode, struct file *file)
{
	printk("%s opened\n", DEVICE_NAME);
	return SUCCESS;
}

/* Process closes device file */
static int device_release(struct inode *inode, struct file *file)
{
	printk("%s released\n", DEVICE_NAME);
	return SUCCESS;
}

/* Function to resize array, does nothing if newSize and currentSize are the same size.  */
static void resizeArray(char array[], unsigned int currentSize, unsigned int newSize)
{
	if (currentSize < newSize)
	{
		char newArray[newSize];
		int i;

		for (i = 0; i < currentSize; i++)
			newArray[i] = array[currentSize];
		for (i = currentSize; i < newSize; i++)
			newArray[i] = '\n';

		array = newArray;
	}
	else if (currentSize > newSize)
	{
		char newArray[newSize];
		int i;

		for (i = 0; i < newSize; i++)
			newArray[i] = array[i];

		array = newArray;
	}	
}

/* Process attempts to read from device file */
static ssize_t device_read(struct file *file, char *buffer, size_t length, loff_t * offset)
{
 	/*initialize needed vars*/
	unsigned int i, oldSize = dataSize;
	char nullChar = '\0';
	
	/*if there is less data than the buffer size*/
	if (dataSize < length)
	{
		i = 0;
		while (i < dataSize)
		{
			buffer[i] = data[i];
			i++;
		}
		while (i < length)
		{
			buffer[i] = nullChar;
			i++;
		}

		if (dataSize != 0)
		{
			resizeArray(data, dataSize, 0);
			dataSize = 0;
		}
		return oldSize;
	}
	else /*more data than buffer size*/
	{
		for (i = 0; i < length; i++)
		{
			buffer[i] = data[i];
			dataSize--;
		}

		if (dataSize != 0)
		{
			unsigned char temp[dataSize];
			for (i = dataSize; i < oldSize; i++)
			{
				temp[i - dataSize] = data[i];
			}
	
			resizeArray(data, oldSize, dataSize);
			for (i = 0; i < dataSize; i++)
			{
				data[i] = temp[i];	
			}
		}
		else
		{
			resizeArray(data, oldSize, 1);
			dataSize = 0;
		}
	}

	return length;
}

/* Process attempts to write to device file */
static ssize_t device_write(struct file *file, const char *buff, size_t len, loff_t * off)
{
	int tmpSize = dataSize + (int) len;
	int count = 0;
	int i;

	/* reallocate for our new array size */
	resizeArray(data, dataSize, tmpSize);

	/* add the new data */
	for (i = dataSize; i < tmpSize; i++)
	{
		data[i] = buff[count];
		count++;

		/* keep keeping track of data size */
		dataSize++;
	}

	return SUCCESS;
}
