package sunsetsatellite.catalyst.effects.api.effect.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.core.net.command.TextFormatting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.CatalystEffectsClient;
import sunsetsatellite.catalyst.effects.api.attribute.Attribute;
import sunsetsatellite.catalyst.effects.api.attribute.type.NumberAttribute;
import sunsetsatellite.catalyst.effects.api.effect.*;
import sunsetsatellite.catalyst.effects.api.effect.render.heartContainer.IHasCustomHeartContainer;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.*;

@Environment(EnvType.CLIENT)
public class EffectRendererManager extends Gui {
	private static final EffectRendererDispatcher dispatcher = EffectRendererDispatcher.getInstance();

	/**
	 * @param container The container being examined.
	 * @return most potent EffectStack affecting the container.
	 */
	public static @Nullable EffectStack resolveDominantEffect(EffectContainer<?> container) {
		EffectStack dominant = null;
		for (EffectStack effectStack : container.getEffects()) {
			final Effect effect = effectStack.getEffect();
			final EffectRenderer<?> renderer = dispatcher.getDispatch(effect);

			if (renderer.shouldDisplayScreenEffect()) {
				if (dominant == null) dominant = effectStack;

				int effectStackPotency = effectStack.getAmount() * effectStack.getDuration();
				int dominantPotency = dominant.getAmount() * dominant.getDuration();
				if (effectStackPotency > dominantPotency) dominant = effectStack;
			}
		}

		return dominant;
	}

	/**
	 * @param container affected container
	 * @return most potent EffectStack affecting the player that implements IHasCustomHeartContainer
	 */
	public static EffectStack resolveDominantHeartContainer(EffectContainer<?> container) {
		EffectStack dominant = null;
		for (EffectStack effectStack : container.getEffects()) {
			final Effect effect = effectStack.getEffect();
			final EffectRenderer<?> renderer = dispatcher.getDispatch(effect);

			if (renderer instanceof IHasCustomHeartContainer) {
				if (dominant == null) dominant = effectStack;

				int effectStackPotency = effectStack.getAmount() * effectStack.getDuration();
				int dominantPotency = dominant.getAmount() * dominant.getDuration();
				if (effectStackPotency > dominantPotency) dominant = effectStack;
			}
		}

		return dominant;
	}

	public void drawScreenEffects(EffectContainer<?> container, Minecraft mc, int mouseX, int mouseY, float partialTick) {
		EffectStack mostPotent = resolveDominantEffect(container);
		if (mostPotent == null) return;

		final Effect effect = mostPotent.getEffect();
		final EffectRenderer<?> renderer = dispatcher.getDispatch(effect);
		int width = mc.resolution.getScaledWidthScreenCoords();
		int height = mc.resolution.getScaledHeightScreenCoords();

		renderer.drawScreenEffect(mc, this, mostPotent, width, height, mouseX, mouseY, partialTick);
	}

	public void drawEffectIndicators(EffectContainer<?> container, Minecraft mc, int mouseX, int mouseY, float partialTick) {
		begin();
		int x = 4;
		int y = 4;

		for (EffectStack stack : container.getEffects()) {
			EffectRenderer<?> renderer = dispatcher.getDispatch(stack.getEffect());
			if (!renderer.shouldDisplayIcon()) continue;

			drawEffectIcon(mc, renderer, stack, x, y, mouseX, mouseY);
			if (mouseX > x && mouseX < x + 20 && mouseY > y && mouseY < y + 20) {
				end();
				drawTooltip(mc, stack, mouseX, mouseY);
				begin();
			}
			x += 24;
		}
		if(Boolean.FALSE.equals(CatalystEffectsClient.keybinds.getRenderAttributeIcon().value))
		{
			end();
			return;
		}
		for (Attribute<?> attribute : container.getAttributes()) {
			if (!attribute.shouldDisplayIcon()) continue;
			drawAttributeIcon(mc, attribute, container, x, y, mouseX, mouseY);
			if (mouseX > x && mouseX < x + 20 && mouseY > y && mouseY < y + 20) {
				end();
				drawAttributeTooltip(mc, attribute, container, mouseX, mouseY);
				begin();
			}
			x += 24;
		}
		end();
	}

