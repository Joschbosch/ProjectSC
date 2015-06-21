#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility; 

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;


void main(void){
   vec3 normalUnit = normalize(surfaceNormal);
   vec3 lightUnit = normalize(toLightVector);
   vec3 toCamUnit = normalize(toCameraVector);
   
   vec3 lightDirection = -lightUnit;
   
   float nDot1 = dot(normalUnit, lightUnit);
   float brightness = max(nDot1, 0.2);
   vec3 diffuse = brightness * lightColor; 

	vec3 reflectedLightDirection = reflect(lightDirection, normalUnit);
	
	float specFac = dot(reflectedLightDirection, toCamUnit);
	specFac = max (specFac, 0.0);
	float dampedFac = pow(specFac, shineDamper);
	vec3 finalSpecular = dampedFac * reflectivity * lightColor;
	
	vec4 textureColor =  texture(textureSampler, pass_textureCoords);
	if (textureColor.a < 0.5){
		discard;
	}
   out_Color = vec4(diffuse, 1.0) * textureColor + vec4(finalSpecular, 1.0);
   out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
   
 }