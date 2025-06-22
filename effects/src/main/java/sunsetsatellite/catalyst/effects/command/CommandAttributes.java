package sunsetsatellite.catalyst.effects.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;

public class CommandAttributes implements CommandManager.CommandRegistry {

	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("attribute")
			.then(ArgumentBuilderLiteral.<CommandSource>literal("list")
				.executes(ctx -> {
						ctx.getSource().sendMessage("Available attributes:");
						for (Attribute<?> attribute : Attributes.getInstance()) {
							ctx.getSource().sendMessage("- " +Attributes.getInstance().getKey(attribute));
						}
						return Command.SINGLE_SUCCESS;
					}
				)
			)
		);
	}
}
