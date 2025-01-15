package sunsetsatellite.catalyst;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.fx.EntityFX;
import net.minecraft.core.Global;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sunsetsatellite.catalyst.core.util.*;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import turniplabs.halplibe.helper.NetworkHelper;

import java.util.*;


public class Catalyst implements ModInitializer {
    public static final String MOD_ID = "catalyst";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Registry<MpGuiEntry> GUIS = new Registry<>();

	public static final Signal<BlockChangeInfo> TILE_ENTITY_BLOCK_CHANGED_SIGNAL = new Signal<>();
	public static final Signal<BlockChangeInfo> ANY_BLOCK_CHANGED_SIGNAL = new Signal<>();
	public static final Signal<World> DIMENSION_LOAD_SIGNAL = new Signal<>();
	public static final Signal<World> DIMENSION_SAVE_SIGNAL = new Signal<>();

	static {
		NetworkHelper.register(PacketOpenGui.class,false,true);
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

	public static @UnmodifiableView List<ItemStack> collectStacks(IInventory inv){
		if(inv == null) return Collections.emptyList();
		ArrayList<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			stacks.add(i,inv.getStackInSlot(i));
		}

		return Collections.unmodifiableList(stacks);
	}

	public static @UnmodifiableView List<ItemStack> collectAndCondenseStacks(IInventory inv){
		return condenseItemList(collectStacks(inv));
	}

	@Override
    public void onInitialize() {
		TILE_ENTITY_BLOCK_CHANGED_SIGNAL.connect(NetworkManager.BlockChangeListener.INSTANCE);
		DIMENSION_LOAD_SIGNAL.connect(NetworkManager.LoadSaveListener.INSTANCE);
		DIMENSION_SAVE_SIGNAL.connect(NetworkManager.LoadSaveListener.INSTANCE);
        LOGGER.info("Catalyst initialized.");
    }

	public static void displayGui(EntityPlayer player, IInventory inventory, ItemStack stack){
		((IMpGui)player).displayCustomGUI(inventory,stack);
	}


	public static void displayGui(EntityPlayer player, TileEntity tileEntity, String id){
		((IMpGui)player).displayCustomGUI(tileEntity, id);
	}

	public static Pair<Direction,BlockSection> getBlockSurfaceClickPosition(World world, EntityPlayer player, HitResult hit){
		if (!Global.isServer) {
			if(hit.hitType == HitResult.HitType.TILE){
				Direction dir = Direction.getDirectionFromSide(hit.side.getId());
				Vec3f vec3f = new Vec3f(hit.location.xCoord,hit.location.yCoord,hit.location.zCoord);
				Vec2f clickPosition = vec3f.subtract(vec3f.copy().floor()).abs().set(hit.side.getAxis(),0).toVec2f();
				switch (hit.side) {
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
				}
				return Pair.of(dir,BlockSection.getClosestBlockSection(clickPosition));
			}
		}
		return null;
	}

	public static Side calculatePlayerFacing(float rotation) {
		return Side.values()[(2 + ((MathHelper.floor_double((double) ((rotation * 4F) / 360F) + 0.5D) + 2) & 3))];
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

	public static void spawnParticle(EntityFX particle){
		if (Minecraft.getMinecraft(Minecraft.class) == null || Minecraft.getMinecraft(Minecraft.class).thePlayer == null || Minecraft.getMinecraft(Minecraft.class).effectRenderer == null)
			return;
		double d6 = Minecraft.getMinecraft(Minecraft.class).thePlayer.x - particle.x;
		double d7 = Minecraft.getMinecraft(Minecraft.class).thePlayer.y - particle.y;
		double d8 = Minecraft.getMinecraft(Minecraft.class).thePlayer.z - particle.z;
		double d9 = 16.0D;
		if (d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9)
			return;
		Minecraft.getMinecraft(Minecraft.class).effectRenderer.addEffect(particle);
	}

}
