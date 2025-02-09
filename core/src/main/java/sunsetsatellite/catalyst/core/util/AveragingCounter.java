package sunsetsatellite.catalyst.core.util;

import net.minecraft.core.world.World;
import sunsetsatellite.catalyst.core.util.mixin.interfaces.IAbsoluteWorldTime;

import java.util.Arrays;

public class AveragingCounter {

	private final long defaultValue;
	private final long[] values;
	private long lastUpdatedWorldTime = 0;
	private int currentIndex = 0;
	private boolean dirty = true;
	private double lastAverage = 0;

	public AveragingCounter() {
		this(0, 20);
	}

	public AveragingCounter(long defaultValue, int length) {
		this.defaultValue = defaultValue;
		this.values = new long[length];
		Arrays.fill(values, defaultValue);
	}

	private void update(World world) {
		if (world == null) return;
		long currentWorldTime = ((IAbsoluteWorldTime) world).getAbsoluteWorldTime();
		if (currentWorldTime != lastUpdatedWorldTime) {
			long dif = currentWorldTime - lastUpdatedWorldTime;
			if (dif >= values.length || dif < 0) {
				Arrays.fill(values, defaultValue);
				currentIndex = 0;
			} else {
				currentIndex += (int) dif;
				if (currentIndex > values.length - 1)
					currentIndex -= values.length;
				int index;
				for (int i = 0, n = values.length; i < dif; i++) {
					index = i + currentIndex;
					if (index >= n)
						index -= n;
					values[index] = defaultValue;
				}
			}
			this.lastUpdatedWorldTime = currentWorldTime;
			dirty = true;
		}
	}

	public long getLast(World world) {
		update(world);
		return values[currentIndex];
	}

	public double getAverage(World world) {
		update(world);
		if (!dirty)
			return lastAverage;
		dirty = false;
		return lastAverage = Arrays.stream(values).sum() / (double) (values.length);
	}

	public void increment(World world, long value) {
		update(world);
		values[currentIndex] += value;
	}

	public void set(World world, long value) {
		update(world);
		values[currentIndex] = value;
	}
}
