CC=gcc
DEBUG=-g -D_DEBUG
DEFINE=-DMODULE -D__KERNEL__ -DLINUX
WARNINGS=-Wall -Wmissing-prototypes -Wmissing-declarations
#ISO=-ansi -pedantic
CC_OPTIONS=-O1 $(WARNINGS) $(ISO) $(DEBUG) $(DEFINE)

# Where to look for header files
INC=-I. -I/usr/include -I/usr/src/kernels/`uname -r`/include

DRIVER=issimagedrv.o
MODULE=issimagedrv.ko
EXE=issimagedrv-test
OBJ=main.o $(DRIVER)

obj-m += $(DRIVER)

all: $(EXE)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules

clean:
	rm -f $(EXE) $(OBJ)
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean

register: $(DRIVER)
	insmod ./$(MODULE)
	modinfo $(MODULE)
	lsmod | grep issimagedrv

$(EXE): main.o
	$(CC) main.o -o $(EXE)

main.o: main.c common.h
	$(CC) $(CC_OPTIONS) $(INC) -c main.c

$(DRIVER): types.h issimagedrv.h issimagedrv.c
	$(CC) $(CC_OPTIONS) $(INC) -c issimagedrv.c

clean-all:
	make clean
	rmmod issimagedrv
	lsmod