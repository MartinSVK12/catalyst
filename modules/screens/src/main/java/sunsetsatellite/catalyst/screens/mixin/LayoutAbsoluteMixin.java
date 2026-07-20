package sunsetsatellite.catalyst.screens.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.option.GameSettings;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;

@Mixin(value = LayoutAbsolute.class,remap = false)
public abstract class LayoutAbsoluteMixin extends Layout {

	@Shadow
	private float xPosition;

	@Shadow
	private int xOffset;

	@Shadow
	private int yOffset;

	@Shadow
	private float yPosition;

	@Shadow
	private @NotNull ComponentAnchor anchor;

	@WrapMethod(method = "getComponentX")
	public int getComponentX(HudComponent component, int xSizeScreen, Operation<Integer> original) {
		if(component == null) return 0;
		if(component instanceof GuiComponent){
			return (int)(this.xPosition * (float)(xSizeScreen)) - component.getTrueAnchorX(this.anchor) + this.xOffset;
		}
		return original.call(component,xSizeScreen);
	}

	@WrapMethod(method = "getComponentY")
	public int getComponentY(HudComponent component, int ySizeScreen, Operation<Integer> original) {
		if(component == null) return 0;
		if(component instanceof GuiComponent){
			return (int)(this.yPosition * (float)(ySizeScreen)) - component.getTrueAnchorY(this.anchor) + this.yOffset;
		}
		return original.call(component,ySizeScreen);
	}

}
