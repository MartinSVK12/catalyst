package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import org.lwjgl.util.vector.Vector3f;
import sunsetsatellite.catalyst.core.util.vector.Vec3f;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

public enum Direction {
	/**
	 * EAST, 5, X
	 */
	X_POS(new Vec3i(1, 0, 0), 5, "EAST", Axis.X, (3 * Math.PI) / 2, 1),
	/**
	 * WEST, 4, X
	 */
	X_NEG(new Vec3i(-1, 0, 0), 4, "WEST", Axis.X, Math.PI / 2, 3),
	/**
	 * UP, 1, Y
	 */
	Y_POS(new Vec3i(0, 1, 0), 1, "UP", Axis.Y, 0.0f, -1),
	/**
	 * DOWN, 0, Y
	 */
	Y_NEG(new Vec3i(0, -1, 0), 0, "DOWN", Axis.Y, 0.0f, -1),
	/**
	 * SOUTH, 3, Z
	 */
	Z_POS(new Vec3i(0, 0, 1), 3, "SOUTH", Axis.Z, Math.PI, 2),
	/**
	 * NORTH, 2, Z
	 */
	Z_NEG(new Vec3i(0, 0, -1), 2, "NORTH", Axis.Z, 0.0f, 0);


	private final Vec3i vec;
	private Direction opposite;
	private final int side;
	private final String name;
	private final Axis axis;
	private final double angle;
	private final int horizontalIndex;

	Direction(Vec3i vec3I, int side, String name, Axis axis, double angle, int horizontalIndex) {
		this.vec = vec3I;
		this.side = side;
		this.name = name;
		this.axis = axis;
		this.angle = angle;
		this.horizontalIndex = horizontalIndex;
	}

	public TileEntity getTileEntity(WorldSource world, TileEntity tile) {
		TilePos pos = tile.tilePos.add(vec.x, vec.y, vec.z);
		//Vec3i pos = new Vec3i(tile.x + vec.x, tile.y + vec.y, tile.z + vec.z);
		return world.getTileEntity(pos);
	}

	public Block<?> getBlock(WorldSource world, TileEntity tile) {
		TilePos pos = tile.tilePos.add(vec.x, vec.y, vec.z);
		return world.getBlockType(pos);
	}

	public Block<?> getBlock(WorldSource world, Vec3i baseVec) {
		TilePos pos = baseVec.pos.add(vec.x, vec.y, vec.z);
		return world.getBlockType(pos);
	}

	public int getBlockMetadata(WorldSource world, TileEntity tile) {
		TilePos pos = tile.tilePos.add(vec.x, vec.y, vec.z);
		return world.getBlockData(pos);
	}

	public int getBlockMetadata(WorldSource world, Vec3i baseVec) {
		TilePos pos = baseVec.pos.add(vec.x, vec.y, vec.z);
		return world.getBlockData(pos);
	}

	public TileEntity getTileEntity(WorldSource world, Vec3i baseVec) {
		TilePos pos = baseVec.pos.add(vec.x, vec.y, vec.z);
		return world.getTileEntity(pos);
	}

	public String getName() {
		return name;
	}

	public Direction getOpposite() {
		return opposite;
	}

	public Vec3i getVec() {
		return vec.copy();
	}

	public static Vec3i[] getVecs() {
		Vec3i[] vecs = new Vec3i[Direction.values().length];
		for (int i = 0; i < Direction.values().length; i++) {
			vecs[i] = Direction.values()[i].getVec();
		}
		return vecs;
	}

	public Axis getAxis() {
		return axis;
	}

	public int getHorizontalIndex() {
		return horizontalIndex;
	}

	public Direction rotate(int amount) {
		if (this == Y_POS || this == Y_NEG) return this;
		Direction[] horizontalDirections = {Z_NEG, X_POS, Z_POS, X_NEG};
		return horizontalDirections[this.getHorizontalIndex() + amount & 3];
	}

	public static Direction getDirectionFromSide(int side) {
		for (Direction dir : values()) {
			if (dir.side == side) {
				return dir;
			}
		}
		return Direction.X_NEG;
	}

	public static Direction getFromName(String name) {
		for (Direction dir : values()) {
			if (dir.name.equalsIgnoreCase(name)) {
				return dir;
			}
		}
		return null;
	}

	/**
	 * Gets minecraft's side number, NOTE: this and .ordinal() aren't the same!
	 *
	 * @return Minecraft's side number.
	 */
	public int getSideNumber() {
		return side;
	}

	public Side getSide() {
		return Side.fromId(side);
	}

	public Vec3f getVecF() {
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
		return switch (this) {
			case X_POS -> Direction.Z_POS;
			case X_NEG -> Direction.Z_NEG;
			case Z_POS -> Direction.X_POS;
			case Z_NEG -> Direction.X_NEG;
			default -> this;
		};
	}

	public static Vector3f[] getVerticesForSide(Direction dir) {
		float min = -0.5f; // Centered at 0,0,0
		float max = 0.5f;

		return switch (dir) {
			case Y_POS -> // TOP
				new Vector3f[]{
					new Vector3f(min, max, min),
					new Vector3f(min, max, max),
					new Vector3f(max, max, max),
					new Vector3f(max, max, min)
				};
			case Y_NEG -> // BOTTOM
				new Vector3f[]{
					new Vector3f(min, min, min),
					new Vector3f(max, min, min),
					new Vector3f(max, min, max),
					new Vector3f(min, min, max)
				};
			case Z_NEG -> // NORTH
				new Vector3f[]{
					new Vector3f(min, min, min),
					new Vector3f(min, max, min),
					new Vector3f(max, max, min),
					new Vector3f(max, min, min)
				};
			case Z_POS -> // SOUTH
				new Vector3f[]{
					new Vector3f(min, min, max),
					new Vector3f(max, min, max),
					new Vector3f(max, max, max),
					new Vector3f(min, max, max)
				};
			case X_NEG -> // WEST
				new Vector3f[]{
					new Vector3f(min, min, min),
					new Vector3f(min, min, max),
					new Vector3f(min, max, max),
					new Vector3f(min, max, min)
				};
			case X_POS -> // EAST
				new Vector3f[]{
					new Vector3f(max, min, min),
					new Vector3f(max, max, min),
					new Vector3f(max, max, max),
					new Vector3f(max, min, max)
				};
			default -> new Vector3f[0];
		};
	}

	/*public Vec3 getMinecraftVec() {
		return Vec3.getTempVec3(vec.x, vec.y, vec.z);
	}*/

	static {
		X_POS.opposite = X_NEG;
		X_NEG.opposite = X_POS;
		Y_NEG.opposite = Y_POS;
		Y_POS.opposite = Y_NEG;
		Z_NEG.opposite = Z_POS;
		Z_POS.opposite = Z_NEG;
	}

}
