package sunsetsatellite.catalyst.multipart.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.data.block.mojang.BlockModelMojangData;

import static net.minecraft.core.util.collection.NamespaceID.DEFAULT_NAMESPACE;
import static sunsetsatellite.catalyst.CatalystMultipart.LOGGER;

public class MultipartData extends BlockModelMojangData.Cache {
	public static @Nullable BlockModelMojangData.Builder loadModelDataRaw(@NotNull final String id) {
		final String namespace;
		final String value;
		if (!id.contains(":")) {
			namespace = DEFAULT_NAMESPACE;
			value = id;
		} else {
			final String[] strings = id.split(":");
			if (strings.length != 2) throw new  IllegalArgumentException("Block model id '" + id + "' cannot have more then 1 ':' character!");
			namespace = strings[0];
			value = strings[1];
		}
		//if (modelDataCache.containsKey(id)) return modelDataCache.get(id);
		final BlockModelMojangData.Builder data = loadFromStream(Minecraft.getMinecraft().texturePackList.getResourceAsStream(String.format(RESOURCE_PATH, namespace, value)));
		if (data == null) {
			LOGGER.error("Could not locate block model data for id '{}'!", id);
			return null;
		}
		return data;
	}
}
