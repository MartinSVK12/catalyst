package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.structure.EditableStructure;
import net.minecraft.core.world.structure.SavedStructure;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import sunsetsatellite.catalyst.core.util.BlockInstance;

import java.util.*;

/**
 * Wrapper for vanilla BTA! structure format.
 */
public record CatalystStructure(Structure structure) implements net.minecraft.core.world.structure.Structure {

	@Override
	public @NotNull Vector3ic getSize() {
		return structure.getSize().joml();
	}

	@Override
	public @NotNull Vector3ic getOrigin() {
		return new Vector3i(0);
	}

	@Override
	public @NotNull Block<?> getBlockType(@NotNull Vector3ic pos) {
		Optional<BlockInstance> first = structure.getBlocks().stream().filter(bi -> bi.pos.joml().equals(pos)).findFirst();
		return first.isPresent() ? first.get().block : Blocks.AIR;
	}

	@Override
	public int getBlockData(@NotNull Vector3ic pos) {
		Optional<BlockInstance> first = structure.getBlocks().stream().filter(bi -> bi.pos.joml().equals(pos)).findFirst();
		return first.map(bi -> bi.meta).orElse(0);
	}

	public SavedStructure saved() {
		SavedStructure s = new SavedStructure(getSize(), getOrigin());
		structure.getBlocks().forEach((bi) -> {
			s.setBlockType(bi.pos.joml(), bi.block);
			s.setBlockData(bi.pos.joml(), bi.meta);
		});
		return s;
	}

	public EditableStructure editable() {
		return new EditableStructure(this);
	}
}
