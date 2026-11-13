package com.karmasutra.view;

import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;
import com.karmasutra.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application window for Karma Sutra™: Divine Complaint Management System.
 * Features an editorial institutional header, categorized hierarchical sidebar, and card-swapping content pane.
 */
public class MainApplicationFrame extends JFrame {

    private User currentUser;
    private final AuthService authService;

    // Navigation and Layout
    private CardLayout contentCardLayout;
    private JPanel contentCardPanel;
    private List<JButton> navButtons;

    // Dedicated Content Panels
    private DashboardPanel dashboardPanel;
    private TicketListPanel ticketListPanel;
    private KarmaScorePanel karmaScorePanel;
    private DestinyTrackerPanel destinyTrackerPanel;
    private CosmicUniverseBridgePanel universeBridgePanel;
    private ProUpgradePanel proUpgradePanel;
    private DivineResponsesPanel divineResponsesPanel;
    private ProfilePanel profilePanel;
    private AdminDashboardPanel adminDashboardPanel;

    // Header Live Badges
    private JLabel headerGreeting;
    private JLabel headerKarmaBadge;
    private JLabel headerTierBadge;
    private JButton btnAdminNav;

    public MainApplicationFrame(User initialUser) {
        super("Karma Sutra™ — Divine Grievance & Resolution Office");
        this.currentUser = initialUser;
        this.authService = new AuthService();
        this.navButtons = new ArrayList<>();

        // Start embedded 3D web server bridge
        com.karmasutra.bridge.CosmicWebServer.getInstance().setActiveUser(currentUser);
        com.karmasutra.bridge.CosmicWebServer.getInstance().start();

        initializeUI();
        setSize(1260, 840);
        setMinimumSize(new Dimension(1080, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.COLOR_BG);

        // 1. Editorial Institutional Header
        JPanel topHeader = createTopHeader();
        add(topHeader, BorderLayout.NORTH);

        // 2. Main Body: Categorized Sidebar (West) + Content Area (Center)
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setOpaque(false);

        JPanel sidebar = createCategorizedSidebar();
        bodyPanel.add(sidebar, BorderLayout.WEST);

        // 3. Central Content Cards
        contentCardLayout = new CardLayout();
        contentCardPanel = new JPanel(contentCardLayout);
        contentCardPanel.setOpaque(false);

        // Instantiate Views
        dashboardPanel = new DashboardPanel(currentUser, this::openSubmitGrievanceModal, this::openTicketDetailsModal);
        ticketListPanel = new TicketListPanel(currentUser, this::openTicketDetailsModal, this::openSubmitGrievanceModal);
        karmaScorePanel = new KarmaScorePanel(currentUser, this::refreshHeaderUserData);
        destinyTrackerPanel = new DestinyTrackerPanel(currentUser, this::openSubmitGrievanceModal, this::openTicketDetailsModal);
        universeBridgePanel = new CosmicUniverseBridgePanel(currentUser);
        proUpgradePanel = new ProUpgradePanel(currentUser, this::onUserTierUpgraded);
        divineResponsesPanel = new DivineResponsesPanel(currentUser);
        profilePanel = new ProfilePanel(currentUser, () -> showCard("PRO"), () -> new SettingsDialog(this).setVisible(true), this::performLogout);

        contentCardPanel.add(dashboardPanel, "DASHBOARD");
        contentCardPanel.add(ticketListPanel, "TICKETS");
        contentCardPanel.add(karmaScorePanel, "KARMA");
        contentCardPanel.add(destinyTrackerPanel, "DESTINY");
        contentCardPanel.add(universeBridgePanel, "DEPARTMENTS");
        contentCardPanel.add(proUpgradePanel, "PRO");
        contentCardPanel.add(divineResponsesPanel, "RESPONSES");
        contentCardPanel.add(profilePanel, "PROFILE");

        if (currentUser != null && currentUser.isAdmin()) {
            adminDashboardPanel = new AdminDashboardPanel(currentUser, this::openTicketDetailsModal);
            contentCardPanel.add(adminDashboardPanel, "ADMIN");
        }

        bodyPanel.add(contentCardPanel, BorderLayout.CENTER);
        add(bodyPanel, BorderLayout.CENTER);

        // For sovereign administrators, land directly on the Arch-Deity Console; otherwise Divine Overview
        if (currentUser != null && currentUser.isAdmin() && btnAdminNav != null) {
            setActiveNavButton(btnAdminNav);
            showCard("ADMIN");
        } else if (!navButtons.isEmpty()) {
            setActiveNavButton(navButtons.get(0));
        }
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.COLOR_SURFACE_ALT);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.COLOR_BORDER),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        // Brand Title
        JPanel brandGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandGroup.setOpaque(false);

