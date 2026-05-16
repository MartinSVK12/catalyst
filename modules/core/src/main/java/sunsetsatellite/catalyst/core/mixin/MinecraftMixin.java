package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.type.WorldTypeGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

	@Shadow
	private ISaveFormat saveFormat;

	@Inject(method = "startWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/save/ISaveFormat;getLevelStorage(Ljava/lang/String;Z)Lnet/minecraft/core/world/save/LevelStorage;"))
	public void startWorld(String worldDirName, CallbackInfo ci) {
		LevelStorage saveHandler = this.saveFormat.getLevelStorage(worldDirName, false);
		Catalyst.WORLD_LOAD_SIGNAL.emit(saveHandler);
	}

}
