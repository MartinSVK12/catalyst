package sunsetsatellite.catalyst;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Global;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.screens.component.base.ComponentPicker;
import turniplabs.halplibe.HalpLibe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CatalystScreens implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("catalyst-screens", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Catalyst: Screens initialized.");
	}

	public static String lang(String key){
		return "options.gui."+key;
	}

	public static @Nullable CompoundTag loadSceneNbt(final @NotNull String id) {
		String[] split = id.split(":");
		@Nullable InputStream stream = null;
		if(split.length == 2){
			stream = ComponentPicker.class.getResourceAsStream("/assets/"+split[0]+"/scenes/" + split[1] + ".nbt");
		}
		if (stream == null) {
			try {
				final @NotNull File file = new File(Global.accessor.getMinecraftDir(), "scenes/" + id + ".nbt");
				if (file.exists()) {
					stream = new FileInputStream(file);
				}
			} catch (final @NotNull IOException e) {
				e.printStackTrace();
			}
		}
		if(stream == null){
			return null;
		}
		try {
			final @NotNull CompoundTag tag = NbtIo.readCompressed(stream);
			return tag;
		} catch (final @NotNull IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
