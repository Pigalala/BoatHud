package hibi.boathud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HudRenderer extends DrawableHelper {

	private static final Identifier WIDGETS_TEXTURE = new Identifier("boathud","textures/widgets.png");
	private static final HudRenderer INSTANCE = new HudRenderer();
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	// The index to be used in these scales is the bar type (stored internally as an integer, defined in Config)
	//                                       Pack  Mix Blue
	private static final double[] MIN_V =   {  0d, 10d, 40d}; // Minimum display speed (m/s)
	private static final double[] MAX_V =   { 40d, 70d, 70d}; // Maximum display speed (m/s)
	private static final double[] SCALE_V = {4.5d,  3d,  6d}; // Pixels for 1 unit of speed (px*s/m) (BarWidth / (VMax - VMin))
	// V coordinates for each bar type in the texture file
	//                                    Pk Mix Blu
	private static final int[] BAR_OFF = { 0, 10, 20};
	private static final int[] BAR_ON =  { 5, 15, 25};

	public static HudRenderer get() {
		return INSTANCE;
	}

	public void render(MatrixStack stack) {
		int scaledWidth = CLIENT.getWindow().getScaledWidth();
		int scaledHeight = CLIENT.getWindow().getScaledHeight();
		int i = scaledWidth / 2;
		int yOff = Config.yOffset + 6;

		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		// Overlay texture and bar
		this.drawTexture(stack, i - 91, scaledHeight - yOff - 20, 0, 70, 182, 31);
		this.renderBar(stack, i - 91, scaledHeight - yOff - 20);

		// Sprites
		// Left-right
		this.drawTexture(stack, i + 90, scaledHeight - yOff - 20, CLIENT.options.rightKey.isPressed() ? 193 : 183, 0, 4, 26);
		this.drawTexture(stack, i - 94, scaledHeight - yOff - 20, CLIENT.options.leftKey.isPressed() ? 198 : 188, 0, 4, 26);

		// Pig
		this.drawTexture(stack, i - 11, scaledHeight - yOff - 15, CLIENT.options.forwardKey.isPressed() ? 119 : 96, 30 ,23 ,20);
		// Brake
		this.drawTexture(stack, i - 11, scaledHeight - yOff - 15, CLIENT.options.backKey.isPressed() ? 142 : 165, 30, 22, 20);

		// Speed sprite
		this.drawTexture(stack, i - 87, scaledHeight - yOff - 15, 203, getOvrSpeed(), 7, 9);

		// Ping
		renderPing(stack, i - 87, scaledHeight - yOff - 4);

		// Text
		this.typeCentered(stack, String.format(Config.speedFormat, Common.hudData.speed * Config.speedRate), i - 58, scaledHeight - yOff - 14); // Speed
		this.typeCentered(stack, String.format(Config.angleFormat, Common.hudData.driftAngle), i + 62, scaledHeight - yOff - 14); // Angle
		this.typeCentered(stack, getPingColour() + String.format("%03.0f§fms", (float) Common.hudData.ping), i - 60, scaledHeight - yOff - 4); // Ping
		this.typeCentered(stack, getFPSColour() + String.format("%03.0f §fFPS", (float) Common.hudData.fps), i + 62, scaledHeight - yOff - 4); // FPS

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

	private String getPingColour() {
		if(Common.hudData.ping < 1000) {
			return "§f";
		}
		else {
			return "§c";
		}
	}

	private String getFPSColour() {
		if(Common.hudData.fps < CLIENT.options.getMaxFps().getValue() * 0.25) {
			return "§c";
		} else if(Common.hudData.fps >= CLIENT.options.getMaxFps().getValue() * 0.95) {
			return "§a";
		} else {
			return "§f";
		}
	}

	/** Renders the speed bar atop the HUD, uses displayedSpeed to, well, display the speed. */
	private void renderBar(MatrixStack stack, int x, int y) {
		this.drawTexture(stack, x, y, 0, BAR_OFF[Config.barType], 182, 5);
		if(Common.hudData.speed < MIN_V[Config.barType]) return;
		if(Common.hudData.speed > MAX_V[Config.barType]) {
			if(CLIENT.world.getTime() % 2 == 0) return;
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

	public void typeCentered(MatrixStack stack, String text, int centerX, int y) {
		MinecraftClient.getInstance().textRenderer.drawWithShadow(stack, text, centerX - MinecraftClient.getInstance().textRenderer.getWidth(text) / 2f, y, 0xFFFFFF);
	}
}
