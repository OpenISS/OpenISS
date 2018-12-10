#include <arpa/inet.h>
#include <sys/select.h>
#include <fcntl.h>
#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "tinyosc.h"

static volatile bool keepRunning = true;

// handle Ctrl+C
static void sigintHandler(int x) {
  keepRunning = false;
}

int main(int argc, char *argv[])
{
 char buffer[2048]; // declare a 2Kb buffer to read packet data int
	//open the file to write the log to
	FILE * fp;
	char *str1 = "HEllo";
	fp = fopen("Log.txt","w+");
	if(fp == NULL)
	{
		printf("Error!");
		exit(1);
	}


 // open a socket to listen for datagrams (i.e. UDP packets) on port 9000
  const int fd = socket(AF_INET, SOCK_DGRAM, 0);
  fcntl(fd, F_SETFL, O_NONBLOCK); // set the socket to non-blocking
  struct sockaddr_in sin;
  sin.sin_family = AF_INET;
  sin.sin_port = htons(9000);
  sin.sin_addr.s_addr = INADDR_ANY;
  bind(fd, (struct sockaddr *) &sin, sizeof(struct sockaddr_in));
  printf("tinyosc is now listening on port 9000.\n");
  fprintf(fp, "%s", "HelloWorld");
  printf("Press Ctrl+C to stop.\n");
  while (keepRunning) {
    fd_set readSet;
    FD_ZERO(&readSet);
    FD_SET(fd, &readSet);
    struct timeval timeout = {1, 0}; // select times out after 1 second
    if (select(fd+1, &readSet, NULL, NULL, &timeout) > 0) {
      struct sockaddr sa; // can be safely cast to sockaddr_in
      socklen_t sa_len = sizeof(struct sockaddr_in);
      int len = 0;
      while ((len = (int) recvfrom(fd, buffer, sizeof(buffer), 0, &sa, &sa_len)) > 0) {
        if (tosc_isBundle(buffer)) {
          tosc_bundle bundle;
          tosc_parseBundle(&bundle, buffer, len);
          const uint64_t timetag = tosc_getTimetag(&bundle);
          tosc_message osc;
          while (tosc_getNextMessage(&bundle, &osc)) {
            tosc_printMessage(&osc);
	    fprintf(fp, "%s\n", &osc);
          }
        } else {
          tosc_message osc;
          tosc_parseMessage(&osc, buffer, len);
          tosc_printMessage(&osc);
 	  fprintf(fp, "%s\n", &osc);
        }
      }
    }
  }

  // close the UDP socket
  fclose(fp);
  close(fd);
  return 0; 
}
