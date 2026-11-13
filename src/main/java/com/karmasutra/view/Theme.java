package com.karmasutra.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.TicketStatus;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * Editorial Celestial Institutional Visual Design System for Karma Sutra™.
 * Deep obsidian, warm charcoal, antique gold, restrained burgundy, and warm ivory.
 */
public final class Theme {

    // ===================================================================
    // LUXURY CELESTIAL PALETTE
    // ===================================================================
    public static final Color COLOR_BG = new Color(0x0E, 0x10, 0x15);              // Deep Obsidian
    public static final Color COLOR_SURFACE = new Color(0x16, 0x18, 0x22);         // Dark Cosmic Charcoal Surface
    public static final Color COLOR_SURFACE_ALT = new Color(0x11, 0x13, 0x1A);     // Sidebar Deep Shelf
    public static final Color COLOR_SURFACE_CARD = new Color(0x18, 0x1A, 0x25);    // Premium Card Surface
    public static final Color COLOR_SURFACE_HOVER = new Color(0x21, 0x24, 0x32);   // Card Hover State
    public static final Color COLOR_INPUT_BG = new Color(0x0A, 0x0C, 0x11);        // Deep Inset Input

    // Typography Colors
    public static final Color COLOR_TEXT_PRIMARY = new Color(0xF7, 0xF5, 0xF0);    // Warm Ivory
    public static final Color COLOR_TEXT_MUTED = new Color(0x9E, 0x9A, 0x8E);      // Soft Parchment Gray
    public static final Color COLOR_TEXT_DIM = new Color(0x66, 0x63, 0x58);        // Subtle Dim Gray

    // Editorial Accents
    public static final Color COLOR_GOLD = new Color(0xC5, 0xA8, 0x69);            // Muted Antique Gold
    public static final Color COLOR_GOLD_HOVER = new Color(0xD8, 0xBC, 0x7D);      // Radiant Gold Hover
    public static final Color COLOR_GOLD_LIGHT = new Color(0x2A, 0x24, 0x15);      // Gold Badge Tint

    public static final Color COLOR_BURGUNDY = new Color(0x71, 0x3B, 0x45);        // Imperial Muted Burgundy
    public static final Color COLOR_BURGUNDY_DARK = new Color(0x4A, 0x1E, 0x25);   // Deep Wine
    public static final Color COLOR_BURGUNDY_LIGHT = new Color(0x2B, 0x14, 0x18);  // Burgundy Badge Tint

    public static final Color COLOR_SAGE = new Color(0x74, 0x81, 0x6B);            // Muted Celestial Sage (Resolved)
    public static final Color COLOR_SAGE_LIGHT = new Color(0x1B, 0x24, 0x19);      // Sage Badge Tint

    public static final Color COLOR_CRIMSON = new Color(0xA8, 0x43, 0x43);         // Terracotta Crimson (Alert / Critical)
    public static final Color COLOR_CRIMSON_LIGHT = new Color(0x2D, 0x14, 0x14);   // Crimson Badge Tint

    public static final Color COLOR_BORDER = new Color(0x26, 0x28, 0x36);          // Hairline Astral Gray Divider
    public static final Color COLOR_BORDER_SUBTLE = new Color(0x1C, 0x1E, 0x28);   // Very Subtle Inset Border
    public static final Color COLOR_BORDER_ACCENT = new Color(0x50, 0x42, 0x2A);   // Muted Gold Hairline Accent

    // Compatibility aliases so existing views remain 100% compile-safe
    public static final Color COLOR_CYAN = COLOR_GOLD;
    public static final Color COLOR_PURPLE = COLOR_BURGUNDY;
    public static final Color COLOR_VIOLET_DEEP = COLOR_BURGUNDY_DARK;
    public static final Color COLOR_MAROON = COLOR_BURGUNDY;
    public static final Color COLOR_MAROON_LIGHT = COLOR_BURGUNDY_LIGHT;
    public static final Color COLOR_PINK = COLOR_BURGUNDY;
    public static final Color COLOR_OCHRE = COLOR_CRIMSON;
    public static final Color COLOR_OCHRE_LIGHT = COLOR_CRIMSON_LIGHT;
    public static final Color COLOR_SURFACE_GLASS = COLOR_SURFACE;
    public static final Color COLOR_BORDER_GLOW = COLOR_BORDER_ACCENT;

