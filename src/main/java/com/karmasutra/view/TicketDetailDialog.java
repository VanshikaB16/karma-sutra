package com.karmasutra.view;

import com.karmasutra.exception.DivineServiceUnavailableException;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.ComplaintStatusHistory;
import com.karmasutra.model.Department;
import com.karmasutra.model.User;
import com.karmasutra.model.WishMilestone;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ComplaintService;

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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Editorial, comprehensive ticket details modal displaying:
 * - Sacred metadata & polymorphic classification code
 * - Generated AI divine counsel & multiple perspectives
 * - Official departmental audit trail
 * - Wish milestones (if long-term aspiration)
 */
public class TicketDetailDialog extends JDialog {

    private final Complaint complaint;
    private final User currentUser;
    private final ComplaintService complaintService;
    private final Runnable onUpdated;

    private JTextArea aiResponseArea;
    private JLabel aiPersonaLabel;
    private JLabel aiToneLabel;
    private JButton btnNewPerspective;
    private JProgressBar progressBar;

    public TicketDetailDialog(JFrame parent, Complaint complaint, User currentUser, Runnable onUpdated) {
        super(parent, "Celestial Docket — " + complaint.getTicketNumber(), true);
        this.complaint = complaint;
        this.currentUser = currentUser;
        this.complaintService = new ComplaintService();
        this.onUpdated = onUpdated;

        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.COLOR_BG);
        setPreferredSize(new Dimension(740, 720));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel ticketIdLbl = new JLabel(complaint.getTicketNumber());
        ticketIdLbl.setFont(Theme.FONT_BRAND);
        ticketIdLbl.setForeground(Theme.COLOR_GOLD);

        JLabel subjectLbl = new JLabel(complaint.getSubject());
        subjectLbl.setFont(Theme.FONT_HEADER_2);
        subjectLbl.setForeground(Theme.COLOR_TEXT_PRIMARY);

        titleCol.add(ticketIdLbl);
        titleCol.add(Box.createVerticalStrut(2));
        titleCol.add(subjectLbl);

        // Status Badge at top-right
        JPanel statusCol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusCol.setOpaque(false);
        statusCol.add(Theme.createStatusBadge(complaint.getStatus()));
        statusCol.add(Theme.createSeverityBadge(complaint.getSeverity()));

        header.add(titleCol, BorderLayout.WEST);
        header.add(statusCol, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Tabbed Pane for clean organization
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY_BOLD);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        tabs.addTab("Divine Counsel & Response", createDivineResponseTab());
        tabs.addTab("Petition Dossier", createDossierTab());
        tabs.addTab("Departmental Audit Trail", createAuditTrailTab());

        if (complaint.isLongTermWish()) {
            tabs.addTab("Destiny Milestones", createMilestonesTab());
        }

        if (currentUser != null && currentUser.isAdmin()) {
            tabs.addTab("Arch-Deity Adjudication", createAdminAdjudicationTab());
        }

