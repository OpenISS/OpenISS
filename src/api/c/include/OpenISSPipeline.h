#ifndef _OPENISS_PIPELINE_H
#define _OPENISS_PIPELINE_H

#include "vfx.h"

typedef enum
{
    VFX_TEST_START = 0,
    VFX_CONTOUR,
    VFX_END_CLOSING
} e_vfx_enum;

typedef struct
{
    e_vfx_enum m_eCurrentEffect;
}
t_iss_state;

extern t_iss_state g_tISSStage;

extern t_iss_vfx_ops* a_pvfx[VFX_END_CLOSING + 1];

void iss_init(void);
void iss_draw(void);
void iss_free(void);

#endif
