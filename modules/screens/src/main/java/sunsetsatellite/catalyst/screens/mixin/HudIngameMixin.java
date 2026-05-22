package sunsetsatellite.catalyst.screens.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.screens.screen.ScreenComposed;
import sunsetsatellite.catalyst.screens.screen.ScreenComposedContainer;

@Mixin(value = HudIngame.class, remap = false)
public class HudIngameMixin {

	@Shadow
	protected Minecraft mc;

	@Inject( method = "updateTick", at = @At("TAIL"))
	public void updateTick(CallbackInfo ci) {
		if (CatalystScreensClient.testKey.isPressed() && this.mc.currentScreen == null) {
			this.mc.displayScreen(new ScreenComposedContainer("test", mc.thePlayer.inventory, new Container() {
				@Override
				public int getContainerSize() {
					return 0;
				}

				@Override
				public @Nullable ItemStack getItem(int i) {
					return null;
				}

				@Override
				public @Nullable ItemStack removeItem(int i, int i1) {
					return null;
				}

				@Override
				public void setItem(int i, @Nullable ItemStack itemStack) {

				}

				@Override
				public @NotNull String getNameTranslationKey() {
					return "";
				}

				@Override
				public int getMaxStackSize() {
					return 0;
				}

				@Override
				public void setChanged() {

				}

				@Override
				public boolean stillValid(@NotNull Player player) {
					return false;
				}

				@Override
				public void sort() {

				}
			}));
		}
	}

}
