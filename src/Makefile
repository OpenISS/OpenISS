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

tinyosc: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(INSTALL_OPTION) --tinyosc

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup
clean-tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(UNINSTALL_OPTION) --tinyosc

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
