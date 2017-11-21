SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)
ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --install --ofx
clean-ofx:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup --ofx
	
clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
