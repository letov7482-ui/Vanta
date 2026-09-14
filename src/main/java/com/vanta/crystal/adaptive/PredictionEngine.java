package com.vanta.crystal.adaptive;

import java.util.concurrent.ThreadLocalRandom;

public class PredictionEngine {
    private final ServerProfiler profiler;

    public PredictionEngine(ServerProfiler profiler) { this.profiler = profiler; }

    public int clickDelayMs() {
        double s = profiler.getStrictness();
        int base = 55 + (int) (s * 70);
        int jitter = ThreadLocalRandom.current().nextInt(-12, 13);
        return Math.max(40, base + jitter);
    }

    public double safeReach() {
        double s = profiler.getStrictness();
        return 3.00 + (1.0 - s) * 0.15;
    }

    public float rotationSpeed() {
        double s = profiler.getStrictness();
        return (float) (0.32 + (1.0 - s) * 0.55);
    }

    public int placeCooldownTicks() {
        double s = profiler.getStrictness();
        return 1 + (int) Math.round(s * 3);
    }
}
