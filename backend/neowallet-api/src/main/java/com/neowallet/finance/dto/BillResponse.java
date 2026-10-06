package com.neowallet.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {

    private String billId;
    private String userId;
    private String familyId;
    private String name;
    private BigDecimal amount;
    private String currency;
    private LocalDate dueDate;
    private String category;
    private Boolean isRecurring;
    private String recurringPeriod;
    private String vendor;
    private String notes;
    private String status;
    private LocalDate paidDate;
    private String paymentMethod;
    private String createdAt;
    private String updatedAt;
}
