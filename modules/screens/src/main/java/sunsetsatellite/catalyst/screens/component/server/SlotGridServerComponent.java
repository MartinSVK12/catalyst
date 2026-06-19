package sunsetsatellite.catalyst.screens.component.server;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import sunsetsatellite.catalyst.screens.util.SlotType;

import java.util.ArrayList;
import java.util.List;

public class SlotGridServerComponent extends ServerComponent {
	private int x;
	private int y;
	private int rows;
	private int columns;
	private SlotType type;

	public SlotGridServerComponent(int x, int y, int rows, int columns, SlotType type) {
		this.x = x;
		this.y = y;
		this.rows = rows;
		this.columns = columns;
		this.type = type;
	}

	public SlotGridServerComponent(CompoundTag tag) {
		readFromNbt(tag);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putInt("x", x);
		tag.putInt("y", y);
		tag.putInt("rows", rows);
		tag.putInt("columns", columns);
		tag.putInt("type", type.ordinal());
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		rows = tag.getInteger("rows");
		columns = tag.getInteger("columns");
		type = SlotType.values()[tag.getInteger("type")];
	}

	public static List<SlotGridServerComponent> fromNbt(CompoundTag tag) {
		List<SlotGridServerComponent> list = new ArrayList<>();
		for (Tag<?> value : tag.getValues()) {
			if(value instanceof CompoundTag nbt){
				if(nbt.getString("type").equals("slotGrid")){
					nbt = nbt.getCompound("data");
					list.add(new SlotGridServerComponent(nbt));
				}
			}
		}
		return list;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int rows() {
		return rows;
	}

	public int columns() {
		return columns;
	}

	public SlotType type() {
		return type;
	}
}
