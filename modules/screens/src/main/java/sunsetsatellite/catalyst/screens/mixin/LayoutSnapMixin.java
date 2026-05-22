package sunsetsatellite.catalyst.screens.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.ScreenHudEditor;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.option.GameSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;

@Mixin(value = LayoutSnap.class, remap = false)
public abstract class LayoutSnapMixin extends Layout {

	@Shadow
	private @Nullable HudComponent parent;

	@Shadow
	private @NotNull ComponentAnchor parentAnchor;

	@Shadow
	private @NotNull ComponentAnchor anchor;

	@Shadow
	private int yOffset;

	@Shadow
	private int xOffset;

	@WrapMethod(method = "getComponentX")
	public int getComponentX(HudComponent component, int xSizeScreen, Operation<Integer> original) {
		if(component instanceof GuiComponent){
			if (this.parent == null) {
				return (int)((float)(xSizeScreen) * this.parentAnchor.xPosition) - component.getTrueAnchorX(this.anchor) + this.xOffset;
			} else {
				int posX = this.parent.getLayout().getComponentX(this.parent, xSizeScreen) - component.getTrueAnchorX(this.anchor) + this.parent.getTrueAnchorX(this.parentAnchor);
				if (component.isSnapToFill() && !this.parent.isVisible() && !(mc.currentScreen instanceof ScreenHudEditor)) {
					float collapseDirection = this.anchor.xPosition - this.parentAnchor.xPosition;
					posX += (int)(collapseDirection * (float)this.parent.getTrueXSize());
				} else {
					posX += this.xOffset;
				}

				return posX;
			}
		} else {
			return original.call(component,xSizeScreen);
		}
	}

	@WrapMethod(method = "getComponentY")
	public int getComponentY(HudComponent component, int ySizeScreen, Operation<Integer> original) {
		if(component instanceof GuiComponent){
			if (this.parent == null) {
				return (int)((float)(ySizeScreen) * this.parentAnchor.yPosition) - component.getTrueAnchorY(this.anchor) + this.yOffset;
			} else {
				int posY = this.parent.getLayout().getComponentY(this.parent, ySizeScreen) - component.getTrueAnchorY(this.anchor) + this.parent.getTrueAnchorY(this.parentAnchor);
				if (component.isSnapToFill() && !this.parent.isVisible() && !(mc.currentScreen instanceof ScreenHudEditor)) {
					float collapseDirection = this.anchor.yPosition - this.parentAnchor.yPosition;
					posY += (int)(collapseDirection * (float)this.parent.getTrueYSize());
				} else {
					posY += this.yOffset;
				}

				return posY;
			}
		} else {
			return original.call(component,ySizeScreen);
		}
	}

}
