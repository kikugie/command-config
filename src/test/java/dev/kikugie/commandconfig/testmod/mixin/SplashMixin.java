package dev.kikugie.commandconfig.testmod.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashTextResourceSupplier.class)
public class SplashMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void setSplash(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("§eTesting Command Config!");
    }
}
