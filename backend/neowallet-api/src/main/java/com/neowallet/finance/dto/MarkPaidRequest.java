package com.neowallet.finance.dto;

import java.time.LocalDate;

public record MarkPaidRequest(
    LocalDate paidDate,
    String paymentMethod,
    String notes
) {
}
