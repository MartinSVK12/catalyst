package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.mp.IMpGui;
import sunsetsatellite.catalyst.core.util.mp.MpGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.PacketOpenGui;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = PlayerServer.class,remap = false)
public abstract class PlayerServerMixin extends Player implements IMpGui {

	private PlayerServerMixin(World world) {
		super(world);
	}

	@Shadow
	protected abstract void getNextWindowId();

	@Shadow
	private int currentWindowId;
	@Shadow
	public PacketHandlerServer playerNetServerHandler;
	@Unique
	private final PlayerServer thisAs = (PlayerServer)(Object)this;

	//TODO: change display methods to have xyz argument and stack argument

	@Override
	public void catalyst$displayCustomGUI(Container inventory, ItemStack stack) {
		this.getNextWindowId();
		MpGuiEntry entry = Catalyst.GUIS.getItem(inventory.getNameTranslationKey());
		this.playerNetServerHandler.sendPacket(new PacketOpenGui(this.currentWindowId, inventory.getNameTranslationKey(),stack));
		if(entry.containerClass != null) {
			try {
				this.craftingInventory = (MenuAbstract) entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, inventory);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			this.craftingInventory.containerId = this.currentWindowId;
			this.craftingInventory.addSlotListener(thisAs);
		}

	}

	@Override
	public void catalyst$displayCustomGUI(TileEntity tileEntity, String id) {
		this.getNextWindowId();
		MpGuiEntry entry = Catalyst.GUIS.getItem(id);
		this.playerNetServerHandler.sendPacket(new PacketOpenGui(this.currentWindowId, id, tileEntity.x, tileEntity.y, tileEntity.z));
		if(entry.containerClass != null){
			try {
				this.craftingInventory = (MenuAbstract) entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, tileEntity);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			this.craftingInventory.containerId = this.currentWindowId;
			this.craftingInventory.addSlotListener(thisAs);
		}
	}
}
