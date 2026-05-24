package sunsetsatellite.catalyst.fluids.api.impl.tmb;

import sunsetsatellite.catalyst.fluids.util.RecipeExtendedSymbol;
import sunsetsatellite.catalyst.fluids.util.RecipeOutputStack;
import turing.tmb.TypedIngredient;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.*;
import java.util.stream.Collectors;

public class ExtendedIngredientList implements IIngredientList {
	private final List<ITypedIngredient<?>> list = new ArrayList<>();
	public String itemGroup;

	public ExtendedIngredientList() {

	}

	public ExtendedIngredientList(ITypedIngredient<?>... ingredients) {
		add(ingredients);
	}

	public ExtendedIngredientList(Collection<ITypedIngredient<?>> ingredients) {
		list.addAll(ingredients);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public List<ITypedIngredient<?>> getIngredients() {
		return Collections.unmodifiableList(list);
	}

	@Override
	public void add(ITypedIngredient<?>... ingredients) {
		list.addAll(Arrays.asList(ingredients));
	}

	public static ExtendedIngredientList fromRecipeSymbol(RecipeExtendedSymbol symbol) {
		if (symbol == null) {
			return new ExtendedIngredientList();
		}
		List<ITypedIngredient<?>> ingredients = symbol.resolve().stream().map(TypedIngredient::itemStackIngredient).collect(Collectors.toList());
		symbol.resolveFluids().stream().map(ExtendedTypedIngredient::fluidStackIngredient).forEach(ingredients::add);
		ExtendedIngredientList ingredientList = new ExtendedIngredientList(ingredients);

		if (symbol.getItemGroup() != null && !symbol.getItemGroup().isEmpty()) {
			ingredientList.itemGroup = symbol.getItemGroup();
		}

		return ingredientList;
	}

	public static ExtendedIngredientList fromRecipeOutput(RecipeOutputStack output) {
		if (output == null) {
			return new ExtendedIngredientList();
		}
		if (output.isItem()) {
			return new ExtendedIngredientList(TypedIngredient.itemStackIngredient(output.stack));
		} else if (output.isFluid()) {
			return new ExtendedIngredientList(ExtendedTypedIngredient.fluidStackIngredient(output.fluid));
		}
		return new ExtendedIngredientList();
	}
}
