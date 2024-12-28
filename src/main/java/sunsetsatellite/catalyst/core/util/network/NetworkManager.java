package sunsetsatellite.catalyst.core.util.network;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.BlockChangeInfo;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Signal;
import sunsetsatellite.catalyst.core.util.Vec3i;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Global singleton that manages saving/loading network data, removing/adding blocks from/to networks, merging similar networks together,
 * and splitting disconnected parts of a network.
 */
public class NetworkManager {

	private static final Map<Integer, Set<Network>> NETS = new HashMap<>();
	private static final AtomicInteger ID_PROVIDER = new AtomicInteger(0);

	private NetworkManager() {}

	public static int getNetID(World world, int x, int y, int z) {
		Network net = getNet(world, x, y, z);
		return net == null ? -1 : net.hashCode();
	}


	public static class BlockChangeListener implements Signal.Listener<BlockChangeInfo> {
		public static final Signal.Listener<BlockChangeInfo> INSTANCE = new BlockChangeListener();

		@Override
		public void signalEmitted(Signal<BlockChangeInfo> signal, BlockChangeInfo blockChanged) {
			if (signal != Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL) {
				return;
			}
			if(blockChanged.id == 0){
				removeBlock(blockChanged);
			} else {
				addBlock(blockChanged);
			}
		}
	}

	public static class LoadSaveListener implements Signal.Listener<World> {
		public static final Signal.Listener<World> INSTANCE = new LoadSaveListener();

