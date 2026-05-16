package sunsetsatellite.catalyst.core.util.io;

import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IFluidIO {

	int getActiveFluidSlotForSide(Direction dir);

	void setActiveFluidSlotForSide(Direction dir, int slot);

	Connection getFluidIOForSide(Direction dir);

	void setFluidIOForSide(Direction dir, Connection con);

	void cycleFluidIOForSide(Direction dir);

	void cycleActiveFluidSlotForSide(Direction dir, boolean backwards);
}
