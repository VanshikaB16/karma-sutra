package com.karmasutra.view;

import com.karmasutra.exception.AuthenticationException;
import com.karmasutra.model.User;
import com.karmasutra.service.AuthService;
import com.karmasutra.view.components.CosmicGalaxyPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

/**
 * Sci-Fi Cosmic & Galaxy Login / Registration Portal.
 * Symmetrical, aligned translucent glass console over animated floating planetary galaxy.
 */
public class LoginRegisterPanel extends JPanel {

    private final AuthService authService;
    private final Consumer<User> onLoginSuccess;

    private CardLayout authCardLayout;
    private JPanel authCardPanel;

    // Login Form Fields
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // Register Form Fields
    private JTextField regUsernameField;
    private JTextField regEmailField;
    private JTextField regFullNameField;
    private JPasswordField regPasswordField;

    public LoginRegisterPanel(Consumer<User> onLoginSuccess) {
        this.authService = new AuthService();
        this.onLoginSuccess = onLoginSuccess;
        initializeUI();
    }

    public void showRegisterCard() {
        if (authCardLayout != null && authCardPanel != null) {
            authCardLayout.show(authCardPanel, "REGISTER");
        }
    }

    public void showLoginCard() {
        if (authCardLayout != null && authCardPanel != null) {
            authCardLayout.show(authCardPanel, "LOGIN");
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Living Animated Galaxy Background
        CosmicGalaxyPanel galaxyBg = new CosmicGalaxyPanel();
        galaxyBg.setLayout(new GridBagLayout());

        // Center Container
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(520, 690));

        // 1. Cosmic Branding Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandIcon = new JLabel("✦  K A R M A   S U T R A ™  ✦", SwingConstants.CENTER);
        brandIcon.setFont(Theme.FONT_BRAND);
        brandIcon.setForeground(Theme.COLOR_CYAN);
        brandIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandSub = new JLabel("Divine Grievance & Resolution Office", SwingConstants.CENTER);
        brandSub.setFont(Theme.FONT_HEADER_3);
        brandSub.setForeground(Theme.COLOR_PURPLE);
        brandSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTagline = new JLabel("\"The universe has a ticketing system.\"", SwingConstants.CENTER);
        brandTagline.setFont(Theme.FONT_TAGLINE);
        brandTagline.setForeground(Theme.COLOR_TEXT_MUTED);
        brandTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(brandIcon);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(brandSub);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(brandTagline);
        headerPanel.add(Box.createVerticalStrut(20));

        container.add(headerPanel);

        // 2. Translucent Glass Card
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(500, 530));
        card.setMaximumSize(new Dimension(500, 530));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        authCardLayout = new CardLayout();
        authCardPanel = new JPanel(authCardLayout);
        authCardPanel.setOpaque(false);

        authCardPanel.add(createLoginForm(), "LOGIN");
        authCardPanel.add(createRegisterForm(), "REGISTER");

        card.add(authCardPanel, BorderLayout.CENTER);
        container.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        galaxyBg.add(container, gbc);

