package io.github.anthonyclemens.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Profiler {
    private Map<String, Long> timeMap = new LinkedHashMap<>();
    private long lastTick = 0;

    public void begin() {
        lastTick = System.nanoTime();
    }

    public void tick(String label) {
        long now = System.nanoTime();
        timeMap.put(label, now - lastTick);
        lastTick = now;
    }

    public Map<String, Float> getPercentages() {
        long total = timeMap.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Float> percentages = new LinkedHashMap<>();
        for (var entry : timeMap.entrySet()) {
            percentages.put(entry.getKey(), (entry.getValue() / (float) total) * 100f);
        }
        return percentages;
    }

    public void clear() {
        timeMap.clear();
    }
}
