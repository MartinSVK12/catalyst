package sunsetsatellite.catalyst.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTooltip;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sunsetsatellite.catalyst.core.util.ICustomDescription;

import java.util.Objects;

@Mixin(
        value = GuiTooltip.class,
        remap = false
)
public class GuiTooltipMixin extends Gui {
    @Inject(
            method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/GuiTooltip;formatDescription(Ljava/lang/String;I)Ljava/lang/String;", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void injectCustomTooltip(ItemStack itemStack, boolean showDescription, Slot slot, CallbackInfoReturnable<String> cir, I18n trans, StringBuilder text, boolean discovered, boolean ctrlPressed, boolean shiftPressed, boolean debug) {
		if(itemStack != null && itemStack.getItem() instanceof ItemBlock){
            Block block = Block.blocksList[itemStack.getItem().id];
            if(block instanceof ICustomDescription){
				if(!Objects.equals(((ICustomDescription) block).getDescription(itemStack), "")){
					text.append(((ICustomDescription) block).getDescription(itemStack)).append("\n");
				}
            }
        }
        if(itemStack != null && itemStack.getItem() instanceof ICustomDescription){
			if(!Objects.equals(((ICustomDescription) itemStack.getItem()).getDescription(itemStack), "")){
				text.append(((ICustomDescription) itemStack.getItem()).getDescription(itemStack)).append("\n");
			}
        }
    }

	@Inject(
		method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;",
		at = @At(value = "JUMP",opcode = Opcodes.IFEQ,ordinal = 12)
	)
	public void injectPersistentTooltip(ItemStack itemStack, boolean showDescription, Slot slot, CallbackInfoReturnable<String> cir, @Local StringBuilder text){
		if(itemStack != null && itemStack.getItem() instanceof ItemBlock){
			Block block = Block.blocksList[itemStack.getItem().id];
			if(block instanceof ICustomDescription){
				if(!Objects.equals(((ICustomDescription) block).getPersistentDescription(itemStack), "")){
					text.append("\n").append(((ICustomDescription) block).getPersistentDescription(itemStack));
				}
			}
		}
		if(itemStack != null && itemStack.getItem() instanceof ICustomDescription){
			if(!Objects.equals(((ICustomDescription) itemStack.getItem()).getPersistentDescription(itemStack), "")){
				text.append("\n").append(((ICustomDescription) itemStack.getItem()).getPersistentDescription(itemStack));
			}
		}
	}
}

