package sunsetsatellite.catalyst.multipart.item.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.CatalystMultipart;
import sunsetsatellite.catalyst.multipart.api.Multipart;
import sunsetsatellite.catalyst.multipart.api.MultipartType;
import sunsetsatellite.catalyst.multipart.block.model.BlockModelMultipartItem;
import sunsetsatellite.catalyst.multipart.block.model.MultipartBlockModelBuilder;

import java.util.Objects;
import java.util.Random;

public class ItemModelMultipart extends ItemModelStandard {

	public ItemModelMultipart(Item item, String namespace) {
		super(item, namespace);
	}

	public void renderItemFirstPerson(Tessellator tessellator, ItemRenderer renderer, EntityPlayer player, ItemStack stack, float partialTick) {
		Minecraft mc = Minecraft.getMinecraft(this);
		float brightness = 1.0F;
		if (LightmapHelper.isLightmapEnabled()) {
			int lightmapCoord = player.getLightmapCoord(partialTick);
			if (this.itemfullBright) {
				lightmapCoord = LightmapHelper.setBlocklightValue(lightmapCoord, 15);
			}

			LightmapHelper.setLightmapCoord(lightmapCoord);
		} else if (!mc.fullbright && !this.itemfullBright) {
			brightness = player.getBrightness(1.0F);
		}

		float distanceScale = 0.8F;
		float swingProgress = player.getSwingProgress(partialTick);
		float animationProgress2 = MathHelper.sin(swingProgress * 3.1415927F);
		float animationProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
		GL11.glTranslatef(-animationProgress * 0.4F, MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F * 2.0F) * 0.2F, -animationProgress2 * 0.2F);
		GL11.glTranslatef(0.56F, -0.52F - (1.0F - renderer.getEquippedProgress(partialTick)) * 0.6F, -0.71999997F);
		GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
		GL11.glEnable(32826);
		float animationProgress3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
		GL11.glRotatef(-animationProgress3 * 20.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-animationProgress * 20.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(-animationProgress * 80.0F, 1.0F, 0.0F, 0.0F);
		float scale = 0.4F;
		GL11.glScalef(0.4F, 0.4F, 0.4F);
		BlockModelMultipartItem blockModel = getMultipartModel(stack);
		if (blockModel != null && blockModel.shouldItemRender3d()) {
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		}

		this.renderItem(tessellator, renderer, stack, player, brightness, true);
	}

	public void renderItem(Tessellator tessellator, ItemRenderer renderer, ItemStack itemstack, @Nullable Entity entity, float brightness, boolean handheldTransform) {
		BlockModelMultipartItem blockModel = getMultipartModel(itemstack);
		if (blockModel != null && blockModel.shouldItemRender3d()) {
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			TextureRegistry.blockAtlas.bindTexture();
			blockModel.renderBlockOnInventory(tessellator, itemstack.getMetadata(), brightness, null);
			GL11.glDisable(3042);
		} else {
			super.renderItem(tessellator, renderer, itemstack, entity, brightness, handheldTransform);
		}
	}

	public BlockModelMultipartItem getMultipartModel(ItemStack itemstack) {
		String type = itemstack.getData().getCompound("Multipart").getStringOrDefault("Type", "");
		Block block = Block.getBlock(itemstack.getData().getCompound("Multipart").getInteger("Block"));
		int meta = itemstack.getData().getCompound("Multipart").getInteger("Meta");
		boolean specifiedSideOnly = itemstack.getData().getCompound("Multipart").containsKey("Side");
		if(!Objects.equals(type, "") && block != null && MultipartType.types.containsKey(type)) {
			BlockModelMultipartItem blockModel = new MultipartBlockModelBuilder(CatalystMultipart.MOD_ID)
				.setBlockModel("cover.json")
				.buildItem();
			if(specifiedSideOnly){
				Side side = Side.getSideById(itemstack.getData().getCompound("Multipart").getInteger("Side"));
				blockModel.multipart = new Multipart(MultipartType.types.get(type),block,side,meta);
				return blockModel;
			}
			blockModel.multipart = new Multipart(MultipartType.types.get(type),block,meta);
			return blockModel;
		}
		return null;
	}

	public void heldTransformThirdPerson(ItemRenderer renderer, Entity entity, ItemStack itemStack) {
		BlockModelMultipartItem blockModel = getMultipartModel(itemStack);
		if (itemStack.itemID < Block.blocksList.length && blockModel != null && blockModel.shouldItemRender3d()) {
			float scale = 0.375F;
			GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
			GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(0.375F, -0.375F, 0.375F);
		} else {
			super.heldTransformThirdPerson(renderer, entity, itemStack);
		}

	}

