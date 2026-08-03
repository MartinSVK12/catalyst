package sunsetsatellite.catalyst.screens.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.CatalystScreensClient;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidItemContainer;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.screens.screen.ScreenComposedContainer;

import java.util.ArrayList;

@Mixin(value = HudIngame.class, remap = false)
public class HudIngameMixin {

	@Shadow
	protected Minecraft mc;

	@Inject( method = "updateTick", at = @At("TAIL"))
	public void updateTick(CallbackInfo ci) {
		if (CatalystScreensClient.testKey.isPressed() && this.mc.currentScreen == null) {
			this.mc.displayScreen(new ScreenComposedContainer(mc.thePlayer.inventory, new FluidItemContainer() {
				@Override
				public boolean canInsertFluid(int slot, FluidStack fluidStack) {
					return false;
				}

				@Override
				public FluidStack getFluidInSlot(int slot) {
					return null;
				}

				@Override
				public int getFluidCapacityForSlot(int slot) {
					return 0;
				}

				@Override
				public ArrayList<Fluid> getAllowedFluidsForSlot(int slot) {
					return null;
				}

				@Override
				public void setFluidInSlot(int slot, FluidStack fluid) {

				}

				@Override
				public FluidStack insertFluid(int slot, FluidStack fluidStack) {
					return null;
				}

				@Override
				public int getRemainingCapacity(int slot) {
					return 0;
				}

				@Override
				public int getFluidInventorySize() {
					return 0;
				}

				@Override
				public void onFluidInventoryChanged() {

				}

				@Override
				public int getTransferSpeed() {
					return 0;
				}

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
			}, Catalyst.compoundOf(new String[]{"scene"},"test")));
		}
	}

}
