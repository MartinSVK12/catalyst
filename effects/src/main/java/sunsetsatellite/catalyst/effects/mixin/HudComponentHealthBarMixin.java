package sunsetsatellite.catalyst.effects.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentHealthBar;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.helper.DyeColor;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.IHasEffects;
import sunsetsatellite.catalyst.effects.api.effect.options.EffectExtraHealthDisplayStyle;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRenderer;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererDispatcher;
import sunsetsatellite.catalyst.effects.api.effect.render.EffectRendererManager;
import sunsetsatellite.catalyst.effects.api.effect.render.heartContainer.HeartContainer;
import sunsetsatellite.catalyst.effects.api.effect.render.heartContainer.IHasCustomHeartContainer;
import sunsetsatellite.catalyst.effects.helper.HealthHelper;
import sunsetsatellite.catalyst.effects.interfaces.mixins.IKeybinds;

import java.util.Random;

@Mixin(value = HudComponentHealthBar.class, remap = false)
public abstract class HudComponentHealthBarMixin extends HudComponentMovable {

	@Unique
	Random random = new Random();

	@Unique
	int spacing = 1;

	public HudComponentHealthBarMixin(String key, int xSize, int ySize, Layout layout) {
		super(key, xSize, ySize, layout);
	}

	@Unique
	int getRows(Player player) {
		return (int) Math.ceil(player.getMaxHealth() / 20.0);
	}

	@Override
	public int getYSize(Minecraft mc) {
		if (mc.thePlayer == null) return super.getYSize(mc);

		EffectExtraHealthDisplayStyle extraHealthDisplay = ((IKeybinds) mc.gameSettings).getExtraHealthDisplayStyle().value;

		if (extraHealthDisplay == EffectExtraHealthDisplayStyle.EXTRA_BARS) {
			int rows = getRows(mc.thePlayer);
			return 10 * rows;
		}

		return super.getYSize(mc);
	}

	@Override
	public int getAnchorY(ComponentAnchor anchor) {
		return (int) (anchor.yPosition * this.getYSize(Minecraft.getMinecraft()));
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(Minecraft mc, HudIngame hud, int xSizeScreen, int ySizeScreen, float partialTick, CallbackInfo ci) {
		Player player = mc.thePlayer;
		if (player == null) return;

		EffectStack stack = EffectRendererManager.resolveDominantHeartContainer(((IHasEffects<?>) player).getContainer());
		if (stack == null && getRows(player) == 1) return;

		ci.cancel();

		HeartContainer heartContainer = null;
		if (stack != null) {
			EffectRenderer<?> renderer = EffectRendererDispatcher.getRendererFor(stack.getEffect());

			if (renderer instanceof IHasCustomHeartContainer) {
				heartContainer = ((IHasCustomHeartContainer) renderer).getCustomContainer(player);
			}
		}

		if (heartContainer == null) heartContainer = new HeartContainer(player);

		int x = this.getLayout().getComponentX(mc, this, xSizeScreen);
		int y = this.getLayout().getComponentY(mc, this, ySizeScreen);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);

		this.random.setSeed(hud.updateCounter * 312871L);

		EffectExtraHealthDisplayStyle style = ((IKeybinds) mc.gameSettings).getExtraHealthDisplayStyle().value;

		if (style == EffectExtraHealthDisplayStyle.EXTRA_BARS || getRows(player) == 1) {
			drawExtraBars(mc, hud, player, heartContainer, x, y);
		} else if (style == EffectExtraHealthDisplayStyle.MULTIPLIER) {
			drawNumberBar(mc, hud, player, heartContainer, x, y);
		}
	}

	@Unique
	private void drawNumberBar(Minecraft mc, Gui hud, Player player, HeartContainer heartContainer, int x, int y) {
		float playerHealthPercent = (float) player.getHealth() / HealthHelper.getMaxHealth(player);
		int extraHearts = HealthHelper.getMaxHealth(player) / 2;
		final int heartsToRender = 8;

		drawRow(mc, hud, player, heartContainer, heartsToRender, (int) (playerHealthPercent * (heartsToRender * 2)), x, y);

		hud.drawString(mc.font,
			String.format("+%s", extraHearts),
			x + 3 * spacing + (heartsToRender - 1) * 8,
			y + (heartContainer.shouldShake() ? this.random.nextInt(2) : 0),
			DyeColor.WHITE.color.value
		);
	}

	@Unique
	public void drawExtraBars(Minecraft mc, Gui hud, Player player, HeartContainer heartContainer, int x, int y) {
		int totalHealth = HealthHelper.getMaxHealth(player);
		int rows = getRows(player);

		for (int row = 0; row < rows; row++) {
			int healthInRow = Math.min(totalHealth - row * 20, 20);
			int heartsToRender = (healthInRow + 1) / 2;
			if (heartsToRender == 0) continue;

			int healthInCurrentRow = Math.max(0, player.getHealth() - row * 20);

			int rowY = y + this.getYSize(mc) - 10 - (row * 10);

			drawRow(mc, hud, player, heartContainer, heartsToRender, healthInCurrentRow, x, rowY);
		}
	}

