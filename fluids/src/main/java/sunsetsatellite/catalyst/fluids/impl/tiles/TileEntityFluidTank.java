package sunsetsatellite.catalyst.fluids.impl.tiles;


import net.minecraft.core.block.entity.TileEntity;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityFluidTank extends TileEntityFluidItemContainer {
    public TileEntityFluidTank(){
        fluidCapacity[0] = 8000;
        transferSpeed = 50;
        connections.replace(Direction.Y_POS, Connection.INPUT);
        connections.replace(Direction.Y_NEG, Connection.OUTPUT);
        acceptedFluids.get(0).addAll(CatalystFluids.FLUIDS.getAllFluids());
    }

    @Override
    public void tick() {
        extractFluids();
        super.tick();
    }

    @Override
    public String getInvName() {
        return "Fluid Tank";
    }

    public void extractFluids(){
        for (Map.Entry<Direction, Connection> e : connections.entrySet()) {
            Direction dir = e.getKey();
            Connection connection = e.getValue();
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if (tile instanceof TileEntityFluidPipe) {
                pressurizePipes((TileEntityFluidPipe) tile, new ArrayList<>());
                moveFluids(dir, (TileEntityFluidPipe) tile);
                ((TileEntityFluidPipe) tile).rememberTicks = 100;
            }
        }
    }

    public void pressurizePipes(TileEntityFluidPipe pipe, ArrayList<HashMap<String,Integer>> already){
        pipe.isPressurized = true;
        for (Direction dir : Direction.values()) {
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if (tile instanceof TileEntityFluidPipe) {
                for (HashMap<String, Integer> V2 : already) {
                    if (V2.get("x") == tile.x && V2.get("y") == tile.y && V2.get("z") == tile.z) {
                        return;
                    }
                }
                HashMap<String,Integer> list = new HashMap<>();
                list.put("x",tile.x);
                list.put("y",tile.y);
                list.put("z",tile.z);
                already.add(list);
                ((TileEntityFluidPipe) tile).isPressurized = true;
                pressurizePipes((TileEntityFluidPipe) tile,already);
            }
        }
    }

    public void unpressurizePipes(TileEntityFluidPipe pipe,ArrayList<HashMap<String,Integer>> already){
        pipe.isPressurized = false;
        for (Direction dir : Direction.values()) {
            TileEntity tile = dir.getTileEntity(worldObj,this);
            if (tile instanceof TileEntityFluidPipe) {
                for (HashMap<String, Integer> V2 : already) {
                    if (V2.get("x") == tile.x && V2.get("y") == tile.y && V2.get("z") == tile.z) {
                        return;
                    }
                }
                HashMap<String,Integer> list = new HashMap<>();
                list.put("x",tile.x);
                list.put("y",tile.y);
                list.put("z",tile.z);
                already.add(list);
                ((TileEntityFluidPipe) tile).isPressurized = false;
                unpressurizePipes((TileEntityFluidPipe) tile,already);
            }
        }
    }
}
