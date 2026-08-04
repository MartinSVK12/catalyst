package sunsetsatellite.catalyst.core.util.network;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.BlockChangeInfo;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.Signal;
import sunsetsatellite.catalyst.core.util.mp.PacketAddNetworkBlock;
import sunsetsatellite.catalyst.core.util.mp.PacketRemoveNetworkBlock;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.io.File;
import java.io.IOException;
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

	private NetworkManager() {
	}

	public static int getNetID(World world, Vec3i pos) {
		Network net = getNet(world, pos);
		return net == null ? -1 : net.hashCode();
	}


	public static class BlockChangeListener implements Signal.Listener<BlockChangeInfo> {
		public static final Signal.Listener<BlockChangeInfo> INSTANCE = new BlockChangeListener();

		@Override
		public void signalEmitted(Signal<BlockChangeInfo> signal, BlockChangeInfo blockChanged) {
			if (signal != Catalyst.TILE_ENTITY_BLOCK_CHANGED_SIGNAL) {
				return;
			}
			if (blockChanged.block.id() == 0) {
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
			if (signal == Catalyst.DIMENSION_LOAD_SIGNAL) {
				File file = world.getLevelStorage().getDataFile("networks_" + world.dimension.id);
				if (file == null) return;
				if (file.exists()) {
					try {
						CompoundTag tag = NbtIo.readCompressed(Files.newInputStream(file.toPath()));
						NetworkManager.netsFromTag(world, tag);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (signal == Catalyst.DIMENSION_SAVE_SIGNAL) {
				try {
					File file = world.getLevelStorage().getDataFile("networks_" + world.dimension.id);
					if (file == null) return;
					CompoundTag tag = new CompoundTag();
					try {
						tag = NbtIo.readCompressed(Files.newInputStream(file.toPath()));
					} catch (NoSuchFileException e) {
						Catalyst.LOGGER.info("Creating new networks file for dimension {}!", world.dimension.id);
					}
					NetworkManager.netsToTag(world, tag);
					NbtIo.writeCompressed(tag, Files.newOutputStream(file.toPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static void addBlock(BlockChangeInfo blockChanged) {
		Vec3i pos = blockChanged.pos.copy();
		World world = blockChanged.world;

		if (!canBeNet(blockChanged.block)) {
			return;
		}

		NetworkComponent component = Catalyst.blockLogic(blockChanged.block, NetworkComponent.class);

		Set<Network> nets = NETS.computeIfAbsent(world.dimension.id, i -> new HashSet<>());

		Set<Network> sideNets = new HashSet<>();
		for (Network net : nets) {
			for (Vec3i offset : Direction.getVecs()) {
				Vec3i point = pos.copy().add(offset);
				if (net.existsOnPos(point)) {
					sideNets.add(net);
				}
			}
		}

		Network net = null;
		int size = sideNets.size();
		//no nets around, create one
		if (size == 0) {
			net = new Network(world, component.getType());
			net.addBlock(pos);
			for (Vec3i offset : Direction.getVecs()) {
				Vec3i point = pos.copy().add(offset);
				if (canBeNet(world, point)) {
					net.addBlock(point);
				}
			}
			if (net.getSize() > 1) {
				nets.add(net);
			}
		} else if (size == 1) {
			Network potentialNet = sideNets.stream().findAny().get();
			if (potentialNet.isOfSameType(component)) {
				potentialNet.addBlock(pos);
				net = potentialNet;
			}
		} else { //multiple nets around
			Network[] netsArray = sideNets.toArray(new Network[size]);
			Network main;
			for (Network network : netsArray) {
				if (network.isOfSameType(component)) {
					main = network;
					main.addBlock(pos);
					for (Network otherNet : netsArray) {
						if (otherNet == main) {
							continue;
						}
						if (otherNet.isOfSameType(main)) {
							main.mergeNetwork(otherNet);
							nets.remove(otherNet);
						}
					}
					net = main;
					break;
				}
			}
		}

		if (net == null && getNet(world, pos) == null) {
			net = new Network(world, component.getType());
			net.addBlock(pos);
			for (Vec3i offset : Direction.getVecs()) {
				Vec3i point = pos.copy().add(offset);
				if (canBeNet(world, pos)) {
					net.addBlock(pos);
				}
			}
			if (net.getSize() > 1) {
				nets.add(net);
			}
		}

		//add surrounding blocks to net if type matches
		for (Vec3i offset : Direction.getVecs()) {
			Vec3i point = pos.copy().add(offset);
			if (canBeNet(world, point) && getNet(world, point) == null && net != null) {
				NetworkComponent sideComponent = Catalyst.blockLogic(point.getBlock(world), NetworkComponent.class);
				if (net.isOfSameType(sideComponent)) {
					net.addBlock(point);
				}
			}
		}

		NetworkHandler.sendToAllPlayers(new PacketAddNetworkBlock(blockChanged.pos.x, blockChanged.pos.y, blockChanged.pos.z, blockChanged.block.id(), blockChanged.meta, blockChanged.world.dimension.id));
	}

	public static void removeBlock(BlockChangeInfo blockChanged) {
		Vec3i pos = blockChanged.pos.copy();
		World world = blockChanged.world;

		Set<Network> nets = NETS.get(world.dimension.id);

		if (nets == null) {
			return;
		}

		Network target = null;
		for (Network net : nets) {
			if (net.existsOnPos(pos)) {
				target = net;
				break;
			}
		}

		if (target != null) {
			List<? extends Network> sideNets = target.removeBlock(pos);
			NetworkHandler.sendToAllPlayers(new PacketRemoveNetworkBlock(blockChanged.pos.x, blockChanged.pos.y, blockChanged.pos.z, blockChanged.world.dimension.id));
			if (sideNets != null) {
				nets.remove(target);
				nets.addAll(sideNets);
			} else if (target.getSize() < 2) {
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
			net.update();
			if (net.getSize() > 1) {
				nets.add(net);
			}
		}
	}

	public static boolean canBeNet(WorldSource world, Vec3i pos) {
		Block<?> block = pos.getBlock(world);
		return canBeNet(block);
	}

	public static boolean canBeNet(Block<?> block) {
		return Block.hasLogicClass(block, NetworkComponent.class);
	}

	public static Network getNet(World world, Vec3i pos) {
		Set<Network> nets = NETS.get(world.dimension.id);
		if (nets != null) {
			for (Network net : nets) {
				if (net.existsOnPos(pos)) {
					return net;
				}
			}
		}
		return null;
	}

	public static Map<Integer, Set<Network>> getAllNets() {
		return Collections.unmodifiableMap(NETS);
	}

	public static Set<Network> getNetsForDimension(int dim) {
		return Collections.unmodifiableSet(NETS.getOrDefault(dim, Collections.emptySet()));
	}

	public static void clearNets(int dim) {
		NETS.remove(dim);
	}

	public static void updateAllNets() {
		NETS.forEach((dimId, nets) -> {
			for (Network net : nets) {
				net.update();
			}
		});
	}
}
