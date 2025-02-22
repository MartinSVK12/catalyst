package sunsetsatellite.catalyst.fluids.util;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFluid;
import net.minecraft.core.item.Item;
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
	public final String translationKey;

    @NotNull
    public final List<Block<?>> blocks = new ArrayList<>();

    @NotNull
    public static final Map<NamespaceID, Fluid> fluidMap = new LinkedHashMap<>();

	public Fluid(@NotNull NamespaceID id, @NotNull String translationKey) {
		this.id = id;
		this.translationKey = translationKey;

		if (fluidMap.containsKey(this.id)){
			throw new IllegalArgumentException("Fluid id '" + id + "' is already used by '" + fluidMap.get(this.id) + "' when adding " + this);
		}

		fluidMap.put(this.id, this);
	}

    public Fluid(@NotNull NamespaceID id, @NotNull String translationKey, @Nullable List<Block<?>> blocks) {
        this.id = id;
		this.translationKey = translationKey;
        if (blocks != null) this.blocks.addAll(blocks);

        if (fluidMap.containsKey(this.id)){
            throw new IllegalArgumentException("Fluid id '" + id + "' is already used by '" + fluidMap.get(this.id) + "' when adding " + this);
        }

		fluidMap.put(this.id, this);
    }

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Fluid)) return false;

		Fluid fluid = (Fluid) o;
		return id.equals(fluid.id);
	}

	public @NotNull String getTranslationKey() {
		return translationKey;
	}

	public @NotNull String getName(){
		return I18n.getInstance().translateNameKey(translationKey);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
