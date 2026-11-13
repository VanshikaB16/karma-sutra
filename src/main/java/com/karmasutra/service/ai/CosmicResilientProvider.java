package com.karmasutra.service.ai;

import com.karmasutra.model.Department;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.Severity;

/**
 * Intelligent Humour Engine & Resilient Local Celestial Synthesis Provider for Karma Sutra™.
 * Classifies petitions into:
 *   1. Genuine grievance
 *   2. Sincere wish or prayer
 *   3. Funny / hilarious wish
 *   4. Unusual or absurd wish
 *   5. Inappropriate submission
 *
 * Generates context-aware, witty, sarcastic-yet-charming cosmic decrees for hilarious and absurd wishes,
 * dignified actionable counsel for genuine grievances, and profound sacred care for sensitive concerns.
 */
public class CosmicResilientProvider implements LLMProvider {

    @Override
    public String getProviderName() {
        return "Karma Sutra™ Intelligent Humour Engine (Local Synthesis)";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public DivineAIResult generateDivineResponse(Complaint complaint, Department department, int iterationNumber) {
        String deptHead = department != null ? department.getDivineHead() : "Chitragupta of the Bell Curve";
        String deptName = department != null ? department.getName() : "Universal Docket Office";
        String subject = complaint.getSubject() != null ? complaint.getSubject() : "";
        String desc = complaint.getDescription() != null ? complaint.getDescription() : "";
        String combined = (subject + " " + desc).toLowerCase();

        ClassificationResult classification = classifySubmission(subject, desc);
        String categoryType = classification.primaryClassification();

        // =========================================================================
        // 1. INAPPROPRIATE / SENSITIVE CRISIS SUBMISSIONS (Zero sarcasm, deep sacred care)
        // =========================================================================
        if ("Inappropriate submission".equals(categoryType)) {
            String compassionateResponse = String.format("""
                OFFICIAL RECORD — SACRED ESCALATION DOCKET #%s
                Presiding Emissary: Arch-Deity Directorate of Celestial Equanimity
                Department: %s

                Dear Devotee,

                We have suspended all routine bureaucratic procedures, sarcasm, and satirical protocols for this matter. Your distress has been received with profound stillness, reverence, and unconditional solidarity.

                While the cosmos moves through exhausting, heavy, and painful cycles, please know you do not have to carry this suffocating weight alone. Your breath, your heartbeat, and your presence hold irreplaceable sacred worth.

                We urge you with all our hearts to reach out immediately to a trusted earthly support system:
                • Crisis Lifeline & Mental Health Support: Dial 988 or 112 (Earthly Emergency)
                • Vandrevala Foundation Helpline: +91 9999 666 555
                • Tele-MANAS (Govt of India): 14416 / 1800 891 4416

                The Universe stands with you—not with red tape or cosmic tests, but in deep, quiet, enduring care. 🕊️✨

                With sacred blessings and cosmic warmth,
                Arch-Deity Directorate
                """, complaint.getTicketNumber(), deptName);

            return new DivineAIResult("Arch-Deity Directorate of Celestial Equanimity", compassionateResponse, "SACRED_COMPASSION", false, "DEPT_MISC");
        }

        // =========================================================================
        // 2. FUNNY / HILARIOUS WISHES (Witty, charming, context-aware cosmic roasting)
        // =========================================================================
        if ("Funny / hilarious wish".equals(categoryType)) {

            // Specific Case 2A: ATTENDANCE WITHOUT ATTENDING
            if (combined.contains("attendance") || (combined.contains("absent") && combined.contains("present")) || combined.contains("bunk")) {
                String attendanceResp = String.format("""
                    OFFICIAL DETERMINATION — DOCKET #%s
                    Authority: Department of Academic Suffering & Attendance Arbitration
                    Presiding Deity: Chitragupta of the Bell Curve, Senior Proctor

                    Dear Devotee, even the Universe cannot mark you present while you're orbiting outside the classroom. Your attendance karma needs serious intervention. 😂

                    Our celestial telemetry scanned your classroom coordinates during the last semester:
                    • Assigned Seat: Empty void vibrating with quantum disappointment.
                    • Actual Location: Canteen periphery, third table from the counter, sipping cold coffee while debating whether a 12%% attendance record can be "rounded up" to 75%%.

                    Chitragupta reviewed Section 75(B) of the Akashic Education Code:
                    "Submitting emergency petitions to alter biometric scanners or forge the professor's signature register is officially classified as Hardware Fraud Against Heaven."

                    If the cosmos intervened to retroactively mark you present, professors across three continents would spontaneously combust into chalk dust.

                    OFFICIAL MANDATORY ACTION:
                    1. Place your biological vessel inside the lecture hall for at least three consecutive Tuesdays.
                    2. Maintain eye contact with the whiteboard for a minimum of 11 continuous minutes.
                    3. Revision remains legal in your earthly jurisdiction. Good luck, Devotee! 🎓🪐

                    Faithfully in Proctorial Duty,
                    Chitragupta of the Bell Curve
                    """, complaint.getTicketNumber());

                return new DivineAIResult("Chitragupta of the Bell Curve", attendanceResp, "HILARIOUS_ROAST", false, "DEPT_ACAD");
            }

            // Specific Case 2B: CRUSH / UNREQUITED LOVE SPELL
            if (combined.contains("crush") || (combined.contains("fall in love") && (combined.contains("make") || combined.contains("force"))) ||
                combined.contains("love me") || combined.contains("like me")) {
                String crushResp = String.format("""
                    OFFICIAL CUPID DISPATCH — DOCKET #%s
                    Authority: Bureau of Unrequited Affection & Situationship Arbitration
                    Presiding Deity: Kamadeva, Senior Grievance Officer

                    The Universe can align planets, Devotee, but free will is a different department. Perhaps start with a hello? 😉

                    Our celestial surveillance unit audited your current romantic operational strategy regarding:
                    "%s"

                    Current Devotee Strategy Telemetry:
                    • 45 minutes: Staring intensely at the back of their head from three tables away in the library.
                    • 1:45 AM: Analyzing the metaphysical significance of them viewing your Instagram story within 4 minutes.
                    • 3:12 AM: Formulating 14 fictional wedding scenarios in your head before remembering they don't even know your name.

                    The High Council of Eros confirms that telepathic flirting has a 0.00%% cosmic success rate across all recorded solar systems.

                    DIVINE ADVISORY DIRECTIVE:
                    1. Cease writing 8-paragraph explanatory essays in your iPhone Notes app.
                    2. Approach them in the physical realm like a normal mortal biped and ask a standard question (e.g. "Do you have a pen?" or "Is this seat taken?").
                    3. Venus supports the brave; the universe does not deliver romantic partners via Amazon Prime. 😂💌

                    Yours in Celestial Romance,
                    Kamadeva
                    Senior Grievance Officer, Bureau of Unrequited Affection
                    """, complaint.getTicketNumber(), subject);

                return new DivineAIResult("Kamadeva, Senior Grievance Officer", crushResp, "HILARIOUS_ROAST", false, "DEPT_LOVE");
            }

            // Specific Case 2C: EXAM DISAPPEARANCE
            if ((combined.contains("exam") || combined.contains("test") || combined.contains("quiz") || combined.contains("paper")) &&
                (combined.contains("disappear") || combined.contains("cancel") || combined.contains("vanish") || combined.contains("delete") || combined.contains("skip"))) {
                String examResp = String.format("""
                    FORENSIC DETERMINATION — DOCKET #%s
                    Authority: High Directorate of Academic Tribulations & Bell Curve Disasters
                    Presiding Deity: Chitragupta of the Bell Curve, Senior Proctor

                    Your wish has reached the Cosmic Examination Department. Unfortunately, the examination board has stronger gravitational forces than we anticipated. Good luck, Devotee. 😂

                    Our Akashic Auditors ran the mathematical simulation of deleting tomorrow's exam:
                    If the examination paper spontaneously vanished, space-time causality dictates that the rescheduled paper would contain 14 surprise sub-questions strictly from the footnote of Chapter 9.

                    A quick review of your pre-exam telemetry reveals:
                    • 4 hours: Watching YouTube shorts titled "How to cover 5 modules in 20 minutes using quantum memory techniques."
                    • 1 hour: Arranging six highlighters in ascending chromatic order.
                    • 12 minutes: Opening the textbook, gasping in horror, and immediately filing this cosmic petition.

                    REMEDIAL MANDATE:
                    Drink a glass of water, turn off the reels, skim the summary slides, and pray that Option (C) carries its historical statistical weight. The exam will not disappear, Devotee, but human resilience is eternal! 📚⚡

                    By Order of the Examination High Council,
                    Chitragupta
                    Chief Proctor of the Holy Gaussian Distribution
                    """, complaint.getTicketNumber());

                return new DivineAIResult("Chitragupta of the Bell Curve", examResp, "HILARIOUS_ROAST", false, "DEPT_ACAD");
            }

            // Specific Case 2D: BILLIONAIRE WITHOUT WORK / PASSIVE WEALTH FANTASIES
            if (combined.contains("billionaire") || combined.contains("millionaire") ||
                (combined.contains("rich") && (combined.contains("nothing") || combined.contains("lazy") || combined.contains("without"))) ||
                combined.contains("money without work") || combined.contains("horizontal")) {
                String billionaireResp = String.format("""
                    OFFICIAL REJECTION ORDER — TICKET #%s
                    Authority: Ministry of Corporate Karmic Balancing & Audacious Opulence
                    Presiding Deity: Lord Shani, Lead SCRUM Master of Cosmic Fate

                    Salutations Petitioner,

                    Lord Shani personally spat his celestial espresso across the Akashic Server Rack upon reading this docket regarding:
                    "%s"

                    Our senior auditors invoked Section 0.00 of the Akashic Penal Code (Pure, Unadulterated Audacity):
                    1. ANCESTRAL TRUST FUND: Null and void. Your great-grandfather neglected to claim ownership of three diamond mines.
                    2. TECH GRIFT FEASIBILITY: Rejected. You have not even bothered to rebrand a standard Google Sheet as a "Self-Evolving Quantum Neural Pipeline" to raise $25M from venture capitalists.
                    3. LOTTERY LOGISTICS: Cancelled. You have not purchased a lottery ticket, as that would require the physical exertion of standing upright.

                    RULING UNDER THE FIRST LAW OF KARMIC THERMODYNAMICS:
                    "Ten-figure net worth cannot spontaneously materialize from horizontal sofa alignment without tearing a hole in the space-time fabric." 😂

                    MANDATORY REMEDIAL ACTION:
                    Engage in at least fourteen (14) consecutive minutes of productive biological effort—such as sitting at a 90-degree angle—before submitting further tickets to the High Council of Delusional Ambitions. 💼☕

                    Yours in cosmic disbelief,
                    Lord Shani, Lead SCRUM Master
                    Ministry of Corporate Karmic Balancing
                    """, complaint.getTicketNumber(), subject);

                return new DivineAIResult("Lord Shani, Lead SCRUM Master", billionaireResp, "HILARIOUS_ROAST", false, "DEPT_CAREER");
            }

            // Specific Case 2E: FOOD / NEGATIVE CALORIE DREAMS
            if (combined.contains("calorie") || combined.contains("pizza") || combined.contains("burger") || combined.contains("eat without gaining") || combined.contains("six pack")) {
                String foodResp = String.format("""
                    METABOLIC DETERMINATION NOTICE — DOCKET #%s
                    Authority: Divine Revenue Service & Fiscal-Metabolic Cell
                    Presiding Deity: Lord Kubera, Auditor General

                    Dear Devotee,

                    Kubera's Metabolism Division has audited your earnest wish regarding:
                    "%s"

                    The Universe can create supernovas, bend light around black holes, and accelerate galaxies, but the First Law of Thermodynamics remains strictly non-negotiable even in the third heaven.

                    Our Akashic sensors note that you ordered double cheese garlic bread at 1:42 AM and followed it with a cosmic petition asking: "Why do carbohydrates cling to my waistline?"

                    Devotee, heaven did not enter your OTP on the food delivery app. 😂

                    DIVINE DISPENSATION:
                    Eat the lonely, disintegrating vegetables currently decomposing in your refrigerator crisper before seeking miraculous metabolic dispensations. A minor karma credit of +10 has been granted for sheer culinary optimism. 🍕🥗

                    Yours in Metabolic Reality,
                    Lord Kubera
                    Auditor General of Vanishing Calories
                    """, complaint.getTicketNumber(), subject);

                return new DivineAIResult("Lord Kubera, Auditor General", foodResp, "HILARIOUS_ROAST", false, "DEPT_FIN");
            }

            // General Hilarious Wish Dynamic Generator (Contextual roasting referencing exact words)
            String wittyGeneralResp = String.format("""
                CELESTIAL BUREAUCRACY DISPATCH — DOCKET #%s
                Authority: Supreme Secretariat of Divine Irony & Audacious Wishes
                Presiding Emissary: %s (%s)

                Salutations Ambitious Devotee,

                Your delightfully absurd request has reached our celestial intake docket:
                "%s"

                Our senior celestial intake officers paused the morning standup meeting just to marvel at the sheer audacity of this petition.

                You asked: "%s"

                Cosmic Evaluation under Subsection 42(a):
                While the Universe deeply admires your creative optimism, granting this request would violate seventeen local laws of physics and cause profound philosophical confusion among earthly philosophers. 😂

                The cosmos has logged your request under "High-Priority Cosmic Comedy". We have awarded you +10 Karma points purely for making the celestial bureaucrats chuckle during peak server load.

                RECOMMENDATION:
                Drink a warm cup of chai, take a deep breath, and remember: if wishes had no friction, Earth would have spun out of orbit three millenniums ago. Keep dreaming, Devotee, but keep your feet on earthly soil! 😉🪐

                With celestial chuckles,
                %s
                Supreme Secretariat of Divine Grievances
                """, complaint.getTicketNumber(), deptHead, deptName, subject, desc, deptHead);

            return new DivineAIResult(deptHead, wittyGeneralResp, "HILARIOUS_ROAST", false, "DEPT_MISC");
        }

        // =========================================================================
        // 3. UNUSUAL OR ABSURD WISHES (Surreal, quantum, bizarre, science-fiction whimsies)
        // =========================================================================
        if ("Unusual or absurd wish".equals(categoryType)) {
            String absurdResp = String.format("""
                QUANTUM ANOMALY DISPATCH — DOCKET #%s
                Authority: Bureau of Alternate Realities & Quantum Curiosities
                Presiding Deity: Lord Brahma, Chief Architect of Multiverses

                Greetings Cosmic Explorer,

                Your profoundly unusual proposal has triggered our deep-space telemetry sensors:
                "%s"

                Our senior quantum architects reviewed your scenario:
                "%s"

                DETERMINATION UNDER MULTIVERSE REGULATION 108:
                We ran a 14-dimensional Monte Carlo simulation across eight parallel timelines:
                1. In Timeline C-137, your proposal was enacted—resulting in local pigeons acquiring Ph.D. degrees in quantum mechanics and immediately demanding collective bargaining rights.
                2. In Timeline K-404, gravity indeed paused for 45 minutes, but everyone spilled their tea simultaneously, causing a worldwide diplomatic crisis.

                CONCLUSION:
                While this wish cannot be deployed in your current planetary build (Earth v2026.4), Lord Brahma personally commends your surreal imagination. The universe was built from pure curiosity, Devotee. Never stop asking the weird questions. 🌀🪐✨

                Stay delightfully strange,
                Lord Brahma, Chief Architect
                Bureau of Alternate Realities & Life Decisions
                """, complaint.getTicketNumber(), subject, desc);

            return new DivineAIResult("Lord Brahma, Chief Architect", absurdResp, "ABSURD_WIT", false, "DEPT_DESTINY");
        }

        // =========================================================================
        // 4. SINCERE WISHES OR PRAYERS (Uplifting, inspiring, reverent affirmation)
        // =========================================================================
        if ("Sincere wish or prayer".equals(categoryType)) {
            String prayerResp = String.format("""
                SACRED BLESSING & ENDORSEMENT — TICKET #%s
                Authority: Supreme Council of Divine Administration & Akashic Harmony
                Presiding Emissary: %s (%s)

                Blessed Devotee,

                Your sincere and noble wish has been inscribed upon the Golden Akashic Registry:
                "%s"

                The Universe does not ignore heartfelt dedication, pure intention, and earnest human longing. Sincere karma creates subtle, enduring gravitational ripples that align circumstance, character, and opportunity over time.

                DIVINE ENCOURAGEMENT:
                Continue pouring honest earthly labor, patience, and compassion into this path. Trust in the quiet rhythm of the universe—what is planted with clean intent bears fruit in its proper cosmic season.

                You have been endowed with a divine blessing of clarity, inner resilience, and peace. May auspicious celestial transits guide every step of your earthly journey. ✨🙏

                With Eternal Cosmic Blessings,
                %s
                Supreme Council of Divine Administration
                """, complaint.getTicketNumber(), deptHead, deptName, subject, deptHead);

            return new DivineAIResult(deptHead, prayerResp, "SINCERE_BLESSING", true, "DEPT_ADMIN");
        }

        // =========================================================================
        // 5. GENUINE GRIEVANCE (Empathetic, serious, constructive, structured determination)
        // =========================================================================
        String grievanceResp = String.format("""
            OFFICIAL ADJUDICATION DETERMINATION — DOCKET #%s
            Authority: %s
            Presiding Deity: %s

            Salutations Petitioner,

            Your formal grievance concerning:
            "%s"
            has been audited, indexed, and accepted into active cosmic adjudication.

            DETAILED STATEMENT AUDITED:
            "%s"

            ADJUDICATION FINDINGS:
            The Ministry confirms that the earthly friction you are experiencing violates basic principles of fairness and equilibrium. The administrative bureaucracy of mortal institutions can feel maddeningly indifferent, but your grievance has been registered on the permanent Akashic Record.

            ACTIONABLE STEPS ORDERED:
            1. DOCUMENTATION: Maintain clear, contemporaneous records of all earthly communications regarding this matter.
            2. KARMIC SHIELD: A protective karma buffer has been assigned to safeguard your peace of mind against further toxic escalations.
            3. CELESTIAL AUDIT: The opposing parties have been slated for karmic reconciliation under Section 4(B) of the Law of Moral Retribution.

            Stand tall, maintain your composure, and proceed with firm earthly integrity. The Universe has stamped your docket. ⚖️📜

            By Order of the Directorate,
            %s
            %s
            """, complaint.getTicketNumber(), deptName, deptHead, subject, desc, deptHead, deptName);

        return new DivineAIResult(deptHead, grievanceResp, "GENUINE_COUNSEL", false, department != null ? department.getCode() : "DEPT_MISC");
    }

    @Override
    public ClassificationResult classifySubmission(String subject, String description) {
        String sub = subject != null ? subject.toLowerCase() : "";
        String desc = description != null ? description.toLowerCase() : "";
        String combined = (sub + " " + desc).trim();

        // 1. INAPPROPRIATE SUBMISSIONS (Crisis, self-harm, severe trauma, harassment)
        if (combined.contains("suicide") || combined.contains("kill myself") || combined.contains("end my life") ||
            combined.contains("die") || combined.contains("abuse") || combined.contains("grief") ||
            combined.contains("hopeless") || combined.contains("severe depression")) {
            return new ClassificationResult("Inappropriate submission", false, ComplaintCategory.OTHER_DIVINE_MATTERS, Severity.COSMIC_CRITICAL, "COMPASSIONATE_CARE", 1);
        }

        // 2. FUNNY / HILARIOUS WISHES
        boolean isHilarious =
            // Attendance jokes
            combined.contains("attendance") || (combined.contains("class") && (combined.contains("without") || combined.contains("bunk"))) ||
            // Crush wishes
            combined.contains("crush") || (combined.contains("love") && (combined.contains("fall in love") || combined.contains("make them") || combined.contains("force"))) ||
            // Exam disappearance
            ((combined.contains("exam") || combined.contains("test") || combined.contains("quiz")) &&
             (combined.contains("disappear") || combined.contains("vanish") || combined.contains("cancel") || combined.contains("delete"))) ||
            // Billionaire / lazy rich
            combined.contains("billionaire without") || combined.contains("rich without") || combined.contains("money for nothing") ||
            combined.contains("sofa") || combined.contains("couch") ||
            // Food / calorie dreams
            combined.contains("calorie") || combined.contains("eat without gaining") || (combined.contains("pizza") && combined.contains("abs")) ||
            // Humorous words
            combined.contains("cheat") || combined.contains("magic shortcut") || combined.contains("clone myself") ||
            combined.contains("homework do itself") || combined.contains("ex like my photo");

        if (isHilarious) {
            ComplaintCategory cat = ComplaintCategory.CAREER_AND_UNEMPLOYMENT;
            if (combined.contains("exam") || combined.contains("attendance") || combined.contains("class")) {
                cat = ComplaintCategory.ACADEMIC_SUFFERING;
            } else if (combined.contains("crush") || combined.contains("love") || combined.contains("ex")) {
                cat = ComplaintCategory.LOVE_AND_RELATIONSHIPS;
            } else if (combined.contains("calorie") || combined.contains("pizza") || combined.contains("money")) {
                cat = ComplaintCategory.FINANCIAL_DIFFICULTIES;
            }
            return new ClassificationResult("Funny / hilarious wish", false, cat, Severity.MEDIUM, "WITTY_ROAST", 1);
        }

        // 3. UNUSUAL OR ABSURD WISHES
        boolean isAbsurd =
            combined.contains("alien") || combined.contains("pigeon") || combined.contains("talk to animals") ||
            combined.contains("reverse gravity") || combined.contains("teleport") || combined.contains("time machine") ||
            combined.contains("multiverse") || combined.contains("portal") || combined.contains("space time") ||
            combined.contains("invisible") || combined.contains("superpower") || combined.contains("fly to the moon for samosas");

        if (isAbsurd) {
            return new ClassificationResult("Unusual or absurd wish", true, ComplaintCategory.DESTINY_AND_LIFE_DECISIONS, Severity.MEDIUM, "SURREAL_CURIOSITY", 5);
        }

        // 4. SINCERE WISH OR PRAYER
        boolean isSincerePrayer =
            combined.contains("prayer") || combined.contains("pray") || combined.contains("bless") ||
            combined.contains("peace") || combined.contains("health") || combined.contains("parents") ||
            combined.contains("family well-being") || combined.contains("spiritual") || combined.contains("gratitude") ||
            combined.contains("aspire to build") || combined.contains("healing");

        if (isSincerePrayer) {
            return new ClassificationResult("Sincere wish or prayer", true, ComplaintCategory.DESTINY_AND_LIFE_DECISIONS, Severity.LOW, "REVERENT_BLESSING", 3);
        }

        // 5. GENUINE GRIEVANCE (Default for real problems, disputes, frictions)
        ComplaintCategory cat = ComplaintCategory.OTHER_DIVINE_MATTERS;
        if (combined.contains("boss") || combined.contains("manager") || combined.contains("job") || combined.contains("salary") || combined.contains("layoff")) {
            cat = ComplaintCategory.CAREER_AND_UNEMPLOYMENT;
        } else if (combined.contains("exam") || combined.contains("professor") || combined.contains("grade") || combined.contains("college")) {
            cat = ComplaintCategory.ACADEMIC_SUFFERING;
        } else if (combined.contains("relative") || combined.contains("family") || combined.contains("wedding") || combined.contains("marriage")) {
            cat = ComplaintCategory.FAMILY_DRAMA;
        } else if (combined.contains("ex") || combined.contains("relationship") || combined.contains("ghosted") || combined.contains("partner")) {
            cat = ComplaintCategory.LOVE_AND_RELATIONSHIPS;
        } else if (combined.contains("neighbor") || combined.contains("noise") || combined.contains("drilling") || combined.contains("karma") || combined.contains("cut me off")) {
            cat = ComplaintCategory.KARMA_DEPARTMENT;
        }

        return new ClassificationResult("Genuine grievance", false, cat, Severity.HIGH, "STRUCTURED_DETERMINATION", 1);
    }
}
