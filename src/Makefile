SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)

tinyosc: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS) --tinyosc

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup
clean-tinyosc:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup --tinyosc

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
