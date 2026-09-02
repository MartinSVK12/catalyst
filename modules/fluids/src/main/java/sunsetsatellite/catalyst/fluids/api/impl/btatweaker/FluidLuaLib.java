package sunsetsatellite.catalyst.fluids.api.impl.btatweaker;

import net.minecraft.core.util.collection.NamespaceID;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import sunsetsatellite.catalyst.fluids.util.Fluid;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import sunsetsatellite.catalyst.fluids.util.Fluids;

public class FluidLuaLib extends TwoArgFunction {
	@Override
	public LuaValue call(LuaValue modname, LuaValue env) {
		FluidStackFunc fluidStackFunc = new FluidStackFunc();

		env.set("fluid", fluidStackFunc);
		env.get("package").get("loaded").set("fluid", fluidStackFunc);

		return fluidStackFunc;
	}

	protected static final class FluidStackFunc extends VarArgFunction {
		@Override
		public Varargs invoke(Varargs varargs) {
			int blockId = -1;
			String id = null;
			int amount = 1000;
			if (varargs.isnumber(2)) {
				amount = varargs.checkint(2);
			}
			if (varargs.isnumber(1)) {
				blockId = varargs.checkint(1);
				Fluid fluid = Fluids.getFluid(blockId);
				if (fluid != null) {
					return new LuaFluidStack(new FluidStack(fluid, amount));
				}
			} else if (varargs.isstring(1)) {
				id = varargs.checkjstring(1);
				try {
					NamespaceID namespaceID = NamespaceID.fromPool(id);
					Fluid fluid = Fluids.getFluid(namespaceID);
					if (fluid != null) {
						return new LuaFluidStack(new FluidStack(fluid, amount));
					}
				} catch (Exception ignored) {}
			}
			throw new LuaError("could not find fluid with id '" + blockId + "' or namespaceid '" + id + "'");
		}
	}
}
