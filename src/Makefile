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

maintainer-clean:
	make clean;
	rm /scripts/build.cache 
