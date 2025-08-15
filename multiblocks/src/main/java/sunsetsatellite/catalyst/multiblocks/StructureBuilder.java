package sunsetsatellite.catalyst.multiblocks;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureBuilder {

	private final char originSymbol;
	private final Block<?> originBlock;
	private final int originMeta;
	private final List<String[]> layers = new ArrayList<>();
	private final Map<Character, ItemStack> symbolMap = new HashMap<>();

	public StructureBuilder(char originSymbol, Block<?> originBlock, int originMeta) {
		this.originSymbol = originSymbol;
		this.originBlock = originBlock;
		this.originMeta = originMeta;
	}

	public StructureBuilder addLayer(String... layer){
		layers.add(layer);
		return this;
	}

	public StructureBuilder mapSymbol(char symbol, ItemStack stack){
		if(stack.itemID > 16384 || Blocks.getBlock(stack.itemID) == null){
			throw new IllegalArgumentException("Symbol stack has to contain an already existing block!");
		}
		symbolMap.put(symbol, stack);
		return this;
	}

	public CompoundTag build(){

		int x = 0;
		int y = 0;
		int z = 0;

		Vec3i originPos = null;
		CompoundTag structureData = new CompoundTag();
		CompoundTag originTag = new CompoundTag();
		CompoundTag blocksTag = new CompoundTag();
		CompoundTag tileEntitiesTag = new CompoundTag();
		CompoundTag substitutionsTag = new CompoundTag();

		top:
		for (String[] layer : layers) {
			z = 0;
			for (String section : layer) {
				x = 0;
				for (char symbol : section.toCharArray()) {
					if(symbol == originSymbol){
						originPos = new Vec3i(x,y,z);
						break top;
					}
					x++;
				}
				z++;
			}
			y++;
		}

		if(originPos == null){
			throw new IllegalStateException("No origin found in structure schematic!");
		}

		originTag.putInt("id", originBlock.id());
		originTag.putInt("meta",originMeta);
		originTag.putBoolean("tile", originBlock.isEntityTile);
		CompoundTag originPosTag = new CompoundTag();
		new Vec3i().writeToNBT(originPosTag);
		originTag.putCompound("pos",originPosTag);

		x = y = z = 0;

		int i = 0;

		for (String[] layer : layers) {
			z = 0;
			for (String section : layer) {
				x = 0;
				for (char symbol : section.toCharArray()) {
					if(symbol == ' '){
						x++;
						continue;
					}
					if(symbol == originSymbol){
						x++;
						continue;
					}
					ItemStack mappedStack = symbolMap.get(symbol);
					if(mappedStack == null){
						throw new NullPointerException("Unmapped symbol: '"+symbol+"'!");
					}
					CompoundTag blockTag = new CompoundTag();
					CompoundTag posTag = new CompoundTag();
					boolean isTile = Blocks.getBlock(mappedStack.itemID).isEntityTile;
					new Vec3i(x,y,z).subtract(originPos).writeToNBT(posTag);
					blockTag.putInt("id", mappedStack.itemID);
					blockTag.putInt("meta", mappedStack.getMetadata());
					blockTag.putBoolean("tile", isTile);
					blockTag.putCompound("pos", posTag);
					blocksTag.put(String.valueOf(i), blockTag);
					if(isTile){
						tileEntitiesTag.put(String.valueOf(i), blockTag);
					}
					i++;
					x++;
				}
				z++;
			}
			y++;
		}

		structureData.putCompound("Origin", originTag);
		structureData.putCompound("Blocks", blocksTag);
		structureData.putCompound("TileEntities", tileEntitiesTag);
		structureData.putCompound("Substitutions", substitutionsTag);

		return structureData;
	}

}
