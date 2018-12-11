#include "tutorial03Wrapper.h"

#include <../../../../../ogl/common/shader.cpp>

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

glm::mat4 MVP;
GLfloat* MVPC;

GLuint CLoadShaders()
{
	return LoadShaders( "SimpleVertexShader.vertexshader", "SimpleFragmentShader.fragmentshader" );
}

void ComputeMVP()
{
	// Projection matrix : 45° Field of View, 4:3 ratio, display range : 0.1 unit <-> 100 units
	glm::mat4 Projection = glm::perspective(glm::radians(45.0f), 4.0f / 3.0f, 0.1f, 100.0f);
	// Or, for an ortho camera :
	//glm::mat4 Projection = glm::ortho(-10.0f,10.0f,-10.0f,10.0f,0.0f,100.0f); // In world coordinates
	
	// Camera matrix
	glm::mat4 View       = glm::lookAt(
								glm::vec3(4,3,3), // Camera is at (4,3,3), in World Space
								glm::vec3(0,0,0), // and looks at the origin
								glm::vec3(0,1,0)  // Head is up (set to 0,-1,0 to look upside-down)
						   );
	// Model matrix : an identity matrix (model will be at the origin)
	glm::mat4 Model      = glm::mat4(1.0f);
	// Our ModelViewProjection : multiplication of our 3 matrices
/*	glm::mat4 */	MVP        = Projection * View * Model; // Remember, matrix multiplication is the other way around
}

GLfloat* GetMVP(int x, int y)
{
	//MVPC = MVP[x][y]; 
	//return MVPC;
	return &MVP[0][0];
}
