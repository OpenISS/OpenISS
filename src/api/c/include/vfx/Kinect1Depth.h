#ifndef _VFX_KINECT1_DEPTH
#define _VFX_KINECT1_DEPTH

int vfx_kinect1_depth_module_init(void);

int vfx_kinect1_depth_init(void);
void vfx_kinect1_depth_draw(void);
void vfx_kinect1_depth_free(void);
void vfx_kinect1_depth_DrawGLScene(void);
void vfx_kinect1_depth_keyPressed(unsigned char key, int x, int y);

#endif /* _VFX_KINECT1_DEPTH */
