package com.neowallet.ai.tool.impl;

import com.neowallet.ai.tool.NeowalletTool;
import com.neowallet.ai.tool.ToolContext;
import com.neowallet.finance.dto.TransactionFilter;
import com.neowallet.finance.dto.TransactionsResponse;
import com.neowallet.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Read-only tool that retrieves the user's transactions.
 */
@Component
@RequiredArgsConstructor
public class GetTransactionsTool implements NeowalletTool {

    private final TransactionService transactionService;

    @Override
    public String name() {
        return "getTransactions";
    }

    @Override
    public String description() {
        return "Retrieves the user's recent transactions.";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Map<String, Object> execute(ToolContext context, Map<String, String> parameters) {
        int page = Integer.parseInt(parameters.getOrDefault("page", "0"));
        int size = Integer.parseInt(parameters.getOrDefault("size", "10"));
        TransactionFilter filter = TransactionFilter.builder().build();
        TransactionsResponse transactions = transactionService.listTransactions(context.userId(), Optional.ofNullable(context.familyId()), filter, page, size, "date,desc");
        return Map.of(
                "tool", name(),
                "transactions", transactions.transactions(),
                "totalCount", transactions.pagination().total()
        );
    }
}
