package com.karmasutra.view;

import com.karmasutra.dao.ResponseDAO;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.service.ComplaintService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Divine Responses & Heavenly Decrees Archive.
 * Searchable depository of all deity roasts, satirical verdicts, and official rulings.
 */
public class DivineResponsesPanel extends JPanel {

    private final User user;
    private final ComplaintService complaintService;
    private final ResponseDAO responseDAO;

    private JTextField searchField;
    private JComboBox<String> deityFilterBox;
    private JPanel responseListContainer;

    // Selected Decree View
    private JLabel decreeTicketBadge;
    private JLabel decreePersonaBadge;
    private JLabel decreeSubjectLabel;
    private JTextArea decreeOriginalText;
    private JTextArea decreeParchmentText;

    private List<DocketWithResponse> allDocketsWithResponses;

    private record DocketWithResponse(Complaint complaint, ComplaintResponse response) {}

    public DivineResponsesPanel(User user) {
        this.user = user;
        this.complaintService = new ComplaintService();
        this.responseDAO = new ResponseDAO();
        this.allDocketsWithResponses = new ArrayList<>();

        initializeUI();
        refreshResponses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel title = new JLabel("Divine Responses & Heavenly Decrees Archive");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("Official depository of all AI-synthesized celestial decrees, roasts, and verdicts across cosmic departments.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(3));
        titleCol.add(sub);
        headerPanel.add(titleCol, BorderLayout.WEST);

        // Filter toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);

        searchField = Theme.createTextField("Search decree text or keyword...");
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilter();
            }
        });

        deityFilterBox = new JComboBox<>(new String[]{
                "All Divine Authorities",
                "Lord Shani (Career & SCRUM)",
                "Chitragupta (Academics & Bell Curve)",
                "Kamadeva (Love & Heartbreak)",
                "Dharmaraja Yama (Karma & Retribution)",
                "Council of Matriarchs (Family Drama)",
                "Lord Brahma (Destiny & Decisions)"
        });
        deityFilterBox.setFont(Theme.FONT_BODY);
        deityFilterBox.addActionListener(e -> applyFilter());

        toolbar.add(new JLabel("Authority:"));
        toolbar.add(deityFilterBox);
        toolbar.add(searchField);
        headerPanel.add(toolbar, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Split View: Left List + Right Illuminated Decree View
        JPanel splitPanel = new JPanel(new BorderLayout(14, 0));
        splitPanel.setOpaque(false);

        // Left List Container
        responseListContainer = new JPanel();
        responseListContainer.setLayout(new BoxLayout(responseListContainer, BoxLayout.Y_AXIS));
        responseListContainer.setOpaque(false);

        JScrollPane leftScroll = new JScrollPane(responseListContainer);
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        leftScroll.getViewport().setBackground(Theme.COLOR_BG);
        leftScroll.setPreferredSize(new Dimension(320, 500));
        splitPanel.add(leftScroll, BorderLayout.WEST);

        // Right Illuminated Decree Card
        JPanel rightCard = Theme.createCardPanel();
        rightCard.setLayout(new BorderLayout(0, 12));

        // Decree Header
        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setOpaque(false);

        JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBadges.setOpaque(false);
        decreeTicketBadge = Theme.createBadge("TICKET #", Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD);
        decreePersonaBadge = Theme.createBadge("PRESIDING DEITY", Theme.COLOR_BURGUNDY_LIGHT, Theme.COLOR_BURGUNDY);
        leftBadges.add(decreeTicketBadge);
        leftBadges.add(decreePersonaBadge);

        rightHeader.add(leftBadges, BorderLayout.WEST);

        JPanel decreeSubjectCol = new JPanel();
        decreeSubjectCol.setLayout(new BoxLayout(decreeSubjectCol, BoxLayout.Y_AXIS));
        decreeSubjectCol.setOpaque(false);
        decreeSubjectLabel = new JLabel("Select a decree from the left to read");
        decreeSubjectLabel.setFont(Theme.FONT_HEADER_2);
        decreeSubjectLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);
        decreeSubjectCol.add(Box.createVerticalStrut(6));
        decreeSubjectCol.add(decreeSubjectLabel);
        rightHeader.add(decreeSubjectCol, BorderLayout.SOUTH);

        rightCard.add(rightHeader, BorderLayout.NORTH);

        // Center: Original Submitter Text + Decree Parchment
        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);

        JLabel originalTitle = new JLabel("DEVOTEE SUBMISSION DOSSIER:");
        originalTitle.setFont(Theme.FONT_LABEL);
        originalTitle.setForeground(Theme.COLOR_TEXT_MUTED);
        centerBody.add(originalTitle);
        centerBody.add(Box.createVerticalStrut(4));

        decreeOriginalText = new JTextArea("Petition details...");
        decreeOriginalText.setFont(Theme.FONT_SMALL);
        decreeOriginalText.setForeground(Theme.COLOR_TEXT_MUTED);
        decreeOriginalText.setBackground(Theme.COLOR_INPUT_BG);
        decreeOriginalText.setEditable(false);
        decreeOriginalText.setLineWrap(true);
        decreeOriginalText.setWrapStyleWord(true);
        decreeOriginalText.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        decreeOriginalText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        decreeOriginalText.setPreferredSize(new Dimension(500, 45));
        centerBody.add(decreeOriginalText);
        centerBody.add(Box.createVerticalStrut(10));

        JLabel decreeLabel = new JLabel("✦ HEAVENLY DECREE & RESOLUTION ORDER:");
        decreeLabel.setFont(Theme.FONT_LABEL);
        decreeLabel.setForeground(Theme.COLOR_GOLD);
        centerBody.add(decreeLabel);
        centerBody.add(Box.createVerticalStrut(4));

        decreeParchmentText = new JTextArea();
        decreeParchmentText.setFont(Theme.FONT_SERIF_BODY);
        decreeParchmentText.setForeground(Theme.COLOR_TEXT_PRIMARY);
        decreeParchmentText.setBackground(new Color(0x10, 0x12, 0x1A));
        decreeParchmentText.setEditable(false);
        decreeParchmentText.setLineWrap(true);
        decreeParchmentText.setWrapStyleWord(true);
        decreeParchmentText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_BORDER_ACCENT, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JScrollPane decreeScroll = new JScrollPane(decreeParchmentText);
        decreeScroll.setBorder(BorderFactory.createEmptyBorder());
        centerBody.add(decreeScroll);

        rightCard.add(centerBody, BorderLayout.CENTER);
        splitPanel.add(rightCard, BorderLayout.CENTER);

        add(splitPanel, BorderLayout.CENTER);
    }

    public void refreshResponses() {
        if (user == null) return;

        List<Complaint> complaints = complaintService.getComplaintsForUser(user.getId(), null, null, null);
        allDocketsWithResponses.clear();

        for (Complaint c : complaints) {
            ComplaintResponse resp = responseDAO.getLatestByComplaintId(c.getId());
            if (resp != null) {
                allDocketsWithResponses.add(new DocketWithResponse(c, resp));
            }
        }

        applyFilter();
    }

    private void applyFilter() {
        String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String deity = deityFilterBox != null ? (String) deityFilterBox.getSelectedItem() : "All";

        responseListContainer.removeAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");

        DocketWithResponse firstMatch = null;

        for (DocketWithResponse d : allDocketsWithResponses) {
            String deityName = d.response.getDivinePersona() != null ? d.response.getDivinePersona() : "";
            if (deity != null && !deity.startsWith("All")) {
                String filterKeyword = deity.split(" ")[0].toLowerCase();
                if (!deityName.toLowerCase().contains(filterKeyword)) {
                    continue;
                }
            }

            if (!search.isEmpty()) {
                String text = (d.complaint.getSubject() + " " + d.response.getResponseText()).toLowerCase();
                if (!text.contains(search)) {
                    continue;
                }
            }

            if (firstMatch == null) {
                firstMatch = d;
            }

            JPanel itemCard = createResponseListItem(d, dtf);
            responseListContainer.add(itemCard);
            responseListContainer.add(Box.createVerticalStrut(8));
        }

        if (firstMatch != null) {
            displayDecree(firstMatch);
        } else {
            decreeTicketBadge.setText("NO DECREES");
            decreePersonaBadge.setText("Awaiting Filings");
            decreeSubjectLabel.setText("No matching celestial decrees in archive");
            decreeOriginalText.setText("Submit a complaint or wish to generate dynamic divine responses.");
            decreeParchmentText.setText("When heaven responds to your grievances, the official orders will be permanently inscribed here.");
        }

        responseListContainer.revalidate();
        responseListContainer.repaint();
    }

    private JPanel createResponseListItem(DocketWithResponse d, DateTimeFormatter dtf) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setPreferredSize(new Dimension(300, 85));

        JPanel topBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        topBadges.setOpaque(false);
        topBadges.add(Theme.createBadge(d.complaint.getTicketNumber(), Theme.COLOR_GOLD_LIGHT, Theme.COLOR_GOLD));

        String persona = d.response.getDivinePersona() != null ? d.response.getDivinePersona() : "Chitragupta";
        JLabel personaLabel = new JLabel(persona.split(",")[0]);
        personaLabel.setFont(Theme.FONT_SMALL);
        personaLabel.setForeground(Theme.COLOR_BURGUNDY);
        topBadges.add(personaLabel);

        JLabel subLabel = new JLabel(d.complaint.getSubject());
        subLabel.setFont(Theme.FONT_BODY_BOLD);
        subLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);

        String dateStr = d.response.getCreatedAt() != null ? d.response.getCreatedAt().toLocalDateTime().format(dtf) : "Recent";
        JLabel dateLabel = new JLabel("Stamped: " + dateStr);
        dateLabel.setFont(Theme.FONT_SMALL);
        dateLabel.setForeground(Theme.COLOR_TEXT_MUTED);

        card.add(topBadges);
        card.add(Box.createVerticalStrut(3));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(dateLabel);

        card.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                displayDecree(d);
            }
        });

        return card;
    }

    private void displayDecree(DocketWithResponse d) {
        decreeTicketBadge.setText(d.complaint.getTicketNumber());
        decreePersonaBadge.setText(d.response.getDivinePersona() != null ? d.response.getDivinePersona() : "Divine Council");
        decreeSubjectLabel.setText(d.complaint.getSubject());
        decreeOriginalText.setText(d.complaint.getDescription());
        decreeParchmentText.setText(d.response.getResponseText());
        decreeParchmentText.setCaretPosition(0);
    }
}
