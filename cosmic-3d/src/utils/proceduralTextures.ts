import * as THREE from 'three';

/**
 * Procedural texture and cosmic environment generator for Karma Sutra™.
 * Uses HTML5 2D Canvas to generate high-resolution, photorealistic procedural textures
 * and creates multi-layer starfields, subtle cosmic dust nebulae, and distant galaxies.
 */

// Helper to create an offscreen canvas
function createOffscreenCanvas(width: number, height: number): { canvas: HTMLCanvasElement; ctx: CanvasRenderingContext2D } {
  const canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext('2d', { willReadFrequently: true })!;
  return { canvas, ctx };
}

// Pseudo-random deterministic noise generator
function pseudoNoise(x: number, y: number, seed: number = 42): number {
  const n = Math.sin(x * 12.9898 + y * 78.233 + seed) * 43758.5453;
  return n - Math.floor(n);
}

// Multi-octave fractional Brownian motion
function fbm(x: number, y: number, octaves: number = 4): number {
  let value = 0;
  let amplitude = 0.5;
  let frequency = 1.0;
  for (let i = 0; i < octaves; i++) {
    value += amplitude * (pseudoNoise(x * frequency, y * frequency, i * 19.7) * 2.0 - 1.0);
    frequency *= 2.0;
    amplitude *= 0.5;
  }
  return (value + 1.0) * 0.5;
}

// Helper to create a circular particle texture
function createParticleTexture(size: number = 64, innerAlpha: number = 1.0): THREE.CanvasTexture {
  const { canvas, ctx } = createOffscreenCanvas(size, size);
  const center = size / 2;
  const radGrad = ctx.createRadialGradient(center, center, 0, center, center, center);
  radGrad.addColorStop(0, `rgba(255, 255, 255, ${innerAlpha})`);
  radGrad.addColorStop(0.35, `rgba(255, 255, 255, ${innerAlpha * 0.5})`);
  radGrad.addColorStop(0.7, `rgba(255, 255, 255, ${innerAlpha * 0.12})`);
  radGrad.addColorStop(1, 'rgba(255, 255, 255, 0)');
  ctx.fillStyle = radGrad;
  ctx.fillRect(0, 0, size, size);

  const texture = new THREE.CanvasTexture(canvas);
  return texture;
}

/**
 * 1. SUN TEXTURE (Solar granulation, convection cells, fiery magnetic flares)
 */
export function generateSunTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const imgData = ctx.createImageData(size, size);
  const data = imgData.data;

  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      const u = x / size;
      const v = y / size;

      const n1 = fbm(u * 14, v * 14, 4);
      const n2 = fbm(u * 32 + 3.4, v * 32 + 1.2, 3);
      const n3 = fbm(u * 64, v * 64, 2);
      const val = n1 * 0.55 + n2 * 0.3 + n3 * 0.15;

      const index = (y * size + x) * 4;
      // Vibrant solar photosphere: rich gold to radiant amber and white-hot granules
      data[index] = Math.min(255, Math.floor(255 * (0.92 + val * 0.08)));      // R: 235 - 255
      data[index + 1] = Math.min(255, Math.floor(140 + val * 105));             // G: 140 - 245
      data[index + 2] = Math.min(255, Math.floor(20 + val * 55));               // B: 20 - 75
      data[index + 3] = 255;
    }
  }

  ctx.putImageData(imgData, 0, 0);

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  return texture;
}

/**
 * 2. VENUS TEXTURE (Thick sulfurous cream-yellow atmosphere with sweeping zonal winds)
 */
