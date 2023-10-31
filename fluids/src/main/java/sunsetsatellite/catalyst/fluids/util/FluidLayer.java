package sunsetsatellite.catalyst.fluids.util;

public class FluidLayer {
    public int id;
    public int x;
    public int y;
    public int sizeX;
    public int sizeY;
    public FluidStack fluidStack;

    public FluidLayer(int id, int x, int y, int sizeX, int sizeY, FluidStack fluidStack) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.fluidStack = fluidStack;
    }

    @Override
    public String toString() {
        return "FluidLayer{" +
                "id=" + id +
                "x=" + x +
                ", y=" + y +
                ", sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", fluidStack=" + fluidStack +
                '}';
    }

    public FluidStack getFluidStack() {
        return this.fluidStack;
    }
}
