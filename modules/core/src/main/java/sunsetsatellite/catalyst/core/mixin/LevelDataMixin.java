package sunsetsatellite.catalyst.core.mixin;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.IAbsoluteWorldTime;

@Mixin(value = LevelData.class, remap = false)
public class LevelDataMixin implements IAbsoluteWorldTime {

	@Unique
	private static long absoluteWorldTime;

	@Override
	public long getAbsoluteWorldTime() {
		return absoluteWorldTime;
	}

	@Override
	public void setAbsoluteWorldTime(long value) {
		absoluteWorldTime = value;
	}

	@Inject(method = "<init>(Lnet/minecraft/core/world/save/LevelData;)V", at = @At("TAIL"))
	public void init(LevelData levelData, CallbackInfo ci) {
		absoluteWorldTime = ((IAbsoluteWorldTime) levelData).getAbsoluteWorldTime();
	}

	@Inject(method = "deserialize", at = @At("HEAD"))
	private static void read(CompoundTag tag, CallbackInfoReturnable<LevelData> cir) {
		absoluteWorldTime = tag.getLong("AbsoluteTime");
	}

	@Inject(method = "serialize", at = @At("HEAD"))
	private static void write(LevelData levelData, CompoundTag out, CallbackInfoReturnable<CompoundTag> cir) {
		out.putLong("AbsoluteTime", absoluteWorldTime);
	}
}
