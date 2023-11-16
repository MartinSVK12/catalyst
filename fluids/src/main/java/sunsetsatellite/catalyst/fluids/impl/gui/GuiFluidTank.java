package sunsetsatellite.catalyst.fluids.impl.gui;


import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.fluids.impl.GuiFluid;
import sunsetsatellite.catalyst.fluids.impl.containers.ContainerFluidTank;
import sunsetsatellite.catalyst.fluids.impl.tiles.TileEntityFluidItemContainer;

public class GuiFluidTank extends GuiFluid {

    public String name = "Fluid Tank";
    public GuiFluidTank(InventoryPlayer inventoryPlayer, TileEntity tile) {
        super(new ContainerFluidTank(inventoryPlayer, (TileEntityFluidItemContainer) tile),inventoryPlayer);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f)
    {
        int i = mc.renderEngine.getTexture("/assets/fluidapi/gui/tank_gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

    }

    protected void drawGuiContainerForegroundLayer()
    {
        super.drawGuiContainerForegroundLayer();
        fontRenderer.drawString(name, 64, 6, 0xFF404040);
    }


    public void init()
    {
        super.init();
    }
}
