package com.neowallet.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBillRequest(
    String name,
    BigDecimal amount,
    String currency,
    LocalDate dueDate,
    String category,
    Boolean isRecurring,
    String recurringPeriod,
    String vendor,
    String notes
) {
}
