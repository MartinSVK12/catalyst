package sunsetsatellite.catalyst.core.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTooltip;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sunsetsatellite.catalyst.core.util.ICustomDescription;

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
        ItemStack stack = slot.getStack();
        if(stack != null && stack.getItem() instanceof ItemBlock){
            Block block = Block.blocksList[stack.getItem().id];
            if(block instanceof ICustomDescription){
                text.append(((ICustomDescription) block).getDescription(stack)).append("\n");
            }
        }
        if(stack != null && stack.getItem() instanceof ICustomDescription){
            text.append(((ICustomDescription) stack.getItem()).getDescription(stack)).append("\n");
        }
    }
}

