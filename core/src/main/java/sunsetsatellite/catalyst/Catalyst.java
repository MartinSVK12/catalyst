package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.BlockChangeInfo;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Signal;
import sunsetsatellite.catalyst.core.util.mp.IMpGui;
import sunsetsatellite.catalyst.core.util.mp.MpGuiEntry;
import sunsetsatellite.catalyst.core.util.mp.PacketOpenGui;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import sunsetsatellite.catalyst.core.util.section.BlockSection;
import sunsetsatellite.catalyst.core.util.vector.Vec2f;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.*;
import java.util.function.BiFunction;

public class Catalyst implements ModInitializer {
	public static final String MOD_ID = "catalyst-core";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Registry<MpGuiEntry> GUIS = new Registry<>();

	public static final Signal<BlockChangeInfo> TILE_ENTITY_BLOCK_CHANGED_SIGNAL = new Signal<>();
	public static final Signal<BlockChangeInfo> ANY_BLOCK_CHANGED_SIGNAL = new Signal<>();
	public static final Signal<World> DIMENSION_LOAD_SIGNAL = new Signal<>();
	public static final Signal<World> DIMENSION_SAVE_SIGNAL = new Signal<>();

	@Override
	public void onInitialize() {
		NetworkHandler.registerNetworkMessage(PacketOpenGui::new);

		connectSignals();
		LOGGER.info("Catalyst: Core initialized.");
	}

	public void connectSignals() {
		TILE_ENTITY_BLOCK_CHANGED_SIGNAL.connect(NetworkManager.BlockChangeListener.INSTANCE);
		DIMENSION_LOAD_SIGNAL.connect(NetworkManager.LoadSaveListener.INSTANCE);
		DIMENSION_SAVE_SIGNAL.connect(NetworkManager.LoadSaveListener.INSTANCE);
	}

