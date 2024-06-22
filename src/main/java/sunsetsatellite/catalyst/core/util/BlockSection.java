package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.util.helper.Side;

public enum BlockSection {
	TOP_LEFT(new Vec2f(0.0,0.8),new Vec2f(0.2,0.1)),
	BOTTOM_LEFT(new Vec2f(0.0,0.0),new Vec2f(0.2,0.2)),
	CENTER_LEFT(new Vec2f(0.0,0.4),new Vec2f(0.2,0.6)),
	TOP_RIGHT(new Vec2f(0.8,0.8),new Vec2f(1,1)),
	BOTTOM_RIGHT(new Vec2f(0.8,0.0),new Vec2f(1,0.2)),
	CENTER_RIGHT(new Vec2f(0.8,0.4),new Vec2f(1,0.6)),
	UPPER_CENTER(new Vec2f(0.4,1),new Vec2f(0.6,0.8)),
	LOWER_CENTER(new Vec2f(0.4,0.0),new Vec2f(0.6,0.2)),
	CENTER(new Vec2f(0.4,0.4),new Vec2f(0.6,0.6)),
	;

	public final Vec2f min;
	public final Vec2f max;

	BlockSection(Vec2f min, Vec2f max) {
		this.min = min;
		this.max = max;
	}

    public static BlockSection getClosestBlockSection(Vec2f vec) {
        double bestDistance = Double.MAX_VALUE;
        BlockSection bestSection = null;
        for (BlockSection section : BlockSection.values()) {
            double minDistance = vec.distanceTo(section.min);
            double maxDistance = vec.distanceTo(section.max);
            double currentMinimumDistance = Math.min(minDistance, maxDistance);
            if (currentMinimumDistance < bestDistance) {
                bestDistance = currentMinimumDistance;
                bestSection = section;
            }
        }
        return bestSection;
    }

	public Direction toDirection(Direction blockSide, Side playerFacing){
		if(blockSide.getAxis().isVertical()){
			switch (this){
				case TOP_RIGHT:
				case BOTTOM_LEFT:
				case BOTTOM_RIGHT:
				case TOP_LEFT:
					return blockSide.getOpposite();
				case CENTER_LEFT:
					return Direction.X_NEG;
				case CENTER_RIGHT:
					return Direction.X_POS;
				case UPPER_CENTER:
					return Direction.Z_POS;
				case LOWER_CENTER:
					return Direction.Z_NEG;
				case CENTER:
					return blockSide;
			}
		} else {
			switch (this) {
				case TOP_RIGHT:
				case BOTTOM_LEFT:
				case BOTTOM_RIGHT:
				case TOP_LEFT:
					return blockSide.getOpposite();
				case CENTER_LEFT:
					switch (playerFacing) {
						case NORTH:
							return Direction.X_NEG;
						case EAST:
							return Direction.Z_NEG;
						case SOUTH:
							return Direction.X_POS;
						case WEST:
							return Direction.Z_POS;
					}
					break;
				case CENTER_RIGHT:
					switch (playerFacing) {
						case NORTH:
							return Direction.X_POS;
						case EAST:
							return Direction.Z_POS;
						case SOUTH:
							return Direction.X_NEG;
						case WEST:
							return Direction.Z_NEG;
					}
					break;
				case UPPER_CENTER:
					return Direction.Y_POS;
				case LOWER_CENTER:
					return Direction.Y_NEG;
				case CENTER:
					return blockSide;
			}
		}
		return null;
	}

}
