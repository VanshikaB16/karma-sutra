package com.karmasutra.service.ai;

public record DivineAIResult(
    String divinePersona,
    String responseText,
    String emotionalTone,
    boolean isLongTermAspiration,
    String recommendedDeptCode
) {}
