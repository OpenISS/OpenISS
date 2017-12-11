#include "OpenISSPipeline.h"

t_iss_state g_tISSStage;
void* a_pvfx[VFX_END_CLOSING + 1];

void iss_init()
{
    g_tISSStage.m_eCurrentEffect = VFX_TEST_START;
}

void iss_draw()
{
    printf("Current effect: %d\n", g_tISSStage.m_eCurrentEffect);
}

void iss_free()
{
    printf("pipeline:iss_free() - NOOP");
}

/* EOF */
