package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.UnlimitedItemStack;

@Mixin(value = ItemStack.class, remap = false)
public class ItemStackMixin implements UnlimitedItemStack {
    @Unique
    public boolean unlimited = false;
	@Unique
	public boolean customMaxSizeEnabled = false;

	@Unique
	public int customMaxSize = 64;

    @Override
    @Unique
    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }

	@Override
	public void enableCustomMaxSize(int maxSize) {
		customMaxSizeEnabled = true;
		customMaxSize = maxSize;
	}

	@Override
	public void disableCustomMaxSize() {
		customMaxSizeEnabled = false;
	}

	@Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true)
    public void getMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        if (unlimited) {
            cir.setReturnValue(Integer.MAX_VALUE);
        } else if (customMaxSizeEnabled) {
			cir.setReturnValue(customMaxSize);
		}
	}
}
