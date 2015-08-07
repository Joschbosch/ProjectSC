#version 330 core

in vec2 UV;
in vec4 particleColor;

out vec4 color;

uniform sampler2D myTextureSampler;

void main(){
	color = texture2D( myTextureSampler, UV ) * particleColor;

}