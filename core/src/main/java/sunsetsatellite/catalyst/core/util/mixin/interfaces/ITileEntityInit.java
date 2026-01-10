package sunsetsatellite.catalyst.core.util.mixin.interfaces;

import net.minecraft.core.block.Block;

public interface ITileEntityInit {
	void init(Block<?> block);

	default boolean isInitialized() {
		return false;
	}

	default void setInitialized() {

	}
}