	public static <K,V> Map<K,V> mapOf(K[] keys, V[] values){
		if(keys.length != values.length){
			throw new IllegalArgumentException("Arrays differ in size!");
		}
		HashMap<K,V> map = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i],values[i]);
		}
		return map;
	}

	public static <T,V> T[] arrayFill(T[] array,V value){
		Arrays.fill(array,value);
		return array;
	}

	public static double map(double valueCoord,
							 double startCoord1, double endCoord1,
							 double startCoord2, double endCoord2) {

		final double EPSILON = 1e-12;
		if (Math.abs(endCoord1 - startCoord1) < EPSILON) {
			throw new ArithmeticException("Division by 0");
		}

		double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
		return ratio * (valueCoord - startCoord1) + startCoord2;
	}

	@SafeVarargs
	public static <T> List<T> listOf(T... values){
		return new ArrayList<>(Arrays.asList(values));
	}

	@SafeVarargs
	public static <T> Set<T> setOf(T... values){
		return new HashSet<>(Arrays.asList(values));
	}

	public static <T,U> List<Pair<T,U>> zip(List<T> first, List<U> second){
		List<Pair<T,U>> list = new ArrayList<>();
		List<?> shortest = first.size() < second.size() ? first : second;
		for (int i = 0; i < shortest.size(); i++) {
			list.add(Pair.of(first.get(i),second.get(i)));
		}
		return list;
	}

	/**
	 * @param values The values to be checked
	 * @return Returns the smallest of <code>values</code>
	 */
	public static long multiMin(long... values){
		long min = Long.MAX_VALUE;
		for (long value : values) {
			if(value < min){
				min = value;
			}
		}
		return min;
	}

	public static ArrayList<ItemStack> condenseItemList(List<ItemStack> list) {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		for (ItemStack stack : list) {
			if (stack != null) {
				boolean found = false;
				for (ItemStack S : stacks) {
					if (S.isItemEqual(stack) && (S.getData().equals(stack.getData()))) {
						S.stackSize += stack.stackSize;
						found = true;
					}
				}
				if(!found) stacks.add(stack.copy());
			}
		}
		return stacks;
	}

	public static @UnmodifiableView List<ItemStack> collectStacks(Container inv){
		if(inv == null) return Collections.emptyList();
		ArrayList<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inv.getContainerSize(); i++) {
			stacks.add(i,inv.getItem(i));
		}

		return Collections.unmodifiableList(stacks);
	}

	public static @UnmodifiableView List<ItemStack> collectAndCondenseStacks(Container inv){
		return condenseItemList(collectStacks(inv));
	}

	public static Pair<Direction, BlockSection> getBlockSurfaceClickPosition(World world, Player player, Side side, Vec2f clickPosition){
		if (!Global.isServer) {
			Direction dir = Direction.getDirectionFromSide(side.getId());
			/*switch (side) {
				case NORTH:
					clickPosition.x = 1-clickPosition.x;
					break;
				case EAST: {
					double temp1 = clickPosition.y;
					double temp2 = clickPosition.x;
					clickPosition.x = 1-temp1;
					clickPosition.y = temp2;
					break;
				}
				case SOUTH:
					//no change needed
					break;
				case WEST: {
					double temp1 = clickPosition.y;
					double temp2 = clickPosition.x;
					clickPosition.x = temp1;
					clickPosition.y = temp2;
					break;
				}
			}*/
			return Pair.of(dir,BlockSection.getClosestBlockSection(clickPosition));
		}
		return null;
	}

	public static Side calculatePlayerFacing(float rotation) {
		return Side.values()[(2 + ((MathHelper.floor((double) ((rotation * 4F) / 360F) + 0.5D) + 2) & 3))];
	}

	public static void displayGui(Player player, Container inventory, int slotIndex, boolean isArmor, String id){
		((IMpGui)player).catalyst$displayCustomGUI(inventory, slotIndex, isArmor, id);
	}


	public static void displayGui(Player player, TileEntity tileEntity, String id){
		((IMpGui)player).catalyst$displayCustomGUI(tileEntity, id);
	}

	public static <T> T blockLogic(Block<? extends BlockLogic> block, Class<T> clazz){
		if(Block.hasLogicClass(block, clazz)) return (T) block.getLogic();
		else return null;
	}

	public static <T> T blockLogic(int id, Class<T> clazz){
		if(Block.hasLogicClass(Blocks.blocksList[id], clazz)) return (T) Blocks.blocksList[id].getLogic();
		else return null;
	}

	public static <T> boolean listContains(List<T> list, T o, BiFunction<T,T,Boolean> equals){
		for (T obj : list) {
			if(equals.apply(o,obj)){
				return true;
			}
		}
		return false;
	}

	public static byte[] HSBtoRGB(float hue, float saturation, float brightness) {
		byte red = 0;
		byte green = 0;
		byte blue = 0;
		if (saturation == 0.0F) {
			red = green = blue = (byte) (brightness * 255F + 0.5F);
		} else {
			float f3 = (hue - (float) Math.floor(hue)) * 6F;
			float f4 = f3 - (float) Math.floor(f3);
			float f5 = brightness * (1.0F - saturation);
			float f6 = brightness * (1.0F - saturation * f4);
			float f7 = brightness * (1.0F - saturation * (1.0F - f4));
			switch ((int) f3) {
				case 0 :
					red = (byte) (brightness * 255F + 0.5F);
					green = (byte) (f7 * 255F + 0.5F);
					blue = (byte) (f5 * 255F + 0.5F);
					break;
				case 1 :
					red = (byte) (f6 * 255F + 0.5F);
					green = (byte) (brightness * 255F + 0.5F);
					blue = (byte) (f5 * 255F + 0.5F);
					break;
				case 2 :
					red = (byte) (f5 * 255F + 0.5F);
					green = (byte) (brightness * 255F + 0.5F);
					blue = (byte) (f7 * 255F + 0.5F);
					break;
				case 3 :
					red = (byte) (f5 * 255F + 0.5F);
					green = (byte) (f6 * 255F + 0.5F);
					blue = (byte) (brightness * 255F + 0.5F);
					break;
				case 4 :
					red = (byte) (f7 * 255F + 0.5F);
					green = (byte) (f5 * 255F + 0.5F);
					blue = (byte) (brightness * 255F + 0.5F);
					break;
				case 5 :
					red = (byte) (brightness * 255F + 0.5F);
					green = (byte) (f5 * 255F + 0.5F);
					blue = (byte) (f6 * 255F + 0.5F);
					break;
			}
		}
		return new byte[]{red,green,blue};
	}
}
