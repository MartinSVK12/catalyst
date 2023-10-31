package sunsetsatellite.catalyst.energy.api;


import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.Direction;

public interface IEnergySource extends IEnergy {
    int provide(Direction dir, int amount, boolean test);
    int provide(ItemStack stack, int amount, boolean test);
    int getMaxProvide();
    int getMaxProvide(Direction dir);
    void setMaxProvide(int amount);
}
