package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.IExtendedScreenDraw;

@Mixin(value = ScreenContainerAbstract.class, remap = false)
public class ScreenContainerAbstractMixin implements IExtendedScreenDraw {

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/player/PlayerLocal;inventory:Lnet/minecraft/core/player/inventory/container/ContainerInventory;", opcode = Opcodes.GETFIELD))
	public void drawScreen1(int mx, int my, float partialTick, CallbackInfo ci) {
		drawAfterSlotAndButtonRendering(mx, my, partialTick);
	}

	@Override
	public void drawAfterSlotAndButtonRendering(int mouseX, int mouseY, float partialTick) {

	}
}
