package sunsetsatellite.catalyst.util;

import com.mojang.nbt.CompoundTag;

public class Vec4f {
    public double x;
    public double y;
    public double z;
    public double w;

    public Vec4f(double x, double y, double z, double w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    };

    public Vec4f(){
        this.x = this.y = this.z = this.w = 0;
    }

    public Vec4f(double size){
        this.x = this.y = this.z = this.w = size;
    }

    public Vec4f(CompoundTag tag){
        readFromNBT(tag);
    }


    public double distanceTo(Vec4f vec3f) {
        double d = vec3f.x - this.x;
        double d1 = vec3f.y - this.y;
        double d2 = vec3f.z - this.z;
        double d3 = vec3f.w - this.w;
        return Math.sqrt(d * d + d1 * d1 + d2 * d2 + d3 * d3);
    }

    public Vec4f add(double value){
        this.x += value;
        this.y += value;
        this.z += value;
        this.w += value;
        return this;
    }

    public Vec4f subtract(double value){
        this.x -= value;
        this.y -= value;
        this.z -= value;
        this.w -= value;
        return this;
    }

    public Vec4f divide(double value){
        this.x /= value;
        this.y /= value;
        this.z /= value;
        this.w /= value;
        return this;
    }

    public Vec4f multiply(double value){
        this.x *= value;
        this.y *= value;
        this.z *= value;
        this.w *= value;
        return this;
    }

    public Vec4f add(Vec4f value){
        this.x += value.x;
        this.y += value.y;
        this.z += value.z;
        this.w += value.w;
        return this;
    }

    public Vec4f subtract(Vec4f value){
        this.x -= value.x;
        this.y -= value.y;
        this.z -= value.z;
        this.w -= value.w;
        return this;
    }

    public Vec4f divide(Vec4f value){
        this.x /= value.x;
        this.y /= value.y;
        this.z /= value.z;
        this.w /= value.w;
        return this;
    }

    public Vec4f multiply(Vec4f value){
        this.x *= value.x;
        this.y *= value.y;
        this.z *= value.z;
        this.w *= value.w;
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag tag){
        tag.putDouble("x",this.x);
        tag.putDouble("y",this.y);
        tag.putDouble("z",this.z);
        tag.putDouble("w",this.w);
        return tag;
    }

    public void readFromNBT(CompoundTag tag){
        this.x = tag.getDouble("x");
        this.y = tag.getDouble("y");
        this.z = tag.getDouble("z");
        this.w = tag.getDouble("w");
    }

    public Vec4f copy(){
        return new Vec4f(this.x,this.y,this.z,this.w);
    }

    @Override
    public String toString() {
        return "Vec4f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec4f vec = (Vec4f) o;

        if (x != vec.x) return false;
        if (y != vec.y) return false;
        if (z != vec.w) return false;
        return w == vec.w;
    }

    @Override
    public int hashCode() {
        int result = (int) x;
        result = (int) (31 * result + y);
        result = (int) (31 * result + z);
        result = (int) (31 * result + w);
        return result;
    }
}
