import React, { useState, useEffect } from 'react';
import { DevoteeDocket, apiBridge } from '../services/apiBridge';
import { X, FileText, Sparkles, Filter, ChevronRight, Award, Radio } from 'lucide-react';

interface DocketsModalProps {
  onClose: () => void;
  initialPlanetId?: string | null;
}

export const DocketsModal: React.FC<DocketsModalProps> = ({ onClose, initialPlanetId }) => {
  const [dockets, setDockets] = useState<DevoteeDocket[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedPlanetFilter, setSelectedPlanetFilter] = useState<string>(initialPlanetId || 'all');
  const [selectedDocket, setSelectedDocket] = useState<DevoteeDocket | null>(null);

  useEffect(() => {
    setLoading(true);
    apiBridge.getDockets().then((data) => {
      setDockets(data);
      if (data.length > 0) {
        if (initialPlanetId) {
          const match = data.find(d => d.planetId === initialPlanetId);
          setSelectedDocket(match || data[0]);
        } else {
          setSelectedDocket(data[0]);
        }
      }
      setLoading(false);
    });
  }, [initialPlanetId]);

  const filteredDockets = selectedPlanetFilter === 'all'
    ? dockets
    : dockets.filter(d => d.planetId === selectedPlanetFilter);

  const planetFilters = [
    { id: 'all', label: 'All Bureaus' },
    { id: 'mars', label: 'Mars (Career)' },
    { id: 'earth', label: 'Earth (Academics)' },
    { id: 'venus', label: 'Venus (Love)' },
    { id: 'jupiter', label: 'Jupiter (Family)' },
    { id: 'saturn', label: 'Saturn (Karma)' },
    { id: 'neptune', label: 'Neptune (Destiny)' },
    { id: 'sun', label: 'Sun (Admin)' }
  ];

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div 
        className="modal-dialog" 
        onClick={(e) => e.stopPropagation()}
        style={{ width: '840px', maxWidth: '96vw', height: '88vh', maxHeight: '720px' }}
      >
        {/* Modal Header */}
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{
              width: '32px',
              height: '32px',
              borderRadius: '8px',
              background: 'rgba(0, 245, 212, 0.15)',
              border: '1px solid rgba(0, 245, 212, 0.35)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#00f5d4'
            }}>
              <FileText size={16} />
            </div>
            <div>
              <h2 className="modal-title">Devotee Cosmic Dockets & Divine Decrees</h2>
              <p className="modal-subtitle">
                "Every action has a reaction. Every petition has an immutable heavenly decree."
              </p>
            </div>
          </div>
          <button 
            onClick={onClose} 
            className="hud-close-btn"
            title="Close dossier"
          >
            <X size={18} />
          </button>
        </div>

        {/* Filter Navigation Bar */}
        <div style={{
          padding: '8px 20px',
          background: 'rgba(0, 0, 0, 0.35)',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          display: 'flex',
          alignItems: 'center',
          gap: '6px',
          overflowX: 'auto'
        }}>
          <Filter size={12} style={{ color: '#8b8f9e', marginRight: '4px', flexShrink: 0 }} />
          {planetFilters.map((f) => (
            <button
              key={f.id}
              onClick={() => setSelectedPlanetFilter(f.id)}
              style={{
                background: selectedPlanetFilter === f.id ? 'rgba(0, 245, 212, 0.2)' : 'transparent',
                borderColor: selectedPlanetFilter === f.id ? 'rgba(0, 245, 212, 0.45)' : 'transparent',
                color: selectedPlanetFilter === f.id ? '#00f5d4' : '#8b8f9e',
                borderWidth: '1px',
                borderStyle: 'solid',
                borderRadius: '6px',
                padding: '4px 10px',
                fontSize: '11px',
                cursor: 'pointer',
                whiteSpace: 'nowrap',
                transition: 'all 0.2s ease'
              }}
            >
              {f.label}
            </button>
          ))}
        </div>

        {/* Dual Panel Body: Docket List + Selected Decree View */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '320px 1fr',
          flex: 1,
          overflow: 'hidden'
        }}>
          {/* Left: Docket List */}
          <div style={{
            borderRight: '1px solid rgba(255, 255, 255, 0.08)',
            background: 'rgba(0, 0, 0, 0.2)',
            overflowY: 'auto',
            padding: '12px'
          }}>
            {loading ? (
              <div style={{ padding: '30px', textAlign: 'center', color: '#8b8f9e', fontSize: '12px' }}>
                Consulting Akashic Records...
              </div>
            ) : filteredDockets.length === 0 ? (
              <div style={{ padding: '30px', textAlign: 'center', color: '#8b8f9e', fontSize: '12px' }}>
                No dockets filed under this cosmic department yet.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {filteredDockets.map((docket) => {
                  const isSelected = selectedDocket?.ticketNumber === docket.ticketNumber;
                  return (
                    <div
                      key={docket.ticketNumber}
                      onClick={() => setSelectedDocket(docket)}
                      style={{
                        padding: '12px',
                        borderRadius: '8px',
                        background: isSelected ? 'rgba(0, 245, 212, 0.1)' : 'rgba(255, 255, 255, 0.03)',
                        border: isSelected ? '1px solid rgba(0, 245, 212, 0.4)' : '1px solid rgba(255, 255, 255, 0.06)',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                        <span style={{ fontFamily: 'var(--font-mono)', fontSize: '10px', color: '#00f5d4', fontWeight: 600 }}>
                          {docket.ticketNumber}
                        </span>
                        <span style={{ fontSize: '10px', color: '#8b8f9e' }}>
                          {docket.createdAt}
                        </span>
                      </div>
                      <div style={{ fontSize: '12px', color: '#fff', fontWeight: 500, lineHeight: 1.3, marginBottom: '6px' }}>
                        {docket.subject}
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span style={{ fontSize: '10px', color: '#ffd166', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                          {docket.category}
                        </span>
                        <ChevronRight size={12} style={{ color: isSelected ? '#00f5d4' : '#5c6070' }} />
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Right: Decree Details & Planet Response */}
          <div style={{
            padding: '20px 24px',
            overflowY: 'auto',
            display: 'flex',
            flexDirection: 'column',
            gap: '16px',
            background: '#090b14'
          }}>
            {selectedDocket ? (
              <>
                {/* Docket Header Banner */}
                <div style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start',
                  paddingBottom: '14px',
                  borderBottom: '1px solid rgba(255, 255, 255, 0.08)'
                }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                      <span style={{
                        fontFamily: 'var(--font-mono)',
                        fontSize: '11px',
                        padding: '2px 8px',
                        borderRadius: '4px',
                        background: 'rgba(0, 245, 212, 0.15)',
                        border: '1px solid rgba(0, 245, 212, 0.35)',
                        color: '#00f5d4'
                      }}>
                        {selectedDocket.ticketNumber}
                      </span>
                      <span style={{
                        fontSize: '11px',
                        padding: '2px 8px',
                        borderRadius: '4px',
                        background: 'rgba(255, 209, 102, 0.15)',
                        border: '1px solid rgba(255, 209, 102, 0.3)',
                        color: '#ffd166'
                      }}>
                        {selectedDocket.status}
                      </span>
                    </div>
                    <h3 style={{ fontFamily: 'var(--font-serif)', fontSize: '18px', color: '#fff', fontWeight: 700 }}>
                      {selectedDocket.subject}
                    </h3>
                    <p style={{ fontSize: '11px', color: '#8b8f9e', marginTop: '2px' }}>
                      Department: <strong style={{ color: '#e5e7eb' }}>{selectedDocket.departmentName}</strong>
                    </p>
                  </div>

                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: '6px 12px',
                    borderRadius: '8px',
                    background: 'rgba(255, 209, 102, 0.12)',
                    border: '1px solid rgba(255, 209, 102, 0.35)',
                    color: '#ffd166',
                    fontFamily: 'var(--font-mono)',
                    fontSize: '11px',
                    fontWeight: 600
                  }}>
                    <Award size={13} />
                    <span>+{selectedDocket.karmaPointsAwarded} Karma</span>
                  </div>
                </div>

                {/* Submitter's Original Petition */}
                <div style={{
                  background: 'rgba(255, 255, 255, 0.03)',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  borderRadius: '8px',
                  padding: '12px 14px'
                }}>
                  <div style={{ fontSize: '10px', fontFamily: 'var(--font-mono)', color: '#00f5d4', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: '6px' }}>
                    DEVOTEE SUBMISSION DOSSIER
                  </div>
                  <p style={{ fontSize: '12px', color: '#d1d5db', lineHeight: 1.5 }}>
                    {selectedDocket.description}
                  </p>
                </div>

                {/* OFFICIAL PLANETARY RESPONSE & DECREE */}
                <div>
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginBottom: '8px'
                  }}>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '6px',
                      fontSize: '11px',
                      fontFamily: 'var(--font-mono)',
                      color: '#ffd166',
                      textTransform: 'uppercase',
                      letterSpacing: '0.1em'
                    }}>
                      <Sparkles size={12} />
                      OFFICIAL PLANETARY RESPONSE & DECREE
                    </div>
                    {selectedDocket.latestResponse?.divinePersona && (
                      <span style={{
                        fontSize: '11px',
                        color: '#c77dff',
                        fontFamily: 'var(--font-mono)',
                        background: 'rgba(157, 78, 221, 0.15)',
                        border: '1px solid rgba(157, 78, 221, 0.3)',
                        padding: '2px 8px',
                        borderRadius: '4px'
                      }}>
                        {selectedDocket.latestResponse.divinePersona}
                      </span>
                    )}
                  </div>

                  {selectedDocket.latestResponse ? (
                    <div className="decree-parchment" style={{
                      borderColor: 'rgba(255, 209, 102, 0.3)',
                      boxShadow: '0 0 20px rgba(0, 0, 0, 0.5), inset 0 0 15px rgba(255, 209, 102, 0.05)',
                      maxHeight: '340px'
                    }}>
                      {selectedDocket.latestResponse.responseText}
                    </div>
                  ) : (
                    <div style={{
                      padding: '24px',
                      background: 'rgba(0, 0, 0, 0.4)',
                      border: '1px dashed rgba(255, 255, 255, 0.15)',
                      borderRadius: '8px',
                      textAlign: 'center',
                      color: '#8b8f9e',
                      fontSize: '12px'
                    }}>
                      Petition currently queued in the cosmic queue buffer. The presiding deity is reviewing your dossier.
                    </div>
                  )}
                </div>
              </>
            ) : (
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                height: '100%',
                color: '#5c6070',
                fontSize: '13px'
              }}>
                Select a docket from the list to review the planetary verdict.
              </div>
            )}
          </div>
        </div>

        {/* Modal Footer */}
        <div className="modal-header" style={{ justifyContent: 'space-between', padding: '12px 24px', background: 'rgba(0, 0, 0, 0.4)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '11px', color: '#8b8f9e' }}>
            <Radio size={12} style={{ color: '#10b981' }} />
            <span>Synchronized with MySQL Akashic Database & Divine Engine</span>
          </div>
          <button onClick={onClose} className="btn-secondary">
            Close Dossier
          </button>
        </div>
      </div>
    </div>
  );
};
