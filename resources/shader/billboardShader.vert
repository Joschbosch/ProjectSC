#version 330 core

layout(location = 0) in vec3 squareVertices;

out vec2 UV;

uniform vec3 cameraRightWorldspace;
uniform vec3 cameraUpWorldspace;
uniform mat4 modelViewProjectionMatrix; // Model-View-Projection matrix, but without the Model (the position is in BillboardPos; the orientation depends on the camera)
uniform vec3 billboardPos; // Position of the center of the billboard
uniform vec2 billboardSize; // Size of the billboard, in world units (probably meters)

void main()
{
	vec3 particleCenter_wordspace = billboardPos;
	
	vec3 vertexPosition_worldspace = 
		particleCenter_wordspace
		+ cameraRightWorldspace * squareVertices.x * billboardSize.x
		+ cameraUpWorldspace * squareVertices.y * billboardSize.y;

	// Output position of the vertex
	gl_Position = modelViewProjectionMatrix * vec4(vertexPosition_worldspace, 1.0f);

	// UV of the vertex. No special space for this one.
	UV = squareVertices.xy + vec2(0.5, 0.5);
}

