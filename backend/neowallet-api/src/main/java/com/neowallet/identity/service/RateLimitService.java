package com.neowallet.identity.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, AttemptWindow> windows = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int maxAttempts, long windowSeconds) {
        Instant now = Instant.now();
        AttemptWindow window = windows.computeIfAbsent(key, k -> new AttemptWindow(now, 0));

        if (window.start.plusSeconds(windowSeconds).isBefore(now)) {
            windows.put(key, new AttemptWindow(now, 1));
            return true;
        }

        if (window.count >= maxAttempts) {
            return false;
        }

        window.count++;
        windows.put(key, window);
        return true;
    }

    public int remainingAttempts(String key, int maxAttempts, long windowSeconds) {
        Instant now = Instant.now();
        AttemptWindow window = windows.getOrDefault(key, new AttemptWindow(now, 0));
        if (window.start.plusSeconds(windowSeconds).isBefore(now)) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - window.count);
    }

    public void reset(String key) {
        windows.remove(key);
    }

    private static class AttemptWindow {
        Instant start;
        int count;

        AttemptWindow(Instant start, int count) {
            this.start = start;
            this.count = count;
        }
    }

}
