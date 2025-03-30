package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data;


import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.Utilities;

import java.util.HashMap;
import java.util.Map;

public class ModelData {
	public String parent = null;
	public boolean ambientocclusion = true;
	public Map<String, PositionData> display;
	public Map<String, String> textures = new HashMap<>();
	public CubeData[] elements;
	public ModelData(){
		this(null, true, new HashMap<>(), new HashMap<>(), null);
	}
	public ModelData(String parent, boolean ao, Map<String, PositionData> display, Map<String, String> texture, CubeData[] elements){
		this.parent = parent;
		this.ambientocclusion = ao;
		this.display = display;
		this.textures = texture;
		this.elements = elements;
	}

	public static final HashMap<String, Side> keyToSide = new HashMap<>();
	public static final HashMap<Side, String> sideToKey = new HashMap<>();
	private static void registerSide(Side side, String key){
		keyToSide.put(key, side);
		sideToKey.put(side, key);
	}
	static {
		registerSide(Side.BOTTOM, "down");
		registerSide(Side.TOP, "up");
		registerSide(Side.NORTH, "north");
		registerSide(Side.SOUTH, "south");
		registerSide(Side.WEST, "west");
		registerSide(Side.EAST, "east");
	}
	public String toString(){
		StringBuilder builder = new StringBuilder("\n");
		builder.append("parent: ").append(parent).append("\n");
		builder.append("ambientocclusion: ").append(ambientocclusion).append("\n");
		builder.append("display: ").append("\n");
		for (String key: display.keySet()) {
			builder.append("\t").append(key).append("\n");;
			builder.append(Utilities.tabBlock(display.get(key).toString(), 2));
		}
		builder.append("textures: ").append("\n");
		for (String key: textures.keySet()) {
			builder.append("\t").append(key).append(": ").append(textures.get(key)).append("\n");
		}
		builder.append("elements: ").append("\n");
		if (elements != null){
			for (CubeData cube: elements) {
				builder.append("\t{\n");
				builder.append(Utilities.tabBlock(cube.toString(),1));
				builder.append("\t}\n");
			}
		} else {
			builder.append("\tnull").append("\n");
		}
		return builder.toString();
	}
}
