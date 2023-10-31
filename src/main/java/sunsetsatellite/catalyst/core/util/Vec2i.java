package sunsetsatellite.catalyst.core.util;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.util.helper.MathHelper;

public class Vec2i {
    public int x;
    public int y;

    public Vec2i(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vec2i(){
        this.x = this.y = 0;
    }

    public Vec2i(int size){
        this.x = this.y = size;
    }

    public Vec2i(CompoundTag tag){
        readFromNBT(tag);
    }

    public double distanceTo(Vec2i vec3f) {
        int d = vec3f.x - this.x;
        int d1 = vec3f.y - this.y;
        return MathHelper.sqrt_double(d * d + d1 * d1);
    }

    public Vec2i add(int value){
        this.x += value;
        this.y += value;
        return this;
    }

    public Vec2i subtract(int value){
        this.x -= value;
        this.y -= value;
        return this;
    }

    public Vec2i divide(int value){
        this.x /= value;
        this.y /= value;
        return this;
    }

    public Vec2i multiply(int value){
        this.x *= value;
        this.y *= value;
        return this;
    }

    public Vec2i add(Vec2i value){
        this.x += value.x;
        this.y += value.y;
        return this;
    }

    public Vec2i subtract(Vec2i value){
        this.x -= value.x;
        this.y -= value.y;
        return this;
    }

    public Vec2i divide(Vec2i value){
        this.x /= value.x;
        this.y /= value.y;
        return this;
    }

    public Vec2i multiply(Vec2i value){
        this.x *= value.x;
        this.y *= value.y;
        return this;
    }

    public void writeToNBT(CompoundTag tag){
        tag.putInt("x",this.x);
        tag.putInt("y",this.y);
    }

    public void readFromNBT(CompoundTag tag){
        this.x = tag.getInteger("x");
        this.y = tag.getInteger("y");
    }

    public Vec2i copy(){
        return new Vec2i(this.x,this.y);
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


    @Override
    public String toString() {
        return "Vec2i{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec2i vec2i = (Vec2i) o;

        if (x != vec2i.x) return false;
        return y == vec2i.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
