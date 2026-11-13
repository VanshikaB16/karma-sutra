package com.karmasutra.view;

import com.karmasutra.exception.DivineServiceUnavailableException;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.service.DivineResponseService;
import com.karmasutra.service.ai.ClassificationResult;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Editorial submission modal for petitions, prayers, and grievances.
 * Employs SwingWorker for responsive background AI divine synthesis.
 */
public class SubmitGrievanceDialog extends JDialog {

    private final User user;
    private final ComplaintService complaintService;
    private final DivineResponseService divineResponseService;
    private final Consumer<Complaint> onComplaintCreated;

    // Form inputs
    private JTextField subjectField;
    private JComboBox<ComplaintCategory> categoryBox;
    private JComboBox<ComplaintType> typeBox;
    private JComboBox<Severity> severityBox;
    private JTextArea descriptionArea;
    private JTextField desiredResolutionField;

    // Dynamic AI Detection Preview
    private JLabel aiDetectionBadge;

    // Actions & Progress State
    private JButton btnSubmit;
    private JButton btnCancel;
    private JPanel progressPanel;
    private JLabel progressLabel;
    private JProgressBar progressBar;

    public SubmitGrievanceDialog(JFrame parent, User user, Consumer<Complaint> onComplaintCreated) {
        super(parent, "Submit Grievance to the Cosmos — Karma Sutra™", true);
        this.user = user;
        this.complaintService = new ComplaintService();
        this.divineResponseService = new DivineResponseService();
        this.onComplaintCreated = onComplaintCreated;

        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.COLOR_BG);
        setPreferredSize(new Dimension(640, 720));
        setResizable(false);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel title = new JLabel("Official Divine Grievance Registration");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("All submissions are indexed into Akashic records and routed to appropriate cosmic heads.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);
        add(headerPanel, BorderLayout.NORTH);

        // Form Body inside Card
        JPanel formCard = Theme.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 1. Subject
        formCard.add(createFieldLabel("SUBJECT / PETITION SUMMARY *"));
        formCard.add(Box.createVerticalStrut(4));
        subjectField = Theme.createTextField("Brief summary of your grievance or prayer");
        formCard.add(subjectField);
        formCard.add(Box.createVerticalStrut(12));

        // 2. Selectors Row: Category, Type, Severity
        JPanel selectorsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        selectorsRow.setOpaque(false);

        // Category
        JPanel catCol = new JPanel();
        catCol.setLayout(new BoxLayout(catCol, BoxLayout.Y_AXIS));
        catCol.setOpaque(false);
        catCol.add(createFieldLabel("CATEGORY"));
        catCol.add(Box.createVerticalStrut(4));
        categoryBox = new JComboBox<>(ComplaintCategory.values());
        categoryBox.setFont(Theme.FONT_BODY);
        catCol.add(categoryBox);

        // Type
        JPanel typeCol = new JPanel();
        typeCol.setLayout(new BoxLayout(typeCol, BoxLayout.Y_AXIS));
        typeCol.setOpaque(false);
        typeCol.add(createFieldLabel("PETITION TYPE"));
        typeCol.add(Box.createVerticalStrut(4));
        typeBox = new JComboBox<>(ComplaintType.values());
        typeBox.setFont(Theme.FONT_BODY);
        typeCol.add(typeBox);

        // Severity
        JPanel sevCol = new JPanel();
        sevCol.setLayout(new BoxLayout(sevCol, BoxLayout.Y_AXIS));
        sevCol.setOpaque(false);
        sevCol.add(createFieldLabel("URGENCY / SEVERITY"));
        sevCol.add(Box.createVerticalStrut(4));
        severityBox = new JComboBox<>(Severity.values());
        severityBox.setSelectedItem(Severity.MEDIUM);
        severityBox.setFont(Theme.FONT_BODY);
        sevCol.add(severityBox);

        selectorsRow.add(catCol);
        selectorsRow.add(typeCol);
        selectorsRow.add(sevCol);
        formCard.add(selectorsRow);
        formCard.add(Box.createVerticalStrut(14));

        // 3. Description
        formCard.add(createFieldLabel("DETAILED GRIEVANCE OR PRAYER DESCRIPTION *"));
        formCard.add(Box.createVerticalStrut(4));
        descriptionArea = Theme.createTextArea(6, 40);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER, 1, true));
        formCard.add(descScroll);
        formCard.add(Box.createVerticalStrut(10));

        // AI Wish / Severity Auto-detector preview indicator
        aiDetectionBadge = new JLabel("Cosmic Bureaucracy Engine: Ready to classify submission", JLabel.LEFT);
        aiDetectionBadge.setFont(Theme.FONT_SMALL);
        aiDetectionBadge.setForeground(Theme.COLOR_TEXT_MUTED);
        formCard.add(aiDetectionBadge);
        formCard.add(Box.createVerticalStrut(12));

        // 4. Optional Desired Resolution
        formCard.add(createFieldLabel("DESIRED RESOLUTION / COVETED OUTCOME (OPTIONAL)"));
        formCard.add(Box.createVerticalStrut(4));
        desiredResolutionField = Theme.createTextField("What would right this cosmic balance?");
        formCard.add(desiredResolutionField);

        // Real-time classification listener
        descriptionArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateAiClassificationPreview();
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        wrapper.add(formCard, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Bottom Actions & Progress
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(12, 24, 20, 24));

        // Progress panel (hidden by default)
        progressPanel = new JPanel(new BorderLayout(8, 4));
        progressPanel.setOpaque(false);
        progressLabel = new JLabel("Routing ticket through celestial departments...", JLabel.CENTER);
        progressLabel.setFont(Theme.FONT_BODY);
        progressLabel.setForeground(Theme.COLOR_MAROON);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.setVisible(false);

        bottomContainer.add(progressPanel);
        bottomContainer.add(Box.createVerticalStrut(10));

        // Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);

        btnCancel = Theme.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        btnSubmit = Theme.createPrimaryButton("Transmit to the Universe");
        btnSubmit.addActionListener(e -> submitGrievanceAsync());

        buttonRow.add(btnCancel);
        buttonRow.add(btnSubmit);
        bottomContainer.add(buttonRow);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.COLOR_TEXT_MUTED);
        return l;
    }

    private void updateAiClassificationPreview() {
        String text = descriptionArea.getText().trim();
        if (text.length() > 20) {
            ClassificationResult res = divineResponseService.classifySubmission(subjectField.getText(), text);
            if (res.isLongTermWish()) {
                aiDetectionBadge.setText("✨ Long-Term Wish Detected: Classified for multi-department extended cosmic processing");
                aiDetectionBadge.setForeground(Theme.COLOR_GOLD);
            } else if ("Serious Personal Concern".equals(res.primaryClassification())) {
                aiDetectionBadge.setText("🕊️ Sacred Escalation: Compassionate expedited protocol assigned");
                aiDetectionBadge.setForeground(Theme.COLOR_SAGE);
            } else {
                aiDetectionBadge.setText("✓ Routed to: " + res.suggestedCategory().getLabel() + " (" + res.suggestedSeverity().getLabel() + " Urgency)");
                aiDetectionBadge.setForeground(Theme.COLOR_TEXT_MUTED);
            }
        }
    }

    private void submitGrievanceAsync() {
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject for your petition.", "Required Field", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide the details of your grievance or prayer.", "Required Field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable buttons and show loading bar
        btnSubmit.setEnabled(false);
        btnCancel.setEnabled(false);
        progressPanel.setVisible(true);
        progressLabel.setText("DOCKETING IN AKASHIC DATABASE & GENERATING DIVINE RESPONSE...");

        ComplaintCategory category = (ComplaintCategory) categoryBox.getSelectedItem();
        ComplaintType type = (ComplaintType) typeBox.getSelectedItem();
        Severity severity = (Severity) severityBox.getSelectedItem();
        String desiredRes = desiredResolutionField.getText().trim();

        // SwingWorker for smooth asynchronous background processing
        SwingWorker<SubmissionResult, Void> worker = new SwingWorker<>() {
            @Override
            protected SubmissionResult doInBackground() throws Exception {
                // Step 1: Save complaint to MySQL
                Complaint c = complaintService.submitComplaint(
                        user.getId(), subject, description, category, type, severity, desiredRes
                );

                // Step 2: Attempt Divine Response Generation
                ComplaintResponse response = null;
                String errorMsg = null;
                try {
                    response = complaintService.generateAIResponse(c, 1);
                } catch (DivineServiceUnavailableException ex) {
                    errorMsg = ex.getMessage();
                }

                return new SubmissionResult(c, response, errorMsg);
            }

            @Override
            protected void done() {
                try {
                    SubmissionResult result = get();
                    progressPanel.setVisible(false);
                    btnSubmit.setEnabled(true);
                    btnCancel.setEnabled(true);

                    // Notify caller to refresh dashboard / lists
                    if (onComplaintCreated != null) {
                        onComplaintCreated.accept(result.complaint);
                    }

                    dispose();

                    // Display confirmation & response dialog
                    showConfirmationModal(result);

                } catch (Exception ex) {
                    progressPanel.setVisible(false);
                    btnSubmit.setEnabled(true);
                    btnCancel.setEnabled(true);
                    JOptionPane.showMessageDialog(SubmitGrievanceDialog.this,
                            "Cosmic Transmission Fault: " + ex.getMessage(),
                            "Submission Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void showConfirmationModal(SubmissionResult result) {
        Complaint c = result.complaint;
        ComplaintResponse r = result.response;

        JDialog confDialog = new JDialog((JFrame) getOwner(), "Cosmic Docket Confirmation — " + c.getTicketNumber(), true);
        confDialog.setLayout(new BorderLayout());
        confDialog.getContentPane().setBackground(Theme.COLOR_BG);
        confDialog.setPreferredSize(new Dimension(580, 560));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Petition Successfully Stamped in Heaven");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_GOLD);

        JLabel ticketLbl = new JLabel("Ticket ID: " + c.getTicketNumber() + " | Category: " + c.getCategory().getLabel());
        ticketLbl.setFont(Theme.FONT_MONO);
        ticketLbl.setForeground(Theme.COLOR_TEXT_PRIMARY);

        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(ticketLbl);
        content.add(Box.createVerticalStrut(16));

        // Response parchment card
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        JTextArea resArea = new JTextArea();
        resArea.setEditable(false);
        resArea.setLineWrap(true);
        resArea.setWrapStyleWord(true);
        resArea.setFont(Theme.FONT_BODY);
        resArea.setForeground(Theme.COLOR_TEXT_PRIMARY);

        if (r != null) {
            resArea.setText(r.getResponseText());
        } else {
            resArea.setText("Your complaint has been safely recorded in the divine ledger under Ticket #" + c.getTicketNumber() +
                    ".\n\nNotice from Cosmic Dispatch:\n" + result.errorMessage +
                    "\n\nYou may view this ticket in your docket and request divine generation once bandwidth stabilizes.");
        }
        resArea.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(resArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        content.add(card);
        content.add(Box.createVerticalStrut(16));

        // Bottom close
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnClose = Theme.createPrimaryButton("Return to Docket");
        btnClose.addActionListener(e -> confDialog.dispose());
        btnPanel.add(btnClose);
        content.add(btnPanel);

        confDialog.add(content);
        confDialog.pack();
        confDialog.setLocationRelativeTo(getOwner());
        confDialog.setVisible(true);
    }

    private record SubmissionResult(Complaint complaint, ComplaintResponse response, String errorMessage) {}
}
