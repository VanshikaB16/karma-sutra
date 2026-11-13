export interface CelestialBody {
  id: string;
  name: string;
  bodyType: 'star' | 'planet';
  departmentCode: string;
  departmentName: string;
  divineHead: string;
  subtitle: string;
  description: string;
  lore: string;
  escalationProtocol: string;
  backlogCount: number;
  averageResolutionTime: string;
  satisfactionRate: string;
  currentCelestialTransit: string;
  // 3D Rendering Metrics
  radius: number;
  orbitRadius: number;
  orbitSpeed: number;
  rotationSpeed: number;
  axialTilt: number; // in radians
  primaryColor: string;
  atmosphereColor: string;
  emissiveColor?: string;
  hasRings?: boolean;
  ringInnerRadius?: number;
  ringOuterRadius?: number;
  hasClouds?: boolean;
  cloudSpeed?: number;
  textureType: 'sun' | 'venus' | 'earth' | 'mars' | 'jupiter' | 'saturn' | 'neptune';
  recentDockets: {
    ticketId: string;
    subject: string;
    status: string;
    filedAgo: string;
  }[];
}

export const CELESTIAL_DEPARTMENTS: CelestialBody[] = [
  {
    id: 'sun',
    name: 'The Sun (Sol)',
    bodyType: 'star',
    departmentCode: 'DEPT_ADMIN',
    departmentName: 'Supreme Council of Divine Administration',
    divineHead: 'Arch-Deity Central Directorate',
    subtitle: 'Supreme Cosmic Governance & Meta-Appeals',
    description: 'The supreme radiant nexus of cosmic administration where unclassifiable miracles, executive pardons, and universal firmware upgrades are sanctioned.',
    lore: 'Occupies the absolute gravitational zero-point of the system. Petitions here require clearance from at least three avatar-grade deities and a stamped Form 108-A in quintuplicate.',
    escalationProtocol: 'Direct intervention by the Supreme Cosmic Council during total solar eclipses.',
    backlogCount: 4219,
    averageResolutionTime: '1 solar cycle',
    satisfactionRate: '99.4%',
    currentCelestialTransit: 'Corona Expansion Phase — Maximum Cosmic Bandwidth',
    radius: 4.8,
    orbitRadius: 0,
    orbitSpeed: 0,
    rotationSpeed: 0.0015,
    axialTilt: 0.12,
    primaryColor: '#ffb703',
    atmosphereColor: '#ff9e00',
    emissiveColor: '#ff8500',
    textureType: 'sun',
    recentDockets: [
      { ticketId: 'KS-2026-SUN01', subject: 'Emergency intervention request for heat death timeline', status: 'Under Deliberation', filedAgo: '12m ago' },
      { ticketId: 'KS-2026-SUN02', subject: 'Requisition of 4 billion lumens for winter hemisphere', status: 'Approved in Principle', filedAgo: '2h ago' },
      { ticketId: 'KS-2026-SUN03', subject: 'Protest regarding daylight saving time earthly tampering', status: 'Dismissed with Irony', filedAgo: '1d ago' }
    ]
  },
  {
    id: 'venus',
    name: 'Venus',
    bodyType: 'planet',
    departmentCode: 'DEPT_LOVE',
    departmentName: 'Bureau of Unrequited Affection & Heartbreak',
    divineHead: 'Kamadeva, Senior Grievance Officer',
    subtitle: 'Romantic Tribulations & Situationship Arbitration',
    description: 'Adjudicates mortal romantic clownery, ghosting incidents, one-sided emotional investments, and analyzing the philosophical void of lowercase single-letter text replies.',
    lore: 'Atmosphere composed of 96% concentrated carbon dioxide and 4% condensed tears of people who texted "I just think it\'s funny how..." at 2:14 AM.',
    escalationProtocol: 'Mandatory 90-day detachment sabbatical and confiscation of Notes app.',
    backlogCount: 89412,
    averageResolutionTime: '3 heartbreaks',
    satisfactionRate: '41.2%',
    currentCelestialTransit: 'Venus in Retrograde — Do Not Text Your Ex',
    radius: 1.1,
    orbitRadius: 14,
    orbitSpeed: 0.015,
    rotationSpeed: -0.002, // Retrograde rotation
    axialTilt: 3.09,
    primaryColor: '#e07a5f',
    atmosphereColor: '#f4a261',
    textureType: 'venus',
    recentDockets: [
      { ticketId: 'KS-2026-LV88', subject: 'Ex viewed my story 3 seconds after posting', status: 'Under Surveillance', filedAgo: '4m ago' },
      { ticketId: 'KS-2026-LV89', subject: 'Crush used a thumbs up emoji instead of a heart', status: 'Priority Clown Docket', filedAgo: '18m ago' },
      { ticketId: 'KS-2026-LV90', subject: 'Need divine intervention to undo 4 AM paragraph', status: 'Rejected by Fate', filedAgo: '1h ago' }
    ]
  },
  {
    id: 'earth',
    name: 'Earth (Terra)',
    bodyType: 'planet',
    departmentCode: 'DEPT_ACAD',
    departmentName: 'Department of Academic Suffering',
    divineHead: 'Chitragupta of the Bell Curve',
    subtitle: 'Exam Tribulations, Syllabus Panic & Thesis Dread',
    description: 'Adjudicates syllabus cramming crises, professor arbitrariness, bell-curve anomalies, and emergency prayers submitted between 4:00 AM and 8:45 AM on exam day.',
    lore: 'Submitting 42 emergency prayers the night before an exam is officially classified under Celestial Statute 404 as a Distributed Denial of Service (DDoS) attack on heaven.',
    escalationProtocol: 'Re-evaluation by the High Council of Proctors (Option C probability increased by 2.4%).',
    backlogCount: 104280,
    averageResolutionTime: '1 semester',
    satisfactionRate: '58.7%',
    currentCelestialTransit: 'Final Exam Conjunction with Mercury — Revise Chapter 4',
    radius: 1.25,
    orbitRadius: 22,
    orbitSpeed: 0.011,
    rotationSpeed: 0.006,
    axialTilt: 0.41,
    primaryColor: '#2a9d8f',
    atmosphereColor: '#00b4d8',
    hasClouds: true,
    cloudSpeed: 0.008,
    textureType: 'earth',
    recentDockets: [
      { ticketId: 'KS-2026-AC101', subject: 'Professor gave surprise pop quiz on unassigned reading', status: 'Under Investigation', filedAgo: '8m ago' },
      { ticketId: 'KS-2026-AC102', subject: 'Need 1.2% curve to maintain basic earthly honor', status: 'In Cosmic Review', filedAgo: '35m ago' },
      { ticketId: 'KS-2026-AC103', subject: 'Group project member vanished into another dimension', status: 'Subpoena Issued', filedAgo: '3h ago' }
    ]
  },
  {
    id: 'mars',
    name: 'Mars',
    bodyType: 'planet',
    departmentCode: 'DEPT_CAREER',
    departmentName: 'Ministry of Corporate Karmic Balancing',
    divineHead: 'Lord Shani, Lead SCRUM Master',
    subtitle: 'Workplace Torment & Audacious Billionaire Wishes',
    description: 'Manages unread resumes, toxic middle management, 9-hour reel screen times, and delusions of achieving nine-figure net worth strictly from horizontal sofa alignment.',
    lore: 'Presided over by Lord Shani, who sips celestial espresso while auditing requests under Section 0.00 of the Akashic Penal Code (Pure, Unadulterated Audacity).',
    escalationProtocol: 'Immediate 14-minute biological activity order and ATS algorithm override.',
    backlogCount: 67120,
    averageResolutionTime: '4 fiscal quarters',
    satisfactionRate: '63.2%',
    currentCelestialTransit: 'Saturn Square Mars — Maintain Composure in Standup',
    radius: 0.95,
    orbitRadius: 30,
    orbitSpeed: 0.0085,
    rotationSpeed: 0.0055,
    axialTilt: 0.44,
    primaryColor: '#c84b31',
    atmosphereColor: '#e07a5f',
    textureType: 'mars',
    recentDockets: [
      { ticketId: 'KS-2026-CR404', subject: 'I want to be a billionaire without doing anything at all', status: 'Roast Dispatched', filedAgo: '1m ago' },
      { ticketId: 'KS-2026-CR405', subject: 'Manager scheduled 5:30 PM Friday alignment sync', status: 'Karmic Hex Queued', filedAgo: '14m ago' },
      { ticketId: 'KS-2026-CR406', subject: 'Submitted 800 applications with zero replies', status: 'Telemetry Verified', filedAgo: '2h ago' }
    ]
  },
  {
    id: 'jupiter',
    name: 'Jupiter',
    bodyType: 'planet',
    departmentCode: 'DEPT_FAMILY',
    departmentName: 'High Tribunal of Domestic Drama',
    divineHead: 'Council of Cosmic Matriarchs & Elders',
    subtitle: 'Relative Comparisons, Shaadi Taunts & WhatsApp Forwards',
    description: 'Adjudicates unsolicited advice from third-removed aunts, comparisons with Sharma Ji\'s son, and matrimonial interrogation sessions at family weddings.',
    lore: 'The Great Red Spot is a persistent 400-year-old whirlwind of unverified WhatsApp "Good Morning" GIFs with glowing glitter flowers.',
    escalationProtocol: 'Spiritual dampening spell for family WhatsApp group notifications.',
    backlogCount: 52400,
    averageResolutionTime: '2 family reunions',
    satisfactionRate: '51.0%',
    currentCelestialTransit: 'Jupiter in Eleventh House — Smile and Nod Politely',
    radius: 2.8,
    orbitRadius: 42,
    orbitSpeed: 0.0055,
    rotationSpeed: 0.012, // Fast rotation
    axialTilt: 0.05,
    primaryColor: '#d4a373',
    atmosphereColor: '#e9c46a',
    textureType: 'jupiter',
    recentDockets: [
      { ticketId: 'KS-2026-FM71', subject: 'Aunt asked why I am still single in front of 40 guests', status: 'Immunity Granted', filedAgo: '6m ago' },
      { ticketId: 'KS-2026-FM72', subject: 'Father cited Sharma Ji\'s son earning in Singapore dollars', status: 'Docket Audited', filedAgo: '42m ago' },
      { ticketId: 'KS-2026-FM73', subject: 'Accidentally read the family political debate thread', status: 'Purification Ordered', filedAgo: '5h ago' }
    ]
  },
  {
    id: 'saturn',
    name: 'Saturn',
    bodyType: 'planet',
    departmentCode: 'DEPT_KARMA',
    departmentName: 'Supreme Department of Retributive Karma',
    divineHead: 'Dharmaraja Yama, Chief Justice',
    subtitle: 'Retributive Fate, Instant Payback & Cosmic Justice',
    description: 'Tracks instantaneous versus delayed karmic payback against annoying colleagues, rude queue cutters, and people who leave shopping carts in parking spots.',
    lore: 'Saturn\'s majestic rings are composed of crystallized karmic debits and credits in perfect celestial equilibrium. What goes around spins in elliptical orbit.',
    escalationProtocol: 'Instant-Karma Requisition under Sub-clause 4B (Minor toe-stubbing hex).',
    backlogCount: 142850,
    averageResolutionTime: 'Instant to 1 Reincarnation',
    satisfactionRate: '88.9%',
    currentCelestialTransit: 'Transit of Saturn in Pisces — Every Action Has Reaction',
    radius: 2.2,
    orbitRadius: 56,
    orbitSpeed: 0.0038,
    rotationSpeed: 0.011,
    axialTilt: 0.47,
    primaryColor: '#e0c068',
    atmosphereColor: '#ffd166',
    hasRings: true,
    ringInnerRadius: 2.9,
    ringOuterRadius: 5.6,
    textureType: 'saturn',
    recentDockets: [
      { ticketId: 'KS-2026-KM10', subject: 'Car cut me off without signaling on expressway', status: 'Red Light Scheduled', filedAgo: '3m ago' },
      { ticketId: 'KS-2026-KM11', subject: 'Colleague stole lunch marked with my name from office fridge', status: 'Food Poisoning Pending', filedAgo: '22m ago' },
      { ticketId: 'KS-2026-KM12', subject: 'Friend borrowed 500 rupees in 2021 and has amnesia', status: 'Accumulating 18% Karma Interest', filedAgo: '4h ago' }
    ]
  },
  {
    id: 'neptune',
    name: 'Neptune',
    bodyType: 'planet',
    departmentCode: 'DEPT_DESTINY',
    departmentName: 'Bureau of Alternate Realities & Life Decisions',
    divineHead: 'Lord Brahma, Chief Architect',
    subtitle: 'Existential Crossroads, What-Ifs & Quantum Paths',
    description: 'Resolves regret regarding unchosen career pivots, missed romantic chances, and parallel universe simulations where you actually bought Bitcoin in 2011.',
    lore: 'Winds here whip at 2,100 km/h, carrying echoes of every decision you second-guessed in the shower at 11:30 PM.',
    escalationProtocol: 'Parallel simulation walkthrough of alternative timeline (revealing it was equally chaotic).',
    backlogCount: 39800,
    averageResolutionTime: '7 cosmic epiphanies',
    satisfactionRate: '72.5%',
    currentCelestialTransit: 'Neptune in Retrograde — Accept Your Earthly Timeline',
    radius: 1.8,
    orbitRadius: 70,
    orbitSpeed: 0.0026,
    rotationSpeed: 0.007,
    axialTilt: 0.50,
    primaryColor: '#0077b6',
    atmosphereColor: '#48cae4',
    textureType: 'neptune',
    recentDockets: [
      { ticketId: 'KS-2026-DS09', subject: 'Should I quit my job to open an artisanal tea stall in Himachal?', status: 'Simulation Running', filedAgo: '9m ago' },
      { ticketId: 'KS-2026-DS10', subject: 'Wondering if I would be happier if I took arts instead of engineering', status: 'Alternative Timeline Evaluated', filedAgo: '1h ago' },
      { ticketId: 'KS-2026-DS11', subject: 'Regret not asking out the girl in the library 6 years ago', status: 'Parallel Universe Audit Complete', filedAgo: '6h ago' }
    ]
  }
];
