package hibi.boathud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public record QueuedText(String text, int centreX, int y) {
    public void typeCentred(DrawContext drawer) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        drawer.drawText(tr, text, Math.round(centreX - tr.getWidth(text) / 2f), y, -1, true);
    }
}
