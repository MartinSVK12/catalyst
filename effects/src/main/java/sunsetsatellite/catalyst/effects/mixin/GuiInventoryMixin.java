package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.gui.GuiContainer;
import net.minecraft.client.gui.GuiInventory;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.gui.GuiEffects;

@Mixin(value = GuiInventory.class,remap = false)
public abstract class GuiInventoryMixin extends GuiContainer {

	private EntityPlayer player;

	private GuiInventoryMixin(Container container) {
		super(container);
	}

	@Inject(method = "<init>",at = @At("TAIL"))
	public void init(EntityPlayer player, CallbackInfo ci) {
		this.player = player;
	}

	@Inject(method = "drawScreen",at = @At("TAIL"))
	public void drawEffects(int mouseX, int mouseY, float partialTick, CallbackInfo ci){
		new GuiEffects().drawEffects(((IHasEffects)player).getContainer(),mc,mouseX,mouseY,partialTick);
	}
}
