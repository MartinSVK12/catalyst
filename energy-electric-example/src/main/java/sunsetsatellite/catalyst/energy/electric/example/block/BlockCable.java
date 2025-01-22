package sunsetsatellite.catalyst.energy.electric.example.block;

import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.ConduitCapability;
import sunsetsatellite.catalyst.core.util.IConduitBlock;
import sunsetsatellite.catalyst.core.util.ICustomDescription;
import sunsetsatellite.catalyst.core.util.ISideInteractable;
import sunsetsatellite.catalyst.core.util.network.NetworkComponent;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.energy.electric.api.VoltageTier;
import sunsetsatellite.catalyst.energy.electric.api.WireMaterial;
import sunsetsatellite.catalyst.energy.electric.api.WireProperties;
import sunsetsatellite.catalyst.energy.electric.example.tile.TileEntityCable;


public class BlockCable extends BlockTileEntity implements ICustomDescription, NetworkComponent, IConduitBlock, ISideInteractable {

	public final WireProperties properties;

	public BlockCable(int id, WireProperties properties) {
		super("null", id, Material.metal);
		this.properties = properties;
	}

	public WireProperties getProperties() {
		return properties;
	}

	@Override
	public String getLanguageKey(int meta) {
		String key = properties.isInsulated() ? "tile.catalyst-energy-electric-example.cable." : "tile.catalyst-energy-electric-example.wire.";
		key += properties.getMaterial().getName()+"."+properties.getSize()+"x";
		return key;
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityCable();
	}

	@Override
	public String getDescription(ItemStack stack) {
		return "";
	}

	@Override
	public String getPersistentDescription(ItemStack stack) {
		WireMaterial mat = properties.getMaterial();
		VoltageTier voltage = mat.getMaxVoltage();
		String superconductor = properties.isSuperconductor() ? TextFormatting.MAGENTA+ voltage.name()+" Superconductor\n" : "";
		return superconductor+String.format("%sMax Voltage: %s%dV %s(%s%s%s)\n%sMax Current: %s%dA\n%sVoltage Drop: %s%dV%s/block",
			TextFormatting.LIGHT_GRAY,
			TextFormatting.LIME, voltage.maxVoltage, TextFormatting.LIGHT_GRAY, voltage.textColor, voltage.name(), TextFormatting.LIGHT_GRAY,
			TextFormatting.LIGHT_GRAY,
			TextFormatting.ORANGE, properties.getSize() * mat.getDefaultAmps(),
			TextFormatting.LIGHT_GRAY,
			TextFormatting.RED, mat.getLossPerBlock(), TextFormatting.LIGHT_GRAY
		);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean onBlockRightClicked(World world, int i, int j, int k, EntityPlayer entityplayer, Side side, double xHit, double yHit) {
		TileEntity tile = world.getBlockTileEntity(i,j,k);
		if(tile instanceof TileEntityCable){
			TileEntityCable cable = (TileEntityCable) tile;
			entityplayer.sendMessage("---------------");
			entityplayer.sendMessage(String.format("A: %f",cable.getAverageAmpLoad()));
			entityplayer.sendMessage(String.format("N: %s",cable.energyNet));
			entityplayer.sendMessage("---------------");
		}
		return super.onBlockRightClicked(world, i, j, k, entityplayer, side, xHit, yHit);
	}

	@Override
	public NetworkType getType() {
		return NetworkType.ELECTRIC;
	}

	@Override
	public ConduitCapability getConduitCapability() {
		return ConduitCapability.ELECTRIC;
	}
}
