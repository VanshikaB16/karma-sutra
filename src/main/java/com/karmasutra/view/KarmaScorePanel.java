package com.karmasutra.view;

import com.karmasutra.model.KarmaTransaction;
import com.karmasutra.model.User;
import com.karmasutra.service.KarmaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Editorial, restrained Karma Points ledger reflecting real database transactions.
 */
public class KarmaScorePanel extends JPanel {

    private final User user;
    private final KarmaService karmaService;
    private final Runnable onKarmaUpdated;

    private JLabel pointsLabel;
    private JLabel summaryLabel;
    private JTable ledgerTable;
    private DefaultTableModel tableModel;

    public KarmaScorePanel(User user, Runnable onKarmaUpdated) {
        this.user = user;
        this.karmaService = new KarmaService();
        this.onKarmaUpdated = onKarmaUpdated;

        initializeUI();
        refreshLedger();
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

        JLabel title = new JLabel("Akashic Karma Points Ledger");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("Official ledger recording all cosmic credits, penalties, and devotional balances.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(4));
        titleCol.add(sub);
        headerPanel.add(titleCol, BorderLayout.WEST);

        // Simulate Good Deed Action Button
        JButton btnGoodDeed = Theme.createGoldButton("+ Perform Simulated Good Deed (+25 Karma)");
        btnGoodDeed.addActionListener(e -> recordGoodDeed());
        headerPanel.add(btnGoodDeed, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Content Area: Balance Cards + Ledger Table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Balance & Rating Card
        JPanel balanceCard = Theme.createCardPanel();
        balanceCard.setLayout(new BorderLayout(16, 0));
        balanceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        balanceCard.setPreferredSize(new Dimension(800, 80));

        JPanel pointsCol = new JPanel();
        pointsCol.setLayout(new BoxLayout(pointsCol, BoxLayout.Y_AXIS));
        pointsCol.setOpaque(false);

        JLabel pointsTitle = new JLabel("CERTIFIED BALANCE");
        pointsTitle.setFont(Theme.FONT_LABEL);
        pointsTitle.setForeground(Theme.COLOR_TEXT_MUTED);

        pointsLabel = new JLabel(user.getKarmaPoints() + " Karma Units");
        pointsLabel.setFont(Theme.FONT_BRAND);
        pointsLabel.setForeground(Theme.COLOR_GOLD);

        pointsCol.add(pointsTitle);
        pointsCol.add(Box.createVerticalStrut(2));
        pointsCol.add(pointsLabel);

        summaryLabel = new JLabel(karmaService.getPersonalizedKarmaSummary(user));
        summaryLabel.setFont(Theme.FONT_BODY);
        summaryLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);

        balanceCard.add(pointsCol, BorderLayout.WEST);
        balanceCard.add(summaryLabel, BorderLayout.CENTER);

        centerPanel.add(balanceCard);
        centerPanel.add(Box.createVerticalStrut(18));

        // Ledger Table in Card
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Timestamp", "Transaction Nature", "Karma Points", "Institutional Reason"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        ledgerTable = new JTable(tableModel);
        ledgerTable.setRowHeight(36);
        ledgerTable.setShowVerticalLines(false);
        ledgerTable.setGridColor(Theme.COLOR_BORDER);
        ledgerTable.setFont(Theme.FONT_BODY);

        ledgerTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        ledgerTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
                if (val != null) {
                    String str = val.toString();
                    if (str.startsWith("+")) {
                        setForeground(Theme.COLOR_SAGE);
                    } else if (str.startsWith("-")) {
                        setForeground(Theme.COLOR_MAROON);
                    } else {
                        setForeground(Theme.COLOR_TEXT_PRIMARY);
                    }
                    setFont(Theme.FONT_BODY_BOLD);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(ledgerTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.COLOR_SURFACE);
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void recordGoodDeed() {
        String[] options = {
                "Allowed someone to merge in heavy traffic",
                "Did not reply with an angry paragraph in family WhatsApp group",
                "Returned borrowed umbrella to original earthly owner",
                "Watered a drooping apartment plant",
                "Refrained from unsolicited career advice"
        };

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a demonstrable act of cosmic virtue:",
                "Simulate Virtuous Action",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected != null) {
            karmaService.awardKarma(user.getId(), 25, "Simulated Good Deed: " + selected);
            refreshLedger();
            if (onKarmaUpdated != null) {
                onKarmaUpdated.run();
            }
            JOptionPane.showMessageDialog(this,
                    "25 Karma Points credited to your Akashic ledger under Audit Code #7A.",
                    "Karmic Deposit Verified", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshLedger() {
        if (user == null) {
            return;
        }

        pointsLabel.setText(user.getKarmaPoints() + " Karma Units");
        summaryLabel.setText(karmaService.getPersonalizedKarmaSummary(user));

        List<KarmaTransaction> list = karmaService.getHistory(user.getId());
        tableModel.setRowCount(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        for (KarmaTransaction tx : list) {
            String timeStr = tx.getCreatedAt() != null ? tx.getCreatedAt().toLocalDateTime().format(dtf) : "Cosmic Genesis";
            String amtStr = (tx.getAmount() > 0 ? "+" : "") + tx.getAmount();
            tableModel.addRow(new Object[]{
                    timeStr,
                    tx.getTransactionType(),
                    amtStr,
                    tx.getReason()
            });
        }
    }
}
