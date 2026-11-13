package com.karmasutra.service.ai;

import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.Severity;

public record ClassificationResult(
    String primaryClassification, // "Immediate Wish", "Complaint", "Academic Emergency", "Long-Term Aspiration", "Personal Dilemma", "Serious Personal Concern"
    boolean isLongTermWish,
    ComplaintCategory suggestedCategory,
    Severity suggestedSeverity,
    String emotionalTone,
    int detectedHorizonYears
) {}
