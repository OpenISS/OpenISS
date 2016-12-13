#include "OpenISS.h"

/*
e_vfx_enum g_eCurrentEffect = VFX_TEST_START;
*/
 
t_iss_state g_tISSStage;
void* a_pvfx[VFX_END_CLOSING + 1];

void iss_init()
{
    g_tISSStage.m_eCurrentEffect = VFX_TEST_START;
}

int main(int argc, char** argv)
{
    iss_init();
    iss_draw();
    iss_free();
    return 0;
}

void iss_draw()
{

}

void iss_free()
{
    
}

/* EOF */
