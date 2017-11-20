SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)

tinyosc: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS) --tinyosc

ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --ofx

install-ofx:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --ofxdeps

opencv:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS) --opencv

libfreenect:
	pushd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS) --freenect
	popd

libfreenect2:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS) --freenect2


clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup
clean-tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup --tinyosc
clean-openframeworks:
	cd $(SCRIPTSDIR) ; \
	./dependencies/el6.sh --cleanofx
clean-opencv:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup --opencv
clean-libfreenect2:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(OPTIONS) --cleanup --freenect2

clean-libfreenect:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(OPTIONS) --cleanup --freenect

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
