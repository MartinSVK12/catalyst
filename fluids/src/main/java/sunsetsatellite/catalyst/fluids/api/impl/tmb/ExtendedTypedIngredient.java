package sunsetsatellite.catalyst.fluids.api.impl.tmb;

import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turing.tmb.TypedIngredient;
import turing.tmb.api.ingredient.IIngredientType;
import turing.tmb.util.ModIDHelper;

public class ExtendedTypedIngredient<T> extends TypedIngredient<T> {
    public ExtendedTypedIngredient(String namespace, String name, IIngredientType<T> type, T ingredient) {
        super(namespace, name, type, ingredient);
    }

    public static TypedIngredient<FluidStack> fluidStackIngredient(FluidStack stack) {
        return new TypedIngredient<>(ModIDHelper.getModIDForItem(stack.toItemStack()), stack.toItemStack().getDisplayName(), TMBFluidPlugin.FLUID_STACK, stack.copy());
    }
}
