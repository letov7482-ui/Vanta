package com.vanta.crystal.adaptive;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

public class RotationController {
    private float targetYaw, targetPitch;
    private boolean active;
    private float speed = 0.5f;

    public void aimAt(Vec3d from, Vec3d to, float speed) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        this.targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        this.targetPitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)));
        this.speed = speed;
        this.active = true;
    }

    public void tick(ClientPlayerEntity player) {
        if (!active) return;
        float cYaw = player.getYaw();
        float cPitch = player.getPitch();
        float dYaw = wrap(targetYaw - cYaw);
        float dPitch = targetPitch - cPitch;

        float jY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.35f;
        float jP = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.25f;

        player.setYaw(cYaw + dYaw * speed + jY);
        player.setPitch(clamp(cPitch + dPitch * speed + jP, -90f, 90f));

        if (Math.abs(dYaw) < 0.6f && Math.abs(dPitch) < 0.6f) {
            player.setYaw(targetYaw);
            player.setPitch(clamp(targetPitch, -90f, 90f));
            active = false;
        }
    }

    private static float wrap(float d) {
        while (d > 180f) d -= 360f;
        while (d < -180f) d += 360f;
        return d;
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
