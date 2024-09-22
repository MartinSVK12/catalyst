package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.adapters;


import com.google.gson.*;
import org.apache.commons.lang3.NotImplementedException;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.Utilities;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.PositionData;

import java.lang.reflect.Type;

public class PositionDataJsonAdapter implements JsonDeserializer<PositionData>, JsonSerializer<PositionData> {
	@Override
	public PositionData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		double[] rotation = new double[3];
		double[] translation = new double[3];
		double[] scale = new double[]{1,1,1};
		if (obj.has("rotation")) rotation = Utilities.doubleArrFromJsonArr(obj.getAsJsonArray("rotation"));
		if (obj.has("translation")) translation = Utilities.doubleArrFromJsonArr(obj.getAsJsonArray("translation"));
		if (obj.has("scale")) scale = Utilities.doubleArrFromJsonArr(obj.getAsJsonArray("scale"));
		return new PositionData(rotation, translation, scale);
	}
	@Override
	public JsonElement serialize(PositionData src, Type typeOfSrc, JsonSerializationContext context) {
		throw new NotImplementedException();
	}
}
