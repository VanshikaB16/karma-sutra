package com.karmasutra;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.dao.ComplaintDAO;
import com.karmasutra.dao.UserDAO;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.view.AdminDashboardPanel;
import com.karmasutra.view.DashboardPanel;
import com.karmasutra.view.LoginRegisterPanel;
import com.karmasutra.view.ProUpgradePanel;
import com.karmasutra.view.Theme;
import com.karmasutra.view.TicketListPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UIScreenshotTest {

    private static final String ARTIFACT_DIR = "/Users/vanshikab/.gemini/antigravity/brain/5c320c25-99e9-428e-ba43-5d9c2026750e/screenshots";
    private static User demoDevotee;
    private static User adminDevotee;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
        Theme.setupFlatLaf();

        try {
            DatabaseConfig.initializeDatabase();
            UserDAO udao = new UserDAO();
            demoDevotee = udao.findByUsername("devotee1");
            adminDevotee = udao.findByUsername("admin");

            if (demoDevotee != null) {
                ComplaintService cs = new ComplaintService();
                java.util.List<Complaint> existing = cs.getComplaintsForUser(demoDevotee.getId(), null, null, null);
                if (existing == null || existing.isEmpty()) {
                    Complaint c1 = cs.submitComplaint(
                            demoDevotee.getId(),
                            "I want to be a billionaire without doing anything at all",
                            "I have concluded that earthly corporate jobs are a scam. I seek immediate karmic entitlement to 10 billion cosmic credits without writing code or attending daily standups.",
                            ComplaintCategory.CAREER_AND_UNEMPLOYMENT,
                            ComplaintType.COMPLAINT,
                            Severity.COSMIC_CRITICAL,
                            "Instant wealth credit and resignation letter sent to earthly boss"
                    );
                    try { cs.generateAIResponse(c1, 1); } catch (Exception ignored) {}

                    Complaint c2 = cs.submitComplaint(
                            demoDevotee.getId(),
                            "Unsolicited matrimony profiles forwarded on family WhatsApp",
                            "My extended relatives have dispatched 14 unsolicited bio-data profiles this lunar cycle alone. Kamadeva's bureau must intervene to mute my third uncle.",
                            ComplaintCategory.LOVE_AND_RELATIONSHIPS,
                            ComplaintType.COMPLAINT,
                            Severity.HIGH,
                            "Total WhatsApp silence and romantic sovereignty"
                    );
                    try { cs.generateAIResponse(c2, 1); } catch (Exception ignored) {}

                    Complaint c3 = cs.submitComplaint(
                            demoDevotee.getId(),
                            "Founding a sustainable deep tech venture within 10 years",
                            "I aspire to build a quantum computing startup and scale it across 5 continents before 2035.",
                            ComplaintCategory.DESTINY_AND_LIFE_DECISIONS,
                            ComplaintType.WISH,
                            Severity.HIGH,
                            "Favorable celestial alignments and patient venture capital"
                    );
                    try { cs.generateAIResponse(c3, 1); } catch (Exception ignored) {}

                    Complaint c4 = cs.submitComplaint(
                            demoDevotee.getId(),
                            "Neighbor drills concrete wall every Sunday at 7:00 AM",
                            "Shani Dev, please inspect flat 402. Why is their renovation eternal? Why must the drill harmonize with my REM sleep?",
                            ComplaintCategory.ACADEMIC_SUFFERING,
                            ComplaintType.PERSONAL_DILEMMA,
                            Severity.MEDIUM,
                            "Peaceful acoustic barrier and broken drill bit"
                    );
                    try { cs.generateAIResponse(c4, 1); } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            System.out.println("Running in isolated sandbox mode without active MySQL connection: " + e.getMessage());
            demoDevotee = new User(1, "devotee1", "devotee1@karmasutra.org", "dummy_hash", "dummy_salt", "Arjun Sharma", com.karmasutra.model.enums.UserRole.DEVOTEE, com.karmasutra.model.enums.SubscriptionTier.FREE_DEVOTEE, 138, null);
            adminDevotee = new User(2, "admin", "admin@karmasutra.org", "dummy_hash", "dummy_salt", "Supreme Cosmic Admin", com.karmasutra.model.enums.UserRole.ARCH_DEITY_ADMIN, com.karmasutra.model.enums.SubscriptionTier.VIP_DEVOTEE, 10000, null);
        }

        new File(ARTIFACT_DIR).mkdirs();
    }

    private static void renderAndSavePanel(JComponent comp, int width, int height, String filename) {
        comp.setSize(width, height);
        comp.setPreferredSize(new Dimension(width, height));
        layoutRecursive(comp);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        comp.paint(g2);
        g2.dispose();

        File out = new File(ARTIFACT_DIR, filename);
        try {
            ImageIO.write(img, "png", out);
            System.out.println("Saved pure memory UI render: " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void layoutRecursive(java.awt.Container c) {
        c.doLayout();
        for (java.awt.Component child : c.getComponents()) {
            if (child instanceof java.awt.Container container) {
                layoutRecursive(container);
            }
        }
    }

    @Test
    @DisplayName("Render Login Portal Panel")
    void testRenderLogin() {
        LoginRegisterPanel panel = new LoginRegisterPanel(u -> {});
        renderAndSavePanel(panel, 540, 680, "01_login_portal.png");
        assertTrue(new File(ARTIFACT_DIR, "01_login_portal.png").exists());
    }

    @Test
    @DisplayName("Render Register Portal Panel")
    void testRenderRegister() {
        LoginRegisterPanel panel = new LoginRegisterPanel(u -> {});
        panel.showRegisterCard();
        renderAndSavePanel(panel, 540, 680, "01b_register_portal.png");
        assertTrue(new File(ARTIFACT_DIR, "01b_register_portal.png").exists());
    }

    @Test
    @DisplayName("Render Devotee Dashboard Panel")
    void testRenderDashboard() {
        DashboardPanel panel = new DashboardPanel(demoDevotee, () -> {}, c -> {});
        renderAndSavePanel(panel, 1080, 720, "02_devotee_dashboard.png");
        assertTrue(new File(ARTIFACT_DIR, "02_devotee_dashboard.png").exists());
    }

    @Test
    @DisplayName("Render Ticket Docket Panel")
    void testRenderDocket() {
        TicketListPanel panel = new TicketListPanel(demoDevotee, c -> {});
        renderAndSavePanel(panel, 1080, 720, "03_ticket_docket.png");
        assertTrue(new File(ARTIFACT_DIR, "03_ticket_docket.png").exists());
    }

    @Test
    @DisplayName("Render Karma Sutra PRO Panel")
    void testRenderPro() {
        ProUpgradePanel panel = new ProUpgradePanel(demoDevotee, () -> {});
        renderAndSavePanel(panel, 1080, 720, "04_karma_sutra_pro.png");
        assertTrue(new File(ARTIFACT_DIR, "04_karma_sutra_pro.png").exists());
    }

    @Test
    @DisplayName("Render 3D Cosmic Universe Bridge Hub")
    void testRenderUniverseBridge() {
        com.karmasutra.view.CosmicUniverseBridgePanel panel = new com.karmasutra.view.CosmicUniverseBridgePanel(demoDevotee);
        renderAndSavePanel(panel, 1080, 720, "04b_cosmic_universe_bridge.png");
        assertTrue(new File(ARTIFACT_DIR, "04b_cosmic_universe_bridge.png").exists());
    }

    @Test
    @DisplayName("Render Arch-Deity Admin Console Panel")
    void testRenderAdmin() {
        AdminDashboardPanel panel = new AdminDashboardPanel(adminDevotee, c -> {});
        renderAndSavePanel(panel, 1140, 740, "05_admin_console.png");
        assertTrue(new File(ARTIFACT_DIR, "05_admin_console.png").exists());
    }

    @Test
    @DisplayName("Render Redesigned Cosmic Docket with KPI Metrics, Filters and Decree Preview")
    void testRenderRedesignedDocket() {
        TicketListPanel panel = new TicketListPanel(demoDevotee, c -> {}, () -> {});
        renderAndSavePanel(panel, 1140, 780, "09_redesigned_cosmic_docket.png");
        assertTrue(new File(ARTIFACT_DIR, "09_redesigned_cosmic_docket.png").exists());
    }

    @Test
    @DisplayName("Render Redesigned Divine Overview with Astrological Weather and Wish Progress")
    void testRenderRedesignedOverview() {
        DashboardPanel panel = new DashboardPanel(demoDevotee, () -> {}, c -> {});
        renderAndSavePanel(panel, 1140, 780, "10_redesigned_divine_overview.png");
        assertTrue(new File(ARTIFACT_DIR, "10_redesigned_divine_overview.png").exists());
    }

    @Test
    @DisplayName("Render Destiny Tracker Panel")
    void testRenderDestinyTracker() {
        com.karmasutra.view.DestinyTrackerPanel panel = new com.karmasutra.view.DestinyTrackerPanel(demoDevotee, () -> {}, c -> {});
        renderAndSavePanel(panel, 1140, 780, "11_destiny_tracker.png");
        assertTrue(new File(ARTIFACT_DIR, "11_destiny_tracker.png").exists());
    }

    @Test
    @DisplayName("Render Divine Responses Archive Panel")
    void testRenderDivineResponses() {
        com.karmasutra.view.DivineResponsesPanel panel = new com.karmasutra.view.DivineResponsesPanel(demoDevotee);
        renderAndSavePanel(panel, 1140, 780, "12_divine_responses.png");
        assertTrue(new File(ARTIFACT_DIR, "12_divine_responses.png").exists());
    }

    @Test
    @DisplayName("Render Devotee Profile Dossier Panel")
    void testRenderProfileDossier() {
        com.karmasutra.view.ProfilePanel panel = new com.karmasutra.view.ProfilePanel(demoDevotee, () -> {}, () -> {}, () -> {});
        renderAndSavePanel(panel, 1140, 780, "13_profile_dossier.png");
        assertTrue(new File(ARTIFACT_DIR, "13_profile_dossier.png").exists());
    }

    @Test
    @DisplayName("Render Full App Shell with Categorized Sidebar and Docket")
    void testRenderFullAppShell() {
        TicketListPanel panel = new TicketListPanel(demoDevotee, c -> {}, () -> {});
        JPanel shell = com.karmasutra.view.MainApplicationFrame.createStandaloneShell(demoDevotee, panel);
        renderAndSavePanel(shell, 1260, 840, "14_full_app_shell.png");
        assertTrue(new File(ARTIFACT_DIR, "14_full_app_shell.png").exists());
    }
}
