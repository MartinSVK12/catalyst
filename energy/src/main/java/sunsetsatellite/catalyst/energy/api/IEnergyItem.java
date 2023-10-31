package sunsetsatellite.catalyst.energy.api;


import net.minecraft.core.item.ItemStack;

public interface IEnergyItem {

    int provide(ItemStack stack, int amount, boolean test);
    int receive(ItemStack stack, int amount, boolean test);

    int getEnergy(ItemStack stack);
    int getCapacity(ItemStack stack);
    int getMaxReceive(ItemStack stack);
    int getMaxProvide(ItemStack stack);

    void modifyEnergy(ItemStack stack, int amount);
}
