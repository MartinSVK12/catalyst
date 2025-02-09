package sunsetsatellite.catalyst.core.util.io;

import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IFluidIO {

    int getActiveFluidSlotForSide(Direction dir);

    Connection getFluidIOForSide(Direction dir);

	void setFluidIOForSide(Direction dir, Connection con);
}
