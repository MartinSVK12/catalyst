package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Fluid {

	@NotNull
	public final NamespaceID id;

	@NotNull
	public final NamespaceID stateId;

	@NotNull
	public final String translationKey;

	@NotNull
	public final List<Block<?>> blocks = new ArrayList<>();

	@NotNull
	public static final Map<NamespaceID, Fluid> fluidMap = new LinkedHashMap<>();

	/*public Fluid(@NotNull NamespaceID id, @NotNull String translationKey) {
		this.id = id;
		this.translationKey = translationKey;

		if (fluidMap.containsKey(this.id)){
			throw new IllegalArgumentException("Fluid id '" + id + "' is already used by '" + fluidMap.get(this.id) + "' when adding " + this);
		}

		fluidMap.put(this.id, this);
	}*/

	public Fluid(@NotNull NamespaceID id, @NotNull String translationKey, @Nullable List<Block<?>> blocks) {
		this(id, translationKey, blocks, null);
	}

	public Fluid(@NotNull NamespaceID id, @NotNull String translationKey, @Nullable List<Block<?>> blocks, @Nullable NamespaceID stateId) {
		this.id = id;
		this.stateId = stateId == null ? NamespaceID.fromPool("minecraft","empty") : stateId;
		this.translationKey = translationKey;

		if (blocks == null || blocks.isEmpty()) {
			throw new IllegalArgumentException("Fluid '" + id + "' must have at least one block associated with it.");
		}

		this.blocks.addAll(blocks);

		if (fluidMap.containsKey(this.id)) {
			throw new IllegalArgumentException("Fluid id '" + id + "' is already used by '" + fluidMap.get(this.id) + "' when adding " + this);
		}

		fluidMap.put(this.id, this);
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Fluid fluid)) return false;

		return id.equals(fluid.id);
	}

	public @NotNull String getTranslationKey() {
		return translationKey;
	}

	public @NotNull String getName() {
		return I18n.getInstance().translateKey(translationKey+".name");
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public int getFirstId() {
		if (blocks.isEmpty()) {
			return 0;
		}
		return blocks.get(0).id();
	}
}
