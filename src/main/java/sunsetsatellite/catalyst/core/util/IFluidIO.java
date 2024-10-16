package sunsetsatellite.catalyst.core.util;

public interface IFluidIO {

    int getActiveFluidSlotForSide(Direction dir);

    Connection getFluidIOForSide(Direction dir);

	void setFluidIOForSide(Direction dir, Connection con);
}
