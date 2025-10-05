package io.codetoil.purpuritis.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.codetoil.purpuritis.core.component.PurpuritisDataComponentTypes;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {

    @ModifyReturnValue(method = "getRarity", at = @At("RETURN"))
    private Rarity onGetRarity(Rarity rarity)
    {
        if (this.has(PurpuritisDataComponentTypes.PURPURED.get()))
        {
            return Rarity.BY_ID.apply(Math.min(rarity.ordinal() + 1, Rarity.values().length));
        }
        return rarity;
    }
}
