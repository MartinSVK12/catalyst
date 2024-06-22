package sunsetsatellite.catalyst.fluids.impl;


import net.minecraft.client.gui.GuiContainer;
import net.minecraft.client.gui.GuiTooltip;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.util.helper.Color;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sunsetsatellite.catalyst.core.util.IColorOverride;
import sunsetsatellite.catalyst.core.util.NumberUtil;
import sunsetsatellite.catalyst.fluids.interfaces.mixins.IPlayerController;
import sunsetsatellite.catalyst.fluids.util.SlotFluid;


public class GuiFluid extends GuiContainer {

    public GuiFluid(ContainerFluid containerFluid, InventoryPlayer invP) {
        super(containerFluid);
        inventoryPlayer = invP;
    }

    @Override
    public void drawScreen(int i1, int i2, float f3) {
        super.drawScreen(i1, i2, f3);
        int i4 = (this.width - this.xSize) / 2;
        int i5 = (this.height - this.ySize) / 2;
        GL11.glPushMatrix();
        GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
        Lighting.disable();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)i4, (float)i5, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        int i9;
        int i10;
        SlotFluid slot6 = null;
        ContainerFluid fluidContainer = ((ContainerFluid) inventorySlots);
        for(int i7 = 0; i7 < fluidContainer.fluidSlots.size(); i7++) {
            SlotFluid slot8 = fluidContainer.fluidSlots.get(i7);
            this.drawFluidSlotInventory(slot8);
            if(this.getIsMouseOverFluidSlot(slot8, i1, i2)) {
                slot6 = slot8;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                i9 = slot8.xPos;
                i10 = slot8.yPos;
                this.drawGradientRect(i9, i10, i9 + 16, i10 + 16, 0x40FFFFFF, 0x40FFFFFF);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
        if(slot6 != null && slot6.getHasStack() && slot6.getFluidStack().getLiquid() != null) {
            i9 = i1 - i4;
            i10 = i2 - i5;
            String name = I18n.getInstance().translateNameKey(slot6.getFluidStack().getLiquid().getLanguageKey(0)).replace("Flowing ","").replace("Still ","");
            String amount = slot6.getFluidStack().amount+"/"+fluidContainer.tile.getFluidCapacityForSlot(slot6.slotIndex)+" mB";
            String percent = " ("+Math.round(((float)slot6.getFluidStack().amount/(float)fluidContainer.tile.getFluidCapacityForSlot(slot6.slotIndex))*100)+"%)";
            GuiTooltip tooltip = new GuiTooltip(mc);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            tooltip.render(name+"\n"+TextFormatting.LIGHT_GRAY+amount+percent,i9,i10,8,-8);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else if(slot6 != null) {
			i9 = i1 - i4;
			i10 = i2 - i5;
			String name = "Empty";
			String amount = 0+"/"+fluidContainer.tile.getFluidCapacityForSlot(slot6.slotIndex)+" mB";
			String percent = " ("+Math.round((0.0f/(float)fluidContainer.tile.getFluidCapacityForSlot(slot6.slotIndex))*100)+"%)";
			GuiTooltip tooltip = new GuiTooltip(mc);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			tooltip.render(name+"\n"+TextFormatting.LIGHT_GRAY+amount+percent,i9,i10,8,-8);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
        Lighting.enableInventoryLight();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f1) {

    }

    @Override
    protected void drawGuiContainerForegroundLayer() {
        super.drawGuiContainerForegroundLayer();
    }

    protected boolean getIsMouseOverFluidSlot(SlotFluid slot1, int i2, int i3) {
        int i4 = (this.width - this.xSize) / 2;
        int i5 = (this.height - this.ySize) / 2;
        i2 -= i4;
        i3 -= i5;
        return i2 >= slot1.xPos - 1 && i2 < slot1.xPos + 16 + 1 && i3 >= slot1.yPos - 1 && i3 < slot1.yPos + 16 + 1;
    }

    protected void drawFluidSlotInventory(SlotFluid slot1) {
        int i2 = slot1.xPos;
        int i3 = slot1.yPos;
        if(slot1.getHasStack() && slot1.getFluidStack().liquid != null){
            ItemStack itemStack4 = new ItemStack(slot1.getFluidStack().getLiquid(),slot1.getFluidStack().amount,0);
            int i5 = slot1.getBackgroundIconIndex();
            if(i5 >= 0) {
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
                this.drawTexturedModalRect(i2, i3, i5 % 16 * 16, i5 / 16 * 16, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                return;
            }

			ItemModel itemModel = ItemModelDispatcher.getInstance().getDispatch(slot1.getFluidStack().getLiquid().getDefaultStack().getItem());
			BlockModel<?> blockModel = BlockModelDispatcher.getInstance().getDispatch(slot1.getFluidStack().getLiquid());

			if(slot1.getFluidStack().getLiquid() == Block.fluidWaterFlowing && mc.gameSettings.biomeWater.value){
				int waterColor = BlockColorDispatcher.getInstance().getDispatch(Block.fluidWaterFlowing).getWorldColor(this.mc.theWorld, (int) this.mc.thePlayer.x, (int) this.mc.thePlayer.y, (int) this.mc.thePlayer.z);
				Color c = new Color().setARGB(waterColor);
				c.setRGBA(c.getRed(),c.getGreen(),c.getBlue(),0x40);

				((IColorOverride)blockModel).overrideColor(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
				itemModel.renderItemIntoGui(Tessellator.instance,this.fontRenderer, this.mc.renderEngine, itemStack4, i2, i3,1.0F);
			} else {
				itemModel.renderItemIntoGui(Tessellator.instance,this.fontRenderer, this.mc.renderEngine, itemStack4, i2, i3,1.0F);
			}

			((IColorOverride)blockModel).overrideColor(1,1,1,1);
			itemModel.renderItemOverlayIntoGUI(Tessellator.instance,this.fontRenderer, this.mc.renderEngine, itemStack4, i2, i3,1.0F);
        }
    }

    protected SlotFluid getFluidSlotAtPosition(int i1, int i2) {
        ContainerFluid fluidContainer = ((ContainerFluid) inventorySlots);
        for(int i3 = 0; i3 < fluidContainer.fluidSlots.size(); ++i3) {
            SlotFluid slot4 = fluidContainer.fluidSlots.get(i3);
            if(this.getIsMouseOverFluidSlot(slot4, i1, i2)) {
                return slot4;
            }
        }

        return null;
    }

    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        if (button == 0 || button == 1 || button == 10) {
            SlotFluid slot = this.getFluidSlotAtPosition(x, y);
            int l = (this.width - this.xSize) / 2;
            int i1 = (this.height - this.ySize) / 2;
            boolean flag = x < l || y < i1 || x >= l + this.xSize || y >= i1 + this.ySize;
            int j1 = -1;
            if (slot != null) {
                j1 = slot.slotIndex;
            }

            if (flag) {
                j1 = -999;
            }

            if (j1 != -1) {
                boolean shift = j1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54) || button == 10);
                boolean control = j1 != -999 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157));
                if (this.mc.gameSettings.swapCraftingButtons.value) {
                    boolean a = shift;
                    shift = control;
                    control = a;
                }

                ((IPlayerController)this.mc.playerController).fluidPickUpFromInventory(this.inventorySlots.windowId, j1, button == 10 ? 0 : button, shift, control, this.mc.thePlayer);
            }
        }
    }

    public void init()
    {
        super.init();
    }
    private final InventoryPlayer inventoryPlayer;
}
