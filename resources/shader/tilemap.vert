#version 330

layout(location = 0) in vec4 position;

uniform mat4 cameraToClipMatrix;
uniform mat4 worldToCameraMatrix;
uniform mat4 modelToWorldMatrix;

void main()
{
	vec4 v = vec4(position);
	gl_Position = cameraToClipMatrix * worldToCameraMatrix * modelToWorldMatrix * v;
}
