package sunsetsatellite.catalyst.energy.electric.example.tile;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.CatalystEnergy;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.tile.feature.ItemContainerFeature;
import sunsetsatellite.catalyst.energy.electric.example.item.ItemBattery;
import sunsetsatellite.catalyst.energy.electric.base.TileEntityElectricStorage;
import sunsetsatellite.catalyst.energy.electric.example.block.BlockElectric;

import java.util.Random;

public class TileEntitySimpleElectricBatteryBox extends TileEntityElectricStorage implements IInventory {

	public ItemContainerFeature inv;

	@Override
	public void init(Block block) {
		((ItemContainerFeature) createAndAddFeature(CatalystEnergy.ITEM_CONTAINER_FEATURE)).setSize(4);
		super.init(block);
		inv = (ItemContainerFeature) getFeature(CatalystEnergy.ITEM_CONTAINER_FEATURE);
		maxAmpsIn = 8;
		maxAmpsOut = 4;
		maxVoltageIn = getTier((BlockElectric) block).maxVoltage;
		maxVoltageOut = getTier((BlockElectric) block).maxVoltage;
	}

	@Override
	public boolean canProvide(@NotNull Direction dir) {
		int meta = getMovedData();
		Direction outputDir = Direction.getDirectionFromSide(meta);
		return outputDir == dir;
	}

	@Override
	public void onOvervoltage(long voltage) {

	}

	@Override
	public boolean canReceive(@NotNull Direction dir) {
		int meta = getMovedData();
		Direction outputDir = Direction.getDirectionFromSide(meta);
		return outputDir != dir;
	}

	@Override
	public void tick() {
		super.tick();
	}

	public int getAmountOfBatteriesInstalled(){
		int n = 0;
		for (ItemStack stack : inv.itemContents) {
			if(stack != null && stack.getItem() instanceof ItemBattery){
				ItemBattery batt = (ItemBattery) stack.getItem();
				if (batt.getTier().ordinal() <= getTier().ordinal()) {
					n++;
				}
			}
		}
		return n;
	}

	@Override
	public long getEnergy() {
		long e = 0;
		for (ItemStack stack : inv.itemContents) {
			if (stack != null && stack.getItem() instanceof ItemBattery) {
				ItemBattery batt = (ItemBattery) stack.getItem();
				if (batt.getTier().ordinal() <= getTier().ordinal()) {
					e += batt.getEnergy(stack);
				}
			}
		}
		return e;
	}

	@Override
	public long getCapacity() {
		long c = 0;
		for (ItemStack stack : inv.itemContents) {
			if(stack != null && stack.getItem() instanceof ItemBattery){
				ItemBattery batt = (ItemBattery) stack.getItem();
				if (batt.getTier().ordinal() <= getTier().ordinal()) {
					c += batt.getCapacity(stack);
				}
			}
		}
		return c;
	}

	@Override
	public long internalChangeEnergy(long difference) {
		long actualDifference = 0;
		if(getAmountOfBatteriesInstalled() <= 0){
			return 0;
		} else {
			averageEnergyTransfer.increment(worldObj,difference);
			long abs = Math.abs(difference);
			long split = abs / getAmountOfBatteriesInstalled();
			long remainder = abs % getAmountOfBatteriesInstalled();
			if(difference < 0){
				boolean first = true;
				for (ItemStack stack : inv.itemContents) {
					if (stack == null || !(stack.getItem() instanceof ItemBattery)) {
						continue;
					}
					ItemBattery batt = (ItemBattery) stack.getItem();
					if (batt.getTier().ordinal() > getTier().ordinal()) {
						continue;
					}

					actualDifference = -batt.discharge(stack,split);
					if(first){
						actualDifference -= batt.discharge(stack,remainder);
						first = false;
					}
				}
			} else {
				boolean first = true;
				for (ItemStack stack : inv.itemContents) {
					if (stack == null || !(stack.getItem() instanceof ItemBattery)) {
						continue;
					}
					ItemBattery batt = (ItemBattery) stack.getItem();
					if (batt.getTier().ordinal() > getTier().ordinal()) {
						continue;
					}

					actualDifference = batt.charge(stack,split);
					if(first){
						actualDifference += batt.charge(stack,remainder);
						first = false;
					}
				}
			}
		}

		return actualDifference;
	}

	//item container delegates
	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inv.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		inv.setInventorySlotContents(i, itemStack);
	}

	@Override
	public String getInvName() {
		return inv.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		inv.onInventoryChanged();
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return inv.canInteractWith(entityPlayer);
	}

	@Override
	public void sortInventory() {
		inv.sortInventory();
	}


}
