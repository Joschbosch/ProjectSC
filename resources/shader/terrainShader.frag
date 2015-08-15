#version 400 core

const int lightSources = 6;

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[lightSources];
in vec3 toCameraVector;
in float visibility; 

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor[lightSources];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform vec3 attenuation[lightSources];

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
   vec3 toCamUnit = normalize(toCameraVector);

   vec3 totalDiffuse = vec3 (0.0);
   vec3 totalSpecular = vec3 (0.0);
   
   for (int i = 0; i<lightSources; i++){
   	   float distance = length(toLightVector[i]);
   	   float attFactor = attenuation[i].x + (  attenuation[i].y *distance )+( attenuation[i].z * distance* distance);
	   vec3 lightUnit = normalize(toLightVector[i]);
	   vec3 lightDirection = -lightUnit;
	   float nDot1 = dot(normalUnit, lightUnit);
	   float brightness = max(nDot1, 0.0);
	   vec3 reflectedLightDirection = reflect(lightDirection, normalUnit);
	   float specFac = dot(reflectedLightDirection, toCamUnit);
	   specFac = max (specFac, 0.0);
	   float dampedFac = pow(specFac, shineDamper);
	   totalSpecular = totalSpecular + (dampedFac * reflectivity * lightColor[i])/attFactor;
	   totalDiffuse = totalDiffuse + (brightness * lightColor[i])/attFactor; 
   }
   totalDiffuse = max (totalDiffuse, 0.0);
   	out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
 }