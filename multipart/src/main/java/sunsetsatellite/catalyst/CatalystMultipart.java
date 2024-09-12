package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.MpGuiEntry;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.block.BlockCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.block.BlockMultipart;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityMultipart;
import sunsetsatellite.catalyst.multipart.block.model.MultipartBlockModelBuilder;
import sunsetsatellite.catalyst.multipart.container.ContainerCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.gui.GuiCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.item.ItemMultipart;
import sunsetsatellite.catalyst.multipart.item.model.ItemModelMultipart;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;
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

import static net.minecraft.core.block.Block.*;


public class CatalystMultipart implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "catalyst-multipart";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final int blockIdStart = 3256;
	private static final int itemIdStart = 19640;

	public static final TomlConfigHandler config;

	public static final Tag<Block> CAN_BE_MULTIPART = Tag.of("can_be_multipart");
	public static final HashMap<String,Tag<Block>> TYPE_TAGS = new HashMap<>();

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

		config = new TomlConfigHandler(MOD_ID, new Toml("Catalyst: Multipart configuration file."),false);

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

	public static final BlockMultipart multipartBlock = new BlockBuilder(MOD_ID)
		.setBlockSound(BlockSounds.STONE)
		.setHardness(0.5f)
		.setResistance(20)
		.setBlockModel(block ->
			new MultipartBlockModelBuilder(MOD_ID)
				.setBlockModel("cover.json")
				.build(block))
		.setTextures("minecraft:block/stone")
		.setTags(BlockTags.NOT_IN_CREATIVE_MENU)
		.build(new BlockMultipart("multipart",config.getInt("BlockIDs.multipartBlock")));

	public static final ItemMultipart multipartItem = new ItemBuilder(MOD_ID)
		.setItemModel((item) -> new ItemModelMultipart(item, MOD_ID))
		.setTags(ItemTags.NOT_IN_CREATIVE_MENU)
		.build(new ItemMultipart("multipart", config.getInt("ItemIDs.multipartItem")));

	public static final Block carpenterWorkbench = new BlockBuilder(MOD_ID)
		.setTopTexture("catalyst-multipart:block/carpenter_workbench_top")
		.setBottomTexture("catalyst-multipart:block/carpenter_workbench_bottom")
		.setSideTextures("catalyst-multipart:block/carpenter_workbench_side")
		.setSouthTexture("catalyst-multipart:block/carpenter_workbench_front")
		.setHardness(0.5f)
		.setResistance(20)
		.build(new BlockCarpenterWorkbench("workbench.carpenter",config.getInt("BlockIDs.carpenterWorkbench")));

    @Override
    public void onInitialize() {

		EntityHelper.createTileEntity(TileEntityMultipart.class, "Multipart");
		EntityHelper.createTileEntity(TileEntityCarpenterWorkbench.class, "Carpenter Workbench");

		Catalyst.GUIS.register("Carpenter Workbench", new MpGuiEntry(TileEntityCarpenterWorkbench.class, GuiCarpenterWorkbench.class, ContainerCarpenterWorkbench.class));

		ItemToolPickaxe.miningLevels.put(multipartBlock,1);
		ItemToolPickaxe.miningLevels.put(carpenterWorkbench,1);

		MultipartType.types.forEach((K,V)-> TYPE_TAGS.put(K,Tag.of(K)));

		ArrayList<Tag<Block>> list = new ArrayList<>(TYPE_TAGS.values());
		Tag<Block>[] tags = list.toArray(new Tag[0]);

		carpenterWorkbench.withTags(CAN_BE_MULTIPART).withTags(tags);

		stone.withTags(CAN_BE_MULTIPART).withTags(tags);
		basalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		limestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		granite.withTags(CAN_BE_MULTIPART).withTags(tags);
		marble.withTags(CAN_BE_MULTIPART).withTags(tags);
		slate.withTags(CAN_BE_MULTIPART).withTags(tags);
		permafrost.withTags(CAN_BE_MULTIPART).withTags(tags);
		cobbleStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		cobbleStoneMossy.withTags(CAN_BE_MULTIPART).withTags(tags);
		cobbleBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		cobbleLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		cobbleGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		cobblePermafrost.withTags(CAN_BE_MULTIPART).withTags(tags);
		stonePolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		granitePolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		limestonePolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		basaltPolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		slatePolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		permafrostPolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		pillarMarble.withTags(CAN_BE_MULTIPART).withTags(tags);
		capstoneMarble.withTags(CAN_BE_MULTIPART).withTags(tags);
		sandstone.withTags(CAN_BE_MULTIPART).withTags(tags);
		stoneCarved.withTags(CAN_BE_MULTIPART).withTags(tags);
		graniteCarved.withTags(CAN_BE_MULTIPART).withTags(tags);
		limestoneCarved.withTags(CAN_BE_MULTIPART).withTags(tags);
		basaltCarved.withTags(CAN_BE_MULTIPART).withTags(tags);
		permafrostCarved.withTags(CAN_BE_MULTIPART).withTags(tags);
		planksOak.withTags(CAN_BE_MULTIPART).withTags(tags);
		planksOakPainted.withTags(CAN_BE_MULTIPART).withTags(tags);
		bookshelfPlanksOak.withTags(CAN_BE_MULTIPART).withTags(tags);
		wool.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickClay.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickStonePolished.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickStonePolishedMossy.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickSandstone.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickGold.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickLapis.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickMarble.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickSlate.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickPermafrost.withTags(CAN_BE_MULTIPART).withTags(tags);
		brickIron.withTags(CAN_BE_MULTIPART).withTags(tags);
		obsidian.withTags(CAN_BE_MULTIPART).withTags(tags);
		glass.withTags(CAN_BE_MULTIPART).withTags(tags);
		glassTinted.withTags(CAN_BE_MULTIPART).withTags(tags);
		grass.withTags(CAN_BE_MULTIPART).withTags(tags);
		grassRetro.withTags(CAN_BE_MULTIPART).withTags(tags);
		grassScorched.withTags(CAN_BE_MULTIPART).withTags(tags);
		pathDirt.withTags(CAN_BE_MULTIPART).withTags(tags);
		dirt.withTags(CAN_BE_MULTIPART).withTags(tags);
		dirtScorched.withTags(CAN_BE_MULTIPART).withTags(tags);
		dirtScorchedRich.withTags(CAN_BE_MULTIPART).withTags(tags);
		mud.withTags(CAN_BE_MULTIPART).withTags(tags);
		mudBaked.withTags(CAN_BE_MULTIPART).withTags(tags);
		spongeDry.withTags(CAN_BE_MULTIPART).withTags(tags);
		spongeWet.withTags(CAN_BE_MULTIPART).withTags(tags);
		mossStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		mossBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		mossLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		mossGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		sand.withTags(CAN_BE_MULTIPART).withTags(tags);
		gravel.withTags(CAN_BE_MULTIPART).withTags(tags);
		bedrock.withTags(CAN_BE_MULTIPART).withTags(tags);
		fluidWaterFlowing.withTags(CAN_BE_MULTIPART).withTags(tags);
		fluidWaterStill.withTags(CAN_BE_MULTIPART).withTags(tags);
		fluidLavaFlowing.withTags(CAN_BE_MULTIPART).withTags(tags);
		fluidLavaStill.withTags(CAN_BE_MULTIPART).withTags(tags);
		logOak.withTags(CAN_BE_MULTIPART).withTags(tags);
		logPine.withTags(CAN_BE_MULTIPART).withTags(tags);
		logBirch.withTags(CAN_BE_MULTIPART).withTags(tags);
		logCherry.withTags(CAN_BE_MULTIPART).withTags(tags);
		logEucalyptus.withTags(CAN_BE_MULTIPART).withTags(tags);
		logOakMossy.withTags(CAN_BE_MULTIPART).withTags(tags);
		logThorn.withTags(CAN_BE_MULTIPART).withTags(tags);
		logPalm.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesOak.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesOakRetro.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesPine.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesBirch.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesCherry.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesEucalyptus.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesShrub.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesCherryFlowering.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesCacao.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesThorn.withTags(CAN_BE_MULTIPART).withTags(tags);
		leavesPalm.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreCoalStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreCoalBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreCoalLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreCoalGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreIronStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreIronBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreIronLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreIronGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreGoldStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreGoldBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreGoldLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreGoldGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreLapisStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreLapisBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreLapisLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreLapisGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneGlowingStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneGlowingBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneGlowingLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreRedstoneGlowingGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreDiamondStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreDiamondBasalt.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreDiamondLimestone.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreDiamondGranite.withTags(CAN_BE_MULTIPART).withTags(tags);
		oreNethercoalNetherrack.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockCoal.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockIron.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockGold.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockLapis.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockRedstone.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockDiamond.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockNetherCoal.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockSteel.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockQuartz.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockOlivine.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockCharcoal.withTags(CAN_BE_MULTIPART).withTags(tags);
		motionsensorIdle.withTags(CAN_BE_MULTIPART).withTags(tags);
		motionsensorActive.withTags(CAN_BE_MULTIPART).withTags(tags);
		pistonBase.withTags(CAN_BE_MULTIPART).withTags(tags);
		pistonBaseSticky.withTags(CAN_BE_MULTIPART).withTags(tags);
		noteblock.withTags(CAN_BE_MULTIPART).withTags(tags);
		dispenserCobbleStone.withTags(CAN_BE_MULTIPART).withTags(tags);
		tnt.withTags(CAN_BE_MULTIPART).withTags(tags);
		mesh.withTags(CAN_BE_MULTIPART).withTags(tags);
		meshGold.withTags(CAN_BE_MULTIPART).withTags(tags);
		mobspawner.withTags(CAN_BE_MULTIPART).withTags(tags);
		mobspawnerDeactivated.withTags(CAN_BE_MULTIPART).withTags(tags);
		workbench.withTags(CAN_BE_MULTIPART).withTags(tags);
		furnaceStoneIdle.withTags(CAN_BE_MULTIPART).withTags(tags);
		furnaceStoneActive.withTags(CAN_BE_MULTIPART).withTags(tags);
		furnaceBlastIdle.withTags(CAN_BE_MULTIPART).withTags(tags);
		furnaceBlastActive.withTags(CAN_BE_MULTIPART).withTags(tags);
		trommelIdle.withTags(CAN_BE_MULTIPART).withTags(tags);
		trommelActive.withTags(CAN_BE_MULTIPART).withTags(tags);
		chestLegacy.withTags(CAN_BE_MULTIPART).withTags(tags);
		chestLegacyPainted.withTags(CAN_BE_MULTIPART).withTags(tags);
		chestPlanksOak.withTags(CAN_BE_MULTIPART).withTags(tags);
		chestPlanksOakPainted.withTags(CAN_BE_MULTIPART).withTags(tags);
		farmlandDirt.withTags(CAN_BE_MULTIPART).withTags(tags);
		layerSnow.withTags(CAN_BE_MULTIPART).withTags(tags);
		layerLeavesOak.withTags(CAN_BE_MULTIPART).withTags(tags);
		layerSlate.withTags(CAN_BE_MULTIPART).withTags(tags);
		ice.withTags(CAN_BE_MULTIPART).withTags(tags);
		permaice.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockSnow.withTags(CAN_BE_MULTIPART).withTags(tags);
		cactus.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockClay.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockSugarcane.withTags(CAN_BE_MULTIPART).withTags(tags);
		blockSugarcaneBaked.withTags(CAN_BE_MULTIPART).withTags(tags);
		jukebox.withTags(CAN_BE_MULTIPART).withTags(tags);
		pumpkin.withTags(CAN_BE_MULTIPART).withTags(tags);
		pumpkinCarvedIdle.withTags(CAN_BE_MULTIPART).withTags(tags);
		pumpkinCarvedActive.withTags(CAN_BE_MULTIPART).withTags(tags);
		netherrack.withTags(CAN_BE_MULTIPART).withTags(tags);
		netherrackIgneous.withTags(CAN_BE_MULTIPART).withTags(tags);
		soulsand.withTags(CAN_BE_MULTIPART).withTags(tags);
		glowstone.withTags(CAN_BE_MULTIPART).withTags(tags);
		portalNether.withTags(CAN_BE_MULTIPART).withTags(tags);
		portalParadise.withTags(CAN_BE_MULTIPART).withTags(tags);
		cake.withTags(CAN_BE_MULTIPART).withTags(tags);
		pumpkinPie.withTags(CAN_BE_MULTIPART).withTags(tags);
		lampIdle.withTags(CAN_BE_MULTIPART).withTags(tags);
		lampActive.withTags(CAN_BE_MULTIPART).withTags(tags);
		basket.withTags(CAN_BE_MULTIPART).withTags(tags);
		paperWall.withTags(CAN_BE_MULTIPART).withTags(tags);

		saplingOak.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingOakRetro.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingPine.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingBirch.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingCherry.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingEucalyptus.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingShrub.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingCacao.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingThorn.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		saplingPalm.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		tallgrass.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		tallgrassFern.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		deadbush.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		spinifex.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		algae.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		flowerYellow.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		flowerRed.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		flowerPink.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		flowerPurple.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		flowerLightBlue.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		flowerOrange.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		mushroomBrown.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));
		mushroomRed.withTags(CAN_BE_MULTIPART).withTags(TYPE_TAGS.get("foil"));

        LOGGER.info("Catalyst: Multipart initialized.");
    }

	public static Tag<Block>[] getAllMultipartTags(){
		ArrayList<Tag<Block>> list = new ArrayList<>(TYPE_TAGS.values());
		return list.toArray(new Tag[0]);
	}

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID, "CCC", "CTC", "CCC")
			.addInput('C', "minecraft:cobblestones")
			.addInput('T', workbench)
			.create("carpenter_workbench", new ItemStack(carpenterWorkbench, 1));
	}

	@Override
	public void initNamespaces() {
		RecipeNamespace namespace = new RecipeNamespace();
		final RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Block.workbench)));
		namespace.register("workbench", WORKBENCH);
		Registries.RECIPES.register("catalyst-multipart", namespace);
	}
}
