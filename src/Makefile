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
	./$(EXE) $(OPTIONS) $(INSTALL_OPTION)

opencv:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(INSTALL_OPTION) --opencv

clean-opencv:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --opencv

libfreenect2: $(SCRIPTSDIR)/$(EXE)
        cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(INSTALL_OPTION) --freenect2

clean-libfreenect2:
        cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --freenect2

ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --ogl

clean-ogl:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --ogl

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION)

maintainer-clean: clean
rm $(SCRIPTSDIR)/build.cache 
