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

libfreenect2: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --freenect2

tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --tinyosc

ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --ofx

ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --ogl

opencv:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS) --opencv

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION)

clean-opencv:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --opencv

clean-libfreenect2:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --freenect2

clean-tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --tinyosc

clean-ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --ofx

clean-libfreenect:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --freenect

clean-ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --ogl

maintainer-clean: clean
	rm -f $(SCRIPTSDIR)/build.cache 
