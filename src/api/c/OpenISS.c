#include "OpenISS.h"

/*
 TODO: option to select GL2 or GL3 viewer
 TODO: options to record/replay/stream
*/

int main(int argc, char** argv)
{
    /* From pipeline */
    iss_init();
    iss_draw();
    iss_free();
    
    return 0;
}

/* EOF */
