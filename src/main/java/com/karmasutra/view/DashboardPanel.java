package com.karmasutra.view;

import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.service.KarmaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Editorial, data-driven Divine Overview dashboard displaying real database records,
 * contextual astrological weather, long-term wish milestones, and recent docket activity.
 */
public class DashboardPanel extends JPanel {

    private final User user;
    private final ComplaintService complaintService;
    private final KarmaService karmaService;
    private final Runnable onSubmitNewClicked;
    private final Consumer<Complaint> onOpenTicketDetails;

    // Stat metric labels
    private JLabel totalTicketsVal;
    private JLabel pendingTicketsVal;
    private JLabel resolvedTicketsVal;
    private JLabel karmaPointsVal;
    private JLabel cosmicStatusBanner;

    // Destiny Wish Progress Widget
    private JPanel wishProgressCard;
    private JLabel wishTitleLabel;
    private JLabel wishDeptLabel;
    private JProgressBar wishProgressBar;
    private JLabel wishPercentLabel;

    private JTable recentTable;
    private DefaultTableModel tableModel;
    private List<Complaint> recentComplaintsList;

    public DashboardPanel(User user, Runnable onSubmitNewClicked, Consumer<Complaint> onOpenTicketDetails) {
        this.user = user;
        this.complaintService = new ComplaintService();
        this.karmaService = new KarmaService();
        this.onSubmitNewClicked = onSubmitNewClicked;
        this.onOpenTicketDetails = onOpenTicketDetails;

        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // 1. TOP BANNER / GREETING AREA
        JPanel topBanner = new JPanel(new BorderLayout());
        topBanner.setOpaque(false);

        JPanel greetingCol = new JPanel();
        greetingCol.setLayout(new BoxLayout(greetingCol, BoxLayout.Y_AXIS));
        greetingCol.setOpaque(false);

        JLabel welcomeLbl = new JLabel("Divine Overview — " + (user != null ? user.getFullName() : "Petitioner"));
        welcomeLbl.setFont(Theme.FONT_HEADER_1);
        welcomeLbl.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel subGreeting = new JLabel("Official Akashic Docket Telemetry & Active Celestial Directorate");
        subGreeting.setFont(Theme.FONT_BODY);
        subGreeting.setForeground(Theme.COLOR_TEXT_MUTED);

        greetingCol.add(welcomeLbl);
        greetingCol.add(Box.createVerticalStrut(3));
        greetingCol.add(subGreeting);

        // Prominent "+ Submit a Grievance" Action Button
        JButton btnSubmitNew = Theme.createPrimaryButton("+ Submit a Grievance");
        btnSubmitNew.addActionListener(e -> {
            if (onSubmitNewClicked != null) {
                onSubmitNewClicked.run();
            }
        });

        topBanner.add(greetingCol, BorderLayout.WEST);
        topBanner.add(btnSubmitNew, BorderLayout.EAST);
        add(topBanner, BorderLayout.NORTH);

        // 2. CENTER CONTENT AREA: Dynamic Weather + Metrics + Wish Tracker + Recent Dockets
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // A. Dynamic Cosmic Status Message Banner
        JPanel weatherBanner = Theme.createCardPanel();
        weatherBanner.setLayout(new BorderLayout());
        weatherBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        weatherBanner.setPreferredSize(new Dimension(800, 44));

        cosmicStatusBanner = new JLabel(getDynamicCosmicStatus());
        cosmicStatusBanner.setFont(Theme.FONT_TAGLINE);
        cosmicStatusBanner.setForeground(Theme.COLOR_GOLD);
        weatherBanner.add(cosmicStatusBanner, BorderLayout.CENTER);

        centerPanel.add(weatherBanner);
        centerPanel.add(Box.createVerticalStrut(14));

        // B. 4 Metric KPI Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        metricsGrid.setPreferredSize(new Dimension(800, 88));

        totalTicketsVal = new JLabel("0");
        pendingTicketsVal = new JLabel("0");
        resolvedTicketsVal = new JLabel("0");
        karmaPointsVal = new JLabel(String.valueOf(user != null ? user.getKarmaPoints() : 108));

        metricsGrid.add(createStatCard("Total Petitions", totalTicketsVal, Theme.COLOR_TEXT_PRIMARY, "All registered dossiers"));
        metricsGrid.add(createStatCard("Pending in Queue", pendingTicketsVal, Theme.COLOR_GOLD, "Under heavenly deliberation"));
        metricsGrid.add(createStatCard("Cosmically Resolved", resolvedTicketsVal, Theme.COLOR_SAGE, "Final rulings stamped"));
        metricsGrid.add(createStatCard("Karma Balance", karmaPointsVal, Theme.COLOR_BURGUNDY, (user != null ? user.getTier().getDisplayName() : "Devotee")));

        centerPanel.add(metricsGrid);
        centerPanel.add(Box.createVerticalStrut(14));

        // C. Long-Term Destiny Wish Progress Widget
        wishProgressCard = createWishProgressWidget();
        centerPanel.add(wishProgressCard);
        centerPanel.add(Box.createVerticalStrut(16));

        // D. Recent Docket Activity Table Card
        JPanel activityHeader = new JPanel(new BorderLayout());
        activityHeader.setOpaque(false);
        activityHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel activityTitle = new JLabel("Recent Celestial Docket Activity");
        activityTitle.setFont(Theme.FONT_HEADER_2);
        activityTitle.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JButton btnRefresh = Theme.createSecondaryButton("Refresh Akashic Data");
        btnRefresh.setFont(Theme.FONT_SMALL);
        btnRefresh.addActionListener(e -> refreshData());

        activityHeader.add(activityTitle, BorderLayout.WEST);
        activityHeader.add(btnRefresh, BorderLayout.EAST);
        centerPanel.add(activityHeader);
        centerPanel.add(Box.createVerticalStrut(8));

        // Table container inside card
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Ticket ID", "Subject", "Category", "Type", "Status", "Department", "Filed On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentTable = new JTable(tableModel);
        recentTable.setRowHeight(40);
        recentTable.setShowVerticalLines(false);
        recentTable.setGridColor(Theme.COLOR_BORDER);
        recentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recentTable.setFont(Theme.FONT_BODY);

        // Custom Cell Renderers
        recentTable.getColumnModel().getColumn(0).setPreferredWidth(125);
        recentTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(Theme.FONT_MONO);
                setForeground(Theme.COLOR_GOLD);
                return c;
            }
        });

        recentTable.getColumnModel().getColumn(1).setPreferredWidth(260);

        recentTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        recentTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                if (value != null) {
                    TicketStatus status = TicketStatus.fromLabel(value.toString());
                    return Theme.createStatusBadge(status);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            }
        });

        // Double-click row listener to open details
        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recentTable.getSelectedRow() >= 0) {
                    int selected = recentTable.getSelectedRow();
                    if (recentComplaintsList != null && selected < recentComplaintsList.size()) {
                        Complaint c = recentComplaintsList.get(selected);
                        if (onOpenTicketDetails != null) {
                            onOpenTicketDetails.accept(c);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.COLOR_SURFACE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createWishProgressWidget() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(14, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        card.setPreferredSize(new Dimension(800, 72));

        // Left Info
        JPanel infoCol = new JPanel();
        infoCol.setLayout(new BoxLayout(infoCol, BoxLayout.Y_AXIS));
        infoCol.setOpaque(false);
        infoCol.setPreferredSize(new Dimension(280, 50));

        JLabel titleTag = new JLabel("✦ ACTIVE DESTINY WISH // MANIFESTATION ORBIT");
        titleTag.setFont(Theme.FONT_LABEL);
        titleTag.setForeground(Theme.COLOR_GOLD);

        wishTitleLabel = new JLabel("Incubating Cosmic Petition...");
        wishTitleLabel.setFont(Theme.FONT_BODY_BOLD);
        wishTitleLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);

        wishDeptLabel = new JLabel("Cosmic Architecture Bureau");
        wishDeptLabel.setFont(Theme.FONT_SMALL);
        wishDeptLabel.setForeground(Theme.COLOR_TEXT_MUTED);

        infoCol.add(titleTag);
        infoCol.add(Box.createVerticalStrut(2));
        infoCol.add(wishTitleLabel);
        infoCol.add(wishDeptLabel);
        card.add(infoCol, BorderLayout.WEST);

        // Center Progress Bar
        JPanel barCol = new JPanel();
        barCol.setLayout(new BoxLayout(barCol, BoxLayout.Y_AXIS));
        barCol.setOpaque(false);

        JPanel percentRow = new JPanel(new BorderLayout());
        percentRow.setOpaque(false);

        JLabel milestoneStages = new JLabel("Milestones: Docket Stamped ➔ Karmic Weight Audit ➔ Celestial Alignment");
        milestoneStages.setFont(Theme.FONT_SMALL);
        milestoneStages.setForeground(Theme.COLOR_TEXT_MUTED);

        wishPercentLabel = new JLabel("65% Alignment");
        wishPercentLabel.setFont(Theme.FONT_LABEL);
        wishPercentLabel.setForeground(Theme.COLOR_GOLD);

        percentRow.add(milestoneStages, BorderLayout.WEST);
        percentRow.add(wishPercentLabel, BorderLayout.EAST);

        wishProgressBar = new JProgressBar(0, 100);
        wishProgressBar.setValue(65);
        wishProgressBar.setForeground(Theme.COLOR_GOLD);
        wishProgressBar.setBackground(Theme.COLOR_INPUT_BG);
        wishProgressBar.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER, 1));
        wishProgressBar.setPreferredSize(new Dimension(300, 8));

        barCol.add(Box.createVerticalStrut(6));
        barCol.add(percentRow);
        barCol.add(Box.createVerticalStrut(6));
        barCol.add(wishProgressBar);
        card.add(barCol, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color valueColor, String subtext) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(Theme.FONT_LABEL);
        titleLbl.setForeground(Theme.COLOR_TEXT_MUTED);

        valueLabel.setFont(Theme.FONT_HEADER_1);
        valueLabel.setForeground(valueColor);

        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(Theme.FONT_SMALL);
        subLbl.setForeground(Theme.COLOR_TEXT_DIM);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(subLbl);
        return card;
    }

    private String getDynamicCosmicStatus() {
        int karma = user != null ? user.getKarmaPoints() : 108;
        if (karma >= 200) {
            return "✦ CELESTIAL ALIGNMENT: Your Akashic balance is exceptionally auspicious. Prayers to Kamadeva & Brahma receive tier-1 priority routing.";
        } else if (karma >= 100) {
            return "✦ COSMIC WEATHER // Transit of Saturn in Pisces: Retributive audits active. Lord Shani recommends patience in corporate endeavors.";
        } else {
            return "✦ ASTRAL ADVISORY: Karmic reserves low. Chitragupta suggests filing virtuous prayers or upgrading to PRO for accelerated bandwidth.";
        }
    }

    public void refreshData() {
        if (user == null) {
            return;
        }

        // 1. Fetch real counts from MySQL
        Map<String, Integer> counts = complaintService.getStatusCountsForUser(user.getId());
        int total = 0;
        int resolved = 0;
        int pending = 0;

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            total += entry.getValue();
            if (TicketStatus.RESOLVED.getLabel().equalsIgnoreCase(entry.getKey())) {
                resolved += entry.getValue();
            } else {
                pending += entry.getValue();
            }
        }

        totalTicketsVal.setText(String.valueOf(total));
        pendingTicketsVal.setText(String.valueOf(pending));
        resolvedTicketsVal.setText(String.valueOf(resolved));
        karmaPointsVal.setText(String.valueOf(user.getKarmaPoints()));
        cosmicStatusBanner.setText(getDynamicCosmicStatus());

        // 2. Fetch real recent complaints
        recentComplaintsList = complaintService.getComplaintsForUser(user.getId(), null, null, null);
        tableModel.setRowCount(0);

        Complaint topWish = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        for (Complaint c : recentComplaintsList) {
            if (topWish == null && (c.isLongTermWish() || "Wish".equalsIgnoreCase(c.getType().getLabel()))) {
                topWish = c;
            }
            String dateStr = c.getCreatedAt() != null ? c.getCreatedAt().toLocalDateTime().format(dtf) : "Pending";
            tableModel.addRow(new Object[]{
                    c.getTicketNumber(),
                    c.getSubject(),
                    c.getCategory().getLabel(),
                    c.getType().getLabel(),
                    c.getStatus().getLabel(),
                    c.getDepartmentName() != null ? c.getDepartmentName() : "General Docket",
                    dateStr
            });
        }

        // 3. Update Wish Progress Widget
        if (topWish != null) {
            wishTitleLabel.setText(topWish.getSubject());
            wishDeptLabel.setText("Department: " + (topWish.getDepartmentName() != null ? topWish.getDepartmentName() : "Cosmic Architecture"));
            int progress = topWish.getStatus() == TicketStatus.RESOLVED ? 100 : 65;
            wishProgressBar.setValue(progress);
            wishPercentLabel.setText(progress + "% Alignment");
        } else if (!recentComplaintsList.isEmpty()) {
            Complaint first = recentComplaintsList.get(0);
            wishTitleLabel.setText(first.getSubject());
            wishDeptLabel.setText("Active Petition: " + (first.getDepartmentName() != null ? first.getDepartmentName() : "General"));
            int progress = first.getStatus() == TicketStatus.RESOLVED ? 100 : 50;
            wishProgressBar.setValue(progress);
            wishPercentLabel.setText(progress + "% Deliberated");
        } else {
            wishTitleLabel.setText("No Active Long-Term Destiny Wish Registered");
            wishDeptLabel.setText("Submit a Wish to begin cosmic milestone incubation");
            wishProgressBar.setValue(15);
            wishPercentLabel.setText("Awaiting Petition");
        }
    }
}
