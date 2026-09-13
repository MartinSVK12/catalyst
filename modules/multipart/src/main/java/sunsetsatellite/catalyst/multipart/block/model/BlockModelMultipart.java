package sunsetsatellite.catalyst.multipart.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Sides;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.data.block.mojang.BlockModelMojangData;
import org.useless.dragonfly.data.block.mojang.Element;
import org.useless.dragonfly.data.block.mojang.Face;
import org.useless.dragonfly.models.block.mojang.StaticBlockModelMojang;
import sunsetsatellite.catalyst.multipart.api.ISupportsMultiparts;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.block.logic.BlockLogicMultipart;

import java.util.HashMap;
import java.util.Map;

public class BlockModelMultipart extends BlockModelGeneric<BlockLogicMultipart> {

	public static final int[] orientationLookUpVertical = new int[]{
		3, 2, 0, 1, 4, 5, //down
		3, 2, 1, 0, 5, 4 //up
	};

	public static final BlockModelMojangData EMPTY = new BlockModelMojangData.Builder()
		.addElement(new Element.Builder(0, 0, 0, 16, 16, 16)
			.addFace(Direction.UP, new Face.Builder("#empty"))
			.addFace(Direction.DOWN, new Face.Builder("#empty"))
			.addFace(Direction.NORTH, new Face.Builder("#empty"))
			.addFace(Direction.SOUTH, new Face.Builder("#empty"))
			.addFace(Direction.WEST, new Face.Builder("#empty"))
			.addFace(Direction.EAST, new Face.Builder("#empty"))
		)
		.setTexture("empty","catalyst-multipart:block/empty")
		.setAO(false)
		.build(Minecraft.getMinecraft().texturePackList, "catalyst-multipart:block/empty");

	public BlockModelMultipart(Block<BlockLogicMultipart> block) {
		super(block, EMPTY);
	}

	@Override
	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos, boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		TileEntity tile = worldSource.getTileEntity(tilePos);
		if(tile instanceof ISupportsMultiparts multipart){
			for (Map.Entry<sunsetsatellite.catalyst.core.util.Direction, Multipart> e : multipart.getParts().entrySet()) {
				Direction direction = e.getKey().getSide().direction();
				Multipart part = e.getValue();
				if(part == null) continue;
				Map<Direction, String> textures = new HashMap<>();
				for (Direction dir : Direction.values()) {

					int data = direction.id;
					if (part.specifiedSideOnly) {
						data = part.side.id;
					}
					boolean isVertical = data == 0 || data == 1;
					int index;
					if (isVertical) {
						index = orientationLookUpVertical[6 * data + dir.id];
					} else {
						index = Sides.orientationLookUpHorizontal[6 * Math.min(data, 5) + dir.id];
					}
					if (index >= Sides.orientationLookUpHorizontal.length) continue;
					Side side = Side.fromId(index);

					textures.put(dir, "minecraft:block/missing");
					if(part.block != null){
						BlockModel<?> model = BlockModelDispatcher.getInstance().getDispatch(part.block);
						if(model instanceof BlockModelStandard<?> standard){
							IconCoordinate texture = standard.getBlockTextureFromSideAndMetadata(side, part.meta);
							if(texture != null) {
								textures.put(dir, texture.namespaceId.toString());
							}
						} else if (model instanceof BlockModelGeneric<?> generic) {
							if(generic.getModelFromData(part.meta) instanceof StaticBlockModelMojang mojang){
								IconCoordinate texture = mojang.compiled.textures.get("#"+side.direction.name().toLowerCase());
								if(texture != null){
									textures.put(dir, texture.namespaceId.toString());
								} else {
									texture = mojang.compiled.textures.get("#cross");
									if(texture != null){
										textures.put(dir, texture.namespaceId.toString());
									}
								}
							}
						}
					}
				}
				BlockModelMojangData model = new BlockModelMojangData.Builder()
					.setParent("catalyst-multipart:block/"+part.type.model)
					.setTexture("north", textures.get(Direction.NORTH))
					.setTexture("east", textures.get(Direction.EAST))
					.setTexture("south", textures.get(Direction.SOUTH))
					.setTexture("west", textures.get(Direction.WEST))
					.setTexture("up", textures.get(Direction.UP))
					.setTexture("down", textures.get(Direction.DOWN))
					.setTexture("particle_north", textures.get(Direction.NORTH))
					.setTexture("particle_east", textures.get(Direction.EAST))
					.setTexture("particle_south", textures.get(Direction.SOUTH))
					.setTexture("particle_west", textures.get(Direction.WEST))
					.setTexture("particle_up", textures.get(Direction.UP))
					.setTexture("particle_down", textures.get(Direction.DOWN))
					.setTexture("overlay", textures.get(Direction.NORTH))
					.build(Minecraft.getMinecraft().texturePackList, "catalyst-multipart:block/"+part.type.model+"_"+part.block.namespaceId().namespace()+"_"+part.block.namespaceId().value().split("/")[1]);

				switch (direction) {
					case UP -> {
						model.asModel().renderAttached(this, tessellator, worldSource, tilePos, 1, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					}
					case DOWN -> {
						model.asModel().renderAttached(this, tessellator, worldSource, tilePos, -1, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					}
					case NORTH -> {
						model.asModel().renderAttached(this, tessellator, worldSource, tilePos, 0, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					}
					case SOUTH -> {
						model.asModel().renderAttached(this, tessellator, worldSource, tilePos, 0, 2, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					}
					case WEST -> {
						model.asModel().renderAttached(this, tessellator, worldSource, tilePos, 0, 1, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					}
					case EAST -> {
						model.asModel().renderAttached(this, tessellator, worldSource, tilePos, 0, 3, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					}
				}
			}

		}
		return super.renderAttached(tessellator, worldSource, tilePos, cullFaces, overrideTexture);
	}

}
