package io.codetoil.purpuritis.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.codetoil.purpuritis.core.component.PurpuritisDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ItemMixin {

    @Final
    @Shadow
    private DataComponentMap components;

    @ModifyReturnValue(method = "getName()Lnet/minecraft/network/chat/Component;", at = @At("RETURN"))
    public Component onGetName(Component value)
    {
        if (this.components.has(PurpuritisDataComponentTypes.PURPURED.get()))
        {
            return Component.literal("Purpured ").append(value);
        }
        return value;
    }

    @WrapMethod(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;")
    public Component onGetNameItemStack(ItemStack stack, Operation<Component> operation)
    {
        Component value = operation.call(stack);
        if (stack.getComponents().has(PurpuritisDataComponentTypes.PURPURED.get())) {
            return Component.literal("Purpured ").append(value);
        }
        return value;
    }
}
