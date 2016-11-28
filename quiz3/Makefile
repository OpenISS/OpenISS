obj-m += hello.o

all: compile
	insmod ./hello.ko

clean:
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) clean
	rmmod hello
	rm -f /dev/hello

compile:
	make -C /lib/modules/$(shell uname -r)/build M=$(PWD) modules