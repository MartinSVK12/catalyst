package sunsetsatellite.catalyst.core.util;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.util.helper.MathHelper;

public class Vec2f {
    public double x;
    public double y;

    public Vec2f(double x, double y){
        this.x = x;
        this.y = y;
    }

	public Vec2f(){
        this.x = this.y = 0;
    }

    public Vec2f(double size){
        this.x = this.y = size;
    }

    public Vec2f(CompoundTag tag){
        readFromNBT(tag);
    }

    public double distanceTo(Vec2f vec3f) {
        double d = vec3f.x - this.x;
        double d1 = vec3f.y - this.y;
        return MathHelper.sqrt_double(d * d + d1 * d1);
    }

    public Vec2f add(double value){
        this.x += value;
        this.y += value;
        return this;
    }

    public Vec2f subtract(double value){
        this.x -= value;
        this.y -= value;
        return this;
    }

    public Vec2f divide(double value){
        this.x /= value;
        this.y /= value;
        return this;
    }

    public Vec2f multiply(double value){
        this.x *= value;
        this.y *= value;
        return this;
    }

    public Vec2f add(Vec2f value){
        this.x += value.x;
        this.y += value.y;
        return this;
    }

    public Vec2f subtract(Vec2f value){
        this.x -= value.x;
        this.y -= value.y;
        return this;
    }

    public Vec2f divide(Vec2f value){
        this.x /= value.x;
        this.y /= value.y;
        return this;
    }

    public Vec2f multiply(Vec2f value){
        this.x *= value.x;
        this.y *= value.y;
        return this;
    }

    public void writeToNBT(CompoundTag tag){
        tag.putDouble("x",this.x);
        tag.putDouble("y",this.y);
    }

    public void readFromNBT(CompoundTag tag){
        this.x = tag.getDouble("x");
        this.y = tag.getDouble("y");
    }

    public Vec2f copy(){
        return new Vec2f(this.x,this.y);
    }

    /*public Vec2f rotate(Vec2f origin, Direction direction){
        Vec2f pos;
        switch (direction){
            case X_POS:
                pos = new Vec2f(this.z + origin.x, this.y + origin.y, this.x + origin.z);
                break;
            case X_NEG:
                pos = new Vec2f(-this.z + origin.x, this.y + origin.y, -this.x + origin.z);
                break;
            case Z_NEG:
                pos = new Vec2f(-this.x + origin.x, this.y + origin.y, -this.z + origin.z);
                break;
            case Y_NEG:
                pos = new Vec2f(this.x + origin.x, -this.y + origin.y, this.z + origin.z);
                break;
            default:
                pos = new Vec2f(this.x + origin.x, this.y + origin.y, this.z + origin.z);
                break;
        }
        return pos;
    }

    public Vec2f rotate(Direction direction){
        Vec2f pos;
        switch (direction){
            case X_POS:
                pos = new Vec2f(this.z, this.y, this.x);
                break;
            case X_NEG:
                pos = new Vec2f(-this.z, this.y, -this.x);
                break;
            case Z_NEG:
                pos = new Vec2f(-this.x, this.y, -this.z);
                break;
            case Y_NEG:
                pos = new Vec2f(this.x, -this.y, this.z);
                break;
            default:
                pos = new Vec2f(this.x, this.y, this.z);
                break;
        }
        return pos;
    }*/

	public Vec2f abs(){
		this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
		return this;
	}

    @Override
    public String toString() {
        return "Vec2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec2f vec2f = (Vec2f) o;

        if (x != vec2f.x) return false;
        return y == vec2f.y;
    }

    @Override
    public int hashCode() {
        int result = (int) x;
        result = (int) (31 * result + y);
        return result;
    }
}
