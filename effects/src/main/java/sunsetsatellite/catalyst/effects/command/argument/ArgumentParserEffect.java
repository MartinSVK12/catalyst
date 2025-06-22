package sunsetsatellite.catalyst.effects.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.helpers.ArgumentParser;
import net.minecraft.core.net.command.util.CommandHelper;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ArgumentParserEffect extends ArgumentParser {

	private Effect effect;
	private static final SimpleCommandExceptionType INVALID_EFFECT = new SimpleCommandExceptionType(() -> I18n.getInstance().translateKey("error.catalyst-effects.invalidEffect"));

	protected ArgumentParserEffect(StringReader reader) {
		super(reader);
	}

	private CompletableFuture<Suggestions> suggestItems(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
		consumer.accept(suggestionsBuilder2);
		return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
	}

	public Effect parse() throws CommandSyntaxException {
		this.startPosition = this.reader.getCursor();
		this.suggestions = this::suggestItems;
		this.parseEffect();
		if (this.effect == null) {
			throw INVALID_EFFECT.createWithContext(this.reader);
		} else {
			return effect;
		}
	}

	private void parseEffect() throws CommandSyntaxException {
		StringBuilder builder = new StringBuilder();

		while(this.reader.canRead()) {
			char peak = this.reader.peek();
			if (peak == '[' || peak == '{' || peak == ' ') {
				break;
			}

			builder.append(this.reader.read());
		}

		String string = builder.toString();

		this.effect = Effects.getInstance().getItem(string);
	}
}
