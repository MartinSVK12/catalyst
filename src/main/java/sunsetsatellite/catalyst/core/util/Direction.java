package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.WorldSource;

public enum Direction {
	/**EAST, 5, X*/
    X_POS (new Vec3i(1,0,0),5,"EAST", Axis.X, (3*Math.PI)/2),
	/**WEST, 4, X*/
	X_NEG (new Vec3i(-1,0,0),4,"WEST", Axis.X, Math.PI/2),
	/**UP, 1, Y*/
	Y_POS (new Vec3i(0,1,0),1,"UP", Axis.Y, 0.0f),
	/**DOWN, 0, Y*/
	Y_NEG (new Vec3i(0,-1,0),0,"DOWN", Axis.Y, 0.0f),
	/**SOUTH, 3, Z*/
	Z_POS (new Vec3i(0,0,1),3,"SOUTH", Axis.Z, Math.PI),
	/**NORTH, 2, Z*/
	Z_NEG (new Vec3i(0,0,-1),2,"NORTH", Axis.Z, 0.0f);


	private final Vec3i vec;
    private Direction opposite;
    private final int side;
    private final String name;
	private final Axis axis;
	private final double angle;

    Direction(Vec3i vec3I, int side, String name, Axis axis, double angle) {
        this.vec = vec3I;
        this.side = side;
        this.name = name;
        this.axis = axis;
		this.angle = angle;
	}

    public TileEntity getTileEntity(WorldSource world, TileEntity tile){
        Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
        return world.getBlockTileEntity(pos.x,pos.y,pos.z);
    }

	public Block getBlock(WorldSource world, TileEntity tile){
		Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
		return world.getBlock(pos.x,pos.y,pos.z);
	}

	public Block getBlock(WorldSource world, Vec3i baseVec){
		Vec3i pos = new Vec3i(baseVec.x + vec.x, baseVec.y + vec.y, baseVec.z + vec.z);
		return world.getBlock(pos.x,pos.y,pos.z);
	}

    public TileEntity getTileEntity(WorldSource world, Vec3i baseVec){
        Vec3i pos = new Vec3i(baseVec.x + vec.x, baseVec.y + vec.y, baseVec.z + vec.z);
        return world.getBlockTileEntity(pos.x,pos.y,pos.z);
    }

    public String getName() {
        return name;
    }

    public Direction getOpposite(){
        return opposite;
    }

    public Vec3i getVec() {
        return vec.copy();
    }

	public Axis getAxis() {
		return axis;
	}

	public static Direction getDirectionFromSide(int side){
        for (Direction dir : values()) {
            if(dir.side == side){
                return dir;
            }
        }
        return Direction.X_NEG;
    }

    public static Direction getFromName(String name){
        for (Direction dir : values()) {
            if(dir.name.equalsIgnoreCase(name)){
                return dir;
            }
        }
        return null;
    }

    public Direction rotate(int amount){
        if(this == Y_POS || this == Y_NEG) return this;
        return getDirectionFromSide(net.minecraft.core.util.helper.Direction.getDirectionById(this.side).rotate(amount).getId());
    }

    /**
     * Gets minecraft's side number, NOTE: this and .ordinal() aren't the same!
     * @return Minecraft's side number.
     */
    public int getSideNumber() {
        return side;
    }

	public Side getSide(){
		return Side.getSideById(side);
	}

    public Vec3f getVecF(){
        return new Vec3f(vec.x, vec.y, vec.z);
    }

	/**
	 * @return Angle in radians from North for horizontal directions, vertical directions return 0
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @return Z direction if provided a X direction or X direction if provided Z direction
	 */
	public Direction shiftAxis() {
		switch (this){
			case X_POS:
				return Direction.Z_POS;
			case X_NEG:
				return Direction.Z_NEG;
			case Z_POS:
				return Direction.X_POS;
			case Z_NEG:
				return Direction.X_NEG;
		}
		return this;
	}

	public Vec3d getMinecraftVec(){
        return Vec3d.createVectorHelper(vec.x, vec.y, vec.z);
    }

    static {
        X_POS.opposite = X_NEG;
        X_NEG.opposite = X_POS;
        Y_NEG.opposite = Y_POS;
        Y_POS.opposite = Y_NEG;
        Z_NEG.opposite = Z_POS;
        Z_POS.opposite = Z_NEG;
    }

}