	private void drawAttributeTooltip(Minecraft mc, Attribute<?> attribute, EffectContainer<?> container, int mouseX, int mouseY) {
		TooltipElement tooltip = new TooltipElement(mc);
		StringBuilder sb = new StringBuilder()
			.append(attribute.getName())
			.append(" ")
			.append("(")
			.append(attribute.calculate(((IHasEffects<?>)container.getParent())))
			.append(")\n");
		if(attribute instanceof NumberAttribute){
			Number number = (Number) attribute.calculate(((IHasEffects<?>) container.getParent()));
			Number base = (Number) attribute.getBaseValue();
			if (number.doubleValue() > base.doubleValue()) {
				tooltip.render(sb.toString(), mouseX, mouseY, 4, 4);
			}
		}else{
			tooltip.render(sb.toString(), mouseX, mouseY, 4, 4);
		}
	}

	private void drawAttributeIcon(Minecraft mc, Attribute<?> attribute, EffectContainer<?> container, int x, int y, int mouseX, int mouseY) {
		end();
		Object value = attribute.calculate(((IHasEffects<?>) container.getParent()));
		String stackSizeString = "x" + value;
		if(attribute instanceof NumberAttribute){
			Number number = (Number) value;
			Number base = (Number) attribute.getBaseValue();
			if (number.doubleValue() > base.doubleValue()) {
				attribute.drawIcon(mc, this, x, y);
				drawString(mc.font, stackSizeString, x + 1, y + 10, 0xFFFFFFFF);
			}
		}else{
			attribute.drawIcon(mc, this, x, y);
			drawString(mc.font, stackSizeString, x + 1, y + 10, 0xFFFFFFFF);
		}
		begin();
	}

	private void drawTooltip(Minecraft mc, EffectStack effect, int mouseX, int mouseY) {
		TooltipElement tooltip = new TooltipElement(mc);
		StringBuilder sb = new StringBuilder();
		sb.append(effect.getEffect().getName()).append(" ").append("(x").append(effect.getAmount()).append(")").append("\n");
		for (Modifier<?> modifier : effect.getEffect().getModifiers()) {
			if (modifier instanceof IntModifier) {
				sb.append("  ").append(String.format(modifier.type.template, ((IntModifier) modifier).calculate(effect), modifier.attribute.getName()));
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof LongModifier) {
				sb.append("  ").append(String.format(modifier.type.template, ((LongModifier) modifier).calculate(effect), modifier.attribute.getName()));
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof FloatModifier) {
				sb.append("  ").append(String.format(modifier.type.template, String.format("%.2f", ((FloatModifier) modifier).calculate(effect)), modifier.attribute.getName()));
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof DoubleModifier) {
				sb.append("  ").append(String.format(modifier.type.template, String.format("%.2f", ((DoubleModifier) modifier).calculate(effect)), modifier.attribute.getName()));
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof BooleanModifier) {
				sb.append("  ").append(modifier);
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			}
		}
		if (effect.getEffect().getTimeType() != EffectTimeType.PERMANENT)
			sb.append("(").append(effect.getTimeLeft() / 20).append("s)");
		if (effect.getEffect().getTimeType() == EffectTimeType.ADD)
			sb.append(" (+").append(effect.getEffect().getDurationIncrease() / 20).append("s/ea)");
		tooltip.render(sb.toString(), mouseX, mouseY, 4, 4);
	}

	private void drawEffectIcon(Minecraft mc, EffectRenderer<?> effectRenderer, EffectStack stack, int x, int y, int mouseX, int mouseY) {
		drawRectWidthHeight(x, y, 20, 20, effectRenderer.getColor());
		end();
		effectRenderer.drawIcon(mc, this, x, y);
		String stackSize = "x" + stack.getAmount();
		if (stack.getEffect().getMaxStack() == 1) {
			stackSize = "";
		}
		drawString(mc.font, stackSize, x + 1, y + 10, 0xFFFFFFFF);
		begin();
		drawRectWidthHeight(x, y, 20, (int) (20 - ((float) stack.getTimeLeft() / (float) stack.getDuration()) * 20), 0x80000000);
	}

	private void begin() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	private void end() {
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
