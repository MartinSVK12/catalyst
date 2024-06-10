package sunsetsatellite.catalyst.fluids.impl.tiles;


import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMultiFluidTank extends TileEntityMassFluidItemContainer {
    public TileEntityMultiFluidTank(){
        fluidCapacity = 16000;
        transferSpeed = 50;
        connections.replace(Direction.Y_POS, Connection.INPUT);
        connections.replace(Direction.Y_NEG, Connection.OUTPUT);
        acceptedFluids.addAll(CatalystFluids.FLUIDS.getAllFluids());
    }

    @Override
    public void tick() {
        extractFluids();
        super.tick();
    }

    @Override
    public String getInvName() {
        return "Multi Fluid Tank";
    }

    public void extractFluids(){
        for (Map.Entry<Direction, Connection> e : connections.entrySet()) {
            Direction dir = e.getKey();
            Connection connection = e.getValue();
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if (tile instanceof TileEntityFluidPipe) {
                moveFluids(dir, (TileEntityFluidPipe) tile);
                ((TileEntityFluidPipe) tile).rememberTicks = 100;
            }
        }
    }
}
