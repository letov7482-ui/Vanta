package com.vanta.crystal.module;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void register(Module... mods) {
        for (Module m : mods) modules.add(m);
    }

    public List<Module> getAll() { return modules; }

    public <T extends Module> T get(Class<T> clazz) {
        for (Module m : modules) if (clazz.isInstance(m)) return clazz.cast(m);
        return null;
    }

    public void tickAll(MinecraftClient mc) {
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick(mc);
        }
    }
}
