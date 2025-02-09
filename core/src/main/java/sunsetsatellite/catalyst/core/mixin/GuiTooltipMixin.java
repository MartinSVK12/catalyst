package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.player.inventory.slot.Slot;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.ICustomDescription;

import java.util.Objects;

@Mixin(
        value = TooltipElement.class,
        remap = false
)
public class GuiTooltipMixin extends Gui {

	@Inject(
		method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/TooltipElement;formatDescription(Ljava/lang/String;I)Ljava/lang/String;",shift = At.Shift.AFTER)
	)
	public void injectCustomTooltip(ItemStack itemStack, boolean showDescription, Slot slot, CallbackInfoReturnable<String> cir, @Local StringBuilder text) {
		addDescription(itemStack, text);
	}

	@Inject(
		method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;",
		at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 12))
	public void injectPersistentTooltip(ItemStack itemStack, boolean showDescription, Slot slot, CallbackInfoReturnable<String> cir, @Local StringBuilder text){
		addDescription(itemStack, text);
	}

	@Unique
	private void addDescription(ItemStack itemStack, StringBuilder text) {
		if(itemStack != null && itemStack.getItem() instanceof ICustomDescription){
			if(!Objects.equals(((ICustomDescription) itemStack.getItem()).getDescription(itemStack), "")){
				text.append(((ICustomDescription) itemStack.getItem()).getDescription(itemStack)).append("\n");
			}
		}

		if(itemStack != null && itemStack.getItem() instanceof ItemBlock){
			Block<?> b = ((ItemBlock<?>) itemStack.getItem()).getBlock();
			ICustomDescription block = Catalyst.blockLogic(b, ICustomDescription.class);
			if(block != null){
				if(!Objects.equals(block.getDescription(itemStack), "")){
					text.append(block.getDescription(itemStack)).append("\n");
				}
			}
		}
	}
}

