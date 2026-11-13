import React, { useState } from 'react';
import { CelestialBody } from '../data/celestialDepartments';
import { 
  X, 
  Send, 
  Clock, 
  Users, 
  ShieldAlert, 
  Compass, 
  Sparkles,
  FileText
} from 'lucide-react';

interface PlanetHUDProps {
  body: CelestialBody;
  onClose: () => void;
  onSubmitGrievance: (body: CelestialBody) => void;
  onOpenDocketsModal?: (planetId?: string) => void;
}

export const PlanetHUD: React.FC<PlanetHUDProps> = ({
  body,
  onClose,
  onSubmitGrievance,
  onOpenDocketsModal
}) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'docket'>('overview');

  return (
    <div 
      className="planet-hud"
      style={{
        borderTop: `3px solid ${body.atmosphereColor}`
      }}
    >
      {/* HUD Header */}
      <div className="hud-header">
        <div>
          <div className="station-label">
            <span 
              className="planet-dot"
              style={{ backgroundColor: body.atmosphereColor, boxShadow: `0 0 8px ${body.atmosphereColor}` }}
            />
            <span>
              {body.bodyType === 'star' ? 'SOLAR NEXUS' : `ORBITAL STATION // ${body.name.toUpperCase()}`}
            </span>
          </div>
          <h2 className="hud-title">
            {body.departmentName}
          </h2>
          <p className="hud-deity">
            Presiding: {body.divineHead}
          </p>
        </div>

        <button
          onClick={onClose}
          className="hud-close-btn"
          title="Return to Universe Overview"
        >
          <X size={18} />
        </button>
      </div>

      {/* Navigation Pills */}
      <div className="hud-tabs">
        <button
          onClick={() => setActiveTab('overview')}
          className={`hud-tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
        >
          Bureau Overview
        </button>
        <button
          onClick={() => setActiveTab('docket')}
          className={`hud-tab-btn ${activeTab === 'docket' ? 'active' : ''}`}
        >
          Active Docket ({body.recentDockets.length})
        </button>
      </div>

      {/* Tab Content */}
      <div className="hud-body">
        {activeTab === 'overview' ? (
          <>
            {/* Quick Metrics Bar */}
            <div className="metrics-grid">
              <div className="metric-card">
                <div className="metric-title">
                  <Users size={12} style={{ color: '#00f5d4' }} />
                  Active Backlog
                </div>
                <div className="metric-value">
                  {body.backlogCount.toLocaleString()} <span style={{ fontSize: '10px', color: '#8b8f9e', fontWeight: 400 }}>petitions</span>
                </div>
              </div>

              <div className="metric-card">
                <div className="metric-title">
                  <Clock size={12} style={{ color: '#ffd166' }} />
                  Avg Resolution
                </div>
                <div className="metric-value">
                  {body.averageResolutionTime}
                </div>
              </div>
            </div>

            {/* Department Mandate */}
            <div className="desc-card">
              <div className="card-tag">
                <Compass size={11} />
                Celestial Mandate
              </div>
              <p>{body.description}</p>
            </div>

            {/* Bureaucratic Satirical Lore */}
            <div className="lore-card">
              <div className="card-tag" style={{ color: '#ffd166' }}>
                <Sparkles size={11} />
                Akashic Telemetry Lore
              </div>
              <p>"{body.lore}"</p>
            </div>

            {/* Escalation Protocol */}
            <div className="desc-card" style={{ borderColor: 'rgba(157, 78, 221, 0.25)' }}>
              <div className="card-tag" style={{ color: '#c77dff' }}>
                <ShieldAlert size={11} />
                Escalation Protocol
              </div>
              <p>{body.escalationProtocol}</p>
            </div>

            {/* Astrological Weather */}
            <div className="transit-weather">
              <span className="status-dot-pulse" style={{ backgroundColor: '#10b981' }} />
              <span>{body.currentCelestialTransit}</span>
            </div>
          </>
        ) : (
          /* Active Dockets Tab */
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', color: '#8b8f9e', fontFamily: 'var(--font-mono)' }}>
              <span>RECENT HEAVENLY DOCKETS</span>
              <span style={{ color: '#00f5d4' }}>SYNCED</span>
            </div>
            {body.recentDockets.map((doc) => (
              <div 
                key={doc.ticketId}
                style={{
                  background: 'rgba(0, 0, 0, 0.45)',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  borderRadius: '8px',
                  padding: '10px 12px'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '10px', fontFamily: 'var(--font-mono)', color: '#8b8f9e', marginBottom: '4px' }}>
                  <span style={{ color: '#00f5d4' }}>{doc.ticketId}</span>
                  <span>{doc.filedAgo}</span>
                </div>
                <div style={{ fontSize: '12px', color: '#f7f5f0', fontWeight: 500 }}>
                  {doc.subject}
                </div>
                <div style={{ marginTop: '6px' }}>
                  <span style={{
                    fontSize: '10px',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    background: 'rgba(255, 209, 102, 0.15)',
                    border: '1px solid rgba(255, 209, 102, 0.3)',
                    color: '#ffd166'
                  }}>
                    {doc.status}
                  </span>
                </div>
              </div>
            ))}

            {onOpenDocketsModal && (
              <button
                onClick={() => onOpenDocketsModal(body.id)}
                className="btn-secondary"
                style={{ width: '100%', marginTop: '6px', fontSize: '11px', padding: '8px' }}
              >
                <FileText size={12} style={{ color: '#00f5d4' }} />
                <span>Open {body.name} Docket Dossier</span>
              </button>
            )}
          </div>
        )}
      </div>

      {/* Action Footer */}
      <div className="hud-footer">
        <button
          onClick={onClose}
          className="btn-secondary"
          style={{ flex: 1 }}
        >
          Orbit Overview
        </button>

        <button
          onClick={() => onSubmitGrievance(body)}
          className="btn-primary"
          style={{ flex: 1 }}
        >
          <Send size={13} />
          Submit Grievance
        </button>
      </div>
    </div>
  );
};
