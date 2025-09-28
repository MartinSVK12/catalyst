package sunsetsatellite.catalyst.effects.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.core.net.command.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import sunsetsatellite.catalyst.effects.api.effect.EffectContainer;
import sunsetsatellite.catalyst.effects.api.effect.EffectStack;
import sunsetsatellite.catalyst.effects.api.effect.EffectTimeType;
import sunsetsatellite.catalyst.effects.api.modifier.Modifier;
import sunsetsatellite.catalyst.effects.api.modifier.type.*;

public class ScreenEffects extends Gui {

	public void drawEffects(EffectContainer<?> container, Minecraft mc, int mouseX, int mouseY, float partialTick){
		begin();
		int x = 4;
		int y = 4;
		for (EffectStack effect : container.getEffects()) {
			drawEffect(mc,effect,x,y,mouseX,mouseY);
			if(mouseX > x && mouseX < x+20 && mouseY > y && mouseY < y+20){
				end();
				drawTooltip(mc,effect,mouseX,mouseY);
				begin();
			}
			x+=24;
		}
		end();
	}

	private void drawTooltip(Minecraft mc, EffectStack effect, int mouseX, int mouseY){
		TooltipElement tooltip = new TooltipElement(mc);
		StringBuilder sb = new StringBuilder();
		sb.append(effect.getEffect().getName()).append(" ").append("(x").append(effect.getAmount()).append(")").append("\n");
		for (Modifier<?> modifier : effect.getEffect().getModifiers()) {
			if(modifier instanceof IntModifier){
				sb.append("  ").append(String.format(modifier.type.template,((IntModifier) modifier).calculate(effect),modifier.attribute.getName()));
				if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)){
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if(modifier instanceof LongModifier){
				sb.append("  ").append(String.format(modifier.type.template,((LongModifier) modifier).calculate(effect),modifier.attribute.getName()));
				if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)){
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof FloatModifier) {
				sb.append("  ").append(String.format(modifier.type.template,String.format("%.2f",((FloatModifier) modifier).calculate(effect)),modifier.attribute.getName()));
				if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)){
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof DoubleModifier) {
				sb.append("  ").append(String.format(modifier.type.template,String.format("%.2f",((DoubleModifier) modifier).calculate(effect)),modifier.attribute.getName()));
				if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)){
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			} else if (modifier instanceof BooleanModifier) {
				sb.append("  ").append(modifier);
				if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)){
					sb.append(": ").append(TextFormatting.GRAY).append(modifier.attribute.getDesc()).append(TextFormatting.WHITE).append("\n");
				} else {
					sb.append("\n");
				}
			}
		}
		if(effect.getEffect().getTimeType() != EffectTimeType.PERMANENT) sb.append("(").append(effect.getTimeLeft()/20).append("s)");
		if(effect.getEffect().getTimeType() == EffectTimeType.ADD) sb.append(" (+").append(effect.getEffect().getDurationIncrease()/20).append("s/ea)");
		tooltip.render(sb.toString(),mouseX,mouseY,4,4);
	}

	private void drawEffect(Minecraft mc, EffectStack effect, int x, int y, int mouseX, int mouseY) {
		drawRectWidthHeight(x,y,20,20,effect.getEffect().color);
		end();
		if(effect.getEffect().imagePath != null && !effect.getEffect().imagePath.isEmpty()){
			mc.textureManager.loadTexture("/assets/"+effect.getEffect().id.split(":")[0]+"/effects/icons/"+effect.getEffect().imagePath).bind();
			GL11.glColor4f(1,1,1,1);
			GL11.glDisable(GL11.GL_LIGHTING);
			drawTexturedModalRect(x, y, 0, 0, 20, 20,16,1/16f);
			GL11.glEnable(GL11.GL_LIGHTING);
		} else if (effect.getEffect().icon != null) {
			GL11.glColor4f(1,1,1,1);
			GL11.glDisable(GL11.GL_LIGHTING);
			drawTexturedIcon(x,y,20,20,effect.getEffect().icon);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		drawString(mc.font,"x"+effect.getAmount(),x+1,y+10,0xFFFFFFFF);
		begin();
		drawRectWidthHeight(x,y,20, (int) (20-((float)effect.getTimeLeft()/(float)effect.getDuration())*20), 0x80000000);
	}

	private void begin(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	private void end(){
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
