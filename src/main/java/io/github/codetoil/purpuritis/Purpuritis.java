package io.github.codetoil.purpuritis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import io.github.codetoil.purpuritis.block.IPurpuredBlock;
import io.github.codetoil.purpuritis.block.PurpuredBlock;
import io.github.codetoil.purpuritis.block.PurpuredItemBlock;
import io.github.codetoil.purpuritis.item.*;
import io.github.codetoil.purpuritis.world.dimension.CopyModDimension;
import io.github.codetoil.purpuritis.world.dimension.DimensionPurpuredOverworld;
import io.github.codetoil.purpuritis.world.gen.CopyChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.data.IDataProvider;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("purpuritis")
public class Purpuritis
{
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public static Purpuritis INSTANCE;

	public static ModDimension purpuritis_overworld_dim;
	public static DimensionType purpuritis_overworld;
	public static BiMap<Block, Block> blockConversionMap;
	public static BiMap<Biome, Biome> biomeConversionMap;
	public static BiMap<Integer, Integer> dimensionConversionMap;
	public static ResourceLocation purpuritis_overworld_loc = new ResourceLocation("purpuritis", "purpuritis_overworld");
	public static ResourceLocation purpuritis_overworld_chunk_generator_loc = new ResourceLocation("purpuritis", "purpuritis_overworld_chunk_generator");
	public static ResourceLocation purpuritis_overworld_biome_provider_loc = new ResourceLocation("purpuritis", "purpuritis_overworld_biome_provider");
	public static BiMap<Block, IPurpuredBlock> purpuredBlocks = HashBiMap.create();
	public static BiMap<Item, IPurpuredItem> purpuredItems = HashBiMap.create();
	public static BiMap<IItemTier, PurpuredTier> purpuredItemTiers = HashBiMap.create();
	public static BiMap<IArmorMaterial, PurpuredArmorMaterial> purpuredArmorMaterials = HashBiMap.create();
	public IDataProvider dataProvider;
	public RegistryEvents events;
	public boolean hasSetFirstToLoad = false;
	private Queue<ChunkPos> chunkPositionQueue = Queues.newArrayDeque();
	private boolean loadedAllChunks = false;

	public Purpuritis()
	{
		LOGGER.info("Constructing Purpuritis");
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		// Gather Data
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onGatherData);
		events = new RegistryEvents();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onBlocksRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onItemRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onDimensionTypeRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onChunkGeneratorTypeRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onBiomeProviderRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onModDimensionRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(events::onRegistryCreation);

