package com.vanta.crystal.module.impl;

import com.vanta.crystal.CrystalClient;
import com.vanta.crystal.adaptive.RotationController;
import com.vanta.crystal.module.Module;
import com.vanta.crystal.util.Slots;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoCrystalModule extends Module {
    public AutoCrystalModule() {
        super("AutoCrystal",
                "Ставит End Crystal на обсидиан (или бедрок) под прицелом.\n" +
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

        BlockPos base = bhr.getBlockPos();
        var state = mc.world.getBlockState(base);
        if (!state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.BEDROCK)) return;

        BlockPos top = base.up();
        if (!mc.world.getBlockState(top).isAir()) return;
        if (!mc.world.getFluidState(top).isEmpty()) return;

        Box box = new Box(top).expand(-0.2, 0, -0.2).stretch(0, 2, 0);
        if (!mc.world.getEntitiesByClass(EndCrystalEntity.class, box, e -> true).isEmpty()) return;

        int slot = Slots.findHotbar(mc.player, Items.END_CRYSTAL);
        if (slot == -1) return;

        Vec3d look = Vec3d.ofCenter(base).add(0, 1, 0);
        RotationController rot = new RotationController();
        rot.aimAt(mc.player.getEyePos(), look, CrystalClient.INSTANCE.prediction.rotationSpeed());
        rot.tick(mc.player);

        int prev = Slots.getSelected(mc.player);
        Slots.setSelected(mc.player, slot);

        BlockHitResult place = new BlockHitResult(bhr.getPos(), Direction.UP, base, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, place);
        mc.player.swingHand(Hand.MAIN_HAND);

        Slots.setSelected(mc.player, prev);
        cooldown = CrystalClient.INSTANCE.prediction.placeCooldownTicks();
    }
}
