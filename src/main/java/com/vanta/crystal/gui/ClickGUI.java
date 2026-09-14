package com.vanta.crystal.gui;

import com.vanta.crystal.CrystalClient;
import com.vanta.crystal.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    private final List<ModuleButton> buttons = new ArrayList<>();

    public ClickGUI() { super(Text.literal("VANTA")); }

    @Override
    protected void init() {
        buttons.clear();
        int y = 44;
        for (Module m : CrystalClient.INSTANCE.modules.getAll()) {
            buttons.add(new ModuleButton(m, 18, y, 200, 22));
            y += 26;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x66000000);
        ctx.fill(12, 12, 226, height - 12, 0x40000000);
        ctx.fill(14, 14, 224, height - 14, 0xF00B0B12);
        ctx.fill(14, 14, 224, 38, 0xFF18182A);
        ctx.drawTextWithShadow(textRenderer, "VANTA CRYSTAL", 24, 22, 0xFFB080FF);

        String status = String.format("strict %.2f  ping %.0fms",
                CrystalClient.INSTANCE.profiler.getStrictness(),
                CrystalClient.INSTANCE.profiler.getLatency());
        int sw = textRenderer.getWidth(status);
        ctx.drawTextWithShadow(textRenderer, status, 224 - sw - 12, 22, 0xFF808090);

        for (ModuleButton b : buttons) b.render(ctx, mouseX, mouseY, delta);

        for (ModuleButton b : buttons) {
            if (b.isHovered(mouseX, mouseY)) {
                renderTooltip(ctx, b.getModule().getDescription(), mouseX + 14, mouseY + 10);
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderTooltip(DrawContext ctx, String desc, int x, int y) {
        String[] lines = desc.split("\n");
        int maxW = 0;
        for (String l : lines) maxW = Math.max(maxW, textRenderer.getWidth(l));
        int pad = 6;
        int boxW = maxW + pad * 2;
        int boxH = lines.length * 11 + pad * 2;
        if (x + boxW > width - 4) x = width - boxW - 4;
        if (y + boxH > height - 4) y = height - boxH - 4;

        ctx.fill(x, y, x + boxW, y + boxH, 0xF0121220);
        ctx.fill(x, y, x + boxW, y + 1, 0xFF8B5CF6);
        for (int i = 0; i < lines.length; i++) {
            ctx.drawTextWithShadow(textRenderer, lines[i], x + pad, y + pad + i * 11, 0xFFE0E0E8);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (ModuleButton b : buttons) {
            if (b.isHovered((int) mx, (int) my)) {
                if (button == 0) b.getModule().toggle();
                else if (button == 1) b.getModule().setListeningForBind(true);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleButton b : buttons) {
            if (b.getModule().isListeningForBind()) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) b.getModule().setListeningForBind(false);
                else b.getModule().setKeybind(keyCode);
                b.getModule().setListeningForBind(false);
                return true;
            }
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}
