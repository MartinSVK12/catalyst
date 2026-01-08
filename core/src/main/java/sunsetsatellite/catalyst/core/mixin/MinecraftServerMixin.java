package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.SaveHandlerServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;

import java.io.File;

@Mixin(value = MinecraftServer.class,remap = false)
public class MinecraftServerMixin {

	@Inject(method = "initWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/Dimension;getDimensionList()Ljava/util/Map;", shift = At.Shift.BEFORE))
	private void initWorld(ISaveFormat saveFormat, String worldDirName, long l, CallbackInfo ci) {
		SaveHandlerServer saveHandler = new SaveHandlerServer(saveFormat, new File("."), worldDirName, true);
		Catalyst.WORLD_LOAD_SIGNAL.emit(saveHandler);
	}

}
