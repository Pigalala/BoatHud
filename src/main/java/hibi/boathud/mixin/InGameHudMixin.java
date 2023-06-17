package hibi.boathud.mixin;

import hibi.boathud.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.boathud.Common;
import hibi.boathud.Config;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Inject(
		method = "render",
		at = @At(
				value = "INVOKE",
				target = "net/minecraft/client/MinecraftClient.getProfiler()Lnet/minecraft/util/profiler/Profiler;",
				ordinal = 8
		)
	)
	private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
		if(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen)) {
			HudRenderer.get().render(context);
		}
	}

	@Inject(
			method = "renderStatusBars",
			at = @At("HEAD"),
			cancellable = true)
	private void renderStatusBars(DrawContext context, CallbackInfo ci) {
		if(!(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen))) return;
		ci.cancel();
	}

	@Inject(
			method = "renderExperienceBar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if(!(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen))) return;
		ci.cancel();
	}

	@Inject(
			method = "renderHotbar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
		if(!(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen))) return;
		ci.cancel();
	}
}
