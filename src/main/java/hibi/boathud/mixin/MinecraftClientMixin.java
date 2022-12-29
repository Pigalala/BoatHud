package hibi.boathud.mixin;

import hibi.boathud.Common;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow private static int currentFps;

    @Inject(method = "render", at = @At("TAIL"))
    private void getCurrentFPS(boolean tick, CallbackInfo ci) {
        if(tick && Common.ridingBoat) Common.hudData.fps = currentFps;
    }
}
