/* issimagedrv.h */

#ifndef ISS_IMAGE_DRV_H
#define ISS_IMAGE_DRV_H

#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/fs.h>
#include <asm/uaccess.h>

#define SUCCESS 0
#define DEVICE_NAME "/dev/ISSIMAGEDRV"
#define BUF_LEN 80

int init_module(void);
void cleanup_module(void);
static int device_open(struct inode*, struct file*);
static int device_release(struct inode*, struct file*);
static ssize_t device_read(struct file*, char*, size_t, loff_t*);
static ssize_t device_write(struct file*, const char*, size_t, loff_t*);

struct file_operations Fops = 
{
	.read = device_read,
	.write = device_write,
	.open = device_open,
	.release = device_release
};
#endif
