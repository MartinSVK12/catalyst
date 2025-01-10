package sunsetsatellite.catalyst.energy.simple.test.tile;

import net.minecraft.client.entity.fx.EntityFlameFX;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.energy.simple.impl.TileEntityEnergyDevice;

public class TileEntitySimpleMachine extends TileEntityEnergyDevice {
	public TileEntitySimpleMachine() {
		capacity = 1024;
		maxReceive = 8;
	}

	@Override
	public void tick() {
		super.tick();
		if(energy >= 2){
			internalRemoveEnergy(2);
			Catalyst.spawnParticle(new EntityFlameFX(worldObj,x+Math.random(),y,z+Math.random(),0,0.1,0, EntityFlameFX.Type.ORANGE));
		}
	}
}
