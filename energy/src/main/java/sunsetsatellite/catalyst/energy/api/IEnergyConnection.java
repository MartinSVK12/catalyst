package sunsetsatellite.catalyst.energy.api;

import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IEnergyConnection {
    boolean canConnect(Direction dir, Connection connection);
}
