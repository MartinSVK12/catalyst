package sunsetsatellite.catalyst.core.util;

public interface IItemIO {

    int getActiveItemSlotForSide(Direction dir);

    Connection getItemIOForSide(Direction dir);
}
