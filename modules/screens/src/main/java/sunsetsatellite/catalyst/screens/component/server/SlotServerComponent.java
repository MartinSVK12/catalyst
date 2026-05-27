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
	private int xSize;
	private int ySize;
	private SlotType type;

	public SlotServerComponent(int index, int x, int y, int xSize, int ySize, SlotType type) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
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

	public int xSize() {
		return xSize;
	}

	public int ySize() {
		return ySize;
	}


	public SlotType type() {
		return type;
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
		tag.putInt("xSize", xSize);
		tag.putInt("ySize", ySize);
		tag.putInt("type", type.ordinal());
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		index = tag.getInteger("index");
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		xSize = tag.getInteger("xSize");
		ySize = tag.getInteger("ySize");
		type = SlotType.values()[tag.getInteger("type")];
	}
}
