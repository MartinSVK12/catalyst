package sunsetsatellite.catalyst.fluids.api.impl.btatweaker;

import net.minecraft.core.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import sunsetsatellite.catalyst.fluids.util.FluidStack;
import turing.btatweaker.luapi.LuaClass;
import turing.btatweaker.luapi.LuaItem;
import turing.btatweaker.util.LuaFunctionFactory;
import turing.docs.*;

import java.util.Collections;
import java.util.List;

@Documented
@turing.docs.LuaClass(value = "FluidStack", constructor = @Function(value = "fluid", returnType = "FluidStack", arguments = {
	@Argument(value = "string | number", name = "fluid"),
	@Argument(value = "number?", name = "amount")
}, examples = {
	@FunctionExample(value = "271", returnValues = "water"),
	@FunctionExample(value = {"\"minecraft:lava\"", "5000"}, returnValues = "fiveBucketsOfLava")
}))
@Property(name = "Amount", value = "number", description = "The amount of fluid")
@Property(name = "NamespaceID", value = "string", description = "The NamespaceID of the fluid")
@Property(name = "TranslationKey", value = "string", description = "The translation key of the fluid")
@Property(name = "Blocks", value = "table", description = "A table of all block ids associated with the fluid")
@Description("Represents a FluidStack.")
public class LuaFluidStack extends LuaClass implements ILuaFluidIngredient {
	private final FluidStack stack;

	public LuaFluidStack(FluidStack stack) {
		super();
		this.stack = stack;

		rawset("Amount", stack.amount);
		rawset("NamespaceID", stack.fluid.stateId.toString());
		rawset("RegistryName", stack.fluid.stateId.toString());
		rawset("Namespace", stack.fluid.stateId.namespace());
		rawset("ModId", stack.fluid.stateId.namespace());
		rawset("TranslationKey", stack.fluid.translationKey);

		LuaTable blockTable = new LuaTable();

		for (int i = 0; i < stack.fluid.blocks.size(); i++) {
			blockTable.set(i, LuaValue.valueOf(stack.fluid.blocks.get(i).id()));
		}

		rawset("Blocks", blockTable);
		rawset("ToItemStack", new ToItemStack());
	}

	@Method(value = "ToItemStack", builder = true, examples = @FunctionExample(value = "", returnValues = "item"))
	@Description("Attempts to get an Item version of this FluidStack")
	protected static final class ToItemStack extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue self) {
			ItemStack stack = ((LuaFluidStack) self).stack.toItemStack();
			if (stack != null) {
				return new LuaItem(stack);
			} else {
				return NIL;
			}
		}
	}

	@Override
	public TwoArgFunction getMulFunction() {
		return LuaFunctionFactory.oneArgBuilderMethod((self, arg) -> {
			int amount = arg.checkint();
			self.rawset("Amount", amount);
			((LuaFluidStack) self).stack.amount = amount;
		});
	}

	@Override
	public OneArgFunction getLenFunction() {
		return LuaFunctionFactory.zeroArgMethod((self) ->
			self.rawget("Amount")
		);
	}

	@Override
	public OneArgFunction getToStringFunction() {
		return LuaFunctionFactory.zeroArgMethod((self) ->
			LuaValue.valueOf(((LuaFluidStack) self).stack.toString())
		);
	}

	@Override
	public TwoArgFunction getEqualFunction() {
		return LuaFunctionFactory.oneArgMethod((self, arg) -> {
			if (arg.istable() && arg instanceof LuaFluidStack) {
				LuaFluidStack other = (LuaFluidStack) arg;
				return LuaValue.valueOf(other.stack.isStackEqual(((LuaFluidStack) self).stack));
			}
			return FALSE;
		});
	}

	@Override
	public TwoArgFunction getSubFunction() {
		return LuaFunctionFactory.oneArgBuilderMethod((self, arg) -> {
			int amount = arg.checkint();
			int resultAmount = Math.max(((LuaFluidStack) self).stack.amount - amount, 1);
			rawset("Amount", resultAmount);
			((LuaFluidStack) self).stack.amount = resultAmount;
		});
	}

	@Override
	public TwoArgFunction getAddFunction() {
		return LuaFunctionFactory.oneArgBuilderMethod((self, arg) -> {
			int amount = arg.checkint();
			int resultAmount = ((LuaFluidStack) self).stack.amount + amount;
			rawset("Amount", resultAmount);
			((LuaFluidStack) self).stack.amount = resultAmount;
		});
	}

	@Override
	public int getAmount() {
		return stack.amount;
	}

	@Override
	public List<FluidStack> resolve() {
		return Collections.singletonList(stack);
	}

	public FluidStack getStack() {
		return this.stack;
	}
}
