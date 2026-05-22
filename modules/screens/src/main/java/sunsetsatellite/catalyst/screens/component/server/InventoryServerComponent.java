package sunsetsatellite.catalyst.screens.component.server;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;

import java.util.ArrayList;
import java.util.List;

public class InventoryServerComponent extends ServerComponent {
	private int x;
	private int y;

	public InventoryServerComponent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public InventoryServerComponent(CompoundTag tag) {
		readFromNbt(tag);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putInt("x", x);
		tag.putInt("y", y);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		x = tag.getInteger("x");
		y = tag.getInteger("y");
	}

	public static List<InventoryServerComponent> fromNbt(CompoundTag tag) {
		List<InventoryServerComponent> list = new ArrayList<>();
		for (Tag<?> value : tag.getValues()) {
			if(value instanceof CompoundTag nbt){
				if(nbt.getString("type").equals("inventory")){
					nbt = nbt.getCompound("data");
					list.add(new InventoryServerComponent(nbt));
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
}
