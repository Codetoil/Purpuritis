package io.codetoil.purpuritis;

import com.google.common.collect.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import io.codetoil.purpuritis.data.PurpuritisModelProvider;
import io.codetoil.purpuritis.data.PurpuritisLootTableProvider;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Purpuritis.MOD_ID)
public class Purpuritis {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "purpuritis";

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final RegistryObject<DataComponentType<Unit>> PURPURED = DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            .register("purpured", () -> DataComponentType.<Unit>builder()
                    .persistent(Unit.CODEC)
                    .networkSynchronized(Unit.STREAM_CODEC)
                    .build());

    private static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public Purpuritis(FMLJavaModLoadingContext context) {
        LOGGER.info("Constructing Purpuritis");
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);
        InterModEnqueueEvent.getBus(modBusGroup).addListener(this::enqueueIMC);
        InterModProcessEvent.getBus(modBusGroup).addListener(this::processIMC);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(this::clientSetup);
        GatherDataEvent.getBus(modBusGroup).addListener(this::gatherData);
        ModelEvent.ModifyBakingResult.getBus(modBusGroup).addListener(this::onModifyBakingResult);
        TickEvent.ServerTickEvent.Pre.BUS.addListener(this::preServerTickEvent);
        DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(modBusGroup);
        ITEM_DEFERRED_REGISTER.register(modBusGroup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM SETUP");
        //LOGGER.info("STONE BLOCK >> {}", Blocks.STONE.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        // InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        @SuppressWarnings("unused") List<Object> objects = event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList());
        // some example code to receive and process InterModComms from other mods
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        // LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    public void gatherData(final GatherDataEvent event) {
        LOGGER.info("We are running a data version of minecraft forge, gathering data and printing");
        event.getGenerator().addProvider(true, (DataProvider.Factory<PurpuritisModelProvider>)
                PurpuritisModelProvider::new);
        event.getGenerator().addProvider(true, (DataProvider.Factory<PurpuritisLootTableProvider>)
                (output) -> new PurpuritisLootTableProvider(
                        output,
                        Collections.emptySet(),
                        List.of(),
                        event.getLookupProvider()
                ));
    }

    public void onModifyBakingResult(final ModelEvent.ModifyBakingResult event) {
        HasComponent hasComponent = new HasComponent(PURPURED.get(), false);

        event.getResults().itemStackModels().forEach((resourceLocation, itemModel) -> {
            ClientItem clientItem = event.getModelBakery().clientInfos.get(resourceLocation);

            assert itemModel != null;
            ItemModel.Unbaked unbakedPurpuredItemModel = new ItemModel.Unbaked() {
                @Override
                public @NotNull MapCodec<? extends ItemModel.Unbaked> type() {
                    return clientItem.model().type();
                }

                @Override
                public @NotNull ItemModel bake(ItemModel.@NotNull BakingContext p_376062_) {
                    return clientItem.model().bake(p_376062_);
                }

                @Override
                public void resolveDependencies(@NotNull Resolver p_376736_) {
                    clientItem.model().resolveDependencies(p_376736_);
                }
            };
            ClientItem purpuredClientItem = new ClientItem(unbakedPurpuredItemModel,
                    new ClientItem.Properties(clientItem.properties().handAnimationOnSwap(),
                            clientItem.properties().oversizedInGui()));
            ItemModel purpuredItemModel = unbakedPurpuredItemModel.bake(
                    new ItemModel.BakingContext(
                            event.getModelBakery(). new ModelBakerImpl(new SpriteGetter() {
                                private final TextureAtlasSprite missingSprite = p_422832_.missing();

                                @Override
                                public TextureAtlasSprite get(Material p_375858_, ModelDebugName p_375833_) {
                                    if (p_375858_.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
                                        TextureAtlasSprite textureatlassprite = p_422832_.getSprite(p_375858_.texture());
                                        if (textureatlassprite != null) {
                                            return textureatlassprite;
                                        }
                                    }

                                    multimap.put(p_375833_.debugName(), p_375858_);
                                    return this.missingSprite;
                                }

                                @Override
                                public TextureAtlasSprite reportMissingReference(String p_378821_, ModelDebugName p_377684_) {
                                    multimap1.put(p_377684_.debugName(), p_378821_);
                                    return this.missingSprite;
                                }
                            }),
                            event.getModelBakery().entityModelSet,
                            event.getModelBakery().materials,
                            event.getModelBakery().playerSkinRenderCache,
                            event.getResults().missingModels().item(),
                            purpuredClientItem.registrySwapper()
                    ));
            event.getResults().itemStackModels()
                    .replace(resourceLocation, new ConditionalItemModel(hasComponent, purpuredItemModel, itemModel));
        });
    }

    public void preServerTickEvent(final TickEvent.ServerTickEvent.Pre event) {

    }
}
