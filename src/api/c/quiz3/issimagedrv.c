/* issimagedrv.c */

#include "issimagedrv.h"
#include <linux/fs.h>
#include <asm/segment.h>
#include <asm/uaccess.h>
#include <linux/buffer_head.h>

static int Major; /* Major number assigned to our device driver */
unsigned int** data[3];
unsigned char* readData;
unsigned int size = 255;
struct file* thefile = NULL;


struct file* file_open(const char* path, int flags, int rights) {
    struct file* filp = NULL;
    mm_segment_t oldfs;
    int err = 0;

    oldfs = get_fs();
    set_fs(get_ds());
    filp = filp_open(path, flags, rights);
    set_fs(oldfs);
    if(IS_ERR(filp)) {
        err = PTR_ERR(filp);
        return NULL;
    }
    return filp;
}

void file_close(struct file* file) {
    filp_close(file, NULL);
}

int file_read(struct file* file, loff_t offset, unsigned char* data, unsigned int size) {
    mm_segment_t oldfs;
    int ret;

    oldfs = get_fs();
    set_fs(get_ds());

    ret = vfs_read(file, data, size, &offset);
    printk("\n%i\n", ret);

    set_fs(oldfs);
    return ret;
}  

int file_write(struct file* file, unsigned long long offset, unsigned char* data, unsigned int size) {
    mm_segment_t oldfs;
    int ret;

    oldfs = get_fs();
    set_fs(get_ds());

    ret = vfs_write(file, data, size, &offset);

    set_fs(oldfs);
    return ret;
}



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
 	printk("%s being read\n", DEVICE_NAME);
	return SUCCESS;
}

/* Process attempts to write to device file */
static ssize_t device_write(struct file *file, const char *buff, size_t len, loff_t * off)
{
	printk("%s", buff);
	thefile = file_open(buff, 0, 0);

	int ret;
	ret = file_read(thefile, 0, readData, size);

	//printk("%i", ret);

	file_close(thefile);
	//printk("%s being written to\n", DEVICE_NAME);
	return SUCCESS;
}