import * as THREE from 'three';

/**
 * Realistic Planetary Atmospheric Limb Scattering Shader.
 * Renders physically inspired limb glow on the front-facing silhouette of the planet
 * illuminated by the celestial light source, without harsh cartoon outlines.
 */

export const AtmosphereLimbShader = {
  vertexShader: `
    varying vec3 vNormal;
    varying vec3 vWorldPosition;
    varying vec3 vViewPosition;

    void main() {
      vNormal = normalize(normalMatrix * normal);
      vec4 worldPosition = modelMatrix * vec4(position, 1.0);
      vWorldPosition = worldPosition.xyz;
      vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
      vViewPosition = -mvPosition.xyz;
      gl_Position = projectionMatrix * mvPosition;
    }
  `,
  fragmentShader: `
    uniform vec3 glowColor;
    uniform vec3 lightPosition;
    uniform float intensity;
    uniform float power;
    varying vec3 vNormal;
    varying vec3 vWorldPosition;
    varying vec3 vViewPosition;

    void main() {
      vec3 viewDir = normalize(vViewPosition);
      vec3 normal = normalize(vNormal);

      // Fresnel rim factor (strongest at grazing angle to camera)
      float rim = 1.0 - max(dot(viewDir, normal), 0.0);
      float fresnel = pow(rim, power);

      // Light direction from Sun (assumed near origin or given lightPosition)
      vec3 lightDir = normalize(lightPosition - vWorldPosition);
      float sunDot = max(dot(normal, lightDir), 0.0);

      // Atmospheric scattering is most prominent where grazing angle meets solar illumination
      // with subtle twilight wrap-around
      float solarInfluence = clamp(sunDot + 0.35, 0.0, 1.0);
      float alpha = fresnel * intensity * solarInfluence;

      gl_FragColor = vec4(glowColor, alpha);
    }
  `
};

/**
 * Creates a subtle, realistic atmospheric limb scattering mesh for terrestrial planets.
 * Uses FrontSide rendering with very tight radius (1.012 - 1.018) to avoid hollow outline rings.
 */
export function createAtmosphereLimbMesh(
  radius: number,
  colorHex: string,
  intensity: number = 0.45,
  power: number = 3.8
): THREE.Mesh {
  const geometry = new THREE.SphereGeometry(radius * 1.015, 64, 48);
  const material = new THREE.ShaderMaterial({
    vertexShader: AtmosphereLimbShader.vertexShader,
    fragmentShader: AtmosphereLimbShader.fragmentShader,
    uniforms: {
      glowColor: { value: new THREE.Color(colorHex) },
      lightPosition: { value: new THREE.Vector3(0, 0, 0) },
      intensity: { value: intensity },
      power: { value: power }
    },
    blending: THREE.AdditiveBlending,
    side: THREE.FrontSide,
    transparent: true,
    depthWrite: false
  });

  return new THREE.Mesh(geometry, material);
}
