package com.karmasutra;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.model.User;
import com.karmasutra.view.LoginRegisterPanel;
import com.karmasutra.view.MainApplicationFrame;
import com.karmasutra.view.Theme;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application entry point for Karma Sutra™.
 * Divine Grievance & Resolution Office — "The universe has a ticketing system."
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static JFrame loginFrame;

    public static void main(String[] args) {
        // Enforce Swing rendering properties for crisp typography on macOS and high-DPI displays
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Karma Sutra™");

        SwingUtilities.invokeLater(() -> {
            // Apply custom theme and FlatLaf styling
            Theme.setupFlatLaf();

            // Start embedded 3D Cosmic Orrery Web Server immediately on port 8108 & 5173
            try {
                com.karmasutra.bridge.CosmicWebServer.getInstance().start();
            } catch (Exception e) {
                LOGGER.warning("Could not start embedded Cosmic Web Server on boot: " + e.getMessage());
            }

            // Attempt automated database initialization
            boolean dbReady = DatabaseConfig.initializeDatabase();
            if (!dbReady) {
                LOGGER.warning("MySQL database not reachable on localhost:3306. Check scripts/start-db.sh or settings.");
                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Karma Sutra™ was unable to connect to MySQL on localhost:3306.\n\n" +
                        "If MySQL is installed, please run 'bash scripts/start-db.sh' or start your MySQL server.\n" +
                        "Would you like to proceed anyway to inspect the interface and configuration settings?",
                        "Cosmic Database Connectivity Notice",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new Object[]{"Proceed to Login", "Exit"},
                        "Proceed to Login"
                );
                if (choice != 0) {
                    System.exit(0);
                    return;
                }
            }

            launchLoginWindow();
        });
    }

    public static void launchLoginWindow() {
        if (loginFrame != null) {
            loginFrame.dispose();
        }

        loginFrame = new JFrame("Karma Sutra™ — Divine Grievance & Resolution Office");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.getContentPane().setBackground(Theme.COLOR_BG);

        LoginRegisterPanel panel = new LoginRegisterPanel(authenticatedUser -> {
            loginFrame.dispose();
            launchMainApplication(authenticatedUser);
        });

        loginFrame.setContentPane(panel);
        loginFrame.setPreferredSize(new Dimension(580, 740));
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    public static void launchMainApplication(User user) {
        MainApplicationFrame mainFrame = new MainApplicationFrame(user);
        mainFrame.setVisible(true);
    }
}
