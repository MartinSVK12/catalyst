package sunsetsatellite.catalyst.core.util.network;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.core.util.conduit.ConduitCapability;
import sunsetsatellite.catalyst.core.util.conduit.IConduitTile;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.*;

/**
 * A network of blocks.
 */
public class Network {

	protected final Map<Vec3i, NetworkComponent> networkBlocks = new HashMap<>();
	protected final Map<Vec3i, BlockEntry> blocks = new HashMap<>();
	protected final World world;
	protected final int id;
	protected final NetworkPathMap NET_PATH_DATA = new NetworkPathMap();
	protected final Random random;
	public final Color color;
	public final @NotNull NetworkType type;

	public Network(World world, @NotNull NetworkType type) {
		this(world, NetworkManager.getUID(), type);
	}

	private Network(World world, int id, @NotNull NetworkType type) {
		this.world = world;
		this.id = id;
		this.type = type;
		this.random = new Random(id);
		color = new Color().setRGBA(random.nextInt(255), random.nextInt(255), random.nextInt(255), 64);
	}

	/**
	 * Returns a list of paths possible in this network from the current position.
	 *
	 * @param pos The position to start from
	 * @return List of possible <code>NetworkPath</code>s sorted according to their distance from <code>pos</code>
	 */
	public List<NetworkPath> getPathData(Vec3i pos) {
		List<NetworkPath> routes = NET_PATH_DATA.get(pos);
		if (routes == null) {
			routes = NetworkWalker.createNetworkPaths(world, pos);
			if (routes == null) {
				return Collections.emptyList();
			}
			routes.sort(Comparator.comparingInt(NetworkPath::getDistance));
			NET_PATH_DATA.put(pos, routes);
		}
		return routes;
	}

	public int getSize() {
		return blocks.size();
	}

	public int getId() {
		return id;
	}

	public Color getColor() {
		return color;
	}

	public boolean existsOnPos(Vec3i pos) {
		return blocks.containsKey(pos);
	}

	public void addBlock(Vec3i pos) {
		Block<?> b = pos.getBlock(world);
		int meta = pos.getBlockMetadata(world);

		if (!(b != null && b.getLogic() instanceof NetworkComponent)) return;

		blocks.put(pos, new BlockEntry(b, meta));
		BlockLogic block = b.getLogic();
		if (((NetworkComponent) block).getType().equals(type)) {
			networkBlocks.put(pos, (NetworkComponent) block);
			if (pos.getTileEntity(world) instanceof NetworkComponentTile) {
				((NetworkComponentTile) pos.getTileEntity(world)).networkChanged(this);
			}
		}
		update();
		NET_PATH_DATA.clear();
	}

	public List<Network> removeBlock(Vec3i pos) {
		NetworkComponent component = networkBlocks.get(pos);
		if (component != null) {
			if (pos.getTileEntity(world) instanceof NetworkComponentTile) {
				((NetworkComponentTile) pos.getTileEntity(world)).removedFromNetwork(this);
			}
		}
		networkBlocks.remove(pos);
		blocks.remove(pos);
		update();

		List<Vec3i> sideNets = new ArrayList<>(6);
		for (byte i = 0; i < 6; i++) {
			Vec3i offset = Direction.getVecs()[i];
			Vec3i side = pos.copy().add(offset);
			if (blocks.containsKey(side)) {
				sideNets.add(side);
			}
		}

		List<Set<Vec3i>> preNets = new ArrayList<>();
		boolean[] ignore = new boolean[sideNets.size()];
		for (byte i = 0; i < ignore.length; i++) {
			if (ignore[i]) {
				continue;
			}
			Vec3i startBlock = sideNets.get(i);
			Set<Vec3i> netBlocks = floodFill(startBlock);
			preNets.add(netBlocks);
			if (i < ignore.length - 1) {
				for (byte j = (byte) (i + 1); j < ignore.length; j++) {
					if (netBlocks.contains(sideNets.get(j))) {
						ignore[j] = true;
					}
				}
			}
		}

		final int size = preNets.size();
		if (size < 2) {
			return null;
		}

		List<Network> result = new ArrayList<>(size);
		for (Set<Vec3i> preNet : preNets) {
			Network sideNet = new Network(world, type);

			preNet.forEach(blockPos -> {
				sideNet.blocks.put(blockPos, blocks.get(blockPos));
				NetworkComponent netBlock = networkBlocks.get(blockPos);
				if (netBlock != null) {
					sideNet.networkBlocks.put(blockPos, netBlock);
					TileEntity tile = world.getTileEntity(blockPos.tilePos());
					if (tile instanceof NetworkComponentTile) {
						((NetworkComponentTile) tile).networkChanged(sideNet);
					}
				}
			});

			if (sideNet.getSize() > 1) {
				result.add(sideNet);
				sideNet.update();
			}
		}
		update();
		NET_PATH_DATA.clear();
		return result;
	}

