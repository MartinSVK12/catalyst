package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.core.util.helper.Side;
import org.useless.dragonfly.model.block.data.ModelData;
import org.useless.dragonfly.model.block.processed.ModernBlockModel;
import org.useless.dragonfly.utilities.NamespaceId;

import java.util.HashMap;
import java.util.Objects;

public class ModernMultipartBlockModel extends ModernBlockModel {

	public final HashMap<Side, String> textures;

	public ModernMultipartBlockModel(NamespaceId namespaceId, HashMap<Side, String> texture) {
		super(namespaceId);
		this.textures = texture;
		this.modelData.ambientocclusion = false;
	}

	@Override
	public NamespaceId getTexture(String textureKey) {
		if(Objects.equals(textureKey, "#missing") || textures == null){
			return NamespaceId.idFromString("minecraft:block/texture_missing");
		}
		String sideStr = textureKey.replace("#", "").toUpperCase();
		sideStr = sideStr.replace("UP","TOP").replace("DOWN","BOTTOM");
		Side side = Side.valueOf(sideStr);
		return NamespaceId.idFromString(textures.get(side));
	}

	public ModelData getModelData() {
		return modelData;
	}
}
