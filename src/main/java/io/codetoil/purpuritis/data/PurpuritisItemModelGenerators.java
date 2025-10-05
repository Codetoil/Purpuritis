package io.codetoil.purpuritis.data;

import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class PurpuritisItemModelGenerators extends ItemModelGenerators {
    public PurpuritisItemModelGenerators(ItemModelOutput p_375677_,
                                         BiConsumer<ResourceLocation, ModelInstance> p_377569_) {
        super(p_375677_, p_377569_);
    }

    @Override
    public void run() {
        ModelTemplates.FLAT_ITEM.create(ResourceLocation.fromNamespaceAndPath(Purpuritis.MOD_ID,
                "purpured_tint"), TextureMapping.layer0(ResourceLocation.
                fromNamespaceAndPath(Purpuritis.MOD_ID, "purpured_tint")), modelOutput);
    }
}
