#include <arpa/inet.h>
#include <sys/select.h>
#include <fcntl.h>
#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include "tinyosc.h"

static volatile bool keepRunning = true;

// handle Ctrl+C
static void sigintHandler(int x) {
  keepRunning = false;
}

/**
  * Prepare and send a message     *use curl ifconfig.me to get ip address and throw it in as argv
  */
int main(int argc, char *argv[])
{

  char buffer[2048]; // declare a 2Kb buffer to read packet data into
  printf("Starting write tests:\n");
  int len = 0; 

/* Currently, send a hard coded message to the reciever */
  len = tosc_writeMessage(buffer, sizeof(buffer), "/player 1", "f",
      1.0f, 2.0f, 3.0f); 
  tosc_printOscBuffer(buffer, len);
  printf("done.\n");

/**
  * While the program is running, the program will be sending the message continuously to port 9000
  */
    int sockfd;
    if((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        printf("\n Error : Could not create socket \n");
        return 1;
    } 

  struct sockaddr_in serv_addr; 

  memset(&serv_addr, '0', sizeof(serv_addr)); 

  serv_addr.sin_family = AF_INET;

  serv_addr.sin_port = htons(9000); 

      if(inet_pton(AF_INET, argv[1], &serv_addr.sin_addr)<=0)
    {
        printf("\n inet_pton error occured\n");
        return 1;
    } 

    if( connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
       printf("\n Error : Connect Failed \n");
       return 1;
} 

  while (keepRunning) {

    tosc_printOscBuffer(buffer, len);
    if(send(sockfd,buffer,len,0) < 0)
	{
    		printf("Error: Not sending \n");
	}

  }

  return 0; 

}
