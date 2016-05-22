#version 400 core

const int lightSources = 6;
const int maxWeights = 4;
const int MAX_JOINTS = 150;

const float density = 0.0035;
const float gradient = 5;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangents;
in vec4 jointWeights;
in ivec4 jointIndices;

out vec2 pass_textureCoords;
out vec3 toLightVector[lightSources];
out vec3 toCameraVector;
out vec3 surfaceNormal;
out float visibility;
out vec4 debugColor; 

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPositionEyeSpace[lightSources];
uniform float useFakeLighting;
uniform float numberOfRows;
uniform vec2 offset;
uniform vec4 plane;
uniform bool hasNormalMap;
uniform mat4 jointsMatrix[MAX_JOINTS];
uniform bool isAnimated;


void main (void){
	//Animation part
	vec4 initPos = vec4(0, 0, 0, 0);
	vec4 initNormal = vec4(0, 0, 0, 0);
	int count = 0;
	if (isAnimated){
	    for(int i = 0; i < maxWeights; i++){
	        float weight = jointWeights[i];
	        if(weight > 0) {
	            count++;
	            int jointIndex = jointIndices[i];
	            vec4 tmpPos = jointsMatrix[jointIndex] * vec4(position, 1.0);
	            initPos += weight * tmpPos;
	            vec4 tmpNormal = jointsMatrix[jointIndex] * vec4(normal, 0.0);
	            initNormal += weight * tmpNormal;
	         }
	    }
  	} 
  	if (count == 0 || !isAnimated) {
   		initPos = vec4(position, 1);
   		initNormal = vec4(normal, 0.0);
   	} 
	vec4 worldPosition = transformationMatrix * initPos;
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoords = (textureCoords/numberOfRows) + offset;
	
	vec3 actualNormal = initNormal.xyz; 
	if (useFakeLighting > 0.5){
		actualNormal = vec3 (0.0, 1.0, 0.0);
	}

	surfaceNormal = (modelViewMatrix * initNormal).xyz;
	if (!isAnimated && hasNormalMap) {
		vec3 norm = normalize(surfaceNormal);
		vec3 tang = normalize((modelViewMatrix * vec4(tangents, 0.0)).xyz);
		vec3 bitang = normalize(cross(norm, tang));
		
		mat3 toTangentSpace = mat3(
			tang.x, bitang.x, norm.x,
			tang.y, bitang.y, norm.y,
			tang.z, bitang.z, norm.z
		);
		
		for(int i=0;i<lightSources;i++){
			toLightVector[i] = toTangentSpace * (lightPositionEyeSpace[i] - positionRelativeToCamera.xyz);
		}
		toCameraVector = toTangentSpace * (-positionRelativeToCamera.xyz);
	} else {
		for(int i=0;i<lightSources;i++){
			toLightVector[i] = (lightPositionEyeSpace[i] - positionRelativeToCamera.xyz);
		}
		toCameraVector = (-positionRelativeToCamera.xyz);
	}
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0,1.0);
}