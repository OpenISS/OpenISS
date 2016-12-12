#include "OpenISS.h"

/*
e_vfx_enum g_eCurrentEffect = VFX_TEST_START;
*/
 
t_iss_state g_tISSStage;

void setup()
{
    g_tISSStage.m_eCurrentEffect = VFX_TEST_START;
}

int main(int argc, char** argv)
{
    setup();
}

void draw()
{

}

/* EOF */
