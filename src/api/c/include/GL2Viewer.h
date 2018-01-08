#ifndef ISS_GL2VIEWER_H
#define ISS_GL2VIEWER_H

void DrawGLScene();
void keyPressed(unsigned char key, int x, int y);
void ReSizeGLScene(int Width, int Height);
void InitGL(int Width, int Height);
void initGLUT();
int viewer_init(int argc, char **argv);

#endif /* ISS_GL2VIEWER_H */
