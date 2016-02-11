#version 400 core

const int lightSources = 6;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[lightSources];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[lightSources];
uniform vec4 plane;
uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

const float density = 0.0035;
const float gradient = 5;
const float transitionDistance = 10.0;
void main (void){
    
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	shadowCoords = toShadowMapSpace * worldPosition;
	
    gl_ClipDistance[0] = dot(worldPosition, plane);	
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoords = textureCoords;

	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	for (int i = 0; i < lightSources; i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0,1.0);
	distance = distance - (shadowDistance - transitionDistance);
	distance = distance / transitionDistance;
	shadowCoords.w = clamp (1.0 - distance, 0.0, 1.0);
}