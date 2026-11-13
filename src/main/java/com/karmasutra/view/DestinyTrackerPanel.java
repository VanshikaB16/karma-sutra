package com.karmasutra.view;

import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ComplaintService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Destiny Tracker — Long-Term Wishes & Cosmic Milestones.
 * Tracks progress toward soul goals, manifestation stages, and celestial blessings.
 */
public class DestinyTrackerPanel extends JPanel {

    private final User user;
    private final ComplaintService complaintService;
    private final Runnable onRegisterWish;
    private final Consumer<Complaint> onOpenDetails;

    private JLabel activeWishCountVal;
    private JLabel avgAlignmentVal;
    private JLabel karmaBoostVal;
    private JPanel wishesListContainer;

    public DestinyTrackerPanel(User user, Runnable onRegisterWish, Consumer<Complaint> onOpenDetails) {
        this.user = user;
        this.complaintService = new ComplaintService();
        this.onRegisterWish = onRegisterWish;
        this.onOpenDetails = onOpenDetails;

        initializeUI();
        refreshWishes();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel title = new JLabel("Destiny Tracker — Cosmic Wishes & Milestones");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("Monitor celestial incubation, planetary alignments, and milestone approvals for your soul's petitions.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(3));
        titleCol.add(sub);
        headerPanel.add(titleCol, BorderLayout.WEST);

        JButton btnNewWish = Theme.createPrimaryButton("+ Register New Cosmic Wish");
        btnNewWish.addActionListener(e -> {
            if (onRegisterWish != null) {
                onRegisterWish.run();
            }
        });
        headerPanel.add(btnNewWish, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Area: KPI Metrics + Scrollable Wish Cards
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // KPI Bar
        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        kpiGrid.setPreferredSize(new Dimension(800, 76));

        activeWishCountVal = new JLabel("0");
        avgAlignmentVal = new JLabel("0%");
        karmaBoostVal = new JLabel(user != null ? user.getTier().getDisplayName() : "Standard Tier");

        kpiGrid.add(createKpiCard("Incubating Wishes", activeWishCountVal, Theme.COLOR_GOLD));
        kpiGrid.add(createKpiCard("Mean Celestial Alignment", avgAlignmentVal, Theme.COLOR_SAGE));
        kpiGrid.add(createKpiCard("Manifestation Velocity", karmaBoostVal, Theme.COLOR_BURGUNDY));

        centerPanel.add(kpiGrid);
        centerPanel.add(Box.createVerticalStrut(16));

        // Subhead for Wish Cards
        JLabel sectionTitle = new JLabel("Active Destiny Dossiers & Celestial Milestones");
        sectionTitle.setFont(Theme.FONT_HEADER_2);
        sectionTitle.setForeground(Theme.COLOR_TEXT_PRIMARY);
        centerPanel.add(sectionTitle);
        centerPanel.add(Box.createVerticalStrut(8));

        // Wish Cards Container
        wishesListContainer = new JPanel();
        wishesListContainer.setLayout(new BoxLayout(wishesListContainer, BoxLayout.Y_AXIS));
        wishesListContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(wishesListContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.COLOR_BG);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valLabel, Color valColor) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(Theme.FONT_LABEL);
        titleLbl.setForeground(Theme.COLOR_TEXT_MUTED);

        valLabel.setFont(Theme.FONT_HEADER_1);
        valLabel.setForeground(valColor);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        return card;
    }

    public void refreshWishes() {
        if (user == null) return;

        List<Complaint> allComplaints = complaintService.getComplaintsForUser(user.getId(), null, null, null);
        List<Complaint> wishes = new ArrayList<>();

        for (Complaint c : allComplaints) {
            if (c.isLongTermWish() || "Wish".equalsIgnoreCase(c.getType().getLabel()) || "Prayer".equalsIgnoreCase(c.getType().getLabel())) {
                wishes.add(c);
            }
        }

        activeWishCountVal.setText(String.valueOf(wishes.size()));
        int baseAlignment = wishes.isEmpty() ? 0 : Math.min(95, 40 + (user.getKarmaPoints() / 5));
        avgAlignmentVal.setText(wishes.isEmpty() ? "0%" : baseAlignment + "%");

        wishesListContainer.removeAll();

        if (wishes.isEmpty()) {
            JPanel emptyCard = Theme.createCardPanel();
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            emptyCard.setPreferredSize(new Dimension(800, 160));
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

            JLabel emptyTitle = new JLabel("✦ No Long-Term Destiny Wishes Registered Yet");
            emptyTitle.setFont(Theme.FONT_HEADER_2);
            emptyTitle.setForeground(Theme.COLOR_GOLD);

            JLabel emptyDesc = new JLabel("Submit a Wish or Prayer petition to initiate the 4-stage celestial manifestation protocol.");
            emptyDesc.setFont(Theme.FONT_BODY);
            emptyDesc.setForeground(Theme.COLOR_TEXT_MUTED);

            JButton btnStartWish = Theme.createGoldButton("+ Register Your First Destiny Wish");
            btnStartWish.addActionListener(e -> {
                if (onRegisterWish != null) onRegisterWish.run();
            });

            emptyCard.add(emptyTitle);
            emptyCard.add(Box.createVerticalStrut(6));
            emptyCard.add(emptyDesc);
            emptyCard.add(Box.createVerticalStrut(14));
            emptyCard.add(btnStartWish);

            wishesListContainer.add(emptyCard);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
            for (Complaint w : wishes) {
                JPanel wishCard = createSingleWishCard(w, dtf);
                wishesListContainer.add(wishCard);
                wishesListContainer.add(Box.createVerticalStrut(12));
            }
        }

        wishesListContainer.revalidate();
        wishesListContainer.repaint();
    }

    private JPanel createSingleWishCard(Complaint w, DateTimeFormatter dtf) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(14, 8));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setPreferredSize(new Dimension(800, 125));

