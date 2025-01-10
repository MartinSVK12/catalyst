package sunsetsatellite.catalyst.energy.electric.base;

import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.network.NetworkComponentTile;
import sunsetsatellite.catalyst.core.util.network.NetworkPath;
import sunsetsatellite.catalyst.energy.electric.api.IElectric;



public abstract class TileEntityElectricDevice extends TileEntityElectricBase implements NetworkComponentTile {

	@Override
	public boolean canReceive(@NotNull Direction dir) {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		//reset counters
		ampsUsing = 0;
		averageAmpLoad.increment(worldObj,0);
		averageEnergyTransfer.increment(worldObj,0);
		//try to pull max allowed current from any connected side
		for (Direction dir : Direction.values()) {
			TileEntity tile = dir.getTileEntity(worldObj,this);
			if(tile instanceof TileEntityElectricConductor) {
				TileEntityElectricConductor wire = (TileEntityElectricConductor) tile;
				receiveEnergy(dir,getMaxInputAmperage());
			}
		}
	}

	@Override
	public long receiveEnergy(@NotNull Direction dir, long amperage) {
		if(amperage > getMaxInputAmperage()){
			return 0;
		}
		long remainingCapacity = getCapacityRemaining();
		long willUseAmps = 0;
		TileEntity tile = dir.getTileEntity(worldObj,this);
		if(tile instanceof TileEntityElectricConductor) {
			TileEntityElectricConductor wire = (TileEntityElectricConductor) tile;

			//for every known path
			for (NetworkPath path : energyNet.getPathData(wire.getPosition())) {
				long pathLoss = 0;
				//ignore itself or non-electric components in the path
				if(path.target == this || !(path.target instanceof IElectric)){
					continue;
				}
				IElectric dest = (IElectric) path.target;

				//receive/provide check
				if(dest.canProvide(path.targetDirection.getOpposite())) {
					if (canReceive(dir)) {
						//get max voltage from destination
						//limit amps to maximum available from dest
						long voltage = dest.getMaxOutputVoltage();
						amperage = Math.min(amperage, (dest.getMaxOutputAmperage() - dest.getAmpsCurrentlyUsed()));
						//calculate path loss
						for (NetworkComponentTile component : path.path) {
							if(component instanceof TileEntityElectricConductor){
								pathLoss += ((TileEntityElectricConductor) component).getProperties().getMaterial().getLossPerBlock();
							}
						}
						if(pathLoss >= voltage){
							//avoid paths where all energy is lost
							continue;
						}
						//voltage drop
						long pathVoltage = voltage - pathLoss;
						boolean pathBroken = false;
						//handle wires with insufficient voltage rating
						for (NetworkComponentTile pathTile : path.path) {
							if(pathTile instanceof TileEntityElectricConductor){
								TileEntityElectricConductor pathWire = (TileEntityElectricConductor) pathTile;
								if(pathWire.getVoltageRating() < voltage){
									pathWire.onOvervoltage(voltage);
									pathBroken = true;
									pathVoltage = Math.min(pathWire.getVoltageRating(), pathVoltage);
									break;
								}
							}
						}
						if(pathBroken) continue;

						if(pathVoltage > 0){
							//handle device over-voltage
							if(pathVoltage > getMaxInputVoltage()){
								onOvervoltage(pathVoltage);
								return Math.max(amperage, getMaxInputAmperage() - ampsUsing); //short circuit amperage
							}
							if(remainingCapacity >= pathVoltage){
								//calculate real current draw
								willUseAmps = Math.min(remainingCapacity / pathVoltage, Math.min(amperage, getMaxInputAmperage() - ampsUsing));
								if(willUseAmps > 0){
									long willUseEnergy = pathVoltage * willUseAmps;
									if(dest.getEnergy() >= willUseEnergy){

										//set current in wires
										for (NetworkComponentTile pathTile : path.path) {
											if (pathTile instanceof TileEntityElectricConductor) {
												TileEntityElectricConductor pathWire = (TileEntityElectricConductor) pathTile;
												long voltageTraveled = voltage;
												voltageTraveled -= pathWire.getProperties().getMaterial().getLossPerBlock();
												if (voltageTraveled <= 0) break;
												pathWire.incrementAmperage(willUseAmps);
											}
										}

										//finish energy transfer
										addAmpsToUse(willUseAmps);
										//dest.addAmpsToUse(willUseAmps);
										internalAddEnergy(willUseEnergy);
										dest.internalRemoveEnergy(willUseEnergy);
									}
								}
							}
						}
					}
				}
			}
		}
		return willUseAmps; //return amps used
	}
}
