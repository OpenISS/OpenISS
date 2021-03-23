#include <GL/glew.h>

#ifdef __cplusplus
#define EXTERNC extern "C"
#else
#define EXTERNC
#endif

#ifdef __cplusplus





extern "C" {
#else

#endif

/*extern "C" */
GLuint CLoadShaders();
void ComputeMVP();
GLfloat* GetMVP(int x, int y);
#ifdef __cplusplus
}
#endif
