SHELL:=/bin/bash

EXE=play
TESTSOUND=SampleAudio_0.4mb.mp3

all:

	gcc -o play sound_playback.c -lasound

install:
	yum install alsa-lib-devel.x86_64


build:
	./$(EXE) 20010 3 2 < $(TESTSOUND)

clean:
	echo dependencies uninstalled