export function generateVenusTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const grad = ctx.createLinearGradient(0, 0, 0, size);
  grad.addColorStop(0, '#e8caa4');
  grad.addColorStop(0.2, '#d4a872');
  grad.addColorStop(0.5, '#f5deb3');
  grad.addColorStop(0.8, '#c99965');
  grad.addColorStop(1, '#e8caa4');
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, size, size);

  // Atmospheric aerodynamic chevron cloud bands
  ctx.globalAlpha = 0.22;
  for (let i = 0; i < 45; i++) {
    const y = (i / 45) * size;
    const h = 25 + Math.random() * 50;
    ctx.fillStyle = i % 2 === 0 ? '#fff3d6' : '#a3713f';
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.bezierCurveTo(size * 0.3, y + Math.sin(i * 0.8) * 35, size * 0.7, y - Math.cos(i * 0.8) * 35, size, y);
    ctx.lineTo(size, y + h);
    ctx.bezierCurveTo(size * 0.7, y + h - Math.cos(i * 0.8) * 35, size * 0.3, y + h + Math.sin(i * 0.8) * 35, 0, y + h);
    ctx.closePath();
    ctx.fill();
  }
  ctx.globalAlpha = 1.0;

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * 3. EARTH TEXTURE (Detailed continental landmasses, blue oceans, polar ice caps)
 */
export function generateEarthTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const imgData = ctx.createImageData(size, size);
  const data = imgData.data;

  for (let y = 0; y < size; y++) {
    const lat = (y / size) * Math.PI - Math.PI / 2; // -PI/2 to +PI/2
    const isPolar = Math.abs(lat) > 1.25;

    for (let x = 0; x < size; x++) {
      const u = x / size;
      const v = y / size;

      // Realistic continental macro-distribution with fbm noise
      const n1 = fbm(u * 5.0, v * 5.0, 5);
      const n2 = fbm(u * 12.0 + 1.7, v * 12.0 + 3.1, 3);
      const continentVal = n1 * 0.75 + n2 * 0.25;

      const isLand = continentVal > 0.49;
      const isMountain = continentVal > 0.63;
      const isArid = continentVal > 0.55 && Math.abs(lat) < 0.6 && Math.abs(lat) > 0.25;

      const index = (y * size + x) * 4;

      if (isPolar) {
        // Polar ice shelves
        const iceNoise = fbm(u * 20, v * 20, 2);
        data[index] = 238 + Math.floor(iceNoise * 15);
        data[index + 1] = 244 + Math.floor(iceNoise * 10);
        data[index + 2] = 252;
      } else if (isLand) {
        if (isMountain) {
          // Alpine peaks & highland brown
          data[index] = 135;
          data[index + 1] = 112;
          data[index + 2] = 88;
        } else if (isArid) {
          // Desert & savanna golden-tan
          data[index] = 185;
          data[index + 1] = 158;
          data[index + 2] = 105;
        } else {
          // Lush forest green
          data[index] = 38 + Math.floor(continentVal * 30);
          data[index + 1] = 108 + Math.floor(continentVal * 40);
          data[index + 2] = 52 + Math.floor(continentVal * 25);
        }
      } else {
        // Deep blue ocean with continental shelf turquoise shallowing
        const shallowFactor = Math.max(0, 1.0 - (0.49 - continentVal) * 15.0);
        data[index] = Math.floor(10 + shallowFactor * 22);
        data[index + 1] = Math.floor(45 + shallowFactor * 75);
        data[index + 2] = Math.floor(115 + shallowFactor * 55);
      }
      data[index + 3] = 255;
    }
  }

  ctx.putImageData(imgData, 0, 0);

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * EARTH CLOUD TEXTURE (Volumetric white swirling cloud patterns)
 */
export function generateEarthCloudTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const imgData = ctx.createImageData(size, size);
  const data = imgData.data;

  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      const u = x / size;
      const v = y / size;

      const n1 = fbm(u * 7.0 + 2.5, v * 7.0 + 1.2, 4);
      const n2 = fbm(u * 15.0, v * 15.0, 3);
      const cloudVal = n1 * 0.7 + n2 * 0.3;
      const density = Math.max(0, cloudVal - 0.48) * 2.3;

      const index = (y * size + x) * 4;
      data[index] = 255;
      data[index + 1] = 255;
      data[index + 2] = 255;
      data[index + 3] = Math.min(255, Math.floor(density * 255));
    }
  }

  ctx.putImageData(imgData, 0, 0);

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * 4. MARS TEXTURE (Rust-red iron oxide dunes, dark basalt impact craters, polar ice)
 */
