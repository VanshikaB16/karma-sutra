package com.karmasutra.view;

import com.karmasutra.model.SimulatedPayment;
import com.karmasutra.model.Subscription;
import com.karmasutra.model.User;
import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.service.SubscriptionService;

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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Editorial, sophisticated premium tier upgrade experience for Karma Sutra PRO™.
 * Fictional subscription system demonstrating multi-table JDBC transactions.
 */
public class ProUpgradePanel extends JPanel {

    private final User user;
    private final SubscriptionService subscriptionService;
    private final Runnable onTierUpdated;

    private JLabel currentTierBadge;
    private JLabel expiryInfoLabel;
    private DefaultTableModel paymentHistoryModel;

    public ProUpgradePanel(User user, Runnable onTierUpdated) {
        this.user = user;
        this.subscriptionService = new SubscriptionService();
        this.onTierUpdated = onTierUpdated;

        initializeUI();
        refreshStatus();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Top Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Karma Sutra PRO™ — Priority Celestial Escalation");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("\"Some prayers need priority processing.\" Expedite long-term aspirations through cosmic red tape.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);
        add(headerPanel, BorderLayout.NORTH);

        // Center: Tier Cards + Payment History
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Active Status Bar
        JPanel statusBar = Theme.createCardPanel();
        statusBar.setLayout(new BorderLayout());
        statusBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        statusBar.setPreferredSize(new Dimension(800, 48));

        JPanel statusWest = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusWest.setOpaque(false);
        JLabel lblActive = new JLabel("CURRENT ACCREDITATION:");
        lblActive.setFont(Theme.FONT_LABEL);
        lblActive.setForeground(Theme.COLOR_TEXT_MUTED);
        currentTierBadge = Theme.createBadge(user.getTier().getDisplayName(), Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD);
        statusWest.add(lblActive);
        statusWest.add(currentTierBadge);

        expiryInfoLabel = new JLabel("Free Devotee (No expiry)");
        expiryInfoLabel.setFont(Theme.FONT_SMALL);
        expiryInfoLabel.setForeground(Theme.COLOR_TEXT_MUTED);

        statusBar.add(statusWest, BorderLayout.WEST);
        statusBar.add(expiryInfoLabel, BorderLayout.EAST);
        centerPanel.add(statusBar);
        centerPanel.add(Box.createVerticalStrut(18));

        // 3-Tier Comparison Matrix
        JPanel tiersGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        tiersGrid.setOpaque(false);
        tiersGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        tiersGrid.setPreferredSize(new Dimension(800, 240));

        tiersGrid.add(createTierCard(
                "FREE DEVOTEE",
                "₹0",
                "/month",
                "• Standard Cosmic Docket Queue\n• 1 Divine Response per Ticket\n• Routine Departmental Routing\n• Standard Akashic Balance",
                user.getTier() == SubscriptionTier.FREE_DEVOTEE ? "Current Tier" : "Downgrade",
                user.getTier() == SubscriptionTier.FREE_DEVOTEE,
                SubscriptionTier.FREE_DEVOTEE
        ));

        tiersGrid.add(createTierCard(
                "KARMA SUTRA PRO",
                "₹49",
                "/month",
                "• Expedited Celestial Review\n• Multi-Decade Destiny Tracking\n• 3 Perspectives per Ticket\n• Automatic Wish Milestones\n• +100 Karmic Onboarding Bonus",
                user.getTier() == SubscriptionTier.KARMA_SUTRA_PRO ? "Current Tier" : "Upgrade (₹49)",
                user.getTier() == SubscriptionTier.KARMA_SUTRA_PRO,
                SubscriptionTier.KARMA_SUTRA_PRO
        ));

        tiersGrid.add(createTierCard(
                "VIP DEVOTEE",
                "₹99",
                "/month",
                "• Direct Arch-Deity Escalation\n• Priority Astrological Bandwidth\n• Unlimited Perspectives\n• Custom Planetary Protection\n• +250 Karmic Onboarding Bonus",
                user.getTier() == SubscriptionTier.VIP_DEVOTEE ? "Current Tier" : "Upgrade (₹99)",
                user.getTier() == SubscriptionTier.VIP_DEVOTEE,
                SubscriptionTier.VIP_DEVOTEE
        ));

        centerPanel.add(tiersGrid);
        centerPanel.add(Box.createVerticalStrut(22));

        // Simulated Payments Audit Log
        JLabel payTitle = new JLabel("Simulated Divine Transaction History (JDBC Ledger)");
        payTitle.setFont(Theme.FONT_HEADER_3);
        payTitle.setForeground(Theme.COLOR_TEXT_PRIMARY);
        centerPanel.add(payTitle);
        centerPanel.add(Box.createVerticalStrut(8));

        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Reference Code", "Plan Tier", "Amount", "Currency", "Payment Method", "Status", "Timestamp"};
        paymentHistoryModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable payTable = new JTable(paymentHistoryModel);
        payTable.setRowHeight(32);
        payTable.setFont(Theme.FONT_BODY);
        payTable.setShowVerticalLines(false);
        payTable.setGridColor(Theme.COLOR_BORDER);

        tableCard.add(new JScrollPane(payTable), BorderLayout.CENTER);
        centerPanel.add(tableCard);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTierCard(String name, String price, String period, String features,
                                  String buttonText, boolean isCurrent, SubscriptionTier tier) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(name);
        title.setFont(Theme.FONT_LABEL);
        title.setForeground(tier == SubscriptionTier.KARMA_SUTRA_PRO || tier == SubscriptionTier.VIP_DEVOTEE ? Theme.COLOR_MAROON : Theme.COLOR_TEXT_MUTED);

        JPanel priceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        priceRow.setOpaque(false);
        JLabel p = new JLabel(price);
        p.setFont(Theme.FONT_HEADER_1);
        p.setForeground(Theme.COLOR_TEXT_PRIMARY);
        JLabel per = new JLabel(period);
        per.setFont(Theme.FONT_SMALL);
        per.setForeground(Theme.COLOR_TEXT_MUTED);
        priceRow.add(p);
        priceRow.add(per);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(priceRow);
        card.add(Box.createVerticalStrut(10));

        // Feature list
        JLabel feats = new JLabel("<html>" + features.replace("\n", "<br>") + "</html>");
        feats.setFont(Theme.FONT_SMALL);
        feats.setForeground(Theme.COLOR_TEXT_PRIMARY);
        card.add(feats);
        card.add(Box.createVerticalGlue());

        JButton btn = isCurrent ? Theme.createSecondaryButton("Active Tier") : (tier == SubscriptionTier.VIP_DEVOTEE ? Theme.createGoldButton(buttonText) : Theme.createPrimaryButton(buttonText));
        btn.setEnabled(!isCurrent);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(e -> launchSimulatedCheckout(tier));
        card.add(btn);

        return card;
    }