	public void mergeNetwork(Network net) {
		if (net.isOfSameType(net)) {
			blocks.putAll(net.blocks);
			networkBlocks.putAll(net.networkBlocks);
		}
		networkBlocks.forEach((pos, networkComponent) -> {
			TileEntity tile = world.getTileEntity(pos.tilePos());
			if (tile instanceof NetworkComponentTile) {
				((NetworkComponentTile) tile).networkChanged(net);
			}
		});
		NET_PATH_DATA.clear();
	}

	public CompoundTag toTag() {
		CompoundTag net = new CompoundTag();
		ListTag positions = new ListTag();
		net.put("blocks", positions);
		net.putInt("id", id);
		net.putString("type", type.type);

		blocks.forEach((pos, entry) -> {
			CompoundTag tag = new CompoundTag();
			tag.putInt("x", pos.x);
			tag.putInt("y", pos.y);
			tag.putInt("z", pos.z);
			tag.putInt("id", entry.block.id());
			tag.putInt("meta", entry.meta);
			positions.addTag(tag);
		});

		return net;
	}

	public static Network fromTag(World world, CompoundTag root) {
		int id = root.getInteger("id");
		ListTag positions = root.getList("blocks");
		NetworkType networkType = new NetworkType(root.getString("type"));
		Network net = new Network(world, id, networkType);

		final int size = positions.tagCount();
		for (int i = 0; i < size; i++) {
			CompoundTag tag = (CompoundTag) positions.tagAt(i);
			Block<?> block = Blocks.blocksList[tag.getInteger("id")];
			if (block != null) {
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				int meta = tag.getInteger("meta");
				net.blocks.put(new Vec3i(x, y, z), new BlockEntry(block, meta));
				if (NetworkManager.canBeNet(block)) {
					net.networkBlocks.put(new Vec3i(x, y, z), (NetworkComponent) block.getLogic());
				}
			}
		}

		net.update();

		return net;
	}

	private Set<Vec3i> floodFill(Vec3i start) {
		List<Set<Vec3i>> edges = new ArrayList<>();
		Set<Vec3i> result = new HashSet<>();
		edges.add(Catalyst.setOf(start));
		edges.add(new HashSet<>());

		byte n = 0;
		boolean added = true;
		while (added) {
			Set<Vec3i> oldEdge = edges.get(n & 1);
			Set<Vec3i> newEdge = edges.get((n + 1) & 1);
			n = (byte) ((n + 1) & 1);
			oldEdge.forEach(pos -> {
				for (byte i = 0; i < 6; i++) {
					Vec3i offset = Direction.getVecs()[i];
					Vec3i side = new Vec3i(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);
					if (blocks.containsKey(side) && !result.contains(side)) {
						newEdge.add(side);
					}
				}
			});
			added = !oldEdge.isEmpty();
			result.addAll(oldEdge);
			oldEdge.clear();
		}

		return result;
	}

	public void update() {
		networkBlocks.forEach((pos, networkComponent) -> {
			TileEntity tile = world.getTileEntity(pos.tilePos());
			if (tile instanceof NetworkComponentTile) {
				((NetworkComponentTile) tile).networkChanged(this);
			}
		});
		NET_PATH_DATA.clear();
	}

	public boolean isOfSameType(NetworkComponent component) {
		return component != null && component.getType().equals(type);
	}

	public boolean isOfSameType(Network net) {
		return net.type.equals(type);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Network net) {
			Optional<Vec3i> optional = net.blocks.keySet().stream().findAny();
			if (optional.isPresent()) {
				Vec3i pos = optional.get();
				return blocks.containsKey(pos);
			}
		}
		return false;
	}

	public String toString() {
		return String.format("[ID: %d, Size: %d]", id, networkBlocks.size());
	}

	protected static class BlockEntry {
		Block<?> block;
		int meta;

		private BlockEntry(Block<?> block, int meta) {
			this.block = block;
			this.meta = meta;
		}
	}

	public <T> Set<T> search(Vec3i start, Class<T> clazz) {
		HashSet<T> result = new HashSet<>();
		List<NetworkPath> paths = getPathData(start);
		for (NetworkPath path : paths) {
			if (clazz.isAssignableFrom(path.target.getClass())) {
				if (path.target.getPosition().getTileEntity(world) != path.target) {
					NET_PATH_DATA.clear();
				} else {
					result.add(clazz.cast(path.target));
				}
			}
		}
		return result;
	}

	public <T> T findFirst(Vec3i start, Class<T> clazz) {
		for (Direction dir : Direction.values()) {
			TileEntity tileEntity = dir.getTileEntity(world, start);
			if (tileEntity instanceof IConduitTile) {
				if (((IConduitTile) tileEntity).getConduitCapability() == ConduitCapability.RES_NETWORK) {
					List<NetworkPath> paths = getPathData(((IConduitTile) tileEntity).getPosition());
					for (NetworkPath path : paths) {
						if (clazz.isAssignableFrom(path.target.getClass())) {
							if (path.target.getPosition().getTileEntity(world) != path.target) continue;
							return clazz.cast(path.target);
						}
					}
				}
			}
		}
		return null;
	}

	public Set<Vec3i> getPositions() {
		return Collections.unmodifiableSet(blocks.keySet());
	}

}
