#include "libfreenect.h"
#include "libfreenect_sync.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#ifndef _WIN32
  #include <unistd.h>
#else
  // Microsoft Visual C++ does not provide the <unistd.h> header, but most of
  // its contents can be found within the <stdint.h> header:
  #include <stdint.h>
  // except for the UNIX sleep() function that has to be emulated:
  #include <windows.h>
  // http://pubs.opengroup.org/onlinepubs/009695399/functions/sleep.html
  // According to the link above, the semantics of UNIX sleep() is as follows:
  // "If sleep() returns because the requested time has elapsed, the value
  //  returned shall be 0. If sleep() returns due to delivery of a signal, the
  //  return value shall be the "unslept" amount (the requested time minus the
  //  time actually slept) in seconds."
  // The following function does not implement the return semantics, but
  // will do for now... A proper implementation would require Performance
  // Counters before and after the forward call to the Windows Sleep()...
  unsigned sleep(unsigned seconds)
  {
    Sleep(seconds*1000);  // The Windows Sleep operates on milliseconds
    return(0);
  }
  // Note for MinGW-gcc users: MinGW-gcc also does not provide the UNIX sleep()
  // function within <unistd.h>, but it does provide usleep(); trivial wrapping
  // of sleep() aroung usleep() is possible, however the usleep() documentation
  // (http://docs.hp.com/en/B2355-90682/usleep.2.html) clearly states that:
  // "The useconds argument must be less than 1,000,000. (...)
  //  (...) The usleep() function may fail if:
  //  [EINVAL]
  //     The time interval specified 1,000,000 or more microseconds."
  // which means that something like below can be potentially dangerous:
  // unsigned sleep(unsigned seconds)
  // {
  //   usleep(seconds*1000000);  // The usleep operates on microseconds
  //   return(0);
  // }
  // So, it is strongly advised to stick with the _WIN32/_MSC_VER
  // http://www.xinotes.org/notes/note/439/
#endif//_WIN32



/* This is a simple demo. It connects to the kinect and plays with the motor,
   the accelerometers, and the LED. It doesn't do anything with images. And,
   unlike the other examples, no OpenGL is required!

   So, this should serve as the reference example for working with the motor,
   accelerometers, and LEDs.   */

void no_kinect_quit(void)
{
	printf("Error: Kinect not connected?\n");
	exit(1);
}
