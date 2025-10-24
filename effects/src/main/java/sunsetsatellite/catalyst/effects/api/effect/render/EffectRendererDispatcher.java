package sunsetsatellite.catalyst.effects.api.effect.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.util.dispatch.Dispatcher;
import net.minecraft.core.item.Items;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;


@Environment(EnvType.CLIENT)
public class EffectRendererDispatcher extends Dispatcher<Effect, EffectRenderer<?>> {

	private static final EffectRendererDispatcher INSTANCE = new EffectRendererDispatcher();

	@Override
	protected EffectRenderer<?> getDefault() {
		return null;
	}

	public static EffectRendererDispatcher getInstance() {
		return INSTANCE;
	}

	public EffectRenderer<?> getDispatch(Effect effect) {
		return effect == null ? getDefault() : super.getDispatch(effect);
	}

	public static EffectRenderer<?> getRendererFor(Effect effect) {
		return INSTANCE.getDispatch(effect);
	}
}
