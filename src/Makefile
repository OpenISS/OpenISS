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
	./$(EXE) $(OPTIONS)

libfreenect:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --freenect

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup
clean-libfreenect:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --freenect

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