        JLabel brandLogo = new JLabel("✦ KARMA SUTRA™");
        brandLogo.setFont(Theme.FONT_BRAND);
        brandLogo.setForeground(Theme.COLOR_GOLD);

        JLabel divider = new JLabel("|");
        divider.setFont(Theme.FONT_BODY);
        divider.setForeground(Theme.COLOR_BORDER);

        JLabel brandDept = new JLabel("Divine Grievance & Resolution Office");
        brandDept.setFont(Theme.FONT_TAGLINE);
        brandDept.setForeground(Theme.COLOR_TEXT_MUTED);

        brandGroup.add(brandLogo);
        brandGroup.add(divider);
        brandGroup.add(brandDept);
        header.add(brandGroup, BorderLayout.WEST);

        // Right User Profile & Telemetry Actions
        JPanel userActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userActions.setOpaque(false);

        headerGreeting = new JLabel(currentUser != null ? currentUser.getFullName() : "Devotee");
        headerGreeting.setFont(Theme.FONT_BODY_BOLD);
        headerGreeting.setForeground(Theme.COLOR_TEXT_PRIMARY);

        headerKarmaBadge = Theme.createBadge(
                (currentUser != null ? currentUser.getKarmaPoints() : 108) + " Karma",
                Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD
        );

        headerTierBadge = Theme.createBadge(
                currentUser != null ? currentUser.getTier().getDisplayName() : "Free Devotee",
                Theme.COLOR_BURGUNDY_LIGHT, Theme.COLOR_BURGUNDY
        );

        JButton btnSettings = Theme.createSecondaryButton("⚙ Settings");
        btnSettings.setFont(Theme.FONT_SMALL);
        btnSettings.addActionListener(e -> new SettingsDialog(this).setVisible(true));

        JButton btnLogout = Theme.createSecondaryButton("Sign Out");
        btnLogout.setFont(Theme.FONT_SMALL);
        btnLogout.addActionListener(e -> performLogout());

        userActions.add(headerGreeting);
        userActions.add(headerTierBadge);
        userActions.add(headerKarmaBadge);
        userActions.add(btnSettings);
        userActions.add(btnLogout);
        header.add(userActions, BorderLayout.EAST);

