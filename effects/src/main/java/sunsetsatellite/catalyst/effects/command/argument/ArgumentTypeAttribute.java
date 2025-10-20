package sunsetsatellite.catalyst.effects.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;

import java.util.concurrent.CompletableFuture;

public class ArgumentTypeAttribute implements ArgumentType<Attribute<?>> {

	public static ArgumentType<Attribute<?>> attribute() {
		return new ArgumentTypeAttribute();
	}

	public static Attribute<?> getAttribute(CommandContext<?> context, String name) {
		return context.getArgument(name, Attribute.class);
	}

	@Override
	public Attribute<?> parse(StringReader stringReader) throws CommandSyntaxException {
		int start = stringReader.getCursor();

		String key;
		try { key = stringReader.readStringUntil(' ');}
		catch (CommandSyntaxException e) {
			stringReader.setCursor(start);
			key = stringReader.getRemaining();
		}

		stringReader.setCursor(start + key.length());
		return Attributes.getInstance().getItem(key);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String key = builder.getRemaining();

		SuggestionsBuilder result = new SuggestionsBuilder(builder.getInput(), builder.getStart());
		Attributes attrs = Attributes.getInstance();
		attrs.iterator().forEachRemaining(
			a -> {
				String attrKey = attrs.getKey(a);
				if (attrKey.startsWith(key) || attrKey.split(":")[1].startsWith(key)) {
					result.suggest(attrKey);
				}
			}
		);

		return result.buildFuture();
	}
}
