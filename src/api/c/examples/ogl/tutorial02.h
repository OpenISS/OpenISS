#ifndef _TUTORIAL02_H
#define _TUTORIAL02_H

typedef struct
{
    void (*02_init) (void);
    void (*02_draw) (void);
    void (*02_free) (void);
}
t_iss_02_ops;

extern t_iss_02_ops p_o02EmptyOpenGLTest;

#endif
