package sunsetsatellite.catalyst.energy.api;


import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IEnergySink extends IEnergy {
    int receive(Direction dir, int amount, boolean test);
    int receive(ItemStack stack, int amount, boolean test);
    int getMaxReceive();
    int getMaxReceive(Direction dir);
    void setMaxReceive(int amount);
}
