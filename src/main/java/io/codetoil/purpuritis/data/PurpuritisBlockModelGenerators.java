package io.codetoil.purpuritis.data;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PurpuritisBlockModelGenerators extends BlockModelGenerators {
    public PurpuritisBlockModelGenerators(Consumer<BlockModelDefinitionGenerator> p_378137_, ItemModelOutput p_378502_,
                                          BiConsumer<ResourceLocation, ModelInstance> p_378240_) {
        super(p_378137_, p_378502_, p_378240_);
    }

    @Override
    public void run() {

    }
}
