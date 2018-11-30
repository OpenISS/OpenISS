#include "tutorial03Wrapper.h"

#include <../../../../../ogl/common/shader.cpp>

GLuint CLoadShaders()
{
	return LoadShaders( "SimpleVertexShader.vertexshader", "SimpleFragmentShader.fragmentshader" );
}
