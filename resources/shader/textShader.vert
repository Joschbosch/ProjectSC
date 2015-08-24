#version 330 core

layout(location = 0) in vec3 squareVertices;
layout(location = 1) in vec4 xyzs; 
layout(location = 2) in vec4 color; 

out vec2 UV;

void main(){
	vec2 vertexPosition_homoneneousspace = position - vec2(400,300); // [0..800][0..600] -> [-400..400][-300..300]
	vertexPosition_homoneneousspace /= vec2(400,300);
	gl_Position =  vec4(vertexPosition_homoneneousspace,0,1);
	UV = vertexUV;
}