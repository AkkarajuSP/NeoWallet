package com.neowallet.ai.tool.impl;

import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.finance.dto.BillListResponse;
import com.neowallet.finance.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only tool that retrieves the user's bills.
 */
@Component
@RequiredArgsConstructor
public class GetBillsTool implements NeowalletTool {

    private final BillService billService;

    @Override
    public String name() {
        return "getBills";
    }

    @Override
    public String description() {
        return "Retrieves the user's upcoming and past bills.";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Map<String, Object> execute(ToolContext context, Map<String, String> parameters) {
        int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
        int size = Integer.parseInt(parameters.getOrDefault("size", "10"));
        BillListResponse bills = billService.listBills(context.userId(), Optional.ofNullable(context.familyId()),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), page, size, "dueDate,asc");
        return Map.of(
                "tool", name(),
                "bills", bills.getBills(),
                "totalCount", bills.getPagination().getTotalCount()
        );
    }
}
