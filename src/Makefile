SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=--install

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)
	#add --option to the end of the above line like $(OPTIONS) --boom

opencv:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(OPTIONS) --opencv

clean-opencv:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) --cleanup --opencv

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
