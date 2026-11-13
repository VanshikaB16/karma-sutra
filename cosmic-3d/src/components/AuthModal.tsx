import React, { useState } from 'react';
import { Sparkles, Key, Mail, User, ShieldCheck, AlertCircle, ArrowRight, X } from 'lucide-react';
import { apiBridge, DevoteeSession } from '../services/apiBridge';

interface AuthModalProps {
  isOpen: boolean;
  initialMode?: 'login' | 'register';
  departureMessage?: string | null;
  onClose?: () => void;
  onLoginSuccess: (session: DevoteeSession) => void;
}

export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  initialMode = 'login',
  departureMessage,
  onClose,
  onLoginSuccess
}) => {
  const [mode, setMode] = useState<'login' | 'register'>(initialMode);
  const [identifier, setIdentifier] = useState('devotee1');
  const [password, setPassword] = useState('Devotee@108');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Sync mode when initialMode prop changes
  React.useEffect(() => {
    if (initialMode) {
      setMode(initialMode);
      setErrorMessage(null);
      if (initialMode === 'register') {
        setIdentifier('');
        setPassword('');
      } else {
        setIdentifier('devotee1');
        setPassword('Devotee@108');
      }
    }
  }, [initialMode, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setIsLoading(true);

    try {
      if (mode === 'login') {
        const res = await apiBridge.login(identifier.trim(), password);
        if (res.success && res.user) {
          onLoginSuccess(res.user);
        } else {
          setErrorMessage(res.error || 'Authentication denied by Celestial Gatekeeper.');
        }
      } else {
        // Registering a new soul in Akashic records
        if (!fullName.trim() || !identifier.trim() || !email.trim() || !password) {
          setErrorMessage('Please furnish all earthly identifiers (Full Name, Email, Username, Passphrase).');
          setIsLoading(false);
          return;
        }
        if (!email.includes('@') || !email.includes('.')) {
          setErrorMessage('Please enter a valid earthly email address.');
          setIsLoading(false);
          return;
        }
        if (password.length < 6) {
          setErrorMessage('Master Passphrase must be at least 6 characters.');
          setIsLoading(false);
          return;
        }

        const res = await apiBridge.register(fullName.trim(), email.trim(), identifier.trim(), password);
        if (res.success && res.user) {
          onLoginSuccess(res.user);
        } else {
          setErrorMessage(res.error || 'Registration failed.');
        }
      }
    } catch (err: any) {
      setErrorMessage(err.message || 'Transmission disrupted reaching gateway.');
    } finally {
      setIsLoading(false);
    }
  };

  const setPresetUser = (u: string, p: string) => {
    setIdentifier(u);
    setPassword(p);
  };

  return (
    <div className="modal-overlay" style={{ zIndex: 1200 }}>
      <div 
        className="modal-dialog"
        style={{
          maxWidth: '480px',
          width: '92vw',
          background: 'linear-gradient(180deg, #0e121e 0%, #060911 100%)',
          border: '1px solid rgba(201, 169, 110, 0.3)',
          boxShadow: '0 25px 60px rgba(0, 0, 0, 0.9), 0 0 35px rgba(0, 245, 212, 0.12)'
        }}
      >
        {/* Departure Banner */}
        {departureMessage && (
          <div style={{
            background: 'linear-gradient(90deg, rgba(239, 68, 68, 0.2) 0%, rgba(201, 169, 110, 0.2) 100%)',
            borderBottom: '1px solid rgba(239, 68, 68, 0.3)',
            padding: '10px 16px',
            fontSize: '12px',
            color: '#fef08a',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            <Sparkles size={14} style={{ color: '#ffd166' }} />
            <span>{departureMessage}</span>
          </div>
        )}

        {/* Modal Header */}
        <div className="modal-header" style={{ borderBottomColor: 'rgba(255, 255, 255, 0.08)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '18px', color: '#00f5d4' }}>✦</span>
            <div>
              <h2 className="modal-title" style={{ fontSize: '18px', color: '#f3f4f6' }}>
                {mode === 'login' ? 'Enter Celestial Realm' : 'Initialize Cosmic Record'}
              </h2>
              <p className="modal-subtitle">
                {mode === 'login' 
                  ? 'Identify your soul to inspect and petition the universe' 
                  : 'Register a new earthly devotee in the Akashic records'}
              </p>
            </div>
          </div>
          {onClose && (
            <button onClick={onClose} className="hud-close-btn">
              <X size={18} />
            </button>
          )}
        </div>

        {/* Modal Body */}
        <div className="modal-body" style={{ padding: '20px' }}>
          {/* Preset Devotee Shortcuts */}
          {mode === 'login' && (
            <div style={{
              background: 'rgba(255, 255, 255, 0.03)',
              borderRadius: '8px',
              border: '1px solid rgba(255, 255, 255, 0.06)',
              padding: '10px 12px',
              marginBottom: '16px'
            }}>
              <div style={{ fontSize: '10px', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '6px' }}>
                SANCTUARY DEVOTEE PROFILES (CLICK TO AUTOFILL):
              </div>
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                <button
                  type="button"
                  onClick={() => setPresetUser('devotee1', 'Devotee@108')}
                  style={{
                    background: identifier === 'devotee1' ? 'rgba(0, 245, 212, 0.2)' : 'rgba(255, 255, 255, 0.05)',
                    border: identifier === 'devotee1' ? '1px solid #00f5d4' : '1px solid rgba(255, 255, 255, 0.1)',
                    color: identifier === 'devotee1' ? '#00f5d4' : '#e5e7eb',
                    padding: '4px 10px',
                    borderRadius: '6px',
                    fontSize: '11px',
                    cursor: 'pointer'
                  }}
                >
                  Arjun Sharma (devotee1)
                </button>
                <button
                  type="button"
                  onClick={() => setPresetUser('admin', 'CosmicAdmin@108')}
                  style={{
                    background: identifier === 'admin' ? 'rgba(255, 209, 102, 0.2)' : 'rgba(255, 255, 255, 0.05)',
                    border: identifier === 'admin' ? '1px solid #ffd166' : '1px solid rgba(255, 255, 255, 0.1)',
                    color: identifier === 'admin' ? '#ffd166' : '#e5e7eb',
                    padding: '4px 10px',
                    borderRadius: '6px',
                    fontSize: '11px',
                    cursor: 'pointer'
                  }}
                >
                  Arch-Deity Admin (admin)
                </button>
              </div>
            </div>
          )}

          {errorMessage && (
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              padding: '10px 12px',
              borderRadius: '8px',
              background: 'rgba(239, 68, 68, 0.15)',
              border: '1px solid rgba(239, 68, 68, 0.35)',
              color: '#fca5a5',
              fontSize: '12px',
              marginBottom: '14px'
            }}>
              <AlertCircle size={15} />
              <span>{errorMessage}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            {mode === 'register' && (
              <>
                <div className="form-group">
                  <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <User size={12} style={{ color: '#00f5d4' }} />
                    <span>DEVOTEE FULL NAME</span>
                  </label>
                  <input
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="e.g. Arjun Sharma"
                    className="form-input"
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Mail size={12} style={{ color: '#00f5d4' }} />
                    <span>EARTHLY EMAIL ADDRESS</span>
                  </label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="devotee@earth.in"
                    className="form-input"
                    required
                  />
                </div>
              </>
            )}

            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <User size={12} style={{ color: '#00f5d4' }} />
                <span>EARTHLY USERNAME</span>
              </label>
              <input
                type="text"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                placeholder="devotee1 or admin"
                className="form-input"
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Key size={12} style={{ color: '#ffd166' }} />
                <span>MASTER PASSPHRASE</span>
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter master passphrase"
                className="form-input"
                required
              />
            </div>

            {/* Submit Button with Perfect Alignment */}
            <div style={{ marginTop: '8px' }}>
              <button
                type="submit"
                disabled={isLoading}
                className="btn-primary"
                style={{
                  width: '100%',
                  justifyContent: 'center',
                  padding: '12px',
                  fontSize: '14px',
                  fontWeight: 600
                }}
              >
                <span>
                  {isLoading
                    ? 'Entering Celestial Realm...'
                    : mode === 'login'
                    ? 'Sign In to Celestial Realm'
                    : 'Initialize Cosmic Record (108 Karma Included)'}
                </span>
                <ArrowRight size={15} />
              </button>
            </div>
          </form>

          {/* Toggle Login / Register */}
          <div style={{
            marginTop: '16px',
            textAlign: 'center',
            fontSize: '12px',
            color: '#9ca3af',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '8px'
          }}>
            <span>{mode === 'login' ? 'First time approaching the cosmos?' : 'Already recorded in heaven?'}</span>
            <button
              type="button"
              onClick={() => {
                setErrorMessage(null);
                if (mode === 'login') {
                  setMode('register');
                  setIdentifier('');
                  setPassword('');
                  setFullName('');
                  setEmail('');
                } else {
                  setMode('login');
                  setIdentifier('devotee1');
                  setPassword('Devotee@108');
                }
              }}
              style={{
                background: 'transparent',
                border: 'none',
                color: '#00f5d4',
                fontWeight: 600,
                cursor: 'pointer',
                textDecoration: 'underline'
              }}
            >
              {mode === 'login' ? 'Initialize Cosmic Record' : 'Return to Sign In'}
            </button>
          </div>

          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '6px',
            marginTop: '14px',
            fontSize: '10px',
            color: '#6b7280'
          }}>
            <ShieldCheck size={12} style={{ color: '#10b981' }} />
            <span>Connected to sovereign MySQL Akashic Ledger</span>
          </div>
        </div>
      </div>
    </div>
  );
};