    // ===================================================================
    // EDITORIAL CELESTIAL TYPOGRAPHY
    // ===================================================================
    private static final String SANS_FAMILY = getAvailableFont("Inter", "SF Pro Text", "Segoe UI", "Helvetica Neue", "SansSerif");
    private static final String SERIF_FAMILY = getAvailableFont("Cormorant Garamond", "Playfair Display", "Georgia", "Cinzel", "Serif");
    private static final String MONO_FAMILY = getAvailableFont("JetBrains Mono", "SF Mono", "Consolas", "Monaco", "Monospaced");

    public static final Font FONT_BRAND = new Font(SERIF_FAMILY, Font.BOLD, 22);
    public static final Font FONT_HEADER_1 = new Font(SERIF_FAMILY, Font.BOLD, 22);
    public static final Font FONT_HEADER_2 = new Font(SERIF_FAMILY, Font.BOLD, 17);
    public static final Font FONT_HEADER_3 = new Font(SANS_FAMILY, Font.BOLD, 14);

    public static final Font FONT_BODY = new Font(SANS_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(SANS_FAMILY, Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font(SANS_FAMILY, Font.BOLD, 11);
    public static final Font FONT_SMALL = new Font(SANS_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_TAGLINE = new Font(SERIF_FAMILY, Font.ITALIC, 13);
    public static final Font FONT_MONO = new Font(MONO_FAMILY, Font.BOLD, 12);
    public static final Font FONT_SERIF_BODY = new Font(SERIF_FAMILY, Font.PLAIN, 14);

    public static final int CORNER_RADIUS = 8;

    private Theme() {
    }

    public static void setupFlatLaf() {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Panel.background", COLOR_BG);
            UIManager.put("Label.foreground", COLOR_TEXT_PRIMARY);
            UIManager.put("Label.font", FONT_BODY);

            UIManager.put("TextField.background", COLOR_INPUT_BG);
            UIManager.put("TextField.foreground", COLOR_TEXT_PRIMARY);
            UIManager.put("TextField.caretForeground", COLOR_GOLD);
            UIManager.put("TextField.arc", CORNER_RADIUS);

            UIManager.put("PasswordField.background", COLOR_INPUT_BG);
            UIManager.put("PasswordField.foreground", COLOR_TEXT_PRIMARY);
            UIManager.put("PasswordField.caretForeground", COLOR_GOLD);
            UIManager.put("PasswordField.arc", CORNER_RADIUS);

            UIManager.put("Button.arc", CORNER_RADIUS);
            UIManager.put("Component.arc", CORNER_RADIUS);

            UIManager.put("Table.background", COLOR_SURFACE);
            UIManager.put("Table.foreground", COLOR_TEXT_PRIMARY);
            UIManager.put("Table.font", FONT_BODY);
            UIManager.put("TableHeader.font", FONT_LABEL);
            UIManager.put("TableHeader.background", COLOR_SURFACE_ALT);
            UIManager.put("TableHeader.foreground", COLOR_TEXT_MUTED);
            UIManager.put("Table.selectionBackground", new Color(0x28, 0x22, 0x34));
            UIManager.put("Table.selectionForeground", COLOR_TEXT_PRIMARY);
            UIManager.put("Table.gridColor", COLOR_BORDER);

            UIManager.put("ScrollPane.background", COLOR_SURFACE);
            UIManager.put("ScrollBar.thumbArc", 6);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

            UIManager.put("TabbedPane.selectedBackground", COLOR_SURFACE);
            UIManager.put("TabbedPane.selectedForeground", COLOR_GOLD);
            UIManager.put("TabbedPane.foreground", COLOR_TEXT_MUTED);
        } catch (Exception ignored) {
        }
    }

    private static String getAvailableFont(String... candidates) {
        String[] installed = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String c : candidates) {
            for (String ins : installed) {
                if (ins.equalsIgnoreCase(c)) {
                    return ins;
                }
            }
        }
        return candidates[candidates.length - 1];
    }

    /**
     * Primary action button with restrained imperial burgundy background and fine gold border.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, COLOR_BURGUNDY_DARK, getWidth(), getHeight(), new Color(0x3B, 0x18, 0x1E));
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, new Color(0x86, 0x45, 0x52), getWidth(), getHeight(), COLOR_BURGUNDY);
                } else {
                    gp = new GradientPaint(0, 0, COLOR_BURGUNDY, getWidth(), getHeight(), COLOR_BURGUNDY_DARK);
                }

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                // Subtle antique gold hairline border
                g2.setColor(getModel().isRollover() ? COLOR_GOLD : new Color(0xC5, 0xA8, 0x69, 100));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Secondary button with dark obsidian background and hairline border.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(0x1E, 0x20, 0x2A));
                } else if (getModel().isRollover()) {
                    g2.setColor(COLOR_SURFACE_HOVER);
                } else {
                    g2.setColor(COLOR_SURFACE_CARD);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                // Hairline border
                g2.setColor(getModel().isRollover() ? COLOR_GOLD : COLOR_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * High-visibility Antique Gold CTA button with obsidian text.
     */
    public static JButton createGoldButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = getModel().isPressed()
                        ? new Color(0xAA, 0x8F, 0x54)
                        : (getModel().isRollover() ? COLOR_GOLD_HOVER : COLOR_GOLD);

                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(COLOR_BG); // Dark obsidian readable text on gold
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setForeground(COLOR_TEXT_PRIMARY);
        tf.setBackground(COLOR_INPUT_BG);
        tf.setCaretColor(COLOR_GOLD);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setForeground(COLOR_TEXT_PRIMARY);
        pf.setBackground(COLOR_INPUT_BG);
        pf.setCaretColor(COLOR_GOLD);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_BODY);
        ta.setForeground(COLOR_TEXT_PRIMARY);
        ta.setBackground(COLOR_INPUT_BG);
        ta.setCaretColor(COLOR_GOLD);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return ta;
    }

    /**
     * Editorial institutional card panel with solid fill and hairline border.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(COLOR_SURFACE_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        return card;
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 90));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(FONT_LABEL);
        badge.setForeground(fg);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return badge;
    }

    public static JLabel createStatusBadge(TicketStatus status) {
        if (status == null) {
            return createBadge("Unknown", new Color(0x1F, 0x22, 0x2E), COLOR_TEXT_MUTED);
        }
        return switch (status) {
            case RESOLVED -> createBadge("Resolved", COLOR_SAGE_LIGHT, COLOR_SAGE);
            case UNDER_DIVINE_REVIEW -> createBadge("Under Review", COLOR_GOLD_LIGHT, COLOR_GOLD);
            case FORWARDED_TO_KARMA_DEPT -> createBadge("Karma Dept", COLOR_BURGUNDY_LIGHT, COLOR_BURGUNDY);
            case AWAITING_COSMIC_APPROVAL -> createBadge("Cosmic Approval", new Color(0x28, 0x24, 0x1A), COLOR_GOLD);
            case ON_HOLD_BANDWIDTH -> createBadge("Bandwidth Delay", COLOR_CRIMSON_LIGHT, COLOR_CRIMSON);
            case RECEIVED_BY_HEAVEN -> createBadge("Received", new Color(0x1A, 0x1D, 0x28), new Color(0xB8, 0xC0, 0xD4));
        };
    }

    public static JLabel createSeverityBadge(Severity severity) {
        if (severity == null) {
            return createBadge("Medium", COLOR_GOLD_LIGHT, COLOR_GOLD);
        }
        return switch (severity) {
            case COSMIC_CRITICAL -> createBadge("Critical", COLOR_CRIMSON_LIGHT, COLOR_CRIMSON);
            case HIGH -> createBadge("High", new Color(0x35, 0x1A, 0x20), new Color(0xD4, 0x72, 0x72));
            case MEDIUM -> createBadge("Medium", COLOR_GOLD_LIGHT, COLOR_GOLD);
            case LOW -> createBadge("Low", COLOR_SAGE_LIGHT, COLOR_SAGE);
        };
    }
}