export function generateMarsTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const imgData = ctx.createImageData(size, size);
  const data = imgData.data;

  for (let y = 0; y < size; y++) {
    const lat = (y / size) * Math.PI - Math.PI / 2;
    const isPolar = Math.abs(lat) > 1.34;

    for (let x = 0; x < size; x++) {
      const u = x / size;
      const v = y / size;

      const n1 = fbm(u * 7.0, v * 7.0, 4);
      const craterNoise = fbm(u * 28.0, v * 28.0, 2);

      const index = (y * size + x) * 4;

      if (isPolar) {
        data[index] = 242;
        data[index + 1] = 236;
        data[index + 2] = 236;
      } else {
        const r = 185 + n1 * 40 - craterNoise * 25;
        const g = 75 + n1 * 25;
        const b = 45 + n1 * 20;

        data[index] = Math.min(255, Math.max(0, Math.floor(r)));
        data[index + 1] = Math.min(255, Math.max(0, Math.floor(g)));
        data[index + 2] = Math.min(255, Math.max(0, Math.floor(b)));
      }
      data[index + 3] = 255;
    }
  }

  ctx.putImageData(imgData, 0, 0);

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * 5. JUPITER TEXTURE (Cream, amber, and caramel gas bands with Great Red Spot at -22° lat)
 */
export function generateJupiterTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  // Zonal atmospheric belts
  const beltColors = [
    '#f5e6d3', '#ddb892', '#b07d62', '#e6ccb2',
    '#7f4f24', '#f0dcbe', '#c08552', '#ede0d4',
    '#936639', '#d4a373', '#f7ede2', '#a67c52'
  ];

  for (let i = 0; i < size; i += 4) {
    const colorIdx = Math.floor((i / size) * beltColors.length);
    ctx.fillStyle = beltColors[colorIdx % beltColors.length];
    ctx.fillRect(0, i, size, 4);
  }

  // Turbulent shearing cyclonic waves
  ctx.globalAlpha = 0.32;
  for (let i = 0; i < 55; i++) {
    const y = Math.random() * size;
    ctx.fillStyle = i % 2 === 0 ? '#43281c' : '#ffffff';
    ctx.beginPath();
    ctx.moveTo(0, y);
    for (let x = 0; x <= size; x += 50) {
      ctx.lineTo(x, y + Math.sin(x * 0.04 + i) * 14);
    }
    ctx.lineTo(size, y + 22);
    ctx.lineTo(0, y + 22);
    ctx.closePath();
    ctx.fill();
  }

  // The Great Red Spot (Southern hemisphere ~62% down canvas)
  const grsX = size * 0.65;
  const grsY = size * 0.62;
  const grsW = 85;
  const grsH = 42;

  ctx.globalAlpha = 0.9;
  const grsGrad = ctx.createRadialGradient(grsX, grsY, 5, grsX, grsY, grsW);
  grsGrad.addColorStop(0, '#c33a18');
  grsGrad.addColorStop(0.65, '#922612');
  grsGrad.addColorStop(1, 'rgba(146, 38, 18, 0)');
  ctx.fillStyle = grsGrad;
  ctx.beginPath();
  ctx.ellipse(grsX, grsY, grsW, grsH, -0.06, 0, Math.PI * 2);
  ctx.fill();

  ctx.globalAlpha = 1.0;

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * 6. SATURN TEXTURE & RINGS
 */
export function generateSaturnTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const colors = [
    '#faecc9', '#ecd49c', '#dfbf66', '#f3deb1',
    '#d6b460', '#fcefd2', '#cfa750', '#f1dba6'
  ];

  for (let i = 0; i < size; i += 5) {
    const colorIdx = Math.floor((i / size) * colors.length);
    ctx.fillStyle = colors[colorIdx % colors.length];
    ctx.fillRect(0, i, size, 5);
  }

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

