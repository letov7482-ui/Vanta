package com.vanta.crystal.module;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    private final String name;
    private final String description;
    private int keybind = GLFW.GLFW_KEY_UNKNOWN;
    private boolean enabled;
    private boolean listeningForBind;

    public Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void onTick(MinecraftClient mc);
    public void onDisable() {}

    public boolean isActive(MinecraftClient mc) {
        if (!enabled) return false;
        if (keybind == GLFW.GLFW_KEY_UNKNOWN) return true;
        long h = mc.getWindow().getHandle();
        return GLFW.glfwGetKey(h, keybind) == GLFW.GLFW_PRESS;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int k) { this.keybind = k; }
    public boolean isEnabled() { return enabled; }
    public boolean isListeningForBind() { return listeningForBind; }
    public void setListeningForBind(boolean b) { this.listeningForBind = b; }
    public void toggle() {
        enabled = !enabled;
        if (!enabled) onDisable();
    }
}
