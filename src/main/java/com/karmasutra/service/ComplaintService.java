package com.karmasutra.service;

import com.karmasutra.dao.ComplaintDAO;
import com.karmasutra.dao.DepartmentDAO;
import com.karmasutra.dao.MilestoneDAO;
import com.karmasutra.dao.ResponseDAO;
import com.karmasutra.dao.StatusHistoryDAO;
import com.karmasutra.exception.InvalidTicketStateException;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.ComplaintStatusHistory;
import com.karmasutra.model.Department;
import com.karmasutra.model.WishMilestone;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.complaint.ComplaintFactory;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ai.ClassificationResult;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Core business service managing divine complaints, routing, and resolutions.
 */
public class ComplaintService {

    private static final Logger LOGGER = Logger.getLogger(ComplaintService.class.getName());
    private static final String TICKET_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ComplaintDAO complaintDAO;
    private final DepartmentDAO departmentDAO;
    private final ResponseDAO responseDAO;
    private final StatusHistoryDAO statusHistoryDAO;
    private final MilestoneDAO milestoneDAO;
    private final DivineResponseService divineResponseService;
    private final KarmaService karmaService;

    public ComplaintService() {
        this.complaintDAO = new ComplaintDAO();
        this.departmentDAO = new DepartmentDAO();
        this.responseDAO = new ResponseDAO();
        this.statusHistoryDAO = new StatusHistoryDAO();
        this.milestoneDAO = new MilestoneDAO();
        this.divineResponseService = new DivineResponseService();
        this.karmaService = new KarmaService();
    }

