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

	@Inject(method = "startWorld(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/core/world/type/WorldTypeGroups$Group;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/save/ISaveFormat;getSaveHandler(Ljava/lang/String;Z)Lnet/minecraft/core/world/save/LevelStorage;"))
	public void startWorld(String worldDirName, String worldName, long seed, WorldTypeGroups.Group worldTypeGroup, CallbackInfo ci) {
		LevelStorage saveHandler = this.saveFormat.getSaveHandler(worldDirName, false);
		Catalyst.WORLD_LOAD_SIGNAL.emit(saveHandler);
	}

}
