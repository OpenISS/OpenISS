CC=gcc
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations

DRIVER=issimagedrv.o
MODULE=issimagedrv.ko
EXE=driver-test
OBJ=main.o $(DRIVER)
DEVICE_NAME=/dev/ISSIMAGEDRV
DEVICE_MINOR=1


obj-m += $(DRIVER)

all: $(EXE)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules
	make register
clean:
	rm -f $(EXE) $(OBJ)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean
	rmmod issimagedrv
	rm -f $(DEVICE_NAME)
	rm -f parent_image.ppd child_image.ppd

register:
	insmod ./$(MODULE)
	#mknod $(DEVICE_NAME) c $(VAR) $(DEVICE_MINOR)
	@echo "******To finish installing the driver type in:******"
	@echo "mknod $(DEVICE_NAME) c `dmesg | tail -5 | grep 'ISSIMG' | sed s/"ISSIMG "/\/` $(DEVICE_MINOR)"
	sleep 6

$(EXE): main.o
	$(CC) $(CC_OPTIONS) main.o -o $(EXE)

main.o: main.c
	$(CC) $(CC_OPTIONS) -c main.c

$(DRIVER): issimagedrv.h issimagedrv.c
	$(CC) $(CC_OPTIONS) -c issimagedrv.c
