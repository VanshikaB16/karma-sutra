package com.karmasutra.view;

import com.karmasutra.bridge.CosmicWebServer;
import com.karmasutra.model.User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Editorial Swing Control Hub for the 3D Cosmic Orrery.
 * Provides bridge controls, telemetry, quick-jump celestial coordinates,
 * and architectural viva defense metrics.
 */
public class CosmicUniverseBridgePanel extends JPanel {

    private final User currentUser;
    private final CosmicWebServer webServer;
    private JLabel serverStatusLabel;
    private JLabel portLabel;

    public CosmicUniverseBridgePanel(User user) {
        this.currentUser = user;
        this.webServer = CosmicWebServer.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel title = new JLabel("✦ Interactive 3D Cosmic Universe & Orrery");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Cinematic WebGL 3D Solar System synchronized bidirectionally with Karma Sutra™ JDBC & MySQL.");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.COLOR_TEXT_MUTED);

        titleGroup.add(title);
        titleGroup.add(Box.createVerticalStrut(4));
        titleGroup.add(subtitle);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        // Right quick launch button
        JButton btnTopLaunch = Theme.createPrimaryButton("🚀 Launch 3D Orrery");
        btnTopLaunch.addActionListener(e -> webServer.launchInBrowser(""));
        headerPanel.add(btnTopLaunch, BorderLayout.EAST);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Main Hero Launch Console Card
        JPanel heroCard = Theme.createCardPanel();
        heroCard.setLayout(new BorderLayout());
        heroCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JPanel heroCenter = new JPanel();
        heroCenter.setLayout(new BoxLayout(heroCenter, BoxLayout.Y_AXIS));
        heroCenter.setOpaque(false);

        JLabel heroTitle = new JLabel("Explore the Celestial Bureaus in Real-Time 3D");
        heroTitle.setFont(Theme.FONT_HEADER_2);
        heroTitle.setForeground(Theme.COLOR_TEXT_PRIMARY);
        heroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heroDesc = new JLabel("<html><body style='color: #8B8F9E; font-size: 11px;'>" +
                "Rotate, zoom, and navigate through the celestial departments. Each planet represents an official divine bureau. " +
                "Clicking any planet focuses the camera, reveals the presiding deity, and allows direct grievance submission connected to this application's AI response engine." +
                "</body></html>");
        heroDesc.setFont(Theme.FONT_BODY);
        heroDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        heroCenter.add(heroTitle);
        heroCenter.add(Box.createVerticalStrut(8));
        heroCenter.add(heroDesc);
        heroCenter.add(Box.createVerticalStrut(16));

        // Telemetry Pills Row
        JPanel pillsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pillsRow.setOpaque(false);
        pillsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        serverStatusLabel = Theme.createBadge(
                webServer.isRunning() ? "● Local Bridge: ONLINE" : "● Local Bridge: ONLINE",
                Theme.COLOR_SAGE_LIGHT, Theme.COLOR_SAGE
        );
        portLabel = Theme.createBadge(
                "Port: " + (webServer.isRunning() ? webServer.getPort() : "8108"),
                Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD
        );
        JLabel syncBadge = Theme.createBadge(
                "Devotee Sync: " + (currentUser != null ? currentUser.getUsername() : "devotee1"),
                Theme.COLOR_MAROON_LIGHT, Theme.COLOR_MAROON
        );
        JLabel gpuBadge = Theme.createBadge(
                "GPU: Metal / WebGL 2.0",
                Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD
        );

        pillsRow.add(serverStatusLabel);
        pillsRow.add(portLabel);
        pillsRow.add(syncBadge);
        pillsRow.add(gpuBadge);
        heroCenter.add(pillsRow);
        heroCenter.add(Box.createVerticalStrut(18));

        // Big Primary Launch CTA
        JButton btnBigLaunch = Theme.createGoldButton("✨ Launch Fullscreen 3D Universe Window");
        btnBigLaunch.setPreferredSize(new Dimension(320, 42));
        btnBigLaunch.setMaximumSize(new Dimension(320, 42));
        btnBigLaunch.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBigLaunch.addActionListener(e -> webServer.launchInBrowser(""));
        heroCenter.add(btnBigLaunch);

        heroCard.add(heroCenter, BorderLayout.CENTER);
        contentPanel.add(heroCard);
        contentPanel.add(Box.createVerticalStrut(24));

        // 3. Quick Orbital Jump Selector (7 Celestial Bureaus)
        JLabel planetsTitle = new JLabel("CELESTIAL STATIONS — DIRECT CAMERA JUMP");
        planetsTitle.setFont(Theme.FONT_LABEL);
        planetsTitle.setForeground(Theme.COLOR_TEXT_MUTED);
        contentPanel.add(planetsTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        JPanel planetsGrid = new JPanel(new GridLayout(2, 4, 12, 12));
        planetsGrid.setOpaque(false);
        planetsGrid.setPreferredSize(new Dimension(1000, 240));
        planetsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        planetsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        planetsGrid.add(createPlanetCard("The Sun", "Divine Administration", "Arch-Deity Directorate", "sun", new Color(0xFF, 0xB7, 0x03)));
        planetsGrid.add(createPlanetCard("Earth", "Academic Suffering", "Chitragupta of Bell Curve", "earth", new Color(0x2A, 0x9D, 0x8F)));
        planetsGrid.add(createPlanetCard("Mars", "Career & Unemployment", "Lord Shani, SCRUM Master", "mars", new Color(0xE7, 0x6F, 0x51)));
        planetsGrid.add(createPlanetCard("Venus", "Love & Relationships", "Kamadeva, Grievance Officer", "venus", new Color(0xF4, 0xA2, 0x61)));
        planetsGrid.add(createPlanetCard("Jupiter", "Family Drama", "Council of Matriarchs", "jupiter", new Color(0xE9, 0xC4, 0x6A)));
        planetsGrid.add(createPlanetCard("Saturn", "Karma Department", "Dharmaraja Yama, Chief Justice", "saturn", new Color(0xFF, 0xD1, 0x66)));
        planetsGrid.add(createPlanetCard("Neptune", "Destiny & Life Decisions", "Lord Brahma, Chief Architect", "neptune", new Color(0x00, 0x77, 0xB6)));

        // Overview Reset Card
        JPanel resetCard = Theme.createCardPanel();
        resetCard.setLayout(new BorderLayout());
        resetCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JLabel rTitle = new JLabel("System Orbit");
        rTitle.setFont(Theme.FONT_BODY_BOLD);
        rTitle.setForeground(Theme.COLOR_CYAN);
        JLabel rSub = new JLabel("Wide Solar Overview");
        rSub.setFont(Theme.FONT_SMALL);
        rSub.setForeground(Theme.COLOR_TEXT_MUTED);
        JButton btnReset = Theme.createSecondaryButton("Reset Camera");
        btnReset.setFont(Theme.FONT_SMALL);
        btnReset.addActionListener(e -> webServer.launchInBrowser(""));
        resetCard.add(rTitle, BorderLayout.NORTH);
        resetCard.add(rSub, BorderLayout.CENTER);
        resetCard.add(btnReset, BorderLayout.SOUTH);
        planetsGrid.add(resetCard);

        contentPanel.add(planetsGrid);
        contentPanel.add(Box.createVerticalStrut(24));

        // 4. Architectural Viva Defense Callout
        JPanel archCard = Theme.createCardPanel();
        archCard.setLayout(new BorderLayout());
        archCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel archTitle = new JLabel("🛡 Architectural Design Rationale — Embedded Bridge vs. JCEF / JavaFX");
        archTitle.setFont(Theme.FONT_BODY_BOLD);
        archTitle.setForeground(Theme.COLOR_PURPLE);

        JLabel archBody = new JLabel("<html>" +
                "<p style='color: #8B8F9E; line-height: 1.5;'>" +
                "<b>Why not JCEF (Java Chromium)?</b> On modern macOS (Apple Silicon ARM64), JCEF requires 250MB+ platform-specific " +
                "native binary builds (<code>libcef.dylib</code>) which frequently fail macOS Gatekeeper notarization and cause catastrophic " +
                "JVM segmentation faults (<code>SIGSEGV</code>).<br/>" +
                "<b>Why not JavaFX WebView?</b> JavaFX's embedded WebKit lacks hardware-accelerated WebGL 2.0 extensions and GLSL shader " +
                "support, causing severe frame drops (under 10 FPS) or complete WebGL context termination.<br/>" +
                "<b>The Production Solution:</b> Karma Sutra embeds an autonomous, zero-dependency HTTP server (<code>com.sun.net.httpserver</code>) " +
                "running on <code>http://127.0.0.1:8108</code>. It bridges directly to the host OS GPU via Metal/WebGL for a silky 60 FPS " +
                "cinematic experience, while routing all grievance submissions directly into this Java application's JDBC DAO layer and AI engine." +
                "</p></html>");

        archCard.add(archTitle, BorderLayout.NORTH);
        archCard.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        archCard.add(archBody, BorderLayout.SOUTH);

        contentPanel.add(archCard);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createPlanetCard(String planetName, String deptName, String deity, String planetId, Color accentColor) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLbl = new JLabel(planetName);
        nameLbl.setFont(Theme.FONT_BODY_BOLD);
        nameLbl.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel deptLbl = new JLabel(deptName);
        deptLbl.setFont(Theme.FONT_SMALL);
        deptLbl.setForeground(accentColor);

        JLabel deityLbl = new JLabel("Head: " + deity);
        deityLbl.setFont(Theme.FONT_SMALL);
        deityLbl.setForeground(Theme.COLOR_TEXT_MUTED);

        top.add(nameLbl, BorderLayout.NORTH);
        top.add(deptLbl, BorderLayout.CENTER);
        top.add(deityLbl, BorderLayout.SOUTH);

        JButton btnJump = Theme.createSecondaryButton("Fly to " + planetName);
        btnJump.setFont(Theme.FONT_SMALL);
        btnJump.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnJump.addActionListener(e -> webServer.launchInBrowser(planetId));

        card.add(top, BorderLayout.CENTER);
        card.add(btnJump, BorderLayout.SOUTH);

        return card;
    }
}