export function generateSaturnRingTexture(): THREE.CanvasTexture {
  const width = 1024;
  const height = 64;
  const { canvas, ctx } = createOffscreenCanvas(width, height);

  const imgData = ctx.createImageData(width, height);
  const data = imgData.data;

  for (let x = 0; x < width; x++) {
    const r = x / width; // 0 (inner) to 1 (outer)

    let alpha = 0;
    let brightness = 210;

    if (r > 0.04 && r < 0.22) {
      // C-ring (faint crepe ring)
      alpha = 0.25 * ((r - 0.04) / 0.18);
      brightness = 175;
    } else if (r >= 0.22 && r < 0.64) {
      // B-ring (brightest, densest ring)
      alpha = 0.88 + Math.sin(r * 180) * 0.08;
      brightness = 235;
    } else if (r >= 0.64 && r < 0.72) {
      // Cassini Division (prominent dark gap)
      alpha = 0.04;
      brightness = 50;
    } else if (r >= 0.72 && r < 0.94) {
      // A-ring
      alpha = 0.68 + Math.sin(r * 120) * 0.07;
      brightness = 208;
    } else if (r >= 0.94 && r < 0.98) {
      // Encke gap and F-ring
      alpha = 0.18;
      brightness = 185;
    }

    for (let y = 0; y < height; y++) {
      const idx = (y * width + x) * 4;
      data[idx] = Math.floor(brightness * 0.96);      // R: golden-beige
      data[idx + 1] = Math.floor(brightness * 0.88);  // G
      data[idx + 2] = Math.floor(brightness * 0.66);  // B
      data[idx + 3] = Math.floor(alpha * 255);        // A
    }
  }

  ctx.putImageData(imgData, 0, 0);

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.ClampToEdgeWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * 7. NEPTUNE TEXTURE (Deep azure methane gas giant with faint cloud bands)
 */
export function generateNeptuneTexture(): THREE.CanvasTexture {
  const size = 1024;
  const { canvas, ctx } = createOffscreenCanvas(size, size);

  const grad = ctx.createLinearGradient(0, 0, 0, size);
  grad.addColorStop(0, '#023e8a');
  grad.addColorStop(0.3, '#0077b6');
  grad.addColorStop(0.5, '#03045e');
  grad.addColorStop(0.7, '#0077b6');
  grad.addColorStop(1, '#023e8a');
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, size, size);

  // Faint white cirrus cloud bands
  ctx.globalAlpha = 0.22;
  for (let i = 0; i < 28; i++) {
    const y = Math.random() * size;
    ctx.fillStyle = '#caf0f8';
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.bezierCurveTo(size * 0.35, y + Math.sin(i) * 22, size * 0.65, y - Math.cos(i) * 22, size, y);
    ctx.lineTo(size, y + 12);
    ctx.lineTo(0, y + 12);
    ctx.closePath();
    ctx.fill();
  }
  ctx.globalAlpha = 1.0;

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.ClampToEdgeWrapping;
  return texture;
}

/**
 * 8. MULTI-LAYER DEEP SPACE STARFIELD
 * Layered celestial field with realistic stellar temperatures (O/B blue, A/F white, G yellow, K orange, M red).
 */
