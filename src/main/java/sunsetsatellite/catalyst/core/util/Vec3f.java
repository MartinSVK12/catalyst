package sunsetsatellite.catalyst.core.util;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.util.helper.MathHelper;

public class Vec3f {
    public double x;
    public double y;
    public double z;

    public Vec3f(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    };

    public Vec3f(){
        this.x = this.y = this.z = 0;
    }

    public Vec3f(double size){
        this.x = this.y = this.z = size;
    }

    public Vec3f(CompoundTag tag){
        readFromNBT(tag);
    }

    public double distanceTo(Vec3f vec3f) {
        double d = vec3f.x - this.x;
        double d1 = vec3f.y - this.y;
        double d2 = vec3f.z - this.z;
        return MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
    }

    public Vec3f add(double value){
        this.x += value;
        this.y += value;
        this.z += value;
        return this;
    }

    public Vec3f subtract(double value){
        this.x -= value;
        this.y -= value;
        this.z -= value;
        return this;
    }

    public Vec3f divide(double value){
        this.x /= value;
        this.y /= value;
        this.z /= value;
        return this;
    }

    public Vec3f multiply(double value){
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    public Vec3f add(Vec3f value){
        this.x += value.x;
        this.y += value.y;
        this.z += value.z;
        return this;
    }

    public Vec3f subtract(Vec3f value){
        this.x -= value.x;
        this.y -= value.y;
        this.z -= value.z;
        return this;
    }

    public Vec3f divide(Vec3f value){
        this.x /= value.x;
        this.y /= value.y;
        this.z /= value.z;
        return this;
    }

    public Vec3f multiply(Vec3f value){
        this.x *= value.x;
        this.y *= value.y;
        this.z *= value.z;
        return this;
    }

    public void writeToNBT(CompoundTag tag){
        tag.putDouble("x",this.x);
        tag.putDouble("y",this.y);
        tag.putDouble("z",this.z);
    }

    public void readFromNBT(CompoundTag tag){
        this.x = tag.getDouble("x");
        this.y = tag.getDouble("y");
        this.z = tag.getDouble("z");
    }

    public Vec3f copy(){
        return new Vec3f(this.x,this.y,this.z);
    }

    public Vec3f rotate(Vec3f origin, Direction direction){
        Vec3f pos;
        switch (direction){
            case X_POS:
                pos = new Vec3f(this.z + origin.x, this.y + origin.y, this.x + origin.z);
                break;
            case X_NEG:
                pos = new Vec3f(-this.z + origin.x, this.y + origin.y, -this.x + origin.z);
                break;
            case Z_NEG:
                pos = new Vec3f(-this.x + origin.x, this.y + origin.y, -this.z + origin.z);
                break;
            case Y_NEG:
                pos = new Vec3f(this.x + origin.x, -this.y + origin.y, this.z + origin.z);
                break;
            default:
                pos = new Vec3f(this.x + origin.x, this.y + origin.y, this.z + origin.z);
                break;
        }
        return pos;
    }

    public Vec3f rotate(Direction direction){
        Vec3f pos;
        switch (direction){
            case X_POS:
                pos = new Vec3f(this.z, this.y, this.x);
                break;
            case X_NEG:
                pos = new Vec3f(-this.z, this.y, -this.x);
                break;
            case Z_NEG:
                pos = new Vec3f(-this.x, this.y, -this.z);
                break;
            case Y_NEG:
                pos = new Vec3f(this.x, -this.y, this.z);
                break;
            default:
                pos = new Vec3f(this.x, this.y, this.z);
                break;
        }
        return pos;
    }

	public Vec3f lerp(Vec3f to, double amount){
		double lerpX = x + (to.x - x) * amount;
		double lerpY = y + (to.y - x) * amount;
		double lerpZ = z + (to.z - x) * amount;
		return new Vec3f(lerpX, lerpY, lerpZ);
	}


    @Override
    public String toString() {
        return "Vec3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec3f vec3I = (Vec3f) o;

        if (x != vec3I.x) return false;
        if (y != vec3I.y) return false;
        return z == vec3I.z;
    }

    @Override
    public int hashCode() {
        int result = (int) x;
        result = (int) (31 * result + y);
        result = (int) (31 * result + z);
        return result;
    }
}
