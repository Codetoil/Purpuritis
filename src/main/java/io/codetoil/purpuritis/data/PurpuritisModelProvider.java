package io.codetoil.purpuritis.data;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PurpuritisModelProvider extends ModelProvider {
    public PurpuritisModelProvider(PackOutput p_378149_) {
        super(p_378149_);
    }

    @Override
    protected Stream<Item> getKnownItems() {
        return Stream.empty();
    }

    @Override
    protected Stream<Block> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected BlockModelGenerators getBlockModelGenerators(BlockStateGeneratorCollector blocks,
                                                           ItemInfoCollector items, SimpleModelCollector models) {
        return new PurpuritisBlockModelGenerators(blocks, items, models);
    }

    @Override
    protected ItemModelGenerators getItemModelGenerators(ItemInfoCollector items, SimpleModelCollector models) {
        return new PurpuritisItemModelGenerators(items, models);
    }
}
