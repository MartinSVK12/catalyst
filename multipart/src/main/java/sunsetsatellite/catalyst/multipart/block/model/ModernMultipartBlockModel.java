package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.NamespaceId;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.helper.ModelHelper;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.ModelData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.PositionData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.processed.BlockCube;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

public class ModernMultipartBlockModel {

	private static final HashMap<String, PositionData> defaultDisplays = new HashMap<>();

	static {
		PositionData gui = new PositionData(new double[]{30, 225, 0}, new double[] {0, 0, 0}, new double[]{0.625, 0.625, 0.625});
		PositionData ground = new PositionData(new double[]{0, 0, 0}, new double[] {0, 3, 0}, new double[]{0.25, 0.25, 0.25});
		PositionData right_3rd = new PositionData(new double[]{75, 45, 0}, new double[] {0, 2.5, 0}, new double[]{0.375, 0.375, 0.375});
		PositionData right_first = new PositionData(new double[]{0, 45, 0}, new double[] {0, 0, 0}, new double[]{0.4, 0.4, 0.4});

		defaultDisplays.put("gui", gui);
		defaultDisplays.put("ground", ground);
		defaultDisplays.put("thirdperson_righthand", right_3rd);
		defaultDisplays.put("firstperson_righthand", right_first);
	}

	public final HashMap<Side, String> textures;
	public final NamespaceId namespaceId;
	public BlockCube[] blockCubes = new BlockCube[0];
	public HashMap<String, String> textureMap;
	public HashMap<String, PositionData> display;
	protected ModelData modelData;
	protected ModernMultipartBlockModel parentModel;

	public ModernMultipartBlockModel(NamespaceId namespaceId, HashMap<Side, String> texture) {
		this.namespaceId = namespaceId;
		this.textures = texture;
		refreshModel();
	}

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

	public void refreshModel(){
		this.modelData = ModelHelper.loadBlockModel(namespaceId);
		this.modelData.ambientocclusion = false;
		textureMap = new HashMap<>();
		display = new HashMap<>();

		textureMap.putAll(modelData.textures);
		display.putAll(modelData.display);

		// Use parent elements if model does not specify its own
		if (parentModel != null && modelData.elements == null){
			this.blockCubes = new BlockCube[parentModel.blockCubes.length];
			for (int i = 0; i < blockCubes.length; i++) {
				blockCubes[i] = new BlockCube(this, parentModel.blockCubes[i].cubeData);
			}
		} else if (modelData.elements != null) {
			this.blockCubes = new BlockCube[modelData.elements.length];
			for (int i = 0; i < blockCubes.length; i++) {
				blockCubes[i] = new BlockCube(this, modelData.elements[i]);
			}
		}
	}

	public boolean getAO(){
		return modelData.ambientocclusion;
	}

	@Nonnull
	public PositionData getDisplayPosition(String key){
		if (display.containsKey(key)){
			return display.get(key);
		}
		display.put(key, defaultDisplays.getOrDefault(key, new PositionData()));
		return display.get(key);
	}
}
