#include <arpa/inet.h>
#include <sys/select.h>
#include <fcntl.h>
#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <unistd.h>

#include "tinyosc.h"

static volatile bool keepRunning = true;

// handle Ctrl+C
static void sigintHandler(int x) {
  keepRunning = false;
}

/**
  * Prepare and send a message
  */
int main(int argc, char *argv[])
{

  char buffer[2048]; // declare a 2Kb buffer to read packet data into
  printf("Starting write tests:\n");
  int len = 0;

/* Currently, send a hard coded message to the reciever */
  len = tosc_writeMessage(buffer, sizeof(buffer), "/player 1", "/left-hand",
      1.0f, 2.0f, 3.0f); 
  tosc_printOscBuffer(buffer, len);
  printf("done.\n");

/**
  * While the program is running, the program will be sending the message continuously to port 9000
  */

  while (keepRunning) {
 printf("Sending message\n");
    tosc_printOscBuffer(buffer, len);
  }

  return 0; 

}
