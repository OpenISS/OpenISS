#include <stdio.h>

#include "OpenISSPipeline.h"

t_iss_state g_tISSStage;

t_iss_vfx_ops* a_pvfx[VFX_END_CLOSING + 1];

void iss_init()
{
    g_tISSStage.m_eCurrentEffect = VFX_TEST_START;
    a_pvfx[g_tISSStage.m_eCurrentEffect]->vfx_init();
}

void iss_draw()
{
    printf("Current effect: %d\n", g_tISSStage.m_eCurrentEffect);
    a_pvfx[g_tISSStage.m_eCurrentEffect]->vfx_draw();
}

void iss_free()
{
    printf("pipeline:iss_free() - NOOP");
}

/* EOF */
