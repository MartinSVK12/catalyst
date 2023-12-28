package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiInventory;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.gui.GuiEffects;

@Mixin(value = GuiIngame.class,remap = false)
public abstract class GuiIngameMixin extends Gui {

	private GuiIngameMixin(){}

//	@Inject(
//		method = "renderGameOverlay",
//		at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/WorldRenderer;setupScaledResolution()V", shift = At.Shift.AFTER),
//		locals = LocalCapture.CAPTURE_FAILHARD
//	)
//	public void renderAfterGameOverlay(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci, I18n stringtranslate, int width, int height, int sp, FontRenderer fontrenderer) {
//		new GuiEffects().drawEffects(((IHasEffects)Minecraft.getMinecraft(this).thePlayer).getContainer(),Minecraft.getMinecraft(this),mouseX,mouseY,partialTicks);
//	}

}
