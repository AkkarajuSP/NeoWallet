package com.neowallet.ai.dto;

/**
 * Categorizes a Neo AI response so the consumer can distinguish facts from interpretations.
 */
public enum AIResponseType {
    FACT,
    ANALYSIS,
    RECOMMENDATION,
    WARNING,
    INSUFFICIENT_DATA,
    REFUSAL
}
