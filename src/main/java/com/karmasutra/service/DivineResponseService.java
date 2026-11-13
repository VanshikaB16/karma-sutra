package com.karmasutra.service;

import com.karmasutra.config.AppConfig;
import com.karmasutra.dao.ComplaintDAO;
import com.karmasutra.dao.DepartmentDAO;
import com.karmasutra.dao.ResponseDAO;
import com.karmasutra.dao.StatusHistoryDAO;
import com.karmasutra.exception.DivineServiceUnavailableException;
import com.karmasutra.model.ComplaintResponse;
import com.karmasutra.model.ComplaintStatusHistory;
import com.karmasutra.model.Department;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.enums.TicketStatus;
import com.karmasutra.service.ai.ClassificationResult;
import com.karmasutra.service.ai.CosmicResilientProvider;
import com.karmasutra.service.ai.DivineAIResult;
import com.karmasutra.service.ai.GeminiProvider;
import com.karmasutra.service.ai.GroqProvider;
import com.karmasutra.service.ai.LLMProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * High-level orchestration service for AI divine response generation and intelligent classification.
 */
public class DivineResponseService {

    private static final Logger LOGGER = Logger.getLogger(DivineResponseService.class.getName());

    private final GeminiProvider geminiProvider;
    private final GroqProvider groqProvider;
    private final CosmicResilientProvider offlineProvider;

    private final ComplaintDAO complaintDAO;
    private final DepartmentDAO departmentDAO;
    private final ResponseDAO responseDAO;
    private final StatusHistoryDAO statusHistoryDAO;

    public DivineResponseService() {
        this.geminiProvider = new GeminiProvider();
        this.groqProvider = new GroqProvider();
        this.offlineProvider = new CosmicResilientProvider();

        this.complaintDAO = new ComplaintDAO();
        this.departmentDAO = new DepartmentDAO();
        this.responseDAO = new ResponseDAO();
        this.statusHistoryDAO = new StatusHistoryDAO();
    }

    /**
     * Determines the active LLM provider based on configuration and key availability.
     */
    public LLMProvider getActiveProvider() {
        String configured = AppConfig.get("ai.provider", "GEMINI").toUpperCase();
        if ("GROQ".equals(configured) && groqProvider.isAvailable()) {
            return groqProvider;
        }
        if (geminiProvider.isAvailable()) {
            return geminiProvider;
        }
        if (groqProvider.isAvailable()) {
            return groqProvider;
        }
        // Fallback to local offline synthesis provider
        return offlineProvider;
    }

    /**
     * Intelligently classifies the complaint text using the active AI engine.
     */
    public ClassificationResult classifySubmission(String subject, String description) {
        return getActiveProvider().classifySubmission(subject, description);
    }

    /**
     * Generates a dynamic divine response, persists it to MySQL, updates ticket status,
     * and logs the status change history.
     */
    public ComplaintResponse generateAndSaveResponse(Complaint complaint, int iterationNumber) throws DivineServiceUnavailableException {
        Department dept = null;
        if (complaint.getDepartmentId() > 0) {
            dept = departmentDAO.findById(complaint.getDepartmentId());
        }

        LLMProvider provider = getActiveProvider();
        LOGGER.info("Invoking Divine AI engine via provider: " + provider.getProviderName());

        DivineAIResult aiResult;
        try {
            aiResult = provider.generateDivineResponse(complaint, dept, iterationNumber);
        } catch (DivineServiceUnavailableException e) {
            LOGGER.log(Level.WARNING, "Primary provider failed, attempting offline cosmic fallback", e);
            if (provider != offlineProvider) {
                // Graceful fallback to offline local cosmic generator so the petitioner always receives celestial counsel
                aiResult = offlineProvider.generateDivineResponse(complaint, dept, iterationNumber);
            } else {
                throw e;
            }
        }

        // Persist ComplaintResponse in MySQL
        ComplaintResponse response = new ComplaintResponse();
        response.setComplaintId(complaint.getId());
        response.setResponderType("AI_DIVINE_COUNCIL");
        response.setDivinePersona(aiResult.divinePersona());
        response.setResponseText(aiResult.responseText());
        response.setEmotionalTone(aiResult.emotionalTone());
        response.setIterationNumber(iterationNumber);

        responseDAO.create(response);

        // Update ticket status
        TicketStatus newStatus = TicketStatus.UNDER_DIVINE_REVIEW;
        if (iterationNumber > 1) {
            newStatus = TicketStatus.AWAITING_COSMIC_APPROVAL;
        }
        complaintDAO.updateStatus(complaint.getId(), newStatus);

        // Record Status History
        ComplaintStatusHistory history = new ComplaintStatusHistory();
        history.setComplaintId(complaint.getId());
        history.setOldStatus(complaint.getStatus().getLabel());
        history.setNewStatus(newStatus.getLabel());
        history.setRemarks("Divine counsel delivered via " + provider.getProviderName() + " (Perspective #" + iterationNumber + ")");
        statusHistoryDAO.create(history);

        return response;
    }
}
