#include "OpenISS.h"

/*
 TODO: option to select GL2 or GL3 viewer
 TODO: options to record/replay/stream
 TODO: protonect pipeline options
*/

extern int g_argc;
extern char **g_argv;

int main(int argc, char** argv)
{
    g_argc = argc;
    g_argv = argv;
    
    /* From pipeline */
    iss_init();
    //iss_draw();
    iss_free();
    
    return 0;
}

/* EOF */
