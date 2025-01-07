package sunsetsatellite.catalyst.fluids.api.impl.tmb;

import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.FluidType;
import turing.tmb.TMB;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.TMBEntrypoint;
import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.ModIDHelper;

public class TMBFluidPlugin implements ITMBPlugin, TMBEntrypoint {

	public static final IIngredientTypeWithSubtypes<FluidType, FluidStack> FLUID_STACK = new IIngredientTypeWithSubtypes<FluidType, FluidStack>() {
		@Override
		public String getUid() {
			return "fluid_stack";
		}

		@Override
		public Class<? extends FluidStack> getIngredientClass() {
			return FluidStack.class;
		}

		@Override
		public Class<? extends FluidType> getIngredientBaseClass() {
			return FluidType.class;
		}

		@Override
		public FluidType getBase(FluidStack ingredient) {
			return ingredient.getType();
		}

		@Override
		public FluidStack getDefaultIngredient(FluidType base) {
			return new FluidStack(base,1000);
		}
	};

	@Override
	public void registerIngredientTypes(ITMBRuntime runtime) {
		runtime.getIngredientTypeRegistry().registerIngredientType(FLUID_STACK, FluidStackIngredientRenderer.INSTANCE);
	}

	@Override
	public void registerIngredients(ITMBRuntime runtime) {
		IIngredientRegistry<FluidStack> registry = runtime.getRegistryForIngredientType(FLUID_STACK);
		for (FluidType fluidType : CatalystFluids.TYPES) {
			if(!fluidType.fluids.isEmpty()){
				ItemStack stack = fluidType.fluids.get(0).getDefaultStack();
				registry.registerIngredient(ModIDHelper.getModIDForItem(stack), stack.getDisplayName(), new FluidStack(fluidType.fluids.get(0), 1));
			}
		}
	}

	@Override
	public void onGatherPlugins(boolean isReload) {
		TMB.LOGGER.info("Loading plugin: "+this.getClass().getSimpleName()+" from "+CatalystFluids.MOD_ID);
		TMB.registerPlugin(this);
	}
}
