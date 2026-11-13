-- ===================================================================
-- KARMA SUTRA™: Divine Complaint Management System
-- Relational Schema & Cosmic Seed Data (MySQL 8.0+)
-- "The universe has a ticketing system."
-- ===================================================================

CREATE DATABASE IF NOT EXISTS karma_sutra_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE karma_sutra_db;

-- 1. USERS & COSMIC DEVOTEES
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'DEVOTEE',
    tier VARCHAR(30) NOT NULL DEFAULT 'FREE_DEVOTEE',
    karma_points INT NOT NULL DEFAULT 108,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_role (role),
    INDEX idx_user_tier (tier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. COSMIC DEPARTMENTS
CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    divine_head VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    escalation_protocol VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. DIVINE COMPLAINTS & PETITIONS (Inheritance Base)
CREATE TABLE IF NOT EXISTS complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(40) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(60) NOT NULL,
    complaint_type VARCHAR(40) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    department_id INT NOT NULL,
    status VARCHAR(60) NOT NULL DEFAULT 'Received by Heaven',
    desired_resolution TEXT,
    is_long_term_wish BOOLEAN NOT NULL DEFAULT FALSE,
    is_escalated BOOLEAN NOT NULL DEFAULT FALSE,
    karma_points_awarded INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaints_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaints_dept FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX idx_complaint_status (status),
    INDEX idx_complaint_category (category),
    INDEX idx_complaint_type (complaint_type),
    INDEX idx_complaint_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. DIVINE RESPONSES (AI & Administrative Council)
CREATE TABLE IF NOT EXISTS complaint_responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    responder_type VARCHAR(40) NOT NULL DEFAULT 'AI_DIVINE_COUNCIL',
    divine_persona VARCHAR(100) NOT NULL,
    response_text TEXT NOT NULL,
    emotional_tone VARCHAR(40) NOT NULL DEFAULT 'NEUTRAL_BUREAUCRATIC',
    iteration_number INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_responses_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    INDEX idx_response_complaint (complaint_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. TICKET STATUS AUDIT TRAIL
CREATE TABLE IF NOT EXISTS complaint_status_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    old_status VARCHAR(60) NOT NULL,
    new_status VARCHAR(60) NOT NULL,
    changed_by_user_id INT NULL,
    remarks TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_history_complaint (complaint_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. KARMA POINTS LEDGER
CREATE TABLE IF NOT EXISTS karma_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount INT NOT NULL,
    transaction_type VARCHAR(40) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_karma_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_karma_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. SIMULATED SUBSCRIPTIONS (Karma Sutra PRO™)
CREATE TABLE IF NOT EXISTS subscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    plan_tier VARCHAR(40) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sub_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. SIMULATED PAYMENTS
CREATE TABLE IF NOT EXISTS simulated_payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    subscription_id INT NULL,
    transaction_reference VARCHAR(64) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    payment_status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    payment_method VARCHAR(50) NOT NULL DEFAULT 'COSMIC_UPI',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
    INDEX idx_payments_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. WISH MILESTONES (For Long-Term Aspirations)
CREATE TABLE IF NOT EXISTS wish_milestones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    target_date DATE NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'PENDING',
    cosmic_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_milestones_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    INDEX idx_milestones_complaint (complaint_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================================
-- SEED DATA: OFFICIAL DIVINE DEPARTMENTS
-- ===================================================================
INSERT INTO departments (code, name, divine_head, description, escalation_protocol)
VALUES
('DEPT_ACAD', 'Department of Academic Suffering', 'Chitragupta of the Bell Curve', 'Handles exam anxiety, grading anomalies, professor arbitrariness, and thesis existentialism.', 'Re-evaluation by the High Council of Proctors'),
('DEPT_CAREER', 'Ministry of Corporate Karmic Balancing', 'Lord Shani, Lead SCRUM Master', 'Manages unread resumes, toxic managers, delayed appraisals, and unfulfilled stock options.', 'Direct appraisal audit via Cosmic HR'),
('DEPT_LOVE', 'Bureau of Unrequited Affection & Heartbreak', 'Kamadeva, Senior Grievance Officer', 'Processes ghosting incidents, unreciprocated romantic feelings, and ill-fated matchmaking.', 'Three-month mandatory detachment sabbatical'),
('DEPT_FAMILY', 'High Tribunal of Domestic Drama', 'Council of Matriarchs & Elders', 'Adjudicates relative comparisons, unsolicited career advice, and wedding guest disputes.', 'Silencing talisman for family WhatsApp groups'),
('DEPT_FIN', 'Divine Revenue Service & Fiscal Penance', 'Kubera''s Special Audit Cell', 'Reviews empty bank accounts, crypto losses, impulse purchases, and rent inflations.', 'Karmic wealth re-allocation lottery'),
('DEPT_KARMA', 'Supreme Department of Retributive Karma', 'Dharmaraja Yama, Chief Justice', 'Tracks instantaneous versus delayed karmic payback against annoying colleagues and rivals.', 'Instant instant-karma requisition (Sub-clause 4B)'),
('DEPT_DESTINY', 'Bureau of Alternate Realities & Life Decisions', 'Lord Brahma, Chief Architect', 'Resolves regret regarding career pivots, missed opportunities, and parallel universe queries.', 'Simulation walkthrough of unchosen paths'),
('DEPT_MISC', 'Cosmic Switchboard & Miscellaneous Petitions', 'Narada Muni, Communication Emissary', 'General prayers, traffic light timing complaints, and weather negotiations.', 'Standard prayer queuing buffer (48 cosmic hours)')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ===================================================================
-- SEED DATA: INITIAL USERS (Password hashes using PBKDF2WithHmacSHA256)
-- Default admin: admin / CosmicAdmin@108
-- Default devotee: devotee1 / Devotee@108
-- ===================================================================
INSERT INTO users (username, email, password_hash, salt, full_name, role, tier, karma_points)
VALUES
('admin', 'admin@karmasutra.cosmic', 'ef211e4f494caea9d2f2cbca9d903f6f1c4915ee2a2b02008f1b63750529ce81', '7f8a9b0c1d2e3f4a', 'Arch-Deity Administrator', 'ARCH_DEITY_ADMIN', 'VIP_DEVOTEE', 9999),
('devotee1', 'arjun.sharma@earth.in', 'a062fb82258ef9cb494a8f9d0c2e36b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4', '1a2b3c4d5e6f7a8b', 'Arjun Sharma', 'DEVOTEE', 'FREE_DEVOTEE', 108)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);
