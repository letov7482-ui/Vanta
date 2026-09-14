package com.vanta.crystal.module.impl;

import com.vanta.crystal.CrystalClient;
import com.vanta.crystal.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;

public class HitCrystalModule extends Module {
    public HitCrystalModule() {
        super("HitCrystal",
                "Атакует End Crystal под прицелом.\n" +
                "Задержка между ударами адаптируется под сервер.\n" +
                "ПКМ по модулю в GUI — переназначить клавишу.");
    }

    private long lastHit = 0L;

    @Override
    public void onTick(MinecraftClient mc) {
        if (!isActive(mc) || mc.player == null || mc.world == null || mc.interactionManager == null) return;

        Entity target = null;
        HitResult hit = mc.crosshairTarget;
        if (hit instanceof EntityHitResult ehr && ehr.getEntity() instanceof EndCrystalEntity) {
            target = ehr.getEntity();
        }

        if (target == null) {
            double reach = CrystalClient.INSTANCE.prediction.safeReach();
            Box box = mc.player.getBoundingBox().expand(reach);
            for (EndCrystalEntity e : mc.world.getEntitiesByClass(EndCrystalEntity.class, box, e -> true)) {
                if (isLookedAt(mc, e)) { target = e; break; }
            }
        }

        if (target == null) return;

        long now = System.currentTimeMillis();
        int delay = CrystalClient.INSTANCE.prediction.clickDelayMs();
        if (now - lastHit < delay) return;

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        lastHit = now;
    }

    private boolean isLookedAt(MinecraftClient mc, Entity e) {
        var eye = mc.player.getEyePos();
        var dir = mc.player.getRotationVec(1f);
        var to = e.getBoundingBox().getCenter().subtract(eye);
        double len = to.length();
        if (len < 0.01) return true;
        double dot = to.normalize().dotProduct(dir);
        return dot > 0.985;
    }
}
