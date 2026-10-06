package com.neowallet.observability;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ApiError {

    private String code;
    private String message;
    private UUID requestId;
    private Instant timestamp;
    private List<ApiErrorDetail> details;

    @Data
    @Builder
    public static class ApiErrorDetail {
        private String field;
        private String message;
    }

}