        add(galaxyBg, BorderLayout.CENTER);
    }

    private JPanel createLoginForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 16, 4, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        int row = 0;

        // Title
        gbc.gridy = row++;
        JLabel title = new JLabel("Devotee Portal Sign In", SwingConstants.LEFT);
        title.setFont(Theme.FONT_HEADER_2);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);
        p.add(title, gbc);

        // Username
        gbc.gridy = row++;
        gbc.insets = new Insets(14, 16, 2, 16);
        JLabel lblUser = new JLabel("USERNAME OR CELESTIAL EMAIL");
        lblUser.setFont(Theme.FONT_LABEL);
        lblUser.setForeground(Theme.COLOR_CYAN);
        p.add(lblUser, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 16, 8, 16);
        loginUsernameField = Theme.createTextField("Enter your identifier");
        loginUsernameField.setText("devotee1");
        loginUsernameField.setPreferredSize(new Dimension(380, 38));
        p.add(loginUsernameField, gbc);

        // Password
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 16, 2, 16);
        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setFont(Theme.FONT_LABEL);
        lblPass.setForeground(Theme.COLOR_CYAN);
        p.add(lblPass, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 16, 16, 16);
        loginPasswordField = Theme.createPasswordField();
        loginPasswordField.setText("Devotee@108");
        loginPasswordField.setPreferredSize(new Dimension(380, 38));
        p.add(loginPasswordField, gbc);

        // Sign In Button (Full width, perfectly centered)
        gbc.gridy = row++;
        gbc.insets = new Insets(8, 16, 14, 16);
        JButton btnLogin = Theme.createPrimaryButton("Authenticate Grievance Access");
        btnLogin.setPreferredSize(new Dimension(380, 44));
        btnLogin.addActionListener(e -> performLogin());
        p.add(btnLogin, gbc);

        // Demo Quick Access Holographic Pills
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 16, 16, 16);
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        quickPanel.setOpaque(false);

        JButton btnDemoUser = Theme.createSecondaryButton("✦ Demo Devotee");
        btnDemoUser.setFont(Theme.FONT_SMALL);
        btnDemoUser.addActionListener(e -> {
            loginUsernameField.setText("devotee1");
            loginPasswordField.setText("Devotee@108");
            performLogin();
        });

        JButton btnDemoAdmin = Theme.createSecondaryButton("⚡ Arch-Deity Admin");
        btnDemoAdmin.setFont(Theme.FONT_SMALL);
        btnDemoAdmin.addActionListener(e -> {
            loginUsernameField.setText("admin");
            loginPasswordField.setText("CosmicAdmin@108");
            performLogin();
        });

        quickPanel.add(btnDemoUser);
        quickPanel.add(btnDemoAdmin);
        p.add(quickPanel, gbc);

        // Switch to Register
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 16, 4, 16);
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        JLabel noAcct = new JLabel("New petitioner?");
        noAcct.setFont(Theme.FONT_BODY);
        noAcct.setForeground(Theme.COLOR_TEXT_MUTED);
        JButton btnSwitch = new JButton("Register Devotee Record");
        btnSwitch.setFont(Theme.FONT_BODY_BOLD);
        btnSwitch.setForeground(Theme.COLOR_CYAN);
        btnSwitch.setContentAreaFilled(false);
        btnSwitch.setBorderPainted(false);
        btnSwitch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSwitch.addActionListener(e -> authCardLayout.show(authCardPanel, "REGISTER"));

        switchPanel.add(noAcct);
        switchPanel.add(btnSwitch);
        p.add(switchPanel, gbc);

        return p;
    }

    private JPanel createRegisterForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 16, 2, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        int row = 0;

        // Title
        gbc.gridy = row++;
        JLabel title = new JLabel("Register Devotee Docket", SwingConstants.LEFT);
        title.setFont(Theme.FONT_HEADER_2);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);
        p.add(title, gbc);

        // Full Name
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 16, 2, 16);
        JLabel lblName = new JLabel("EARTHLY FULL NAME");
        lblName.setFont(Theme.FONT_LABEL);
        lblName.setForeground(Theme.COLOR_CYAN);
        p.add(lblName, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 16, 6, 16);
        regFullNameField = Theme.createTextField("e.g. Arjun Sharma");
        regFullNameField.setPreferredSize(new Dimension(380, 36));
        p.add(regFullNameField, gbc);

        // Username
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 16, 2, 16);
        JLabel lblUser = new JLabel("CHOSEN DEVOTEE USERNAME");
        lblUser.setFont(Theme.FONT_LABEL);
        lblUser.setForeground(Theme.COLOR_CYAN);
        p.add(lblUser, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 16, 6, 16);
        regUsernameField = Theme.createTextField("min. 3 characters");
        regUsernameField.setPreferredSize(new Dimension(380, 36));
        p.add(regUsernameField, gbc);

        // Email
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 16, 2, 16);
        JLabel lblEmail = new JLabel("EARTHLY EMAIL ADDRESS");
        lblEmail.setFont(Theme.FONT_LABEL);
        lblEmail.setForeground(Theme.COLOR_CYAN);
        p.add(lblEmail, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 16, 6, 16);
        regEmailField = Theme.createTextField("yourname@domain.com");
        regEmailField.setPreferredSize(new Dimension(380, 36));
        p.add(regEmailField, gbc);

        // Password
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 16, 2, 16);
        JLabel lblPass = new JLabel("PASSWORD (MIN 6 CHARACTERS)");
        lblPass.setFont(Theme.FONT_LABEL);
        lblPass.setForeground(Theme.COLOR_CYAN);
        p.add(lblPass, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 16, 14, 16);
        regPasswordField = Theme.createPasswordField();
        regPasswordField.setPreferredSize(new Dimension(380, 36));
        p.add(regPasswordField, gbc);

        // Register Button (Centered, full-width, perfectly aligned!)
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 16, 10, 16);
        JButton btnRegister = Theme.createPrimaryButton("Initialize Cosmic Record (108 Karma Included)");
        btnRegister.setPreferredSize(new Dimension(380, 44));
        btnRegister.addActionListener(e -> performRegister());
        p.add(btnRegister, gbc);

        // Switch to Login
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 16, 4, 16);
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        JLabel haveAcct = new JLabel("Already registered?");
        haveAcct.setFont(Theme.FONT_BODY);
        haveAcct.setForeground(Theme.COLOR_TEXT_MUTED);
        JButton btnSwitch = new JButton("Return to Sign In");
        btnSwitch.setFont(Theme.FONT_BODY_BOLD);
        btnSwitch.setForeground(Theme.COLOR_CYAN);
        btnSwitch.setContentAreaFilled(false);
        btnSwitch.setBorderPainted(false);
        btnSwitch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSwitch.addActionListener(e -> authCardLayout.show(authCardPanel, "LOGIN"));

        switchPanel.add(haveAcct);
        switchPanel.add(btnSwitch);
        p.add(switchPanel, gbc);

        return p;
    }

    private void performLogin() {
        String identifier = loginUsernameField.getText().trim();
        String pass = new String(loginPasswordField.getPassword());

        try {
            User user = authService.login(identifier, pass);
            onLoginSuccess.accept(user);
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cosmic Gatekeeper Notice", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "System error during authentication: " + ex.getMessage(), "Authentication Fault", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRegister() {
        String name = regFullNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String pass = new String(regPasswordField.getPassword());

        try {
            User user = authService.register(username, email, name, pass);
            JOptionPane.showMessageDialog(this, "Cosmic record successfully opened! 108 Welcome Karma points granted.", "Akashic Record Created", JOptionPane.INFORMATION_MESSAGE);
            onLoginSuccess.accept(user);
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Incomplete", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "System error during registration: " + ex.getMessage(), "Registration Fault", JOptionPane.ERROR_MESSAGE);
        }
    }
}
