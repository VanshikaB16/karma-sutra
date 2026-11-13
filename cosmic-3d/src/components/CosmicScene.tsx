import React, { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { CelestialBody, CELESTIAL_DEPARTMENTS } from '../data/celestialDepartments';
import {
  generateSunTexture,
  generateVenusTexture,
  generateEarthTexture,
  generateEarthCloudTexture,
  generateMarsTexture,
  generateJupiterTexture,
  generateSaturnTexture,
  generateSaturnRingTexture,
  generateNeptuneTexture,
  createMultiLayerStarfield,
  createCosmicNebulaClouds
} from '../utils/proceduralTextures';
import { createAtmosphereLimbMesh } from '../shaders/atmosphereShader';

interface CosmicSceneProps {
  selectedBody: CelestialBody | null;
  onSelectBody: (body: CelestialBody | null) => void;
  orbitSpeedMultiplier: number;
  showOrbits: boolean;
  showLabels: boolean;
}

interface PlanetMeshNode {
  body: CelestialBody;
  pivot: THREE.Group;
  planetMesh: THREE.Mesh;
  cloudMesh?: THREE.Mesh;
  atmosphereMesh?: THREE.Mesh;
  hoverRing?: THREE.Mesh;
  orbitAngle: number;
}

export const CosmicScene: React.FC<CosmicSceneProps> = ({
  selectedBody,
  onSelectBody,
  orbitSpeedMultiplier,
  showOrbits
}) => {
  const mountRef = useRef<HTMLDivElement>(null);
  const [hoveredBody, setHoveredBody] = useState<CelestialBody | null>(null);

  const sceneRef = useRef<THREE.Scene | null>(null);
  const cameraRef = useRef<THREE.PerspectiveCamera | null>(null);
  const controlsRef = useRef<OrbitControls | null>(null);
  const planetsRef = useRef<PlanetMeshNode[]>([]);
  const orbitLinesRef = useRef<THREE.Line[]>([]);
  const sunTextureRef = useRef<THREE.Texture | null>(null);
  const sunCoronaRef = useRef<THREE.Sprite | null>(null);

  // Camera transition animation state
  const cameraAnimRef = useRef<{
    active: boolean;
    startPos: THREE.Vector3;
    endPos: THREE.Vector3;
    startTarget: THREE.Vector3;
    endTarget: THREE.Vector3;
    startTime: number;
    duration: number;
  } | null>(null);

  useEffect(() => {
    const container = mountRef.current;
    if (!container) return;

    const width = container.clientWidth || window.innerWidth;
    const height = container.clientHeight || window.innerHeight;

    // 1. SCENE
    const scene = new THREE.Scene();
    sceneRef.current = scene;
    scene.background = new THREE.Color('#05060b');

    // 2. CAMERA
    const camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 2000);
    camera.position.set(0, 48, 115);
    cameraRef.current = camera;

    // 3. RENDERER
    const renderer = new THREE.WebGLRenderer({
      antialias: true,
      powerPreference: 'high-performance'
    });
    renderer.setSize(width, height);
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.15;
    renderer.outputColorSpace = THREE.SRGBColorSpace;
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    container.appendChild(renderer.domElement);

    // 4. ORBIT CONTROLS
    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controls.minDistance = 5;
    controls.maxDistance = 450;
    controls.maxPolarAngle = Math.PI / 2 + 0.18;
    controlsRef.current = controls;

    // 5. LIGHTING ARCHITECTURE
    // Primary PointLight from the radiant Sun at origin
    const sunLight = new THREE.PointLight(0xfff7e8, 4.4, 550, 0.42);
    sunLight.position.set(0, 0, 0);
    sunLight.castShadow = true;
    sunLight.shadow.mapSize.width = 1024;
    sunLight.shadow.mapSize.height = 1024;
    scene.add(sunLight);

    // Balanced celestial ambient fill (prevents dark silhouettes, reveals realistic night-side terrain)
    const ambientLight = new THREE.AmbientLight(0x8ea5c8, 0.68);
    scene.add(ambientLight);

    // Hemisphere light for natural gradient shading from cosmic zenith
    const hemiLight = new THREE.HemisphereLight(0xa5c4f2, 0x161d2d, 0.38);
    scene.add(hemiLight);

    // Soft camera-aligned celestial starlight fill
    const camLight = new THREE.DirectionalLight(0xdde8fa, 0.42);
    camLight.position.set(45, 65, 80);
    scene.add(camLight);

    // 6. MULTI-LAYER STARFIELD & COSMIC NEBULA DUST
    const starfield = createMultiLayerStarfield();
    scene.add(starfield);

    const nebulaDust = createCosmicNebulaClouds();
    scene.add(nebulaDust);

    // Helper: Texture loader that progressively enhances procedural textures with high-res NASA photography
    const textureLoader = new THREE.TextureLoader();
    const resolveTextureUrl = (path: string) => path.startsWith('/') ? '.' + path : path;
    const loadProgressiveTexture = (url: string, proceduralFallback: THREE.CanvasTexture, onLoaded?: (tex: THREE.Texture) => void): THREE.Texture => {
      textureLoader.load(
        resolveTextureUrl(url),
        (loadedTex) => {
          loadedTex.colorSpace = THREE.SRGBColorSpace;
          loadedTex.wrapS = THREE.RepeatWrapping;
          loadedTex.wrapT = url.includes('sun') ? THREE.RepeatWrapping : THREE.ClampToEdgeWrapping;
          loadedTex.needsUpdate = true;
          if (onLoaded) onLoaded(loadedTex);
        },
        undefined,
        () => {
          // Retain proceduralFallback seamlessly
        }
      );
      return proceduralFallback;
    };

    // 7. BUILD CELESTIAL BODIES
    const planetNodes: PlanetMeshNode[] = [];
    const orbitLines: THREE.Line[] = [];

    CELESTIAL_DEPARTMENTS.forEach((body) => {
      const pivot = new THREE.Group();
      scene.add(pivot);

      let texture: THREE.Texture;
      let roughness = 0.75;
      let metalness = 0.05;

      const sphereGeo = new THREE.SphereGeometry(body.radius, 64, 48);

      let material: THREE.Material;
      if (body.bodyType === 'star') {
        const baseTex = generateSunTexture();
        sunTextureRef.current = baseTex;
        const basicMat = new THREE.MeshBasicMaterial({ map: baseTex });
        loadProgressiveTexture('/textures/sun.jpg', baseTex, (loadedTex) => {
          basicMat.map = loadedTex;
          basicMat.needsUpdate = true;
          sunTextureRef.current = loadedTex;
        });
        material = basicMat;
      } else {
        switch (body.textureType) {
          case 'venus': {
            roughness = 0.82;
            metalness = 0.02;
            break;
          }
          case 'earth': {
            roughness = 0.52; // Oceans reflect realistic specular solar highlights
            metalness = 0.08;
            break;
          }
          case 'mars': {
            roughness = 0.85;
            metalness = 0.0;
            break;
          }
          case 'jupiter': {
            roughness = 0.68;
            metalness = 0.04;
            break;
          }
          case 'saturn': {
            roughness = 0.72;
            metalness = 0.04;
            break;
          }
          case 'neptune': {
            roughness = 0.58;
            metalness = 0.06;
            break;
          }
        }

        let baseTex: THREE.CanvasTexture;
        let imgUrl = `/textures/${body.textureType}.jpg`;
        switch (body.textureType) {
          case 'venus': baseTex = generateVenusTexture(); break;
          case 'earth': baseTex = generateEarthTexture(); break;
          case 'mars': baseTex = generateMarsTexture(); break;
          case 'jupiter': baseTex = generateJupiterTexture(); break;
          case 'saturn': baseTex = generateSaturnTexture(); break;
          case 'neptune': baseTex = generateNeptuneTexture(); break;
          default: baseTex = generateEarthTexture(); break;
        }

        const stdMat = new THREE.MeshStandardMaterial({
          map: baseTex,
          roughness: roughness,
          metalness: metalness
        });

        loadProgressiveTexture(imgUrl, baseTex, (loadedTex) => {
          stdMat.map = loadedTex;
          stdMat.needsUpdate = true;
        });

        // Add Mars topographical bump mapping if available
        if (body.id === 'mars') {
          textureLoader.load(resolveTextureUrl('/textures/mars-bump.jpg'), (bumpTex) => {
            bumpTex.wrapS = THREE.RepeatWrapping;
            bumpTex.wrapT = THREE.ClampToEdgeWrapping;
            stdMat.bumpMap = bumpTex;
            stdMat.bumpScale = 0.035;
            stdMat.needsUpdate = true;
          });
        }

        material = stdMat;
      }

      const planetMesh = new THREE.Mesh(sphereGeo, material);
      planetMesh.castShadow = body.bodyType !== 'star';
      planetMesh.receiveShadow = body.bodyType !== 'star';
      planetMesh.rotation.z = body.axialTilt;
      planetMesh.userData = { bodyId: body.id, bodyData: body };

      const initialAngle = Math.random() * Math.PI * 2;
      planetMesh.position.set(
        Math.cos(initialAngle) * body.orbitRadius,
        0,
        Math.sin(initialAngle) * body.orbitRadius
      );
      pivot.add(planetMesh);

      // SUN CORONA & RADIANCE
      if (body.bodyType === 'star') {
        const coronaCanvas = document.createElement('canvas');
        coronaCanvas.width = 256;
        coronaCanvas.height = 256;
        const cctx = coronaCanvas.getContext('2d')!;
        const cgrad = cctx.createRadialGradient(128, 128, 18, 128, 128, 128);
        cgrad.addColorStop(0, 'rgba(255, 220, 90, 0.95)');
        cgrad.addColorStop(0.2, 'rgba(255, 160, 20, 0.6)');
        cgrad.addColorStop(0.5, 'rgba(255, 90, 0, 0.22)');
        cgrad.addColorStop(0.8, 'rgba(255, 40, 0, 0.05)');
        cgrad.addColorStop(1, 'rgba(0, 0, 0, 0)');
        cctx.fillStyle = cgrad;
        cctx.fillRect(0, 0, 256, 256);

        const coronaTexture = new THREE.CanvasTexture(coronaCanvas);
        const coronaMaterial = new THREE.SpriteMaterial({
          map: coronaTexture,
          blending: THREE.AdditiveBlending,
          transparent: true,
          depthWrite: false
        });
        const coronaSprite = new THREE.Sprite(coronaMaterial);
        coronaSprite.scale.set(body.radius * 3.4, body.radius * 3.4, 1);
        planetMesh.add(coronaSprite);
        sunCoronaRef.current = coronaSprite;
      }

      // EARTH VOLUMETRIC CLOUD LAYER
      let cloudMesh: THREE.Mesh | undefined;
      if (body.hasClouds) {
        const cloudGeo = new THREE.SphereGeometry(body.radius * 1.018, 64, 48);
        const baseCloudTex = generateEarthCloudTexture();
        const cloudMat = new THREE.MeshStandardMaterial({
          map: baseCloudTex,
          transparent: true,
          opacity: 0.68,
          blending: THREE.NormalBlending,
          roughness: 0.95
        });
        loadProgressiveTexture('/textures/earth-clouds.jpg', baseCloudTex, (loadedCloudTex) => {
          cloudMat.map = loadedCloudTex;
          cloudMat.needsUpdate = true;
        });

        cloudMesh = new THREE.Mesh(cloudGeo, cloudMat);
        cloudMesh.castShadow = true;
        planetMesh.add(cloudMesh);
      }

      // VENUS DENSE ATMOSPHERIC CLOUD ENVELOPE
      if (body.id === 'venus') {
        const vCloudGeo = new THREE.SphereGeometry(body.radius * 1.012, 64, 48);
        const vCloudTex = generateVenusTexture();
        const vCloudMat = new THREE.MeshStandardMaterial({
          map: vCloudTex,
          transparent: true,
          opacity: 0.45,
          roughness: 0.8
        });
        loadProgressiveTexture('/textures/venus-clouds.jpg', vCloudTex, (loadedVCloudTex) => {
          vCloudMat.map = loadedVCloudTex;
          vCloudMat.needsUpdate = true;
        });

        const vCloudMesh = new THREE.Mesh(vCloudGeo, vCloudMat);
        planetMesh.add(vCloudMesh);
      }

      // REALISTIC ATMOSPHERIC LIMB GLOW (EARTH & VENUS ONLY — NO CARTOON NEON OUTLINES)
      let atmosphereMesh: THREE.Mesh | undefined;
      if (body.id === 'earth') {
        atmosphereMesh = createAtmosphereLimbMesh(body.radius, '#38bdf8', 0.42, 4.2);
        planetMesh.add(atmosphereMesh);
      } else if (body.id === 'venus') {
        atmosphereMesh = createAtmosphereLimbMesh(body.radius, '#fed7aa', 0.28, 4.0);
        planetMesh.add(atmosphereMesh);
      }

      // SATURN RINGS WITH PHOTOMETRIC RESPONSE & CASSINI DIVISION
      if (body.hasRings && body.ringInnerRadius && body.ringOuterRadius) {
        const ringGeo = new THREE.RingGeometry(body.ringInnerRadius, body.ringOuterRadius, 128);
        const pos = ringGeo.attributes.position;
        const v3 = new THREE.Vector3();
        for (let i = 0; i < pos.count; i++) {
          v3.fromBufferAttribute(pos, i);
          const rDist = v3.length();
          const u = (rDist - body.ringInnerRadius) / (body.ringOuterRadius - body.ringInnerRadius);
          ringGeo.attributes.uv.setXY(i, u, 0.5);
        }

        const baseRingTex = generateSaturnRingTexture();
        const ringMat = new THREE.MeshStandardMaterial({
          map: baseRingTex,
          side: THREE.DoubleSide,
          transparent: true,
          opacity: 0.94,
          roughness: 0.62,
          metalness: 0.05
        });

        loadProgressiveTexture('/textures/saturn-ring.png', baseRingTex, (loadedRingTex) => {
          loadedRingTex.wrapS = THREE.ClampToEdgeWrapping;
          loadedRingTex.wrapT = THREE.ClampToEdgeWrapping;
          ringMat.map = loadedRingTex;
          ringMat.needsUpdate = true;
        });

        const ringMesh = new THREE.Mesh(ringGeo, ringMat);
        ringMesh.rotation.x = Math.PI / 2;
        ringMesh.castShadow = true;
        ringMesh.receiveShadow = true;
        planetMesh.add(ringMesh);
      }

      // MINIMAL CELESTIAL HOVER RETICLE
      const hoverRingGeo = new THREE.RingGeometry(body.radius * 1.25, body.radius * 1.32, 64);
      const hoverRingMat = new THREE.MeshBasicMaterial({
        color: new THREE.Color('#d4af37'), // Muted celestial antique gold
        side: THREE.DoubleSide,
        transparent: true,
        opacity: 0
      });
      const hoverRing = new THREE.Mesh(hoverRingGeo, hoverRingMat);
      hoverRing.rotation.x = Math.PI / 2;
      planetMesh.add(hoverRing);

      // ORBITAL ELLIPSE TRACK LINE
      if (body.orbitRadius > 0) {
        const orbitCurve = new THREE.EllipseCurve(
          0, 0,
          body.orbitRadius, body.orbitRadius,
          0, 2 * Math.PI,
          false,
          0
        );
        const points = orbitCurve.getPoints(160);
        const orbitGeo = new THREE.BufferGeometry().setFromPoints(
          points.map(p => new THREE.Vector3(p.x, 0, p.y))
        );
        const orbitMat = new THREE.LineBasicMaterial({
          color: new THREE.Color('#4a5568'),
          transparent: true,
          opacity: 0.22
        });
        const orbitLine = new THREE.Line(orbitGeo, orbitMat);
        scene.add(orbitLine);
        orbitLines.push(orbitLine);
      }

      planetNodes.push({
        body,
        pivot,
        planetMesh,
        cloudMesh,
        atmosphereMesh,
        hoverRing,
        orbitAngle: initialAngle
      });
    });

    planetsRef.current = planetNodes;
    orbitLinesRef.current = orbitLines;

    // 8. RAYCASTING INTERACTION
    const raycaster = new THREE.Raycaster();
    const mouse = new THREE.Vector2();

    const onPointerMove = (e: MouseEvent) => {
      const rect = container.getBoundingClientRect();
      mouse.x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
      mouse.y = -((e.clientY - rect.top) / rect.height) * 2 + 1;

      raycaster.setFromCamera(mouse, camera);
      const meshesToTest = planetNodes.map(n => n.planetMesh);
      const intersects = raycaster.intersectObjects(meshesToTest, false);

      if (intersects.length > 0) {
        const hitBody = intersects[0].object.userData.bodyData as CelestialBody;
        setHoveredBody(hitBody);
        container.style.cursor = 'pointer';

        planetNodes.forEach(n => {
          if (n.hoverRing) {
            (n.hoverRing.material as THREE.MeshBasicMaterial).opacity = n.body.id === hitBody.id ? 0.8 : 0;
          }
        });
      } else {
        setHoveredBody(null);
        container.style.cursor = 'default';
        planetNodes.forEach(n => {
          if (n.hoverRing) {
            (n.hoverRing.material as THREE.MeshBasicMaterial).opacity = 0;
          }
        });
      }
    };

    const onPointerClick = (e: MouseEvent) => {
      const rect = container.getBoundingClientRect();
      mouse.x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
      mouse.y = -((e.clientY - rect.top) / rect.height) * 2 + 1;

      raycaster.setFromCamera(mouse, camera);
      const meshesToTest = planetNodes.map(n => n.planetMesh);
      const intersects = raycaster.intersectObjects(meshesToTest, false);

      if (intersects.length > 0) {
        const hitBody = intersects[0].object.userData.bodyData as CelestialBody;
        onSelectBody(hitBody);
      }
    };

    container.addEventListener('mousemove', onPointerMove);
    container.addEventListener('click', onPointerClick);

    // 9. ANIMATION LOOP
    let animationFrameId: number;

    const animate = () => {
      animationFrameId = requestAnimationFrame(animate);

      const time = performance.now() * 0.001;

      // Animate active solar granulation and coronal breathing
      if (sunTextureRef.current) {
        sunTextureRef.current.offset.x = (sunTextureRef.current.offset.x + 0.00025) % 1.0;
        sunTextureRef.current.offset.y = (sunTextureRef.current.offset.y + 0.0001) % 1.0;
      }
      if (sunCoronaRef.current) {
        const pulse = 1.0 + 0.035 * Math.sin(time * 2.0);
        sunCoronaRef.current.scale.set(4.8 * 3.4 * pulse, 4.8 * 3.4 * pulse, 1);
      }

      // Rotate and orbit planets
      planetNodes.forEach(node => {
        node.planetMesh.rotation.y += node.body.rotationSpeed;

        if (node.cloudMesh && node.body.cloudSpeed) {
          node.cloudMesh.rotation.y += node.body.cloudSpeed;
        }

        if (node.body.orbitRadius > 0) {
          node.orbitAngle += node.body.orbitSpeed * 0.35 * orbitSpeedMultiplier;
          node.planetMesh.position.x = Math.cos(node.orbitAngle) * node.body.orbitRadius;
          node.planetMesh.position.z = Math.sin(node.orbitAngle) * node.body.orbitRadius;
        }
      });

      // Smooth Camera Flight Transitions
      if (cameraAnimRef.current && cameraAnimRef.current.active) {
        const anim = cameraAnimRef.current;
        const elapsed = (performance.now() - anim.startTime) / anim.duration;

        if (elapsed >= 1.0) {
          camera.position.copy(anim.endPos);
          controls.target.copy(anim.endTarget);
          anim.active = false;
        } else {
          const t = elapsed < 0.5 ? 4 * elapsed * elapsed * elapsed : 1 - Math.pow(-2 * elapsed + 2, 3) / 2;
          camera.position.lerpVectors(anim.startPos, anim.endPos, t);
          controls.target.lerpVectors(anim.startTarget, anim.endTarget, t);
        }
      } else if (selectedBody) {
        const activeNode = planetNodes.find(n => n.body.id === selectedBody.id);
        if (activeNode) {
          const worldPos = new THREE.Vector3();
          activeNode.planetMesh.getWorldPosition(worldPos);
          controls.target.lerp(worldPos, 0.08);
        }
      }

      controls.update();
      renderer.render(scene, camera);
    };

    animate();

    const handleResize = () => {
      if (!container) return;
      const w = container.clientWidth || window.innerWidth;
      const h = container.clientHeight || window.innerHeight;
      camera.aspect = w / h;
      camera.updateProjectionMatrix();
      renderer.setSize(w, h);
    };

    window.addEventListener('resize', handleResize);

    return () => {
      cancelAnimationFrame(animationFrameId);
      window.removeEventListener('resize', handleResize);
      container.removeEventListener('mousemove', onPointerMove);
      container.removeEventListener('click', onPointerClick);
      if (renderer.domElement.parentNode) {
        renderer.domElement.parentNode.removeChild(renderer.domElement);
      }
      renderer.dispose();
    };
  }, []);

  useEffect(() => {
    orbitLinesRef.current.forEach(line => {
      line.visible = showOrbits;
    });
  }, [showOrbits]);

  useEffect(() => {
    const camera = cameraRef.current;
    const controls = controlsRef.current;
    if (!camera || !controls) return;

    if (selectedBody) {
      const node = planetsRef.current.find(n => n.body.id === selectedBody.id);
      if (node) {
        const worldPos = new THREE.Vector3();
        node.planetMesh.getWorldPosition(worldPos);

        const dist = selectedBody.radius * 3.4 + (selectedBody.hasRings ? 4.5 : 2.5);
        const targetCamPos = new THREE.Vector3(
          worldPos.x + dist * 0.7,
          worldPos.y + dist * 0.45,
          worldPos.z + dist * 0.9
        );

        cameraAnimRef.current = {
          active: true,
          startPos: camera.position.clone(),
          endPos: targetCamPos,
          startTarget: controls.target.clone(),
          endTarget: worldPos.clone(),
          startTime: performance.now(),
          duration: 1600
        };
      }
    } else {
      cameraAnimRef.current = {
        active: true,
        startPos: camera.position.clone(),
        endPos: new THREE.Vector3(0, 48, 115),
        startTarget: controls.target.clone(),
        endTarget: new THREE.Vector3(0, 0, 0),
        startTime: performance.now(),
        duration: 1500
      };
    }
  }, [selectedBody]);

  return (
    <div className="cosmic-canvas-container" ref={mountRef}>
      {hoveredBody && !selectedBody && (
        <div className="hover-tooltip">
          <span style={{ fontWeight: 700, color: '#f5f0e8' }}>{hoveredBody.name.toUpperCase()}</span>
          <span style={{ color: '#8b8f9e' }}>•</span>
          <span style={{ color: '#d4af37' }}>{hoveredBody.departmentName}</span>
          <span style={{ color: '#8b8f9e', fontSize: '10px' }}>(Click to Enter Orbit)</span>
        </div>
      )}
    </div>
  );
};
