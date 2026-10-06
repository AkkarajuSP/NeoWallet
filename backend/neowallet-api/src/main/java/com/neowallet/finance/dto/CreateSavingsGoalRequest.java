package com.neowallet.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSavingsGoalRequest(
    String name,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    LocalDate targetDate,
    String currency,
    String priority,
    String category
) {}
