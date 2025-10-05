package io.codetoil.purpuritis.world.item;

import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PurpuritisItems {

    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, Purpuritis.MOD_ID);
}
