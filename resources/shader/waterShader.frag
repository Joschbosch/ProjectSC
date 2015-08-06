#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvTexture;
uniform float moveFactor;

const float waveStrength = 0.02;

void main(void) {

	vec2 normalizedDeviceSpace = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 reflectionTexCoords = vec2(normalizedDeviceSpace.x, -normalizedDeviceSpace.y);
	vec2 refractionTexCoords = vec2(normalizedDeviceSpace.x, normalizedDeviceSpace.y);
	
	vec2 distortion1 = (texture(dudvTexture, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 2.0 -1.0) * waveStrength;
	vec2 distortion2 = (texture(dudvTexture, vec2(-textureCoords.x + moveFactor, textureCoords.y + moveFactor)).rg * 2.0 -1.0) * waveStrength;
	vec2 totalDistortion = distortion1 + distortion2;
	reflectionTexCoords += totalDistortion;
	refractionTexCoords += totalDistortion;
	refractionTexCoords = clamp(refractionTexCoords, 0.001, 0.999);
	reflectionTexCoords.x = clamp(reflectionTexCoords.x, 0.001, 0.999);
	reflectionTexCoords.y = clamp(reflectionTexCoords.y, -0.999, -0.001);
	
	vec4 reflectionColor= texture(reflectionTexture, reflectionTexCoords);
	vec4 refractionColor= texture(refractionTexture, refractionTexCoords);
	
	out_Color = mix(reflectionColor, refractionColor, 0.5);
	out_Color = mix(out_Color, vec4(0.0,0.3,0.5,1.0), 0.2);

}