package com.neowallet.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillListResponse {

    private List<BillResponse> bills;
    private PaginationDto pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        private int page;
        private int limit;
        private int totalCount;
        private int pageCount;
    }
}