        // Top Row: Ticket ID + Status Badge + Date
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);
        leftBadges.add(Theme.createBadge(w.getTicketNumber(), Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD));
        leftBadges.add(Theme.createStatusBadge(w.getStatus()));
        leftBadges.add(Theme.createBadge(w.getCategory().getLabel(), Theme.COLOR_BURGUNDY_LIGHT, Theme.COLOR_BURGUNDY));

        String dateStr = w.getCreatedAt() != null ? w.getCreatedAt().toLocalDateTime().format(dtf) : "Queued";
        JLabel dateLbl = new JLabel("Enacted: " + dateStr);
        dateLbl.setFont(Theme.FONT_SMALL);
        dateLbl.setForeground(Theme.COLOR_TEXT_MUTED);

        topRow.add(leftBadges, BorderLayout.WEST);
        topRow.add(dateLbl, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Center: Subject & Milestones Progress Bar
        JPanel centerCol = new JPanel();
        centerCol.setLayout(new BoxLayout(centerCol, BoxLayout.Y_AXIS));
        centerCol.setOpaque(false);

        JLabel subjectLbl = new JLabel(w.getSubject());
        subjectLbl.setFont(Theme.FONT_HEADER_2);
        subjectLbl.setForeground(Theme.COLOR_TEXT_PRIMARY);

        int progress = w.getStatus() == TicketStatus.RESOLVED ? 100 : 65;

        JPanel milestoneRow = new JPanel(new BorderLayout());
        milestoneRow.setOpaque(false);

        JLabel milestoneText = new JLabel("Milestones: 1. Stamped ➔ 2. Karmic Weighing ➔ 3. Astrological Alignment ➔ 4. Manifestation");
        milestoneText.setFont(Theme.FONT_SMALL);
        milestoneText.setForeground(Theme.COLOR_TEXT_MUTED);

        JLabel pctLabel = new JLabel(progress + "% Cosmic Incubation");
        pctLabel.setFont(Theme.FONT_LABEL);
        pctLabel.setForeground(progress == 100 ? Theme.COLOR_SAGE : Theme.COLOR_GOLD);

        milestoneRow.add(milestoneText, BorderLayout.WEST);
        milestoneRow.add(pctLabel, BorderLayout.EAST);

        JProgressBar pBar = new JProgressBar(0, 100);
        pBar.setValue(progress);
        pBar.setForeground(progress == 100 ? Theme.COLOR_SAGE : Theme.COLOR_GOLD);
        pBar.setBackground(Theme.COLOR_INPUT_BG);
        pBar.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER, 1));
        pBar.setPreferredSize(new Dimension(400, 6));

        centerCol.add(subjectLbl);
        centerCol.add(Box.createVerticalStrut(4));
        centerCol.add(milestoneRow);
        centerCol.add(Box.createVerticalStrut(4));
        centerCol.add(pBar);

        card.add(centerCol, BorderLayout.CENTER);

        // Right CTA: Inspect
        JButton btnInspect = Theme.createSecondaryButton("Inspect Decree");
        btnInspect.setFont(Theme.FONT_SMALL);
        btnInspect.addActionListener(e -> {
            if (onOpenDetails != null) onOpenDetails.accept(w);
        });
        JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
        rightCol.setOpaque(false);
        rightCol.add(btnInspect);
        card.add(rightCol, BorderLayout.EAST);

        return card;
    }
}