	@Unique
	public void drawRow(Minecraft mc, Gui hud, Player player, HeartContainer heartContainer, int heartsToRender, int healthInCurrentRow, int x, int y) {
		HeartContainer.HeartGlyphVariant glyphVariant = player.getGamemode() == Gamemode.hardcore
			? HeartContainer.HeartGlyphVariant.HARDCORE
			: HeartContainer.HeartGlyphVariant.NONE;

		for (int i = 0; i < heartsToRender; i++) {
			int xHeart = x + i * 8;
			int yHeart = y;

			if (heartContainer.shouldShake()) {
				yHeart += this.random.nextInt(2);
			}

			int currentHeart = i * 2 + 1;

			heartContainer.drawHeart(glyphVariant, HeartContainer.HeartGlyphType.CONTAINER, xHeart, yHeart, hud);

			if (currentHeart < healthInCurrentRow) {
				heartContainer.drawHeart(glyphVariant, HeartContainer.HeartGlyphType.FULL, xHeart, yHeart, hud);
			}

			if (currentHeart == healthInCurrentRow) {
				heartContainer.drawHeart(glyphVariant, HeartContainer.HeartGlyphType.HALF, xHeart, yHeart, hud);
			}

			if (player.inventory.getCurrentItem() != null
				&& player.inventory.getCurrentItem().getItem() instanceof ItemFood
				&& Boolean.TRUE.equals(mc.gameSettings.foodHealthRegenOverlay.value)) {

				int healing = ((ItemFood) player.inventory.getCurrentItem().getItem()).getHealAmount();

				if (currentHeart >= healthInCurrentRow) {
					if (currentHeart == healthInCurrentRow) {
						heartContainer.drawHeart(HeartContainer.HeartGlyphVariant.PREVIEW, HeartContainer.HeartGlyphType.HALF_RIGHT, xHeart, yHeart, hud);
					} else if (currentHeart < healthInCurrentRow + healing) {
						heartContainer.drawHeart(HeartContainer.HeartGlyphVariant.PREVIEW, HeartContainer.HeartGlyphType.FULL, xHeart, yHeart, hud);
					} else if (currentHeart == healthInCurrentRow + healing) {
						heartContainer.drawHeart(HeartContainer.HeartGlyphVariant.PREVIEW, HeartContainer.HeartGlyphType.HALF, xHeart, yHeart, hud);
					}
				}
			}
		}
	}

	@Inject(method = "renderPreview", at = @At("HEAD"), cancellable = true)
	public void renderPreview(Minecraft mc, Gui gui, Layout layout, int xSizeScreen, int ySizeScreen, CallbackInfo ci) {
		Player player = mc.thePlayer;
		if (player == null) return;

		EffectStack stack = EffectRendererManager.resolveDominantHeartContainer(((IHasEffects<?>) player).getContainer());
		if (stack == null && getRows(player) == 1) return;

		ci.cancel();

		int x = layout.getComponentX(mc, this, xSizeScreen);
		int y = layout.getComponentY(mc, this, ySizeScreen);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);

		final int previewHealth = 21;
		final int rows = 2;
		final int previewYSize = 10 * rows;

		for (int row = 0; row < rows; row++) {
			int healthInRow = 20;
			int heartsToRender = (healthInRow + 1) / 2;

			int healthInCurrentRow = Math.max(0, previewHealth - row * 20);
			int rowY = y + previewYSize - 10 - (row * 10);

			Player previewPlayer = mc.thePlayer;
			HeartContainer previewHeartContainer = new HeartContainer(previewPlayer);

			drawRowPreview(gui, previewPlayer, previewHeartContainer, heartsToRender, healthInCurrentRow, x, rowY);
		}
	}

	@Unique
	private void drawRowPreview(Gui gui, Player player, HeartContainer heartContainer, int heartsToRender, int healthInCurrentRow, int x, int y) {
		HeartContainer.HeartGlyphVariant glyphVariant = player.getGamemode() == Gamemode.hardcore
			? HeartContainer.HeartGlyphVariant.HARDCORE
			: HeartContainer.HeartGlyphVariant.NONE;

		for (int i = 0; i < heartsToRender; i++) {
			int xHeart = x + i * 8;

			int currentHeart = i * 2 + 1;

			heartContainer.drawHeart(glyphVariant, HeartContainer.HeartGlyphType.CONTAINER, xHeart, y, gui);

			if (currentHeart < healthInCurrentRow) {
				heartContainer.drawHeart(glyphVariant, HeartContainer.HeartGlyphType.FULL, xHeart, y, gui);
			}

			if (currentHeart == healthInCurrentRow) {
				heartContainer.drawHeart(glyphVariant, HeartContainer.HeartGlyphType.HALF, xHeart, y, gui);
			}
		}
	}

}
