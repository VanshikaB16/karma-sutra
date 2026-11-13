package com.karmasutra.view;

import com.karmasutra.config.AppConfig;
import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.service.DivineResponseService;
import com.karmasutra.service.ai.LLMProvider;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Editorial configuration modal for AI providers and database connections.
 * Allows university evaluators to configure keys or select offline mode without touching code.
 */
public class SettingsDialog extends JDialog {

    private JComboBox<String> providerCombo;
    private JPasswordField geminiKeyField;
    private JPasswordField groqKeyField;
    private JTextField dbHostField;
    private JTextField dbPortField;
    private JTextField dbUserField;

    public SettingsDialog(JFrame parent) {
        super(parent, "Celestial Telemetry & Configuration — Karma Sutra™", true);
        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.COLOR_BG);
        setPreferredSize(new Dimension(540, 520));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel title = new JLabel("System Telemetry & Providers");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("Configure external LLM gateways or switch to offline resilient synthesis mode.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // Body Form in Card
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 1. AI Provider Selection
        card.add(createFieldLabel("PREFERRED AI PROVIDER"));
        card.add(Box.createVerticalStrut(4));
        String[] providers = {"GEMINI (Google Gemini 1.5 Flash)", "GROQ (Llama 3.3 70B)", "COSMIC_MOCK (Offline Resilient Engine)"};
        providerCombo = new JComboBox<>(providers);
        String currentProv = AppConfig.get("ai.provider", "GEMINI").toUpperCase();
        if (currentProv.contains("GROQ")) {
            providerCombo.setSelectedIndex(1);
        } else if (currentProv.contains("MOCK") || currentProv.contains("OFFLINE")) {
            providerCombo.setSelectedIndex(2);
        } else {
            providerCombo.setSelectedIndex(0);
        }
        card.add(providerCombo);
        card.add(Box.createVerticalStrut(14));

        // 2. Gemini API Key
        card.add(createFieldLabel("GOOGLE GEMINI API KEY"));
        card.add(Box.createVerticalStrut(4));
        geminiKeyField = Theme.createPasswordField();
        String currentGemini = AppConfig.get("gemini.api.key", AppConfig.get("GEMINI_API_KEY", ""));
        if (!currentGemini.isBlank() && !currentGemini.contains("your_gemini")) {
            geminiKeyField.setText(currentGemini);
        }
        card.add(geminiKeyField);
        card.add(Box.createVerticalStrut(14));

        // 3. Groq API Key
        card.add(createFieldLabel("GROQ CLOUD API KEY"));
        card.add(Box.createVerticalStrut(4));
        groqKeyField = Theme.createPasswordField();
        String currentGroq = AppConfig.get("groq.api.key", AppConfig.get("GROQ_API_KEY", ""));
        if (!currentGroq.isBlank() && !currentGroq.contains("your_groq")) {
            groqKeyField.setText(currentGroq);
        }
        card.add(groqKeyField);
        card.add(Box.createVerticalStrut(16));

        // 4. Database Info Grid
        JLabel dbTitle = new JLabel("DATABASE PARAMETERS (MYSQL)");
        dbTitle.setFont(Theme.FONT_LABEL);
        dbTitle.setForeground(Theme.COLOR_TEXT_MUTED);
        card.add(dbTitle);
        card.add(Box.createVerticalStrut(6));

        JPanel dbGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        dbGrid.setOpaque(false);

        dbHostField = Theme.createTextField(AppConfig.get("db.host", "localhost"));
        dbHostField.setText(AppConfig.get("db.host", "localhost"));
        dbPortField = Theme.createTextField(String.valueOf(AppConfig.getInt("db.port", 3306)));
        dbPortField.setText(String.valueOf(AppConfig.getInt("db.port", 3306)));
        dbUserField = Theme.createTextField(AppConfig.get("db.user", "root"));
        dbUserField.setText(AppConfig.get("db.user", "root"));

        JPanel hCol = new JPanel(new BorderLayout());
        hCol.setOpaque(false);
        hCol.add(new JLabel("Host:"), BorderLayout.NORTH);
        hCol.add(dbHostField, BorderLayout.CENTER);

        JPanel pCol = new JPanel(new BorderLayout());
        pCol.setOpaque(false);
        pCol.add(new JLabel("Port:"), BorderLayout.NORTH);
        pCol.add(dbPortField, BorderLayout.CENTER);

        JPanel uCol = new JPanel(new BorderLayout());
        uCol.setOpaque(false);
        uCol.add(new JLabel("User:"), BorderLayout.NORTH);
        uCol.add(dbUserField, BorderLayout.CENTER);

        dbGrid.add(hCol);
        dbGrid.add(pCol);
        dbGrid.add(uCol);
        card.add(dbGrid);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        wrap.add(card, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        btnPanel.setOpaque(false);

        JButton btnTest = Theme.createSecondaryButton("Test Active AI Gateway");
        btnTest.addActionListener(e -> testAiConnection());

        JButton btnSave = Theme.createPrimaryButton("Save Configurations");
        btnSave.addActionListener(e -> saveSettings());

        btnPanel.add(btnTest);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.COLOR_TEXT_MUTED);
        return l;
    }

    private void testAiConnection() {
        saveSettingsToRuntime();
        DivineResponseService s = new DivineResponseService();
        LLMProvider active = s.getActiveProvider();
        JOptionPane.showMessageDialog(this,
                "Active Provider: " + active.getProviderName() + "\nStatus: " +
                (active.isAvailable() ? "Online & Ready" : "Offline / Key Not Configured"),
                "AI Telemetry Diagnostic", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveSettingsToRuntime() {
        int idx = providerCombo.getSelectedIndex();
        String prov = idx == 1 ? "GROQ" : (idx == 2 ? "COSMIC_MOCK" : "GEMINI");
        AppConfig.setRuntimeProperty("ai.provider", prov);

        String geminiKey = new String(geminiKeyField.getPassword()).trim();
        if (!geminiKey.isEmpty()) {
            AppConfig.setRuntimeProperty("gemini.api.key", geminiKey);
            AppConfig.setRuntimeProperty("GEMINI_API_KEY", geminiKey);
        }

        String groqKey = new String(groqKeyField.getPassword()).trim();
        if (!groqKey.isEmpty()) {
            AppConfig.setRuntimeProperty("groq.api.key", groqKey);
            AppConfig.setRuntimeProperty("GROQ_API_KEY", groqKey);
        }

        AppConfig.setRuntimeProperty("db.host", dbHostField.getText().trim());
        AppConfig.setRuntimeProperty("db.port", dbPortField.getText().trim());
        AppConfig.setRuntimeProperty("db.user", dbUserField.getText().trim());
    }

    private void saveSettings() {
        saveSettingsToRuntime();
        JOptionPane.showMessageDialog(this,
                "Cosmic configurations successfully saved in active runtime.",
                "Settings Updated", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
