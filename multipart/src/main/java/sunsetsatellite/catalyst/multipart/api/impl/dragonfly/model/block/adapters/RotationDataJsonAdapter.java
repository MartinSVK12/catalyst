package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.adapters;


import com.google.gson.*;
import net.minecraft.core.util.helper.Axis;
import org.apache.commons.lang3.NotImplementedException;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.Utilities;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.RotationData;

import java.lang.reflect.Type;
import java.util.HashMap;

public class RotationDataJsonAdapter implements JsonDeserializer<RotationData>, JsonSerializer<RotationData> {
	public static HashMap<String, Axis> keyToAxis = new HashMap<>();
	static {
		keyToAxis.put("x", Axis.X);
		keyToAxis.put("y", Axis.Y);
		keyToAxis.put("z", Axis.Z);
	}
	@Override
	public RotationData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		RotationData data = new RotationData();

		if (obj.has("origin")) data.origin = Utilities.floatArrFromJsonArr(obj.getAsJsonArray("origin"));
		if (obj.has("axis")) data.axis = keyToAxis.get(obj.get("axis").getAsString());
		if (obj.has("angle")) data.angle = obj.get("angle").getAsFloat();
		if (obj.has("rescale")) data.rescale = obj.get("rescale").getAsBoolean();

		return data;
	}

	@Override
	public JsonElement serialize(RotationData src, Type typeOfSrc, JsonSerializationContext context) {
		throw new NotImplementedException();
	}
}
