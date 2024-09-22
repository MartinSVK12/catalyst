package sunsetsatellite.catalyst.multipart.api.impl.dragonfly.helper;

import com.google.gson.stream.JsonReader;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.NamespaceId;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.Utilities;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.CubeData;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.model.block.data.ModelData;
import sunsetsatellite.catalyst.multipart.block.model.ModernMultipartBlockModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ModelHelper {
	public static final Map<NamespaceId, ModelData> modelDataFiles = new HashMap<>();
	public static final Map<NamespaceId, ModernMultipartBlockModel> registeredModels = new HashMap<>();

	/**
	 * Place mod models in the <i>assets/modid/models/block/</i> directory for them to be seen.
	 */
	public static ModernMultipartBlockModel getOrCreateBlockModel(String modId, Multipart multipart) {
		NamespaceId namespaceId = new NamespaceId(modId, multipart.type.model+"_"+multipart.meta+multipart.block.getKey().replace("tile","").replace(modId,"").replace(".","_").toLowerCase());
		if(multipart.specifiedSideOnly){
			namespaceId = new NamespaceId(modId, multipart.type.model+"_"+multipart.meta+multipart.block.getKey().replace("tile","").replace(modId,"").replace(".","_").toLowerCase()+"_"+multipart.side.name().toLowerCase());
		}
		NamespaceId modelId = new NamespaceId(modId, multipart.type.model);
		if (ModelHelper.registeredModels.containsKey(namespaceId)){
			return registeredModels.get(namespaceId);
		}
		ModernMultipartBlockModel model = new ModernMultipartBlockModel(modelId, multipart.textures);
		for (CubeData cube : model.getModelData().elements) {
			cube.faces.forEach((K,V)->{
				V.texture = "#"+K;
			});
		}
		ModelHelper.registeredModels.put(namespaceId, model);
		return model;
	}

	public static ModelData loadBlockModel(NamespaceId namespaceId){
		if (modelDataFiles.containsKey(namespaceId)){
			return modelDataFiles.get(namespaceId);
		}
		return Objects.requireNonNull(createBlockModel(namespaceId));
	}
	private static ModelData createBlockModel(NamespaceId namespaceId){
		JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Utilities.getResourceAsStream(getModelLocation(namespaceId)))));
		ModelData modelData = CatalystMultipart.GSON.fromJson(reader, ModelData.class);
		modelDataFiles.put(namespaceId, modelData);
		return modelData;
	}

	public static String getModelLocation(NamespaceId namespaceId){
		String modelSource = namespaceId.getId();
		if (!modelSource.contains(".json")){
			modelSource += ".json";
		}
		return "/assets/" + namespaceId.getNamespace() + "/models/" + modelSource;
	}
	public static String getBlockStateLocation(NamespaceId namespaceId){
		String modelSource = namespaceId.getId();
		if (!modelSource.contains(".json")){
			modelSource += ".json";
		}
		return "/assets/" + namespaceId.getNamespace() + "/blockstates/" + modelSource;
	}
	public static void refreshModels(){
		Set<NamespaceId> blockModelDataKeys = new HashSet<>(modelDataFiles.keySet());

		for (NamespaceId modelDataKey : blockModelDataKeys){
			createBlockModel(modelDataKey);
		}
		for (ModernMultipartBlockModel model : registeredModels.values()){
			model.refreshModel();
		}
	}
}
