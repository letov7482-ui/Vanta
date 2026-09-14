package com.vanta.crystal.adaptive;

import net.minecraft.client.MinecraftClient;

public class ServerProfiler {
    private double latencyEwma = 50.0;
    private double strictnessEwma = 0.30;
    private int rejections = 0;
    private long windowStart = System.currentTimeMillis();

    public void tick(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null || mc.player == null) return;

        var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        int ping = entry != null ? entry.getLatency() : 0;
        latencyEwma = 0.9 * latencyEwma + 0.1 * ping;

        long now = System.currentTimeMillis();
        if (now - windowStart > 60_000) {
            rejections = 0;
            windowStart = now;
        }

        double latencyFactor = Math.min(1.0, latencyEwma / 220.0);
        double rejectFactor = Math.min(1.0, rejections / 4.0);
        double target = Math.min(1.0, latencyFactor * 0.45 + rejectFactor * 0.55);
        strictnessEwma = 0.96 * strictnessEwma + 0.04 * target;
    }

    public void onRejection() { rejections++; }

    public double getLatency() { return latencyEwma; }
    public double getStrictness() { return strictnessEwma; }
}
