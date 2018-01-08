#ifndef _VFX_KINECT1_RGB
#define _VFX_KINECT1_RGB

int vfx_kinect1_rgb_module_init(void);

int vfx_kinect1_rgb_init(void);
void vfx_kinect1_rgb_draw(void);
void vfx_kinect1_rgb_free(void);
void vfx_kinect1_rgb_DrawGLScene(void);
void vfx_kinect1_rgb_keyPressed(unsigned char key, int x, int y);

#endif /* _VFX_KINECT1_RGB */
