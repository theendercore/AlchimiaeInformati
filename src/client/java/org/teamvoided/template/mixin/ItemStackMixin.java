package org.teamvoided.template.mixin;

import com.ssblur.alchimiae.alchemy.ClientAlchemyHelper;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teamvoided.template.IconRendererData;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
    void x(CallbackInfoReturnable<Optional<TooltipData>> cir) {
        var returnValue = cir.getReturnValue();
        if (returnValue.isEmpty()) {
            var list = ClientAlchemyHelper.get((ItemStack) (Object) this);
            if (list != null ) cir.setReturnValue(Optional.of(new IconRendererData(list)));
        }
    }
}
