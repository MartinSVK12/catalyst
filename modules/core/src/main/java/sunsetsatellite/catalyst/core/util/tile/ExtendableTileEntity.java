package sunsetsatellite.catalyst.core.util.tile;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.ITileEntityInit;
import sunsetsatellite.catalyst.core.util.tile.feature.IHasFeatures;

import java.util.HashMap;
import java.util.Map;

public class ExtendableTileEntity extends TileEntity implements ITileEntityInit, IHasFeatures {

	protected Map<String, TEFeature> features = new HashMap<>();
	private CompoundTag loadData;

	@MustBeInvokedByOverriders
	@Override
	public void init(Block<?> block) {
		loadFeatures();
		features.forEach((S, F) -> F.init(block));
	}

	@MustBeInvokedByOverriders
	@Override
	public void tick() {
		super.tick();
		features.forEach((S, F) -> F.tick());
	}

	@MustBeInvokedByOverriders
	@Override
	public void writeAdditionalData(@NotNull CompoundTag compoundTag) {
		CompoundTag featuresTag = new CompoundTag();

		features.forEach((S, F) -> {
			CompoundTag featureTag = new CompoundTag();
			F.writeToNBT(featureTag);
			featuresTag.putCompound(S, featureTag);
		});
		compoundTag.put("Features", featuresTag);
	}

	public void loadFeatures() {
		if (loadData != null) {
			CompoundTag featuresTag = loadData.getCompound("Features");
			for (Tag<?> tag : featuresTag.getValues()) {
				if (tag instanceof CompoundTag) {
					CompoundTag featureTag = (CompoundTag) tag;
					TEFeature feature = TEFeature.loadFeature(featureTag, worldObj);
					if (feature != null) {
						features.put(feature.id, feature);
					}
				}
			}
			loadData = null;
		}
	}

	@MustBeInvokedByOverriders
	@Override
	public void readAdditionalData(@NotNull CompoundTag compoundTag) {
		loadData = compoundTag;
	}

	@Override
	public boolean hasFeature(String id) {
		return features.get(id) != null;
	}

	@Override
	public TEFeature getFeature(String id) {
		return features.get(id);
	}

	@Override
	public TEFeature createAndAddFeature(String featureId) {
		TEFeature feature = TEFeature.createFeature(featureId, worldObj, tilePos.x, tilePos.y, tilePos.z);
		features.put(featureId, feature);
		return feature;
	}
}
