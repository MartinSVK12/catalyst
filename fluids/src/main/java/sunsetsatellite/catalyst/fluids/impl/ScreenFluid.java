package sunsetsatellite.catalyst.fluids.impl;

import net.minecraft.client.gui.container.ScreenContainerAbstract;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.fluids.interfaces.mixin.FluidPickupController;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;

public class ScreenFluid extends ScreenContainerAbstract {

	public MenuFluid fluidSlots;

	public ScreenFluid(MenuFluid container) {
		super(container);
		this.fluidSlots = container;
	}

	@Override
	public void render(int mx, int my, float partialTick) {
		super.render(mx, my, partialTick);
		int centerX = (this.width - this.xSize) / 2;
		int centerY = (this.height - this.ySize) / 2;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)centerX, (float)centerY, 0.0F);
		SlotFluid slot = null;
		for (int i = 0; i < fluidSlots.fluidSlots.size(); i++) {
			SlotFluid currentSlot = fluidSlots.fluidSlots.get(i);
			boolean mouseOver = this.getIsMouseOverFluidSlot(currentSlot, mx, my);

			this.itemElement.render(currentSlot.getFluidStack().toItemStack(), currentSlot.x, currentSlot.y, mouseOver);

			if (mouseOver) {
				slot = currentSlot;
			}
		}

		if(slot != null && slot.hasStack()) {
			boolean showDescription = mc.gameSettings.alwaysShowDescriptions.value || mc.gameSettings.keyDescription.isPressed();
			String str = tooltipElement.getTooltipText(slot.getFluidStack().toItemStack(), showDescription);
			if(!str.isEmpty())
			{
				tooltipElement.render(str, mx, my, 8, -8);
			}
		}
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f) {

	}

	protected boolean getIsMouseOverFluidSlot(SlotFluid slot, int i2, int i3) {
		int i4 = (this.width - this.xSize) / 2;
		int i5 = (this.height - this.ySize) / 2;
		i2 -= i4;
		i3 -= i5;
		return i2 >= slot.x - 1 && i2 < slot.x + 16 + 1 && i3 >= slot.y - 1 && i3 < slot.y + 16 + 1;
	}

	protected SlotFluid getFluidSlotAtPosition(int i1, int i2) {
		MenuFluid fluidContainer = ((MenuFluid) inventorySlots);
		for(int i3 = 0; i3 < fluidContainer.fluidSlots.size(); ++i3) {
			SlotFluid slot4 = fluidContainer.fluidSlots.get(i3);
			if(this.getIsMouseOverFluidSlot(slot4, i1, i2)) {
				return slot4;
			}
		}

		return null;
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		super.mouseClicked(mx, my, buttonNum);
		if (buttonNum == 0 || buttonNum == 1 || buttonNum == 2 || buttonNum == 10) {
			this.clickFluidInventory(mx, my, buttonNum);
		}
	}

	public void clickFluidInventory(int mx, int my, int button) {
		SlotFluid slot = this.getFluidSlotAtPosition(mx, my);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		int slotId = -1;
		if (slot != null) {
			slotId = slot.slotIndex;
		}

		boolean outsideScreen = mx < x || my < y || mx >= x + this.xSize || my >= y+ this.ySize;

		if(outsideScreen){
			slotId = -999;
		}

		if(slotId != -1){
			boolean shift = slotId != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54) || button == 10);
			boolean control = slotId != -999 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157));
			if (this.mc.gameSettings.swapCraftingButtons.value) {
				boolean a = shift;
				shift = control;
				control = a;
			}

			((FluidPickupController)this.mc.playerController).catalyst$fluidPickUpFromInventory(this.inventorySlots.containerId, slotId, button == 10 ? 0 : button, shift, control, this.mc.thePlayer);
		}
	}


}
