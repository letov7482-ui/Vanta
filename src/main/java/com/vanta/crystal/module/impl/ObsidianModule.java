package com.vanta.crystal.module.impl;

import com.vanta.crystal.CrystalClient;
import com.vanta.crystal.module.Module;
import com.vanta.crystal.util.Slots;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ObsidianModule extends Module {
    public ObsidianModule() {
        super("Obsidian",
                "Ставит обсидиан на грань блока под прицелом.\n" +
                "Работает, пока зажата клавиша.\n" +
                "ПКМ по модулю в GUI — переназначить клавишу.");
    }

    private int cooldown = 0;

    @Override
    public void onTick(MinecraftClient mc) {
        if (!isActive(mc) || mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (cooldown > 0) { cooldown--; return; }

        HitResult hit = mc.crosshairTarget;
        if (!(hit instanceof BlockHitResult bhr) || hit.getType() != HitResult.Type.BLOCK) return;

        int slot = Slots.findHotbar(mc.player, Items.OBSIDIAN);
        if (slot == -1) return;

        int prev = Slots.getSelected(mc.player);
        Slots.setSelected(mc.player, slot);

        BlockHitResult place = new BlockHitResult(bhr.getPos(), bhr.getSide(), bhr.getBlockPos(), false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, place);
        mc.player.swingHand(Hand.MAIN_HAND);

        Slots.setSelected(mc.player, prev);
        cooldown = CrystalClient.INSTANCE.prediction.placeCooldownTicks();
    }
}