export function createMultiLayerStarfield(): THREE.Group {
  const group = new THREE.Group();
  const particleTexture = createParticleTexture(32, 1.0);

  // Layer 1: Deep field stars (4,200 distant stars, r: 500 to 1200)
  const deepGeo = new THREE.BufferGeometry();
  const deepCount = 4200;
  const deepPos = new Float32Array(deepCount * 3);
  const deepCols = new Float32Array(deepCount * 3);

  const stellarPalettes = [
    new THREE.Color('#ffffff'), // Class A/F white (40%)
    new THREE.Color('#ffffff'),
    new THREE.Color('#b8d5ff'), // Class O/B blue-white (25%)
    new THREE.Color('#d4e8ff'),
    new THREE.Color('#fff4cc'), // Class G yellow (20%)
    new THREE.Color('#ffd299'), // Class K orange (10%)
    new THREE.Color('#ffb380')  // Class M red dwarf (5%)
  ];

  for (let i = 0; i < deepCount; i++) {
    const r = 500 + Math.random() * 700;
    const theta = Math.random() * Math.PI * 2;
    const phi = Math.acos(2 * Math.random() - 1);

    deepPos[i * 3] = r * Math.sin(phi) * Math.cos(theta);
    deepPos[i * 3 + 1] = r * Math.sin(phi) * Math.sin(theta);
    deepPos[i * 3 + 2] = r * Math.cos(phi);

    const c = stellarPalettes[Math.floor(Math.random() * stellarPalettes.length)];
    deepCols[i * 3] = c.r;
    deepCols[i * 3 + 1] = c.g;
    deepCols[i * 3 + 2] = c.b;
  }

  deepGeo.setAttribute('position', new THREE.BufferAttribute(deepPos, 3));
  deepGeo.setAttribute('color', new THREE.BufferAttribute(deepCols, 3));

  const deepMat = new THREE.PointsMaterial({
    size: 1.4,
    vertexColors: true,
    map: particleTexture,
    transparent: true,
    opacity: 0.82,
    depthWrite: false,
    blending: THREE.AdditiveBlending
  });

  group.add(new THREE.Points(deepGeo, deepMat));

  // Layer 2: Midground stars (1,200 closer stars, r: 280 to 520)
  const midGeo = new THREE.BufferGeometry();
  const midCount = 1200;
  const midPos = new Float32Array(midCount * 3);
  const midCols = new Float32Array(midCount * 3);

  for (let i = 0; i < midCount; i++) {
    const r = 280 + Math.random() * 240;
    const theta = Math.random() * Math.PI * 2;
    const phi = Math.acos(2 * Math.random() - 1);

    midPos[i * 3] = r * Math.sin(phi) * Math.cos(theta);
    midPos[i * 3 + 1] = r * Math.sin(phi) * Math.sin(theta);
    midPos[i * 3 + 2] = r * Math.cos(phi);

    const c = stellarPalettes[Math.floor(Math.random() * stellarPalettes.length)];
    midCols[i * 3] = c.r;
    midCols[i * 3 + 1] = c.g;
    midCols[i * 3 + 2] = c.b;
  }

  midGeo.setAttribute('position', new THREE.BufferAttribute(midPos, 3));
  midGeo.setAttribute('color', new THREE.BufferAttribute(midCols, 3));

  const midMat = new THREE.PointsMaterial({
    size: 2.2,
    vertexColors: true,
    map: particleTexture,
    transparent: true,
    opacity: 0.9,
    depthWrite: false,
    blending: THREE.AdditiveBlending
  });

  group.add(new THREE.Points(midGeo, midMat));

  // Layer 3: Navigational celestial beacon stars (90 prominent stars)
  const beaconGeo = new THREE.BufferGeometry();
  const beaconCount = 90;
  const beaconPos = new Float32Array(beaconCount * 3);
  const beaconCols = new Float32Array(beaconCount * 3);

  for (let i = 0; i < beaconCount; i++) {
    const r = 320 + Math.random() * 450;
    const theta = Math.random() * Math.PI * 2;
    const phi = Math.acos(2 * Math.random() - 1);

    beaconPos[i * 3] = r * Math.sin(phi) * Math.cos(theta);
    beaconPos[i * 3 + 1] = r * Math.sin(phi) * Math.sin(theta);
    beaconPos[i * 3 + 2] = r * Math.cos(phi);

    const c = stellarPalettes[Math.floor(Math.random() * stellarPalettes.length)];
    beaconCols[i * 3] = c.r;
    beaconCols[i * 3 + 1] = c.g;
    beaconCols[i * 3 + 2] = c.b;
  }

  beaconGeo.setAttribute('position', new THREE.BufferAttribute(beaconPos, 3));
  beaconGeo.setAttribute('color', new THREE.BufferAttribute(beaconCols, 3));

  const beaconMat = new THREE.PointsMaterial({
    size: 3.8,
    vertexColors: true,
    map: particleTexture,
    transparent: true,
    opacity: 0.95,
    depthWrite: false,
    blending: THREE.AdditiveBlending
  });

  group.add(new THREE.Points(beaconGeo, beaconMat));

  return group;
}

/**
 * 9. SUBTLE COSMIC NEBULA DUST & DISTANT GALAXIES
 * Restrained, atmospheric celestial dust in muted deep obsidian, indigo, and warm antique bronze.
 */
