obj-m += issimagedrv.o

all: compile
	insmod ./issimagedrv.ko

clean:
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean
	rmmod issimagedrv
	rm -f /dev/issimagedrv

compile:
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules