package io.codetoil.purpuritis.mixin.client;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.codetoil.purpuritis.Purpuritis;
import io.codetoil.purpuritis.client.render.item.properties.conditional.PurpuritisConditionalItemModelProperties;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientItemInfoLoader.class)
public class ClientItemInfoLoaderMixin {
    @ModifyReturnValue(method = "scheduleLoad", at = @At("RETURN"))
    private static CompletableFuture<ClientItemInfoLoader.LoadedClientInfos>
    onScheduleLoad(CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> original)
    {
        return original.thenApply(loadedClientInfos ->
                new ClientItemInfoLoader.LoadedClientInfos(Maps.transformValues(loadedClientInfos.contents(),
                (clientItem) -> {
                    assert clientItem != null;
                    return new ClientItem(purpuritis$createReplacementUnbakedModel(clientItem.model()),
                            new ClientItem.Properties(clientItem.properties().handAnimationOnSwap(),
                                    clientItem.properties().oversizedInGui()));
        })));
    }

    @Unique
    private static @NotNull ItemModel.Unbaked
    purpuritis$createPurpuredUnbakedModel(ItemModel.Unbaked unbakedItemModel) {
        if (unbakedItemModel instanceof BlockModelWrapper.Unbaked)
        {
            List<ItemTintSource> purpuredTints =
                    new ArrayList<>(((BlockModelWrapper.Unbaked) unbakedItemModel).tints());
            purpuredTints.add(new Constant(0xac7bac));
            return new BlockModelWrapper.Unbaked(((BlockModelWrapper.Unbaked) unbakedItemModel).model(), purpuredTints);
        }
        return new CompositeModel.Unbaked(List.of(unbakedItemModel,
                new BlockModelWrapper.Unbaked(ResourceLocation
                        .fromNamespaceAndPath(Purpuritis.MOD_ID, "purpured_tint"),
                        List.of())));
    }

    @Unique
    private static @NotNull ItemModel.Unbaked
    purpuritis$createReplacementUnbakedModel(ItemModel.Unbaked unbakedItemModel) {
        return new ConditionalItemModel.Unbaked(PurpuritisConditionalItemModelProperties.IS_PURPURED.get(),
                purpuritis$createPurpuredUnbakedModel(unbakedItemModel), unbakedItemModel);
    }
}
