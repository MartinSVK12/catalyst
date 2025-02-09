package sunsetsatellite.catalyst.core.util.mixin.interfaces;

public interface UnlimitedItemStack {
    void setUnlimited(boolean unlimited);

	void enableCustomMaxSize(int maxSize);

	void disableCustomMaxSize();
}
