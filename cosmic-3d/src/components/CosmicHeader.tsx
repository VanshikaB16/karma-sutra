import React, { useState, useRef, useEffect } from 'react';
import { CelestialBody, CELESTIAL_DEPARTMENTS } from '../data/celestialDepartments';
import { DevoteeSession } from '../services/apiBridge';
import { AccountTab } from './AccountModal';
import { 
  Send, Radio, FileText, ChevronDown, User, Sparkles, Crown, 
  Settings, Key, LogOut, LogIn, Shield, ShieldAlert 
} from 'lucide-react';

interface CosmicHeaderProps {
  session: DevoteeSession | null;
  selectedBody: CelestialBody | null;
  onSelectBody: (body: CelestialBody | null) => void;
  onOpenGrievanceModal: () => void;
  onOpenDocketsModal: () => void;
  onOpenAccountModal: (tab: AccountTab) => void;
  onOpenAdminConsole?: () => void;
  onOpenLogoutModal: () => void;
  onOpenAuthModal: () => void;
  onNavigateHome?: () => void;
  isBridgeConnected: boolean;
}

export const CosmicHeader: React.FC<CosmicHeaderProps> = ({
  session,
  selectedBody,
  onSelectBody,
  onOpenGrievanceModal,
  onOpenDocketsModal,
  onOpenAccountModal,
  onOpenAdminConsole,
  onOpenLogoutModal,
  onOpenAuthModal,
  onNavigateHome,
  isBridgeConnected
}) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const isAdmin = session !== null && (session.role === 'ARCH_DEITY_ADMIN' || session.username.toLowerCase() === 'admin');

  // Close dropdown on click outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleMenuAction = (action: () => void) => {
    setIsDropdownOpen(false);
    action();
  };

  return (
    <header className="cosmic-header" style={{ position: 'relative', zIndex: 100 }}>
      {/* Brand & Tagline */}
      <div className="brand-section">
        <div 
          onClick={() => onSelectBody(null)}
          className="brand-badge-group"
          style={{ cursor: 'pointer' }}
        >
          <span className="brand-sparkle">✦</span>
          <div>
            <h1 className="brand-title">
              <span>KARMA SUTRA™</span>
              <span className="brand-pill">3D ORRERY</span>
            </h1>
            <p className="brand-tagline">
              "The universe has a ticketing system."
            </p>
          </div>
        </div>

        {/* Planet Quick Select Navigation */}
        <nav className="planet-nav-bar">
          <button
            onClick={() => onSelectBody(null)}
            className={`planet-nav-btn ${!selectedBody ? 'active' : ''}`}
          >
            System Orbit
          </button>
          {CELESTIAL_DEPARTMENTS.map((b) => (
            <button
              key={b.id}
              onClick={() => onSelectBody(b)}
              className={`planet-nav-btn ${selectedBody?.id === b.id ? 'active' : ''}`}
            >
              <span 
                className="planet-dot" 
                style={{ backgroundColor: b.atmosphereColor }} 
              />
              {b.name.startsWith('The ') ? b.name.substring(4) : b.name.split(' ')[0]}
            </button>
          ))}
        </nav>
      </div>

      {/* Devotee Telemetry, Profile Dropdown & Actions */}
      <div className="header-actions">
        {/* Return to Landing Website */}
        {onNavigateHome && (
          <button
            onClick={onNavigateHome}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '6px 12px',
              background: 'rgba(255, 255, 255, 0.05)',
              border: '1px solid rgba(229, 224, 216, 0.15)',
              borderRadius: '8px',
              color: '#ffd166',
              fontSize: '12px',
              fontWeight: 600,
              cursor: 'pointer',
              transition: 'all 0.2s ease'
            }}
            onMouseEnter={e => {
              e.currentTarget.style.background = 'rgba(255, 209, 102, 0.15)';
              e.currentTarget.style.borderColor = '#ffd166';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.background = 'rgba(255, 255, 255, 0.05)';
              e.currentTarget.style.borderColor = 'rgba(229, 224, 216, 0.15)';
            }}
            title="Return to KARMA SUTRA Landing Website"
          >
            <span>← Landing Site</span>
          </button>
        )}

        {/* Java Bridge Sync Status */}
        <div 
          className="telemetry-pill"
          title={isBridgeConnected ? "Synchronized with local Java Swing process & MySQL" : "Running in standalone cosmic synthesis mode"}
        >
          <Radio size={12} className={isBridgeConnected ? "status-dot-pulse" : ""} style={{ color: isBridgeConnected ? '#10b981' : '#f59e0b' }} />
          <span>
            {isBridgeConnected ? "SWING SYNC" : "OFFLINE ENGINE"}
          </span>
        </div>

        {/* Devotee Profile Area (Clickable Dropdown Trigger) */}
        {session ? (
          <div ref={dropdownRef} style={{ position: 'relative' }}>
            <button
              onClick={() => setIsDropdownOpen(!isDropdownOpen)}
              className="profile-trigger-btn"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                background: isAdmin 
                  ? 'linear-gradient(135deg, rgba(255, 215, 0, 0.15) 0%, rgba(220, 38, 38, 0.15) 100%)'
                  : isDropdownOpen ? 'rgba(201, 169, 110, 0.18)' : 'rgba(255, 255, 255, 0.05)',
                border: isAdmin
                  ? '1px solid rgba(255, 215, 0, 0.5)'
                  : isDropdownOpen ? '1px solid rgba(201, 169, 110, 0.5)' : '1px solid rgba(255, 255, 255, 0.1)',
                borderRadius: '8px',
                padding: '5px 10px',
                color: '#f3f4f6',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                outline: 'none',
                boxShadow: isAdmin ? '0 0 12px rgba(255, 215, 0, 0.2)' : 'none'
              }}
            >
              <div style={{
                width: '24px',
                height: '24px',
                borderRadius: '50%',
                background: isAdmin 
                  ? 'linear-gradient(135deg, #ffd700 0%, #ef4444 100%)' 
                  : 'linear-gradient(135deg, #ffd166 0%, #00f5d4 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#0a0d16',
                fontWeight: 700,
                fontSize: '11px'
              }}>
                {isAdmin ? '👑' : session.fullName.charAt(0)}
              </div>

              <span style={{ fontSize: '12px', fontWeight: 600, color: isAdmin ? '#fef08a' : '#f3f4f6' }}>
                {session.fullName}
              </span>

              <span className="karma-badge" style={{ margin: 0, padding: '2px 7px', fontSize: '11px' }}>
                {session.karmaPoints} KP
              </span>

              <span 
                className="tier-badge" 
                style={{ 
                  margin: 0, 
                  padding: '2px 7px', 
                  fontSize: '10px',
                  background: isAdmin ? 'rgba(239, 68, 68, 0.2)' : undefined,
                  color: isAdmin ? '#fca5a5' : undefined,
                  borderColor: isAdmin ? 'rgba(239, 68, 68, 0.4)' : undefined
                }}
              >
                {isAdmin ? 'ARCH-DEITY ADMIN' : session.tier.replace(' Devotee', '')}
              </span>

              <ChevronDown 
                size={14} 
                style={{ 
                  color: '#9ca3af', 
                  transition: 'transform 0.2s ease',
                  transform: isDropdownOpen ? 'rotate(180deg)' : 'rotate(0deg)'
                }} 
              />
            </button>

            {/* Polished Celestial Dropdown Menu */}
            {isDropdownOpen && (
              <div 
                className="profile-dropdown-menu"
                style={{
                  position: 'absolute',
                  top: 'calc(100% + 8px)',
                  right: 0,
                  width: '270px',
                  background: 'linear-gradient(180deg, #0e121e 0%, #080b13 100%)',
                  border: isAdmin ? '1px solid rgba(255, 215, 0, 0.4)' : '1px solid rgba(201, 169, 110, 0.3)',
                  borderRadius: '10px',
                  boxShadow: '0 15px 35px rgba(0, 0, 0, 0.8), 0 0 20px rgba(201, 169, 110, 0.12)',
                  padding: '6px',
                  zIndex: 200,
                  animation: 'fadeInSlide 0.15s ease'
                }}
              >
                {/* User Identity Header */}
                <div style={{
                  padding: '10px 12px 10px',
                  borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
                  marginBottom: '6px',
                  background: isAdmin ? 'rgba(255, 215, 0, 0.05)' : 'transparent',
                  borderRadius: '6px'
                }}>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: isAdmin ? '#fef08a' : '#f3f4f6' }}>
                    {session.fullName}
                  </div>
                  <div style={{ fontSize: '11px', color: '#9ca3af', fontFamily: 'var(--font-mono)' }}>
                    @{session.username} • {isAdmin ? 'ROOT HIGH DEITY' : `DEV-00${session.id}`}
                  </div>
                </div>

                {/* ARCH-DEITY ADMIN SPECIFIC OPTION */}
                {isAdmin && onOpenAdminConsole && (
                  <button
                    onClick={() => handleMenuAction(onOpenAdminConsole)}
                    className="dropdown-item-btn"
                    style={{
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px',
                      padding: '9px 12px',
                      background: 'linear-gradient(90deg, rgba(255, 215, 0, 0.15) 0%, rgba(220, 38, 38, 0.15) 100%)',
                      border: '1px solid rgba(255, 215, 0, 0.3)',
                      borderRadius: '6px',
                      color: '#fef08a',
                      fontSize: '12px',
                      fontWeight: 600,
                      cursor: 'pointer',
                      textAlign: 'left',
                      marginBottom: '6px'
                    }}
                  >
                    <Crown size={15} style={{ color: '#ffd700' }} />
                    <span>Arch-Deity Admin Console</span>
                  </button>
                )}

                {/* Dropdown Options */}
                <button
                  onClick={() => handleMenuAction(() => onOpenAccountModal('profile'))}
                  className="dropdown-item-btn"
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '8px 12px',
                    background: 'transparent',
                    border: 'none',
                    borderRadius: '6px',
                    color: '#e5e7eb',
                    fontSize: '12px',
                    cursor: 'pointer',
                    textAlign: 'left'
                  }}
                >
                  <User size={14} style={{ color: '#00f5d4' }} />
                  <span>Profile / Devotee Details</span>
                </button>

                <button
                  onClick={() => handleMenuAction(() => onOpenAccountModal('karma'))}
                  className="dropdown-item-btn"
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '8px 12px',
                    background: 'transparent',
                    border: 'none',
                    borderRadius: '6px',
                    color: '#e5e7eb',
                    fontSize: '12px',
                    cursor: 'pointer',
                    textAlign: 'left'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <Sparkles size={14} style={{ color: '#ffd166' }} />
                    <span>My Karma Points</span>
                  </div>
                  <span style={{
                    fontSize: '11px',
                    color: '#ffd166',
                    fontFamily: 'var(--font-mono)',
                    fontWeight: 600
                  }}>
                    {session.karmaPoints} KP
                  </span>
                </button>

                <button
                  onClick={() => handleMenuAction(() => onOpenAccountModal('subscription'))}
                  className="dropdown-item-btn"
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '8px 12px',
                    background: 'transparent',
                    border: 'none',
                    borderRadius: '6px',
                    color: '#e5e7eb',
                    fontSize: '12px',
                    cursor: 'pointer',
                    textAlign: 'left'
                  }}
                >
                  <Crown size={14} style={{ color: '#c084fc' }} />
                  <span>Subscription Information</span>
                </button>

                <button
                  onClick={() => handleMenuAction(() => onOpenAccountModal('settings'))}
                  className="dropdown-item-btn"
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '8px 12px',
                    background: 'transparent',
                    border: 'none',
                    borderRadius: '6px',
                    color: '#e5e7eb',
                    fontSize: '12px',
                    cursor: 'pointer',
                    textAlign: 'left'
                  }}
                >
                  <Settings size={14} style={{ color: '#9ca3af' }} />
                  <span>Account Settings</span>
                </button>

                <button
                  onClick={() => handleMenuAction(() => onOpenAccountModal('password'))}
                  className="dropdown-item-btn"
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '8px 12px',
                    background: 'transparent',
                    border: 'none',
                    borderRadius: '6px',
                    color: '#e5e7eb',
                    fontSize: '12px',
                    cursor: 'pointer',
                    textAlign: 'left'
                  }}
                >
                  <Key size={14} style={{ color: '#38bdf8' }} />
                  <span>Change Master Passphrase</span>
                </button>

                <div style={{ height: '1px', background: 'rgba(255, 255, 255, 0.08)', margin: '4px 6px' }} />

                <button
                  onClick={() => handleMenuAction(onOpenLogoutModal)}
                  className="dropdown-item-btn logout"
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '8px 12px',
                    background: 'rgba(239, 68, 68, 0.08)',
                    border: 'none',
                    borderRadius: '6px',
                    color: '#f87171',
                    fontSize: '12px',
                    fontWeight: 600,
                    cursor: 'pointer',
                    textAlign: 'left'
                  }}
                >
                  <LogOut size={14} />
                  <span>Logout of Realm</span>
                </button>
              </div>
            )}
          </div>
        ) : (
          /* Unauthenticated State: Sign In Button */
          <button
            onClick={onOpenAuthModal}
            className="btn-primary"
            style={{ padding: '7px 14px', fontSize: '12px' }}
          >
            <LogIn size={13} />
            <span>Enter Realm</span>
          </button>
        )}

        {/* ARCH-DEITY ADMIN CONSOLE BUTTON */}
        {isAdmin && onOpenAdminConsole && (
          <button
            onClick={onOpenAdminConsole}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '8px',
              background: 'linear-gradient(135deg, rgba(255, 215, 0, 0.2) 0%, rgba(220, 38, 38, 0.2) 100%)',
              border: '1px solid rgba(255, 215, 0, 0.45)',
              color: '#fef08a',
              fontSize: '12px',
              fontWeight: 700,
              cursor: 'pointer',
              boxShadow: '0 2px 10px rgba(255, 215, 0, 0.25)',
              transition: 'all 0.15s ease'
            }}
            title="Arch-Deity High Council Administration Console"
          >
            <Crown size={14} style={{ color: '#ffd700' }} />
            <span>Admin Console</span>
          </button>
        )}

        {/* Devotee Filed Dockets & Responses */}
        <button
          onClick={onOpenDocketsModal}
          className="btn-secondary"
          title="Inspect filed grievances and planetary decrees"
          style={{ padding: '8px 14px' }}
        >
          <FileText size={13} style={{ color: '#00f5d4' }} />
          <span>{isAdmin ? 'All Dockets' : 'My Dockets'}</span>
        </button>

        {/* Global Submit CTA */}
        <button
          onClick={onOpenGrievanceModal}
          className="btn-primary"
        >
          <Send size={13} />
          <span>File Grievance</span>
        </button>
      </div>
    </header>
  );
};
