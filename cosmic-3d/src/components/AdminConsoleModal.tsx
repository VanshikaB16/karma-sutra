import React, { useState, useEffect } from 'react';
import { 
  X, Crown, Activity, Users, FileText, CheckCircle2, Clock, 
  Search, Filter, ShieldCheck, Sparkles, RefreshCw, ChevronRight, Eye, Gift 
} from 'lucide-react';
import { 
  apiBridge, AdminOverviewMetrics, GlobalAdminDocket, DevoteeSoulRecord 
} from '../services/apiBridge';

interface AdminConsoleModalProps {
  isOpen: boolean;
  onClose: () => void;
  onRefreshDockets?: () => void;
}

export const AdminConsoleModal: React.FC<AdminConsoleModalProps> = ({
  isOpen,
  onClose,
  onRefreshDockets
}) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'dockets' | 'devotees' | 'treasury'>('overview');
  const [metrics, setMetrics] = useState<AdminOverviewMetrics | null>(null);
  const [globalDockets, setGlobalDockets] = useState<GlobalAdminDocket[]>([]);
  const [devotees, setDevotees] = useState<DevoteeSoulRecord[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // Filters for dockets
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedDocket, setSelectedDocket] = useState<GlobalAdminDocket | null>(null);

  // Karma grant modal state
  const [selectedDevoteeForKarma, setSelectedDevoteeForKarma] = useState<DevoteeSoulRecord | null>(null);
  const [grantAmount, setGrantAmount] = useState(108);
  const [grantReason, setGrantReason] = useState('Arch-Deity Sovereign Blessing');
  const [actionNotice, setActionNotice] = useState<string | null>(null);

  useEffect(() => {
    if (isOpen) {
      loadAdminData();
    }
  }, [isOpen]);

  const loadAdminData = async () => {
    setIsLoading(true);
    setActionNotice(null);
    try {
      const [metricsData, docketsData, devoteesData] = await Promise.all([
        apiBridge.getAdminOverview(),
        apiBridge.getAllGlobalDockets(),
        apiBridge.getAllDevotees()
      ]);
      if (metricsData) setMetrics(metricsData);
      if (docketsData) {
        setGlobalDockets(docketsData);
        if (docketsData.length > 0 && !selectedDocket) {
          setSelectedDocket(docketsData[0]);
        }
      }
      if (devoteesData) setDevotees(devoteesData);
    } catch (err) {
      console.warn('Failed loading admin telemetry', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateStatus = async (ticketNumber: string, newStatus: string) => {
    try {
      const res = await apiBridge.updateDocketStatus(ticketNumber, newStatus);
      if (res.success) {
        setActionNotice(`Docket #${ticketNumber} status updated to '${newStatus}' by divine decree.`);
        // Update local state
        setGlobalDockets(prev => prev.map(d => d.ticketNumber === ticketNumber ? { ...d, status: newStatus } : d));
        if (selectedDocket?.ticketNumber === ticketNumber) {
          setSelectedDocket(prev => prev ? { ...prev, status: newStatus } : null);
        }
        if (onRefreshDockets) onRefreshDockets();
      }
    } catch (err: any) {
      setActionNotice('Failed to update status: ' + err.message);
    }
  };

  const handleGrantKarma = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedDevoteeForKarma) return;

    try {
      const res = await apiBridge.grantDevoteeKarma(selectedDevoteeForKarma.id, grantAmount, grantReason);
      if (res.success) {
        setActionNotice(`Granted ${grantAmount} KP to ${selectedDevoteeForKarma.fullName}!`);
        setDevotees(prev => prev.map(dev => dev.id === selectedDevoteeForKarma.id ? { ...dev, karmaPoints: res.newBalance || (dev.karmaPoints + grantAmount) } : dev));
        setSelectedDevoteeForKarma(null);
      }
    } catch (err: any) {
      setActionNotice('Error granting karma: ' + err.message);
    }
  };

  if (!isOpen) return null;

  const filteredDockets = globalDockets.filter(d => {
    const matchesSearch = !searchQuery.trim() || 
      d.ticketNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      d.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
      d.devoteeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      d.category.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesStatus = statusFilter === 'all' || d.status.toLowerCase().includes(statusFilter.toLowerCase());
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="modal-overlay" style={{ zIndex: 1100 }}>
      <div 
        className="modal-dialog"
        style={{
          maxWidth: '1020px',
          width: '96vw',
          height: '90vh',
          maxHeight: '820px',
          display: 'flex',
          flexDirection: 'column',
          background: 'linear-gradient(180deg, #101424 0%, #080a14 100%)',
          border: '1px solid rgba(255, 215, 0, 0.35)',
          boxShadow: '0 30px 80px rgba(0, 0, 0, 0.9), 0 0 40px rgba(255, 215, 0, 0.15)'
        }}
      >
        {/* Header */}
        <div className="modal-header" style={{ borderBottomColor: 'rgba(255, 215, 0, 0.25)', padding: '16px 24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div style={{
              width: '38px',
              height: '38px',
              borderRadius: '10px',
              background: 'linear-gradient(135deg, rgba(255, 215, 0, 0.25) 0%, rgba(220, 38, 38, 0.25) 100%)',
              border: '1px solid rgba(255, 215, 0, 0.5)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffd700'
            }}>
              <Crown size={22} />
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <h2 className="modal-title" style={{ fontSize: '18px', color: '#fef08a' }}>
                  Arch-Deity Administrative Console
                </h2>
                <span style={{
                  fontSize: '10px',
                  fontFamily: 'var(--font-mono)',
                  background: 'rgba(239, 68, 68, 0.2)',
                  color: '#fca5a5',
                  padding: '2px 8px',
                  borderRadius: '12px',
                  border: '1px solid rgba(239, 68, 68, 0.4)',
                  fontWeight: 700
                }}>
                  ROOT DEITY ACCESS
                </span>
              </div>
              <p className="modal-subtitle">
                High-level surveillance of global dockets, celestial routing, and devotee souls.
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <button
              onClick={loadAdminData}
              className="btn-secondary"
              disabled={isLoading}
              style={{ padding: '6px 12px', fontSize: '12px' }}
            >
              <RefreshCw size={12} className={isLoading ? "spin-animation" : ""} />
              <span>Refresh Realm</span>
            </button>

            <button onClick={onClose} className="hud-close-btn">
              <X size={18} />
            </button>
          </div>
        </div>

        {/* Action Notice Banner */}
        {actionNotice && (
          <div style={{
            background: 'linear-gradient(90deg, rgba(16, 185, 129, 0.2) 0%, rgba(255, 215, 0, 0.15) 100%)',
            borderBottom: '1px solid rgba(16, 185, 129, 0.3)',
            padding: '8px 24px',
            fontSize: '12px',
            color: '#6ee7b7',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Sparkles size={14} style={{ color: '#ffd700' }} />
              <span>{actionNotice}</span>
            </div>
            <button
              onClick={() => setActionNotice(null)}
              style={{ background: 'transparent', border: 'none', color: '#9ca3af', cursor: 'pointer', fontSize: '11px' }}
            >
              Dismiss
            </button>
          </div>
        )}

        {/* Navigation Tabs */}
        <div style={{
          display: 'flex',
          gap: '4px',
          padding: '10px 20px 0',
          borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
          background: 'rgba(8, 12, 22, 0.6)'
        }}>
          <button
            onClick={() => setActiveTab('overview')}
            style={{
              padding: '8px 16px',
              background: activeTab === 'overview' ? 'rgba(255, 215, 0, 0.15)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'overview' ? '2px solid #ffd700' : '2px solid transparent',
              color: activeTab === 'overview' ? '#ffd700' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer'
            }}
          >
            <Activity size={14} />
            <span>Cosmic Surveillance</span>
          </button>

          <button
            onClick={() => setActiveTab('dockets')}
            style={{
              padding: '8px 16px',
              background: activeTab === 'dockets' ? 'rgba(255, 215, 0, 0.15)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'dockets' ? '2px solid #ffd700' : '2px solid transparent',
              color: activeTab === 'dockets' ? '#ffd700' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer'
            }}
          >
            <FileText size={14} />
            <span>All Global Petitions ({globalDockets.length})</span>
          </button>

          <button
            onClick={() => setActiveTab('devotees')}
            style={{
              padding: '8px 16px',
              background: activeTab === 'devotees' ? 'rgba(255, 215, 0, 0.15)' : 'transparent',
              border: 'none',
              borderBottom: activeTab === 'devotees' ? '2px solid #ffd700' : '2px solid transparent',
              color: activeTab === 'devotees' ? '#ffd700' : '#9ca3af',
              fontSize: '12px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer'
            }}
          >
            <Users size={14} />
            <span>Devotee Directory ({devotees.length})</span>
          </button>
        </div>

        {/* Body Content */}
        <div className="modal-body" style={{ padding: '20px', flex: 1, overflowY: 'auto' }}>
          {/* TAB 1: OVERVIEW METRICS */}
          {activeTab === 'overview' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {/* 4 Major KPI Cards */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(210px, 1fr))',
                gap: '14px'
              }}>
                <div style={{
                  padding: '18px',
                  borderRadius: '12px',
                  background: 'rgba(15, 23, 42, 0.65)',
                  border: '1px solid rgba(255, 215, 0, 0.25)',
                  boxShadow: '0 4px 16px rgba(0,0,0,0.4)'
                }}>
                  <div style={{ fontSize: '11px', color: '#ffd700', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>
                    TOTAL GLOBAL PETITIONS
                  </div>
                  <div style={{ fontSize: '32px', fontWeight: 800, color: '#fef08a', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                    {metrics?.totalTickets || globalDockets.length}
                  </div>
                  <div style={{ fontSize: '12px', color: '#9ca3af', marginTop: '4px' }}>
                    Active across all 7 planetary departments
                  </div>
                </div>

                <div style={{
                  padding: '18px',
                  borderRadius: '12px',
                  background: 'rgba(15, 23, 42, 0.65)',
                  border: '1px solid rgba(0, 245, 212, 0.25)',
                  boxShadow: '0 4px 16px rgba(0,0,0,0.4)'
                }}>
                  <div style={{ fontSize: '11px', color: '#00f5d4', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>
                    REGISTERED DEVOTEE SOULS
                  </div>
                  <div style={{ fontSize: '32px', fontWeight: 800, color: '#a7f3d0', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                    {metrics?.totalDevotees || devotees.length}
                  </div>
                  <div style={{ fontSize: '12px', color: '#9ca3af', marginTop: '4px' }}>
                    Recorded in MySQL Akashic Registry
                  </div>
                </div>

                <div style={{
                  padding: '18px',
                  borderRadius: '12px',
                  background: 'rgba(15, 23, 42, 0.65)',
                  border: '1px solid rgba(245, 158, 11, 0.25)',
                  boxShadow: '0 4px 16px rgba(0,0,0,0.4)'
                }}>
                  <div style={{ fontSize: '11px', color: '#fbbf24', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>
                    PENDING HIGH COUNCIL REVIEW
                  </div>
                  <div style={{ fontSize: '32px', fontWeight: 800, color: '#fde68a', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                    {metrics?.pendingReview ?? 2}
                  </div>
                  <div style={{ fontSize: '12px', color: '#9ca3af', marginTop: '4px' }}>
                    Awaiting Lord Shani & Proctorial fiat
                  </div>
                </div>

                <div style={{
                  padding: '18px',
                  borderRadius: '12px',
                  background: 'rgba(15, 23, 42, 0.65)',
                  border: '1px solid rgba(16, 185, 129, 0.25)',
                  boxShadow: '0 4px 16px rgba(0,0,0,0.4)'
                }}>
                  <div style={{ fontSize: '11px', color: '#34d399', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>
                    GLOBALLY RESOLVED
                  </div>
                  <div style={{ fontSize: '32px', fontWeight: 800, color: '#6ee7b7', fontFamily: 'var(--font-mono)', marginTop: '4px' }}>
                    {metrics?.resolved ?? 8}
                  </div>
                  <div style={{ fontSize: '12px', color: '#9ca3af', marginTop: '4px' }}>
                    Sealed by celestial decrees & AI decrees
                  </div>
                </div>
              </div>

              {/* Department Breakdown Strip */}
              <div style={{
                background: 'rgba(10, 15, 26, 0.7)',
                borderRadius: '12px',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                padding: '18px 20px'
              }}>
                <div style={{ fontSize: '13px', fontWeight: 600, color: '#f3f4f6', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <ShieldCheck size={16} style={{ color: '#ffd700' }} />
                  <span>Planetary Bureau Distribution</span>
                </div>
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))',
                  gap: '10px'
                }}>
                  {[
                    { dept: 'Earth', name: 'Academic Suffering', color: '#60a5fa' },
                    { dept: 'Mars', name: 'Career & Unemployment', color: '#f87171' },
                    { dept: 'Venus', name: 'Love & Relationships', color: '#f472b6' },
                    { dept: 'Jupiter', name: 'Family Drama', color: '#fbbf24' },
                    { dept: 'Saturn', name: 'Karma Department', color: '#c084fc' },
                    { dept: 'Neptune', name: 'Destiny & Decisions', color: '#38bdf8' },
                    { dept: 'Sun', name: 'Divine Administration', color: '#f59e0b' }
                  ].map((p, idx) => {
                    const count = globalDockets.filter(d => d.planetId === p.dept.toLowerCase() || d.category.toLowerCase().includes(p.name.toLowerCase().split(' ')[0])).length;
                    return (
                      <div key={idx} style={{
                        padding: '10px 12px',
                        borderRadius: '8px',
                        background: 'rgba(255, 255, 255, 0.03)',
                        border: '1px solid rgba(255, 255, 255, 0.06)'
                      }}>
                        <div style={{ fontSize: '11px', color: p.color, fontWeight: 600 }}>{p.dept}</div>
                        <div style={{ fontSize: '10px', color: '#9ca3af', margin: '2px 0 4px' }}>{p.name}</div>
                        <div style={{ fontSize: '15px', fontWeight: 700, color: '#f3f4f6', fontFamily: 'var(--font-mono)' }}>
                          {count} petitions
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: ALL GLOBAL DOCKETS (WITH ADMIN ACTIONS) */}
          {activeTab === 'dockets' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', height: '100%' }}>
              {/* Filter Controls */}
              <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                <div style={{ flex: 1, minWidth: '220px', position: 'relative' }}>
                  <Search size={14} style={{ position: 'absolute', left: '12px', top: '12px', color: '#9ca3af' }} />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Search petitions by devotee, subject, ticket..."
                    className="form-input"
                    style={{ paddingLeft: '32px' }}
                  />
                </div>

                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                  className="form-select"
                  style={{ width: '180px' }}
                >
                  <option value="all">All Statuses</option>
                  <option value="Received">Received by Heaven</option>
                  <option value="Review">Under Divine Review</option>
                  <option value="Resolved">Resolved</option>
                </select>
              </div>

              {/* Dockets Master Table & Inspection Split */}
              <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: '16px', minHeight: '380px' }}>
                {/* Table */}
                <div style={{
                  background: 'rgba(10, 15, 26, 0.7)',
                  borderRadius: '10px',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  overflowY: 'auto',
                  maxHeight: '440px'
                }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '12px' }}>
                    <thead>
                      <tr style={{ background: 'rgba(255, 255, 255, 0.04)', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }}>
                        <th style={{ textAlign: 'left', padding: '10px 12px', color: '#ffd700', fontWeight: 600 }}>Ticket</th>
                        <th style={{ textAlign: 'left', padding: '10px 12px', color: '#9ca3af', fontWeight: 500 }}>Devotee</th>
                        <th style={{ textAlign: 'left', padding: '10px 12px', color: '#9ca3af', fontWeight: 500 }}>Subject</th>
                        <th style={{ textAlign: 'center', padding: '10px 12px', color: '#9ca3af', fontWeight: 500 }}>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredDockets.length === 0 ? (
                        <tr>
                          <td colSpan={4} style={{ textAlign: 'center', padding: '30px', color: '#6b7280' }}>
                            No global dockets found matching criteria.
                          </td>
                        </tr>
                      ) : (
                        filteredDockets.map((d) => (
                          <tr
                            key={d.id}
                            onClick={() => setSelectedDocket(d)}
                            style={{
                              borderBottom: '1px solid rgba(255, 255, 255, 0.04)',
                              cursor: 'pointer',
                              background: selectedDocket?.ticketNumber === d.ticketNumber ? 'rgba(255, 215, 0, 0.08)' : 'transparent'
                            }}
                          >
                            <td style={{ padding: '10px 12px', color: '#00f5d4', fontFamily: 'var(--font-mono)', fontWeight: 600 }}>
                              {d.ticketNumber}
                            </td>
                            <td style={{ padding: '10px 12px', color: '#f3f4f6' }}>
                              {d.devoteeName}
                              <div style={{ fontSize: '10px', color: '#9ca3af' }}>@{d.devoteeUsername}</div>
                            </td>
                            <td style={{ padding: '10px 12px', color: '#e5e7eb', maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                              {d.subject}
                            </td>
                            <td style={{ padding: '10px 12px', textAlign: 'center' }}>
                              <span style={{
                                fontSize: '10px',
                                padding: '2px 8px',
                                borderRadius: '12px',
                                background: d.status.includes('Resolved') ? 'rgba(16, 185, 129, 0.15)' : 'rgba(245, 158, 11, 0.15)',
                                color: d.status.includes('Resolved') ? '#34d399' : '#fbbf24',
                                border: `1px solid ${d.status.includes('Resolved') ? 'rgba(16, 185, 129, 0.3)' : 'rgba(245, 158, 11, 0.3)'}`
                              }}>
                                {d.status}
                              </span>
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>

                {/* Selected Docket Inspector & Admin Fiat Actions */}
                {selectedDocket ? (
                  <div style={{
                    background: 'rgba(15, 23, 42, 0.65)',
                    borderRadius: '10px',
                    border: '1px solid rgba(255, 215, 0, 0.25)',
                    padding: '16px',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '12px',
                    maxHeight: '440px',
                    overflowY: 'auto'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255, 255, 255, 0.08)', paddingBottom: '8px' }}>
                      <div>
                        <div style={{ fontSize: '11px', color: '#ffd700', fontFamily: 'var(--font-mono)' }}>
                          DOCKET #{selectedDocket.ticketNumber}
                        </div>
                        <div style={{ fontSize: '13px', fontWeight: 600, color: '#f3f4f6' }}>
                          Mortal: {selectedDocket.devoteeName} (@{selectedDocket.devoteeUsername})
                        </div>
                      </div>
                      <span style={{ fontSize: '11px', color: '#9ca3af' }}>{selectedDocket.createdAt}</span>
                    </div>

                    <div>
                      <div style={{ fontSize: '11px', color: '#9ca3af', textTransform: 'uppercase' }}>PETITION SUBJECT</div>
                      <div style={{ fontSize: '13px', fontWeight: 600, color: '#e5e7eb', marginTop: '2px' }}>
                        {selectedDocket.subject}
                      </div>
                    </div>

                    <div>
                      <div style={{ fontSize: '11px', color: '#9ca3af', textTransform: 'uppercase' }}>DETAILED STATEMENT</div>
                      <div style={{ fontSize: '12px', color: '#d1d5db', background: 'rgba(0, 0, 0, 0.3)', padding: '8px 10px', borderRadius: '6px', marginTop: '2px', lineHeight: 1.5 }}>
                        {selectedDocket.description}
                      </div>
                    </div>

                    {selectedDocket.latestResponse && (
                      <div>
                        <div style={{ fontSize: '11px', color: '#00f5d4', textTransform: 'uppercase' }}>
                          DIVINE DECREE: {selectedDocket.latestResponse.divinePersona}
                        </div>
                        <div style={{ fontSize: '11px', color: '#d1d5db', background: 'rgba(0, 245, 212, 0.05)', border: '1px solid rgba(0, 245, 212, 0.2)', padding: '8px 10px', borderRadius: '6px', marginTop: '2px', maxHeight: '100px', overflowY: 'auto', whiteSpace: 'pre-line' }}>
                          {selectedDocket.latestResponse.responseText}
                        </div>
                      </div>
                    )}

                    {/* Divine Fiat Status Buttons */}
                    <div style={{ marginTop: 'auto', borderTop: '1px solid rgba(255, 255, 255, 0.08)', paddingTop: '10px' }}>
                      <div style={{ fontSize: '10px', color: '#ffd700', textTransform: 'uppercase', marginBottom: '6px', fontWeight: 600 }}>
                        SOVEREIGN ADMINISTRATIVE FIAT:
                      </div>
                      <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                        <button
                          onClick={() => handleUpdateStatus(selectedDocket.ticketNumber, 'Under Divine Review')}
                          className="btn-secondary"
                          style={{ fontSize: '11px', padding: '4px 8px' }}
                        >
                          Mark Under Review
                        </button>
                        <button
                          onClick={() => handleUpdateStatus(selectedDocket.ticketNumber, 'Resolved by Divine Fiat')}
                          className="btn-primary"
                          style={{ fontSize: '11px', padding: '4px 8px' }}
                        >
                          Resolve by Fiat
                        </button>
                        <button
                          onClick={() => handleUpdateStatus(selectedDocket.ticketNumber, 'Forwarded to Karma Department')}
                          className="btn-secondary"
                          style={{ fontSize: '11px', padding: '4px 8px', color: '#c084fc' }}
                        >
                          Forward to Karma
                        </button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#6b7280', fontSize: '12px' }}>
                    Select a docket to inspect or execute divine fiat
                  </div>
                )}
              </div>
            </div>
          )}

          {/* TAB 3: DEVOTEES DIRECTORY */}
          {activeTab === 'devotees' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ fontSize: '13px', color: '#e5e7eb', fontWeight: 600 }}>
                  Registered Sovereign Devotees in Akashic Registry ({devotees.length})
                </div>
                <div style={{ fontSize: '11px', color: '#9ca3af' }}>
                  Arch-Deity Administrator may grant discretionary karma endowments.
                </div>
              </div>

              <div style={{
                background: 'rgba(10, 15, 26, 0.7)',
                borderRadius: '10px',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                overflow: 'hidden'
              }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '12px' }}>
                  <thead>
                    <tr style={{ background: 'rgba(255, 255, 255, 0.04)', borderBottom: '1px solid rgba(255, 255, 255, 0.08)' }}>
                      <th style={{ textAlign: 'left', padding: '10px 14px', color: '#ffd700', fontWeight: 600 }}>ID</th>
                      <th style={{ textAlign: 'left', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Devotee Name</th>
                      <th style={{ textAlign: 'left', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Email / Username</th>
                      <th style={{ textAlign: 'center', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Rank / Tier</th>
                      <th style={{ textAlign: 'right', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Karma Balance</th>
                      <th style={{ textAlign: 'center', padding: '10px 14px', color: '#9ca3af', fontWeight: 500 }}>Admin Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {devotees.map((dev) => (
                      <tr key={dev.id} style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.04)' }}>
                        <td style={{ padding: '10px 14px', color: '#9ca3af', fontFamily: 'var(--font-mono)' }}>
                          DEV-00{dev.id}
                        </td>
                        <td style={{ padding: '10px 14px', color: '#f3f4f6', fontWeight: 600 }}>
                          {dev.fullName}
                        </td>
                        <td style={{ padding: '10px 14px', color: '#00f5d4', fontFamily: 'var(--font-mono)', fontSize: '11px' }}>
                          {dev.email} <span style={{ color: '#9ca3af' }}>(@{dev.username})</span>
                        </td>
                        <td style={{ padding: '10px 14px', textAlign: 'center' }}>
                          <span style={{
                            fontSize: '10px',
                            padding: '2px 8px',
                            borderRadius: '12px',
                            background: dev.role === 'ARCH_DEITY_ADMIN' ? 'rgba(220, 38, 38, 0.2)' : 'rgba(201, 169, 110, 0.15)',
                            color: dev.role === 'ARCH_DEITY_ADMIN' ? '#fca5a5' : '#ffd166',
                            border: `1px solid ${dev.role === 'ARCH_DEITY_ADMIN' ? 'rgba(220, 38, 38, 0.4)' : 'rgba(201, 169, 110, 0.3)'}`
                          }}>
                            {dev.tier}
                          </span>
                        </td>
                        <td style={{ padding: '10px 14px', textAlign: 'right', color: '#ffd700', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>
                          {dev.karmaPoints} KP
                        </td>
                        <td style={{ padding: '10px 14px', textAlign: 'center' }}>
                          <button
                            onClick={() => setSelectedDevoteeForKarma(dev)}
                            className="btn-secondary"
                            style={{ padding: '3px 8px', fontSize: '11px', display: 'inline-flex', alignItems: 'center', gap: '4px' }}
                          >
                            <Gift size={11} style={{ color: '#ffd700' }} />
                            <span>Grant Karma</span>
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Karma Grant Inline Sub-Modal */}
              {selectedDevoteeForKarma && (
                <div style={{
                  background: 'rgba(255, 215, 0, 0.08)',
                  border: '1px solid rgba(255, 215, 0, 0.3)',
                  borderRadius: '10px',
                  padding: '16px',
                  marginTop: '10px'
                }}>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: '#fef08a', marginBottom: '10px' }}>
                    ✦ Grant Sovereign Karma Endowment to {selectedDevoteeForKarma.fullName} (@{selectedDevoteeForKarma.username})
                  </div>
                  <form onSubmit={handleGrantKarma} style={{ display: 'flex', gap: '10px', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                    <div style={{ width: '130px' }}>
                      <label style={{ fontSize: '10px', color: '#9ca3af', textTransform: 'uppercase' }}>Amount (KP)</label>
                      <input
                        type="number"
                        min="1"
                        max="10000"
                        value={grantAmount}
                        onChange={(e) => setGrantAmount(parseInt(e.target.value) || 0)}
                        className="form-input"
                      />
                    </div>
                    <div style={{ flex: 1, minWidth: '200px' }}>
                      <label style={{ fontSize: '10px', color: '#9ca3af', textTransform: 'uppercase' }}>Reason</label>
                      <input
                        type="text"
                        value={grantReason}
                        onChange={(e) => setGrantReason(e.target.value)}
                        className="form-input"
                      />
                    </div>
                    <button type="submit" className="btn-primary" style={{ padding: '9px 16px', fontSize: '12px' }}>
                      Bestow Karma
                    </button>
                    <button type="button" onClick={() => setSelectedDevoteeForKarma(null)} className="btn-secondary" style={{ padding: '9px 12px', fontSize: '12px' }}>
                      Cancel
                    </button>
                  </form>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
