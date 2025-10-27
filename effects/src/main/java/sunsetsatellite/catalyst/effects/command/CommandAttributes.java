package sunsetsatellite.catalyst.effects.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import org.jetbrains.annotations.NotNull;
import org.useless.seedviewer.collections.NamespaceID;
import sunsetsatellite.catalyst.CatalystEffects;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.command.argument.ArgumentTypeAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommandAttributes implements CommandManager.CommandRegistry {

	public static void listAttributes(CommandContext<CommandSource> ctx, IHasEffects target) {
		Attributes attrs = Attributes.getInstance();

		HashMap<String, List<Attribute<?>>> attrByMod = new HashMap<>();

		for (Attribute<?> attribute : Attributes.getInstance()) {
			List<Attribute<?>> list = attrByMod.computeIfAbsent(new NamespaceID(attrs.getKey(attribute)).namespace, k -> new ArrayList<>());
			list.add(attribute);
		}

		CommandSource source = ctx.getSource();
		for (String key : attrByMod.keySet()) {
			List<Attribute<?>> attributes = attrByMod.get(key);
			if (attributes == null) continue;

			source.sendMessage(
				"  " + TextFormatting.YELLOW + TextFormatting.BOLD + "@" + key + TextFormatting.RESET
			);

			for (Attribute<?> attribute : attributes) {
				source.sendMessage(
					String.format(
						"- %s: %s",
						new NamespaceID(attrs.getKey(attribute)).value,
						attribute.calculate(target)
					)
				);
			}
		}
	}

	public static int listAttributesForSender(CommandContext<CommandSource> ctx) {
		ctx.getSource().sendMessage("Available attributes:");
		listAttributes(ctx, (IHasEffects<?>) ctx.getSource().getSender());
		return Command.SINGLE_SUCCESS;
	}

	public static int listAttributesForTarget(CommandContext<CommandSource> ctx) {
		EntitySelector entitySelector = ctx.getArgument("target", EntitySelector.class);

		try {
			List<? extends Entity> entities = entitySelector.get(ctx.getSource());

			for (int i = 0; i < entities.size(); i++) {
				Entity entity = entities.get(i);

				ctx.getSource().sendMessage(
					String.format(
						"Available attributes for %s:",
						TextFormatting.get((entity instanceof Mob) ? ((Mob) entity).chatColor : 0).toString() +
						TextFormatting.BOLD +
						Entity.getNameFromEntity(entity, true) +
						TextFormatting.RESET
					)
				);

				listAttributes(ctx, (IHasEffects<?>) entity);
				if (i < entities.size() -1) ctx.getSource().sendMessage(" ");
			}
		}

		catch (CommandSyntaxException e) {
			CatalystEffects.LOGGER.info(String.valueOf(e));
			throw new RuntimeException(e);
		}
		return Command.SINGLE_SUCCESS;
	}

	public static int getAttribute(CommandContext<CommandSource> ctx) {
		Attribute<?> attr = ctx.getArgument("attribute", Attribute.class);
		CommandSource source = ctx.getSource();
		Player player = Objects.requireNonNull(source.getSender());

		source.sendMessage("Current value for \"" + attr.getName() + "\": ");
		source.sendMessage(
			String.format(
				"%s : %s",
				Entity.getNameFromEntity(player, true),
				attr.calculate((IHasEffects<?>) player)
			)
		);

		return Command.SINGLE_SUCCESS;
	}

	public static int getAttributeForTarget(CommandContext<CommandSource> ctx) {
		Attribute<?> attr = ctx.getArgument("attribute", Attribute.class);
		EntitySelector entitySelector = ctx.getArgument("target", EntitySelector.class);
		CommandSource source = ctx.getSource();

		try {
			List<? extends Entity> entities = entitySelector.get(source);

			source.sendMessage("Current value for \"" + attr.getName() + "\": ");
			for (Entity entity : entities) {
				source.sendMessage(
					Entity.getNameFromEntity(entity, true) + TextFormatting.RESET +
						": " +
						attr.calculate((IHasEffects<?>) entity)
				);
			}
		}

		catch (CommandSyntaxException e) {
			CatalystEffects.LOGGER.info(String.valueOf(e));
			throw new RuntimeException(e);
		}

		return Command.SINGLE_SUCCESS;
	}

	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		ArgumentBuilderLiteral<CommandSource> attributes =
			ArgumentBuilderLiteral.<CommandSource>literal("attribute")
			.then(
				ArgumentBuilderLiteral.<CommandSource>literal("list")
				.then(
					ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.entities())
					.executes(CommandAttributes::listAttributesForTarget)
				)
				.executes(CommandAttributes::listAttributesForSender)

			)
			.then(
				ArgumentBuilderLiteral.<CommandSource>literal("get")
				.then(
					ArgumentBuilderRequired.<CommandSource, Attribute<?>>argument("attribute", ArgumentTypeAttribute.attribute())
					.then(
						ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("target", ArgumentTypeEntity.entities())
						.executes(CommandAttributes::getAttributeForTarget)
					)
					.executes(CommandAttributes::getAttribute)
				)
			);

		commandDispatcher.register(attributes);
	}

}
