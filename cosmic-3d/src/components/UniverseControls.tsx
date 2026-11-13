import React, { useState, useEffect, useRef } from 'react';
import { 
  RotateCcw, 
  Play, 
  Pause, 
  Volume2, 
  VolumeX, 
  FastForward,
  Compass
} from 'lucide-react';

interface UniverseControlsProps {
  onResetCamera: () => void;
  orbitSpeedMultiplier: number;
  setOrbitSpeedMultiplier: (speed: number) => void;
  showOrbits: boolean;
  setShowOrbits: (show: boolean) => void;
  showLabels: boolean;
  setShowLabels: (show: boolean) => void;
}

export const UniverseControls: React.FC<UniverseControlsProps> = ({
  onResetCamera,
  orbitSpeedMultiplier,
  setOrbitSpeedMultiplier,
  showOrbits,
  setShowOrbits
}) => {
  const [audioActive, setAudioActive] = useState(false);
  const audioCtxRef = useRef<AudioContext | null>(null);
  const oscNodesRef = useRef<OscillatorNode[]>([]);

  const toggleAudio = () => {
    if (!audioActive) {
      try {
        const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
        const ctx = new AudioContextClass();
        audioCtxRef.current = ctx;

        const freqs = [92.5, 138.6, 185.0, 277.2];
        const gainMaster = ctx.createGain();
        gainMaster.gain.setValueAtTime(0.04, ctx.currentTime);
        gainMaster.connect(ctx.destination);

        const nodes: OscillatorNode[] = [];
        freqs.forEach((f, idx) => {
          const osc = ctx.createOscillator();
          osc.type = idx % 2 === 0 ? 'sine' : 'triangle';
          osc.frequency.setValueAtTime(f, ctx.currentTime);

          const lfo = ctx.createOscillator();
          lfo.frequency.setValueAtTime(0.1 + idx * 0.05, ctx.currentTime);
          const lfoGain = ctx.createGain();
          lfoGain.gain.setValueAtTime(1.5, ctx.currentTime);
          lfo.connect(lfoGain);
          lfoGain.connect(osc.frequency);
          lfo.start();

          osc.connect(gainMaster);
          osc.start();
          nodes.push(osc);
        });

        oscNodesRef.current = nodes;
        setAudioActive(true);
      } catch (err) {
        console.warn('Audio Context initialization blocked by browser policy', err);
      }
    } else {
      if (audioCtxRef.current) {
        audioCtxRef.current.close();
        audioCtxRef.current = null;
      }
      oscNodesRef.current = [];
      setAudioActive(false);
    }
  };

  useEffect(() => {
    return () => {
      if (audioCtxRef.current) {
        audioCtxRef.current.close();
      }
    };
  }, []);

  return (
    <div className="universe-controls-capsule">
      {/* Reset Camera */}
      <button
        onClick={onResetCamera}
        className="ctrl-btn"
        title="Reset Camera to Solar System Overview"
      >
        <RotateCcw size={14} />
        <span>Overview</span>
      </button>

      <div className="ctrl-divider" />

      {/* Orbit Speed Controls */}
      <button
        onClick={() => setOrbitSpeedMultiplier(orbitSpeedMultiplier === 0 ? 1 : 0)}
        className={`ctrl-btn ${orbitSpeedMultiplier === 0 ? 'active' : ''}`}
        title={orbitSpeedMultiplier === 0 ? "Resume Planetary Orbit" : "Pause Planetary Orbit"}
      >
        {orbitSpeedMultiplier === 0 ? <Play size={14} /> : <Pause size={14} />}
      </button>

      <button
        onClick={() => setOrbitSpeedMultiplier(orbitSpeedMultiplier === 1 ? 2.5 : 1)}
        className={`ctrl-btn ${orbitSpeedMultiplier > 1 ? 'active' : ''}`}
        title="Toggle Warp Speed"
      >
        <FastForward size={14} />
      </button>

      <div className="ctrl-divider" />

      {/* Toggle Orbit Track Lines */}
      <button
        onClick={() => setShowOrbits(!showOrbits)}
        className={`ctrl-btn ${showOrbits ? 'active' : ''}`}
        title="Toggle Orbital Trajectory Lines"
      >
        <Compass size={14} />
      </button>

      <div className="ctrl-divider" />

      {/* Generative Celestial Audio */}
      <button
        onClick={toggleAudio}
        className={`ctrl-btn ${audioActive ? 'active' : ''}`}
        title={audioActive ? "Mute Celestial Drone" : "Enable Generative Cosmic Audio Drone"}
      >
        {audioActive ? <Volume2 size={14} /> : <VolumeX size={14} />}
        <span>
          {audioActive ? "Resonating" : "Drone"}
        </span>
      </button>
    </div>
  );
};