        add(tabs, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(8, 24, 18, 24));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        bottomBar.add(progressBar, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnRow.setOpaque(false);

        JButton btnClose = Theme.createSecondaryButton("Close Docket");
        btnClose.addActionListener(e -> dispose());
        btnRow.add(btnClose);

        bottomBar.add(btnRow, BorderLayout.SOUTH);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel createDivineResponseTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));

        // Sub-header with Persona & Tone
        JPanel metaRow = new JPanel(new BorderLayout());
        metaRow.setOpaque(false);

        aiPersonaLabel = new JLabel("Presiding Signatory: Loading...");
        aiPersonaLabel.setFont(Theme.FONT_HEADER_3);
        aiPersonaLabel.setForeground(Theme.COLOR_MAROON);

        aiToneLabel = new JLabel("Tone: Neutral Bureaucratic");
        aiToneLabel.setFont(Theme.FONT_SMALL);
        aiToneLabel.setForeground(Theme.COLOR_TEXT_MUTED);

        metaRow.add(aiPersonaLabel, BorderLayout.WEST);
        metaRow.add(aiToneLabel, BorderLayout.EAST);
        p.add(metaRow, BorderLayout.NORTH);

        // Parchment Card for Response Text
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        aiResponseArea = new JTextArea();
        aiResponseArea.setEditable(false);
        aiResponseArea.setLineWrap(true);
        aiResponseArea.setWrapStyleWord(true);
        aiResponseArea.setFont(Theme.FONT_BODY);
        aiResponseArea.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JScrollPane scroll = new JScrollPane(aiResponseArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);

        // Bottom request another perspective action
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);

        btnNewPerspective = Theme.createGoldButton("✨ Request Alternate Divine Perspective");
        btnNewPerspective.addActionListener(e -> requestNewPerspectiveAsync());
        actionRow.add(btnNewPerspective);

        p.add(actionRow, BorderLayout.SOUTH);

        loadDivineResponses();
        return p;
    }

    private void loadDivineResponses() {
        List<ComplaintResponse> responses = complaintService.getResponsesForComplaint(complaint.getId());
        if (responses != null && !responses.isEmpty()) {
            ComplaintResponse latest = responses.get(responses.size() - 1);
            aiPersonaLabel.setText("Signatory: " + latest.getDivinePersona() + " (Perspective #" + latest.getIterationNumber() + ")");
            aiToneLabel.setText("Classification Tone: " + latest.getEmotionalTone());
            aiResponseArea.setText(latest.getResponseText());
            aiResponseArea.setCaretPosition(0);

            // Limit perspectives to reasonable number
            if (responses.size() >= 3) {
                btnNewPerspective.setEnabled(false);
                btnNewPerspective.setText("Max Celestial Perspectives Reached (3/3)");
            }
        } else {
            aiPersonaLabel.setText("Awaiting Celestial Deliberation");
            aiToneLabel.setText("Status: In Queue");
            aiResponseArea.setText("Your petition is currently pending review by the assigned cosmic department. Click below to request immediate divine synthesis.");
            aiResponseArea.setCaretPosition(0);
            btnNewPerspective.setText("⚡ Generate Initial Divine Counsel");
        }
    }

    private void requestNewPerspectiveAsync() {
        btnNewPerspective.setEnabled(false);
        progressBar.setVisible(true);

        SwingWorker<ComplaintResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected ComplaintResponse doInBackground() throws Exception {
                List<ComplaintResponse> existing = complaintService.getResponsesForComplaint(complaint.getId());
                int nextIteration = existing != null ? existing.size() + 1 : 1;
                return complaintService.generateAIResponse(complaint, nextIteration);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                btnNewPerspective.setEnabled(true);
                try {
                    ComplaintResponse res = get();
                    loadDivineResponses();
                    if (onUpdated != null) {
                        onUpdated.run();
                    }
                    JOptionPane.showMessageDialog(TicketDetailDialog.this,
                            "A new perspective has been handed down from the high celestial council.",
                            "Divine Counsel Delivered", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TicketDetailDialog.this,
                            "Celestial Gateway Notice: " + ex.getMessage(),
                            "Transmission Delay", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private JPanel createDossierTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Metadata grid
        JPanel metaGrid = new JPanel(new GridLayout(3, 2, 16, 10));
        metaGrid.setOpaque(false);

        metaGrid.add(createMetaItem("Department", complaint.getDepartmentName() != null ? complaint.getDepartmentName() : "Cosmic General Bureau"));
        metaGrid.add(createMetaItem("Bureaucratic Code", complaint.getBureaucraticClassification()));
        metaGrid.add(createMetaItem("Category", complaint.getCategory().getLabel()));
        metaGrid.add(createMetaItem("Petition Type", complaint.getType().getLabel()));
        metaGrid.add(createMetaItem("Karma Weight", complaint.calculateKarmaWeight() + " Karma Points"));
        metaGrid.add(createMetaItem("Escalation Route", complaint.getSuggestedEscalationLevel()));

        card.add(metaGrid);
        card.add(Box.createVerticalStrut(14));

        // Description
        JLabel descLabel = new JLabel("SUBMITTED DESCRIPTION");
        descLabel.setFont(Theme.FONT_LABEL);
        descLabel.setForeground(Theme.COLOR_TEXT_MUTED);
        card.add(descLabel);
        card.add(Box.createVerticalStrut(4));

        JTextArea descArea = new JTextArea(complaint.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(Theme.FONT_BODY);
        descArea.setCaretPosition(0);
        card.add(new JScrollPane(descArea));
        card.add(Box.createVerticalStrut(12));

        if (complaint.getDesiredResolution() != null && !complaint.getDesiredResolution().isBlank()) {
            JLabel resLabel = new JLabel("COVETED DESIRED RESOLUTION");
            resLabel.setFont(Theme.FONT_LABEL);
            resLabel.setForeground(Theme.COLOR_TEXT_MUTED);
            card.add(resLabel);
            card.add(Box.createVerticalStrut(4));

            JLabel resVal = new JLabel(complaint.getDesiredResolution());
            resVal.setFont(Theme.FONT_BODY);
            card.add(resVal);
        }

        p.add(card);
        return p;
    }

    private JPanel createMetaItem(String key, String val) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setOpaque(false);

        JLabel k = new JLabel(key.toUpperCase());
        k.setFont(Theme.FONT_LABEL);
        k.setForeground(Theme.COLOR_TEXT_MUTED);

        JLabel v = new JLabel(val);
        v.setFont(Theme.FONT_BODY_BOLD);
        v.setForeground(Theme.COLOR_TEXT_PRIMARY);

        item.add(k);
        item.add(Box.createVerticalStrut(2));
        item.add(v);
        return item;
    }

    private JPanel createAuditTrailTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        String[] cols = {"Timestamp", "Previous Status", "New Status", "Audited By", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<ComplaintStatusHistory> history = complaintService.getStatusHistory(complaint.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        if (history != null) {
            for (ComplaintStatusHistory h : history) {
                String timeStr = h.getChangedAt() != null ? h.getChangedAt().toLocalDateTime().format(dtf) : "Cosmic Era";
                model.addRow(new Object[]{
                        timeStr,
                        h.getOldStatus(),
                        h.getNewStatus(),
                        h.getChangedByUsername() != null ? h.getChangedByUsername() : "Celestial System",
                        h.getRemarks() != null ? h.getRemarks() : "Standard transition"
                });
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.COLOR_BORDER);
        table.setFont(Theme.FONT_BODY);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel createMilestonesTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        String[] cols = {"Milestone Title", "Target Horizon", "Status", "Cosmic Assessment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<WishMilestone> milestones = complaintService.getMilestones(complaint.getId());
        if (milestones != null) {
            for (WishMilestone m : milestones) {
                model.addRow(new Object[]{
                        m.getTitle(),
                        m.getTargetDate() != null ? m.getTargetDate().toString() : "TBD",
                        m.getStatus(),
                        m.getCosmicNote() != null ? m.getCosmicNote() : "Under cosmic surveillance"
                });
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(Theme.FONT_BODY);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel createAdminAdjudicationTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Arch-Deity Adjudication Controls");
        title.setFont(Theme.FONT_HEADER_2);
        title.setForeground(Theme.COLOR_MAROON);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        // Reassign Department
        JLabel lblDept = new JLabel("REASSIGN COSMIC DEPARTMENT");
        lblDept.setFont(Theme.FONT_LABEL);
        card.add(lblDept);
        card.add(Box.createVerticalStrut(4));

        List<Department> departments = complaintService.getAllDepartments();
        JComboBox<Department> deptCombo = new JComboBox<>(departments.toArray(new Department[0]));
        for (Department d : departments) {
            if (d.getId() == complaint.getDepartmentId()) {
                deptCombo.setSelectedItem(d);
                break;
            }
        }
        card.add(deptCombo);
        card.add(Box.createVerticalStrut(12));

        // Update Status
        JLabel lblStatus = new JLabel("UPDATE DOCKET STATUS");
        lblStatus.setFont(Theme.FONT_LABEL);
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(4));

        JComboBox<TicketStatus> statusCombo = new JComboBox<>(TicketStatus.values());
        statusCombo.setSelectedItem(complaint.getStatus());
        card.add(statusCombo);
        card.add(Box.createVerticalStrut(12));

        // Admin Note
        JLabel lblNote = new JLabel("ARCH-DEITY ADMINISTRATIVE NOTE / RESOLUTION REMARKS");
        lblNote.setFont(Theme.FONT_LABEL);
        card.add(lblNote);
        card.add(Box.createVerticalStrut(4));

        JTextArea adminNoteArea = Theme.createTextArea(3, 40);
        card.add(new JScrollPane(adminNoteArea));
        card.add(Box.createVerticalStrut(16));

        JButton btnApply = Theme.createPrimaryButton("Apply Official Administrative Order");
        btnApply.addActionListener(e -> {
            Department chosenDept = (Department) deptCombo.getSelectedItem();
            TicketStatus chosenStatus = (TicketStatus) statusCombo.getSelectedItem();
            String note = adminNoteArea.getText().trim();

            complaintService.updateTicketByAdmin(
                    complaint.getId(),
                    chosenStatus,
                    chosenDept != null ? chosenDept.getId() : null,
                    currentUser.getId(),
                    note
            );

            complaint.setStatus(chosenStatus);
            if (chosenDept != null) {
                complaint.setDepartmentId(chosenDept.getId());
                complaint.setDepartmentName(chosenDept.getName());
            }

            if (onUpdated != null) {
                onUpdated.run();
            }

            JOptionPane.showMessageDialog(TicketDetailDialog.this,
                    "Administrative order ratified and docket updated.",
                    "Order Enacted", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        card.add(btnApply);

        p.add(card);
        return p;
    }
}
