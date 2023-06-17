package hibi.boathud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HudRenderer {

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

	public void render(DrawContext context) {
		int scaledWidth = CLIENT.getWindow().getScaledWidth();
		int scaledHeight = CLIENT.getWindow().getScaledHeight();
		int i = scaledWidth / 2;
		int yOff = Config.yOffset + 6; // yes

		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		// Overlay texture and bar
		context.drawTexture(WIDGETS_TEXTURE, i - 91, scaledHeight - yOff - 20, 0, 30, 182, 26);
		int currentBarX = this.renderBar(context, i - 91, scaledHeight - yOff - 20);

		if(Common.hudData.isDriver) {
			// Sprites
			// Left-right
			context.drawTexture(WIDGETS_TEXTURE, i + 90, scaledHeight - yOff - 20, CLIENT.options.rightKey.isPressed() ? 200 : 192, 0, 4, 26);
			context.drawTexture(WIDGETS_TEXTURE, i - 94, scaledHeight - yOff - 20, CLIENT.options.leftKey.isPressed() ? 204 : 196, 0, 4, 26);

			// Pig
			context.drawTexture(WIDGETS_TEXTURE, i - 11, scaledHeight - yOff - 15, CLIENT.options.forwardKey.isPressed() ? 22 : 0, 56 ,22 ,20);
			// Brake
			if(CLIENT.options.backKey.isPressed()) context.drawTexture(WIDGETS_TEXTURE, i - 11, scaledHeight - yOff - 15, 44, 56, 22, 20);
		} else {
			context.drawTexture(WIDGETS_TEXTURE, i - 11, scaledHeight - yOff - 15, 22, 56 ,22 ,20);
		}
		// Ping
		renderPing(context, i - 77, scaledHeight - yOff - 4);


		// Text
		if(Common.hudData.isDriver) {
			if (Config.experimentalHud) {
				this.typeCentered(context, String.format(Config.gFormat, (float) Common.hudData.g), i - 52, scaledHeight - yOff - 14); // G
				this.renderSpeedText(context, currentBarX, scaledHeight - yOff - 28);
			}
			else this.typeCentered(context, getOvrSpeedIcon() + String.format(Config.speedFormat, Common.hudData.speed * Config.speedRate), i - 52, scaledHeight - yOff - 14);
			this.typeCentered(context, String.format(Config.angleFormat, Common.hudData.driftAngle), i + 52, scaledHeight - yOff - 14); // Angle
		}
		this.typeCentered(context, String.format("§f%03.0fms", (float) Common.hudData.ping), i - 50, scaledHeight - yOff - 4); // Ping
		this.typeCentered(context, String.format("§f%03.0f FPS", (float) Common.hudData.fps), i + 52, scaledHeight - yOff - 4); // FPS

		RenderSystem.disableBlend();
	}

	private String getOvrSpeedIcon() {
		if (Common.hudData.g > .1d) {
			// positive
			return "§a↑§f ";
		} else if (Common.hudData.g < -.1d) {
			// negative
			return "§c↓§f ";
		} else {
			// no acceleration
			return "§7-§f ";
		}
	}

	/** Renders the speed bar atop the HUD, uses displayedSpeed to, well, display the speed. */
	private int renderBar(DrawContext context, int x, int y) {
		context.drawTexture(WIDGETS_TEXTURE, x, y, 0, BAR_OFF[Config.barType], 182, 5);
		if(Common.hudData.speed < MIN_V[Config.barType]) return x;
		if(Common.hudData.speed > MAX_V[Config.barType]) {
			if(CLIENT.world.getTime() % 2 == 0) return x + 182;
			context.drawTexture(WIDGETS_TEXTURE, x, y, 0, BAR_ON[Config.barType], 182, 5);
			return x + 182;
		}
		context.drawTexture(WIDGETS_TEXTURE, x, y, 0, BAR_ON[Config.barType], (int)((Common.hudData.speed - MIN_V[Config.barType]) * SCALE_V[Config.barType]), 5);
		return x + (int)((Common.hudData.speed - MIN_V[Config.barType]) * SCALE_V[Config.barType]);
	}

	private void renderSpeedText(DrawContext context, int x, int y) {
		this.typeCentered(context, String.format(Config.speedFormat, Common.hudData.speed * Config.speedRate), x, y - 8);
		this.typeCentered(context, "↓", x, y);
	}

	/** Implementation is cloned from the notchian ping display in the tab player list.	 */
	private void renderPing(DrawContext context, int x, int y) {
		int y2;
		if(Common.hudData.ping < 0) {
			y2 = 35;
		}
		else if(Common.hudData.ping < 150) {
			y2 = 0;
		}
		else if(Common.hudData.ping < 300) {
			y2 = 7;
		}
		else if(Common.hudData.ping < 600) {
			y2 = 14;
		}
		else if(Common.hudData.ping < 1000) {
			y2 = 21;
		}
		else {
			y2 = 28;
		}
		context.drawTexture(WIDGETS_TEXTURE, x, y, 182, y2, 10, 7);
	}

	public void typeCentered(DrawContext context, String text, int centerX, int y) {
		context.drawText(MinecraftClient.getInstance().textRenderer, text, Math.round(centerX - MinecraftClient.getInstance().textRenderer.getWidth(text) / 2f), y, -1, true);
	}
}