		INSTANCE = this;

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);


		// Setup Conversion Maps, no stuff added until fill[Normal,Server]ConversionMaps()
		setupConversionMaps();

		// make sure Purpuritis is last!
		setFirstToLoad();
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		// some preinit code
		LOGGER.info("HELLO FROM SETUP");
		fillNormalConversionMaps();
		//LOGGER.info("STONE BLOCK >> {}", Blocks.STONE.getRegistryName());
	}

	private void enqueueIMC(final InterModEnqueueEvent event)
	{
		// some example code to dispatch IMC to another mod
		// InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
	}

	private void processIMC(final InterModProcessEvent event)
	{
		List<Object> objects = event.getIMCStream().
				map(m -> m.getMessageSupplier().get()).
				collect(Collectors.toList());
		// some example code to receive and process InterModComms from other mods
		// LOGGER.info("Got IMC {}", event.getIMCStream().
		// 		map(m -> m.getMessageSupplier().get()).
		// 		collect(Collectors.toList()));
	}

	private void doClientStuff(final FMLClientSetupEvent event)
	{
		// do something that can only be done on the client
		// LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
	}

	public void onGatherData(final GatherDataEvent event)
	{
		LOGGER.info("We are running a data version of minecraft forge, gathering data and printing");
		dataProvider = new DataProviderPurpuritis(event.getGenerator());
		event.getGenerator().addProvider(dataProvider);
	}

	public static void setupConversionMaps()
	{
		blockConversionMap = HashBiMap.create();
		biomeConversionMap = HashBiMap.create();
		dimensionConversionMap = HashBiMap.create();
	}

	//Somewhat unsafe... ignore that fact
	private void setFirstToLoad()
	{
		if (hasSetFirstToLoad) return;
		LOGGER.info("Changing net.minecraftforge.fml.ModList.mods to make sure purpuritis is last");
		AbstractList modList = ObfuscationReflectionHelper.getPrivateValue(ModList.class, ModList.get(), "mods");
		LOGGER.info("net.minecraftforge.fml.ModList.mods before: " + modList.stream().map(container -> ((ModContainer) container).getModId()).collect(Collectors.toList()));
		final int modificationAmount = ObfuscationReflectionHelper.getPrivateValue(AbstractList.class, modList, "modCount");
		if (modList == null) {
			throw new ReportedException(new CrashReport("modList is null", new NullPointerException("modList is null")));
		}
		if (ModList.get().getModContainerById("purpuritis").isPresent()) {
			ModContainer container = ModList.get().getModContainerById("purpuritis").get();
			modList.remove(container);
			modList.add(container);
		} else {
			throw new IllegalStateException("Purpuritis does not have a mod container!");
		}
		LOGGER.info("net.minecraftforge.fml.ModList.mods after: " + modList.stream().map(container -> ((ModContainer) container).getModId()).collect(Collectors.toList()));
		ObfuscationReflectionHelper.setPrivateValue(AbstractList.class, modList, modificationAmount, "modCount");
		LOGGER.info("Set modCount(amount of modifications) to value before change to prevent ConcurrentModificationException");
		hasSetFirstToLoad = true;
		LOGGER.info("Done!");
	}

	public static void fillNormalConversionMaps()
	{
		purpuredBlocks.forEach((block, purpuredBlock) -> {
			blockConversionMap.put(block, purpuredBlock.getPurpuredBlock());
		});
		Biome.BIOMES.forEach(biome -> {
			//biomeConversionMap.put(biome, Biomes.THE_VOID);
		});
	}

	public static void fillServerConversionMaps()
	{
		if (purpuritis_overworld.getRegistryName() != null) {
			purpuritis_overworld.setRegistryName(purpuritis_overworld_loc);
		}
		dimensionConversionMap.put(DimensionType.OVERWORLD.getId(), purpuritis_overworld.getId());
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event)
	{
		// do something when the server starts
		LOGGER.info("HELLO from server starting");
	}

	@SubscribeEvent
	public void onServerStopped(FMLServerStoppedEvent event)
	{
		loadedAllChunks = false;
		LOGGER.info("Server has stopped");
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		if (event.phase.equals(TickEvent.Phase.START)) return;
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		Iterator<ServerWorld> worldIterator = server.getWorlds().iterator();
		while (worldIterator.hasNext()) {
			ServerWorld world = worldIterator.next();
			if (world.getChunkProvider().generator instanceof CopyChunkGenerator<?>) {
				// TODO Set Chunk provider, somehow... maybe set the world to something else? Like ServerWorldCopy?
			}
		}
		if (chunkPositionQueue.isEmpty()) return;
		LOGGER.info("Going through queue of size " + chunkPositionQueue.size());
		long startTime = System.currentTimeMillis();
		while (!chunkPositionQueue.isEmpty()) {
			ChunkPos pos = chunkPositionQueue.remove();
			server.getWorlds().forEach((serverWorld) -> {
				if (!serverWorld.isRemote && serverWorld.getChunkProvider().generator instanceof CopyChunkGenerator<?>) {
					ServerChunkProvider provider = serverWorld.getChunkProvider();
					IChunk chunk = provider.getChunk(pos.x, pos.z, false);
					DimensionType normalType = convertDimension(serverWorld.dimension.getType());
					ServerWorld world = server.getWorld(normalType);
					IChunk oldChunk = world.getChunkProvider().getChunk(pos.x, pos.z, false);
					if (oldChunk == null) {
						oldChunk = new ChunkPrimer(pos, UpgradeData.EMPTY);
						world.getChunkProvider().generator.generateBiomes(oldChunk);
						oldChunk = new Chunk(world, (ChunkPrimer) oldChunk);
						if (world.getChunkProvider().isChunkLoaded(pos)) {
							world.getChunkProvider().generator.generateSurface(oldChunk);
						} else {
							LOGGER.info("Chunk {} is not loaded!", oldChunk);
						}
					}
					chunk = ((CopyChunkGenerator<?>) provider.generator).generateChunk(chunk, oldChunk);
					ChunkManager manager = provider.chunkManager;
					if (chunk != null) {
						CompoundNBT nbt = ChunkSerializer.write(world, chunk);
						Supplier<DimensionSavedDataManager> supplier = ObfuscationReflectionHelper.getPrivateValue(ChunkManager.class, manager, "field_219259_m");
						Validate.notNull(supplier, "Supplier is null");
						try {
							manager.writeChunk(pos, nbt);
							manager.readChunk(pos);
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						//LOGGER.info(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_222869_l"));
						try {
							ObfuscationReflectionHelper.findMethod(ServerChunkProvider.class, "func_222869_l").invoke(serverWorld.getChunkProvider());
						}
						catch (Throwable t) {
							t.printStackTrace();
							throw new ReportedException(new CrashReport("Could not find or invoke method func_222869, has a coremod changed it's name and parameters?", t));
						}
					}
				}
			});
		}
		LOGGER.info("Finished going through queue");
		loadedAllChunks = true;
		Purpuritis.LOGGER.info("loadingChunks took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public DimensionType convertDimension(DimensionType oldType)
	{
		Integer newType;
		if (dimensionConversionMap.containsKey(oldType.getId())) {
			newType = dimensionConversionMap.get(oldType.getId());
		} else if (dimensionConversionMap.containsValue(oldType.getId())) {
			newType = dimensionConversionMap.inverse().get(oldType.getId());
		} else {
			newType = oldType.getId();
		}
		return DimensionType.getById(newType);
	}

	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if (this.loadedAllChunks) return;
		chunkPositionQueue.add(event.getChunk().getPos());
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	// @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public class RegistryEvents
	{
		public void onRegistryCreation(final RegistryEvent.NewRegistry newRegistry)
		{

		}

		@SubscribeEvent
		public void onBlocksRegistry(final RegistryEvent.Register<Block> register)
		{
			if (register.getGenericType().equals(Block.class)) {
				LOGGER.info("Registering Blocks");
				ForgeRegistries.BLOCKS.forEach((block) -> {
					if (!(block instanceof PurpuredBlock)) {
						PurpuredBlock block1 = new PurpuredBlock(block);
						purpuredBlocks.put(block, block1);
					}
				});
				((ForgeRegistry) ForgeRegistries.BLOCKS).unfreeze();
				purpuredBlocks.forEach((block, purpuredBlock) -> ForgeRegistries.BLOCKS.register(purpuredBlock.getPurpuredBlock()));
			}
		}

		@SubscribeEvent
		public void onItemRegistry(final RegistryEvent.Register<Item> register)
		{
			if (register.getGenericType().equals(Item.class)) {
				if (purpuredItemTiers.isEmpty()) {
					ItemTier[] tiers = ItemTier.values();
					for (IItemTier tier :
							tiers) {
						purpuredItemTiers.put(tier, new PurpuredTier(tier));
					}
				}
				LOGGER.info(purpuredItemTiers);
				if (purpuredArmorMaterials.isEmpty()) {
					ArmorMaterial[] materials = ArmorMaterial.values();
					for (ArmorMaterial material :
							materials) {
						purpuredArmorMaterials.put(material, new PurpuredArmorMaterial(material));
					}
				}
				LOGGER.info(purpuredArmorMaterials);
				registerItems();
			}
		}

		public void registerItems()
		{
			LOGGER.info("Registering Items");
			ForgeRegistries.ITEMS.forEach((item) -> {
				LOGGER.info("purpurifying item: " + item);
				if (!(item instanceof PurpuredItem)) {
					if (item instanceof SwordItem) {
						addToolSword((SwordItem) item);
					} else if (item instanceof ShovelItem) {
						addToolShovel((ShovelItem) item);
					} else if (item instanceof PickaxeItem) {
						addToolPickaxe((PickaxeItem) item);
					} else if (item instanceof AxeItem) {
						addToolAxe((AxeItem) item);
					} else if (item instanceof HoeItem) {
						addItemHoe((HoeItem) item);
					} else if (item instanceof ArmorItem) {
						addItemArmor((ArmorItem) item);
					} else if (item instanceof BlockItem)
					{
						BlockItem original = (BlockItem) item;
						Block orignialBlock = original.getBlock();
						IPurpuredBlock block = purpuredBlocks.get(orignialBlock);
						Validate.notNull(block, "Block " + orignialBlock + " did not have a registry!");
						addItemBlock((BlockItem) item, block);
					} else {
						addNormalItem(item);
					}
				}
			});
			((ForgeRegistry) ForgeRegistries.ITEMS).unfreeze();
			purpuredItems.forEach((item, purpuredItem) -> ForgeRegistries.ITEMS.register(purpuredItem.asItem()));
		}

		private void addToolSword(SwordItem item)
		{
			PurpuredItemSword item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			verifyItemTier(item.getTier());
			//LOGGER.info(speed);
			item1 = new PurpuredItemSword(item, purpuredItemTiers.get(item.getTier()), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackDamage").iterator().next().getAmount(), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackSpeed").iterator().next().getAmount(), properties);
			purpuredItems.put(item, item1);
		}

		private void addToolShovel(ShovelItem item)
		{
			PurpuredItemShovel item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			verifyItemTier(item.getTier());
			Set<Block> field_150814_c = ObfuscationReflectionHelper.getPrivateValue(ToolItem.class, item, "field_150914_c");
			Set<Block> effectientBlocks = Sets.newHashSet();
			effectientBlocks.addAll(field_150814_c);
			effectientBlocks.addAll(field_150814_c.stream().map(block -> purpuredBlocks.get(block).getPurpuredBlock()).collect(Collectors.toSet()));
			item1 = new PurpuredItemShovel(item, purpuredItemTiers.get(item.getTier()), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackDamage").iterator().next().getAmount(), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackSpeed").iterator().next().getAmount(), effectientBlocks, properties);
			purpuredItems.put(item, item1);
		}

		private void addToolPickaxe(PickaxeItem item)
		{
			PurpuredItemPickaxe item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			verifyItemTier(item.getTier());
			Set<Block> field_150814_c = ObfuscationReflectionHelper.getPrivateValue(ToolItem.class, item, "field_150914_c");
			Set<Block> effectientBlocks = Sets.newHashSet();
			effectientBlocks.addAll(field_150814_c);
			effectientBlocks.addAll(field_150814_c.stream().map(block -> purpuredBlocks.get(block).getPurpuredBlock()).collect(Collectors.toSet()));
			item1 = new PurpuredItemPickaxe(item, purpuredItemTiers.get(item.getTier()), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackDamage").iterator().next().getAmount(), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackSpeed").iterator().next().getAmount(), effectientBlocks, properties);
			purpuredItems.put(item, item1);
		}

		private void addToolAxe(AxeItem item)
		{
			PurpuredItemAxe item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			verifyItemTier(item.getTier());
			Set<Block> field_150814_c = ObfuscationReflectionHelper.getPrivateValue(ToolItem.class, item, "field_150914_c");
			Set<Block> effectientBlocks = Sets.newHashSet();
			effectientBlocks.addAll(field_150814_c);
			effectientBlocks.addAll(field_150814_c.stream().map(block -> purpuredBlocks.get(block).getPurpuredBlock()).collect(Collectors.toSet()));
			item1 = new PurpuredItemAxe(item, purpuredItemTiers.get(item.getTier()), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackDamage").iterator().next().getAmount(), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackSpeed").iterator().next().getAmount(), effectientBlocks, properties);
			purpuredItems.put(item, item1);
		}

		private void addItemHoe(HoeItem item)
		{
			PurpuredItemHoe item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			verifyItemTier(item.getTier());
			item1 = new PurpuredItemHoe(item, purpuredItemTiers.get(item.getTier()), (float) item.getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackSpeed").iterator().next().getAmount(), properties);
			purpuredItems.put(item, item1);
		}

		private void addItemArmor(ArmorItem item)
		{
			PurpuredItemArmor item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			verifyArmorMaterial(item.getArmorMaterial());
			item1 = new PurpuredItemArmor(item, purpuredArmorMaterials.get(item.getArmorMaterial()), item.getEquipmentSlot(), properties);
			purpuredItems.put(item, item1);
		}

		private void addNormalItem(Item item)
		{
			PurpuredItem item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			item1 = new PurpuredItem(item, properties);
			purpuredItems.put(item, item1);
		}

		private void addItemBlock(BlockItem item, IPurpuredBlock blockNew)
		{
			PurpuredItemBlock item1;
			Item.Properties properties = new Item.Properties();
			addCommonStuffItem(item, properties);
			item1 = new PurpuredItemBlock(item, blockNew, properties);
			purpuredItems.put(item, item1);
		}

		private void addCommonStuffItem(Item item, Item.Properties properties)
		{
			Set<ToolType> toolTypes = item.getToolTypes(new ItemStack(item));
			toolTypes.forEach(toolType -> {
				properties.addToolType(toolType, 4 + item.getHarvestLevel(new ItemStack(item), toolType, null, null));
			});
			if (item.getMaxDamage() > 0) {
				properties.maxDamage(item.getMaxDamage() * 2);
			} else {
				properties.maxStackSize(item.getMaxStackSize());
			}

			if (item.isFood()) {
				Food food;
				Food foodOriginal = item.getFood();
				Food.Builder builder = new Food.Builder();
				if (foodOriginal.canEatWhenFull()) {
					builder = builder.setAlwaysEdible();
				}
				if (foodOriginal.isFastEating()) {
					builder = builder.fastToEat();
				}
				if (foodOriginal.isMeat()) {
					builder = builder.meat();
				}
				builder = builder.saturation(foodOriginal.getSaturation() + 1.2F);
				builder.hunger(foodOriginal.getHealing() + 5);
				List<Pair<EffectInstance, Float>> effects = foodOriginal.getEffects();
				for (Pair<EffectInstance, Float> effectInstanceFloatPair :
						effects) {
					builder = builder.effect(effectInstanceFloatPair.getLeft(), effectInstanceFloatPair.getRight() * 2);
				}
				food = builder.build();
				properties.food(food);
			}
			if (item.getGroup() != null) {
				properties.group(item.getGroup());
			}
			Rarity rarity = item.getRarity(new ItemStack(item));
			Rarity newRarity = Rarity.COMMON;
			switch (rarity) {
				case COMMON:
					newRarity = Rarity.UNCOMMON;
					break;
				case UNCOMMON:
					newRarity = Rarity.RARE;
					break;
				case RARE:
					newRarity = Rarity.EPIC;
					break;
				case EPIC:
					newRarity = Rarity.EPIC;
					break;
			}
			properties.rarity(newRarity);
			if (FMLEnvironment.dist.isClient()) {
				if (item.getTileEntityItemStackRenderer() != null) {
					properties.setTEISR(() -> {
						return item::getTileEntityItemStackRenderer;
					});
				}
			}
		}

		private void verifyItemTier(IItemTier tier)
		{
			if (!purpuredItemTiers.containsKey(tier))
			{
				purpuredItemTiers.put(tier, new PurpuredTier(tier));
			}
		}

		private void verifyArmorMaterial(IArmorMaterial material)
		{
			if (!purpuredArmorMaterials.containsKey(material))
			{
				purpuredArmorMaterials.put(material, new PurpuredArmorMaterial(material));
			}
		}

		@SubscribeEvent
		public void onDimensionTypeRegistry(final RegistryEvent.Register<DimensionType> register)
		{
			if (register.getGenericType().equals(DimensionType.class)) {
				LOGGER.info("HELLO from Register DimensionType");
				if (purpuritis_overworld_dim == null) {
					purpuritis_overworld_dim = new CopyModDimension(Purpuritis.purpuritis_overworld_loc, DimensionPurpuredOverworld.class);
				}
				if (purpuritis_overworld == null) {
					purpuritis_overworld = DimensionManager.registerDimension(purpuritis_overworld_loc, purpuritis_overworld_dim, null, true);
					DimensionManager.keepLoaded(purpuritis_overworld, true);
				}
				fillServerConversionMaps();
			}
		}

		@SubscribeEvent
		public void onChunkGeneratorTypeRegistry(final RegistryEvent.Register<ChunkGeneratorType<?, ?>> register)
		{
			if (register.getGenericType().equals(ChunkGeneratorType.class)) {

			}
			//if (ForgeRegistries.CHUNK_GENERATOR_TYPES instanceof ForgeRegistry && !((ForgeRegistry) ForgeRegistries.CHUNK_GENERATOR_TYPES).isLocked())
			//	ForgeRegistries.CHUNK_GENERATOR_TYPES.register(new ChunkGeneratorType<>(new CopyChunkGeneratorFactory<>(blockConversionMap), false, PurpuredOverworldGenSettings::getMAIN).setRegistryName(purpuritis_overworld_chunk_generator_loc));
			//else
			//	LOGGER.warn("Registry for CHUNK_GENERATOR_TYPES is locked...");
		}

		@SubscribeEvent
		public void onBiomeProviderRegistry(final RegistryEvent.Register<BiomeProviderType<?, ?>> register)
		{
			if (register.getGenericType().equals(BiomeProviderType.class)) {

			}
			//if (ForgeRegistries.BIOME_PROVIDER_TYPES instanceof ForgeRegistry && !((ForgeRegistry) ForgeRegistries.BIOME_PROVIDER_TYPES).isLocked())
			//	ForgeRegistries.BIOME_PROVIDER_TYPES.register(new BiomeProviderType<>((settings) -> {return new CopyBiomeProvider(biomeConversionMap);}, PurpuritisOverworldBiomeProviderSettings::new).setRegistryName(purpuritis_overworld_biome_provider_loc));
			//else
			//	LOGGER.warn("Registry for BIOME_PROVIDER_TYPES is locked...");
		}

		@SubscribeEvent
		public void onModDimensionRegistry(final RegistryEvent.Register<ModDimension> register)
		{
			if (register.getGenericType().equals(ModDimension.class)) {
				if (purpuritis_overworld_dim == null) {
					purpuritis_overworld_dim = new CopyModDimension(Purpuritis.purpuritis_overworld_loc, DimensionPurpuredOverworld.class);
				}
				ForgeRegistries.MOD_DIMENSIONS.register(purpuritis_overworld_dim);
			}
		}
	}
}
