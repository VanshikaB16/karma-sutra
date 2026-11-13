package com.karmasutra.service.ai;

import com.karmasutra.exception.DivineServiceUnavailableException;
import com.karmasutra.model.Department;
import com.karmasutra.model.complaint.Complaint;

/**
 * Modular interface for LLM response generation and intelligent classification.
 */
public interface LLMProvider {

    String getProviderName();

    boolean isAvailable();

    DivineAIResult generateDivineResponse(Complaint complaint, Department department, int iterationNumber)
            throws DivineServiceUnavailableException;

    ClassificationResult classifySubmission(String subject, String description);
}
