#version 400 core

const int lightSources = 6;
const int maxSelectedOrHightlighted = 256;

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[lightSources];
in vec3 toCameraVector;
in float visibility; 
in vec4 shadowCoords;
in vec2 pass_worldPos;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform vec3 lightColor[lightSources];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform vec3 attenuation[lightSources];

uniform vec3 highlightedPositions[maxSelectedOrHightlighted];
uniform vec3 selectedPositions[maxSelectedOrHightlighted];
uniform int highlightedCount;
uniform int selectedCount;

const int pcfCount = 3;
const float highlightThickness = 1.4;
const float highlightAlpha = 0.5;
const vec3 highlightColor = vec3(1,0,0);
const vec3 selectColor = vec3(0,1,0);

float smoothlyStep(float edge0, float edge1, float x){
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}

float getHighlightAlpha(vec3 info, vec2 worldPos){
	float distance = length(info.xy - worldPos);
	float inner = 1.0 - smoothlyStep(info.z, info.z+0.05, distance);
	float outer = 1.0 - smoothlyStep(info.z * highlightThickness, info.z * highlightThickness + 0.05, distance);
	return (outer - inner) * highlightAlpha;
}

void main(void){
   
   float totalTexels = pow(pcfCount * 2.0 + 1.0, 2);
   float mapSize = textureSize(shadowMap, 0).x;
   float texelSize = 1.0 / mapSize;
   float total = 0.0;
   
   for (int x = -pcfCount; x<=pcfCount; x++){
   		for (int y = -pcfCount; y<=pcfCount; y++){
   			float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x,y) * texelSize).r;
   			 if (shadowCoords.z > objectNearestLight){
   	  			total += 1.0;
  			 }
   		}
   }
   
   total /= totalTexels;
   
   
   float lightFactor = 1.0 - (total * shadowCoords.w);

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
   totalDiffuse = max (totalDiffuse * lightFactor, 0.4);
   out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
   out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
    
   for (int i = 0; i<highlightedCount; i++){
      float alpha = getHighlightAlpha(highlightedPositions[i], pass_worldPos);
   	  out_Color = mix(out_Color, vec4(highlightColor, 1.0), alpha);
   }
   for (int i = 0; i<selectedCount; i++){
      float alpha = getHighlightAlpha(selectedPositions[i], pass_worldPos);
   	  out_Color = mix(out_Color, vec4(selectColor, 1.0), alpha);
   }
 }