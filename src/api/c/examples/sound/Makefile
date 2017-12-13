SHELL:=/bin/bash

EXE=play
TESTSOUND=SampleAudio_0.4mb.mp3

install:
	yum install alsa-lib-devel.x86_64

all:
	gcc -o play sound_playback.c -lasound

build:
	./$(EXE) 20010 3 2 < $(TESTSOUND)

clean:
	echo dependencies uninstalled


