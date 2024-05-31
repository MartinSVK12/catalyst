package sunsetsatellite.catalyst.multiblocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.season.SeasonManager;
import sunsetsatellite.catalyst.core.util.BlockInstance;
import sunsetsatellite.catalyst.core.util.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;

public class HologramWorld implements WorldSource {
	private final HashMap<Vec3i, BlockInstance> blocks = new HashMap<>();

	public HologramWorld(ArrayList<BlockInstance> structure){
		for (BlockInstance block : structure) {
			blocks.put(block.pos,block);
		}
	}

	@Override
	public int getBlockId(int i, int j, int k) {
		Vec3i vec = new Vec3i(i,j,k);
		BlockInstance inst = blocks.get(vec);
		if(inst == null){
			return 0;
		}
		return inst.block.id;
	}

	@Override
	public Block getBlock(int i, int j, int k) {
		Vec3i vec = new Vec3i(i,j,k);
		BlockInstance inst = blocks.get(vec);
		if(inst == null){
			return null;
		}
		return inst.block;
	}

	@Override
	public TileEntity getBlockTileEntity(int i, int j, int k) {
		return null;
	}

	@Override
	public float getBrightness(int i, int j, int k, int l) {
		return 1.0f;
	}

	@Override
	public int getLightmapCoord(int i, int j, int k, int l) {
		return 0;
	}

	@Override
	public float getLightBrightness(int i, int j, int k) {
		return 1.0f;
	}

	@Override
	public int getBlockMetadata(int i, int j, int k) {
		Vec3i vec = new Vec3i(i,j,k);
		BlockInstance inst = blocks.get(vec);
		if(inst == null){
			return 0;
		}
		return inst.meta;
	}

	@Override
	public Material getBlockMaterial(int i, int j, int k) {
		Vec3i vec = new Vec3i(i,j,k);
		BlockInstance inst = blocks.get(vec);
		if(inst == null){
			return null;
		}
		return inst.block.blockMaterial;
	}

	@Override
	public boolean isBlockOpaqueCube(int i, int j, int k) {
		Vec3i vec = new Vec3i(i,j,k);
		BlockInstance inst = blocks.get(vec);
		if(inst == null){
			return false;
		}
		return inst.block.isSolidRender();
	}

	@Override
	public boolean isBlockNormalCube(int i, int j, int k) {
		Vec3i vec = new Vec3i(i,j,k);
		BlockInstance inst = blocks.get(vec);
		if(inst == null){
			return false;
		}
		Block block = inst.block;
		return block.blockMaterial.isSolidBlocking();
	}

	@Override
	public double getBlockTemperature(int i, int j) {
		return 0;
	}

	@Override
	public double getBlockHumidity(int i, int j) {
		return 0;
	}

	@Override
	public SeasonManager getSeasonManager() {
		return null;
	}

	@Override
	public Biome getBlockBiome(int i, int j, int k) {
		return null;
	}
}
