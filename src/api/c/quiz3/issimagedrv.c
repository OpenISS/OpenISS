/* issimagedrv.c */

#include <linux/sched.h>

#include "issimagedrv.h"
#include <linux/fs.h>
#include <asm/segment.h>
#include <asm/uaccess.h>
#include <linux/buffer_head.h>

static int Major; /* Major number assigned to our device driver */
/*unsigned char* data[3];*/
unsigned char* data;
unsigned int dataSize;

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
		"Registeration is a success. The major device number is %d.\n",
		Major
	);

	printk
	(
		"If you want to talk to the device driver,\n" \
		"you'll have to create a device file. \n" \
		"We suggest you use:\n" \
		"mknod %s c %d <minor>\n" \
		"You can try different minor numbers and see what happens.\n",
		DEVICE_NAME,
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

/* Process attempts to read from device file */
static ssize_t device_read(struct file *file, char *buffer, size_t length, loff_t * offset)
{
 	/*initialize needed vars*/
	int i, oldSize = dataSize;
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
		}
		realloc(data, 0);
		int temp = datasize;		
		dataSize = 0;
		return dataSize;
	}
	else /*more data than buffer size*/
	{
		for (i = 0; i < length; i++)
		{
			buffer[i] = data[i];
			dataSize--;
		}
		char* temp[dataSize];
		for (int i = dataSize; i < oldSize; i--)
		{
			temp[i - dataSize] = data[i];
		}

		realloc(data, dataSize);
		for (i = 0; i < dataSize; i++)
		{
			data[i] = temp[i];	
		}
	}
	printk("%s being read\n", DEVICE_NAME);
	return length;
}

/* Process attempts to write to device file */
static ssize_t device_write(struct file *file, const char *buff, size_t len, loff_t * off)
{
	int tmpSize = dataSize + len;
	int count = 0;

	/* reallocate for our new array size */
	realloc(data, tmpSize);

	/* add the new data */
	for (int i = dataSize; i < tmpSize; i++)
	{
		data[i] = buff[count];
		count++;

		/* keep keeping track of data size */
		dataSize++;
	}

	return SUCCESS;
}
