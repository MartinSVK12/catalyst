package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet100OpenWindow;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.IMpGui;
import sunsetsatellite.catalyst.core.util.MpGuiEntry;
import sunsetsatellite.catalyst.core.util.PacketOpenGui;

import java.lang.reflect.InvocationTargetException;


@Mixin(value = EntityPlayerMP.class,remap = false)
public abstract class EntityPlayerMPMixin extends EntityPlayer implements IMpGui {
	private EntityPlayerMPMixin(World world) {
		super(world);
	}

	@Shadow
	protected abstract void getNextWindowId();

	@Shadow
	private int currentWindowId;
	@Shadow
	public NetServerHandler playerNetServerHandler;
	@Unique
	private EntityPlayerMP thisAs = (EntityPlayerMP)(Object)this;

	//TODO: change display methods to have xyz argument and stack argument

	@Override
	public void displayCustomGUI(IInventory inventory, ItemStack stack) {
		this.getNextWindowId();
		MpGuiEntry entry = Catalyst.GUIS.getItem(inventory.getInvName());
		this.playerNetServerHandler.sendPacket(new PacketOpenGui(this.currentWindowId, inventory.getInvName(),stack));
		if(entry.containerClass != null) {
			try {
				this.craftingInventory = (Container) entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, inventory);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			this.craftingInventory.windowId = this.currentWindowId;
			this.craftingInventory.onContainerInit(thisAs);
		}

	}

	@Override
	public void displayCustomGUI(TileEntity tileEntity, String id) {
		this.getNextWindowId();
		MpGuiEntry entry = Catalyst.GUIS.getItem(id);
		this.playerNetServerHandler.sendPacket(new PacketOpenGui(this.currentWindowId, id, tileEntity.x, tileEntity.y, tileEntity.z));
		if(entry.containerClass != null){
			try {
				this.craftingInventory = (Container)entry.containerClass.getDeclaredConstructors()[0].newInstance(thisAs.inventory, tileEntity);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			this.craftingInventory.windowId = this.currentWindowId;
			this.craftingInventory.onContainerInit(thisAs);
		}
	}
}
