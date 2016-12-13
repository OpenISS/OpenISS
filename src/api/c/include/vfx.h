#ifndef _VFX_H
#define _VFX_H

typedef struct
{
    void (*vfx_init) (void);
    void (*vfx_draw) (void);
    void (*vfx_free) (void);
}
t_iss_vfx_ops;

extern t_iss_vfx_ops p_oVFXEmptyOpenGLTest;

#endif
