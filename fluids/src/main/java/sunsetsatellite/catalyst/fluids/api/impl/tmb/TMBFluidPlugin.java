package sunsetsatellite.catalyst.fluids.api.impl.tmb;

import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.CatalystFluids;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turing.tmb.TMB;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.TMBEntrypoint;
import turing.tmb.api.ingredient.IIngredientRegistry;
import turing.tmb.api.ingredient.IIngredientTypeWithSubtypes;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.ModIDHelper;

public class TMBFluidPlugin implements ITMBPlugin, TMBEntrypoint {

	public static final IIngredientTypeWithSubtypes<Fluid, FluidStack> FLUID_STACK = new IIngredientTypeWithSubtypes<Fluid, FluidStack>() {
		@Override
		public String getUid() {
			return "fluid_stack";
		}

		@Override
		public Class<? extends FluidStack> getIngredientClass() {
			return FluidStack.class;
		}

		@Override
		public Class<? extends Fluid> getIngredientBaseClass() {
			return Fluid.class;
		}

		@Override
		public Fluid getBase(FluidStack ingredient) {
			return ingredient.fluid;
		}

		@Override
		public FluidStack getDefaultIngredient(Fluid base) {
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
		for (Fluid fluid : Fluid.fluidMap.values()) {
			if(!fluid.blocks.isEmpty()){
				ItemStack stack = fluid.blocks.get(0).getDefaultStack();
				registry.registerIngredient(ModIDHelper.getModIDForItem(stack), stack.getDisplayName(), new FluidStack(fluid, 1));
			}
		}
	}

	@Override
	public void onGatherPlugins(boolean isReload) {
		TMB.LOGGER.info("Loading plugin: {} from " + CatalystFluids.MOD_ID, this.getClass().getSimpleName());
		TMB.registerPlugin(this);
	}
}
