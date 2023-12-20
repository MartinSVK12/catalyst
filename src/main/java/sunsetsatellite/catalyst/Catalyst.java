package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.GuiChest;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.ContainerChest;
import net.minecraft.core.player.inventory.IInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.IMpGui;
import sunsetsatellite.catalyst.core.util.MpGuiEntry;


public class Catalyst implements ModInitializer {
    public static final String MOD_ID = "catalyst";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Registry<MpGuiEntry> GUIS = new Registry<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Catalyst initialized.");
    }

	public static void displayGui(EntityPlayer player, IInventory inventory, ItemStack stack){
		((IMpGui)player).displayCustomGUI(inventory,stack);
	}


	public static void displayGui(EntityPlayer player, TileEntity tileEntity, String id){
		((IMpGui)player).displayCustomGUI(tileEntity, id);
	}
}
