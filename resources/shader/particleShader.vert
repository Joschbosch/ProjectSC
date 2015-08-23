#version 330 core

layout(location = 0) in vec3 squareVertices;
layout(location = 1) in vec4 xyzs; 
layout(location = 2) in vec4 color; 
out vec2 UV;
out vec4 particlecolor;

uniform vec3 cameraRightWorldspace;
uniform vec3 cameraUpWorldspace;
uniform mat4 modelViewProjectionMatrix; 

uniform float numberOfRows;
uniform vec2 offset;

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
	// UV of the vertex. No special space for this one.
	UV = (squareVertices.xy/numberOfRows) + offset;
	particlecolor = color;
}

