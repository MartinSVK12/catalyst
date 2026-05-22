package sunsetsatellite.catalyst.screens.component.option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ItemElement;
import net.minecraft.client.gui.options.ScreenOptions;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyCategory implements OptionsComponent {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final ItemElement renderItem = new ItemElement(mc);
	private static final int PADDING = 2;
	private static final int TOP_SPACING = PADDING + 10 + 10 + PADDING;
	private static final int COMPONENT_LEFT_MARGIN = 16;

	private final String translationKey;
	private final Map<String, OptionsComponent> components = new HashMap<>();
	private OptionsComponent clickedComponent = null;
	private int clickedComponentY = 0;

	private boolean collapsed = false;
	protected boolean visible = true;

	private ItemStack icon = null;

	private final @Nullable String tooltipTranslationKey;
	private @Nullable String shownTooltipTranslationKey = null;

	public PropertyCategory(String translationKey) {
		this(translationKey, null);
	}

	public PropertyCategory(String translationKey, ItemStack icon) {
		this(translationKey, icon, null);
	}

	public PropertyCategory(String translationKey, ItemStack icon, @Nullable String tooltipTranslationKey) {
		this.translationKey = translationKey;
		this.tooltipTranslationKey = tooltipTranslationKey;
		this.icon = icon;
	}

	public PropertyCategory withComponent(String id, OptionsComponent component) {
		if (component != null) {
			this.components.put(id, component);
		}
		return this;
	}

	public PropertyCategory remove(String id){
		components.remove(id);
		 return this;
	}

	@Override
	public void init(Minecraft mc) {
		for (OptionsComponent component : this.components.values()) {
			component.init(mc);
		}
	}

	@Override
	public @Nullable String getTooltipTranslationKey() {
		return this.shownTooltipTranslationKey;
	}

	@Override
	public void tick() {
		for (OptionsComponent component : this.components.values()) {
			component.tick();
		}
	}

	@Override
	public int getHeight() {
		int height = TOP_SPACING;
		if (!this.collapsed) {
			for (OptionsComponent component : this.components.values()) {
				height += component.getHeight();
			}
		}
		return height;
	}

	@Override
	public void render(@NotNull ScreenOptions screenOptions, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		I18n i18n = I18n.getInstance();
		int textColor = 0x7F7F7F;
		this.shownTooltipTranslationKey = null;
		if (relativeMouseX >= 0 && relativeMouseX < width && relativeMouseY >= 0 && relativeMouseY < TOP_SPACING) {
			textColor = 0xFFFFA0;
			this.shownTooltipTranslationKey = this.tooltipTranslationKey;

		}
		mc.font.render((this.collapsed ? "▷" : "▽"), x, y + PADDING + 10).setShadow().setColor(textColor).call();

		int xOffset = 10;
		if (this.icon != null) {
			renderItem.render(this.icon, x + 11, y + PADDING + 5);
			xOffset += 20;
		}
		mc.font.render(i18n.translateKey(this.translationKey), x + xOffset, y + PADDING + 10).setShadow().setColor(textColor).call();
		if (!this.collapsed) {
			int componentY = TOP_SPACING;
			for (OptionsComponent component : this.components.values()) {
				if (y + componentY + component.getHeight() >= screenOptions.top && y + componentY <= screenOptions.bottom) {
					component.render(screenOptions, x + COMPONENT_LEFT_MARGIN, y + componentY, width - COMPONENT_LEFT_MARGIN, relativeMouseX - COMPONENT_LEFT_MARGIN, relativeMouseY - componentY);
				}
				componentY += component.getHeight();

				@Nullable String componentTooltipTranslationKey = component.getTooltipTranslationKey();
				if (componentTooltipTranslationKey != null) {
					this.shownTooltipTranslationKey = componentTooltipTranslationKey;
				}
			}
		}
	}

	@Override
	public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		if (relativeMouseX >= 0 && relativeMouseX < width && relativeMouseY >= 0 && relativeMouseY < TOP_SPACING) {
			this.collapsed = !this.collapsed;
			mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
		}
		int componentY = TOP_SPACING;
		for (OptionsComponent component : this.components.values()) {
			if (relativeMouseX >= COMPONENT_LEFT_MARGIN && relativeMouseX < width && relativeMouseY >= componentY && relativeMouseY < componentY + component.getHeight()) {
				component.onMouseClick(mouseButton, x + COMPONENT_LEFT_MARGIN, componentY, width - COMPONENT_LEFT_MARGIN, relativeMouseX - COMPONENT_LEFT_MARGIN, relativeMouseY - componentY);
				this.clickedComponent = component;
				this.clickedComponentY = componentY;
				return;
			}
			componentY += component.getHeight();
		}
	}

	@Override
	public void onMouseMove(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		if (this.clickedComponent != null) {
			this.clickedComponent.onMouseMove(x + COMPONENT_LEFT_MARGIN, this.clickedComponentY, width - COMPONENT_LEFT_MARGIN, relativeMouseX - COMPONENT_LEFT_MARGIN, relativeMouseY - this.clickedComponentY);
		}
	}

	@Override
	public void onMouseRelease(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		if (this.clickedComponent != null) {
			this.clickedComponent.onMouseRelease(mouseButton, x + COMPONENT_LEFT_MARGIN, this.clickedComponentY, width - COMPONENT_LEFT_MARGIN, relativeMouseX - COMPONENT_LEFT_MARGIN, relativeMouseY - this.clickedComponentY);
		}
	}

	@Override
	public void onKeyPress(int keyCode, char character) {
		for (OptionsComponent component : this.components.values()) {
			component.onKeyPress(keyCode, character);
		}
	}

	public PropertyCategory filter(String term) {
		PropertyCategory category = new PropertyCategory(this.translationKey);
		this.components.forEach((k,v)->{
			if (v.matchesSearchTerm(term)) {
				category.withComponent(k,v);
			}
		});
		if (category.components.isEmpty()) {
			return null;
		}
		return category;
	}

	@Override
	public void onClose() {
		for (OptionsComponent component : this.components.values()) {
			component.onClose();
		}
	}

	@Override
	public boolean matchesSearchTerm(String term) {
		return false;
	}
}
