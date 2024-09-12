package sunsetsatellite.catalyst.multipart.api;

import com.mojang.nbt.CompoundTag;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import sunsetsatellite.catalyst.Catalyst;

import java.util.HashMap;

public class Multipart {

	public final MultipartType type;
	public final HashMap<Side, String> textures = (HashMap<Side, String>) Catalyst.mapOf(Side.values(), Catalyst.arrayFill(new String[Side.values().length], "minecraft:block/texture_unassigned"));
	public final Block block;
	public final int meta;
	public final Side side; //side of the texture that will be used, not the actual side this multipart is attached to
	public final boolean specifiedSideOnly;

	public Multipart(MultipartType type, Block block, Side side, int meta) {
		this.type = type;
		this.block = block;
		this.meta = meta;
		this.side = side;
		this.specifiedSideOnly = true;
		for (Side _side : Side.values()) {
			if(_side == Side.NONE) continue;
			NamespaceID id = BlockModelDispatcher.getInstance().getDispatch(block).getBlockTextureFromSideAndMetadata(this.side,meta).namespaceId;
			this.textures.put(_side,id.namespace + ":block/" + id.value);
		}
	}

	public Multipart(MultipartType type, Block block, int meta) {
		this.type = type;
		this.block = block;
		this.meta = meta;
		this.side = null;
		this.specifiedSideOnly = false;
		for (Side sside : Side.values()) {
			if(sside == Side.NONE) continue;
			NamespaceID id = BlockModelDispatcher.getInstance().getDispatch(block).getBlockTextureFromSideAndMetadata(sside,meta).namespaceId;
			this.textures.put(sside,id.namespace + ":block/" + id.value);
		}
	}

	public Multipart(CompoundTag partNbt){
		boolean sideOnly;
		this.type = MultipartType.types.get(partNbt.getString("Type"));
		this.block = Block.getBlock(partNbt.getInteger("Block"));
		this.meta = partNbt.getInteger("Meta");
		if(partNbt.containsKey("Side")){
			this.side = Side.getSideById(partNbt.getInteger("Side"));
			sideOnly = true;
		} else {
			this.side = null;
			sideOnly = false;
		}
		this.specifiedSideOnly = sideOnly;
		if(specifiedSideOnly) {
			for (Side _side : Side.values()) {
				if(_side == Side.NONE) continue;
				NamespaceID id = BlockModelDispatcher.getInstance().getDispatch(block).getBlockTextureFromSideAndMetadata(this.side,meta).namespaceId;
				this.textures.put(_side,id.namespace + ":block/" + id.value);
			}
		} else {
			for (Side sside : Side.values()) {
				if(sside == Side.NONE) continue;
				NamespaceID id = BlockModelDispatcher.getInstance().getDispatch(block).getBlockTextureFromSideAndMetadata(sside,meta).namespaceId;
				this.textures.put(sside,id.namespace + ":block/" + id.value);
			}
		}
	}

	public void writeToNbt(CompoundTag partNbt) {
		partNbt.putString("Type", type.name);
		partNbt.putInt("Meta", meta);
		partNbt.putInt("Block", block.id);
		if (specifiedSideOnly) {
			partNbt.putInt("Side", side.getId());
		}
	}
}
