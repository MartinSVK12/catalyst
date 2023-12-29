package sunsetsatellite.catalyst.effects.command;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;

import java.util.Objects;

public class AttributesCommand extends Command {
	public AttributesCommand(String name, String... alts) {
		super(name, alts);
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		if(!commandSender.isPlayer()){
			commandSender.sendMessage("Only players can run this command!");
			return true;
		}
		IHasEffects effects = ((IHasEffects)commandSender.getPlayer());
		EntityPlayer player = commandSender.getPlayer();
        if (strings.length == 2) {
			if(Objects.equals(strings[0], "add")){
				Attribute<?> attribute = Attributes.getInstance().getItem(strings[1]);
				if(attribute == null){
					commandSender.sendMessage("Attribute not found!");
					return true;
				}
				effects.getContainer().getAttributes().add(attribute);
			} else if (Objects.equals(strings[0], "remove")) {
				Attribute<?> attribute = Attributes.getInstance().getItem(strings[1]);
				if(attribute == null){
					commandSender.sendMessage("Attribute not found!");
					return true;
				}
				effects.getContainer().getAttributes().remove(attribute);
			} else {
				commandSender.sendMessage("Invalid arguments!");
				return false;
			}
		}
		commandSender.sendMessage("Invalid arguments!");
		return false;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
		commandSender.sendMessage("/attribute add (name)");
		commandSender.sendMessage("/attribute remove (name)");
	}
}
