SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup

install_ogl:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --ogl

build_ogl:
	

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
