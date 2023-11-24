package sunsetsatellite.catalyst.fluids.impl.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiContainer;
import net.minecraft.client.gui.GuiTooltip;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.util.helper.Side;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.fluids.impl.containers.ContainerMultiFluidTank;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityMassFluidItemContainer;
import sunsetsatellite.catalyst.fluids.render.RenderFluid;
import sunsetsatellite.catalyst.fluids.util.FluidLayer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;

import java.util.ArrayList;
import java.util.Objects;

public class GuiMultiFluidTank extends GuiContainer {

    public String name = "Multi Fluid Tank";
    public TileEntityMassFluidItemContainer tile;
    public ItemEntityRenderer itemRender = new ItemEntityRenderer();
    public ArrayList<FluidLayer> fluidLayers = new ArrayList<>();
    /*{
        try {
            itemRender = (ItemEntityRenderer) FluidAPI.getPrivateValue(GuiContainer.class, this,"itemRenderer");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }*/

    public GuiMultiFluidTank(InventoryPlayer inventoryPlayer, TileEntity tile) {
        super(new ContainerMultiFluidTank(inventoryPlayer, (TileEntityMassFluidItemContainer) tile));
        this.tile = (TileEntityMassFluidItemContainer) tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f)
    {
        int i = mc.renderEngine.getTexture("/assets/fluidapi/gui/multitank_gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
        drawFluidTank(j+62,k+18,54,54);
    }

    protected void drawFluidTank(int x, int y, int sizeX, int sizeY) {
        tile.fluidContents.removeIf(Objects::isNull);
        int i = sizeY;
        int id = 0;
        fluidLayers.clear();
        for (FluidStack fluidStack : tile.fluidContents) {
            if(fluidStack != null) {
                BlockFluid fluid = fluidStack.liquid;
                if (fluidStack.liquid != null) {
                    int j = (width - xSize) / 2;
                    int k = (height - ySize) / 2;
                    int fluidBarSize = (int) CatalystFluids.map(fluidStack.amount,0,tile.fluidCapacity,2,sizeY-3);
                    RenderFluid.drawFluidIntoGui(fontRenderer, this.mc.renderEngine, fluid.id, 0, fluid.getBlockTextureFromSideAndMetadata(Side.BOTTOM,0), x, y+i-fluidBarSize-2, sizeX-2, fluidBarSize);
                    if (fluidStack.getLiquid() == Block.fluidWaterFlowing && Minecraft.getMinecraft(Minecraft.class).gameSettings.biomeWater.value) {
                        int waterColor = BlockColorDispatcher.getInstance().getDispatch(Block.fluidWaterFlowing).getWorldColor(this.mc.theWorld,tile.xCoord,tile.yCoord,tile.zCoord);
                        Color c = new Color().setARGB(waterColor);
                        c.setRGBA(c.getRed(), c.getGreen(), c.getBlue(), 0x40);
                        RenderFluid.drawFluidIntoGui(fontRenderer, this.mc.renderEngine, fluid.id, 0, fluid.getBlockTextureFromSideAndMetadata(Side.BOTTOM,0), x, y+i-fluidBarSize-2, sizeX-2, fluidBarSize, c.value);
                    }
                    fluidLayers.add(new FluidLayer(id,x,y+i-fluidBarSize-2,sizeX-2,fluidBarSize,fluidStack));
                    id++;

                    i-=fluidBarSize-1;
                }
            }
        }
    }

    public FluidLayer getFluidLayerAtPosition(int x, int y){
        if(!fluidLayers.isEmpty()){
            for (FluidLayer fluidLayer : fluidLayers) {
                if(this.getIsMouseOverFluidLayer(fluidLayer, x, y)) {
                    return fluidLayer;
                }
            }
        }
        return null;
    }

    public boolean getIsMouseOverFluidLayer(FluidLayer fluidLayer, int x, int y) {
        return x >= fluidLayer.x - 1 && x < fluidLayer.x + fluidLayer.sizeX + 1 && y >= fluidLayer.y - 1 && y < fluidLayer.y + fluidLayer.sizeY + 1;
    }

    @Override
    public void drawScreen(int x, int y, float renderPartialTicks) {
        super.drawScreen(x, y, renderPartialTicks);
        FluidLayer layer = getFluidLayerAtPosition(x,y);
        if(layer != null){
            String name = I18n.getInstance().translateNameKey(layer.getFluidStack().getLiquid().getLanguageKey(0)).replace("Flowing ","").replace("Still ","");
            String amount = layer.getFluidStack().amount+"/"+tile.getFluidCapacityForSlot(layer.id)+" mB";
            String percent = " ("+Math.round(((float)layer.getFluidStack().amount/(float)tile.getFluidCapacityForSlot(layer.id))*100)+"%)";
            GuiTooltip tooltip = new GuiTooltip(mc);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            tooltip.render(name+"\n"+TextFormatting.LIGHT_GRAY+amount+percent,x,y,8,-8);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        if(button == 0){
            FluidLayer layer = getFluidLayerAtPosition(x,y);
            if(layer != null){
                tile.fluidContents.sort((F1,F2)->{
                    if(F1 == layer.fluidStack) return -1;
                    if(F2 == layer.fluidStack) return 1;
                    return 0;
                });
            }
        }
    }

    protected void drawGuiContainerForegroundLayer()
    {
        super.drawGuiContainerForegroundLayer();
        fontRenderer.drawString(name, 48, 6, 0xFF404040);
        int l = 16;
    }


    public void initGui()
    {
        super.initGui();
    }
}
