package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.adapters;


import com.google.gson.*;
import org.apache.commons.lang3.NotImplementedException;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.CubeData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.ModelData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.PositionData;

import java.lang.reflect.Type;
import java.util.Map;

public class ModelDataJsonAdapter implements JsonDeserializer<ModelData>, JsonSerializer<ModelData> {
	@Override
	public ModelData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		ModelData data = new ModelData();
		if (obj.has("parent")) data.parent = obj.get("parent").getAsString();
		if (obj.has("ambientocclusion")) data.ambientocclusion = obj.get("ambientocclusion").getAsBoolean();
		if (obj.has("display")){
			for (Map.Entry<String, JsonElement> e : obj.getAsJsonObject("display").asMap().entrySet()){
				data.display.put(e.getKey(), CatalystMultipart.GSON.fromJson(e.getValue(), PositionData.class));

			}
		}
		if (obj.has("textures")){
			for (Map.Entry<String, JsonElement> e : obj.getAsJsonObject("textures").asMap().entrySet()){
				data.textures.put(e.getKey(), e.getValue().getAsString());
			}
		}
		if (obj.has("elements")){
			JsonArray arr = obj.getAsJsonArray("elements");
			data.elements = new CubeData[arr.size()];
			for (int i = 0; i < arr.size(); i++){
				data.elements[i] = CatalystMultipart.GSON.fromJson(arr.get(i), CubeData.class);
			}
		}
		return data;
	}

	@Override
	public JsonElement serialize(ModelData src, Type typeOfSrc, JsonSerializationContext context) {
		throw new NotImplementedException();
	}
}
