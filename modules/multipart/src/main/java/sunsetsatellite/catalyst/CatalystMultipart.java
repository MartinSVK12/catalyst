package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSupplier;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.util.BlockInitEntrypoint;
import turniplabs.halplibe.util.ItemInitEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.core.block.Blocks.*;


public class CatalystMultipart implements ModInitializer, BlockInitEntrypoint, ItemInitEntrypoint, RecipeEntrypoint {
	public static final String MOD_ID = HalpLibe.registerMod("catalyst-multipart", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final int blockIdStart = 3256;
	private static final int itemIdStart = 19640;

	public static final TomlConfigHandler config;

	public static final Tag<Block<?>> CAN_BE_MULTIPART = Tag.of("can_be_multipart");
	public static final HashMap<String, Tag<Block<?>>> TYPE_TAGS = new HashMap<>();

	/*public static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(Vector3f.class, new Vector3fJsonAdapter())
		.registerTypeAdapter(ModelData.class, new ModelDataJsonAdapter())
		.registerTypeAdapter(PositionData.class, new PositionDataJsonAdapter())
		.registerTypeAdapter(CubeData.class, new CubeDataJsonAdapter())
		.registerTypeAdapter(FaceData.class, new FaceDataJsonAdapter())
		.registerTypeAdapter(RotationData.class, new RotationDataJsonAdapter())
		.create();*/
	public static final Side[] sides = new Side[]{Side.BOTTOM, Side.TOP, Side.NORTH, Side.SOUTH, Side.WEST, Side.EAST};
	public static String renderState = "gui";

	static {
		List<Field> blockFields = Arrays.stream(CatalystMultipart.class.getDeclaredFields()).filter((F) -> Block.class.isAssignableFrom(F.getType())).collect(Collectors.toList());
		List<Field> itemFields = Arrays.stream(CatalystMultipart.class.getDeclaredFields()).filter((F) -> Item.class.isAssignableFrom(F.getType())).collect(Collectors.toList());

		Toml defaultConfig = new Toml("Catalyst: Multipart configuration file.");
		defaultConfig.addCategory("BlockIDs");
		defaultConfig.addCategory("ItemIDs");

		int blockId = blockIdStart;
		int itemId = itemIdStart;
		for (Field blockField : blockFields) {
			defaultConfig.addEntry("BlockIDs." + blockField.getName(), blockId++);
		}
		for (Field itemField : itemFields) {
			defaultConfig.addEntry("ItemIDs." + itemField.getName(), itemId++);
		}

		config = new TomlConfigHandler(MOD_ID, new Toml("Catalyst: Multipart configuration file."), false);

		File configFile = config.getConfigFile();

		if (config.getConfigFile().exists()) {
			config.loadConfig();
			config.setDefaults(config.getRawParsed());
			Toml rawConfig = config.getRawParsed();
			int maxBlocks = ((Toml) rawConfig.get(".BlockIDs")).getOrderedKeys().size();
			int maxItems = ((Toml) rawConfig.get(".ItemIDs")).getOrderedKeys().size();
			int newNextBlockId = blockIdStart + maxBlocks;
			int newNextItemId = itemIdStart + maxItems;
			boolean changed = false;

			for (Field F : blockFields) {
				if (!rawConfig.contains("BlockIDs." + F.getName())) {
					rawConfig.addEntry("BlockIDs." + F.getName(), newNextBlockId++);
					changed = true;
				}
			}
			for (Field F : itemFields) {
				if (!rawConfig.contains("ItemIDs." + F.getName())) {
					rawConfig.addEntry("ItemIDs." + F.getName(), newNextItemId++);
					changed = true;
				}
			}
			if (changed) {
				config.setDefaults(rawConfig);
				config.writeConfig();
				config.loadConfig();
			}
		} else {
			config.setDefaults(defaultConfig);
			try {
				//noinspection ResultOfMethodCallIgnored
				configFile.getParentFile().mkdirs();
				//noinspection ResultOfMethodCallIgnored
				configFile.createNewFile();
				config.writeConfig();
				config.loadConfig();
			} catch (IOException e) {
				throw new RuntimeException("Failed to generate config!", e);
			}
		}
	}

	public static Tag<Block<?>>[] getAllMultipartTags() {
		ArrayList<Tag<Block<?>>> list = new ArrayList<>(TYPE_TAGS.values());
		return list.toArray(new Tag[0]);
	}

	public static int item(String cfgId) {
		return config.getInt("ItemIDs." + cfgId);
	}

	public static int block(String cfgId) {
		return config.getInt("BlockIDs." + cfgId);
	}

	public static NamespaceID id(String id) {
		return NamespaceID.fromPool(MOD_ID, id);
	}

	@Override
	public void onInitialize() {

		//EntityHelper.createTileEntity(TileEntityMultipart.class, id("multipart"));
		//EntityHelper.createTileEntity(TileEntityCarpenterWorkbench.class, id("carpenter_workbench"));

		LOGGER.info("Catalyst: Multipart initialized.");
	}

	//public static Block<? extends BlockLogic> multipartBlock;
	//public static Block<? extends BlockLogic> carpenterWorkbench;

	//public static ItemMultipart multipartItem;

	@Override
	public void afterBlockInit() {
		/*multipartBlock = customBlock(
			new BlockBuilder(MOD_ID).setBlockSound(BlockSounds.STONE).setHardness(0.5f).setResistance(202),
			"multipart", "multipart", "multipartBlock", 1,
			BlockLogicMultipart::new
		).withTags(BlockTags.NOT_IN_CREATIVE_MENU);

		carpenterWorkbench = customBlock(
			new BlockBuilder(MOD_ID).setBlockSound(BlockSounds.STONE).setHardness(0.5f).setResistance(202),
			"workbench.carpenter", "carpenter_workbench", "carpenterWorkbench", 1,
			BlockLogicCarpenterWorkbench::new
		);*/

		MultipartType.types.forEach((K, V) -> TYPE_TAGS.put(K, Tag.of(K)));

		ArrayList<Tag<Block<?>>> list = new ArrayList<>(TYPE_TAGS.values());
		Tag<Block<?>>[] tags = list.toArray(new Tag[0]);

		//carpenterWorkbench.withTags(CAN_BE_MULTIPART).withTags(tags);

		STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		MARBLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		SLATE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PERMAFROST.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_STONE_MOSSY.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_PERMAFROST.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_NETHERRACK.withTags(CAN_BE_MULTIPART).withTags(tags);
		COBBLE_NETHERRACK_CRYSTALLINE.withTags(CAN_BE_MULTIPART).withTags(tags);
		NETHERRACK_CARVED.withTags(CAN_BE_MULTIPART).withTags(tags);
		NETHERRACK_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		STONE_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRANITE_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		LIMESTONE_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		BASALT_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		SLATE_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		PERMAFROST_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		PILLAR_MARBLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		CAPSTONE_MARBLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		SANDSTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		STONE_CARVED.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRANITE_CARVED.withTags(CAN_BE_MULTIPART).withTags(tags);
		LIMESTONE_CARVED.withTags(CAN_BE_MULTIPART).withTags(tags);
		BASALT_CARVED.withTags(CAN_BE_MULTIPART).withTags(tags);
		PERMAFROST_CARVED.withTags(CAN_BE_MULTIPART).withTags(tags);
		PLANKS_OAK.withTags(CAN_BE_MULTIPART).withTags(tags);
		PLANKS_OAK_PAINTED.withTags(CAN_BE_MULTIPART).withTags(tags);
		BOOKSHELF_PLANKS_OAK.withTags(CAN_BE_MULTIPART).withTags(tags);
		WOOL.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_CLAY.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_STONE_POLISHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_STONE_POLISHED_MOSSY.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_SANDSTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_GOLD.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_LAPIS.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_MARBLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_SLATE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_PERMAFROST.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_IRON.withTags(CAN_BE_MULTIPART).withTags(tags);
		OBSIDIAN.withTags(CAN_BE_MULTIPART).withTags(tags);
		GLASS.withTags(CAN_BE_MULTIPART).withTags(tags);
		GLASS_TINTED.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRASS.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRASS_RETRO.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRASS_SCORCHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		PATH_DIRT.withTags(CAN_BE_MULTIPART).withTags(tags);
		DIRT.withTags(CAN_BE_MULTIPART).withTags(tags);
		DIRT_SCORCHED.withTags(CAN_BE_MULTIPART).withTags(tags);
		DIRT_SCORCHED_RICH.withTags(CAN_BE_MULTIPART).withTags(tags);
		MUD.withTags(CAN_BE_MULTIPART).withTags(tags);
		MUD_BAKED.withTags(CAN_BE_MULTIPART).withTags(tags);
		SPONGE_DRY.withTags(CAN_BE_MULTIPART).withTags(tags);
		SPONGE_WET.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOSS_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOSS_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOSS_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOSS_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		SAND.withTags(CAN_BE_MULTIPART).withTags(tags);
		GRAVEL.withTags(CAN_BE_MULTIPART).withTags(tags);
		BEDROCK.withTags(CAN_BE_MULTIPART).withTags(tags);
		FLUID_WATER_FLOWING.withTags(CAN_BE_MULTIPART).withTags(tags);
		FLUID_WATER_STILL.withTags(CAN_BE_MULTIPART).withTags(tags);
		FLUID_LAVA_FLOWING.withTags(CAN_BE_MULTIPART).withTags(tags);
		FLUID_LAVA_STILL.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_OAK.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_PINE.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_BIRCH.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_CHERRY.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_EUCALYPTUS.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_OAK_MOSSY.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_THORN.withTags(CAN_BE_MULTIPART).withTags(tags);
		LOG_PALM.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_OAK.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_OAK_RETRO.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_PINE.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_BIRCH.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_CHERRY.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_EUCALYPTUS.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_SHRUB.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_CHERRY_FLOWERING.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_CACAO.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_THORN.withTags(CAN_BE_MULTIPART).withTags(tags);
		LEAVES_PALM.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_COAL_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_COAL_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_COAL_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_COAL_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_IRON_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_IRON_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_IRON_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_IRON_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_GOLD_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_GOLD_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_GOLD_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_GOLD_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_LAPIS_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_LAPIS_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_LAPIS_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_LAPIS_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_GLOWING_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_GLOWING_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_GLOWING_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_REDSTONE_GLOWING_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_DIAMOND_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_DIAMOND_BASALT.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_DIAMOND_LIMESTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_DIAMOND_GRANITE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ORE_NETHERCOAL_NETHERRACK.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_COAL.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_IRON.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_GOLD.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_LAPIS.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_REDSTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_DIAMOND.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_NETHER_COAL.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_STEEL.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_QUARTZ.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_OLIVINE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_CHARCOAL.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOTION_SENSOR_ACTIVE.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOTION_SENSOR_IDLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ACTIVATOR.withTags(CAN_BE_MULTIPART).withTags(tags);
		PISTON_BASE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PISTON_BASE_STICKY.withTags(CAN_BE_MULTIPART).withTags(tags);
		PISTON_BASE_STEEL.withTags(CAN_BE_MULTIPART).withTags(tags);
		NOTEBLOCK.withTags(CAN_BE_MULTIPART).withTags(tags);
		DISPENSER_COBBLE_STONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		TNT.withTags(CAN_BE_MULTIPART).withTags(tags);
		MESH.withTags(CAN_BE_MULTIPART).withTags(tags);
		MESH_GOLD.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOBSPAWNER.withTags(CAN_BE_MULTIPART).withTags(tags);
		MOBSPAWNER_DEACTIVATED.withTags(CAN_BE_MULTIPART).withTags(tags);
		WORKBENCH.withTags(CAN_BE_MULTIPART).withTags(tags);
		FURNACE_STONE_IDLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		FURNACE_STONE_ACTIVE.withTags(CAN_BE_MULTIPART).withTags(tags);
		FURNACE_BLAST_IDLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		FURNACE_BLAST_ACTIVE.withTags(CAN_BE_MULTIPART).withTags(tags);
		TROMMEL_IDLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		TROMMEL_ACTIVE.withTags(CAN_BE_MULTIPART).withTags(tags);
		CHEST_LEGACY.withTags(CAN_BE_MULTIPART).withTags(tags);
		CHEST_LEGACY_PAINTED.withTags(CAN_BE_MULTIPART).withTags(tags);
		CHEST_PLANKS_OAK.withTags(CAN_BE_MULTIPART).withTags(tags);
		CHEST_PLANKS_OAK_PAINTED.withTags(CAN_BE_MULTIPART).withTags(tags);
		FARMLAND_DIRT.withTags(CAN_BE_MULTIPART).withTags(tags);
		LAYER_SNOW.withTags(CAN_BE_MULTIPART).withTags(tags);
		LAYER_LEAVES_OAK.withTags(CAN_BE_MULTIPART).withTags(tags);
		LAYER_SLATE.withTags(CAN_BE_MULTIPART).withTags(tags);
		ICE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PERMAICE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_SNOW.withTags(CAN_BE_MULTIPART).withTags(tags);
		CACTUS.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_CLAY.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_SUGARCANE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BLOCK_SUGARCANE_BAKED.withTags(CAN_BE_MULTIPART).withTags(tags);
		JUKEBOX.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMPKIN.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMPKIN_CARVED_IDLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMPKIN_CARVED_ACTIVE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMPKIN_REDSTONE.withTags(CAN_BE_MULTIPART).withTags();
		NETHERRACK.withTags(CAN_BE_MULTIPART).withTags(tags);
		BRICK_NETHERRACK.withTags(CAN_BE_MULTIPART).withTags(tags);
		SOULSAND.withTags(CAN_BE_MULTIPART).withTags(tags);
		SOULSCHIST.withTags(CAN_BE_MULTIPART).withTags(tags);
		GLOWSTONE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PORTAL_NETHER.withTags(CAN_BE_MULTIPART).withTags(tags);
		PORTAL_DRIFT.withTags(CAN_BE_MULTIPART).withTags(tags);
		CAKE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMPKIN_PIE.withTags(CAN_BE_MULTIPART).withTags(tags);
		LAMP_IDLE.withTags(CAN_BE_MULTIPART).withTags(tags);
		LAMP_ACTIVE.withTags(CAN_BE_MULTIPART).withTags(tags);
		BASKET.withTags(CAN_BE_MULTIPART).withTags(tags);
		PAPER_WALL.withTags(CAN_BE_MULTIPART).withTags(tags);
		BONESHALE.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMICE_DRY.withTags(CAN_BE_MULTIPART).withTags(tags);
		PUMICE_WET.withTags(CAN_BE_MULTIPART).withTags(tags);

		SAPLING_OAK.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_OAK_RETRO.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_PINE.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_BIRCH.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_CHERRY.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_EUCALYPTUS.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_SHRUB.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_CACAO.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_THORN.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SAPLING_PALM.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		TALLGRASS.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		TALLGRASS_FERN.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		DEADBUSH.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		SPINIFEX.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		ALGAE.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		FLOWER_YELLOW.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		FLOWER_RED.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		FLOWER_PINK.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		FLOWER_PURPLE.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		FLOWER_LIGHT_BLUE.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		FLOWER_ORANGE.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		MUSHROOM_BROWN.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		MUSHROOM_RED.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
	}

	@Override
	public void afterItemInit() {
		//multipartItem = (ItemMultipart) new ItemBuilder(MOD_ID).build(new ItemMultipart("multipart", "catalyst-multipart:item/multipart", item("multipartItem"))).withTags(ItemTags.NOT_IN_CREATIVE_MENU);
	}

	public <T extends BlockLogic> Block<T> customBlock(BlockBuilder builder, String lang, String name, String configId, int miningLevel, BlockLogicSupplier<T> blockLogicSupplier) {
		Block<T> block = builder.build(lang, name, block(configId), blockLogicSupplier);
		ItemToolPickaxe.miningLevels.put(block, miningLevel);
		//LOGGER.info("Registering block '{}'.", block.namespaceId());
		return block;
	}

	public static String key(String key) {
		return CatalystMultipart.MOD_ID + ":" + key;
	}

	@Override
	public void onRecipesReady() {
		/*RecipeBuilder.Shaped(MOD_ID, "CCC", "CTC", "CCC")
			.addInput('C', "minecraft:cobblestones")
			.addInput('T', WORKBENCH)
			.create("carpenter_workbench", new ItemStack(carpenterWorkbench, 1));*/
	}

	@Override
	public void initNamespaces() {
		RecipeNamespace namespace = new RecipeNamespace();
		final RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
		namespace.register("workbench", WORKBENCH);
		Registries.RECIPES.register("catalyst-multipart", namespace);
	}
}
