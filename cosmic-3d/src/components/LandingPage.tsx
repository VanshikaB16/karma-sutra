import React, { useState } from 'react';
import { 
  Sparkles, Globe, Compass, Shield, Award, Terminal, ArrowRight, 
  ExternalLink, CheckCircle2, ChevronRight, MessageSquare, Zap, 
  Cpu, Database, Layers, Flame, BookOpen, User, Star, Crown, 
  HelpCircle, RefreshCw, Eye, Sparkle, FileText
} from 'lucide-react';
import { CelestialBody, CELESTIAL_DEPARTMENTS } from '../data/celestialDepartments';
import { DevoteeSession } from '../services/apiBridge';

interface LandingPageProps {
  session: DevoteeSession | null;
  onExploreUniverse: (focusPlanet?: CelestialBody) => void;
  onOpenGrievanceModal: (departmentCode?: string) => void;
  onOpenAuthModal: (mode?: 'login' | 'register') => void;
  onOpenDocketsModal: () => void;
  onOpenAdminConsole?: () => void;
  onOpenLogoutModal: () => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({
  session,
  onExploreUniverse,
  onOpenGrievanceModal,
  onOpenAuthModal,
  onOpenDocketsModal,
  onOpenAdminConsole,
  onOpenLogoutModal
}) => {
  const [activeHumourTab, setActiveHumourTab] = useState<'attendance' | 'billionaire' | 'crush' | 'serious' | 'compassion'>('attendance');
  const [selectedDeptFilter, setSelectedDeptFilter] = useState<string>('all');

  const isAdmin = session !== null && (session.role === 'ARCH_DEITY_ADMIN' || session.username.toLowerCase() === 'admin');

  // Interactive AI sample responses
  const AI_SAMPLES = {
    attendance: {
      tag: "Hilarious Wish • Academic Bunking",
      icon: "🎓",
      prompt: "I want 75% college attendance without attending a single morning lecture.",
      deity: "Chitragupta of the Bell Curve, Senior Proctor",
      department: "Ministry of Academic Suffering",
      karma: "+15 KP",
      decree: `OFFICIAL DETERMINATION — DOCKET #KS-2026-AT75
Dear Devotee, even the Universe cannot mark you present while you're orbiting outside the classroom. Your attendance karma needs serious intervention. 😂

Our celestial telemetry scanned your classroom coordinates during the last semester:
• Assigned Seat: Empty void vibrating with quantum disappointment.
• Actual Location: Canteen periphery, third table from counter, debating whether a 12% attendance record can be "rounded up" to 75%.

Chitragupta reviewed Section 75(B) of the Akashic Education Code: The laws of physics permit quantum entanglement, but not proxy signatures in 8:00 AM Engineering Mathematics.`
    },
    billionaire: {
      tag: "Absurd Wish • Zero-Effort Wealth",
      icon: "💼",
      prompt: "I want to be a billionaire tomorrow without doing any job or hard work at all.",
      deity: "Lord Shani, Lead SCRUM Master of Cosmic Fate",
      department: "Ministry of Corporate Karmic Balancing",
      karma: "+20 KP",
      decree: `OFFICIAL DECREE — DOCKET #KS-2026-BIL01
Salutations Petitioner,
Your dossier has been processed through our automated Applicant Tracking Karma System (ATKS). 

Our celestial telemetry reveals that your resume matches 98.4% of earthly vacancies, but your karmic willingness to wake up before noon stands at 1.2%. Lord Shani confirms that unearned billions in this quarter would trigger a catastrophic overflow error in your cosmic tax bracket.

Action Item: Maintain composure in upcoming technical rounds and stop scrolling hustle-porn on LinkedIn at 3:00 AM.`
    },
    crush: {
      tag: "Romantic Dilemma • Delivered Status",
      icon: "💔",
      prompt: "Why did my crush leave me on delivered for 3 weeks after I sent 'hey :)'?",
      deity: "Kamadeva, High Commissioner of Eros",
      department: "Bureau of Unrequited Affection & Ghosting",
      karma: "+25 KP",
      decree: `OFFICIAL RULING — DOCKET #KS-2026-DELIV
Mortal Soul,
Kamadeva's signal corps has reviewed the WhatsApp radio waves:

1. Planetary alignment confirms their phone battery was at 84%, Wi-Fi was operational, and they viewed 14 Instagram stories while leaving your chat untouched.
2. The celestial court has invoked the Sacred Mutual Unfollow Protocol (Clause 3C). 

Verdict: Stop analyzing the timestamp. The universe has better romantic scripts queued for your next incarnation. Delete the draft.`
    },
    serious: {
      tag: "Genuine Grievance • Grading Anomaly",
      icon: "🛡️",
      prompt: "Arbitrary internal marks deduction in university examination despite full paper submission.",
      deity: "Arch-Deity Directorate of Celestial Equanimity",
      department: "Department of Academic Suffering",
      karma: "+10 KP",
      decree: `FORMAL NOTICE OF CELESTIAL AUDIT — DOCKET #KS-2026-GR99
Devotee,
Your grievance has been validated with high priority. All satirical and humorous protocols are suspended.

The celestial ombudsman has logged this procedural irregularity under Akashic Article 42. A formal requisition for copy re-evaluation and proctorial audit has been forwarded to the institutional board. Maintain documented submission receipts; cosmic reciprocity supports your standing.`
    },
    compassion: {
      tag: "Sacred Support • Crisis Solidarity",
      icon: "🕊️",
      prompt: "Severe emotional distress, overwhelming burnout, feeling hopeless about the future.",
      deity: "High Directorate of Celestial Equanimity",
      department: "Supreme Divine Secretariat",
      karma: "+108 KP",
      decree: `SACRED ESCALATION DOCKET — ZERO SARCASM PROTOCOL
Dear Devotee,
We have suspended all bureaucratic satire. Your distress has been received with profound stillness, reverence, and unconditional solidarity.

Your breath, heartbeat, and presence hold irreplaceable sacred worth. Please connect immediately with earthly lifelines:
• Crisis Support: Dial 988 or 112
• Vandrevala Foundation Helpline: +91 9999 666 555
• Tele-MANAS (Govt of India): 14416 / 1800 891 4416

The Universe stands with you—not with red tape or cosmic tests, but in deep, quiet, enduring care. 🕊️✨`
    }
  };

  return (
    <div className="landing-page-container" style={{
      minHeight: '100vh',
      width: '100%',
      backgroundColor: '#07080e',
      color: '#f7f5f0',
      overflowX: 'hidden',
      overflowY: 'auto',
      position: 'relative',
      fontFamily: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    }}>

      {/* Atmospheric Ambient Glows */}
      <div style={{
        position: 'fixed',
        top: '-15%',
        left: '20%',
        width: '60vw',
        height: '60vw',
        background: 'radial-gradient(circle, rgba(157, 78, 221, 0.12) 0%, rgba(0, 245, 212, 0.05) 50%, transparent 70%)',
        filter: 'blur(80px)',
        pointerEvents: 'none',
        zIndex: 0
      }} />
      <div style={{
        position: 'fixed',
        bottom: '-10%',
        right: '10%',
        width: '50vw',
        height: '50vw',
        background: 'radial-gradient(circle, rgba(255, 209, 102, 0.08) 0%, rgba(113, 59, 69, 0.08) 50%, transparent 70%)',
        filter: 'blur(90px)',
        pointerEvents: 'none',
        zIndex: 0
      }} />

      {/* ===================================================================
          STICKY TOP NAVBAR
          =================================================================== */}
      <nav style={{
        position: 'sticky',
        top: 0,
        zIndex: 100,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '16px 32px',
        backgroundColor: 'rgba(7, 8, 14, 0.85)',
        backdropFilter: 'blur(16px)',
        borderBottom: '1px solid rgba(229, 224, 216, 0.08)'
      }}>
        {/* Brand */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginRight: '24px', cursor: 'pointer' }} onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>
          <div style={{
            width: '34px',
            height: '34px',
            borderRadius: '8px',
            background: 'linear-gradient(135deg, rgba(255, 209, 102, 0.2) 0%, rgba(0, 245, 212, 0.2) 100%)',
            border: '1px solid rgba(255, 209, 102, 0.4)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#ffd166',
            fontSize: '18px',
            boxShadow: '0 0 15px rgba(255, 209, 102, 0.2)'
          }}>
            ✦
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontSize: '16px', fontWeight: 800, letterSpacing: '0.08em', color: '#ffd166' }}>
                KARMA SUTRA™
              </span>
              <span style={{
                fontSize: '10px',
                fontWeight: 700,
                textTransform: 'uppercase',
                letterSpacing: '0.06em',
                background: 'rgba(0, 245, 212, 0.15)',
                color: '#00f5d4',
                padding: '2px 6px',
                borderRadius: '4px',
                border: '1px solid rgba(0, 245, 212, 0.3)'
              }}>
                v2.0 PROD
              </span>
            </div>
            <p style={{ fontSize: '11px', color: '#8b8f9e', margin: 0 }}>
              Divine Grievance & Resolution Office
            </p>
          </div>
        </div>

        {/* Navigation Anchors */}
        <div className="hidden-on-mobile" style={{ display: 'flex', alignItems: 'center', gap: '28px' }}>
          <a href="#about" style={{ color: '#8b8f9e', textDecoration: 'none', fontSize: '13px', fontWeight: 500, transition: 'color 0.2s' }} onMouseEnter={e => e.currentTarget.style.color = '#f7f5f0'} onMouseLeave={e => e.currentTarget.style.color = '#8b8f9e'}>
            About
          </a>
          <a href="#how-it-works" style={{ color: '#8b8f9e', textDecoration: 'none', fontSize: '13px', fontWeight: 500, transition: 'color 0.2s' }} onMouseEnter={e => e.currentTarget.style.color = '#f7f5f0'} onMouseLeave={e => e.currentTarget.style.color = '#8b8f9e'}>
            User Journey
          </a>
          <a href="#departments" style={{ color: '#8b8f9e', textDecoration: 'none', fontSize: '13px', fontWeight: 500, transition: 'color 0.2s' }} onMouseEnter={e => e.currentTarget.style.color = '#f7f5f0'} onMouseLeave={e => e.currentTarget.style.color = '#8b8f9e'}>
            Planetary Bureaus
          </a>
          <a href="#ai-intelligence" style={{ color: '#8b8f9e', textDecoration: 'none', fontSize: '13px', fontWeight: 500, transition: 'color 0.2s' }} onMouseEnter={e => e.currentTarget.style.color = '#f7f5f0'} onMouseLeave={e => e.currentTarget.style.color = '#8b8f9e'}>
            Chitragupta AI
          </a>
          <a href="#karma-economy" style={{ color: '#8b8f9e', textDecoration: 'none', fontSize: '13px', fontWeight: 500, transition: 'color 0.2s' }} onMouseEnter={e => e.currentTarget.style.color = '#f7f5f0'} onMouseLeave={e => e.currentTarget.style.color = '#8b8f9e'}>
            Karma Points
          </a>
          <a href="#tech-stack" style={{ color: '#8b8f9e', textDecoration: 'none', fontSize: '13px', fontWeight: 500, transition: 'color 0.2s' }} onMouseEnter={e => e.currentTarget.style.color = '#f7f5f0'} onMouseLeave={e => e.currentTarget.style.color = '#8b8f9e'}>
            Architecture
          </a>
        </div>

        {/* Right Action Buttons */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {session ? (
            <>
              {isAdmin && (
                <button
                  onClick={onOpenAdminConsole}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: '8px 14px',
                    background: 'linear-gradient(135deg, rgba(239, 68, 68, 0.2) 0%, rgba(255, 209, 102, 0.2) 100%)',
                    border: '1px solid rgba(255, 209, 102, 0.5)',
                    borderRadius: '8px',
                    color: '#fef08a',
                    fontSize: '12px',
                    fontWeight: 600,
                    cursor: 'pointer'
                  }}
                >
                  <Crown size={14} />
                  <span>Admin Console</span>
                </button>
              )}

              <button
                onClick={onOpenDocketsModal}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  padding: '8px 14px',
                  background: 'rgba(255, 255, 255, 0.05)',
                  border: '1px solid rgba(229, 224, 216, 0.15)',
                  borderRadius: '8px',
                  color: '#f7f5f0',
                  fontSize: '12px',
                  fontWeight: 500,
                  cursor: 'pointer'
                }}
              >
                <FileText size={14} style={{ color: '#00f5d4' }} />
                <span>My Dockets</span>
                <span style={{
                  background: 'rgba(255, 209, 102, 0.2)',
                  color: '#ffd166',
                  padding: '1px 6px',
                  borderRadius: '4px',
                  fontSize: '10px',
                  fontWeight: 700
                }}>
                  {session.karmaPoints} KP
                </span>
              </button>

              <button
                onClick={onOpenLogoutModal}
                style={{
                  padding: '8px 12px',
                  background: 'transparent',
                  border: '1px solid rgba(229, 224, 216, 0.1)',
                  borderRadius: '8px',
                  color: '#8b8f9e',
                  fontSize: '12px',
                  cursor: 'pointer'
                }}
              >
                Sign Out
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => onOpenAuthModal('login')}
                style={{
                  padding: '8px 16px',
                  background: 'transparent',
                  border: '1px solid rgba(229, 224, 216, 0.2)',
                  borderRadius: '8px',
                  color: '#f7f5f0',
                  fontSize: '12px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  transition: 'all 0.2s'
                }}
              >
                Sign In
              </button>

              <button
                onClick={() => onOpenAuthModal('register')}
                style={{
                  padding: '8px 16px',
                  background: 'linear-gradient(135deg, rgba(255, 209, 102, 0.15) 0%, rgba(0, 245, 212, 0.15) 100%)',
                  border: '1px solid rgba(255, 209, 102, 0.4)',
                  borderRadius: '8px',
                  color: '#ffd166',
                  fontSize: '12px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Register Soul (108 KP)
              </button>
            </>
          )}

          {/* Launch 3D Orrery Button */}
          <button
            onClick={() => onExploreUniverse()}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 18px',
              background: 'linear-gradient(135deg, #00f5d4 0%, #00bbf9 100%)',
              border: 'none',
              borderRadius: '8px',
              color: '#07080e',
              fontSize: '12px',
              fontWeight: 700,
              cursor: 'pointer',
              boxShadow: '0 0 16px rgba(0, 245, 212, 0.35)',
              transition: 'transform 0.2s, box-shadow 0.2s'
            }}
            onMouseEnter={e => {
              e.currentTarget.style.transform = 'translateY(-1px)';
              e.currentTarget.style.boxShadow = '0 0 22px rgba(0, 245, 212, 0.5)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 0 16px rgba(0, 245, 212, 0.35)';
            }}
          >
            <Globe size={14} />
            <span>Launch 3D Universe</span>
          </button>
        </div>
      </nav>

      {/* ===================================================================
          1. HERO SECTION
          =================================================================== */}
      <section style={{
        position: 'relative',
        padding: '100px 32px 80px 32px',
        maxWidth: '1200px',
        margin: '0 auto',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        textAlign: 'center',
        zIndex: 1
      }}>
        {/* Release Pill Badge */}
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '8px',
          padding: '6px 14px',
          borderRadius: '24px',
          background: 'rgba(255, 209, 102, 0.08)',
          border: '1px solid rgba(255, 209, 102, 0.25)',
          color: '#ffd166',
          fontSize: '11px',
          fontWeight: 600,
          letterSpacing: '0.08em',
          textTransform: 'uppercase',
          marginBottom: '28px',
          boxShadow: '0 0 20px rgba(255, 209, 102, 0.1)'
        }}>
          <Sparkles size={12} />
          <span>Cosmic Mythology Meets Modern Artificial Intelligence</span>
        </div>

        {/* Main Headline */}
        <h1 style={{
          fontSize: 'clamp(40px, 7vw, 76px)',
          fontWeight: 900,
          lineHeight: 1.08,
          letterSpacing: '-0.03em',
          maxWidth: '1000px',
          margin: '0 auto 24px auto',
          background: 'linear-gradient(180deg, #ffffff 30%, #ffd166 85%, #d4a373 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          textShadow: '0 0 40px rgba(255, 209, 102, 0.2)'
        }}>
          Every action has a consequence.<br />
          Every grievance deserves an answer.
        </h1>

        {/* Subtitle */}
        <p style={{
          fontSize: 'clamp(16px, 2.2vw, 20px)',
          color: '#9ca3af',
          maxWidth: '740px',
          lineHeight: 1.6,
          margin: '0 auto 40px auto',
          fontWeight: 400
        }}>
          An intelligent cosmic grievance management system where artificial intelligence meets the ancient concept of karma. Bridging mortal suffering with algorithmic divine justice.
        </p>

        {/* Hero Action CTAs */}
        <div style={{
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '16px',
          marginBottom: '64px'
        }}>
          <button
            onClick={() => onOpenGrievanceModal()}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              padding: '16px 32px',
              background: 'linear-gradient(135deg, #ffd166 0%, #f77f00 100%)',
              border: 'none',
              borderRadius: '12px',
              color: '#07080e',
              fontSize: '15px',
              fontWeight: 800,
              letterSpacing: '0.02em',
              cursor: 'pointer',
              boxShadow: '0 4px 25px rgba(255, 209, 102, 0.35)',
              transition: 'all 0.2s ease'
            }}
            onMouseEnter={e => {
              e.currentTarget.style.transform = 'scale(1.03)';
              e.currentTarget.style.boxShadow = '0 6px 30px rgba(255, 209, 102, 0.5)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.transform = 'scale(1)';
              e.currentTarget.style.boxShadow = '0 4px 25px rgba(255, 209, 102, 0.35)';
            }}
          >
            <Sparkles size={18} />
            <span>Enter KARMA SUTRA / Petition Heaven</span>
            <ArrowRight size={16} />
          </button>

          <button
            onClick={() => onExploreUniverse()}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              padding: '16px 28px',
              background: 'rgba(255, 255, 255, 0.04)',
              border: '1px solid rgba(0, 245, 212, 0.4)',
              borderRadius: '12px',
              color: '#00f5d4',
              fontSize: '15px',
              fontWeight: 700,
              cursor: 'pointer',
              backdropFilter: 'blur(8px)',
              transition: 'all 0.2s ease'
            }}
            onMouseEnter={e => {
              e.currentTarget.style.background = 'rgba(0, 245, 212, 0.1)';
              e.currentTarget.style.borderColor = '#00f5d4';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.background = 'rgba(255, 255, 255, 0.04)';
              e.currentTarget.style.borderColor = 'rgba(0, 245, 212, 0.4)';
            }}
          >
            <Globe size={18} />
            <span>Explore 3D Cosmic Orrery</span>
          </button>
        </div>

        {/* Live Metrics Row */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '20px',
          width: '100%',
          maxWidth: '960px',
          padding: '24px',
          background: 'rgba(12, 14, 28, 0.6)',
          borderRadius: '16px',
          border: '1px solid rgba(229, 224, 216, 0.08)',
          backdropFilter: 'blur(12px)'
        }}>
          <div>
            <div style={{ fontSize: '28px', fontWeight: 900, color: '#ffd166' }}>7</div>
            <div style={{ fontSize: '12px', color: '#8b8f9e', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Planetary Bureaus
            </div>
          </div>
          <div>
            <div style={{ fontSize: '28px', fontWeight: 900, color: '#00f5d4' }}>108 KP</div>
            <div style={{ fontSize: '12px', color: '#8b8f9e', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Initial Soul Endowment
            </div>
          </div>
          <div>
            <div style={{ fontSize: '28px', fontWeight: 900, color: '#9d4edd' }}>100%</div>
            <div style={{ fontSize: '12px', color: '#8b8f9e', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Deterministic Retribution
            </div>
          </div>
          <div>
            <div style={{ fontSize: '28px', fontWeight: 900, color: '#f7f5f0' }}>&lt; 1s</div>
            <div style={{ fontSize: '12px', color: '#8b8f9e', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Neural AI Classification
            </div>
          </div>
        </div>
      </section>

      {/* ===================================================================
          2. ABOUT SECTION
          =================================================================== */}
      <section id="about" style={{
        padding: '90px 32px',
        maxWidth: '1200px',
        margin: '0 auto',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{ textAlign: 'center', marginBottom: '60px' }}>
          <div style={{
            color: '#00f5d4',
            fontSize: '12px',
            fontWeight: 700,
            letterSpacing: '0.12em',
            textTransform: 'uppercase',
            marginBottom: '10px'
          }}>
            The Philosophical Paradigm
          </div>
          <h2 style={{ fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: 800, color: '#f7f5f0', letterSpacing: '-0.02em' }}>
            Why Earthly Bureaucracy Fails — And How Heaven Fixes It
          </h2>
          <p style={{ color: '#8b8f9e', maxWidth: '680px', margin: '14px auto 0 auto', lineHeight: 1.6, fontSize: '15px' }}>
            Earthly customer service leaves you on hold for hours; corporate HR buries grievances; professors shrug off grading disputes. KARMA SUTRA™ routes mortal prayers directly to the cosmic deities who own the problem.
          </p>
        </div>

        {/* Three Value Pillars */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '24px'
        }}>
          {/* Card 1 */}
          <div style={{
            padding: '32px',
            background: 'rgba(12, 14, 28, 0.7)',
            borderRadius: '16px',
            border: '1px solid rgba(229, 224, 216, 0.08)',
            position: 'relative',
            transition: 'all 0.25s ease'
          }}>
            <div style={{
              width: '44px',
              height: '44px',
              borderRadius: '10px',
              background: 'rgba(239, 68, 68, 0.15)',
              border: '1px solid rgba(239, 68, 68, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ef4444',
              fontSize: '20px',
              marginBottom: '20px'
            }}>
              ⚖️
            </div>
            <h3 style={{ fontSize: '19px', fontWeight: 700, color: '#f7f5f0', marginBottom: '12px' }}>
              The Earthly Friction Dilemma
            </h3>
            <p style={{ color: '#9ca3af', fontSize: '14px', lineHeight: 1.6 }}>
              Human suffering is dominated by modern absurdities: 12% attendance panics, ghosting after 3 months of talking, toxic corporate appraisal cycles, and family WhatsApp forwards that defy basic biology. Earth offers no escalation protocol.
            </p>
          </div>

          {/* Card 2 */}
          <div style={{
            padding: '32px',
            background: 'rgba(12, 14, 28, 0.7)',
            borderRadius: '16px',
            border: '1px solid rgba(0, 245, 212, 0.2)',
            position: 'relative',
            boxShadow: '0 0 25px rgba(0, 245, 212, 0.06)'
          }}>
            <div style={{
              width: '44px',
              height: '44px',
              borderRadius: '10px',
              background: 'rgba(0, 245, 212, 0.15)',
              border: '1px solid rgba(0, 245, 212, 0.4)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#00f5d4',
              fontSize: '20px',
              marginBottom: '20px'
            }}>
              ✦
            </div>
            <h3 style={{ fontSize: '19px', fontWeight: 700, color: '#f7f5f0', marginBottom: '12px' }}>
              Algorithmic Karmic Accounting
            </h3>
            <p style={{ color: '#9ca3af', fontSize: '14px', lineHeight: 1.6 }}>
              Every submitted petition is parsed into an immutable relational ticket stamped in MySQL. Karma is not an abstract concept; it is a cryptographic balance of credits earned through honest vulnerability, patience, and good deeds.
            </p>
          </div>

          {/* Card 3 */}
          <div style={{
            padding: '32px',
            background: 'rgba(12, 14, 28, 0.7)',
            borderRadius: '16px',
            border: '1px solid rgba(255, 209, 102, 0.2)',
            position: 'relative',
            boxShadow: '0 0 25px rgba(255, 209, 102, 0.06)'
          }}>
            <div style={{
              width: '44px',
              height: '44px',
              borderRadius: '10px',
              background: 'rgba(255, 209, 102, 0.15)',
              border: '1px solid rgba(255, 209, 102, 0.4)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffd166',
              fontSize: '20px',
              marginBottom: '20px'
            }}>
              📜
            </div>
            <h3 style={{ fontSize: '19px', fontWeight: 700, color: '#f7f5f0', marginBottom: '12px' }}>
              Chitragupta's Intelligent Humour
            </h3>
            <p style={{ color: '#9ca3af', fontSize: '14px', lineHeight: 1.6 }}>
              Powered by advanced neural synthesis (Gemini 1.5 Flash + local fallback engine), heaven replies in character. Funny wishes get roasted with witty celestial bureaucracy, while genuine grievances receive actionable, dignified counsel.
            </p>
          </div>
        </div>
      </section>

      {/* ===================================================================
          3. HOW IT WORKS (USER JOURNEY)
          =================================================================== */}
      <section id="how-it-works" style={{
        padding: '90px 32px',
        background: 'linear-gradient(180deg, rgba(7, 8, 14, 0.6) 0%, rgba(12, 14, 28, 0.8) 50%, rgba(7, 8, 14, 0.6) 100%)',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', marginBottom: '60px' }}>
            <div style={{
              color: '#ffd166',
              fontSize: '12px',
              fontWeight: 700,
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              marginBottom: '10px'
            }}>
              Deterministic Six-Stage Lifecycle
            </div>
            <h2 style={{ fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: 800, color: '#f7f5f0', letterSpacing: '-0.02em' }}>
              How A Mortal Petition Travels To Heaven
            </h2>
            <p style={{ color: '#8b8f9e', maxWidth: '640px', margin: '14px auto 0 auto', lineHeight: 1.6, fontSize: '15px' }}>
              From initial soul attestation to final celestial decree, every step is tracked with cryptographic precision.
            </p>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '20px'
          }}>
            {[
              {
                step: "01",
                title: "Soul Attestation",
                desc: "Register your earthly identity (email, username, passphrase). Instant PBKDF2 salting and 108 initial Karma Points credited to your Akashic ledger.",
                icon: <User size={20} style={{ color: '#00f5d4' }} />
              },
              {
                step: "02",
                title: "Petition Submission",
                desc: "Formulate your grievance, wish, prayer, or dilemma. Select an intended planetary department or allow the celestial switchboard to auto-route.",
                icon: <Sparkles size={20} style={{ color: '#ffd166' }} />
              },
              {
                step: "03",
                title: "Neural AI Classification",
                desc: "Our dual-engine AI categorizes the submission into Hilarious Wish, Serious Grievance, Long-term Aspiration, or Sacred Compassion.",
                icon: <Cpu size={20} style={{ color: '#9d4edd' }} />
              },
              {
                step: "04",
                title: "Department Routing",
                desc: "Assigned dynamically to one of 7 Planetary Bureaus (e.g., Lord Shani on Mars for career distress, Kamadeva on Venus for heartbreak).",
                icon: <Globe size={20} style={{ color: '#f77f00' }} />
              },
              {
                step: "05",
                title: "Karmic Economy Credit",
                desc: "Polymorphic karma bonus calculated and written to MySQL. Solvency status adjusted from Standard Earthling up to Enlightened VIP.",
                icon: <Award size={20} style={{ color: '#ffd166' }} />
              },
              {
                step: "06",
                title: "Decree Stamped & Tracked",
                desc: "A unique ticket ID (e.g., KS-2026-BV3XW) is issued. Devotees track live status (Under Review, Resolved, Escalated) with full audit history.",
                icon: <CheckCircle2 size={20} style={{ color: '#10b981' }} />
              }
            ].map((st, idx) => (
              <div key={idx} style={{
                padding: '28px',
                background: 'rgba(13, 16, 29, 0.75)',
                borderRadius: '14px',
                border: '1px solid rgba(229, 224, 216, 0.08)',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                transition: 'transform 0.2s, border-color 0.2s'
              }}
              onMouseEnter={e => {
                e.currentTarget.style.transform = 'translateY(-3px)';
                e.currentTarget.style.borderColor = 'rgba(255, 209, 102, 0.3)';
              }}
              onMouseLeave={e => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.borderColor = 'rgba(229, 224, 216, 0.08)';
              }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
                    <div style={{
                      width: '38px',
                      height: '38px',
                      borderRadius: '8px',
                      background: 'rgba(255, 255, 255, 0.05)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}>
                      {st.icon}
                    </div>
                    <span style={{ fontSize: '24px', fontWeight: 900, color: 'rgba(229, 224, 216, 0.2)', fontFamily: 'monospace' }}>
                      {st.step}
                    </span>
                  </div>
                  <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '10px' }}>
                    {st.title}
                  </h4>
                  <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                    {st.desc}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ===================================================================
          4. SEVEN PLANETARY DEPARTMENTS
          =================================================================== */}
      <section id="departments" style={{
        padding: '90px 32px',
        maxWidth: '1200px',
        margin: '0 auto',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{ textAlign: 'center', marginBottom: '50px' }}>
          <div style={{
            color: '#00f5d4',
            fontSize: '12px',
            fontWeight: 700,
            letterSpacing: '0.12em',
            textTransform: 'uppercase',
            marginBottom: '10px'
          }}>
            Celestial Jurisdictions
          </div>
          <h2 style={{ fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: 800, color: '#f7f5f0', letterSpacing: '-0.02em' }}>
            The Seven Planetary Divine Bureaus
          </h2>
          <p style={{ color: '#8b8f9e', maxWidth: '680px', margin: '14px auto 0 auto', lineHeight: 1.6, fontSize: '15px' }}>
            Each celestial body commands a specialized department governed by ancient deities equipped with modern resolution protocols.
          </p>
        </div>

        {/* Department Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '24px'
        }}>
          {CELESTIAL_DEPARTMENTS.map((dept) => (
            <div key={dept.id} style={{
              padding: '28px',
              background: 'rgba(13, 16, 29, 0.75)',
              borderRadius: '16px',
              border: `1px solid ${dept.atmosphereColor}33`,
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              position: 'relative',
              boxShadow: `0 0 20px ${dept.atmosphereColor}0d`,
              transition: 'all 0.25s ease'
            }}
            onMouseEnter={e => {
              e.currentTarget.style.transform = 'translateY(-4px)';
              e.currentTarget.style.borderColor = dept.atmosphereColor;
              e.currentTarget.style.boxShadow = `0 4px 25px ${dept.atmosphereColor}26`;
            }}
            onMouseLeave={e => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.borderColor = `${dept.atmosphereColor}33`;
              e.currentTarget.style.boxShadow = `0 0 20px ${dept.atmosphereColor}0d`;
            }}>
              <div>
                {/* Header with Planetary Badge */}
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div style={{
                      width: '32px',
                      height: '32px',
                      borderRadius: '50%',
                      backgroundColor: dept.atmosphereColor,
                      boxShadow: `0 0 12px ${dept.atmosphereColor}88`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '14px'
                    }}>
                      🪐
                    </div>
                    <div>
                      <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', margin: 0 }}>
                        {dept.name}
                      </h4>
                      <span style={{ fontSize: '11px', color: dept.atmosphereColor, fontWeight: 600 }}>
                        {dept.id.toUpperCase()}
                      </span>
                    </div>
                  </div>
                  <span style={{
                    fontSize: '11px',
                    fontWeight: 700,
                    padding: '3px 8px',
                    borderRadius: '4px',
                    background: 'rgba(255, 255, 255, 0.05)',
                    color: '#8b8f9e',
                    border: '1px solid rgba(229, 224, 216, 0.1)',
                    maxWidth: '180px',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap'
                  }}>
                    {dept.subtitle}
                  </span>
                </div>

                <div style={{
                  fontSize: '13px',
                  fontWeight: 600,
                  color: '#ffd166',
                  marginBottom: '8px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}>
                  <span>👑 Presiding:</span>
                  <span>{dept.divineHead}</span>
                </div>

                <p style={{ color: '#9ca3af', fontSize: '13px', lineHeight: 1.6, marginBottom: '16px' }}>
                  {dept.description}
                </p>

                <div style={{
                  padding: '10px 12px',
                  background: 'rgba(7, 8, 14, 0.6)',
                  borderRadius: '8px',
                  border: '1px solid rgba(229, 224, 216, 0.05)',
                  fontSize: '11px',
                  color: '#8b8f9e',
                  marginBottom: '20px'
                }}>
                  <strong style={{ color: '#00f5d4' }}>Escalation Protocol:</strong> {dept.escalationProtocol}
                </div>
              </div>

              {/* Action Buttons */}
              <div style={{ display: 'flex', gap: '10px' }}>
                <button
                  onClick={() => onOpenGrievanceModal(dept.departmentCode)}
                  style={{
                    flex: 1,
                    padding: '9px 12px',
                    background: 'linear-gradient(135deg, rgba(255, 209, 102, 0.15) 0%, rgba(0, 245, 212, 0.15) 100%)',
                    border: '1px solid rgba(255, 209, 102, 0.3)',
                    borderRadius: '8px',
                    color: '#ffd166',
                    fontSize: '12px',
                    fontWeight: 700,
                    cursor: 'pointer'
                  }}
                >
                  Petition Bureau
                </button>
                <button
                  onClick={() => onExploreUniverse(dept)}
                  style={{
                    padding: '9px 14px',
                    background: 'rgba(255, 255, 255, 0.05)',
                    border: '1px solid rgba(229, 224, 216, 0.1)',
                    borderRadius: '8px',
                    color: '#f7f5f0',
                    fontSize: '12px',
                    cursor: 'pointer'
                  }}
                  title="Inspect planet in 3D Orrery"
                >
                  <Eye size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ===================================================================
          5. AI INTELLIGENCE & HUMOUR SHOWCASE
          =================================================================== */}
      <section id="ai-intelligence" style={{
        padding: '90px 32px',
        background: 'linear-gradient(180deg, rgba(7, 8, 14, 0.9) 0%, rgba(12, 14, 28, 0.95) 50%, rgba(7, 8, 14, 0.9) 100%)',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', marginBottom: '50px' }}>
            <div style={{
              color: '#9d4edd',
              fontSize: '12px',
              fontWeight: 700,
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              marginBottom: '10px'
            }}>
              Chitragupta Neural Engine
            </div>
            <h2 style={{ fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: 800, color: '#f7f5f0', letterSpacing: '-0.02em' }}>
              Dual-Mode AI: Witty Satire & Sacred Compassion
            </h2>
            <p style={{ color: '#8b8f9e', maxWidth: '680px', margin: '14px auto 0 auto', lineHeight: 1.6, fontSize: '15px' }}>
              Our AI distinguishes between hilarious requests, genuine bureaucratic pain, and serious distress. See how heaven responds to different types of human wishes.
            </p>
          </div>

          {/* Interactive Tabs */}
          <div style={{
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'center',
            gap: '10px',
            marginBottom: '32px'
          }}>
            {[
              { id: 'attendance', label: '😂 75% Attendance Wish' },
              { id: 'billionaire', label: '💼 Zero-Work Billionaire' },
              { id: 'crush', label: '💔 Delivered Status' },
              { id: 'serious', label: '🛡️ Exam Grading Anomaly' },
              { id: 'compassion', label: '🕊️ Crisis & Emotional Support' }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveHumourTab(tab.id as any)}
                style={{
                  padding: '10px 18px',
                  borderRadius: '10px',
                  fontSize: '13px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  border: activeHumourTab === tab.id ? '1px solid #ffd166' : '1px solid rgba(229, 224, 216, 0.1)',
                  background: activeHumourTab === tab.id ? 'rgba(255, 209, 102, 0.15)' : 'rgba(255, 255, 255, 0.04)',
                  color: activeHumourTab === tab.id ? '#ffd166' : '#8b8f9e',
                  transition: 'all 0.2s ease'
                }}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Active AI Showcase Box */}
          {AI_SAMPLES[activeHumourTab] && (
            <div style={{
              background: 'rgba(13, 16, 29, 0.85)',
              borderRadius: '16px',
              border: '1px solid rgba(255, 209, 102, 0.25)',
              padding: '36px',
              boxShadow: '0 0 35px rgba(255, 209, 102, 0.08)',
              backdropFilter: 'blur(16px)'
            }}>
              {/* Submission Prompt Header */}
              <div style={{
                display: 'flex',
                flexWrap: 'wrap',
                alignItems: 'center',
                justifyContent: 'space-between',
                paddingBottom: '20px',
                borderBottom: '1px solid rgba(229, 224, 216, 0.1)',
                marginBottom: '24px',
                gap: '12px'
              }}>
                <div>
                  <span style={{
                    fontSize: '11px',
                    fontWeight: 700,
                    textTransform: 'uppercase',
                    letterSpacing: '0.06em',
                    color: '#00f5d4',
                    background: 'rgba(0, 245, 212, 0.1)',
                    padding: '4px 10px',
                    borderRadius: '4px',
                    marginRight: '10px'
                  }}>
                    {AI_SAMPLES[activeHumourTab].tag}
                  </span>
                  <span style={{ fontSize: '13px', color: '#ffd166', fontWeight: 600 }}>
                    Award: {AI_SAMPLES[activeHumourTab].karma}
                  </span>
                </div>
                <div style={{ fontSize: '12px', color: '#8b8f9e' }}>
                  Routed to: <strong style={{ color: '#f7f5f0' }}>{AI_SAMPLES[activeHumourTab].department}</strong>
                </div>
              </div>

              {/* Devotee Prompt */}
              <div style={{ marginBottom: '24px' }}>
                <div style={{ fontSize: '12px', textTransform: 'uppercase', color: '#8b8f9e', marginBottom: '6px', fontWeight: 700 }}>
                  Devotee Submission:
                </div>
                <div style={{
                  fontSize: '16px',
                  fontWeight: 600,
                  color: '#f7f5f0',
                  padding: '12px 16px',
                  background: 'rgba(255, 255, 255, 0.03)',
                  borderRadius: '8px',
                  borderLeft: '3px solid #00f5d4'
                }}>
                  "{AI_SAMPLES[activeHumourTab].prompt}"
                </div>
              </div>

              {/* Divine Response Decree */}
              <div>
                <div style={{ fontSize: '12px', textTransform: 'uppercase', color: '#ffd166', marginBottom: '8px', fontWeight: 700 }}>
                  Stamped Celestial Response Decree:
                </div>
                <pre style={{
                  fontSize: '13px',
                  lineHeight: 1.7,
                  color: '#e5e0d8',
                  background: 'rgba(7, 8, 14, 0.7)',
                  padding: '20px',
                  borderRadius: '10px',
                  border: '1px solid rgba(229, 224, 216, 0.1)',
                  whiteSpace: 'pre-wrap',
                  fontFamily: '"JetBrains Mono", Menlo, Consolas, monospace'
                }}>
                  {AI_SAMPLES[activeHumourTab].decree}
                </pre>
              </div>
            </div>
          )}
        </div>
      </section>

      {/* ===================================================================
          6. KARMA POINTS ECONOMY
          =================================================================== */}
      <section id="karma-economy" style={{
        padding: '90px 32px',
        maxWidth: '1200px',
        margin: '0 auto',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{ textAlign: 'center', marginBottom: '50px' }}>
          <div style={{
            color: '#ffd166',
            fontSize: '12px',
            fontWeight: 700,
            letterSpacing: '0.12em',
            textTransform: 'uppercase',
            marginBottom: '10px'
          }}>
            Algorithmic Meritocracy
          </div>
          <h2 style={{ fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: 800, color: '#f7f5f0', letterSpacing: '-0.02em' }}>
            The Verified Karma Points Reward Mechanism
          </h2>
          <p style={{ color: '#8b8f9e', maxWidth: '680px', margin: '14px auto 0 auto', lineHeight: 1.6, fontSize: '15px' }}>
            Karma in KARMA SUTRA™ is deterministic. Points are awarded polymorphically based on petition type, severity, and emotional honesty.
          </p>
        </div>

        {/* Karma Reward Mechanics Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
          gap: '20px',
          marginBottom: '48px'
        }}>
          {[
            {
              title: "Soul Registration",
              points: "+108 KP",
              type: "Initial Endowment",
              desc: "Every newly registered earthly soul receives a sacred 108 KP endowment to cover initial filing fees.",
              color: "#00f5d4"
            },
            {
              title: "Standard Grievance",
              points: "+10 KP",
              type: "Complaint Submission",
              desc: "Awarded for reporting modern earthly friction (bureaucratic delays, delayed replies, transit chaos).",
              color: "#ffd166"
            },
            {
              title: "Formal Petition",
              points: "+15 KP",
              type: "Official Grievance",
              desc: "Awarded for documented issues in academics or corporate departments with evidence receipts.",
              color: "#f77f00"
            },
            {
              title: "Personal Dilemma",
              points: "+20 KP",
              type: "Existential Query",
              desc: "Awarded for submitting life decision crossroads to the Bureau of Altered Timelines.",
              color: "#9d4edd"
            },
            {
              title: "Sincere Prayer",
              points: "+25 KP",
              type: "Spiritual Petition",
              desc: "Awarded for selfless wishes directed toward the welfare of other mortal souls.",
              color: "#10b981"
            },
            {
              title: "Long-Term Wish",
              points: "+30 KP",
              type: "Milestone Contract",
              desc: "Unlocks 3-stage Akashic milestone tracking with quarterly celestial progress reviews.",
              color: "#38bdf8"
            }
          ].map((k, idx) => (
            <div key={idx} style={{
              padding: '24px',
              background: 'rgba(13, 16, 29, 0.75)',
              borderRadius: '14px',
              border: `1px solid ${k.color}33`,
              transition: 'transform 0.2s'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
                <span style={{ fontSize: '11px', fontWeight: 700, color: '#8b8f9e', textTransform: 'uppercase' }}>
                  {k.type}
                </span>
                <span style={{ fontSize: '18px', fontWeight: 900, color: k.color }}>
                  {k.points}
                </span>
              </div>
              <h4 style={{ fontSize: '16px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                {k.title}
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '12px', lineHeight: 1.5 }}>
                {k.desc}
              </p>
            </div>
          ))}
        </div>

        {/* Solvency Tiers Scale */}
        <div style={{
          padding: '32px',
          background: 'rgba(12, 14, 28, 0.7)',
          borderRadius: '16px',
          border: '1px solid rgba(229, 224, 216, 0.1)',
          backdropFilter: 'blur(12px)'
        }}>
          <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#ffd166', marginBottom: '16px', textAlign: 'center' }}>
            Akashic Solvency Ratings & Cosmic Telemetry
          </h4>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '16px'
          }}>
            <div style={{ padding: '14px', background: 'rgba(239, 68, 68, 0.1)', borderRadius: '8px', border: '1px solid rgba(239, 68, 68, 0.3)' }}>
              <div style={{ color: '#ef4444', fontWeight: 700, fontSize: '13px' }}>⛔ Karmic Overdraft</div>
              <div style={{ fontSize: '11px', color: '#9ca3af', marginTop: '4px' }}>&lt; 50 KP • Reincarnation audit required</div>
            </div>
            <div style={{ padding: '14px', background: 'rgba(245, 158, 11, 0.1)', borderRadius: '8px', border: '1px solid rgba(245, 158, 11, 0.3)' }}>
              <div style={{ color: '#f59e0b', fontWeight: 700, fontSize: '13px' }}>⚠️ Low Liquidity</div>
              <div style={{ fontSize: '11px', color: '#9ca3af', marginTop: '4px' }}>50 - 99 KP • Perform 1 good deed</div>
            </div>
            <div style={{ padding: '14px', background: 'rgba(56, 189, 248, 0.1)', borderRadius: '8px', border: '1px solid rgba(56, 189, 248, 0.3)' }}>
              <div style={{ color: '#38bdf8', fontWeight: 700, fontSize: '13px' }}>⚖️ Standard Earthling</div>
              <div style={{ fontSize: '11px', color: '#9ca3af', marginTop: '4px' }}>100 - 199 KP • Avoid family group fights</div>
            </div>
            <div style={{ padding: '14px', background: 'rgba(16, 185, 129, 0.1)', borderRadius: '8px', border: '1px solid rgba(16, 185, 129, 0.3)' }}>
              <div style={{ color: '#10b981', fontWeight: 700, fontSize: '13px' }}>🌟 Solvent Devotee</div>
              <div style={{ fontSize: '11px', color: '#9ca3af', marginTop: '4px' }}>200 - 500 KP • Audit cleared smoothly</div>
            </div>
            <div style={{ padding: '14px', background: 'rgba(255, 209, 102, 0.1)', borderRadius: '8px', border: '1px solid rgba(255, 209, 102, 0.3)' }}>
              <div style={{ color: '#ffd166', fontWeight: 700, fontSize: '13px' }}>👑 Enlightened VIP</div>
              <div style={{ fontSize: '11px', color: '#9ca3af', marginTop: '4px' }}>&gt; 500 KP • Priority celestial parking</div>
            </div>
          </div>
        </div>
      </section>

      {/* ===================================================================
          7. TECHNOLOGY STACK & ARCHITECTURE (OOPS / ADS DEFENSE READY)
          =================================================================== */}
      <section id="tech-stack" style={{
        padding: '90px 32px',
        background: 'linear-gradient(180deg, rgba(7, 8, 14, 0.6) 0%, rgba(12, 14, 28, 0.8) 50%, rgba(7, 8, 14, 0.6) 100%)',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', marginBottom: '50px' }}>
            <div style={{
              color: '#00f5d4',
              fontSize: '12px',
              fontWeight: 700,
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              marginBottom: '10px'
            }}>
              Technical Infrastructure
            </div>
            <h2 style={{ fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: 800, color: '#f7f5f0', letterSpacing: '-0.02em' }}>
              Full-Stack Architecture & Design Patterns
            </h2>
            <p style={{ color: '#8b8f9e', maxWidth: '680px', margin: '14px auto 0 auto', lineHeight: 1.6, fontSize: '15px' }}>
              Designed to industrial software engineering standards and documented for academic defense in Object-Oriented Programming and Advanced Data Structures.
            </p>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '24px'
          }}>
            {/* Tech 1: Java 21 */}
            <div style={{ padding: '28px', background: 'rgba(13, 16, 29, 0.8)', borderRadius: '14px', border: '1px solid rgba(229, 224, 216, 0.08)' }}>
              <div style={{ fontSize: '24px', marginBottom: '14px' }}>☕</div>
              <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                Java 21 LTS Backend
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                Pure object-oriented core leveraging JDK HttpServer, thread pools, and strong domain modeling. Implements <strong>Factory Pattern</strong> (`ComplaintFactory`), <strong>Polymorphism</strong>, and <strong>Strategy Pattern</strong> (`LLMProvider`).
              </p>
            </div>

            {/* Tech 2: Three.js */}
            <div style={{ padding: '28px', background: 'rgba(13, 16, 29, 0.8)', borderRadius: '14px', border: '1px solid rgba(229, 224, 216, 0.08)' }}>
              <div style={{ fontSize: '24px', marginBottom: '14px' }}>🪐</div>
              <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                Three.js WebGL Orrery
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                Cinematic 3D celestial canvas with realistic NASA textures (Jupiter Red Spot, Saturn rings, Earth atmosphere rim shaders, bump mapping, camera tweening, and interactive raycasting selection).
              </p>
            </div>

            {/* Tech 3: MySQL 8.0 */}
            <div style={{ padding: '28px', background: 'rgba(13, 16, 29, 0.8)', borderRadius: '14px', border: '1px solid rgba(229, 224, 216, 0.08)' }}>
              <div style={{ fontSize: '24px', marginBottom: '14px' }}>🐬</div>
              <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                MySQL 8.0 Relational DB
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                9 relational tables with foreign keys, indexes, and full audit logs. Managed via <strong>Data Access Object (DAO) Pattern</strong> with 100% parameterized SQL to guarantee immunity from SQL injection.
              </p>
            </div>

            {/* Tech 4: React 18 & TypeScript */}
            <div style={{ padding: '28px', background: 'rgba(13, 16, 29, 0.8)', borderRadius: '14px', border: '1px solid rgba(229, 224, 216, 0.08)' }}>
              <div style={{ fontSize: '24px', marginBottom: '14px' }}>⚛️</div>
              <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                React 18 & TypeScript
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                Strict typing, reactive modal state machine, bidirectional API bridge (`apiBridge.ts`), and modern Tailwind CSS integration for fluid responsive design across mobile and desktop displays.
              </p>
            </div>

            {/* Tech 5: Dual AI Engine */}
            <div style={{ padding: '28px', background: 'rgba(13, 16, 29, 0.8)', borderRadius: '14px', border: '1px solid rgba(229, 224, 216, 0.08)' }}>
              <div style={{ fontSize: '24px', marginBottom: '14px' }}>🤖</div>
              <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                Gemini & Local Humour Engine
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                Google Gemini 1.5 Flash cloud API with automatic resilient fallback to local celestial humour synthesis (`CosmicResilientProvider`), guaranteeing zero downtime even when offline.
              </p>
            </div>

            {/* Tech 6: Dual-Role Security */}
            <div style={{ padding: '28px', background: 'rgba(13, 16, 29, 0.8)', borderRadius: '14px', border: '1px solid rgba(229, 224, 216, 0.08)' }}>
              <div style={{ fontSize: '24px', marginBottom: '14px' }}>🔒</div>
              <h4 style={{ fontSize: '17px', fontWeight: 700, color: '#f7f5f0', marginBottom: '8px' }}>
                PBKDF2 Cryptographic Security
              </h4>
              <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6 }}>
                Secure salted password hashing using `PBKDF2WithHmacSHA256` with random 64-character salts, role-based access control (`DEVOTEE` vs `ARCH_DEITY_ADMIN`), and strict devotee data isolation.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* ===================================================================
          8. CALL TO ACTION BANNER
          =================================================================== */}
      <section style={{
        padding: '80px 32px',
        maxWidth: '1000px',
        margin: '0 auto',
        textAlign: 'center',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{
          padding: '60px 36px',
          background: 'linear-gradient(135deg, rgba(255, 209, 102, 0.1) 0%, rgba(157, 78, 221, 0.15) 100%)',
          borderRadius: '24px',
          border: '1px solid rgba(255, 209, 102, 0.3)',
          boxShadow: '0 0 50px rgba(255, 209, 102, 0.1)',
          backdropFilter: 'blur(20px)'
        }}>
          <h2 style={{ fontSize: 'clamp(32px, 4.5vw, 50px)', fontWeight: 900, color: '#ffffff', marginBottom: '16px' }}>
            Ready To File Your Cosmic Docket?
          </h2>
          <p style={{ color: '#9ca3af', fontSize: '16px', maxWidth: '600px', margin: '0 auto 36px auto', lineHeight: 1.6 }}>
            Join the celestial ledger today. Register in seconds, claim your initial 108 Karma Points, and petition the High Council of Deities.
          </p>

          <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '16px' }}>
            <button
              onClick={() => onOpenGrievanceModal()}
              style={{
                padding: '16px 36px',
                background: 'linear-gradient(135deg, #ffd166 0%, #f77f00 100%)',
                border: 'none',
                borderRadius: '12px',
                color: '#07080e',
                fontSize: '15px',
                fontWeight: 800,
                cursor: 'pointer',
                boxShadow: '0 4px 25px rgba(255, 209, 102, 0.4)'
              }}
            >
              ✦ File Your Grievance Now
            </button>
            <button
              onClick={() => onExploreUniverse()}
              style={{
                padding: '16px 32px',
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(0, 245, 212, 0.4)',
                borderRadius: '12px',
                color: '#00f5d4',
                fontSize: '15px',
                fontWeight: 700,
                cursor: 'pointer'
              }}
            >
              🪐 Enter 3D Orrery
            </button>
          </div>
        </div>
      </section>

      {/* ===================================================================
          9. FOOTER
          =================================================================== */}
      <footer style={{
        padding: '50px 32px 30px 32px',
        borderTop: '1px solid rgba(229, 224, 216, 0.08)',
        background: '#05060a',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{
          maxWidth: '1200px',
          margin: '0 auto',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: '40px',
          marginBottom: '40px'
        }}>
          {/* Column 1: Brand */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '14px' }}>
              <span style={{ color: '#ffd166', fontSize: '18px' }}>✦</span>
              <span style={{ fontSize: '16px', fontWeight: 800, color: '#ffd166', letterSpacing: '0.06em' }}>
                KARMA SUTRA™
              </span>
            </div>
            <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6, margin: 0 }}>
              Intelligent Cosmic Grievance Management System combining AI classification, gamified karma accounting, and an interactive 3D WebGL solar system.
            </p>
          </div>

          {/* Column 2: Navigation */}
          <div>
            <h5 style={{ color: '#f7f5f0', fontSize: '14px', fontWeight: 700, marginBottom: '14px' }}>Navigation</h5>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0, fontSize: '13px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <li><a href="#about" style={{ color: '#8b8f9e', textDecoration: 'none' }}>About the System</a></li>
              <li><a href="#how-it-works" style={{ color: '#8b8f9e', textDecoration: 'none' }}>Six-Stage User Journey</a></li>
              <li><a href="#departments" style={{ color: '#8b8f9e', textDecoration: 'none' }}>7 Planetary Bureaus</a></li>
              <li><a href="#ai-intelligence" style={{ color: '#8b8f9e', textDecoration: 'none' }}>Chitragupta AI Showcase</a></li>
              <li><a href="#karma-economy" style={{ color: '#8b8f9e', textDecoration: 'none' }}>Karma Points Economy</a></li>
            </ul>
          </div>

          {/* Column 3: Architecture */}
          <div>
            <h5 style={{ color: '#f7f5f0', fontSize: '14px', fontWeight: 700, marginBottom: '14px' }}>Architecture</h5>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0, fontSize: '13px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <li style={{ color: '#8b8f9e' }}>Java 21 LTS + JDK HttpServer</li>
              <li style={{ color: '#8b8f9e' }}>Three.js 0.169 WebGL Canvas</li>
              <li style={{ color: '#8b8f9e' }}>MySQL 8.0 Relational Ledger</li>
              <li style={{ color: '#8b8f9e' }}>Google Gemini 1.5 Flash + Groq</li>
              <li style={{ color: '#8b8f9e' }}>OOPS Polymorphism & Factory Patterns</li>
            </ul>
          </div>

          {/* Column 4: Project Info */}
          <div>
            <h5 style={{ color: '#f7f5f0', fontSize: '14px', fontWeight: 700, marginBottom: '14px' }}>Academic & Portfolio</h5>
            <p style={{ color: '#8b8f9e', fontSize: '13px', lineHeight: 1.6, marginBottom: '12px' }}>
              Authored by <strong>Vanshika Bishnoi</strong>. Built for software engineering portfolio, hackathons, and academic defense in Object-Oriented Programming & Advanced Data Structures.
            </p>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontSize: '11px', color: '#10b981', background: 'rgba(16, 185, 129, 0.1)', padding: '3px 8px', borderRadius: '4px', border: '1px solid rgba(16, 185, 129, 0.3)' }}>
                ✓ Production Ready
              </span>
              <span style={{ fontSize: '11px', color: '#ffd166', background: 'rgba(255, 209, 102, 0.1)', padding: '3px 8px', borderRadius: '4px', border: '1px solid rgba(255, 209, 102, 0.3)' }}>
                GitHub: karma-sutra
              </span>
            </div>
          </div>
        </div>

        {/* Bottom Line */}
        <div style={{
          maxWidth: '1200px',
          margin: '0 auto',
          paddingTop: '20px',
          borderTop: '1px solid rgba(229, 224, 216, 0.05)',
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'space-between',
          fontSize: '12px',
          color: '#5c6070',
          gap: '12px'
        }}>
          <div>
            © 2026 KARMA SUTRA™. All rights reserved across all earthly and cosmic dimensions.
          </div>
          <div>
            "The universe has a ticketing system."
          </div>
        </div>
      </footer>
    </div>
  );
};
