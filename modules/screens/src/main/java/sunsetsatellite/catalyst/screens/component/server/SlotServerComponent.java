package sunsetsatellite.catalyst.screens.component.server;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import sunsetsatellite.catalyst.screens.util.SlotType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SlotServerComponent extends ServerComponent {
	private int index;
	private int x;
	private int y;
	private SlotType type;

	public SlotServerComponent(int index, int x, int y, SlotType type) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public SlotServerComponent(CompoundTag tag) {
		readFromNbt(tag);
	}

	public int index() {
		return index;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public SlotType type() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (SlotServerComponent) obj;
		return this.index == that.index &&
			this.x == that.x &&
			this.y == that.y &&
			Objects.equals(this.type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(index, x, y, type);
	}

	@Override
	public String toString() {
		return "ServerSlotComponent[" +
			"index=" + index + ", " +
			"x=" + x + ", " +
			"y=" + y + ", " +
			"type=" + type + ']';
	}

	public static List<SlotServerComponent> fromNbt(CompoundTag tag) {
		List<SlotServerComponent> list = new ArrayList<>();
		for (Tag<?> value : tag.getValues()) {
			if(value instanceof CompoundTag nbt){
				if(nbt.getString("type").equals("slot")){
					nbt = nbt.getCompound("data");
					list.add(new SlotServerComponent(nbt));
				}
			}
		}
		return list;
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putInt("index", index);
		tag.putInt("x", x);
		tag.putInt("y", y);
		tag.putInt("type", type.ordinal());
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		index = tag.getInteger("index");
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		type = SlotType.values()[tag.getInteger("type")];
	}
}
