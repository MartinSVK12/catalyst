package sunsetsatellite.catalyst.fluids.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystFluids;

@Mixin(value = Minecraft.class,remap = false)
public class MinecraftMixin {

	@Inject(method = "startGame",at = @At(value = "INVOKE",target = "Ljava/io/PrintStream;printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"))
	public void catalystFluidsPreLaunch(CallbackInfo ci){
		CatalystFluids.onPreLaunch();
	}
}
