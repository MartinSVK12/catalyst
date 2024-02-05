package sunsetsatellite.catalyst.effects.command;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import sunsetsatellite.catalyst.effects.api.effect.*;

import java.util.Objects;

public class EffectsCommand extends Command {
	public EffectsCommand(String name, String... alts) {
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
		switch (strings.length){
			case 1: {
				if (Objects.equals(strings[0], "list")) {
					commandSender.sendMessage("Available effects:");
					for (Effect effect : Effects.getInstance()) {
						commandSender.sendMessage("- " +Effects.getInstance().getKey(effect));
					}
					return true;
				}
			}
			case 2: {
				if(Objects.equals(strings[0], "remove")) {
					if (Objects.equals(strings[1], "all")) {
						effects.getContainer().removeAll();
						commandSender.sendMessage("All effects removed!");
						return true;
					}
					Effect effect = Effects.getInstance().getItem(strings[1]);
					if (effect == null) {
						commandSender.sendMessage("Effect not found!");
						return true;
					}
					effects.getContainer().remove(effect);
					commandSender.sendMessage("Effect removed!");
					return true;
				}
			}
			case 3: {
				if(!Objects.equals(strings[0], "subtract")){
					return false;
				}
				Effect effect = Effects.getInstance().getItem(strings[1]);
				if(effect == null){
					commandSender.sendMessage("Effect not found!");
					return true;
				}
				try {
					int amount = Integer.parseInt(strings[2]);
					effects.getContainer().subtract(new EffectStack(effects,effect,amount));
					commandSender.sendMessage("Effect weakened!");
				} catch (NumberFormatException e){
					commandSender.sendMessage("Invalid arguments!");
				}
				return true;
			}
			case 4: {
				if(!Objects.equals(strings[0], "add")){
					return false;
				}
				Effect effect = Effects.getInstance().getItem(strings[1]);
				if(effect == null){
					commandSender.sendMessage("Effect not found!");
					return true;
				}
				try {
					int duration = Integer.parseInt(strings[2]);
					int amount = Integer.parseInt(strings[3]);
					EffectStack stack = new EffectStack(effects,effect,duration,amount);
					effects.getContainer().add(stack);
					stack.start(effects.getContainer());
					commandSender.sendMessage("Effect applied!");
				} catch (NumberFormatException e){
					commandSender.sendMessage("Invalid arguments!");
				}
				return true;
			}
			default: {
				commandSender.sendMessage("Invalid arguments!");
				return false;
			}
		}
    }

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
		commandSender.sendMessage("/effect add (name) (duration) (amount)");
		commandSender.sendMessage("/effect remove (name)/all");
		commandSender.sendMessage("/effect subtract (name) (amount)");
		commandSender.sendMessage("/effect list");
	}
}
