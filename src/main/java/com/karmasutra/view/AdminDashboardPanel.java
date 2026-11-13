package com.karmasutra.view;

import com.karmasutra.dao.UserDAO;
import com.karmasutra.model.Department;
import com.karmasutra.model.SimulatedPayment;
import com.karmasutra.model.User;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ComplaintService;
import com.karmasutra.service.SubscriptionService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Editorial Arch-Deity administrative control console.
 * Strictly restricted to users with role ARCH_DEITY_ADMIN.
 */
public class AdminDashboardPanel extends JPanel {

    private final User adminUser;
    private final ComplaintService complaintService;
    private final SubscriptionService subscriptionService;
    private final UserDAO userDAO;
    private final Consumer<Complaint> onOpenDetails;

    // Stats
    private JLabel totalGlobalTicketsVal;
    private JLabel totalDevoteesVal;
    private JLabel pendingReviewVal;
    private JLabel resolvedGlobalVal;

    // All Tickets Table
    private JTable allTicketsTable;
    private DefaultTableModel allTicketsModel;
    private List<Complaint> allComplaintsList;
    private JTextField searchField;
    private JComboBox<String> statusFilterBox;

    // Devotees Table
    private DefaultTableModel devoteesModel;

    // Payments Table
    private DefaultTableModel paymentsModel;

