package io.github.anthonyclemens.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Profiler {
    private Map<String, Long> timeMap = new LinkedHashMap<>();
    private long lastTick = 0;

    public void begin() {
        lastTick = System.nanoTime();
    }

    private Map<String, long[]> history = new LinkedHashMap<>();
    private static final int SAMPLE_SIZE = 20;

    public void tick(String label) {
        long now = System.nanoTime();
        long delta = now - lastTick;
        lastTick = now;

        history.computeIfAbsent(label, k -> new long[SAMPLE_SIZE]);
        long[] samples = history.get(label);
        System.arraycopy(samples, 1, samples, 0, SAMPLE_SIZE - 1);
        samples[SAMPLE_SIZE - 1] = delta;
    }

    public Map<String, String> getAdaptiveTimes() {
        Map<String, String> result = new LinkedHashMap<>();
        for (var entry : history.entrySet()) {
            long[] samples = entry.getValue();
            long sum = 0;
            for (long v : samples) sum += v;
            long avgNs = sum / SAMPLE_SIZE;

            if (avgNs >= 1_000_000L) {
                result.put(entry.getKey(), Math.round(avgNs / 1_000_000.0) + " ms");
            } else if (avgNs >= 1_000L) {
                result.put(entry.getKey(), Math.round(avgNs / 1_000.0) + " µs");
            } else {
                result.put(entry.getKey(), avgNs + " ns");
            }
        }
        return result;
    }

    public void clear() {
        timeMap.clear();
    }
}
