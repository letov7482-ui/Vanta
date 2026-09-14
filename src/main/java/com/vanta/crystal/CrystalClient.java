package com.vanta.crystal;

import com.vanta.crystal.adaptive.PredictionEngine;
import com.vanta.crystal.adaptive.ServerProfiler;
import com.vanta.crystal.gui.ClickGUI;
import com.vanta.crystal.module.ModuleManager;
import com.vanta.crystal.module.impl.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CrystalClient implements ClientModInitializer {
    public static CrystalClient INSTANCE;

    public final ServerProfiler profiler = new ServerProfiler();
    public final PredictionEngine prediction = new PredictionEngine(profiler);
    public final ModuleManager modules = new ModuleManager();

    public KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        modules.register(
                new AutoCrystalModule(),
                new HitCrystalModule(),
                new ObsidianModule(),
                new AutoAnchorModule()
        );

        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vanta.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.vanta"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            profiler.tick(client);
            modules.tickAll(client);
            while (guiKey.wasPressed()) client.setScreen(new ClickGUI());
        });
    }
}
