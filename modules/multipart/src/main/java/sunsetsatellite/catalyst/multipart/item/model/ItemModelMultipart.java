package sunsetsatellite.catalyst.multipart.item.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Sides;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.useless.dragonfly.DisplayPos;
import org.useless.dragonfly.data.block.mojang.BlockModelMojangData;
import org.useless.dragonfly.models.block.StaticBlockModel;
import org.useless.dragonfly.models.block.mojang.StaticBlockModelMojang;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.item.ItemMultipart;

import java.util.HashMap;
import java.util.Map;

import static sunsetsatellite.catalyst.multipart.block.model.BlockModelMultipart.orientationLookUpVertical;

public class ItemModelMultipart extends ItemModelStandard {

	public StaticBlockModel model;

	public ItemModelMultipart(@NotNull Item item) {
		super(item);
	}

	@Override
	public @NotNull IconCoordinate getIcon(@Nullable Entity entity, @NotNull ItemStack itemStack) {
		ItemMultipart item = (ItemMultipart) itemStack.getItem();
		Multipart multipart = item.getMultipart(itemStack);
		BlockModel<?> model = BlockModelDispatcher.getInstance().getDispatch(multipart.block);
		IconCoordinate icon = model.getOverlayTexture(multipart.meta);
		if(icon != null) return icon;
		return super.getIcon(entity, itemStack);
	}

	@Override
	public @NotNull DisplayPos getDisplayPos(@NotNull String id) {
		if(model == null) return super.getDisplayPos(id);
		return model.getItemDisplayPos(id);
	}

	@Override
	public void render(@NotNull TessellatorGeneral tessellator, @Nullable Entity holder, @NotNull ItemStack itemStack, @NotNull String displayPosId, boolean items3d, int clusterSize, byte lightIndex, float partialTick, boolean leftHanded) {
		random.setSeed(187L);
		GLRenderer.setShader(Shaders.ITEM);
		DisplayPos displayPos = getDisplayPos(displayPosId);
		var model = GLRenderer.modelM4f();
		model.translate(leftHanded ? -displayPos.tx : displayPos.tx, displayPos.ty, displayPos.tz);
		model.rotateX(org.joml.Math.toRadians(displayPos.rx));
		model.rotateY(org.joml.Math.toRadians(leftHanded ? -displayPos.ry : displayPos.ry));
		model.rotateZ(org.joml.Math.toRadians(leftHanded ? -displayPos.rz : displayPos.rz));
		model.scale(displayPos.sx, displayPos.sy, displayPos.sz);
		for (int i = 0; i < clusterSize; i++) {
			float rOffX = 0;
			float rOffY = 0;
			float rOffZ = 0;
			if (i > 0) {
				rOffX = ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / displayPos.sx;
				rOffY = ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / displayPos.sy;
				rOffZ = ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / displayPos.sz;
			}
			model.translate(rOffX, rOffY, rOffZ);
			renderSingle(tessellator, holder, itemStack, items3d, lightIndex, getColor(itemStack), partialTick, leftHanded);
			model.translate(-rOffX, -rOffY, -rOffZ);
		}
	}

	public @NotNull StaticBlockModel getModel(@NotNull TessellatorGeneral tessellator, @NotNull ItemStack itemStack){
		ItemMultipart item = (ItemMultipart) itemStack.getItem();
		Multipart part = item.getMultipart(itemStack);

		Map<Direction, String> textures = new HashMap<>();
		for (Direction dir : Direction.values()) {

			int data = Direction.NORTH.id;
			if (part.specifiedSideOnly) {
				data = part.side.id;
			}
			boolean isVertical = data == 0 || data == 1;
			int index;
			if (isVertical) {
				index = orientationLookUpVertical[6 * data + dir.id];
			} else {
				index = Sides.orientationLookUpHorizontal[6 * java.lang.Math.min(data, 5) + dir.id];
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

		this.model = model.asModel();

		return this.model;
	}

	@Override
	protected void renderSingle(@NotNull TessellatorGeneral tessellator, @Nullable Entity holder, @NotNull ItemStack itemStack, boolean items3d, byte lightIndex, int color, float partialTick, boolean mirrorX) {
		TextureRegistry.worldAtlas.bind();
		ItemMultipart item = (ItemMultipart) itemStack.getItem();
		Multipart part = item.getMultipart(itemStack);
		StaticBlockModel partModel = getModel(tessellator, itemStack);
		partModel.renderStandalone((BlockModelGeneric<? extends BlockLogic>) BlockModelDispatcher.getInstance().getDispatch(CatalystMultipart.multipartBlock), tessellator, 0, 0, 0, part.meta, lightIndex, BlockColorDispatcher.getInstance().getDispatch(part.block));
	}

	@Override
	public void renderGui(@NotNull TessellatorGeneral tessellator, @Nullable Entity holder, @NotNull ItemStack itemStack, int x, int y, byte lightIndex, float partialTick) {
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.ITEM);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		GLRenderer.enableState(State.CULL_FACE);
		GLRenderer.modelM4f().translate(x + 8, y + 8, 8);
		GLRenderer.modelM4f().scale(16f, -16f, 16f);
		render(tessellator, holder, itemStack, DisplayPos.GUI, true, 1, lightIndex, partialTick, false);
		GLRenderer.popFrame();
	}

	@Override
	public void renderItemEntity(@NotNull TessellatorGeneral tessellator, @NotNull ItemStack itemStack, boolean items3d, int clusterSize, int ticks, float yaw, byte lightIndex, float partialTick) {
		GLRenderer.enableState(State.BLEND);
		GLRenderer.modelM4f().rotateY(Math.toRadians(yaw));
		render(tessellator, null, itemStack, DisplayPos.GROUND, items3d, clusterSize, lightIndex, partialTick, false);
		GLRenderer.disableState(State.BLEND);
	}
}
