SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6
INSTALL_OPTION=--install
UNINSTALL_OPTION=--cleanup

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION)

libfreenect:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --freenect

tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --tinyosc

ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --ofx
	
clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION)
clean-tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --tinyosc
clean-ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --ofx
clean-libfreenect:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --freenect

install_ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --ogl

build_ogl:
	

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
