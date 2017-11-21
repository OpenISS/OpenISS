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

ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --ogl

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup

clean-ogl:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(UNINSTALL_OPTION) --ogl

maintainer-clean: clean
rm $(SCRIPTSDIR)/build.cache 
