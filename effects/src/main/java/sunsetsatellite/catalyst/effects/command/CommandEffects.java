package sunsetsatellite.catalyst.effects.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.command.argument.ArgumentTypeEffect;


public class CommandEffects implements CommandManager.CommandRegistry {

	public static SimpleCommandExceptionType INCOMPATIBLE_ENTITY;

	public static int listEffects(CommandContext<CommandSource> ctx) {
		ctx.getSource().sendMessage("Available effects:");
		for (Effect effect : Effects.getInstance()) {
			ctx.getSource().sendMessage("- " +Effects.getInstance().getKey(effect));
		}

		return Command.SINGLE_SUCCESS;
	}

	public static int removeEffect(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		final Effect effect = ArgumentTypeEffect.getEffect(ctx, "name");

		if(!(ctx.getSource().getSender() instanceof IHasEffects)){
			throw INCOMPATIBLE_ENTITY.create();
		}

		IHasEffects effects = ((IHasEffects)ctx.getSource().getSender());
		effects.getContainer().remove(effect);
		return Command.SINGLE_SUCCESS;
	}

	public static int removeAllEffects(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		if(!(ctx.getSource().getSender() instanceof IHasEffects)){
			throw INCOMPATIBLE_ENTITY.create();
		}

		IHasEffects effects = ((IHasEffects)ctx.getSource().getSender());
		effects.getContainer().removeAll();
		ctx.getSource().sendMessage("Removed all effects.");
		return Command.SINGLE_SUCCESS;
	}

	public static int addEffect(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Effect effect = ArgumentTypeEffect.getEffect(ctx, "name");
		final int duration = ArgumentTypeInteger.getInteger(ctx, "duration");
		final int amount = ArgumentTypeInteger.getInteger(ctx, "amount");

		if(!(ctx.getSource().getSender() instanceof IHasEffects)){
			throw INCOMPATIBLE_ENTITY.create();
		}

		IHasEffects effects = ((IHasEffects)ctx.getSource().getSender());
		EffectStack stack = new EffectStack(effects,effect,duration,amount);
		effects.getContainer().add(stack);
		stack.start(effects.getContainer());

		return Command.SINGLE_SUCCESS;
	}

	public static int addEffectNoDuration(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Effect effect = ArgumentTypeEffect.getEffect(ctx, "name");
		final int amount = ArgumentTypeInteger.getInteger(ctx, "amount");

		if(!(ctx.getSource().getSender() instanceof IHasEffects)){
			throw INCOMPATIBLE_ENTITY.create();
		}

		IHasEffects effects = ((IHasEffects)ctx.getSource().getSender());
		EffectStack stack = new EffectStack(effects, effect, amount);
		effects.getContainer().add(stack);
		stack.start(effects.getContainer());

		return Command.SINGLE_SUCCESS;
	}

	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		INCOMPATIBLE_ENTITY = new SimpleCommandExceptionType(new LiteralMessage(I18n.getInstance().translateKey("error.catalyst-effects.incompatibleEntity")));

		commandDispatcher.register(
			ArgumentBuilderLiteral.<CommandSource>literal("effect")

			.then(ArgumentBuilderLiteral.<CommandSource>literal("list")
				.executes(CommandEffects::listEffects)
			)

			.then(ArgumentBuilderLiteral.<CommandSource>literal("remove")
				.requires(src -> src.hasAdmin() && src.getSender() != null)
				.then(ArgumentBuilderLiteral.<CommandSource>literal("all").executes(CommandEffects::removeAllEffects))
				.then(ArgumentBuilderRequired.<CommandSource, Effect>argument("name", ArgumentTypeEffect.effect())
					.executes(CommandEffects::removeEffect))
			)

			.then(ArgumentBuilderLiteral.<CommandSource>literal("add")
				.requires(src -> src.hasAdmin() && src.getSender() != null)
				.then(ArgumentBuilderRequired.<CommandSource, Effect>argument("name", ArgumentTypeEffect.effect())
					.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("amount", ArgumentTypeInteger.integer())
						.executes(CommandEffects::addEffectNoDuration)
					)

					.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("duration", ArgumentTypeInteger.integer())
						.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("amount", ArgumentTypeInteger.integer())
							.executes(CommandEffects::addEffect)
						)
					)
				)
			)
		);
	}
}
