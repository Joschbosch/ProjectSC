#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVec;

uniform sampler2D modelTexture;
uniform vec3 lightColor;

void main(void) {
   vec3 normalUnit = normalize(surfaceNormal);
   vec3 lightUnit = normalize(toLightVec);
   float nDot1 = dot(normalUnit, lightUnit);
   float brightness = max(nDot1, 0.0);
   vec3 diffuse = brightness * lightColor; 
   out_Color = vec4(diffuse, 0.0) * texture(modelTexture, pass_textureCoords);
}