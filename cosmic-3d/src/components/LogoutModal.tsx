import React, { useState } from 'react';
import { X, LogOut, AlertTriangle, ShieldCheck } from 'lucide-react';
import { apiBridge } from '../services/apiBridge';

interface LogoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLogoutConfirmed: (departureMessage: string) => void;
}

export const LogoutModal: React.FC<LogoutModalProps> = ({
  isOpen,
  onClose,
  onLogoutConfirmed
}) => {
  const [isProcessing, setIsProcessing] = useState(false);

  if (!isOpen) return null;

  const handleConfirm = async () => {
    setIsProcessing(true);
    try {
      const res = await apiBridge.logout();
      onLogoutConfirmed(res.message || 'You have successfully departed the Celestial Realm.');
    } catch (err) {
      console.error('Logout error', err);
      onLogoutConfirmed('You have successfully departed the Celestial Realm.');
    } finally {
      setIsProcessing(false);
      onClose();
    }
  };

  return (
    <div className="modal-overlay" style={{ zIndex: 1100 }}>
      <div 
        className="modal-dialog" 
        style={{ 
          maxWidth: '460px',
          border: '1px solid rgba(239, 68, 68, 0.4)',
          boxShadow: '0 20px 50px rgba(0, 0, 0, 0.8), 0 0 30px rgba(239, 68, 68, 0.2)' 
        }}
      >
        <div className="modal-header" style={{ borderBottomColor: 'rgba(239, 68, 68, 0.2)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{
              width: '32px',
              height: '32px',
              borderRadius: '8px',
              background: 'rgba(239, 68, 68, 0.15)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#f87171'
            }}>
              <AlertTriangle size={18} />
            </div>
            <div>
              <h2 className="modal-title" style={{ fontSize: '16px', color: '#fecaca' }}>
                Depart Celestial Realm
              </h2>
              <p className="modal-subtitle">Akashic Session Invalidation</p>
            </div>
          </div>
          <button 
            onClick={onClose} 
            className="hud-close-btn"
            disabled={isProcessing}
          >
            <X size={18} />
          </button>
        </div>

        <div className="modal-body" style={{ padding: '24px 20px 16px' }}>
          <p style={{ 
            fontSize: '15px', 
            fontWeight: 500, 
            color: '#f3f4f6', 
            lineHeight: 1.5,
            marginBottom: '14px',
            textAlign: 'center'
          }}>
            Are you sure you wish to leave the Celestial Realm?
          </p>

          <p style={{
            fontSize: '12px',
            color: '#9ca3af',
            lineHeight: 1.6,
            background: 'rgba(15, 23, 42, 0.6)',
            padding: '12px 14px',
            borderRadius: '8px',
            border: '1px solid rgba(255, 255, 255, 0.06)'
          }}>
            Your active devotee session token and cached celestial telemetry will be cleared. 
            All stored dockets, karma balance, and planetary records remain securely preserved in MySQL.
          </p>

          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            marginTop: '12px',
            fontSize: '11px',
            color: '#34d399'
          }}>
            <ShieldCheck size={14} />
            <span>Cryptographic session invalidation active</span>
          </div>
        </div>

        <div className="modal-footer" style={{
          display: 'flex',
          justifyContent: 'flex-end',
          gap: '10px',
          padding: '14px 20px',
          borderTop: '1px solid rgba(255, 255, 255, 0.08)',
          background: 'rgba(10, 15, 30, 0.5)'
        }}>
          <button
            type="button"
            onClick={onClose}
            disabled={isProcessing}
            className="btn-secondary"
            style={{ padding: '9px 18px', fontSize: '13px' }}
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleConfirm}
            disabled={isProcessing}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '9px 20px',
              borderRadius: '8px',
              background: 'linear-gradient(135deg, #dc2626 0%, #b91c1c 100%)',
              color: '#ffffff',
              fontSize: '13px',
              fontWeight: 600,
              border: '1px solid rgba(248, 113, 113, 0.5)',
              cursor: isProcessing ? 'wait' : 'pointer',
              boxShadow: '0 2px 12px rgba(220, 38, 38, 0.4)'
            }}
          >
            <LogOut size={14} />
            <span>{isProcessing ? 'Departing...' : 'Confirm Logout'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
