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
	
ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --ogl

opencv:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(OPTIONS) --opencv

clean-opencv:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) --cleanup --opencv

libfreenect2: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(INSTALL_OPTION) --freenect2

clean-libfreenect2:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --freenect2

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

clean-ogl:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --ogl

clean-libfreenect2:
        cd $(SCRIPTSDIR) ; \
        ./$(EXE) --cleanup --freenect2


maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
