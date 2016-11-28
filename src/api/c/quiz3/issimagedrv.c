/**
 * issimagedrv.c
 * 
 * Brian Baron
 * Quiz 3
 * 
 * Purpose:
 * Take in bytes form a text or binary file
 * Buffer the input files into an image
 * 
 * Take in a filename, supply the output filename
 * if no output filename, default it to output.png
 **/

/**
* Includes
**/
#include <issimagedrv.h>

/**
* Driver Status
**/
static driver_status_t status =
{
	'0',	/* Starting ASCII character is 0 */
	false,	/* Busy or not */
	{0},	/* Buffer */
	NULL,	/* Buffer pointer */
	-1,		/* Major device number */
	-1		/* Minor device number */
}

/**
* Device Open:
* Call when a process tries to open the device file
**/
static int device_open(inode, file)
	struct inode* inode;
	struct file*  file;
{
	static int counter = 0;

#ifdef _DEBUG
	printk("device_open(%p,%p)\n", inode, file);
#endif

	/* This is how you get the minor device number in
	 * case you have more than one physical device using
	 * the driver.
	 */
	status.minor = inode->i_rdev >> 8;
	status.minor = inode->i_rdev & 0xFF;

	printk
	(
		"Device: %d.%d, busy: %d\n",
		status.major,
		status.minor,
		status.busy
	);

	/* We don't want to talk to two processes at the
	 * same time
	 */
	if(status.busy)
		return -EBUSY;

	status.busy = true;

	/* Initialize the message. */
	sprintf
	(
		status.buf,
		"If I told you once, I told you %d times - %s",
		counter++,
		"Hello, world\n"
	);

	status.buf_ptr = status.buf;

	return SUCCESS;
}

/**
* Device Release:
* Call when process closes device file
**/
static int device_release(inode, file)
	struct inode* inode;
	struct file*  file;
{
#ifdef _DEBUG
	printk ("device_release(%p,%p)\n", inode, file);
#endif

	/* No longer busy */
	status.busy = false;

	return SUCCESS;
}

/**
* Device Read:
* call when a process attempts to read
**/
static ssize_t device_read(file, buffer, length, offset)
	struct file* file;
    char*        buffer;  /* The buffer to fill with data */
    size_t       length;  /* The length of the buffer */
    loff_t*      offset;  /* Our offset in the file */
{
	/* Number of bytes actually written to the buffer */
	int bytes_read = 0;

	/* Actually put the data into the buffer */
	while(length > 0)
	{
		/* Because the buffer is in the user data segment,
		 * not the kernel data segment, assignment wouldn't
		 * work. Instead, we have to use put_user which
		 * copies data from the kernel data segment to the
		 * user data segment.
		 */
		put_user(status.curr_char, buffer++);

		length--;
		bytes_read++;
	}

#ifdef _DEBUG
	printk
	(
		"ascii::device_read() - Read %d bytes, %d left\n",
		bytes_read,
		length
	);
#endif

	/* 
	 * once code reaches 127 we have to wrap around to '0'
	 */
	if(++status.curr_char == 127)
		status.curr_char = '0';

	/* Read functions are supposed to return the number
	 * of bytes actually inserted into the buffer
	 */
	return bytes_read;
}

/**
* Device Write:
* Call when somebody tries writing to the device file
**/
static ssize_t device_write(file, buffer, length, offset)
	struct file* file;
	const char*  buffer;  /* The buffer */
	size_t       length;  /* The length of the buffer */
	loff_t*      offset;  /* Our offset in the file */
{
	int nbytes = 0;

#ifdef _DEBUG
	printk
	(
		"ascii::device_write() - Length: [%d], Buf: [%s]\n",
		length,
		buffer
	);
#endif

	/* Rewind ASCII char back to '0' */
	status.curr_char = '0';

	return nbytes;
}

/**
* Initialize the module - Register the character device 
**/
int init_module(void)
{
	/* Register the character device (at least try) */
	status.major = register_chrdev
	(
		0,
		DEVICE_NAME,
		&Fops
	);

	/* Negative values signify an error */
	if(status.major < 0)
	{
		printk
		(
			"Sorry, registering the ASCII device failed with %d\n",
			status.major
		);

		return status.major;
	}

	printk
	(
		"Registeration is a success. The major device number is %d.\n",
		status.major
	);

	printk
	(
		"If you want to talk to the device driver,\n" \
		"you'll have to create a device file. \n" \
		"We suggest you use:\n\n" \
		"mknod %s c %d <minor>\n\n" \
		"You can try different minor numbers and see what happens.\n",
		DEVICE_NAME,
		status.major
	);

	return SUCCESS;
}


/** 
* Cleanup - unregister the appropriate file from /proc 
**/
void
cleanup_module(void)
{
	unregister_chrdev(status.major, DEVICE_NAME);
}