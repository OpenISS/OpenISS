SHELL:=/bin/bash

EXE=build.sh
SCRIPTSDIR=./scripts
OPTIONS=el6

all:
	make -C api $@

deps: $(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
	./$(EXE) $(OPTIONS)
	#add --option to the end of the above line like $(OPTIONS) --boom

opencv:
	$(SCRIPTSDIR)/$(EXE)
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) $(OPTIONS) --opencv

clean-opencv:
	cd $(SCRIPTSDIR) ; \
        ./$(EXE) --cleanup --opencv

clean:
	cd $(SCRIPTSDIR) ; \
	./$(EXE) --cleanup

maintainer-clean: clean
	rm $(SCRIPTSDIR)/build.cache 