		@Override
		public void signalEmitted(Signal<World> signal, World world) {
			if(signal == Catalyst.DIMENSION_LOAD_SIGNAL){
				File file = world.getSaveHandler().getDataFile("networks_"+world.dimension.id);
				if (file == null) return;
				if (file.exists()) {
					try {
						CompoundTag tag = NbtIo.readCompressed(Files.newInputStream(file.toPath()));
						NetworkManager.netsFromTag(world, tag);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (signal == Catalyst.DIMENSION_SAVE_SIGNAL) {
				try {
					File file = world.getSaveHandler().getDataFile("networks_"+world.dimension.id);
					if (file == null) return;
					CompoundTag tag = new CompoundTag();
					try {
						tag = NbtIo.readCompressed(Files.newInputStream(file.toPath()));
					} catch (NoSuchFileException e){
						Catalyst.LOGGER.info("Creating new networks file for dimension {}!",world.dimension.id);
					}
					NetworkManager.netsToTag(world, tag);
					NbtIo.writeCompressed(tag, Files.newOutputStream(file.toPath()));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static void addBlock(BlockChangeInfo blockChanged) {
		int x = blockChanged.pos.x;
		int y = blockChanged.pos.y;
		int z = blockChanged.pos.z;
		World world = blockChanged.world;

		if(!canBeNet(Block.blocksList[blockChanged.id])) {
			return;
		}

		NetworkComponent component = (NetworkComponent) Block.blocksList[blockChanged.id];

		Set<Network> nets = NETS.computeIfAbsent(world.dimension.id, i -> new HashSet<>());

		Set<Network> sideNets = new HashSet<>();
		for (Network net: nets) {
			for (Vec3i offset: Direction.getVecs()) {
				int px = x + offset.x;
				int py = y + offset.y;
				int pz = z + offset.z;
				if (net.existsOnPos(px, py, pz)) {
					sideNets.add(net);
				}
			}
		}

		Network net = null;
		int size = sideNets.size();
		//no nets around, create one
		if (size == 0) {
			net = new Network(world,component.getType());
			net.addBlock(x, y, z);
			for (Vec3i offset: Direction.getVecs()) {
				int px = x + offset.x;
				int py = y + offset.y;
				int pz = z + offset.z;
				if (canBeNet(world, px, py, pz)) {
					net.addBlock(px, py, pz);
				}
			}
			if (net.getSize() > 1) {
				nets.add(net);
			}
		}
		else if (size == 1) {
			Network potentialNet = sideNets.stream().findAny().get();
			if(potentialNet.isOfSameType(component)){
				potentialNet.addBlock(x, y, z);
				net = potentialNet;
			}
		}
		else { //multiple nets around
			Network[] netsArray = sideNets.toArray(new Network[size]);
			Network main = null;
			for (Network network : netsArray) {
				if(network.isOfSameType(component)){
					main = network;
					main.addBlock(x, y, z);
					for (Network otherNet : netsArray) {
						if(otherNet == main){
							continue;
						}
						if(otherNet.isOfSameType(main)){
							main.mergeNetwork(otherNet);
							nets.remove(otherNet);
						}
					}
					net = main;
					break;
				}
			}
		}

		if(net == null && getNet(world, x, y, z) == null) {
			net = new Network(world,component.getType());
			net.addBlock(x, y, z);
			for (Vec3i offset: Direction.getVecs()) {
				int px = x + offset.x;
				int py = y + offset.y;
				int pz = z + offset.z;
				if (canBeNet(world, px, py, pz)) {
					net.addBlock(px, py, pz);
				}
			}
			if (net.getSize() > 1) {
				nets.add(net);
			}
		}

		//add surrounding blocks to net if type matches
		for (Vec3i offset : Direction.getVecs()) {
			int px = x + offset.x;
			int py = y + offset.y;
			int pz = z + offset.z;
			if (canBeNet(world, px, py, pz) && getNet(world, px, py, pz) == null && net != null) {
				NetworkComponent sideComponent = (NetworkComponent) world.getBlock(px,py,pz);
				if(net.isOfSameType(sideComponent)){
					net.addBlock(px, py, pz);
				}
			}
		}
	}

	public static void removeBlock(BlockChangeInfo blockChanged) {
		int x = blockChanged.pos.x;
		int y = blockChanged.pos.y;
		int z = blockChanged.pos.z;
		World world = blockChanged.world;
		Set<Network> nets = NETS.get(world.dimension.id);

		if (nets == null) {
			return;
		}

		Network target = null;
		for (Network net: nets) {
			if (net.existsOnPos(x, y, z)) {
				target = net;
				break;
			}
		}

		if (target != null) {
			List<? extends Network> sideNets = target.removeBlock(x, y, z);
			if (sideNets != null) {
				nets.remove(target);
				nets.addAll(sideNets);
			}
			else if (target.getSize() < 2) {
				nets.remove(target);
			}
		}
	}

	public static int getUID() {
		return ID_PROVIDER.getAndIncrement();
	}

	public static void netsToTag(World world, CompoundTag root) {
		Set<Network> nets = NETS.get(world.dimension.id);
		CompoundTag dimTag = new CompoundTag();
		root.put("dim" + world.dimension.id, dimTag);

		if (nets == null) {
			return;
		}

		ListTag netsList = new ListTag();
		dimTag.put("nets", netsList);
		nets.forEach(network -> {
			netsList.addTag(network.toTag());
		});
	}

	public static void netsFromTag(World world, CompoundTag root) {
		Set<Network> nets = new HashSet<>();
		NETS.put(world.dimension.id, nets);

		CompoundTag dimTag = root.getCompound("dim" + world.dimension.id);
		if (dimTag == null) {
			return;
		}

		ListTag netsList = dimTag.getList("nets");
		final int size = netsList.tagCount();
		for (int i = 0; i < size; i++) {
			Network net = Network.fromTag(world, (CompoundTag) netsList.tagAt(i));
			if (net.getSize() > 1) {
				nets.add(net);
			}
		}
	}

	public static boolean canBeNet(WorldSource world, int x, int y, int z) {
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		return canBeNet(block);
	}

	public static boolean canBeNet(Block block) {
		return block instanceof NetworkComponent;
	}

	private static Network getNet(World world, int x, int y, int z) {
		Set<Network> nets = NETS.get(world.dimension.id);
		if (nets != null) {
			for (Network net: nets) {
				if (net.existsOnPos(x, y, z)) {
					return net;
				}
			}
		}
		return null;
	}

	public static void updateAllNets(){
		NETS.forEach((dimId, nets)->{
			for (Network net : nets) {
				net.update();
			}
		});
	}
}
