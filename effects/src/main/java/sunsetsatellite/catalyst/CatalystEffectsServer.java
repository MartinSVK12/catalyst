package sunsetsatellite.catalyst;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.net.command.CommandManager;
import sunsetsatellite.catalyst.effects.command.CommandAttributes;
import sunsetsatellite.catalyst.effects.command.CommandEffects;

public class CatalystEffectsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		CommandManager.registerCommand(new CommandEffects());
		CommandManager.registerCommand(new CommandAttributes());
	}
}
