package com.karmasutra.view;

import com.karmasutra.dao.ResponseDAO;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ComplaintService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Editorial Cosmic Ticket Tracking Docket.
 * Features summary KPI metrics, multi-parameter filtering, table view,
 * and contextual live celestial decree preview to eliminate vertical voids.
 */
public class TicketListPanel extends JPanel {

    private final User user;
    private final ComplaintService complaintService;
    private final ResponseDAO responseDAO;
    private final Consumer<Complaint> onOpenDetails;
    private final Runnable onFileNewGrievance;

    // Filter controls
    private JTextField searchField;
    private JComboBox<String> categoryFilterBox;
    private JComboBox<String> statusFilterBox;
    private JComboBox<String> priorityFilterBox;
    private JComboBox<String> dateFilterBox;

    // Summary Metric Labels
    private JLabel totalCountLbl;
    private JLabel pendingCountLbl;
    private JLabel resolvedCountLbl;
    private JLabel criticalCountLbl;

    // Table & Data
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private List<Complaint> currentList;
    private JLabel recordCountLabel;

    // Live Decree Preview Components
    private JPanel previewCard;
    private JLabel previewTicketBadge;
    private JLabel previewDeptLabel;
    private JLabel previewSubjectLabel;
    private JTextArea previewComplaintText;
    private JLabel previewDeityPersona;
    private JTextArea previewDecreeText;
    private JButton btnInspectDossier;
    private Complaint selectedComplaint;

    public TicketListPanel(User user, Consumer<Complaint> onOpenDetails) {
        this(user, onOpenDetails, null);
    }

