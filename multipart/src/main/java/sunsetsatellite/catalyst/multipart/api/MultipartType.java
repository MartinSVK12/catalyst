package sunsetsatellite.catalyst.multipart.api;

import java.util.HashMap;

public class MultipartType {

	public static final HashMap<String, MultipartType> types = new HashMap<>();
	public final String model;
	public final String name;
	public final int thickness;
	public final int cubesPerSide;

	public MultipartType(String name, String model, int thickness, int cubesPerSide) {
		this.model = model;
		this.name = name;
		this.thickness = thickness;
		this.cubesPerSide = cubesPerSide;
		types.put(name, this);
	}

	public static final MultipartType FOIL = new MultipartType("foil", "foil", 1, 1);
	public static final MultipartType COVER = new MultipartType("cover", "cover", 2, 1);
	public static final MultipartType PANEL = new MultipartType("panel", "panel", 4, 1);
	public static final MultipartType SLAB = new MultipartType("slab", "slab", 8, 1);
	/*public static final MultipartType ANTI_FOIL = new MultipartType("antifoil", "antifoil", 15, 1);
	public static final MultipartType ANTI_COVER = new MultipartType("anticover", "anticover", 14, 1);
	public static final MultipartType ANTI_PANEL = new MultipartType("antipanel", "antipanel", 12, 1);*/
	public static final MultipartType HOLLOW_FOIL = new MultipartType("hollow_foil", "hollow_foil",1, 4);
	public static final MultipartType HOLLOW_COVER = new MultipartType("hollow_cover", "hollow_cover",2, 4);
	public static final MultipartType HOLLOW_PANEL = new MultipartType("hollow_panel", "hollow_panel",4, 4);
	public static final MultipartType HOLLOW_SLAB = new MultipartType("hollow_slab", "hollow_slab",8, 4);
	//public static final MultipartType PILLAR = new MultipartType("pillar", "pillar",16, 1);
}
