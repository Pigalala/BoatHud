package hibi.boathud.mixin;

import hibi.boathud.HudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.boathud.Common;
import hibi.boathud.Config;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Inject(
		method = "render",
		at = @At("TAIL")
	)
	private void render(MatrixStack stack, float tickDelta, CallbackInfo info) {
		if(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen)) {
			HudRenderer.get().render(stack);
		}
	}

	@Inject(
			method = "renderStatusBars",
			at = @At("HEAD"),
			cancellable = true)
	private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
		if(!(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen))) return;
		ci.cancel();
	}

	@Inject(
			method = "renderExperienceBar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderExperienceBar(MatrixStack matrices, int x, CallbackInfo ci) {
		if(!(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen))) return;
		ci.cancel();
	}

	@Inject(
			method = "renderHotbar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
		if(!(Config.enabled && Common.ridingBoat && !(Common.client.currentScreen instanceof ChatScreen))) return;
		ci.cancel();
	}
}
