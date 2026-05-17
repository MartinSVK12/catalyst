package sunsetsatellite.catalyst.effects.api.effect.render.heartContainer;

import net.minecraft.core.entity.player.Player;

public class HeartContainerSimple extends HeartContainer {

	private final String path;

	public HeartContainerSimple(Player player, String path) {
		super(player);
		this.path = path;
	}

	@Override
	public String getBasePath() {
		return path;
	}
}