    public AdminDashboardPanel(User adminUser, Consumer<Complaint> onOpenDetails) {
        this.adminUser = adminUser;
        this.complaintService = new ComplaintService();
        this.subscriptionService = new SubscriptionService();
        this.userDAO = new UserDAO();
        this.onOpenDetails = onOpenDetails;

        initializeUI();
        refreshAllAdminData();
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

        JLabel title = new JLabel("Arch-Deity Administrative Console");
        title.setFont(Theme.FONT_HEADER_1);
        title.setForeground(Theme.COLOR_TEXT_PRIMARY);

        JLabel sub = new JLabel("High-level surveillance of global dockets, celestial routing, and devotee subscriptions.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.COLOR_TEXT_MUTED);

        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(4));
        titleCol.add(sub);
        headerPanel.add(titleCol, BorderLayout.WEST);

        JButton btnRefresh = Theme.createSecondaryButton("Refresh Cosmic Records");
        btnRefresh.addActionListener(e -> refreshAllAdminData());
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center Area: Global Metrics Cards + Tabbed Panes
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Metrics Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        metricsGrid.setPreferredSize(new Dimension(800, 80));

        totalGlobalTicketsVal = new JLabel("0");
        totalDevoteesVal = new JLabel("0");
        pendingReviewVal = new JLabel("0");
        resolvedGlobalVal = new JLabel("0");

        metricsGrid.add(createAdminStatCard("Total Petitions Filed", totalGlobalTicketsVal, Theme.COLOR_TEXT_PRIMARY));
        metricsGrid.add(createAdminStatCard("Registered Devotees", totalDevoteesVal, Theme.COLOR_MAROON));
        metricsGrid.add(createAdminStatCard("Pending Adjudication", pendingReviewVal, Theme.COLOR_GOLD));
        metricsGrid.add(createAdminStatCard("Cosmically Resolved", resolvedGlobalVal, Theme.COLOR_SAGE));

        centerPanel.add(metricsGrid);
        centerPanel.add(Box.createVerticalStrut(18));

        // Administrative Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY_BOLD);

        tabs.addTab("Global Complaint Docket", createGlobalDocketTab());
        tabs.addTab("Devotee Registry", createDevoteeRegistryTab());
        tabs.addTab("Celestial Payment Ledger", createPaymentLedgerTab());
        tabs.addTab("Department Management", createDepartmentOverviewTab());

        centerPanel.add(tabs);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createAdminStatCard(String title, JLabel valLabel, java.awt.Color col) {
        JPanel c = Theme.createCardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(Theme.FONT_LABEL);
        t.setForeground(Theme.COLOR_TEXT_MUTED);
        valLabel.setFont(Theme.FONT_HEADER_1);
        valLabel.setForeground(col);
        c.add(t);
        c.add(Box.createVerticalStrut(4));
        c.add(valLabel);
        return c;
    }

    private JPanel createGlobalDocketTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));

        // Toolbar
        JPanel bar = Theme.createCardPanel();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));

        bar.add(new JLabel("Search:"));
        searchField = Theme.createTextField("Ticket ID or keyword...");
        searchField.setPreferredSize(new Dimension(160, 30));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshGlobalTickets();
            }
        });
        bar.add(searchField);

        bar.add(new JLabel("Status:"));
        statusFilterBox = new JComboBox<>();
        statusFilterBox.addItem("All Statuses");
        for (TicketStatus s : TicketStatus.values()) {
            statusFilterBox.addItem(s.getLabel());
        }
        statusFilterBox.addActionListener(e -> refreshGlobalTickets());
        bar.add(statusFilterBox);

        p.add(bar, BorderLayout.NORTH);

        // Table in Card
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        String[] cols = {"Ticket ID", "Petitioner", "Subject", "Category", "Status", "Department", "Filed On"};
        allTicketsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        allTicketsTable = new JTable(allTicketsModel);
        allTicketsTable.setRowHeight(36);
        allTicketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allTicketsTable.setFont(Theme.FONT_BODY);

        allTicketsTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        allTicketsTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
                setFont(Theme.FONT_MONO);
                setForeground(Theme.COLOR_CYAN);
                return c;
            }
        });

        allTicketsTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        allTicketsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                if (val != null) {
                    return Theme.createStatusBadge(TicketStatus.fromLabel(val.toString()));
                }
                return super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
            }
        });

        allTicketsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && allTicketsTable.getSelectedRow() >= 0) {
                    openSelectedTicketDetails();
                }
            }
        });

        card.add(new JScrollPane(allTicketsTable), BorderLayout.CENTER);

        JPanel bottomAct = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomAct.setOpaque(false);
        JButton btnAdjudicate = Theme.createPrimaryButton("Adjudicate Selected Ticket");
        btnAdjudicate.addActionListener(e -> openSelectedTicketDetails());
        bottomAct.add(btnAdjudicate);
        card.add(bottomAct, BorderLayout.SOUTH);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void openSelectedTicketDetails() {
        int row = allTicketsTable.getSelectedRow();
        if (row >= 0 && allComplaintsList != null && row < allComplaintsList.size()) {
            Complaint c = allComplaintsList.get(row);
            if (onOpenDetails != null) {
                onOpenDetails.accept(c);
            }
        }
    }

    private JPanel createDevoteeRegistryTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        String[] cols = {"Devotee ID", "Full Name", "Username", "Email", "Role", "Subscription Tier", "Karma Points"};
        devoteesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(devoteesModel);
        table.setRowHeight(34);
        table.setFont(Theme.FONT_BODY);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel createPaymentLedgerTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        String[] cols = {"Payment Ref", "Devotee ID", "Amount (INR)", "Payment Channel", "Status", "Timestamp"};
        paymentsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(paymentsModel);
        table.setRowHeight(34);
        table.setFont(Theme.FONT_BODY);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel createDepartmentOverviewTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout());

        String[] cols = {"Code", "Department Name", "Divine Presiding Head", "Escalation Protocol"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<Department> depts = complaintService.getAllDepartments();
        for (Department d : depts) {
            model.addRow(new Object[]{
                    d.getCode(),
                    d.getName(),
                    d.getDivineHead(),
                    d.getEscalationProtocol()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(Theme.FONT_BODY);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    public void refreshAllAdminData() {
        // 1. Stats
        Map<String, Integer> counts = complaintService.getAllStatusCounts();
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

        List<User> devotees = userDAO.listAllDevotees();

        totalGlobalTicketsVal.setText(String.valueOf(total));
        totalDevoteesVal.setText(String.valueOf(devotees.size()));
        pendingReviewVal.setText(String.valueOf(pending));
        resolvedGlobalVal.setText(String.valueOf(resolved));

        // 2. Global tickets table
        refreshGlobalTickets();

        // 3. Devotee Registry
        devoteesModel.setRowCount(0);
        for (User u : devotees) {
            devoteesModel.addRow(new Object[]{
                    "DEV-" + u.getId(),
                    u.getFullName(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole().name(),
                    u.getTier().getDisplayName(),
                    u.getKarmaPoints()
            });
        }

        // 4. Payments
        List<SimulatedPayment> allPayments = subscriptionService.getAllPayments();
        paymentsModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        for (SimulatedPayment sp : allPayments) {
            String timeStr = sp.getCreatedAt() != null ? sp.getCreatedAt().toLocalDateTime().format(dtf) : "Cosmic Era";
            paymentsModel.addRow(new Object[]{
                    sp.getTransactionReference(),
                    "DEV-" + sp.getUserId(),
                    "₹" + sp.getAmount(),
                    sp.getPaymentMethod(),
                    sp.getPaymentStatus(),
                    timeStr
            });
        }
    }

    private void refreshGlobalTickets() {
        String search = searchField != null ? searchField.getText().trim() : null;
        String status = statusFilterBox != null ? (String) statusFilterBox.getSelectedItem() : null;

        allComplaintsList = complaintService.getAllComplaints(null, status, search);
        allTicketsModel.setRowCount(0);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
        for (Complaint c : allComplaintsList) {
            String dateStr = c.getCreatedAt() != null ? c.getCreatedAt().toLocalDateTime().format(dtf) : "Cosmic Era";
            allTicketsModel.addRow(new Object[]{
                    c.getTicketNumber(),
                    c.getSubmitterName() != null ? c.getSubmitterName() : "Devotee #" + c.getUserId(),
                    c.getSubject(),
                    c.getCategory().getLabel(),
                    c.getStatus().getLabel(),
                    c.getDepartmentName() != null ? c.getDepartmentName() : "Cosmic General Bureau",
                    dateStr
            });
        }
    }
}
