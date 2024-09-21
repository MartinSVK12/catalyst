package sunsetsatellite.catalyst.multipart.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiContainer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.util.helper.Side;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.container.ContainerCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;

public class GuiCarpenterWorkbench extends GuiContainer {

	public TileEntityCarpenterWorkbench tile;

	public GuiCarpenterWorkbench(InventoryPlayer inventoryplayer, TileEntityCarpenterWorkbench tile) {
		super(new ContainerCarpenterWorkbench(inventoryplayer, tile));
		this.tile = tile;
	}

	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString("Carpenter Workbench", 32, 6, 4210752);
		this.fontRenderer.drawString("Side", 6, 24, 4210752);
		this.fontRenderer.drawString(I18n.getInstance().translateKey("gui.crafting.label.inventory"), 8, this.ySize - 96 + 2, 4210752);

		this.fontRenderer.drawString(String.valueOf(tile.page), 150, 40, 4210752);
	}

	public void init() {
		super.init();
		for (Object slot : inventorySlots.inventorySlots) {
			if (slot instanceof SlotPartPicker) {
				((SlotPartPicker) slot).variableIndex = ((SlotPartPicker) slot).getSlotIndex() + (9 * (tile.page - 1));
			}
		}
		controlList.add(new GuiButton(0, Math.round((float) width / 2 + 60), Math.round((float) height / 2 - 68), 20, 20, "/\\"));
		controlList.add(new GuiButton(1, Math.round((float) width / 2 + 60), Math.round((float) height / 2 - 34), 20, 20, "\\/"));
		controlList.add(new GuiButton(2, Math.round((float) width / 2 - 82), Math.round((float) height / 2 - 51), 20, 20, String.valueOf(this.tile.selectedSide.name().charAt(0))));
	}

	protected void buttonPressed(GuiButton guibutton) {
		if (!guibutton.enabled) {
			return;
		}
		switch (guibutton.id) {
			case 0:
				if (tile.page < tile.maxPages) {
					tile.page++;
					for (Object slot : inventorySlots.inventorySlots) {
						if (slot instanceof SlotPartPicker) {
							((SlotPartPicker) slot).variableIndex += 9;
						}
					}
				}
				break;
			case 1:
				if (tile.page > 1) {
					tile.page--;
					for (Object slot : inventorySlots.inventorySlots) {
						if (slot instanceof SlotPartPicker) {
							((SlotPartPicker) slot).variableIndex -= 9;
						}
					}
				}
				break;
			case 2:
				int i = this.tile.selectedSide.ordinal();
				i++;
				if(i >= Side.values().length) {
					i = 0;
				}
				this.tile.selectedSide = Side.values()[i];
				if(this.tile.selectedSide == Side.NONE) {
					guibutton.displayString = "*";
					break;
				}
				guibutton.displayString = String.valueOf(this.tile.selectedSide.name().charAt(0));
				break;
		}
	}


	protected void drawGuiContainerBackgroundLayer(float f) {
		int i = this.mc.renderEngine.getTexture("/assets/catalyst-multipart/textures/gui/carpenter_workbench.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
	}
}
