import React, { useState, useEffect } from 'react';
import { CosmicScene } from './components/CosmicScene';
import { CosmicHeader } from './components/CosmicHeader';
import { PlanetHUD } from './components/PlanetHUD';
import { GrievanceModal } from './components/GrievanceModal';
import { DocketsModal } from './components/DocketsModal';
import { UniverseControls } from './components/UniverseControls';
import { AccountModal, AccountTab } from './components/AccountModal';
import { LogoutModal } from './components/LogoutModal';
import { AuthModal } from './components/AuthModal';
import { AdminConsoleModal } from './components/AdminConsoleModal';
import { LandingPage } from './components/LandingPage';
import { CelestialBody, CELESTIAL_DEPARTMENTS } from './data/celestialDepartments';
import { apiBridge, DevoteeSession } from './services/apiBridge';

export function App() {
  // Navigation View State: 'landing' (Default Website) vs 'universe' (3D Orrery)
  const [currentView, setCurrentView] = useState<'landing' | 'universe'>(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.get('view') === 'universe' || params.get('focus')) {
      return 'universe';
    }
    return 'landing';
  });

  const [selectedBody, setSelectedBody] = useState<CelestialBody | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalBody, setModalBody] = useState<CelestialBody | null>(null);
  const [isDocketsOpen, setIsDocketsOpen] = useState(false);
  const [docketsPlanetId, setDocketsPlanetId] = useState<string | null>(null);

  // Authentication & Account Management State
  const [session, setSession] = useState<DevoteeSession | null>(apiBridge.getSession());
  const [isAccountModalOpen, setIsAccountModalOpen] = useState(false);
  const [accountModalTab, setAccountModalTab] = useState<AccountTab>('profile');
  const [isAdminConsoleOpen, setIsAdminConsoleOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [authModalMode, setAuthModalMode] = useState<'login' | 'register'>('login');
  const [departureMessage, setDepartureMessage] = useState<string | null>(null);

  const [isBridgeConnected, setIsBridgeConnected] = useState(false);

  // Scene visual preferences
  const [orbitSpeedMultiplier, setOrbitSpeedMultiplier] = useState(1);
  const [showOrbits, setShowOrbits] = useState(true);
  const [showLabels, setShowLabels] = useState(true);

  // Check URL parameters for direct deep-linking from Swing app or external links
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const focusParam = params.get('focus')?.toLowerCase();
    if (focusParam) {
      const match = CELESTIAL_DEPARTMENTS.find(
        b => b.id.toLowerCase() === focusParam || b.name.toLowerCase().includes(focusParam)
      );
      if (match) {
        setSelectedBody(match);
        setCurrentView('universe');
      }
    }

    // Health check bridge and refresh devotee session
    apiBridge.checkHealth().then(connected => {
      setIsBridgeConnected(connected);
      setSession(apiBridge.getSession());
    });
  }, []);

  const handleOpenGrievanceModal = (body?: CelestialBody | null) => {
    if (!session) {
      setDepartureMessage('Please identify your soul to petition the celestial realm.');
      setAuthModalMode('login');
      setIsAuthModalOpen(true);
      return;
    }
    setModalBody(body || selectedBody || null);
    setIsModalOpen(true);
  };

  const handleOpenGrievanceFromLanding = (departmentCode?: string) => {
    let matched: CelestialBody | null = null;
    if (departmentCode) {
      matched = CELESTIAL_DEPARTMENTS.find(b => b.departmentCode === departmentCode) || null;
    }
    handleOpenGrievanceModal(matched);
  };

  const handleExploreUniverse = (focusPlanet?: CelestialBody) => {
    if (focusPlanet) {
      setSelectedBody(focusPlanet);
    }
    setCurrentView('universe');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleOpenDocketsModal = (planetId?: string | null) => {
    if (!session) {
      setDepartureMessage('Please identify your soul to inspect filed dockets.');
      setAuthModalMode('login');
      setIsAuthModalOpen(true);
      return;
    }
    setDocketsPlanetId(planetId || null);
    setIsDocketsOpen(true);
  };

  const handleOpenAccountModal = (tab: AccountTab) => {
    if (!session) {
      setAuthModalMode('login');
      setIsAuthModalOpen(true);
      return;
    }
    setAccountModalTab(tab);
    setIsAccountModalOpen(true);
  };

  const handleModalSuccess = () => {
    setSession(apiBridge.getSession());
  };

  const handleLogoutConfirmed = (msg: string) => {
    setSession(null);
    setDepartureMessage(msg);
    setAuthModalMode('login');
    setIsAuthModalOpen(true);
  };

  const handleLoginSuccess = (newSession: DevoteeSession) => {
    setSession(newSession);
    setDepartureMessage(null);
    setIsAuthModalOpen(false);
  };

  const handleOpenAuth = (mode: 'login' | 'register' = 'login') => {
    setDepartureMessage(null);
    setAuthModalMode(mode);
    setIsAuthModalOpen(true);
  };

  return (
    <div className="cosmic-app-container">
      {/* VIEW 1: PROFESSIONAL LANDING WEBSITE */}
      {currentView === 'landing' && (
        <LandingPage
          session={session}
          onExploreUniverse={handleExploreUniverse}
          onOpenGrievanceModal={handleOpenGrievanceFromLanding}
          onOpenAuthModal={handleOpenAuth}
          onOpenDocketsModal={() => handleOpenDocketsModal()}
          onOpenAdminConsole={() => setIsAdminConsoleOpen(true)}
          onOpenLogoutModal={() => setIsLogoutModalOpen(true)}
        />
      )}

      {/* VIEW 2: INTERACTIVE 3D COSMIC ORRERY */}
      {currentView === 'universe' && (
        <>
          {/* Top Header with Profile Dropdown, Telemetry & Return to Landing */}
          <CosmicHeader
            session={session}
            selectedBody={selectedBody}
            onSelectBody={setSelectedBody}
            onOpenGrievanceModal={() => handleOpenGrievanceModal()}
            onOpenDocketsModal={() => handleOpenDocketsModal()}
            onOpenAccountModal={handleOpenAccountModal}
            onOpenAdminConsole={() => setIsAdminConsoleOpen(true)}
            onOpenLogoutModal={() => setIsLogoutModalOpen(true)}
            onOpenAuthModal={() => handleOpenAuth('login')}
            onNavigateHome={() => setCurrentView('landing')}
            isBridgeConnected={isBridgeConnected}
          />

          {/* 3D WebGL Canvas */}
          <CosmicScene
            selectedBody={selectedBody}
            onSelectBody={setSelectedBody}
            orbitSpeedMultiplier={orbitSpeedMultiplier}
            showOrbits={showOrbits}
            showLabels={showLabels}
          />

          {/* Selected Planet HUD Information Panel */}
          {selectedBody && (
            <PlanetHUD
              body={selectedBody}
              onClose={() => setSelectedBody(null)}
              onSubmitGrievance={(body) => handleOpenGrievanceModal(body)}
              onOpenDocketsModal={(planetId) => handleOpenDocketsModal(planetId)}
            />
          )}

          {/* Floating Bottom Universe Controls */}
          <UniverseControls
            onResetCamera={() => setSelectedBody(null)}
            orbitSpeedMultiplier={orbitSpeedMultiplier}
            setOrbitSpeedMultiplier={setOrbitSpeedMultiplier}
            showOrbits={showOrbits}
            setShowOrbits={setShowOrbits}
            showLabels={showLabels}
            setShowLabels={setShowLabels}
          />
        </>
      )}

      {/* ===================================================================
          GLOBAL MODALS (Accessible from both Landing Page and 3D Universe)
          =================================================================== */}

      {/* Grievance Submission Modal */}
      {isModalOpen && (
        <GrievanceModal
          initialBody={modalBody}
          onClose={() => setIsModalOpen(false)}
          onSuccess={handleModalSuccess}
        />
      )}

      {/* Devotee Filed Dockets & Planetary Decrees Modal */}
      {isDocketsOpen && (
        <DocketsModal
          initialPlanetId={docketsPlanetId}
          onClose={() => setIsDocketsOpen(false)}
        />
      )}

      {/* Account Management & Profile Sanctuary Modal */}
      {isAccountModalOpen && session && (
        <AccountModal
          isOpen={isAccountModalOpen}
          initialTab={accountModalTab}
          onClose={() => setIsAccountModalOpen(false)}
          session={session}
          onSessionUpdated={(updated) => setSession(updated)}
        />
      )}

      {/* Arch-Deity Administrative Console Modal */}
      {isAdminConsoleOpen && session && (
        <AdminConsoleModal
          isOpen={isAdminConsoleOpen}
          onClose={() => setIsAdminConsoleOpen(false)}
        />
      )}

      {/* Logout Confirmation Modal */}
      <LogoutModal
        isOpen={isLogoutModalOpen}
        onClose={() => setIsLogoutModalOpen(false)}
        onLogoutConfirmed={handleLogoutConfirmed}
      />

      {/* Celestial Gateway Sign-In / Registration Modal */}
      <AuthModal
        isOpen={isAuthModalOpen}
        initialMode={authModalMode}
        departureMessage={departureMessage}
        onClose={session ? () => setIsAuthModalOpen(false) : undefined}
        onLoginSuccess={handleLoginSuccess}
      />
    </div>
  );
}

export default App;
