package sunsetsatellite.catalyst.multipart.screen;

import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.core.util.mp.PacketScreenAction;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import sunsetsatellite.catalyst.multipart.block.entity.TileEntityCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.menu.MenuCarpenterWorkbench;
import sunsetsatellite.catalyst.multipart.util.SlotPartPicker;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

public class ScreenCarpenterWorkbench extends ScreenContainerAbstract {

	public TileEntityCarpenterWorkbench tile;

	public ScreenCarpenterWorkbench(ContainerInventory inventoryplayer, TileEntityCarpenterWorkbench tile) {
		super(new MenuCarpenterWorkbench(inventoryplayer, tile));
		this.tile = tile;
	}

	protected void drawGuiContainerForegroundLayer() {
		this.font.drawString("Carpenter Workbench", 32, 6, 0x404040);
		this.font.drawString("Side", 6, 24, 0x404040);
		this.font.drawString(I18n.getInstance().translateKey("gui.crafting.label.inventory"), 8, this.ySize - 96 + 2, 4210752);

		this.font.drawString(String.valueOf(tile.page), 150, 40, 0x404040);
	}

	public void init() {
		super.init();
		for (Object slot : inventorySlots.slots) {
			if (slot instanceof SlotPartPicker) {
				((SlotPartPicker) slot).variableIndex = ((SlotPartPicker) slot).getSlotIndex() + (9 * (tile.page - 1));
			}
		}
		buttons.add(new ButtonElement(0, Math.round((float) width / 2 + 60), Math.round((float) height / 2 - 68), 20, 20, "/\\"));
		buttons.add(new ButtonElement(1, Math.round((float) width / 2 + 60), Math.round((float) height / 2 - 34), 20, 20, "\\/"));
		buttons.add(new ButtonElement(2, Math.round((float) width / 2 - 82), Math.round((float) height / 2 - 51), 20, 20, String.valueOf(this.tile.selectedSide.name().charAt(0))));
	}

	@Override
	protected void buttonClicked(ButtonElement guibutton) {
		if (!guibutton.enabled) {
			return;
		}
		switch (guibutton.id) {
			case 0:
				if (tile.page < tile.maxPages) {
					tile.page++;
					for (Object slot : inventorySlots.slots) {
						if (slot instanceof SlotPartPicker) {
							((SlotPartPicker) slot).variableIndex += 9;
						}
					}
				}
				break;
			case 1:
				if (tile.page > 1) {
					tile.page--;
					for (Object slot : inventorySlots.slots) {
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
		if(EnvironmentHelper.isClientWorld()){
			NetworkHandler.sendToServer(new PacketScreenAction(guibutton.id,0,0,new Vec3i(tile.x, tile.y, tile.z), tile.getClass()));
		}
	}


	protected void drawGuiContainerBackgroundLayer(float f) {
		@NotNull Texture i = this.mc.textureManager.loadTexture("/assets/catalyst-multipart/textures/gui/carpenter_workbench.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.textureManager.bindTexture(i);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
	}
}
