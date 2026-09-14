package com.vanta.crystal.module.impl;

import com.vanta.crystal.CrystalClient;
import com.vanta.crystal.adaptive.RotationController;
import com.vanta.crystal.module.Module;
import com.vanta.crystal.util.Slots;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoAnchorModule extends Module {
    public AutoAnchorModule() {
        super("AutoAnchor",
                "Ставит Respawn Anchor, заряжает 4 глоустоуна и детонирует.\n" +
                "1 нажатие = полный цикл: place → charge ×4 → detonate.\n" +
                "ПКМ по модулю в GUI — переназначить клавишу.");
    }

    private enum State { IDLE, PLACE, CHARGE, DETONATE }
    private State state = State.IDLE;
    private BlockPos anchorPos = null;
    private int ticks = 0;
    private int charges = 0;
    private boolean wasPressed = false;

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        boolean pressed = isActive(mc);
        if (pressed && !wasPressed && state == State.IDLE) {
            state = State.PLACE;
            ticks = 0;
            charges = 0;
        }
        wasPressed = pressed;

        switch (state) {
            case IDLE -> {}
            case PLACE -> doPlace(mc);
            case CHARGE -> doCharge(mc);
            case DETONATE -> doDetonate(mc);
        }
    }

    private void doPlace(MinecraftClient mc) {
        if (ticks++ < CrystalClient.INSTANCE.prediction.placeCooldownTicks()) return;

        HitResult hit = mc.crosshairTarget;
        if (!(hit instanceof BlockHitResult bhr) || hit.getType() != HitResult.Type.BLOCK) {
            state = State.IDLE; return;
        }
        int slot = Slots.findHotbar(mc.player, Items.RESPAWN_ANCHOR);
        if (slot == -1) { state = State.IDLE; return; }

        int prev = Slots.getSelected(mc.player);
        Slots.setSelected(mc.player, slot);

        BlockHitResult place = new BlockHitResult(bhr.getPos(), bhr.getSide(), bhr.getBlockPos(), false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, place);
        mc.player.swingHand(Hand.MAIN_HAND);

        Slots.setSelected(mc.player, prev);

        anchorPos = bhr.getBlockPos().offset(bhr.getSide());
        ticks = 0;
        state = State.CHARGE;
    }

    private void doCharge(MinecraftClient mc) {
        if (ticks++ < CrystalClient.INSTANCE.prediction.placeCooldownTicks()) return;
        ticks = 0;

        if (anchorPos == null) { state = State.IDLE; return; }
        var block = mc.world.getBlockState(anchorPos).getBlock();
        if (block != Blocks.RESPAWN_ANCHOR) { state = State.IDLE; return; }

        int slot = Slots.findHotbar(mc.player, Items.GLOWSTONE);
        if (slot == -1) { state = State.IDLE; return; }

        int prev = Slots.getSelected(mc.player);
        Slots.setSelected(mc.player, slot);

        var top = anchorPos.toCenterPos().add(0, 0.5, 0);
        RotationController rot = new RotationController();
        rot.aimAt(mc.player.getEyePos(), top, CrystalClient.INSTANCE.prediction.rotationSpeed());
        rot.tick(mc.player);

        var bhr = new BlockHitResult(top, Direction.UP, anchorPos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
        mc.player.swingHand(Hand.MAIN_HAND);

        Slots.setSelected(mc.player, prev);

        charges++;
        if (charges >= 4) { state = State.DETONATE; ticks = 0; }
    }

    private void doDetonate(MinecraftClient mc) {
        if (ticks++ < CrystalClient.INSTANCE.prediction.placeCooldownTicks()) return;
        if (anchorPos == null) { state = State.IDLE; return; }

        var top = anchorPos.toCenterPos().add(0, 0.5, 0);
        RotationController rot = new RotationController();
        rot.aimAt(mc.player.getEyePos(), top, CrystalClient.INSTANCE.prediction.rotationSpeed());
        rot.tick(mc.player);

        var bhr = new BlockHitResult(top, Direction.UP, anchorPos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
        mc.player.swingHand(Hand.MAIN_HAND);

        state = State.IDLE;
        anchorPos = null;
    }
                          }
