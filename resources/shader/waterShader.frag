#version 400 core

in vec4 clipSpace;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;

void main(void) {

	vec2 normalizedDeviceSpace = (clipSpace.xy/clipSpace.w)/2.0 * 0.5;
	vec2 refractionTexCoords = normalizedDeviceSpace;
	vec2 reflectionTexCoords = vec(normalizedDeviceSpace.x, -normalizedDeviceSpace.y);
	vec4 reflectionColor= texture(reflectionTexture, reflectionTexCoords);
	vec4 refrectionColor= texture(refractionTexture, refractionTexCoords);
	out_Color = mix(reflectionColor, refractionColor, 0.5);

}