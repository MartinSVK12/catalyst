package sunsetsatellite.catalyst.screens.component;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.gui.options.components.BooleanToggleComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.option.OptionEnum;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import sunsetsatellite.catalyst.Catalyst;
import sunsetsatellite.catalyst.screens.component.option.BasicTextFieldComponent;
import sunsetsatellite.catalyst.screens.util.Colors;
import sunsetsatellite.catalyst.screens.util.TextAlign;

import static sunsetsatellite.catalyst.CatalystScreens.lang;

public class TextComponent extends GuiComponent {

	public static final String ID = "text";

    public int lineHeight = 9;
    protected final int padding = 8;
    protected int offY = padding;
    protected int posX = 0;
    protected float scale = 1f;
    public Minecraft minecraft = Minecraft.getMinecraft();
    protected Gui gui;
    protected int xScreenSize;
    protected int yScreenSize;
    private int posY = 0;

	public OptionEnum<TextAlign> align = new OptionEnum<>("align",TextAlign.class,TextAlign.LEFT);
	public String text = "This is a text component!";
	public boolean hasShadow = true;
	public int color = Colors.WHITE;

    public TextComponent(String name, float x, float y) {
        super(name, 152, 20, new LayoutAbsolute(x,y, ComponentAnchor.CENTER));
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
    public void render(HudIngame HudIngame, int xScreenSize, int yScreenSize, float partialTick){

    }

	@Override
	public void render(Screen screen, int xScreenSize, int yScreenSize, float partialTick) {
		this.gui = screen;
		this.xScreenSize = xScreenSize;
		this.yScreenSize = yScreenSize;
		posY = offY = generateOriginalPosY();
		posX = generateOriginalPosX();
		renderComponent(minecraft, screen, xScreenSize, yScreenSize, partialTick);
	}

	@Override
    public void renderPreview(Gui gui, Layout layout, int xScreenSize, int yScreenSize){
        this.gui = gui;
        this.xScreenSize = xScreenSize;
        this.yScreenSize = yScreenSize;
        posY = offY = generateOriginalPosY();
        posX = generateOriginalPosX();
        renderComponentPreview(minecraft, gui, layout, xScreenSize, yScreenSize);
    }

    public int getLineHeight(){
        return lineHeight;
    }
    public int height(){
        return offY - posY;
    }

	@Override
	public void renderComponent(Minecraft mc, Screen screen, int xSize, int ySize, float partialTick) {
		setOffY(0);
		drawString(text, 0, color, hasShadow);
	}

	@Override
	public void renderComponentPreview(Minecraft mc, Gui gui, Layout layout, int xSize, int ySize) {
		drawString(text, 0, color, hasShadow);
	}

	@Override
	public String getName() {
		return getKey();
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

	@Override
	public boolean isVisible() {
		return true;
	}

	public int generateOriginalPosY() {
        return getLayout().getComponentY(this, yScreenSize);
    }

    public int generateOriginalPosX() {
        return getLayout().getComponentX(this, xScreenSize);
    }

    public void drawString(String text, int offX, int color, boolean shadow) {
        int width = minecraft.font.stringWidth(text);
		if(shadow){
			minecraft.font.render(text, posX+offX + getStartingX(width), offY).setZ(0).setColor(color).setShadow().setZ(zLevel).call();
		} else {
			minecraft.font.render(text, posX+offX + getStartingX(width), offY).setZ(0).setColor(color).setZ(zLevel).call();
		}
        addOffY(getLineHeight());
    }
    public int getStartingX(int width){
        int diff = getBaseXSize() - width;
		return switch (align.value) {
			case LEFT -> 0;
			case CENTER -> diff / 2;
			case RIGHT -> diff;
		};
    }

    public void drawString(String text, int offX, boolean shadow) {
        drawString(text, offX, Colors.WHITE, shadow);
    }
    public void drawStringJustified(String text, int offX, int maxWidth, int color, boolean shadow){
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
                    drawString(line.toString(), offX, color, shadow);
                    line.setLength(0);
                    wordCount = 0;
                    continue;
                }
                drawString(prevline.toString(), offX, color, shadow);
                line = new StringBuilder(word).append(" ");
                wordCount = 0;
            }
        }
        String remainder = line.toString();
        if (!remainder.isEmpty()){
            drawString(remainder, offX, color, shadow);
        }
    }
    public void drawStringCentered(String text){
        drawStringCentered(text, Colors.WHITE);
    }
    public void drawStringCentered(String text, int color){
        minecraft.font.renderCentered(text, posX + (xSize /2), offY).setColor(color).setShadow().call();
        addOffY(getLineHeight());
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

    protected void drawRect(int minX, int minY, int maxX, int maxY, int argb) {
        int temp;
        if (minX < maxX) {
            temp = minX;
            minX = maxX;
            maxX = temp;
        }
        if (minY < maxY) {
            temp = minY;
            minY = maxY;
            maxY = temp;
        }
        float a = (float)(argb >> 24 & 0xFF) / 255.0f;
        float r = (float)(argb >> 16 & 0xFF) / 255.0f;
        float g = (float)(argb >> 8 & 0xFF) / 255.0f;
        float b = (float)(argb & 0xFF) / 255.0f;
        TessellatorGeneral tessellator = GLRenderer.getTessellator();
        GLRenderer.enableState(State.BLEND);
        GLRenderer.setShader(Shaders.COLOR);
        GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
        GLRenderer.setColor4f(r, g, b, a);
        tessellator.startDrawingQuads();
        tessellator.addVertex(minX, maxY, 0.0);
        tessellator.addVertex(maxX, maxY, 0.0);
        tessellator.addVertex(maxX, minY, 0.0);
        tessellator.addVertex(minX, minY, 0.0);
        tessellator.draw();
        GLRenderer.disableState(State.BLEND);
    }

	@Override
	protected void addDefaultOptionSuppliers() {
		super.addDefaultOptionSuppliers();
		addOptionComponentSupplier(()->{
			OptionsCategory textCategory = new OptionsCategory(lang("text"));
			textCategory.withComponent(new BasicTextFieldComponent(lang("text"), null, text,
				(t)-> text = t.getText()));
			textCategory.withComponent(new ToggleableOptionComponent<>(align));
			textCategory.withComponent(new BooleanToggleComponent(lang("hasShadow"),hasShadow,
				()-> hasShadow,
				(b)-> hasShadow = b)
			);
			textCategory.withComponent(new BasicTextFieldComponent(lang("color"), null, String.format("%X",color),
				()-> color = Colors.WHITE,
				(t)-> color = Catalyst.parseIntSafe(t.getText(),16)));
			return textCategory;
		});
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
