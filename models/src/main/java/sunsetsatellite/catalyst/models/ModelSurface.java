package sunsetsatellite.catalyst.models;

import sunsetsatellite.catalyst.core.util.Vec2f;
import sunsetsatellite.catalyst.core.util.Vec3f;
import sunsetsatellite.catalyst.core.util.Vec4f;

public class ModelSurface {
    public Vec3f[] vertices;
    public Vec2f[] uvs;
    public int[] indices;
    public Vec3f[] normals;
    public int texture;

    public Vec3f translation;
    public Vec4f rotation;
    public Vec3f scale;

    public ModelSurface(Vec3f[] vertices, Vec2f[] uvs, int[] indices, Vec3f[] normals, int texture, Vec3f translation, Vec4f rotation, Vec3f scale) {
        this.vertices = vertices;
        this.uvs = uvs;
        this.indices = indices;
        this.normals = normals;
        this.texture = texture;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }
}
