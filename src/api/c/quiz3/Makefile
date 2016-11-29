CC=gcc
CC_OPTIONS=-Wall -Wmissing-prototypes -Wmissing-declarations

DRIVER=issimagedrv.o
MODULE=issimagedrv.ko
EXE=driver-test
OBJ=main.o $(DRIVER)

obj-m += $(DRIVER)

all: $(EXE)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules
	make register
clean:
	rm -f $(EXE) $(OBJ)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean
	rmmod issimagedrv
	rm -f /dev/issimagedrv

register:
	insmod ./$(MODULE)

$(EXE): main.o
	$(CC) $(CC_OPTIONS) main.o -o $(EXE)

main.o: main.c
	$(CC) $(CC_OPTIONS) -c main.c

$(DRIVER): issimagedrv.h issimagedrv.c
	$(CC) $(CC_OPTIONS) -c issimagedrv.c
