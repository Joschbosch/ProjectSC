#version 330 core

layout(location = 0) in vec3 squareVertices;
layout(location = 1) in vec4 xyzs; 
layout(location = 2) in vec4 color; 
layout(location = 3) in vec2 uvCoords; 

out vec2 UV;
out vec4 particlecolor;

uniform vec3 cameraRightWorldspace;
uniform vec3 cameraUpWorldspace;
uniform mat4 modelViewProjectionMatrix; 
uniform float numberOfRows;
void main()
{
	float particleSize = xyzs.w; // because we encoded it this way.
	vec3 particleCenter_wordspace = xyzs.xyz;
	
	vec3 vertexPosition_worldspace = 
		particleCenter_wordspace
		+ cameraRightWorldspace * squareVertices.x * particleSize
		+ cameraUpWorldspace * squareVertices.y *particleSize;

	// Output position of the vertex
	gl_Position = modelViewProjectionMatrix * vec4(vertexPosition_worldspace, 1.0f);
	
	float stages = pow(numberOfRows, 2);
    float timePerStage = (uvCoords.x / stages);
    float index = stages - floor(uvCoords.y/ timePerStage);
    float column = mod(index, numberOfRows);
    float xCoord0 = column / numberOfRows;
    float row =  floor(index / numberOfRows);
    float yCoord0 = row / numberOfRows;
    float size = 1.0 / numberOfRows;
	
	if (squareVertices.x == -0.5f && squareVertices.y == -0.5f){
		UV.x = xCoord0;
		UV.y = yCoord0 + size;
	}
	if (squareVertices.x == 0.5f && squareVertices.y == -0.5f) {
		UV.x = xCoord0 + size;
		UV.y = yCoord0 + size;
	}
	if (squareVertices.x == -0.5f && squareVertices.y == 0.5f) {
		UV.x = xCoord0;
		UV.y = yCoord0;
	}
	if (squareVertices.x == 0.5f && squareVertices.y == 0.5f) {
		UV.x = xCoord0 + size;
		UV.y = yCoord0;
	}
	particlecolor  = color;		
	
}
