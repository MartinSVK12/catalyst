package sunsetsatellite.catalyst.energy.simple.impl;

import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Vec3i;
import sunsetsatellite.catalyst.core.util.network.Network;
import sunsetsatellite.catalyst.core.util.network.NetworkComponentTile;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.energy.simple.api.IEnergyContainer;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public abstract class TileEntityEnergyBase extends TileEntity implements IEnergyContainer, NetworkComponentTile {

	protected long energy = 0;
	protected long capacity = 0;

	protected long maxReceive = 0;
	protected long maxProvide = 0;

	public TileEntityEnergyBase() {}

	//IEnergyContainer
	@Override
	public long getEnergy() {
		return energy;
	}

	@Override
	public long getCapacity() {
		return capacity;
	}

	@Override
	public long getMaxReceive() {
		return maxReceive;
	}

	@Override
	public long getMaxProvide() {
		return maxProvide;
	}

	@Override
	public long internalChangeEnergy(long difference) {
		energy += difference;
		return difference;
	}

	//NetworkComponent
	public Network energyNet;

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}

	@Override
	public Vec3i getPosition() {
		return new Vec3i(x,y,z);
	}

	@Override
	public boolean isConnected(Direction direction) {
		return direction.getTileEntity(worldObj,this) instanceof TileEntityEnergyConductor;
	}

	@Override
	public void networkChanged(Network network) {
		this.energyNet = network;
	}

	@Override
	public void removedFromNetwork(Network network) {
		this.energyNet = null;
	}
}
