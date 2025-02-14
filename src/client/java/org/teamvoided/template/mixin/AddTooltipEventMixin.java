package org.teamvoided.template.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.ssblur.alchimiae.events.AddTooltipEvent;

@Mixin(AddTooltipEvent.class)
public class AddTooltipEventMixin {

    @Inject(method = "append", at = @At("HEAD"), cancellable = true)
    private void doNotAppendIRepeatDoNotAppend(CallbackInfo ci) {
        if (Screen.hasShiftDown()) ci.cancel();
    }
}
