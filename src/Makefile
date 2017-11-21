SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=--install

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)

libfreenect2: $(SCRIPTSDIR)/$(EXE)
        cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(OPTIONS) --freenect2

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup

clean-libfreenect2:
        cd $(SCRIPTSDIR) ; \
        ./$(EXE) --cleanup --freenect2


maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
