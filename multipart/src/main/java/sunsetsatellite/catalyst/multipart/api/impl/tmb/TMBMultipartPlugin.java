package sunsetsatellite.catalyst.multipart.api.impl.tmb;

import com.mojang.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.ContainerPlayerCreative;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.interfaces.mixins.IKeybinds;
import turing.tmb.TMB;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.TMBEntrypoint;
import turing.tmb.api.VanillaTypes;
import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.runtime.ITMBRuntime;

public class TMBMultipartPlugin implements ITMBPlugin, TMBEntrypoint {

	@Override
	public void registerIngredients(ITMBRuntime runtime) {
		if(((IKeybinds) Minecraft.getMinecraft(this).gameSettings).showMultipartsInTMB().value){
			IIngredientRegistry<ItemStack> registry = runtime.getRegistryForIngredientType(VanillaTypes.ITEM_STACK);
			MultipartType.types.forEach((K, V)->{
				for (ItemStack item : ContainerPlayerCreative.creativeItems) {
					if (item == null) continue;
					if (item.itemID >= 16384) continue;
					if (!Block.getBlock(item.itemID).hasTag(CatalystMultipart.CAN_BE_MULTIPART)) continue;
					if (!Block.getBlock(item.itemID).hasTag(CatalystMultipart.TYPE_TAGS.get(K))) continue;
					ItemStack stack = new ItemStack(CatalystMultipart.multipartItem,1, 0);
					CompoundTag tag = new CompoundTag();
					CompoundTag multipartTag = new CompoundTag();
					multipartTag.putString("Type",K);
					multipartTag.putInt("Block", item.itemID);
					multipartTag.putInt("Meta", item.getMetadata());
					tag.putCompound("Multipart",multipartTag);
					stack.setData(tag);
					registry.registerIngredient(CatalystMultipart.MOD_ID,stack.getDisplayName(), stack);
				}
			});
		}
	}

	@Override
	public void onGatherPlugins(boolean isReload) {
		TMB.LOGGER.info("Loading plugin: "+this.getClass().getSimpleName()+" from "+CatalystMultipart.MOD_ID);
		TMB.registerPlugin(this);
	}
}
