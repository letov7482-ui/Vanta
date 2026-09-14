package com.vanta.crystal.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;

public final class Slots {
    private Slots() {}

    public static int findHotbar(ClientPlayerEntity p, Item item) {
        for (int i = 0; i < 9; i++) {
            if (p.getInventory().getStack(i).isOf(item)) return i;
        }
        return -1;
    }

    public static int getSelected(ClientPlayerEntity p) {
        try {
            return (int) p.getInventory().getClass().getMethod("getSelectedSlot").invoke(p.getInventory());
        } catch (Throwable t) {
            try {
                return p.getInventory().getClass().getField("selectedSlot").getInt(p.getInventory());
            } catch (Throwable ignored) { return 0; }
        }
    }

    public static void setSelected(ClientPlayerEntity p, int slot) {
        try {
            p.getInventory().getClass().getMethod("setSelectedSlot", int.class).invoke(p.getInventory(), slot);
        } catch (Throwable t) {
            try {
                p.getInventory().getClass().getField("selectedSlot").setInt(p.getInventory(), slot);
            } catch (Throwable ignored) {}
        }
    }
}
