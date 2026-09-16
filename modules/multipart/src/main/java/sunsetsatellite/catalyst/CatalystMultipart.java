package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSupplier;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.block.logic.BlockLogicCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.block.logic.BlockLogicMultipart;
import sunsetsatellite.catalyst.multipart.item.ItemMultipart;
import sunsetsatellite.catalyst.multipart.tile.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.tile.TileEntityMultipart;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.dependency.Key;
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


public class CatalystMultipart implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("catalyst-multipart", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final int blockIdStart = 3256;
	private static final int itemIdStart = 19640;

	public static final TomlConfigHandler config;

	public static final Tag<Block<?>> CAN_BE_MULTIPART = Tag.of("can_be_multipart");
	public static final HashMap<String, Tag<Block<?>>> TYPE_TAGS = new HashMap<>();

	@Override
	public void onInitialize() {

		CommonEvents.AFTER_BLOCK_INIT.listen(Key.of(MOD_ID), this::afterBlockInit);
		CommonEvents.AFTER_ITEM_INIT.listen(Key.of(MOD_ID), this::afterItemInit);
		CommonEvents.RECIPES_READY.listen(Key.of(MOD_ID), this::onRecipesReady);
		CommonEvents.RECIPES_NAMESPACE_INIT.listen(Key.of(MOD_ID), this::initNamespaces);
		TileEntityDispatcher.addMapping(TileEntityMultipart.class, id("multipart"));
		TileEntityDispatcher.addMapping(TileEntityCarpenterWorkbench.class, id("carpenter_workbench"));

		LOGGER.info("Catalyst: Multipart initialized.");
	}

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

	public static Block<BlockLogicMultipart> multipartBlock;
	public static Block<? extends BlockLogic> carpenterWorkbench;

	public static ItemMultipart multipartItem;

	public void afterBlockInit() {
		multipartBlock = customBlock(
			new BlockBuilder(MOD_ID).setBlockSound(BlockSounds.STONE).setHardness(0.5f).setResistance(202),
			"multipart", "multipart", "multipartBlock", 0,
			BlockLogicMultipart::new
		).withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);

		carpenterWorkbench = customBlock(
			new BlockBuilder(MOD_ID).setBlockSound(BlockSounds.STONE).setHardness(3f).setResistance(202).setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS)),
			"workbench.carpenter", "carpenter_workbench", "carpenterWorkbench", 1,
			BlockLogicCarpenterWorkbench::new
		).withTags(BlockTags.MINEABLE_BY_PICKAXE);

		MultipartType.types.forEach((K, V) -> TYPE_TAGS.put(K, Tag.of(K)));

		ArrayList<Tag<Block<?>>> list = new ArrayList<>(TYPE_TAGS.values());
		Tag<Block<?>>[] tags = list.toArray(new Tag[0]);

		carpenterWorkbench.withTags(CAN_BE_MULTIPART).withTags(tags);

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

	public void afterItemInit() {
		multipartItem = (ItemMultipart) new ItemBuilder(MOD_ID).build(new ItemMultipart("multipart", "catalyst-multipart:item/multipart", item("multipartItem"))).withTags(ItemTags.NOT_IN_CREATIVE_MENU);
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


	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID, "CCC", "CTC", "CCC")
			.addInput('C', "minecraft:cobblestones")
			.addInput('T', WORKBENCH)
			.create("carpenter_workbench", new ItemStack(carpenterWorkbench, 1));
	}


	public void initNamespaces() {
		RecipeNamespace namespace = new RecipeNamespace();
		final RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
		namespace.register("workbench", WORKBENCH);
		Registries.RECIPES.register("catalyst-multipart", namespace);
	}
}
