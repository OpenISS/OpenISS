#include "tutorial02Wrapper.h"

#include <../../../../../ogl/common/shader.cpp>

GLuint CLoadShaders()
{
	return LoadShaders( "SimpleVertexShader.vertexshader", "SimpleFragmentShader.fragmentshader" );
}
