package sunsetsatellite.catalyst.effects.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.net.command.util.CommandHelper;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ArgumentTypeEffect implements ArgumentType<Effect> {

	public static ArgumentType<Effect> effect() {
		return new ArgumentTypeEffect();
	}

	public static Effect getEffect(CommandContext<?> context, String name) {
		return context.getArgument(name, Effect.class);
	}

	@Override
	public Effect parse(StringReader stringReader) throws CommandSyntaxException {
		ArgumentParserEffect parser = new ArgumentParserEffect(stringReader);
		return parser.parse();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ArgumentParserEffect parser = new ArgumentParserEffect(stringReader);

		try {
			parser.parse();
		} catch (CommandSyntaxException ignored) {
		}

		return parser.fillSuggestions(builder, (suggestionsBuilder) -> {
			String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

			Effects.getInstance().forEach((eff) -> {
				CommandHelper.getStringToSuggest(eff.id.toLowerCase(Locale.ROOT), remaining).ifPresent(suggestionsBuilder::suggest);
			});

			suggestionsBuilder.buildFuture();
		});
	}
}
