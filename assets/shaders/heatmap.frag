uniform sampler2D u_texture;
uniform float u_intensity;
uniform float u_alpha;
uniform float u_time;
uniform vec2 u_inv;
uniform vec4 u_outline;

varying vec2 v_texCoords;


// function code by deepseek
vec3 heatmapColor(float intensity) {
    intensity = clamp(intensity * u_intensity, 0.0, 1.0);

    vec3 color = vec3(0.0);
    if (intensity < 0.5) {
        color = mix(vec3(0.0, 0.0, 1.0), vec3(0.0, 1.0, 0.0), intensity * 2.0);
    } else {
        color = mix(vec3(0.0, 1.0, 0.0), vec3(1.0, 0.0, 0.0), (intensity - 0.5) * 2.0);
    }
    return color;
}

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    float damageAlpha = texColor.a;

    if (damageAlpha > 0.0) {
        vec2 detect = u_inv * 3.0;
        bool isEdge = false;

        vec2 offsets[4] = vec2[4](
        vec2(detect.x, 0.0),
        vec2(-detect.x, 0.0),
        vec2(0.0,  detect.y),
        vec2(0.0, -detect.y)
        );

        for (int i = 0; i < 4; i++) {
            vec2 sampleCoord = v_texCoords + offsets[i];
            if (texture2D(u_texture, sampleCoord).a < 0.01) {
                isEdge = true;
                break;
            }
        }

        if (isEdge) {
            gl_FragColor = vec4(u_outline.rgb, u_alpha * 1.5);
            return;
        }
    }

    vec3 heatColor = heatmapColor(damageAlpha);
    gl_FragColor = vec4(heatColor, step(0.0001, damageAlpha) * u_alpha);
}