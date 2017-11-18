SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)
build-ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --ofx
install-ofx:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --ofxdeps
clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
