package sunsetsatellite.catalyst.util;

public interface IItemIO {

    int getActiveItemSlotForSide(Direction dir);

    Connection getItemIOForSide(Direction dir);
}