	public void renderItemIntoGui(Tessellator tessellator, FontRenderer fontrenderer, RenderEngine renderengine, ItemStack itemStack, int x, int y, float brightness, float alpha) {
		if (itemStack != null) {
			BlockModelMultipartItem blockModel = getMultipartModel(itemStack);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(2884);
			if (blockModel != null && blockModel.shouldItemRender3d()) {
				GL11.glBlendFunc(770, 771);
				TextureRegistry.blockAtlas.bindTexture();
				GL11.glPushMatrix();
				GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0F);
				GL11.glScalef(10.0F, 10.0F, 10.0F);
				GL11.glTranslatef(1.0F, 0.5F, 1.0F);
				GL11.glScalef(1.0F, 1.0F, -1.0F);
				GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				if (this.useColor) {
					int color = this.getColor(itemStack);
					float r = (float)(color >> 16 & 255) / 255.0F;
					float g = (float)(color >> 8 & 255) / 255.0F;
					float b = (float)(color & 255) / 255.0F;
					GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);
				} else {
					GL11.glColor4f(brightness, brightness, brightness, alpha);
				}

				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				BlockModel.renderBlocks.useInventoryTint = this.useColor;
				blockModel.renderBlockOnInventory(tessellator, itemStack.getMetadata(), brightness, alpha, null);
				BlockModel.renderBlocks.useInventoryTint = true;
				GL11.glPopMatrix();
			} else {
				super.renderItemIntoGui(tessellator, fontrenderer, renderengine, itemStack, x, y, brightness, alpha);
			}

			GL11.glEnable(2884);
			GL11.glDisable(3042);
		}
	}

	public void renderAsItemEntity(Tessellator tessellator, Entity entity, Random random, ItemStack itemstack, int renderCount, float yaw, float brightness, float partialTick) {
		BlockModelMultipartItem blockModel = getMultipartModel(itemstack);
		if (blockModel != null && blockModel.shouldItemRender3d()) {
			GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
			TextureRegistry.blockAtlas.bindTexture();
			float itemSize = blockModel.getItemRenderScale();
			GL11.glScalef(itemSize, itemSize, itemSize);

			for(int i = 0; i < renderCount; ++i) {
				GL11.glPushMatrix();
				if (i > 0) {
					float rOffX = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / itemSize;
					float rOffY = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / itemSize;
					float rOffZ = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / itemSize;
					GL11.glTranslatef(rOffX, rOffY, rOffZ);
				}

				if (LightmapHelper.isLightmapEnabled()) {
					brightness = 1.0F;
					LightmapHelper.setLightmapCoord(entity.getLightmapCoord(partialTick));
				}

				if (Global.accessor.isFullbrightEnabled() || this.itemfullBright) {
					brightness = 1.0F;
				}

				blockModel.renderBlockOnInventory(tessellator, itemstack.getMetadata(), brightness, null);
				GL11.glPopMatrix();
			}
		} else {
			super.renderAsItemEntity(tessellator, entity, random, itemstack, renderCount, yaw, brightness, partialTick);
		}

	}

	public void renderItemInWorld(Tessellator tessellator, Entity entity, ItemStack itemStack, float brightness, float alpha, boolean worldTransform) {
		if (itemStack != null) {
			BlockModelMultipartItem blockModel = getMultipartModel(itemStack);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(2884);
			if (blockModel != null && blockModel.shouldItemRender3d()) {
				GL11.glBlendFunc(770, 771);
				TextureRegistry.blockAtlas.bindTexture();
				if (this.useColor) {
					int color = this.getColor(itemStack);
					float r = (float)(color >> 16 & 255) / 255.0F;
					float g = (float)(color >> 8 & 255) / 255.0F;
					float b = (float)(color & 255) / 255.0F;
					GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);
				} else {
					GL11.glColor4f(brightness, brightness, brightness, alpha);
				}

				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				BlockModel.renderBlocks.useInventoryTint = this.useColor;
				blockModel.renderBlockOnInventory(tessellator, itemStack.getMetadata(), brightness, alpha, null);
				BlockModel.renderBlocks.useInventoryTint = true;
			} else {
				super.renderItemInWorld(tessellator, entity, itemStack, brightness, alpha, worldTransform);
			}

			GL11.glEnable(2884);
			GL11.glDisable(3042);
		}
	}

}