        return header;
    }

    private JPanel createCategorizedSidebar() {
        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(Theme.COLOR_SURFACE_ALT);
        sidebarContent.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        // 1. THE CELESTIAL DESK
        addCategoryHeader(sidebarContent, "THE CELESTIAL DESK");
        JButton btnOverview = createSidebarNavButton("Divine Overview", "DASHBOARD");
        JButton btnDocket = createSidebarNavButton("Cosmic Docket", "TICKETS");
        sidebarContent.add(btnOverview);
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(btnDocket);
        sidebarContent.add(Box.createVerticalStrut(4));

        JButton btnQuickSubmit = createSidebarActionButton("+ File a Grievance", this::openSubmitGrievanceModal);
        sidebarContent.add(btnQuickSubmit);
        sidebarContent.add(Box.createVerticalStrut(16));

        // 2. THE COSMIC ARCHIVE
        addCategoryHeader(sidebarContent, "THE COSMIC ARCHIVE");
        JButton btnLedger = createSidebarNavButton("Akashic Ledger", "KARMA");
        JButton btnDestiny = createSidebarNavButton("Destiny Tracker", "DESTINY");
        JButton btnDepartments = createSidebarNavButton("Divine Departments", "DEPARTMENTS");
        sidebarContent.add(btnLedger);
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(btnDestiny);
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(btnDepartments);
        sidebarContent.add(Box.createVerticalStrut(16));

        // 3. DIVINE SERVICES
        addCategoryHeader(sidebarContent, "DIVINE SERVICES");
        JButton btnPro = createSidebarNavButton("Karma Sutra PRO™", "PRO");
        JButton btnResponses = createSidebarNavButton("Divine Responses", "RESPONSES");
        sidebarContent.add(btnPro);
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(btnResponses);
        sidebarContent.add(Box.createVerticalStrut(16));

        // 4. ACCOUNT
        addCategoryHeader(sidebarContent, "ACCOUNT");
        JButton btnProfile = createSidebarNavButton("Devotee Profile", "PROFILE");
        sidebarContent.add(btnProfile);
        sidebarContent.add(Box.createVerticalStrut(4));

        JButton btnSettingsNav = createSidebarActionButton("Settings", () -> new SettingsDialog(this).setVisible(true));
        sidebarContent.add(btnSettingsNav);
        sidebarContent.add(Box.createVerticalStrut(4));

        JButton btnSignOutNav = createSidebarActionButton("Sign Out", this::performLogout);
        sidebarContent.add(btnSignOutNav);

        // 5. ADMINISTRATIVE (Admin only)
        if (currentUser != null && currentUser.isAdmin()) {
            sidebarContent.add(Box.createVerticalStrut(16));
            addCategoryHeader(sidebarContent, "ADMINISTRATIVE");
            btnAdminNav = createSidebarNavButton("Arch-Deity Console", "ADMIN");
            sidebarContent.add(btnAdminNav);
        }

        sidebarContent.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(sidebarContent);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.COLOR_BORDER));
        scroll.setPreferredSize(new Dimension(225, Integer.MAX_VALUE));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(Theme.COLOR_SURFACE_ALT);

        JPanel outerSidebar = new JPanel(new BorderLayout());
        outerSidebar.add(scroll, BorderLayout.CENTER);
        return outerSidebar;
    }

    private void addCategoryHeader(JPanel parent, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.COLOR_GOLD);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(8));
    }

    private JButton createSidebarNavButton(String title, String cardKey) {
        JButton btn = new JButton(title);
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(Theme.COLOR_TEXT_PRIMARY);
        btn.setBackground(Theme.COLOR_SURFACE_ALT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            setActiveNavButton(btn);
            showCard(cardKey);
        });

        navButtons.add(btn);
        return btn;
    }

    private JButton createSidebarActionButton(String title, Runnable action) {
        JButton btn = new JButton(title);
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(title.startsWith("+") ? Theme.COLOR_GOLD : Theme.COLOR_TEXT_MUTED);
        btn.setBackground(Theme.COLOR_SURFACE_ALT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            if (action != null) action.run();
        });
        return btn;
    }

    private void showCard(String cardKey) {
        contentCardLayout.show(contentCardPanel, cardKey);
        if ("DASHBOARD".equals(cardKey)) {
            dashboardPanel.refreshData();
        } else if ("TICKETS".equals(cardKey)) {
            ticketListPanel.refreshTickets();
        } else if ("KARMA".equals(cardKey)) {
            karmaScorePanel.refreshLedger();
        } else if ("DESTINY".equals(cardKey)) {
            destinyTrackerPanel.refreshWishes();
        } else if ("PRO".equals(cardKey)) {
            proUpgradePanel.refreshStatus();
        } else if ("RESPONSES".equals(cardKey)) {
            divineResponsesPanel.refreshResponses();
        } else if ("PROFILE".equals(cardKey)) {
            profilePanel.refreshProfile();
        } else if ("ADMIN".equals(cardKey) && adminDashboardPanel != null) {
            adminDashboardPanel.refreshAllAdminData();
        }
    }

    private void setActiveNavButton(JButton activeBtn) {
        for (JButton b : navButtons) {
            if (b == activeBtn) {
                b.setFont(Theme.FONT_BODY_BOLD);
                b.setForeground(Theme.COLOR_GOLD);
                b.setOpaque(true);
                b.setBackground(Theme.COLOR_SURFACE_CARD);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.COLOR_GOLD),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            } else {
                b.setFont(Theme.FONT_BODY);
                b.setForeground(Theme.COLOR_TEXT_MUTED);
                b.setOpaque(false);
                b.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 12));
            }
        }
    }

    private void openSubmitGrievanceModal() {
        SubmitGrievanceDialog dialog = new SubmitGrievanceDialog(this, currentUser, complaint -> {
            refreshHeaderUserData();
            dashboardPanel.refreshData();
            ticketListPanel.refreshTickets();
            destinyTrackerPanel.refreshWishes();
            divineResponsesPanel.refreshResponses();
            profilePanel.refreshProfile();
        });
        dialog.setVisible(true);
    }

    private void openTicketDetailsModal(Complaint complaint) {
        TicketDetailDialog dialog = new TicketDetailDialog(this, complaint, currentUser, () -> {
            dashboardPanel.refreshData();
            ticketListPanel.refreshTickets();
            destinyTrackerPanel.refreshWishes();
            divineResponsesPanel.refreshResponses();
            profilePanel.refreshProfile();
            if (adminDashboardPanel != null) {
                adminDashboardPanel.refreshAllAdminData();
            }
        });
        dialog.setVisible(true);
    }

    private void onUserTierUpgraded() {
        refreshHeaderUserData();
        proUpgradePanel.refreshStatus();
        dashboardPanel.refreshData();
        profilePanel.refreshProfile();
    }

    public void refreshHeaderUserData() {
        if (currentUser != null) {
            headerGreeting.setText(currentUser.getFullName());
            headerKarmaBadge.setText(currentUser.getKarmaPoints() + " Karma");
            headerTierBadge.setText(currentUser.getTier().getDisplayName());
            com.karmasutra.bridge.CosmicWebServer.getInstance().setActiveUser(currentUser);
        }
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you wish to disconnect from the Akashic records and close this cosmic session?",
                "Sign Out Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            com.karmasutra.bridge.CosmicWebServer.getInstance().stop();
            authService.logout();
            dispose();
            com.karmasutra.Main.launchLoginWindow();
        }
    }

    public static JPanel createStandaloneShell(User user, javax.swing.JComponent contentPane) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Theme.COLOR_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.COLOR_SURFACE_ALT);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.COLOR_BORDER),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        JPanel brandGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandGroup.setOpaque(false);
        JLabel brandLogo = new JLabel("✦ KARMA SUTRA™");
        brandLogo.setFont(Theme.FONT_BRAND);
        brandLogo.setForeground(Theme.COLOR_GOLD);
        JLabel divider = new JLabel("|");
        divider.setFont(Theme.FONT_BODY);
        divider.setForeground(Theme.COLOR_BORDER);
        JLabel brandDept = new JLabel("Divine Grievance & Resolution Office");
        brandDept.setFont(Theme.FONT_TAGLINE);
        brandDept.setForeground(Theme.COLOR_TEXT_MUTED);
        brandGroup.add(brandLogo);
        brandGroup.add(divider);
        brandGroup.add(brandDept);
        header.add(brandGroup, BorderLayout.WEST);

        JPanel userActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userActions.setOpaque(false);
        JLabel greeting = new JLabel(user != null ? user.getFullName() : "Devotee");
        greeting.setFont(Theme.FONT_BODY_BOLD);
        greeting.setForeground(Theme.COLOR_TEXT_PRIMARY);
        userActions.add(greeting);
        userActions.add(Theme.createBadge(user != null ? user.getTier().getDisplayName() : "Free Devotee", Theme.COLOR_BURGUNDY_LIGHT, Theme.COLOR_BURGUNDY));
        userActions.add(Theme.createBadge((user != null ? user.getKarmaPoints() : 108) + " Karma", Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD));
        userActions.add(Theme.createSecondaryButton("⚙ Settings"));
        userActions.add(Theme.createSecondaryButton("Sign Out"));
        header.add(userActions, BorderLayout.EAST);
        shell.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        // Sidebar
        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(Theme.COLOR_SURFACE_ALT);
        sidebarContent.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        addStaticCategory(sidebarContent, "THE CELESTIAL DESK");
        sidebarContent.add(createStaticNavBtn("Divine Overview", false));
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(createStaticNavBtn("Cosmic Docket", true));
        sidebarContent.add(Box.createVerticalStrut(6));
        sidebarContent.add(Theme.createPrimaryButton("+ File a Grievance"));
        sidebarContent.add(Box.createVerticalStrut(16));

        addStaticCategory(sidebarContent, "THE COSMIC ARCHIVE");
        sidebarContent.add(createStaticNavBtn("Akashic Ledger", false));
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(createStaticNavBtn("Destiny Tracker", false));
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(createStaticNavBtn("Divine Departments", false));
        sidebarContent.add(Box.createVerticalStrut(16));

        addStaticCategory(sidebarContent, "DIVINE SERVICES");
        sidebarContent.add(createStaticNavBtn("Karma Sutra PRO™", false));
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(createStaticNavBtn("Divine Responses", false));
        sidebarContent.add(Box.createVerticalStrut(16));

        addStaticCategory(sidebarContent, "ACCOUNT");
        sidebarContent.add(createStaticNavBtn("Devotee Profile", false));
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(createStaticNavBtn("Settings", false));

        JScrollPane scroll = new JScrollPane(sidebarContent);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.COLOR_BORDER));
        scroll.setPreferredSize(new Dimension(220, 700));
        body.add(scroll, BorderLayout.WEST);

        body.add(contentPane, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private static void addStaticCategory(JPanel parent, String title) {
        JLabel cat = new JLabel(title);
        cat.setFont(Theme.FONT_LABEL);
        cat.setForeground(Theme.COLOR_TEXT_DIM);
        cat.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
        cat.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(cat);
    }

    private static JButton createStaticNavBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(active ? Theme.FONT_BODY_BOLD : Theme.FONT_BODY);
        b.setForeground(active ? Theme.COLOR_TEXT_PRIMARY : Theme.COLOR_TEXT_MUTED);
        b.setBackground(active ? Theme.COLOR_SURFACE_CARD : Theme.COLOR_SURFACE_ALT);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, active ? 3 : 0, 0, 0, active ? Theme.COLOR_GOLD : Theme.COLOR_SURFACE_ALT),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }
}
