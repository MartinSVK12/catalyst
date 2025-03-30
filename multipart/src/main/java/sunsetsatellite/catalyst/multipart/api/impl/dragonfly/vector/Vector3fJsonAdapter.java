package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.vector;


import com.google.gson.*;

import java.lang.reflect.Type;

public class Vector3fJsonAdapter implements JsonDeserializer<Vector3f>, JsonSerializer<Vector3f> {
	@Override
	public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonArray()){
			JsonArray arr = json.getAsJsonArray();
			return new Vector3f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
		}
		return null;
	}

	@Override
	public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context) {
		return null;
	}
}
