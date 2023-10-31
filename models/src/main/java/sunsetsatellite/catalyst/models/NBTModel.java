package sunsetsatellite.catalyst.models;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.IntTag;
import com.mojang.nbt.NbtIo;
import sunsetsatellite.catalyst.CatalystModels;
import sunsetsatellite.catalyst.core.util.Vec2f;
import sunsetsatellite.catalyst.core.util.Vec3f;
import sunsetsatellite.catalyst.core.util.Vec4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NBTModel {
    public String modId;
    public String filePath;
    public CompoundTag data;
    public ModelSurface[] surfaces;
    public String[] textures;
    public boolean rotatable;

    public static HashMap<String,NBTModel> models = new HashMap<>();

    public NBTModel(String modId, String filePath, String[] textures, boolean rotatable) {
        this.modId = modId;
        this.filePath = filePath;
        this.textures = textures;
        this.rotatable = rotatable;
        loadFromNBT(filePath);
    }

    protected void loadFromNBT(String filePath) {
        try (InputStream resource = this.getClass().getResourceAsStream("/assets/" + modId + "/models/" + filePath + ".nbt")) {
            if (resource != null) {
                this.data = NbtIo.readCompressed(resource);
                ArrayList<ModelSurface> surfaces = new ArrayList<>();
                for (Object o : data.getValues()) {
                    CompoundTag surfaceTag = (CompoundTag) o;
                    int vertexAmount = surfaceTag.getCompound("Vertices").getValues().size();
                    int indexAmount = surfaceTag.getCompound("Indices").getValues().size();
                    CompoundTag translationTag = surfaceTag.getCompound("Translation");
                    CompoundTag rotationTag = surfaceTag.getCompound("Rotation");
                    CompoundTag scaleTag = surfaceTag.getCompound("Scale");
                    Vec3f[] vertices = new Vec3f[vertexAmount];
                    Vec2f[] uvs = new Vec2f[vertexAmount];
                    Vec3f[] normals = new Vec3f[vertexAmount];
                    Vec3f translation = new Vec3f(translationTag);
                    Vec4f rotation = new Vec4f(rotationTag);
                    Vec3f scale = new Vec3f(scaleTag);
                    int[] indices = new int[indexAmount];
                    for (Object o2 : surfaceTag.getCompound("Indices").getValues()) {
                        IntTag index = (IntTag) o2;
                        indices[Integer.parseInt(index.getTagName())] = index.getValue();
                    }
                    for (int i = 0; i < vertexAmount; i++) {
                        CompoundTag vertex = surfaceTag.getCompound("Vertices").getCompound(String.valueOf(i));
                        CompoundTag pos = vertex.getCompound("XYZ");
                        CompoundTag uv = vertex.getCompound("UV");
                        CompoundTag norm = vertex.getCompound("Normal");
                        Vec3f vertexPos = new Vec3f(pos.getDouble("x"),pos.getDouble("y"),pos.getDouble("z"));
                        Vec2f vertexUV = new Vec2f(uv.getDouble("x"),uv.getDouble("y"));
                        Vec3f vertexNorm = new Vec3f(norm.getDouble("x"),norm.getDouble("y"),norm.getDouble("z"));
                        vertices[i] = vertexPos;
                        uvs[i] = vertexUV;
                        normals[i] = vertexNorm;
                    }
                    ModelSurface surface = new ModelSurface(vertices,uvs,indices,normals,surfaceTag.getInteger("Texture"),translation,rotation,scale);
                    surfaces.add(surface);
                }
                this.surfaces = surfaces.toArray(new ModelSurface[0]);
                CatalystModels.LOGGER.info(String.format("Model '%s' loaded.",filePath));
				CatalystModels.LOGGER.info(String.format("Model '%s' contains %s surfaces.",filePath,surfaces.size()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
