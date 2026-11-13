import React, { useState, useEffect } from 'react';
import { CelestialBody } from '../data/celestialDepartments';
import { apiBridge, GrievanceSubmissionResult } from '../services/apiBridge';
import { X, Send, Sparkles, CheckCircle2, Copy, Check, FastForward, MessageSquare, AlertCircle } from 'lucide-react';

interface GrievanceModalProps {
  initialBody: CelestialBody | null;
  onClose: () => void;
  onSuccess: (result: GrievanceSubmissionResult) => void;
}

export const GrievanceModal: React.FC<GrievanceModalProps> = ({
  initialBody,
  onClose,
  onSuccess
}) => {
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');
  const [desiredResolution, setDesiredResolution] = useState('');
  const [type, setType] = useState('Wish');
  const [severity, setSeverity] = useState('High');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [result, setResult] = useState<GrievanceSubmissionResult | null>(null);
  const [copied, setCopied] = useState(false);
  const [authError, setAuthError] = useState<string | null>(null);

  // Typewriter effect state
  const [displayedText, setDisplayedText] = useState('');
  const [isTypingComplete, setIsTypingComplete] = useState(false);

  useEffect(() => {
    if (!result?.responseText) return;

    let index = 0;
    const fullText = result.responseText;
    setDisplayedText('');
    setIsTypingComplete(false);

    // Speed: 12ms per character for snappy yet cinematic reveal
    const interval = window.setInterval(() => {
      index++;
      if (index <= fullText.length) {
        setDisplayedText(fullText.substring(0, index));
      } else {
        setIsTypingComplete(true);
        clearInterval(interval);
      }
    }, 14);

    return () => clearInterval(interval);
  }, [result]);

  const handleSkipTyping = () => {
    if (result) {
      setDisplayedText(result.responseText);
      setIsTypingComplete(true);
    }
  };

  const handleFillBenchmark = (sampleSubject: string, sampleDesc: string, sampleType: string) => {
    setSubject(sampleSubject);
    setDescription(sampleDesc);
    setType(sampleType);
    setAuthError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subject.trim() || !description.trim()) return;

    setAuthError(null);
    setIsSubmitting(true);

    try {
      const res = await apiBridge.submitGrievance({
        departmentCode: initialBody ? initialBody.departmentCode : 'DEPT_MISC',
        category: initialBody ? initialBody.departmentName : 'General Divine Matter',
        type,
        severity,
        subject,
        description,
        desiredResolution
      });

      if (!res.success && res.errorMessage) {
        setAuthError(res.errorMessage);
        return;
      }

      setResult(res);
      onSuccess(res);
    } catch (err: any) {
      console.error(err);
      setAuthError(err.message || 'Submission failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  const copyToClipboard = () => {
    if (!result) return;
    navigator.clipboard.writeText(result.responseText);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const getClassificationMeta = (cls?: string) => {
    const raw = (cls || '').toLowerCase();
    if (raw.includes('funny') || raw.includes('hilarious')) {
      return {
        badge: '🎭 Funny / Hilarious Wish',
        badgeBg: 'rgba(245, 158, 11, 0.18)',
        badgeColor: '#fbbf24',
        borderColor: 'rgba(245, 158, 11, 0.4)',
        tagline: 'Cosmic Wit & Celestial Roast Protocol Activated'
      };
    }
    if (raw.includes('unusual') || raw.includes('absurd')) {
      return {
        badge: '🪐 Unusual or Absurd Wish',
        badgeBg: 'rgba(168, 85, 247, 0.18)',
        badgeColor: '#c084fc',
        borderColor: 'rgba(168, 85, 247, 0.4)',
        tagline: 'Cosmic Curio Recorded in the Ledger of Peculiar Petitions'
      };
    }
    if (raw.includes('inappropriate') || raw.includes('sanctuary')) {
      return {
        badge: '🛡️ Sacred Sanctuary Notice',
        badgeBg: 'rgba(239, 68, 68, 0.18)',
        badgeColor: '#f87171',
        borderColor: 'rgba(239, 68, 68, 0.4)',
        tagline: 'Compassionate Care Safeguard Enacted'
      };
    }
    if (raw.includes('prayer') || raw.includes('sincere')) {
      return {
        badge: '✨ Sincere Wish or Prayer',
        badgeBg: 'rgba(201, 169, 110, 0.18)',
        badgeColor: '#ffd166',
        borderColor: 'rgba(201, 169, 110, 0.4)',
        tagline: 'Devotional Intention Received by the Divine Directorate'
      };
    }
    return {
      badge: '⚖️ Genuine Grievance',
      badgeBg: 'rgba(16, 185, 129, 0.18)',
      badgeColor: '#34d399',
      borderColor: 'rgba(16, 185, 129, 0.4)',
      tagline: 'Official Celestial Arbitration Docket'
    };
  };

  const classificationMeta = getClassificationMeta(result?.classification);

  return (
    <div className="modal-overlay" style={{ zIndex: 1000 }}>
      <div 
        className="modal-dialog"
        style={{
          maxWidth: result ? '680px' : '620px',
          width: '94vw',
          maxHeight: '90vh',
          display: 'flex',
          flexDirection: 'column',
          background: 'linear-gradient(180deg, #0e121e 0%, #060912 100%)',
          border: '1px solid rgba(201, 169, 110, 0.25)',
          boxShadow: '0 25px 60px rgba(0, 0, 0, 0.85), 0 0 30px rgba(0, 245, 212, 0.1)'
        }}
      >
        {/* Modal Header */}
        <div className="modal-header" style={{ borderBottomColor: 'rgba(201, 169, 110, 0.2)' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Sparkles size={13} style={{ color: '#00f5d4' }} />
              <span style={{ fontSize: '10px', fontFamily: 'var(--font-mono)', letterSpacing: '0.12em', color: '#00f5d4', textTransform: 'uppercase' }}>
                AKASHIC SUBMISSION DOCKET
              </span>
            </div>
            <h2 className="modal-title">
              {result ? 'Petition Successfully Stamped in Heaven' : 'File Divine Grievance to the Cosmos'}
            </h2>
            {initialBody && (
              <p className="modal-subtitle">
                Department Destination: {initialBody.departmentName} ({initialBody.name})
              </p>
            )}
          </div>

          <button
            onClick={onClose}
            className="hud-close-btn"
          >
            <X size={18} />
          </button>
        </div>

        {/* Modal Body */}
        <div className="modal-body" style={{ padding: '20px', overflowY: 'auto', flex: 1 }}>
          {authError && (
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              padding: '12px 14px',
              borderRadius: '8px',
              background: 'rgba(239, 68, 68, 0.15)',
              border: '1px solid rgba(239, 68, 68, 0.35)',
              color: '#fca5a5',
              fontSize: '12px',
              marginBottom: '16px'
            }}>
              <AlertCircle size={16} />
              <span>{authError}</span>
            </div>
          )}

          {result ? (
            /* Result Screen with Typewriter & Humour Badges */
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {/* Ticket Metrics Strip */}
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                background: 'rgba(16, 185, 129, 0.1)',
                border: '1px solid rgba(16, 185, 129, 0.3)',
                borderRadius: '8px',
                padding: '10px 14px',
                fontSize: '12px'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#34d399', fontWeight: 600 }}>
                  <CheckCircle2 size={16} />
                  <span>Docket #{result.ticketNumber} Indexed in Heaven</span>
                </div>
                <div style={{ color: '#ffd166', fontFamily: 'var(--font-mono)', fontWeight: 600 }}>
                  +{result.karmaPointsAwarded} KP Granted (Balance: {result.newKarmaBalance})
                </div>
              </div>

              {/* Classification Badge */}
              <div style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '8px 14px',
                borderRadius: '8px',
                background: classificationMeta.badgeBg,
                border: `1px solid ${classificationMeta.borderColor}`
              }}>
                <div style={{
                  fontSize: '12px',
                  fontWeight: 700,
                  color: classificationMeta.badgeColor,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}>
                  <span>{classificationMeta.badge}</span>
                </div>
                <span style={{ fontSize: '11px', color: '#9ca3af', fontStyle: 'italic' }}>
                  {classificationMeta.tagline}
                </span>
              </div>

              {/* Response Parchment with Typewriter Effect */}
              <div 
                className="decree-parchment"
                style={{
                  position: 'relative',
                  minHeight: '160px',
                  background: 'rgba(10, 14, 24, 0.85)',
                  border: '1px solid rgba(201, 169, 110, 0.35)',
                  boxShadow: 'inset 0 0 30px rgba(0, 0, 0, 0.6), 0 8px 24px rgba(0, 0, 0, 0.5)',
                  padding: '18px 20px',
                  borderRadius: '10px'
                }}
              >
                <div style={{
                  fontSize: '11px',
                  color: '#ffd166',
                  borderBottom: '1px solid rgba(201, 169, 110, 0.2)',
                  paddingBottom: '8px',
                  marginBottom: '12px',
                  fontFamily: 'var(--font-sans)',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <MessageSquare size={13} style={{ color: '#ffd166' }} />
                    <span style={{ fontWeight: 700, letterSpacing: '0.04em' }}>
                      DIVINE SIGNATORY: {result.divinePersona}
                    </span>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    {!isTypingComplete && (
                      <button
                        onClick={handleSkipTyping}
                        style={{
                          background: 'rgba(255, 255, 255, 0.08)',
                          border: 'none',
                          color: '#00f5d4',
                          padding: '2px 8px',
                          borderRadius: '4px',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          gap: '4px',
                          fontSize: '10px'
                        }}
                      >
                        <FastForward size={10} />
                        <span>Skip Reveal</span>
                      </button>
                    )}

                    <button
                      onClick={copyToClipboard}
                      style={{
                        background: 'transparent',
                        border: 'none',
                        color: '#9ca3af',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px',
                        fontSize: '11px'
                      }}
                    >
                      {copied ? <Check size={12} style={{ color: '#34d399' }} /> : <Copy size={12} />}
                      <span>{copied ? 'Copied' : 'Copy'}</span>
                    </button>
                  </div>
                </div>

                {/* Animated Body Text */}
                <div style={{
                  fontSize: '13px',
                  lineHeight: '1.7',
                  color: '#f3f4f6',
                  whiteSpace: 'pre-line',
                  minHeight: '80px'
                }}>
                  {displayedText}
                  {!isTypingComplete && (
                    <span 
                      style={{ 
                        display: 'inline-block', 
                        width: '8px', 
                        height: '14px', 
                        backgroundColor: '#ffd166', 
                        marginLeft: '3px',
                        animation: 'pulse 0.7s infinite' 
                      }} 
                    />
                  )}
                </div>
              </div>

              <p style={{ fontSize: '11px', color: '#9ca3af', fontStyle: 'italic', textAlign: 'center' }}>
                Your docket has been mirrored to the local MySQL Akashic Records database.
              </p>

              {/* Action Buttons */}
              <div style={{ display: 'flex', gap: '10px', paddingTop: '4px' }}>
                <button
                  type="button"
                  onClick={() => {
                    setResult(null);
                    setSubject('');
                    setDescription('');
                    setDesiredResolution('');
                  }}
                  className="btn-secondary"
                  style={{ flex: 1, justifyContent: 'center' }}
                >
                  File Another Petition
                </button>
                <button
                  type="button"
                  onClick={onClose}
                  className="btn-primary"
                  style={{ flex: 1, justifyContent: 'center' }}
                >
                  Return to Universe
                </button>
              </div>
            </div>
          ) : (
            /* Submission Form */
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {/* Humour Engine Benchmark Shortcuts */}
              <div style={{
                background: 'rgba(255, 255, 255, 0.03)',
                borderRadius: '8px',
                border: '1px solid rgba(255, 255, 255, 0.06)',
                padding: '10px 12px'
              }}>
                <div style={{ fontSize: '10px', color: '#ffd166', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: '6px', fontWeight: 600 }}>
                  ✦ TEST INTELLIGENT HUMOUR ENGINE PRESETS (CLICK TO AUTOFILL):
                </div>
                <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                  <button
                    type="button"
                    onClick={() => handleFillBenchmark(
                      'I want 100% college attendance without going to class',
                      'Can the cosmic administrators hack the RFID reader or manipulate space-time so my attendance is marked while I sleep?',
                      'Wish'
                    )}
                    style={{
                      background: 'rgba(245, 158, 11, 0.1)',
                      border: '1px solid rgba(245, 158, 11, 0.3)',
                      color: '#fbbf24',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      cursor: 'pointer'
                    }}
                  >
                    😂 Classroom Attendance
                  </button>

                  <button
                    type="button"
                    onClick={() => handleFillBenchmark(
                      'Please make my college crush fall madly in love with me',
                      'I have never spoken a single word to her in 3 years, but I want the universe to orchestrate eternal romantic destiny.',
                      'Wish'
                    )}
                    style={{
                      background: 'rgba(236, 72, 153, 0.1)',
                      border: '1px solid rgba(236, 72, 153, 0.3)',
                      color: '#f472b6',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      cursor: 'pointer'
                    }}
                  >
                    😉 College Crush
                  </button>

                  <button
                    type="button"
                    onClick={() => handleFillBenchmark(
                      'Can tomorrow morning\'s semester exams disappear completely?',
                      'Please create a cosmic black hole or erase the exam question papers so nobody has to take it.',
                      'Wish'
                    )}
                    style={{
                      background: 'rgba(168, 85, 247, 0.1)',
                      border: '1px solid rgba(168, 85, 247, 0.3)',
                      color: '#c084fc',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      cursor: 'pointer'
                    }}
                  >
                    🪐 Exam Disappearance
                  </button>

                  <button
                    type="button"
                    onClick={() => handleFillBenchmark(
                      'Severe injustice in salary disbursement and hostile manager',
                      'My earthly employer withheld my legitimate earned compensation and retaliated unfairly when I raised health concerns.',
                      'Complaint'
                    )}
                    style={{
                      background: 'rgba(16, 185, 129, 0.1)',
                      border: '1px solid rgba(16, 185, 129, 0.3)',
                      color: '#34d399',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      cursor: 'pointer'
                    }}
                  >
                    ⚖️ Serious Grievance
                  </button>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">
                  PETITION SUBJECT / WISH TITLE
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. I want to be a billionaire without doing anything at all"
                  value={subject}
                  onChange={(e) => setSubject(e.target.value)}
                  className="form-input"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">
                    PETITION TYPE
                  </label>
                  <select
                    value={type}
                    onChange={(e) => setType(e.target.value)}
                    className="form-select"
                  >
                    <option value="Wish">Wish</option>
                    <option value="Complaint">Complaint</option>
                    <option value="Prayer">Prayer</option>
                    <option value="Personal Dilemma">Personal Dilemma</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">
                    SEVERITY RATING
                  </label>
                  <select
                    value={severity}
                    onChange={(e) => setSeverity(e.target.value)}
                    className="form-select"
                  >
                    <option value="Low">Low (Patience Permitted)</option>
                    <option value="Medium">Medium (Standard Cycle)</option>
                    <option value="High">High (Urgent Cosmic Review)</option>
                    <option value="Critical">Critical (Immediate Celestial Fiat)</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">
                  DETAILED STATEMENT OF PETITION / ABSURD PRAYER
                </label>
                <textarea
                  required
                  rows={4}
                  placeholder="Narrate your circumstance, ridiculous wish, or heartfelt prayer for planetary deliberation..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="form-input"
                  style={{ resize: 'vertical' }}
                />
              </div>

              <div className="form-group">
                <label className="form-label">
                  DESIRED HEAVENLY RESOLUTION (OPTIONAL)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Direct cosmic intervention, lottery numbers, or divine pardon"
                  value={desiredResolution}
                  onChange={(e) => setDesiredResolution(e.target.value)}
                  className="form-input"
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '6px' }}>
                <button
                  type="button"
                  onClick={onClose}
                  className="btn-secondary"
                  disabled={isSubmitting}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="btn-primary"
                  style={{ minWidth: '160px', justifyContent: 'center' }}
                >
                  <Send size={14} />
                  <span>{isSubmitting ? 'Deliberating in Heaven...' : 'Submit to Cosmos'}</span>
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
