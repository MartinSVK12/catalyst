package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.effects.ItemGiveEffect;
import sunsetsatellite.catalyst.effects.api.attribute.Attributes;
import sunsetsatellite.catalyst.effects.api.effect.Effect;
import sunsetsatellite.catalyst.effects.api.effect.Effects;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.ModifierType;
import sunsetsatellite.catalyst.effects.api.modifier.type.IntModifier;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.util.*;

//TODO: make effects serializable

public class CatalystEffects implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "catalyst-effects";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config;

	static {
		Toml configToml = new Toml("Catalyst: Effects configuration file.");
		config = new TomlConfigHandler(MOD_ID,configToml);
	}
    @Override
    public void onInitialize() {

    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		Attributes.getInstance();
		Effects.getInstance();
		testItem = ItemHelper.createItem(MOD_ID, new ItemGiveEffect("testItem", 17450, Effects.ATTACK_BOOST).setIconIndex(Item.ammoSnowball.getIconFromDamage(0)).setMaxStackSize(1), "testItem");
		testItem2 = ItemHelper.createItem(MOD_ID, new ItemGiveEffect("testItem2", 17451, Effects.DURATION_BOOST).setIconIndex(Item.ammoSnowball.getIconFromDamage(0)).setMaxStackSize(1), "testItem2");
		LOGGER.info(String.format("%d attributes registered.",Attributes.getInstance().size()));
		LOGGER.info(String.format("%d effects registered.",Effects.getInstance().size()));
		LOGGER.info("Catalyst: Effects initialized.");
	}

	public static <K,V> Map<K,V> mapOf(K[] keys, V[] values){
		if(keys.length != values.length){
			throw new IllegalArgumentException("Arrays differ in size!");
		}
		HashMap<K,V> map = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i],values[i]);
		}
		return map;
	}

	@SafeVarargs
	public static <T> List<T> listOf(T... values){
		return new ArrayList<>(Arrays.asList(values));
	}

	public static Item testItem;

    public static Item testItem2;

}