    public static String generateTicketNumber() {
        StringBuilder sb = new StringBuilder("KS-2026-");
        for (int i = 0; i < 5; i++) {
            sb.append(TICKET_CHARS.charAt(RANDOM.nextInt(TICKET_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Submits a new divine petition or grievance.
     * Validates, determines department, persists, and awards initial filing karma.
     */
    public Complaint submitComplaint(int userId, String subject, String description,
                                     ComplaintCategory category, ComplaintType type,
                                     Severity severity, String desiredResolution) {

        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }

        // 1. Intelligent AI Classification
        ClassificationResult classification = divineResponseService.classifySubmission(subject, description);

        // 2. Department Resolution
        Department dept = null;
        if (category != null) {
            dept = departmentDAO.findByCode(category.getDefaultDeptCode());
        }
        if (dept == null) {
            dept = departmentDAO.findByCode(classification.suggestedCategory().getDefaultDeptCode());
        }
        if (dept == null) {
            dept = departmentDAO.listAll().stream().findFirst().orElse(null);
        }

        // 3. Polymorphic Object Creation
        Complaint complaint = ComplaintFactory.createComplaint(type != null ? type : ComplaintType.COMPLAINT);
        complaint.setTicketNumber(generateTicketNumber());
        complaint.setUserId(userId);
        complaint.setSubject(subject.trim());
        complaint.setDescription(description.trim());
        complaint.setCategory(category != null ? category : classification.suggestedCategory());
        complaint.setSeverity(severity != null ? severity : classification.suggestedSeverity());
        complaint.setDepartmentId(dept != null ? dept.getId() : 1);
        complaint.setStatus(TicketStatus.RECEIVED_BY_HEAVEN);
        complaint.setDesiredResolution(desiredResolution != null ? desiredResolution.trim() : null);
        complaint.setLongTermWish(classification.isLongTermWish());

        // Polymorphic karma points calculation
        int karmaBonus = complaint.calculateKarmaWeight();
        complaint.setKarmaPointsAwarded(karmaBonus);

        // 4. Save Complaint to MySQL
        Complaint saved = complaintDAO.create(complaint);
        if (dept != null) {
            saved.setDepartmentName(dept.getName());
        }

        // 5. Create Initial Status History Record
        ComplaintStatusHistory initialHistory = new ComplaintStatusHistory();
        initialHistory.setComplaintId(saved.getId());
        initialHistory.setOldStatus("DRAFT");
        initialHistory.setNewStatus(TicketStatus.RECEIVED_BY_HEAVEN.getLabel());
        initialHistory.setRemarks("Petition docketed. Assigned to " + (dept != null ? dept.getName() : "Cosmic General Bureau"));
        statusHistoryDAO.create(initialHistory);

        // 6. Award Karma Points
        karmaService.awardKarma(userId, karmaBonus, "Grievance Docketing Reward (" + saved.getTicketNumber() + ")");

        // 7. If classified as Long-Term Wish, generate initial cosmic milestones
        if (saved.isLongTermWish()) {
            createInitialMilestones(saved.getId());
        }

        LOGGER.info("Grievance docketed successfully: " + saved.getTicketNumber());
        return saved;
    }

    private void createInitialMilestones(int complaintId) {
        LocalDate today = LocalDate.now();
        WishMilestone m1 = new WishMilestone(0, complaintId, "Cosmic Feasibility & Karmic Capital Audit", Date.valueOf(today.plusMonths(1)), "IN_PROGRESS", "Preliminary review by Lord Brahma's clerks", null);
        WishMilestone m2 = new WishMilestone(0, complaintId, "Mid-Term Earthly Execution & Resilience Checkpoint", Date.valueOf(today.plusYears(1)), "PENDING", "Requires demonstrable dedication from petitioner", null);
        WishMilestone m3 = new WishMilestone(0, complaintId, "Final Materialization & Astrological Alignment", Date.valueOf(today.plusYears(3)), "PENDING", "Subject to favorable celestial transit", null);

        milestoneDAO.create(m1);
        milestoneDAO.create(m2);
        milestoneDAO.create(m3);
    }

    public ComplaintResponse generateAIResponse(Complaint complaint, int iteration) {
        return divineResponseService.generateAndSaveResponse(complaint, iteration);
    }

    public List<Complaint> getComplaintsForUser(int userId, String category, String status, String search) {
        return complaintDAO.listByUserId(userId, category, status, search);
    }

    public List<Complaint> getAllComplaints(String category, String status, String search) {
        return complaintDAO.listAll(category, status, search);
    }

    public Complaint getComplaintById(int id) {
        return complaintDAO.findById(id);
    }

    public Complaint getComplaintByTicket(String ticketNumber) {
        return complaintDAO.findByTicketNumber(ticketNumber);
    }

    public List<ComplaintResponse> getResponsesForComplaint(int complaintId) {
        return responseDAO.listByComplaintId(complaintId);
    }

    public List<ComplaintStatusHistory> getStatusHistory(int complaintId) {
        return statusHistoryDAO.listByComplaintId(complaintId);
    }

    public List<WishMilestone> getMilestones(int complaintId) {
        return milestoneDAO.listByComplaintId(complaintId);
    }

    public Map<String, Integer> getStatusCountsForUser(int userId) {
        return complaintDAO.getCountsByStatus(userId);
    }

    public Map<String, Integer> getAllStatusCounts() {
        return complaintDAO.getAllCountsByStatus();
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.listAll();
    }

    /**
     * Admin action to update status, route department, or add administrative comment.
     */
    public void updateTicketByAdmin(int complaintId, TicketStatus newStatus, Integer newDeptId,
                                    int adminUserId, String adminNote) {
        Complaint c = complaintDAO.findById(complaintId);
        if (c == null) {
            throw new InvalidTicketStateException("Complaint ticket not found in records.");
        }

        TicketStatus oldStatus = c.getStatus();
        if (newStatus != null && newStatus != oldStatus) {
            complaintDAO.updateStatus(complaintId, newStatus);
            ComplaintStatusHistory h = new ComplaintStatusHistory();
            h.setComplaintId(complaintId);
            h.setOldStatus(oldStatus.getLabel());
            h.setNewStatus(newStatus.getLabel());
            h.setChangedByUserId(adminUserId);
            h.setRemarks(adminNote != null ? adminNote : "Status updated by Arch-Deity Administrator");
            statusHistoryDAO.create(h);
        }

        if (newDeptId != null && newDeptId > 0 && newDeptId != c.getDepartmentId()) {
            complaintDAO.updateDepartment(complaintId, newDeptId);
            Department newDept = departmentDAO.findById(newDeptId);
            ComplaintStatusHistory h = new ComplaintStatusHistory();
            h.setComplaintId(complaintId);
            h.setOldStatus(c.getStatus().getLabel());
            h.setNewStatus(c.getStatus().getLabel());
            h.setChangedByUserId(adminUserId);
            h.setRemarks("Rerouted to " + (newDept != null ? newDept.getName() : "Department #" + newDeptId));
            statusHistoryDAO.create(h);
        }

        // If an administrative response note is supplied, persist as administrative response
        if (adminNote != null && !adminNote.isBlank()) {
            ComplaintResponse adminRes = new ComplaintResponse();
            adminRes.setComplaintId(complaintId);
            adminRes.setResponderType("ARCH_DEITY_ADMIN");
            adminRes.setDivinePersona("Arch-Deity Administrator");
            adminRes.setResponseText(adminNote.trim());
            adminRes.setEmotionalTone("AUTHORITATIVE_DIVINE");
            adminRes.setIterationNumber(responseDAO.listByComplaintId(complaintId).size() + 1);
            responseDAO.create(adminRes);
        }
    }
}
