package hibi.boathud;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer
extends DrawableHelper {

	private static final Identifier WIDGETS_TEXTURE = new Identifier("boathud","textures/widgets.png");
	private final MinecraftClient client;
	private int scaledWidth;
	private int scaledHeight;

	// The index to be used in these scales is the bar type (stored internally as an integer, defined in Config)
	//                                       Pack  Mix Blue
	private static final double[] MIN_V =   {  0d, 10d, 40d}; // Minimum display speed (m/s)
	private static final double[] MAX_V =   { 40d, 70d, 70d}; // Maximum display speed (m/s)
	private static final double[] SCALE_V = {4.5d,  3d,  6d}; // Pixels for 1 unit of speed (px*s/m) (BarWidth / (VMax - VMin))
	// V coordinates for each bar type in the texture file
	//                                    Pk Mix Blu
	private static final int[] BAR_OFF = { 0, 10, 20};
	private static final int[] BAR_ON =  { 5, 15, 25};

	public HudRenderer(MinecraftClient client) {
		this.client = client;
	}

	public void render(MatrixStack stack, float tickDelta) {
		this.scaledWidth = this.client.getWindow().getScaledWidth();
		this.scaledHeight = this.client.getWindow().getScaledHeight();
		int i = this.scaledWidth / 2;

		// Render boilerplate
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
			// Overlay texture and bar
			this.drawTexture(stack, i - 91, this.scaledHeight - 60, 0, 70, 182, 31);
			this.renderBar(stack, i - 91, this.scaledHeight - 60);

			// Sprites
			// Left-right
			this.drawTexture(stack, i + 90, this.scaledHeight - 60, this.client.options.rightKey.isPressed() ? 193 : 183, 0, 4, 26);
			this.drawTexture(stack, i - 94, this.scaledHeight - 60, this.client.options.leftKey.isPressed() ? 198 : 188, 0, 4, 26);

			// Pig
			this.drawTexture(stack, i - 11, this.scaledHeight - 55, this.client.options.forwardKey.isPressed() ? 119 : 96, 30 ,23 ,20);
			// Brake
			this.drawTexture(stack, i - 11, this.scaledHeight - 55, this.client.options.backKey.isPressed() ? 142 : 165, 30, 22, 20);

			// Speed sprite
			this.drawTexture(stack, i - 87, this.scaledHeight - 55, 203, getOvrSpeed(), 7, 9);

			// Speed and drift angle
			this.typeCentered(stack, String.format(Config.speedFormat, Common.hudData.speed * Config.speedRate), i - 58, this.scaledHeight - 54, 0xFFFFFF);
			this.typeCentered(stack, String.format(Config.angleFormat, Common.hudData.driftAngle), i + 58, this.scaledHeight - 54, 0xFFFFFF);
		RenderSystem.disableBlend();
	}

	private Integer getOvrSpeed() {
		if (Common.hudData.g > 0) {
			// positive
			return 0;
		} else if (Common.hudData.g < 0) {
			// negative
			return 9;
		} else {
			// no acceleration
			return 18;
		}
	}

	/** Renders the speed bar atop the HUD, uses displayedSpeed to, well, display the speed. */
	private void renderBar(MatrixStack stack, int x, int y) {
		this.drawTexture(stack, x, y, 0, BAR_OFF[Config.barType], 182, 5);
		if(Common.hudData.speed < MIN_V[Config.barType]) return;
		if(Common.hudData.speed > MAX_V[Config.barType]) {
			if(this.client.world.getTime() % 2 == 0) return;
			this.drawTexture(stack, x, y, 0, BAR_ON[Config.barType], 182, 5);
			return;
		}
		this.drawTexture(stack, x, y, 0, BAR_ON[Config.barType], (int)((Common.hudData.speed - MIN_V[Config.barType]) * SCALE_V[Config.barType]), 5);
	}

	/** Implementation is cloned from the notchian ping display in the tab player list.	 */
	private void renderPing(MatrixStack stack, int x, int y) {
		int offset = 0;
		if(Common.hudData.ping < 0) {
			offset = 40;
		}
		else if(Common.hudData.ping < 150) {
			offset = 0;
		}
		else if(Common.hudData.ping < 300) {
			offset = 8;
		}
		else if(Common.hudData.ping < 600) {
			offset = 16;
		}
		else if(Common.hudData.ping < 1000) {
			offset = 24;
		}
		else {
			offset = 32;
		}
		this.drawTexture(stack, x, y, 246, offset, 10, 8);
	}

	/** Renders a piece of text centered horizontally on an X coordinate. */
	private void typeCentered(MatrixStack stack, String text, int centerX, int y, int color) {
		this.client.textRenderer.drawWithShadow(stack, text, centerX - this.client.textRenderer.getWidth(text) / 2, y, color);
	}
}
