package hibi.boathud;

import com.mojang.blaze3d.systems.RenderSystem;
import hibi.boathud.config.Config;
import hibi.boathud.config.SpeedUnits;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

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

	private static final List<QueuedText> queuedTexts = new ArrayList<>();

	public static HudRenderer get() {
		return INSTANCE;
	}

	public void render(DrawContext context) {
		int scaledWidth = CLIENT.getWindow().getScaledWidth();
		int scaledHeight = CLIENT.getWindow().getScaledHeight();
		int i = scaledWidth / 2;
		int yOff = Config.yOffset + 6; // yes
		int scaledY = scaledHeight - yOff;

		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		// Overlay texture and bar
		if(!Config.smallHud) context.drawTexture(WIDGETS_TEXTURE, i - 91, scaledY - 20, 0, 30, 182, 26);
		else context.drawTexture(WIDGETS_TEXTURE, i - 91, scaledY - 20, 0, 76, 182, 16);
		int currentBarX = -1;
		if(Common.hudData.isDriver) currentBarX = this.renderBar(context, i - 91, scaledY - 20);

		if(Common.hudData.isDriver) {
			if(!Config.smallHud) {
				// Sprites
				// Left-right
				context.drawTexture(WIDGETS_TEXTURE, i + 90, scaledY - 20, CLIENT.options.rightKey.isPressed() ? 200 : 192, 0, 4, 26);
				context.drawTexture(WIDGETS_TEXTURE, i - 94, scaledY - 20, CLIENT.options.leftKey.isPressed() ? 204 : 196, 0, 4, 26);
				// Pig
				context.drawTexture(WIDGETS_TEXTURE, i - 11, scaledY - 15, CLIENT.options.forwardKey.isPressed() ? 22 : 0, 56 ,22 ,20);
				// Brake
				if(CLIENT.options.backKey.isPressed()) context.drawTexture(WIDGETS_TEXTURE, i - 11, scaledY - 15, 44, 56, 22, 20);
			} else {
				// Sprites
				// Left-right
				context.drawTexture(WIDGETS_TEXTURE, i + 90, scaledY - 20, CLIENT.options.rightKey.isPressed() ? 216 : 208, 0, 4, 16);
				context.drawTexture(WIDGETS_TEXTURE, i - 94, scaledY - 20, CLIENT.options.leftKey.isPressed() ? 220 : 212, 0, 4, 16);
			}
		} else if(!Config.smallHud) {
			context.drawTexture(WIDGETS_TEXTURE, i - 11, scaledY - 15, 22, 56 ,22 ,20);
		}

		// Ping
		if(!Config.smallHud) {
			renderPing(context, i - 77, scaledY - 4);
			queuedTexts.add(new QueuedText(String.format("§f%03.0fms", (float) Common.hudData.ping), i - 50, scaledY - 4));
		}

		// Text
		if(Common.hudData.isDriver) {
			if (Config.experimentalHud) {
				queuedTexts.add(new QueuedText(String.format(Config.distanceUnit.format(), Common.hudData.distanceTraveled * Config.distanceUnit.rate()), i - 52, scaledY - 14)); // G text
				this.renderSpeedText(context, currentBarX, scaledY - 28);
			}
			else queuedTexts.add(new QueuedText(getOvrSpeedIcon() + String.format(SpeedUnits.currentlySelected().speedFormat(), Common.hudData.speed * SpeedUnits.currentlySelected().speedRate()), i - 52, scaledY - 14));
			queuedTexts.add(new QueuedText(String.format(Config.angleFormat, Common.hudData.driftAngle), i + 52, scaledY - 14)); // Angle
		}
		if(!Config.smallHud) {
			queuedTexts.add(new QueuedText(String.format("§f%03.0f FPS", (float) Common.hudData.fps), i + 52, scaledY - 4)); // FPS
		}

		for(QueuedText queuedText : queuedTexts) queuedText.typeCentred(context);
		queuedTexts.clear();

		RenderSystem.disableBlend();
	}

	private String getOvrSpeedIcon() {
		if (Common.hudData.g > .001d) {
			// positive
			return "§a↑§f ";
		} else if (Common.hudData.g < -.001d) {
			// negative
			return "§c↓§f ";
		} else {
			// no acceleration
			return "§7-§f ";
		}
	}

	/** Renders the speed bar atop the HUD, uses displayedSpeed to, well, display the speed. */
	private int renderBar(DrawContext context, int x, int y) {
		if(!Common.hudData.isDriver) return -1;
		context.drawTexture(WIDGETS_TEXTURE, x, y, 0, BAR_OFF[Config.barType], 182, 5);
		if(Common.hudData.speed < MIN_V[Config.barType]) return x;
		if(Common.hudData.speed > MAX_V[Config.barType]) {
			if(CLIENT.world.getTime() % 2 == 0) return x + 182;
			context.drawTexture(WIDGETS_TEXTURE, x, y, 0, BAR_ON[Config.barType], 182, 5);
			return x + 182;
		}
		context.drawTexture(WIDGETS_TEXTURE, x, y, 0, BAR_ON[Config.barType], (int)((Common.hudData.speed - MIN_V[Config.barType]) * SCALE_V[Config.barType] + 1.5), 5);
		return x + (int)((Common.hudData.speed - MIN_V[Config.barType]) * SCALE_V[Config.barType] + 1.5);
	}

	private void renderSpeedText(DrawContext context, int x, int y) {
		TextRenderer tr = MinecraftClient.getInstance().textRenderer;

		String text = String.format(SpeedUnits.currentlySelected().speedFormat(), Common.hudData.speed * SpeedUnits.currentlySelected().speedRate());
		int textWidth = tr.getWidth(text);

		// round(centreX - textWidth / 2f)
		if(Common.hudData.g > .01d) text = text.concat(" §a→");
		else if(Common.hudData.g < -.01d) {
			text = "§c←§f ".concat(text);
			x -= tr.getWidth("← ");
		}
		context.drawText(tr, text, Math.round(x - textWidth / 2f), y - 8, -1, true);

		queuedTexts.add(new QueuedText("↓", (Common.hudData.g < -.01d) ? x + tr.getWidth("← ") : x, y));
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
}
