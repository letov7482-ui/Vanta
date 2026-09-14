package com.vanta.crystal.gui;

import com.vanta.crystal.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModuleButton {
    private final Module module;
    private final int x, y, w, h;

    public ModuleButton(Module module, int x, int y, int w, int h) {
        this.module = module; this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public Module getModule() { return module; }
    public boolean isHovered(int mx, int my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }

    public void render(DrawContext ctx, int mx, int my, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean hover = isHovered(mx, my);

        int bg = module.isEnabled()
                ? (hover ? 0xFF2A2A55 : 0xFF1E1E3A)
                : (hover ? 0xFF1A1A22 : 0xFF121218);
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.fill(x, y, x + 3, y + h, module.isEnabled() ? 0xFF8B5CF6 : 0xFF3A3A44);

        String label = module.getName();
        int color = module.isEnabled() ? 0xFFFFFFFF : 0xFFB0B0B8;
        ctx.drawTextWithShadow(mc.textRenderer, label, x + 10, y + 7, color);

        String keyText;
        if (module.isListeningForBind()) keyText = "[ ... ]";
        else if (module.getKeybind() == GLFW.GLFW_KEY_UNKNOWN) keyText = "[ - ]";
        else keyText = "[ " + InputUtil.fromKeyCode(module.getKeybind(), 0).getLocalizedText().getString() + " ]";

        int kw = mc.textRenderer.getWidth(keyText);
        ctx.drawTextWithShadow(mc.textRenderer, keyText, x + w - kw - 8, y + 7, 0xFF9A9AA5);
    }
}