export function createCosmicNebulaClouds(): THREE.Group {
  const group = new THREE.Group();

  // Create soft cloud sprite texture
  const { canvas, ctx } = createOffscreenCanvas(128, 128);
  const radGrad = ctx.createRadialGradient(64, 64, 4, 64, 64, 64);
  radGrad.addColorStop(0, 'rgba(255, 255, 255, 0.6)');
  radGrad.addColorStop(0.4, 'rgba(255, 255, 255, 0.25)');
  radGrad.addColorStop(0.8, 'rgba(255, 255, 255, 0.06)');
  radGrad.addColorStop(1, 'rgba(0, 0, 0, 0)');
  ctx.fillStyle = radGrad;
  ctx.fillRect(0, 0, 128, 128);

  const cloudTexture = new THREE.CanvasTexture(canvas);

  // Restrained celestial colors
  const nebulaColors = [
    new THREE.Color('#0b1021'), // Deep dark indigo
    new THREE.Color('#140d24'), // Faint starlight violet
    new THREE.Color('#191209'), // Warm celestial antique bronze
    new THREE.Color('#0a1622')  // Muted cosmic teal
  ];

  nebulaColors.forEach((color, idx) => {
    const particleCount = 45;
    const geo = new THREE.BufferGeometry();
    const pos = new Float32Array(particleCount * 3);

    // Position each cloud cluster in deep space far away
    const baseAngle = (idx / nebulaColors.length) * Math.PI * 2 + 0.4;
    const baseR = 650 + idx * 40;
    const baseX = Math.cos(baseAngle) * baseR;
    const baseZ = Math.sin(baseAngle) * baseR;
    const baseY = (idx % 2 === 0 ? 1 : -1) * (140 + idx * 30);

    for (let i = 0; i < particleCount; i++) {
      pos[i * 3] = baseX + (Math.random() - 0.5) * 220;
      pos[i * 3 + 1] = baseY + (Math.random() - 0.5) * 160;
      pos[i * 3 + 2] = baseZ + (Math.random() - 0.5) * 220;
    }

    geo.setAttribute('position', new THREE.BufferAttribute(pos, 3));

    const mat = new THREE.PointsMaterial({
      size: 140,
      color: color,
      map: cloudTexture,
      transparent: true,
      opacity: 0.08,
      depthWrite: false,
      blending: THREE.AdditiveBlending
    });

    group.add(new THREE.Points(geo, mat));
  });

  // Distant Spiral Galaxy Sprites (far background, depth ~850)
  const { canvas: galCanvas, ctx: galCtx } = createOffscreenCanvas(256, 128);
  const galGrad = galCtx.createRadialGradient(128, 64, 4, 128, 64, 120);
  galGrad.addColorStop(0, 'rgba(255, 245, 230, 0.85)');
  galGrad.addColorStop(0.25, 'rgba(200, 220, 255, 0.45)');
  galGrad.addColorStop(0.65, 'rgba(120, 150, 210, 0.15)');
  galGrad.addColorStop(1, 'rgba(0, 0, 0, 0)');
  galCtx.fillStyle = galGrad;
  galCtx.fillRect(0, 0, 256, 128);

  const galaxyTexture = new THREE.CanvasTexture(galCanvas);

  const galaxyPositions = [
    { pos: new THREE.Vector3(-680, 220, -540), scale: new THREE.Vector2(160, 80), rot: 0.45 },
    { pos: new THREE.Vector3(720, -180, -600), scale: new THREE.Vector2(140, 70), rot: -0.6 }
  ];

  galaxyPositions.forEach(gp => {
    const galMat = new THREE.SpriteMaterial({
      map: galaxyTexture,
      transparent: true,
      opacity: 0.35,
      blending: THREE.AdditiveBlending
    });
    const galSprite = new THREE.Sprite(galMat);
    galSprite.position.copy(gp.pos);
    galSprite.scale.set(gp.scale.x, gp.scale.y, 1);
    galSprite.material.rotation = gp.rot;
    group.add(galSprite);
  });

  return group;
}

// Backward compatibility export
export const createStarfield = (count: number = 3000) => {
  return createMultiLayerStarfield();
};