    private void launchSimulatedCheckout(SubscriptionTier targetTier) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog checkoutModal = new JDialog(parent, "Simulated Divine Checkout — Karma Sutra PRO™", true);
        checkoutModal.setLayout(new BorderLayout());
        checkoutModal.getContentPane().setBackground(Theme.COLOR_BG);
        checkoutModal.setPreferredSize(new Dimension(460, 420));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel t = new JLabel("Simulated Divine Checkout");
        t.setFont(Theme.FONT_HEADER_1);
        t.setForeground(Theme.COLOR_MAROON);

        JLabel desc = new JLabel("Demonstrating multi-table ACID transaction in MySQL.");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.COLOR_TEXT_MUTED);

        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(desc);
        p.add(Box.createVerticalStrut(18));

        JPanel detailsCard = Theme.createCardPanel();
        detailsCard.setLayout(new GridLayout(3, 2, 8, 8));
        detailsCard.add(new JLabel("Target Accreditation:"));
        detailsCard.add(new JLabel(targetTier.getDisplayName()));
        detailsCard.add(new JLabel("Simulated Charge:"));
        detailsCard.add(new JLabel("₹" + targetTier.getMonthlyPrice()));
        detailsCard.add(new JLabel("Validity Period:"));
        detailsCard.add(new JLabel("30 Cosmic Days"));
        p.add(detailsCard);
        p.add(Box.createVerticalStrut(16));

        JLabel lblPay = new JLabel("Select Fictional Payment Channel:");
        lblPay.setFont(Theme.FONT_LABEL);
        p.add(lblPay);
        p.add(Box.createVerticalStrut(4));

        String[] channels = {"Cosmic UPI (Instant Moksha)", "Heavenly NetBanking (Karmic Clearing)", "Akashic Direct Debit"};
        JComboBox<String> channelBox = new JComboBox<>(channels);
        channelBox.setFont(Theme.FONT_BODY);
        p.add(channelBox);
        p.add(Box.createVerticalStrut(20));

        JButton btnConfirm = Theme.createGoldButton("Execute Simulated Payment (₹" + targetTier.getMonthlyPrice() + ")");
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnConfirm.addActionListener(e -> {
            boolean ok = subscriptionService.upgradeTier(user, targetTier, (String) channelBox.getSelectedItem());
            if (ok) {
                checkoutModal.dispose();
                refreshStatus();
                if (onTierUpdated != null) {
                    onTierUpdated.run();
                }
                JOptionPane.showMessageDialog(this,
                        "Accreditation upgraded to " + targetTier.getDisplayName() +
                        "!\nSimulated transaction committed atomically to MySQL ledger.",
                        "Cosmic Upgrade Activated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        p.add(btnConfirm);

        checkoutModal.add(p);
        checkoutModal.pack();
        checkoutModal.setLocationRelativeTo(parent);
        checkoutModal.setVisible(true);
    }

    public void refreshStatus() {
        if (user == null) {
            return;
        }

        currentTierBadge.setText(user.getTier().getDisplayName());

        Subscription activeSub = subscriptionService.getActiveSubscription(user.getId());
        if (activeSub != null) {
            expiryInfoLabel.setText("Active until: " + activeSub.getExpiryDate().toString());
        } else {
            expiryInfoLabel.setText("Free Devotee (No expiration)");
        }

        List<SimulatedPayment> payments = subscriptionService.getPaymentHistory(user.getId());
        paymentHistoryModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        for (SimulatedPayment sp : payments) {
            String timeStr = sp.getCreatedAt() != null ? sp.getCreatedAt().toLocalDateTime().format(dtf) : "Recent";
            paymentHistoryModel.addRow(new Object[]{
                    sp.getTransactionReference(),
                    user.getTier().getDisplayName(),
                    "₹" + sp.getAmount(),
                    sp.getCurrency(),
                    sp.getPaymentMethod(),
                    sp.getPaymentStatus(),
                    timeStr
            });
        }
    }
}
