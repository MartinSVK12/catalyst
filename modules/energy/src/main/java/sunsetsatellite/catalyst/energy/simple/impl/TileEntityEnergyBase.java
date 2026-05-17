package sunsetsatellite.catalyst.energy.simple.impl;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.network.Network;
import sunsetsatellite.catalyst.core.util.network.NetworkComponentTile;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import sunsetsatellite.catalyst.energy.simple.api.IEnergyContainer;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public abstract class TileEntityEnergyBase extends TileEntity implements IEnergyContainer, NetworkComponentTile {

	protected long energy = 0;
	protected long capacity = 0;

	protected long maxReceive = 0;
	protected long maxProvide = 0;

	public TileEntityEnergyBase() {
	}

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
		return new Vec3i(tilePos);
	}

	@Override
	public boolean isConnected(Direction direction) {
		return direction.getTileEntity(worldObj, this) instanceof TileEntityEnergyConductor;
	}

	@Override
	public void writeAdditionalData(CompoundTag tag) {
		tag.putLong("Energy", energy);
		tag.putLong("Capacity", capacity);
		tag.putLong("MaxReceive", maxReceive);
		tag.putLong("MaxProvide", maxProvide);
	}

	@Override
	public void readAdditionalData(CompoundTag tag) {
		energy = tag.getLong("Energy");
		capacity = tag.getLong("Capacity");
		maxReceive = tag.getLong("MaxReceive");
		maxProvide = tag.getLong("MaxProvide");
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
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