    public TicketListPanel(User user, Consumer<Complaint> onOpenDetails, Runnable onFileNewGrievance) {
        this.user = user;
        this.complaintService = new ComplaintService();
        this.responseDAO = new ResponseDAO();
        this.onOpenDetails = onOpenDetails;
        this.onFileNewGrievance = onFileNewGrievance;

        initializeUI();
        refreshTickets();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Theme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // 1. TOP HEADER & SUMMARY KPIS
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Header Title Bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel title = new JLabel("Cosmic Ticket Tracking Docket");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Inspect docket numbers, celestial routing, and response statuses across all submissions.");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.COLOR_TEXT_MUTED);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(3));
        titleCol.add(subtitle);
        headerBar.add(titleCol, BorderLayout.WEST);

        // Prominent "File a Grievance" Button
        if (onFileNewGrievance != null) {
            JButton btnFileNew = Theme.createPrimaryButton("+ File a Grievance");
            btnFileNew.addActionListener(e -> onFileNewGrievance.run());
            headerBar.add(btnFileNew, BorderLayout.EAST);
        }

        topContainer.add(headerBar);
        topContainer.add(Box.createVerticalStrut(14));

        // Compact Summary Metric KPIs
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        kpiGrid.setPreferredSize(new Dimension(800, 70));

        totalCountLbl = new JLabel("0");
        pendingCountLbl = new JLabel("0");
        resolvedCountLbl = new JLabel("0");
        criticalCountLbl = new JLabel("0");

        kpiGrid.add(createMiniKpiCard("Total Petitions", totalCountLbl, Theme.COLOR_TEXT_PRIMARY, "All recorded grievances"));
        kpiGrid.add(createMiniKpiCard("Awaiting Review", pendingCountLbl, Theme.COLOR_GOLD, "In heavenly deliberation"));
        kpiGrid.add(createMiniKpiCard("Cosmically Resolved", resolvedCountLbl, Theme.COLOR_SAGE, "Final decrees executed"));
        kpiGrid.add(createMiniKpiCard("Critical Priority", criticalCountLbl, Theme.COLOR_CRIMSON, "Urgent celestial triage"));

        topContainer.add(kpiGrid);
        topContainer.add(Box.createVerticalStrut(12));

        // 2. MULTI-PARAMETER FILTER TOOLBAR
        JPanel toolbar = Theme.createCardPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 7, 4));

        // Search Input
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(Theme.FONT_SMALL);
        lblSearch.setForeground(Theme.COLOR_TEXT_MUTED);
        toolbar.add(lblSearch);

        searchField = Theme.createTextField("ID or keyword...");
        searchField.setPreferredSize(new Dimension(125, 28));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });
        toolbar.add(searchField);

        // Category Filter
        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(Theme.FONT_SMALL);
        lblCat.setForeground(Theme.COLOR_TEXT_MUTED);
        toolbar.add(lblCat);

        categoryFilterBox = new JComboBox<>();
        categoryFilterBox.addItem("All Categories");
        for (ComplaintCategory c : ComplaintCategory.values()) {
            categoryFilterBox.addItem(c.getLabel());
        }
        categoryFilterBox.setFont(Theme.FONT_SMALL);
        categoryFilterBox.setPreferredSize(new Dimension(135, 28));
        categoryFilterBox.addActionListener(e -> applyFilters());
        toolbar.add(categoryFilterBox);

        // Status Filter
        JLabel lblStat = new JLabel("Status:");
        lblStat.setFont(Theme.FONT_SMALL);
        lblStat.setForeground(Theme.COLOR_TEXT_MUTED);
        toolbar.add(lblStat);

        statusFilterBox = new JComboBox<>();
        statusFilterBox.addItem("All Statuses");
        for (TicketStatus s : TicketStatus.values()) {
            statusFilterBox.addItem(s.getLabel());
        }
        statusFilterBox.setFont(Theme.FONT_SMALL);
        statusFilterBox.setPreferredSize(new Dimension(115, 28));
        statusFilterBox.addActionListener(e -> applyFilters());
        toolbar.add(statusFilterBox);

        // Priority Filter
        JLabel lblPri = new JLabel("Priority:");
        lblPri.setFont(Theme.FONT_SMALL);
        lblPri.setForeground(Theme.COLOR_TEXT_MUTED);
        toolbar.add(lblPri);

        priorityFilterBox = new JComboBox<>(new String[]{"All Priorities", "Critical", "High", "Medium", "Low"});
        priorityFilterBox.setFont(Theme.FONT_SMALL);
        priorityFilterBox.setPreferredSize(new Dimension(105, 28));
        priorityFilterBox.addActionListener(e -> applyFilters());
        toolbar.add(priorityFilterBox);

        // Date Filter
        JLabel lblDate = new JLabel("Date:");
        lblDate.setFont(Theme.FONT_SMALL);
        lblDate.setForeground(Theme.COLOR_TEXT_MUTED);
        toolbar.add(lblDate);

        dateFilterBox = new JComboBox<>(new String[]{"All Time", "Today", "Past 7 Days", "Past 30 Days"});
        dateFilterBox.setFont(Theme.FONT_SMALL);
        dateFilterBox.setPreferredSize(new Dimension(95, 28));
        dateFilterBox.addActionListener(e -> applyFilters());
        toolbar.add(dateFilterBox);

        // Reset Filters Button
        JButton btnReset = Theme.createSecondaryButton("Reset");
        btnReset.setFont(Theme.FONT_SMALL);
        btnReset.setPreferredSize(new Dimension(65, 28));
        btnReset.addActionListener(e -> {
            searchField.setText("");
            categoryFilterBox.setSelectedIndex(0);
            statusFilterBox.setSelectedIndex(0);
            priorityFilterBox.setSelectedIndex(0);
            dateFilterBox.setSelectedIndex(0);
            applyFilters();
        });
        toolbar.add(btnReset);

        topContainer.add(toolbar);
        add(topContainer, BorderLayout.NORTH);

        // 3. CENTER & BOTTOM: SPLIT VIEW (TABLE ON TOP + LIVE DECREE PREVIEW ON BOTTOM)
        JPanel centerContainer = new JPanel(new BorderLayout(0, 12));
        centerContainer.setOpaque(false);

        // Table Card
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        // Header strip for table
        JPanel tableHeaderStrip = new JPanel(new BorderLayout());
        tableHeaderStrip.setOpaque(false);
        tableHeaderStrip.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));

        recordCountLabel = new JLabel("Loading dockets...");
        recordCountLabel.setFont(Theme.FONT_LABEL);
        recordCountLabel.setForeground(Theme.COLOR_TEXT_MUTED);
        tableHeaderStrip.add(recordCountLabel, BorderLayout.WEST);

        JLabel tableHint = new JLabel("Click row to preview divine decree • Double-click for full dossier");
        tableHint.setFont(Theme.FONT_SMALL);
        tableHint.setForeground(Theme.COLOR_GOLD);
        tableHeaderStrip.add(tableHint, BorderLayout.EAST);
        tableCard.add(tableHeaderStrip, BorderLayout.NORTH);

        String[] cols = {"Ticket ID", "Subject", "Category", "Type", "Priority", "Status", "Department", "Date Filed"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        ticketTable = new JTable(tableModel);
        ticketTable.setRowHeight(42);
        ticketTable.setShowVerticalLines(false);
        ticketTable.setGridColor(Theme.COLOR_BORDER);
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketTable.setFont(Theme.FONT_BODY);

        // Column Renderers & Sizing
        ticketTable.getColumnModel().getColumn(0).setPreferredWidth(125);
        ticketTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
                setFont(Theme.FONT_MONO);
                setForeground(Theme.COLOR_GOLD);
                return c;
            }
        });

        ticketTable.getColumnModel().getColumn(1).setPreferredWidth(260);

        ticketTable.getColumnModel().getColumn(4).setPreferredWidth(85);
        ticketTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                if (val != null) {
                    Severity sev = Severity.fromLabel(val.toString());
                    return Theme.createSeverityBadge(sev);
                }
                return super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
            }
        });

        ticketTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        ticketTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                if (val != null) {
                    TicketStatus st = TicketStatus.fromLabel(val.toString());
                    return Theme.createStatusBadge(st);
                }
                return super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
            }
        });

        // Selection Listener: Updates the live decree preview panel immediately
        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedPreview();
            }
        });

        ticketTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ticketTable.getSelectedRow() >= 0) {
                    openSelectedTicket();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.COLOR_SURFACE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerContainer.add(tableCard, BorderLayout.CENTER);

        // 4. BOTTOM CELESTIAL DECREE DOSSIER PREVIEW (ELIMINATES EMPTY VOID)
        previewCard = createLiveDecreePreviewPanel();
        centerContainer.add(previewCard, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createMiniKpiCard(String title, JLabel valueLabel, Color accentColor, String tooltip) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setToolTipText(tooltip);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(Theme.FONT_LABEL);
        titleLbl.setForeground(Theme.COLOR_TEXT_MUTED);

        valueLabel.setFont(Theme.FONT_HEADER_1);
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createLiveDecreePreviewPanel() {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(14, 0));
        card.setPreferredSize(new Dimension(800, 180));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Left Column: Selected Petition Dossier
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);
        leftCol.setPreferredSize(new Dimension(320, 160));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badgeRow.setOpaque(false);

        previewTicketBadge = Theme.createBadge("SELECT DOCKET", Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD);
        previewDeptLabel = new JLabel("Cosmic Bureau");
        previewDeptLabel.setFont(Theme.FONT_SMALL);
        previewDeptLabel.setForeground(Theme.COLOR_TEXT_MUTED);

        badgeRow.add(previewTicketBadge);
        badgeRow.add(previewDeptLabel);
        leftCol.add(badgeRow);
        leftCol.add(Box.createVerticalStrut(6));

        previewSubjectLabel = new JLabel("Select a petition from the table above");
        previewSubjectLabel.setFont(Theme.FONT_HEADER_3);
        previewSubjectLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);
        leftCol.add(previewSubjectLabel);
        leftCol.add(Box.createVerticalStrut(6));

        previewComplaintText = new JTextArea("Petition description and context will appear here upon row selection.");
        previewComplaintText.setFont(Theme.FONT_SMALL);
        previewComplaintText.setForeground(Theme.COLOR_TEXT_MUTED);
        previewComplaintText.setBackground(Theme.COLOR_INPUT_BG);
        previewComplaintText.setEditable(false);
        previewComplaintText.setLineWrap(true);
        previewComplaintText.setWrapStyleWord(true);
        previewComplaintText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane leftScroll = new JScrollPane(previewComplaintText);
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        leftCol.add(leftScroll);
        card.add(leftCol, BorderLayout.WEST);

        // Right Column: Official Divine Decree / AI Response Box
        JPanel rightCol = new JPanel(new BorderLayout(0, 6));
        rightCol.setOpaque(false);

        JPanel decreeHeader = new JPanel(new BorderLayout());
        decreeHeader.setOpaque(false);

        JLabel decreeTitle = new JLabel("✦ OFFICIAL PLANETARY RESPONSE & DECREE");
        decreeTitle.setFont(Theme.FONT_LABEL);
        decreeTitle.setForeground(Theme.COLOR_GOLD);

        previewDeityPersona = new JLabel("Awaiting Celestial Ruling");
        previewDeityPersona.setFont(Theme.FONT_SMALL);
        previewDeityPersona.setForeground(Theme.COLOR_BURGUNDY);

        decreeHeader.add(decreeTitle, BorderLayout.WEST);
        decreeHeader.add(previewDeityPersona, BorderLayout.EAST);
        rightCol.add(decreeHeader, BorderLayout.NORTH);

        previewDecreeText = new JTextArea("Divine decree text will illuminate here when a docket is selected.");
        previewDecreeText.setFont(Theme.FONT_SERIF_BODY);
        previewDecreeText.setForeground(Theme.COLOR_TEXT_PRIMARY);
        previewDecreeText.setBackground(new Color(0x10, 0x12, 0x1A));
        previewDecreeText.setEditable(false);
        previewDecreeText.setLineWrap(true);
        previewDecreeText.setWrapStyleWord(true);
        previewDecreeText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_BORDER_ACCENT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane rightScroll = new JScrollPane(previewDecreeText);
        rightScroll.setBorder(BorderFactory.createEmptyBorder());
        rightCol.add(rightScroll, BorderLayout.CENTER);

        // Action footer
        JPanel actionFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionFooter.setOpaque(false);

        btnInspectDossier = Theme.createPrimaryButton("Inspect Full Dossier");
        btnInspectDossier.setFont(Theme.FONT_SMALL);
        btnInspectDossier.setEnabled(false);
        btnInspectDossier.addActionListener(e -> openSelectedTicket());
        actionFooter.add(btnInspectDossier);

        rightCol.add(actionFooter, BorderLayout.SOUTH);
        card.add(rightCol, BorderLayout.CENTER);

        return card;
    }

    private void updateSelectedPreview() {
        int selected = ticketTable.getSelectedRow();
        if (selected >= 0 && currentList != null && selected < currentList.size()) {
            selectedComplaint = currentList.get(selected);

            previewTicketBadge.setText(selectedComplaint.getTicketNumber());
            previewDeptLabel.setText(selectedComplaint.getDepartmentName() != null ? selectedComplaint.getDepartmentName() : "Cosmic General Bureau");
            previewSubjectLabel.setText(selectedComplaint.getSubject());
            previewComplaintText.setText(selectedComplaint.getDescription() != null ? selectedComplaint.getDescription() : "No submission dossier description recorded.");

            // Fetch live response
            ComplaintResponse latestResp = responseDAO.getLatestByComplaintId(selectedComplaint.getId());
            if (latestResp != null) {
                previewDeityPersona.setText(latestResp.getDivinePersona() != null ? latestResp.getDivinePersona() : "Chitragupta, Cosmic Registrar");
                previewDecreeText.setText(latestResp.getResponseText());
            } else {
                previewDeityPersona.setText("Pending Deliberation");
                previewDecreeText.setText("This petition is currently queued in heaven. The assigned divine department is analyzing your astrological karmic chart.");
            }
            btnInspectDossier.setEnabled(true);
        } else {
            selectedComplaint = null;
            previewTicketBadge.setText("NO SELECTION");
            previewDeptLabel.setText("");
            previewSubjectLabel.setText("Select a petition from the table above");
            previewComplaintText.setText("No docket currently selected.");
            previewDeityPersona.setText("Awaiting Celestial Ruling");
            previewDecreeText.setText("Click on any docket in the table above to read the full celestial response decree.");
            btnInspectDossier.setEnabled(false);
        }
    }

    private void openSelectedTicket() {
        if (selectedComplaint != null && onOpenDetails != null) {
            onOpenDetails.accept(selectedComplaint);
        }
    }

    public void refreshTickets() {
        applyFilters();
    }

    private void applyFilters() {
        if (user == null) {
            return;
        }

        String search = searchField != null ? searchField.getText().trim() : null;
        String cat = categoryFilterBox != null ? (String) categoryFilterBox.getSelectedItem() : null;
        String status = statusFilterBox != null ? (String) statusFilterBox.getSelectedItem() : null;
        String priority = priorityFilterBox != null ? (String) priorityFilterBox.getSelectedItem() : null;
        String dateFilter = dateFilterBox != null ? (String) dateFilterBox.getSelectedItem() : null;

        // Fetch user complaints from database
        List<Complaint> rawList = complaintService.getComplaintsForUser(user.getId(), cat, status, search);
        currentList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        int pendingCount = 0;
        int resolvedCount = 0;
        int criticalCount = 0;

        for (Complaint c : rawList) {
            // Apply Priority Filter
            if (priority != null && !"All Priorities".equals(priority)) {
                if (!c.getSeverity().getLabel().equalsIgnoreCase(priority)) {
                    continue;
                }
            }

            // Apply Date Filter
            if (dateFilter != null && !"All Time".equals(dateFilter) && c.getCreatedAt() != null) {
                LocalDateTime created = c.getCreatedAt().toLocalDateTime();
                if ("Today".equals(dateFilter) && created.toLocalDate().isBefore(now.toLocalDate())) {
                    continue;
                } else if ("Past 7 Days".equals(dateFilter) && created.isBefore(now.minusDays(7))) {
                    continue;
                } else if ("Past 30 Days".equals(dateFilter) && created.isBefore(now.minusDays(30))) {
                    continue;
                }
            }

            currentList.add(c);

            // Compute KPIs
            if (c.getStatus() == TicketStatus.RESOLVED) {
                resolvedCount++;
            } else {
                pendingCount++;
            }
            if (c.getSeverity() == Severity.COSMIC_CRITICAL || c.getSeverity() == Severity.HIGH) {
                criticalCount++;
            }
        }

        // Update KPI card labels
        totalCountLbl.setText(String.valueOf(currentList.size()));
        pendingCountLbl.setText(String.valueOf(pendingCount));
        resolvedCountLbl.setText(String.valueOf(resolvedCount));
        criticalCountLbl.setText(String.valueOf(criticalCount));

        recordCountLabel.setText("Displaying " + currentList.size() + " active celestial petition" + (currentList.size() == 1 ? "" : "s"));

        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
        for (Complaint c : currentList) {
            String dateStr = c.getCreatedAt() != null ? c.getCreatedAt().toLocalDateTime().format(dtf) : "Pending";
            tableModel.addRow(new Object[]{
                    c.getTicketNumber(),
                    c.getSubject(),
                    c.getCategory().getLabel(),
                    c.getType().getLabel(),
                    c.getSeverity().getLabel(),
                    c.getStatus().getLabel(),
                    c.getDepartmentName() != null ? c.getDepartmentName() : "Cosmic General Bureau",
                    dateStr
            });
        }

        // Auto-select first row if available so the preview is immediately illuminated!
        if (!currentList.isEmpty()) {
            ticketTable.setRowSelectionInterval(0, 0);
            updateSelectedPreview();
        } else {
            updateSelectedPreview();
        }
    }
}
