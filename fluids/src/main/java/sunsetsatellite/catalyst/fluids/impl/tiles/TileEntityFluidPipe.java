package sunsetsatellite.catalyst.fluids.impl.tiles;


import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.HashMap;

public class TileEntityFluidPipe extends TileEntityFluidContainer{
    public boolean isPressurized = false;
    public TileEntityFluidContainer last = null;

    public float size = 0.5f;

    public int rememberTicks = 0;
    public int maxRememberTicks = 100;

    public TileEntityFluidPipe(){
        fluidCapacity[0] = 2000;
        transferSpeed = 20;
        for (Direction dir : Direction.values()) {
            fluidConnections.put(dir, Connection.BOTH);
            activeFluidSlots.put(dir,0);
        }
        acceptedFluids.get(0).addAll(CatalystFluids.FLUIDS.getAllFluids());

    }

    @Override
    public String getInvName() {
        return "Fluid Pipe";
    }

    @Override
    public void tick() {
        super.tick();
        rememberTicks++;
        if(rememberTicks >= maxRememberTicks){
            rememberTicks = 0;
            last = null;
        }
        HashMap<Direction, TileEntity> neighbors = new HashMap<>();
        for (Direction dir : Direction.values()) {
            neighbors.put(dir,dir.getTileEntity(worldObj,this));
        }
        neighbors.forEach((side, tile) -> {
            if (tile instanceof TileEntityFluidPipe && !tile.equals(last)) {
                TileEntityFluidPipe inv = (TileEntityFluidPipe) tile;
                Integer activeSlot = inv.activeFluidSlots.get(side.getOpposite());
                FluidStack intFluid = getFluidInSlot(0);
                FluidStack extFluid = inv.getFluidInSlot(activeSlot);
                if (intFluid != null && extFluid == null) {
                    last = (TileEntityFluidPipe) tile;
                    ((TileEntityFluidPipe) tile).last = this;
                    give(side);
                } else if (intFluid == null && extFluid != null) {
                    last = (TileEntityFluidPipe) tile;
                    ((TileEntityFluidPipe) tile).last = this;
                    take(extFluid,side);
                } else if (intFluid != null) { //if both internal and external aren't null
                    last = (TileEntityFluidPipe) tile;
                    ((TileEntityFluidPipe) tile).last = this;
                    if (intFluid.amount < extFluid.amount) {
                        take(extFluid,side);
                    } else {
                        give(side);
                    }
                }
            }
        });
    }
}
