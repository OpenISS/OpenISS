/**
* issimagedrv.h
*
* Prototypes and macros for issimagedrv
*
* Brian Baron
* CSI 230
*/

#ifndef _ISSIMAGE_DRV_H_
#define _ISSIMAGE_DRV_H_

/**
* Necesssary headers
**/
#include <linux/kernel.h>	/* For kernel work */
#include <linux/module.h>	/* Kernel module */

#include <linux/fs.h>		/* Character device definitions */
#include <asm/uaccess.h>	/* for put/get_user */

/**
* Return codes
**/
#define SUCCESS 0

/*********************
* Device declarations ************************************
**********************/
#define DRV_BUF_SIZE 80					/* Max length of message from device */
#define DEVICE_NAME "/dev/ISSIMAGEDRV"	/* Name of our device */

/**
* Driver Status (struct)
**/
typedef struct _driver_status
{
	/* Is the device open right now? */
	bool busy;

	/* Current char in buffer */
	char curr_char;

	/* Character buffer to hold message */
	char buf[DRV_BUF_SIZE];

	/* Buffer pointer: where are we in the read process? */
	char* buf_ptr;

	/* Major and Minor device numbers */
	int major;
	int minor;

} driver_status_t;

/**
* Function prototypes
**/
static int device_open(struct inode*, struct file*);
static int  device_release(struct inode*, struct file*);
static ssize_t device_read(struct file*, char*, size_t, loff_t*);
static ssize_t device_write(struct file*, const char*, size_t, loff_t*);

/* Kernel module-related */

/*********************
* Module declarations ************************************
**********************/

/**
* File operations (struct)
**/
struct file_operations Fops =
{
	NULL,   /* owner */
	NULL,   /* seek */
	device_read,
	device_write,
	NULL,   /* readdir */
	NULL,   /* poll/select */
	NULL,   /* ioctl */
	NULL,   /* mmap */
	device_open,
	NULL,   /* flush */
	device_release  /* a.k.a. close */
};

int init_module(void);
void cleanup_module(void);

#endif /* _ISSIMAGE_DRV_H_ */