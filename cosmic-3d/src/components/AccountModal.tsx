import React, { useState, useEffect } from 'react';
import { 
  X, User as UserIcon, Sparkles, Crown, Settings as SettingsIcon, 
  Key, Shield, CheckCircle2, AlertCircle, RefreshCw, ArrowUpRight, Lock 
} from 'lucide-react';
import { apiBridge, DevoteeSession, DevoteeProfile, KarmaLedgerEntry } from '../services/apiBridge';

export type AccountTab = 'profile' | 'karma' | 'subscription' | 'settings' | 'password';

interface AccountModalProps {
  isOpen: boolean;
  initialTab?: AccountTab;
  onClose: () => void;
  session: DevoteeSession | null;
  onSessionUpdated: (updatedSession: DevoteeSession) => void;
}

export const AccountModal: React.FC<AccountModalProps> = ({
  isOpen,
  initialTab = 'profile',
  onClose,
  session,
  onSessionUpdated
}) => {
  const [activeTab, setActiveTab] = useState<AccountTab>(initialTab);
  const [profile, setProfile] = useState<DevoteeProfile | null>(null);
  const [karmaLedger, setKarmaLedger] = useState<KarmaLedgerEntry[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // Change password form state
  const [currentPass, setCurrentPass] = useState('');
  const [newPass, setNewPass] = useState('');
  const [confirmPass, setConfirmPass] = useState('');
  const [passStatus, setPassStatus] = useState<{ type: 'idle' | 'loading' | 'success' | 'error'; message?: string }>({ type: 'idle' });

  // Account preferences state
  const [soundEnabled, setSoundEnabled] = useState(true);
  const [hudGlowEnabled, setHudGlowEnabled] = useState(true);
  const [autoRotate, setAutoRotate] = useState(true);

  useEffect(() => {
    if (isOpen) {
      setActiveTab(initialTab);
      loadAccountData();
    }
  }, [isOpen, initialTab]);

  const loadAccountData = async () => {
    setIsLoading(true);
    try {
      const [profData, ledgerData] = await Promise.all([
        apiBridge.getProfile(),
        apiBridge.getKarmaLedger()
      ]);
      if (profData) setProfile(profData);
      if (ledgerData) setKarmaLedger(ledgerData);
    } catch (err) {
      console.warn('Failed loading account details', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentPass || !newPass) {
      setPassStatus({ type: 'error', message: 'All passphrase fields are required.' });
      return;
    }
    if (newPass !== confirmPass) {
      setPassStatus({ type: 'error', message: 'New Master Passphrase and Confirmation do not match.' });
      return;
    }
    if (newPass.length < 6) {
      setPassStatus({ type: 'error', message: 'New Passphrase must contain at least 6 characters.' });
      return;
    }

    setPassStatus({ type: 'loading' });
    try {
      const res = await apiBridge.changePassword(currentPass, newPass);
      if (res.success) {
        setPassStatus({ type: 'success', message: res.message || 'Master Passphrase securely updated!' });
        setCurrentPass('');
        setNewPass('');
        setConfirmPass('');
      } else {
        setPassStatus({ type: 'error', message: res.error || 'Passphrase change rejected by Celestial Gatekeeper.' });
      }
    } catch (err: any) {
      setPassStatus({ type: 'error', message: err.message || 'Network failure reaching authorization gate.' });
    }
  };

  if (!isOpen || !session) return null;

  return (
    <div className="modal-overlay" style={{ zIndex: 1050 }}>
      <div 
        className="modal-dialog"
        style={{ 
          maxWidth: '680px',
          width: '92vw',
          maxHeight: '88vh',
          display: 'flex',
          flexDirection: 'column',
          background: 'linear-gradient(180deg, #0e1320 0%, #080b13 100%)',
          border: '1px solid rgba(201, 169, 110, 0.25)',
          boxShadow: '0 25px 60px rgba(0, 0, 0, 0.85), 0 0 35px rgba(201, 169, 110, 0.12)'
        }}
      >
        {/* Modal Header */}
        <div className="modal-header" style={{ borderBottomColor: 'rgba(201, 169, 110, 0.2)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{
              width: '36px',
              height: '36px',
              borderRadius: '10px',
              background: 'linear-gradient(135deg, rgba(201, 169, 110, 0.2) 0%, rgba(0, 245, 212, 0.15) 100%)',
              border: '1px solid rgba(201, 169, 110, 0.4)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffd166'
            }}>
              <Shield size={20} />
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <h2 className="modal-title" style={{ fontSize: '18px', color: '#f3f4f6' }}>
                  Devotee Sanctuary & Account Management
                </h2>
                <span style={{
                  fontSize: '10px',
                  fontFamily: 'var(--font-mono)',
                  background: 'rgba(201, 169, 110, 0.15)',
                  color: '#ffd166',
                  padding: '2px 8px',
                  borderRadius: '12px',
                  border: '1px solid rgba(201, 169, 110, 0.3)'
                }}>
                  {session.tier}
                </span>
              </div>
              <p className="modal-subtitle">
                Official Celestial Dossier & Sovereign Record for {session.fullName} (@{session.username})
              </p>
            </div>
          </div>

          <button onClick={onClose} className="hud-close-btn">
            <X size={18} />
          </button>
        </div>

        {/* Modal Navigation Tabs */}
        <div style={{
          display: 'flex',
          gap: '4px',
          padding: '10px 18px 0',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          background: 'rgba(8, 12, 22, 0.6)',
          overflowX: 'auto'
        }}>
          <button
            onClick={() => setActiveTab('profile')}
            className={`tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
            style={{
              padding: '8px 14px',
              background: activeTab === 'profile' ? 'rgba(201, 169, 110, 0.18)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'profile' ? '2px solid #ffd166' : '2px solid transparent',
              color: activeTab === 'profile' ? '#ffd166' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer',
              transition: 'all 0.15s ease'
            }}
          >
            <UserIcon size={14} />
            <span>Profile Dossier</span>
          </button>

          <button
            onClick={() => setActiveTab('karma')}
            className={`tab-btn ${activeTab === 'karma' ? 'active' : ''}`}
            style={{
              padding: '8px 14px',
              background: activeTab === 'karma' ? 'rgba(201, 169, 110, 0.18)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'karma' ? '2px solid #ffd166' : '2px solid transparent',
              color: activeTab === 'karma' ? '#ffd166' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer',
              transition: 'all 0.15s ease'
            }}
          >
            <Sparkles size={14} />
            <span>My Karma Points</span>
          </button>

          <button
            onClick={() => setActiveTab('subscription')}
            className={`tab-btn ${activeTab === 'subscription' ? 'active' : ''}`}
            style={{
              padding: '8px 14px',
              background: activeTab === 'subscription' ? 'rgba(201, 169, 110, 0.18)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'subscription' ? '2px solid #ffd166' : '2px solid transparent',
              color: activeTab === 'subscription' ? '#ffd166' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer',
              transition: 'all 0.15s ease'
            }}
          >
            <Crown size={14} />
            <span>Subscription Tier</span>
          </button>

          <button
            onClick={() => setActiveTab('settings')}
            className={`tab-btn ${activeTab === 'settings' ? 'active' : ''}`}
            style={{
              padding: '8px 14px',
              background: activeTab === 'settings' ? 'rgba(201, 169, 110, 0.18)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'settings' ? '2px solid #ffd166' : '2px solid transparent',
              color: activeTab === 'settings' ? '#ffd166' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer',
              transition: 'all 0.15s ease'
            }}
          >
            <SettingsIcon size={14} />
            <span>Preferences</span>
          </button>

          <button
            onClick={() => setActiveTab('password')}
            className={`tab-btn ${activeTab === 'password' ? 'active' : ''}`}
            style={{
              padding: '8px 14px',
              background: activeTab === 'password' ? 'rgba(201, 169, 110, 0.18)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'password' ? '2px solid #ffd166' : '2px solid transparent',
              color: activeTab === 'password' ? '#ffd166' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer',
              transition: 'all 0.15s ease'
            }}
          >
            <Key size={14} />
            <span>Change Passphrase</span>
          </button>
        </div>

        {/* Tab Contents */}
        <div className="modal-body" style={{ padding: '20px', overflowY: 'auto', flex: 1 }}>
          {/* TAB 1: PROFILE DOSSIER */}
          {activeTab === 'profile' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '12px'
              }}>
                <div style={{
                  padding: '14px',
                  borderRadius: '10px',
                  background: 'rgba(15, 23, 42, 0.55)',
                  border: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  <div style={{ fontSize: '11px', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '4px' }}>
                    DEVOTEE FULL NAME
                  </div>
                  <div style={{ fontSize: '16px', fontWeight: 600, color: '#f3f4f6' }}>
                    {profile?.fullName || session.fullName}
                  </div>
                </div>

                <div style={{
                  padding: '14px',
                  borderRadius: '10px',
                  background: 'rgba(15, 23, 42, 0.55)',
                  border: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  <div style={{ fontSize: '11px', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '4px' }}>
                    EARTHLY USERNAME
                  </div>
                  <div style={{ fontSize: '15px', fontWeight: 600, color: '#00f5d4', fontFamily: 'var(--font-mono)' }}>
                    @{profile?.username || session.username}
                  </div>
                </div>

                <div style={{
                  padding: '14px',
                  borderRadius: '10px',
                  background: 'rgba(15, 23, 42, 0.55)',
                  border: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  <div style={{ fontSize: '11px', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '4px' }}>
                    CELESTIAL DISPATCH EMAIL
                  </div>
                  <div style={{ fontSize: '13px', fontWeight: 500, color: '#d1d5db', fontFamily: 'var(--font-mono)' }}>
                    {profile?.email || session.email || `${session.username}@earth.in`}
                  </div>
                </div>

                <div style={{
                  padding: '14px',
                  borderRadius: '10px',
                  background: 'rgba(15, 23, 42, 0.55)',
                  border: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  <div style={{ fontSize: '11px', color: '#9ca3af', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '4px' }}>
                    AKASHIC RECORD ID
                  </div>
                  <div style={{ fontSize: '14px', fontWeight: 600, color: '#ffd166', fontFamily: 'var(--font-mono)' }}>
                    DEV-00{session.id} (Sovereign Soul)
                  </div>
                </div>
              </div>

              {/* Devotee Status Cards */}
              <div style={{
                background: 'linear-gradient(135deg, rgba(201, 169, 110, 0.1) 0%, rgba(0, 245, 212, 0.05) 100%)',
                border: '1px solid rgba(201, 169, 110, 0.3)',
                borderRadius: '12px',
                padding: '16px 20px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <div>
                  <div style={{ fontSize: '11px', color: '#ffd166', fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase' }}>
                    SANCTUARY STATUS
                  </div>
                  <div style={{ fontSize: '14px', color: '#f3f4f6', marginTop: '4px' }}>
                    Good Standing in Heavenly Courts. Verified Devotee of Karma Sutra™.
                  </div>
                </div>
                <button
                  onClick={loadAccountData}
                  className="btn-secondary"
                  disabled={isLoading}
                  style={{ padding: '6px 12px', fontSize: '12px' }}
                >
                  <RefreshCw size={12} className={isLoading ? "spin-animation" : ""} />
                  <span>Sync Record</span>
                </button>
              </div>
            </div>
          )}

          {/* TAB 2: MY KARMA POINTS & LEDGER */}
          {activeTab === 'karma' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div style={{
                background: 'linear-gradient(135deg, rgba(201, 169, 110, 0.15) 0%, rgba(255, 209, 102, 0.05) 100%)',
                border: '1px solid rgba(201, 169, 110, 0.35)',
                borderRadius: '12px',
                padding: '20px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}>
                <div>
                  <div style={{ fontSize: '11px', color: '#ffd166', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                    CURRENT SOVEREIGN KARMA BALANCE
                  </div>
                  <div style={{ fontSize: '32px', fontWeight: 800, color: '#fef08a', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                    {session.karmaPoints} <span style={{ fontSize: '16px', fontWeight: 500, color: '#ffd166' }}>KP</span>
                  </div>
                  <div style={{ fontSize: '12px', color: '#9ca3af', marginTop: '4px' }}>
                    Awarded automatically upon grievance filing and righteous earthly deeds.
                  </div>
                </div>
                <div style={{
                  padding: '10px 18px',
                  borderRadius: '10px',
                  background: 'rgba(0, 0, 0, 0.4)',
                  border: '1px solid rgba(201, 169, 110, 0.3)',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '10px', color: '#9ca3af' }}>TIER ACCRUAL RATE</div>
                  <div style={{ fontSize: '16px', fontWeight: 700, color: '#34d399' }}>1.0x Base</div>
                </div>
              </div>

              {/* Transaction Ledger Table */}
              <div>
                <h3 style={{ fontSize: '13px', color: '#e5e7eb', marginBottom: '8px', fontWeight: 600 }}>
                  Recent Akashic Karma Transactions
                </h3>
                <div style={{
                  background: 'rgba(10, 15, 26, 0.7)',
                  borderRadius: '10px',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  overflow: 'hidden'
                }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '12px' }}>
                    <thead>
                      <tr style={{ background: 'rgba(255, 255, 255, 0.03)', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }}>
                        <th style={{ textAlign: 'left', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Reason / Manifestation</th>
                        <th style={{ textAlign: 'center', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Type</th>
                        <th style={{ textAlign: 'right', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Amount</th>
                        <th style={{ textAlign: 'right', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Recorded On</th>
                      </tr>
                    </thead>
                    <tbody>
                      {karmaLedger.length === 0 ? (
                        <tr>
                          <td colSpan={4} style={{ textAlign: 'center', padding: '24px', color: '#6b7280' }}>
                            No karma transactions logged yet in this cosmological cycle.
                          </td>
                        </tr>
                      ) : (
                        karmaLedger.map((tx) => (
                          <tr key={tx.id} style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.04)' }}>
                            <td style={{ padding: '10px 14px', color: '#f3f4f6' }}>{tx.reason}</td>
                            <td style={{ padding: '10px 14px', textAlign: 'center' }}>
                              <span style={{
                                fontSize: '10px',
                                padding: '2px 8px',
                                borderRadius: '12px',
                                background: tx.type === 'EARNED' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
                                color: tx.type === 'EARNED' ? '#34d399' : '#f87171',
                                border: `1px solid ${tx.type === 'EARNED' ? 'rgba(16, 185, 129, 0.3)' : 'rgba(239, 68, 68, 0.3)'}`
                              }}>
                                {tx.type}
                              </span>
                            </td>
                            <td style={{ padding: '10px 14px', textAlign: 'right', color: '#ffd166', fontWeight: 600, fontFamily: 'var(--font-mono)' }}>
                              +{tx.amount} KP
                            </td>
                            <td style={{ padding: '10px 14px', textAlign: 'right', color: '#9ca3af', fontSize: '11px' }}>
                              {tx.createdAt}
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* TAB 3: SUBSCRIPTION INFORMATION */}
          {activeTab === 'subscription' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div style={{
                background: 'linear-gradient(135deg, rgba(168, 85, 247, 0.15) 0%, rgba(201, 169, 110, 0.1) 100%)',
                border: '1px solid rgba(168, 85, 247, 0.35)',
                borderRadius: '12px',
                padding: '20px'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <span style={{
                      fontSize: '10px',
                      textTransform: 'uppercase',
                      letterSpacing: '0.12em',
                      background: 'rgba(168, 85, 247, 0.2)',
                      color: '#d8b4fe',
                      padding: '3px 10px',
                      borderRadius: '12px',
                      border: '1px solid rgba(168, 85, 247, 0.4)'
                    }}>
                      CURRENT ALLOTMENT
                    </span>
                    <h3 style={{ fontSize: '22px', fontWeight: 700, color: '#f3f4f6', marginTop: '8px' }}>
                      {session.tier}
                    </h3>
                    <p style={{ fontSize: '12px', color: '#9ca3af', marginTop: '2px' }}>
                      Access to all 7 planetary departments with standard cosmic response telemetry.
                    </p>
                  </div>
                  <div style={{
                    padding: '8px 14px',
                    borderRadius: '8px',
                    background: 'rgba(16, 185, 129, 0.15)',
                    border: '1px solid rgba(16, 185, 129, 0.3)',
                    color: '#34d399',
                    fontSize: '12px',
                    fontWeight: 600
                  }}>
                    Active & In Good Standing
                  </div>
                </div>

                <div style={{ marginTop: '18px', borderTop: '1px solid rgba(255, 255, 255, 0.08)', paddingTop: '14px' }}>
                  <div style={{ fontSize: '12px', fontWeight: 600, color: '#e5e7eb', marginBottom: '8px' }}>
                    Active Celestial Privileges:
                  </div>
                  <ul style={{ fontSize: '12px', color: '#d1d5db', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '6px', paddingLeft: '18px' }}>
                    <li>Full 3D Planetary Orrery Access</li>
                    <li>Akashic Docket History Logging</li>
                    <li>Lord Shani & Chitragupta Decrees</li>
                    <li>AI Humour & Roast Evaluation</li>
                    <li>Standard Celestial Queue Priority</li>
                    <li>Sovereign Session Protection</li>
                  </ul>
                </div>
              </div>

              {/* VIP Upgrade Banner */}
              {session.tier !== 'VIP Devotee' && (
                <div style={{
                  padding: '16px',
                  borderRadius: '10px',
                  background: 'rgba(201, 169, 110, 0.08)',
                  border: '1px dashed rgba(201, 169, 110, 0.4)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between'
                }}>
                  <div>
                    <div style={{ fontSize: '13px', fontWeight: 600, color: '#ffd166' }}>
                      Unlock Karma Sutra PRO™ (VIP Devotee)
                    </div>
                    <div style={{ fontSize: '11px', color: '#9ca3af' }}>
                      Grants 10x Karma Multipliers, direct petitions to Indra's High Court, and custom avatar halos.
                    </div>
                  </div>
                  <button 
                    onClick={() => alert('Celestial Payment Gateway is operating under divine grace in trial mode.')}
                    className="btn-primary" 
                    style={{ fontSize: '12px', padding: '6px 14px' }}
                  >
                    <span>Ascend to VIP</span>
                    <ArrowUpRight size={14} />
                  </button>
                </div>
              )}
            </div>
          )}

          {/* TAB 4: PREFERENCES & SETTINGS */}
          {activeTab === 'settings' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '14px 16px',
                borderRadius: '10px',
                background: 'rgba(15, 23, 42, 0.55)',
                border: '1px solid rgba(255, 255, 255, 0.08)'
              }}>
                <div>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: '#f3f4f6' }}>Celestial Audio Drone & Chimes</div>
                  <div style={{ fontSize: '11px', color: '#9ca3af' }}>Synthesize subtle harmonic space frequencies during planetary navigation.</div>
                </div>
                <input
                  type="checkbox"
                  checked={soundEnabled}
                  onChange={(e) => setSoundEnabled(e.target.checked)}
                  style={{ width: '18px', height: '18px', accentColor: '#00f5d4', cursor: 'pointer' }}
                />
              </div>

              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '14px 16px',
                borderRadius: '10px',
                background: 'rgba(15, 23, 42, 0.55)',
                border: '1px solid rgba(255, 255, 255, 0.08)'
              }}>
                <div>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: '#f3f4f6' }}>Atmospheric Rim Glow & HUD Shading</div>
                  <div style={{ fontSize: '11px', color: '#9ca3af' }}>Enable realistic Rayleigh planetary atmosphere shaders and gold specular reflections.</div>
                </div>
                <input
                  type="checkbox"
                  checked={hudGlowEnabled}
                  onChange={(e) => setHudGlowEnabled(e.target.checked)}
                  style={{ width: '18px', height: '18px', accentColor: '#ffd166', cursor: 'pointer' }}
                />
              </div>

              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '14px 16px',
                borderRadius: '10px',
                background: 'rgba(15, 23, 42, 0.55)',
                border: '1px solid rgba(255, 255, 255, 0.08)'
              }}>
                <div>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: '#f3f4f6' }}>Autonomous Solar System Orbit</div>
                  <div style={{ fontSize: '11px', color: '#9ca3af' }}>Planets revolve smoothly along Keplerian elliptical orbital paths.</div>
                </div>
                <input
                  type="checkbox"
                  checked={autoRotate}
                  onChange={(e) => setAutoRotate(e.target.checked)}
                  style={{ width: '18px', height: '18px', accentColor: '#00f5d4', cursor: 'pointer' }}
                />
              </div>

              <div style={{
                padding: '12px 14px',
                borderRadius: '8px',
                background: 'rgba(59, 130, 246, 0.1)',
                border: '1px solid rgba(59, 130, 246, 0.25)',
                fontSize: '11px',
                color: '#93c5fd'
              }}>
                ✦ Settings are locally synchronized to your MacBook graphics pipeline.
              </div>
            </div>
          )}

          {/* TAB 5: CHANGE PASSPHRASE */}
          {activeTab === 'password' && (
            <form onSubmit={handlePasswordSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{
                padding: '12px 14px',
                borderRadius: '8px',
                background: 'rgba(201, 169, 110, 0.1)',
                border: '1px solid rgba(201, 169, 110, 0.25)',
                fontSize: '12px',
                color: '#fef08a',
                lineHeight: 1.5
              }}>
                Master Passphrase protects your soul's Akashic petitions in the MySQL celestial registry.
                Hashing utilizes PBKDF2 with SHA-256 and unique 16-byte cryptographic salts.
              </div>

              {passStatus.type === 'error' && (
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '10px 14px',
                  borderRadius: '8px',
                  background: 'rgba(239, 68, 68, 0.15)',
                  border: '1px solid rgba(239, 68, 68, 0.35)',
                  color: '#fca5a5',
                  fontSize: '12px'
                }}>
                  <AlertCircle size={15} />
                  <span>{passStatus.message}</span>
                </div>
              )}

              {passStatus.type === 'success' && (
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '10px 14px',
                  borderRadius: '8px',
                  background: 'rgba(16, 185, 129, 0.15)',
                  border: '1px solid rgba(16, 185, 129, 0.35)',
                  color: '#6ee7b7',
                  fontSize: '12px'
                }}>
                  <CheckCircle2 size={15} />
                  <span>{passStatus.message}</span>
                </div>
              )}

              <div className="form-group">
                <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Lock size={12} style={{ color: '#ffd166' }} />
                  <span>CURRENT MASTER PASSPHRASE</span>
                </label>
                <input
                  type="password"
                  value={currentPass}
                  onChange={(e) => setCurrentPass(e.target.value)}
                  placeholder="Enter current passphrase (e.g. Devotee@108)"
                  className="form-input"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Key size={12} style={{ color: '#00f5d4' }} />
                  <span>NEW MASTER PASSPHRASE</span>
                </label>
                <input
                  type="password"
                  value={newPass}
                  onChange={(e) => setNewPass(e.target.value)}
                  placeholder="At least 6 celestial characters"
                  className="form-input"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <CheckCircle2 size={12} style={{ color: '#00f5d4' }} />
                  <span>CONFIRM NEW MASTER PASSPHRASE</span>
                </label>
                <input
                  type="password"
                  value={confirmPass}
                  onChange={(e) => setConfirmPass(e.target.value)}
                  placeholder="Repeat new master passphrase exactly"
                  className="form-input"
                  required
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '6px' }}>
                <button
                  type="submit"
                  disabled={passStatus.type === 'loading'}
                  className="btn-primary"
                  style={{ padding: '10px 20px', fontSize: '13px' }}
                >
                  <Key size={14} />
                  <span>{passStatus.type === 'loading' ? 'Encrypting & Updating...' : 'Seal New Master Passphrase'}</span>
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
