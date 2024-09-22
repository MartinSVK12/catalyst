package sunsetsatellite.catalyst.multipart.api.impl.dragonfly;


import com.google.gson.JsonArray;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.util.helper.Axis;
import sunsetsatellite.catalyst.multipart.api.impl.dragonfly.vector.Vector3f;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Random;

public class Utilities {
	public static final float COMPARE_CONST = 0.001f;
	public static String writeFields(Class<?> clazz){
		Field[] fields = clazz.getFields();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fields.length; i++)  {
			Field field = fields[i];
			builder.append(field.getName()).append(": ").append(field.getClass().cast(field));
			if (i < fields.length-1){
				builder.append("\n");
			}
		}
		return builder.toString();
	}
	public static String tabBlock(String string, int tabAmount){
		StringBuilder builder = new StringBuilder();
		for (String line: string.split("\n")) {
			builder.append(tabString(tabAmount)).append(line).append("\n");
		}
		return builder.toString();
	}
	public static String tabString(int tabAmount){
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tabAmount; i++) {
			builder.append("\t");
		}
		return builder.toString();
	}
	public static boolean equalFloats(float a, float b){
		return Math.abs(Float.compare(a, b)) < COMPARE_CONST;
	}
	public static Vector3f rotatePoint(Vector3f point, Vector3f origin, Axis axis, float angle){
		switch (axis){
			case X:
				return point.rotateAroundX(origin, -angle);
			case Y:
				return point.rotateAroundY(origin, angle);
			case Z:
				return point.rotateAroundZ(origin, -angle);
		}
		throw new RuntimeException("Axis " + axis + " Is not 'X', 'Y', or 'Z'!");
	}

	/**
	 * Tries to load resource from multiple classes, if they all fail it throws an exception
	 */
	public static InputStream getResourceAsStream(String path){
		try {
			Class.forName("net.minecraft.client.Minecraft");
			try {
				return Objects.requireNonNull(Minecraft.getMinecraft(Utilities.class).texturePackList.getResourceAsStream(path));
			} catch (Exception ignored){}
		} catch (Exception ignored) {}
		try {
			return Objects.requireNonNull(DataLoader.class.getResourceAsStream(path));
		} catch (Exception ignored){}
		try {
			return Objects.requireNonNull(Utilities.class.getResourceAsStream(path));
		} catch (Exception ignored){}
		try {
			return Objects.requireNonNull(FabricLoader.getInstance().getClass().getResourceAsStream(path));
		} catch (Exception ignored){}
		try {
			return Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
		} catch (Exception ignored){}
		throw new RuntimeException("Resource at '" + path + "' returned null! Does this file exist?");
	}
	public static float[] floatArrFromJsonArr(JsonArray arr){
		float[] result = new float[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			result[i] = arr.get(i).getAsFloat();
		}
		return result;
	}
	public static double[] doubleArrFromJsonArr(JsonArray arr){
		double[] result = new double[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			result[i] = arr.get(i).getAsDouble();
		}
		return result;
	}

	public static boolean equalFloat(double a, double b) {
		return Math.abs(a - b) < 1e-9;
	}
	public static Random getRandomFromPos(int x, int y, int z){
		Random rand = new Random(0);
		long l1 = rand.nextLong() / 2L * 2L + 1L;
		long l2 = rand.nextLong() / 2L * 2L + 1L;
		long l3 = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed((long)x * l1 + (long)z * l2 + y * l3);
		return rand;
	}
}
