#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility; 

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void){
   vec4 blendMapColor = texture(blendMap, pass_textureCoords);
   
   float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b );
   vec2 tiledCoords = pass_textureCoords * 40;
   vec4 backgroundTextureColor = texture (backgroundTexture, tiledCoords) * backTextureAmount;
   vec4 rTextureColor = texture (rTexture, tiledCoords) * blendMapColor.r;
   vec4 gTextureColor = texture (gTexture, tiledCoords) * blendMapColor.g;
   vec4 bTextureColor = texture (bTexture, tiledCoords) * blendMapColor.b;
   
   vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

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

   	out_Color = vec4(diffuse, 1.0) * totalColor + vec4(finalSpecular, 1.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
 }