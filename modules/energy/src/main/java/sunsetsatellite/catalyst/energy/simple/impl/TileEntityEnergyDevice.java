package sunsetsatellite.catalyst.energy.simple.impl;

import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.network.NetworkComponentTile;
import sunsetsatellite.catalyst.core.util.network.NetworkPath;
import sunsetsatellite.catalyst.energy.simple.api.IEnergyContainer;

public abstract class TileEntityEnergyDevice extends TileEntityEnergyBase implements NetworkComponentTile {

	@Override
	public boolean canReceive(@NotNull Direction dir) {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		for (Direction dir : Direction.values()) {
			receiveEnergy(dir, getMaxReceive());
		}
	}

	@Override
	public long receiveEnergy(@NotNull Direction dir, long energy) {
		if (energyNet == null) return 0;
		long energyReceived = 0;

		TileEntity tile = dir.getTileEntity(worldObj, this);
		if (tile instanceof TileEntityEnergyConductor wire) {

			for (NetworkPath path : energyNet.getPathData(wire.getPosition())) {
				long energyReceivedFromPath = 0;
				if (path.target == this || !(path.target instanceof IEnergyContainer dest)) {
					continue;
				}

				if (dest.canProvide(path.targetDirection)) {
					if (canReceive(dir)) {
						long maxThroughput = Long.MAX_VALUE;
						for (NetworkComponentTile component : path.path) {
							if (component instanceof TileEntityEnergyConductor) {
								maxThroughput = Math.min(maxThroughput, ((TileEntityEnergyConductor) component).throughput);
							}
						}
						energyReceivedFromPath = Catalyst.multiMin(energy, maxThroughput, getMaxReceive(), dest.getMaxProvide(), getCapacityRemaining(), dest.getEnergy());
						internalChangeEnergy(energyReceivedFromPath);
						dest.internalChangeEnergy(-energyReceivedFromPath);
					}
				}
				energyReceived += energyReceivedFromPath;
			}
		}
		return energyReceived;
	}
}
