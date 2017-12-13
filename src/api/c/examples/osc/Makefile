CC=gcc

FLAG=-std=gnu99
DEP=tinyosc
SENDER=issoscsender
RECEIVER=issoscreceiver

sender: 
	$(CC) -c $(FLAG) $(DEP).c
	$(CC) -c $(SENDER).c
	$(CC)  $(SENDER).o $(DEP).o -o $(SENDER)
	

receiver: 
	$(CC) -c $(FLAG) $(DEP).c
	$(CC) -c $(RECEIVER).c
	$(CC) -o $(RECEIVER) $(RECEIVER).o $(DEP).o	

clean:
	rm tinyosc.o
	rm issoscreceiver.o
	rm issoscsender.o
	
