package sunsetsatellite.catalyst.fluids.api.impl.tmb;

import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import sunsetsatellite.catalyst.core.util.NumberUtil;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turing.tmb.TMB;
import turing.tmb.TMBRuntime;
import turing.tmb.api.drawable.builder.ITooltipBuilder;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.api.ingredient.IIngredientRenderer;
import turing.tmb.util.ModIDHelper;
import turing.tmb.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class FluidStackIngredientRenderer implements IIngredientRenderer<FluidStack> {
	@Override
	public void render(IGuiHelper helper, FluidStack fluidStack) {
		ItemStack ingredient = fluidStack.toItemStack();
		RenderUtil.renderItemInGui(helper.getMinecraft(), ingredient, 0, 0, 1, 1, 1, 1);
		if (ingredient.stackSize > 1) {
			String text = NumberUtil.format(ingredient.stackSize);
			helper.getMinecraft().font.drawStringWithShadow(text, 16 - helper.getMinecraft().font.getStringWidth(text), 8, 0xFFFFFF);
		}
	}

	@Override
	public List<String> getTooltip(ITooltipBuilder tooltipBuilder, boolean isCtrl, boolean isShift) {
		return new ArrayList<>();
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltipBuilder, FluidStack fluidStack, boolean isCtrl, boolean isShift) {
		ItemStack ingredient = fluidStack.toItemStack();
		List<String> lines = getTooltip(tooltipBuilder, isCtrl, isShift);
		lines.add(TMBRuntime.getTooltipText(ingredient, isCtrl));
		if (TMB.shouldShowModName) {
			String modName = ModIDHelper.getModNameForDisplay(ModIDHelper.getModIDForItem(ingredient));
			lines.add(TextFormatting.formatted(modName, TextFormatting.ITALIC, TextFormatting.BLUE));
		}
		tooltipBuilder.addAll(lines);
	}

	public static FluidStackIngredientRenderer INSTANCE = new FluidStackIngredientRenderer();
}
