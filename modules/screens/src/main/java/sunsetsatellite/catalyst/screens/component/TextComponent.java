package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.BooleanToggleComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.OptionEnum;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.screens.component.base.GuiComponent;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.component.option.PropertyCategory;
import sunsetsatellite.catalyst.screens.util.Colors;
import sunsetsatellite.catalyst.screens.util.TextAlign;

import java.util.*;
import java.util.function.Supplier;



public class TextComponent extends GuiComponent {

	public static final String ID = "text";

    public int lineHeight = 9;
    protected final int padding = 8;
    protected int offY = padding;
    protected float scale = 1f;
    public Minecraft minecraft = Minecraft.getMinecraft();

	public OptionEnum<TextAlign> align = new OptionEnum<>("align",TextAlign.class,TextAlign.LEFT);
	public String text = "This is a text component!";
	public boolean hasShadow = true;
	public int color = Colors.WHITE;
	public boolean centered = false;

    public TextComponent(String name, float x, float y) {
        super(name, 176, 20, new LayoutAbsolute(x,y, ComponentAnchor.TOP_LEFT));
    }

	@Override
	public int getBaseXSize() {
		return xSize;
	}

	@Override
	public int getBaseYSize() {
		return ySize;
	}

	@Override
	public void render(Screen screen, int xScreenSize, int yScreenSize, float partialTick) {
		super.render(screen, xScreenSize, yScreenSize, partialTick);
	}

	@Override
    public void renderPreview(Gui gui, Layout layout, int xScreenSize, int yScreenSize){
		super.renderPreview(gui, layout, xScreenSize, yScreenSize);
    }

    public int getLineHeight(){
        return lineHeight;
    }
    public int height(){
        return offY - posY;
    }

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int x, int y, int xScreenSize, int yScreenSize, float partialTick) {
		if(!visible) return;
		if(centered) {
			drawStringCentered(text, x,y, color, hasShadow);
			return;
		}
		drawString(text, x,y, 0, color, hasShadow);
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int x, int y, int xScreenSize, int yScreenSize) {
		if(centered) {
			drawStringCentered(text, x,y, color, hasShadow);
			return;
		}
		drawString(text, x,y, 0, color, hasShadow);
	}

	@Override
	public String getId() {
		return ID;
	}

	public Minecraft getGame() {
        return minecraft;
    }

    public  void addOffY(int offset) {
        offY += offset;
    }
    public  void subOffY(int offset) {
        offY -= offset;
    }

    public  int getOffY() { return offY; }
    public  int getPosX() { return posX; }

    public  void setOffY(int y) { offY = y; }
    public  void setPosX(int x) { posX = x; }

    protected void setScale(float scale) { this.scale = scale; }
    public  float getScale() { return scale; }



    public void drawString(String text, int x, int y, int offX, int color, boolean shadow) {
        int width = minecraft.font.stringWidth(text);
		if(shadow){
			minecraft.font.render(text, x+offX + getStartingX(width), y+offY).setZ(0).setColor(color).setShadow()/*.setZ(zLevel)*/.call();
		} else {
			minecraft.font.render(text, x+offX + getStartingX(width), y+offY).setZ(0).setColor(color)/*.setZ(zLevel)*/.call();
		}
    }
    public int getStartingX(int width){
        int diff = getBaseXSize() - width;
		return switch (align.value) {
			case LEFT -> 0;
			case CENTER -> diff / 2;
			case RIGHT -> diff;
		};
    }

    public void drawString(String text, int x, int y, int offX, boolean shadow) {
        drawString(text, x, y, offX, Colors.WHITE, shadow);
    }
    public void drawStringJustified(String text, int x, int y, int offX, int maxWidth, int color, boolean shadow){
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder prevline;
        int wordCount = 0;
        for (String word: words) {
            prevline = new StringBuilder(line.toString());
            line.append(word).append(" ");
            wordCount++;
            if (minecraft.font.stringWidth(line.toString().trim()) > maxWidth){
                if (wordCount <= 1){
                    drawString(line.toString(), x, y, offX, color, shadow);
                    line.setLength(0);
                    wordCount = 0;
                    continue;
                }
                drawString(prevline.toString(), x, y, offX, color, shadow);
                line = new StringBuilder(word).append(" ");
                wordCount = 0;
            }
        }
        String remainder = line.toString();
        if (!remainder.isEmpty()){
            drawString(remainder, x, y, offX, color, shadow);
        }
    }

    public void drawStringCentered(String text, int x, int y, int color, boolean shadow){
		if(shadow){
			minecraft.font.renderCentered(text, x + (xSize /2), y+offY).setColor(color).setShadow()/*.setZ(zLevel)*/.call();
		} else {
			minecraft.font.renderCentered(text, x + (xSize /2), y+offY).setColor(color)/*.setZ(zLevel)*/.call();
		}
    }

    public void drawTexturedModalRect(double x, double y, double width, double height, float percent) {
        float z = 0.0f;
        TessellatorGeneral tessellator = GLRenderer.getTessellator();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((x + 0),     (y + height), z, 0,       1);
        tessellator.addVertexWithUV((x + width), (y + height), z, percent, 1);
        tessellator.addVertexWithUV((x + width), (y + 0),      z, percent, 0);
        tessellator.addVertexWithUV((x + 0),     (y + 0),      z, 0,       0);
        tessellator.draw();
    }

	@Override
	public Map<String, OptionsComponent> getProperties() {
		Map<String, OptionsComponent> map = new TreeMap<>();
		PropertyCategory textCategory = new PropertyCategory(lang("text"));
		textCategory.withComponent("text",new BasicTextFieldComponent(lang("text"), null, text,
			(t)-> text = t.getText()));
		textCategory.withComponent("align",new ToggleableOptionComponent<>(align));
		textCategory.withComponent("hasShadow",new BooleanToggleComponent(lang("hasShadow"),hasShadow,
			()-> hasShadow,
			(b)-> hasShadow = b)
		);
		textCategory.withComponent("color",new BasicTextFieldComponent(lang("color"), null, String.format("%X",color),
			()-> color = Colors.WHITE,
			(t)-> color = Catalyst.parseIntSafe(t.getText(),16)));
		map.put("textCategory",textCategory);
		return map;
	}

	@Override
	public void addOptions() {
		super.addOptions();
		for (OptionsComponent property : getProperties().values()) {
			addOptionComponentSupplier(()->property);
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		super.writeToNbt(tag);
		tag.putString("text", text);
		tag.putInt("align", align.getValueIndex());
		tag.putBoolean("hasShadow", hasShadow);
		tag.putInt("color", color);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		super.readFromNbt(tag);
		text = tag.getString("text");
		align.setValueWithIndex(tag.getInteger("align"));
		hasShadow = tag.getBoolean("hasShadow");
		color = tag.getInteger("color");
	}
}
