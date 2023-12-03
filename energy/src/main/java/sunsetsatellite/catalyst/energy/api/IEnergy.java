package sunsetsatellite.catalyst.energy.api;

import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IEnergy extends IEnergyConnection {

    int getEnergy();
    int getEnergy(Direction dir);
    int getCapacity();
    int getCapacity(Direction dir);

    void setEnergy(int amount);
    void modifyEnergy(int amount);
    void setCapacity(int amount);

    void notifyOfReceive(IEnergy notifier);

    void notifyOfProvide(IEnergy notifier);

    void setConnection(Direction dir, Connection connection);
}
