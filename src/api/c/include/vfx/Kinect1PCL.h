#ifndef _VFX_KINECT1_PCL
#define _VFX_KINECT1_PCL

int vfx_kinect1_pcl_module_init(void);

int vfx_kinect1_pcl_init(void);
void vfx_kinect1_pcl_draw(void);
void vfx_kinect1_pcl_free(void);

void vfx_kinect1_pcl_DrawGLScene(void);
void vfx_kinect1_pcl_keyPressed(unsigned char key, int x, int y);


void vfx_kinect1_pcl_mouseMoved(int x, int y);
void vfx_kinect1_pcl_mousePress(int button, int state, int x, int y);

void vfx_kinect1_pcl_no_kinect_quit(void);

#endif /* _VFX_KINECT1_PCL */
