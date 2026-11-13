package com.karmasutra.view;

import com.karmasutra.model.User;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.service.KarmaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Devotee Spiritual Dossier & Profile Panel.
 * Displays user identity, spiritual standing, karma telemetry, and account actions.
 */
public class ProfilePanel extends JPanel {

    private final User user;
    private final ComplaintService complaintService;
    private final KarmaService karmaService;
    private final Runnable onUpgradeClicked;
    private final Runnable onSettingsClicked;
    private final Runnable onSignOutClicked;

    private JLabel nameLabel;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel karmaPointsVal;
    private JLabel tierBadge;
    private JLabel totalFiledVal;

    public ProfilePanel(User user, Runnable onUpgradeClicked, Runnable onSettingsClicked, Runnable onSignOutClicked) {
        this.user = user;
        this.complaintService = new ComplaintService();
        this.karmaService = new KarmaService();
        this.onUpgradeClicked = onUpgradeClicked;
        this.onSettingsClicked = onSettingsClicked;
        this.onSignOutClicked = onSignOutClicked;

        initializeUI();
        refreshProfile();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel title = new JLabel("Devotee Spiritual Dossier & Identity Record");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("Official registry credentials and karmic audit records maintained by the Universal Directorate.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(3));
        titleCol.add(sub);
        headerPanel.add(titleCol, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Content Area: Identity Card + Telemetry Grid + Account Actions
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. Devotee Identity Card
        JPanel idCard = Theme.createCardPanel();
        idCard.setLayout(new BorderLayout(20, 0));
        idCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        idCard.setPreferredSize(new Dimension(800, 120));

        // Avatar Icon
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.COLOR_GOLD_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.COLOR_GOLD);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(64, 64));
        avatarPanel.setLayout(new BorderLayout());
        JLabel avatarInitial = new JLabel(user != null && !user.getFullName().isEmpty() ? String.valueOf(user.getFullName().charAt(0)) : "D", javax.swing.SwingConstants.CENTER);
        avatarInitial.setFont(Theme.FONT_HEADER_1);
        avatarInitial.setForeground(Theme.COLOR_GOLD);
        avatarPanel.add(avatarInitial, BorderLayout.CENTER);

        JPanel leftIdentity = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        leftIdentity.setOpaque(false);
        leftIdentity.add(avatarPanel);

        JPanel nameCol = new JPanel();
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));
        nameCol.setOpaque(false);

        nameLabel = new JLabel(user != null ? user.getFullName() : "Devotee");
        nameLabel.setFont(Theme.FONT_HEADER_1);
        nameLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);

        usernameLabel = new JLabel("@" + (user != null ? user.getUsername() : "devotee") + " • Certified Soul Identity");
        usernameLabel.setFont(Theme.FONT_BODY);
        usernameLabel.setForeground(Theme.COLOR_GOLD);

        emailLabel = new JLabel(user != null ? user.getEmail() : "devotee@karmasutra.org");
        emailLabel.setFont(Theme.FONT_SMALL);
        emailLabel.setForeground(Theme.COLOR_TEXT_MUTED);

        nameCol.add(nameLabel);
        nameCol.add(Box.createVerticalStrut(2));
        nameCol.add(usernameLabel);
        nameCol.add(Box.createVerticalStrut(2));
        nameCol.add(emailLabel);
        leftIdentity.add(nameCol);

        idCard.add(leftIdentity, BorderLayout.WEST);

        // Right Badges
        JPanel rightBadges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 24));
        rightBadges.setOpaque(false);

        tierBadge = Theme.createBadge(user != null ? user.getTier().getDisplayName() : "Free Devotee", Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD);
        rightBadges.add(tierBadge);

        JLabel roleBadge = Theme.createBadge(user != null ? user.getRole().name() : "DEVOTEE", Theme.COLOR_BURGUNDY_LIGHT, Theme.COLOR_BURGUNDY);
        rightBadges.add(roleBadge);

        idCard.add(rightBadges, BorderLayout.EAST);

        centerPanel.add(idCard);
        centerPanel.add(Box.createVerticalStrut(16));

        // 2. Spiritual Telemetry KPI Grid
        JPanel telemetryGrid = new JPanel(new GridLayout(1, 3, 14, 0));
        telemetryGrid.setOpaque(false);
        telemetryGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        telemetryGrid.setPreferredSize(new Dimension(800, 85));

        karmaPointsVal = new JLabel(String.valueOf(user != null ? user.getKarmaPoints() : 108));
        totalFiledVal = new JLabel("0");
        JLabel quotaVal = new JLabel(user != null && user.isPro() ? "Unlimited" : "10 / Day");

        telemetryGrid.add(createMetricCard("Akashic Balance", karmaPointsVal, Theme.COLOR_GOLD, "Verified spiritual units"));
        telemetryGrid.add(createMetricCard("Petitions Filed", totalFiledVal, Theme.COLOR_TEXT_PRIMARY, "Historical grievances"));
        telemetryGrid.add(createMetricCard("Bandwidth Quota", quotaVal, Theme.COLOR_SAGE, "Celestial submission tier"));

        centerPanel.add(telemetryGrid);
        centerPanel.add(Box.createVerticalStrut(16));

        // 3. Quick Account Actions Card
        JPanel actionsCard = Theme.createCardPanel();
        actionsCard.setLayout(new BorderLayout());
        actionsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        actionsCard.setPreferredSize(new Dimension(800, 75));

        JPanel actTitleCol = new JPanel();
        actTitleCol.setLayout(new BoxLayout(actTitleCol, BoxLayout.Y_AXIS));
        actTitleCol.setOpaque(false);

        JLabel actTitle = new JLabel("Spiritual Directorate Actions");
        actTitle.setFont(Theme.FONT_HEADER_3);
        actTitle.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel actDesc = new JLabel("Manage API credentials, upgrade to PRO bandwidth, or conclude session.");
        actDesc.setFont(Theme.FONT_SMALL);
        actDesc.setForeground(Theme.COLOR_TEXT_MUTED);

        actTitleCol.add(actTitle);
        actTitleCol.add(Box.createVerticalStrut(2));
        actTitleCol.add(actDesc);
        actionsCard.add(actTitleCol, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);

        JButton btnUpgrade = Theme.createGoldButton("Upgrade to PRO™");
        btnUpgrade.setFont(Theme.FONT_SMALL);
        btnUpgrade.addActionListener(e -> {
            if (onUpgradeClicked != null) onUpgradeClicked.run();
        });

        JButton btnSettings = Theme.createSecondaryButton("Settings");
        btnSettings.setFont(Theme.FONT_SMALL);
        btnSettings.addActionListener(e -> {
            if (onSettingsClicked != null) onSettingsClicked.run();
        });

        JButton btnLogout = Theme.createSecondaryButton("Sign Out");
        btnLogout.setFont(Theme.FONT_SMALL);
        btnLogout.addActionListener(e -> {
            if (onSignOutClicked != null) onSignOutClicked.run();
        });

        btnRow.add(btnUpgrade);
        btnRow.add(btnSettings);
        btnRow.add(btnLogout);
        actionsCard.add(btnRow, BorderLayout.EAST);

        centerPanel.add(actionsCard);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valLabel, Color valColor, String subtitle) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(Theme.FONT_LABEL);
        titleLbl.setForeground(Theme.COLOR_TEXT_MUTED);

        valLabel.setFont(Theme.FONT_HEADER_1);
        valLabel.setForeground(valColor);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(Theme.FONT_SMALL);
        subLbl.setForeground(Theme.COLOR_TEXT_DIM);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(subLbl);
        return card;
    }

    public void refreshProfile() {
        if (user == null) return;

        nameLabel.setText(user.getFullName());
        usernameLabel.setText("@" + user.getUsername() + " • Certified Soul Identity");
        emailLabel.setText(user.getEmail());
        karmaPointsVal.setText(user.getKarmaPoints() + " Karma");
        tierBadge.setText(user.getTier().getDisplayName());

        Map<String, Integer> counts = complaintService.getStatusCountsForUser(user.getId());
        int total = 0;
        for (int c : counts.values()) total += c;
        totalFiledVal.setText(String.valueOf(total));
    }
}
