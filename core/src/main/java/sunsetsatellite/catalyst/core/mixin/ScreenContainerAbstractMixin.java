package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.IExtendedScreenDraw;

@Mixin(value = ScreenContainerAbstract.class, remap = false)
public class ScreenContainerAbstractMixin implements IExtendedScreenDraw {

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/player/PlayerLocal;inventory:Lnet/minecraft/core/player/inventory/container/ContainerInventory;"))
	public void drawScreen1(int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		drawAfterSlotAndButtonRendering(mouseX, mouseY, partialTick);
	}

	@Override
	public void drawAfterSlotAndButtonRendering(int mouseX, int mouseY, float partialTick) {

	}
}
