package com.neowallet.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSavingsContributionRequest(
    BigDecimal amount,
    LocalDate contributionDate,
    String notes
) {}
