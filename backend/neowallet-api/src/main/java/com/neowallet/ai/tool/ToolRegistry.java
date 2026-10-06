package com.neowallet.ai.tool;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry of approved Neo AI tools.
 * Only tools registered here can be executed.
 */
@Component
public class ToolRegistry {

    private final Map<String, NeowalletTool> tools = new ConcurrentHashMap<>();
    private final List<NeowalletTool> toolList;

    public ToolRegistry(List<NeowalletTool> toolList) {
        this.toolList = toolList == null ? List.of() : toolList;
    }

    @PostConstruct
    public void registerAll() {
        for (NeowalletTool tool : toolList) {
            tools.put(tool.name(), tool);
        }
    }

    public Optional<NeowalletTool> get(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public List<NeowalletTool> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(tools.values()));
    }

    public boolean isAllowed(String name) {
        return tools.containsKey(name);
    }
}
