package hibi.boathud.mixin;

import hibi.boathud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.boathud.Common;
import hibi.boathud.config.Config;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Final
	@Shadow
    private LayeredDrawer layeredDrawer;

    @Inject(
			method = "<init>",
			at = @At(value = "TAIL")
	)
	private void init(MinecraftClient client, CallbackInfo ci) {
		layeredDrawer.addLayer((ctx, tickCounter) -> {
			if (Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen)) {
				HudRenderer.get().render(ctx);
			}
		});
	}

	@Inject(
			method = "renderStatusBars",
			at = @At("HEAD"),
			cancellable = true)
	private void renderStatusBars(DrawContext context, CallbackInfo ci) {
		if (Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderExperienceBar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if (Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderHotbar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (Config.enabled && Common.ridingBoat && !(Config.showHotbar || Common.client.currentScreen instanceof ChatScreen)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderExperienceLevel",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderExperienceLevel(DrawContext ctx, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (Config.enabled && Common.ridingBoat && !(Config.showHotbar || Common.client.currentScreen instanceof ChatScreen)) {
			ci.cancel();
		}
	}
}
