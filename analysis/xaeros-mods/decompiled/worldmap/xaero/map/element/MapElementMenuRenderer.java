/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_3417
 *  net.minecraft.class_342
 *  net.minecraft.class_437
 *  net.minecraft.class_6880
 *  org.joml.Matrix3x2fStack
 */
package xaero.map.element;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraft.class_1074;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_6880;
import org.joml.Matrix3x2fStack;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.element.MapElementMenuHitbox;
import xaero.map.element.MapElementMenuScroll;
import xaero.map.element.MapElementRenderHandler;
import xaero.map.element.MenuHitboxReader;
import xaero.map.element.MenuScrollReader;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.element.render.ElementRenderer;
import xaero.map.gui.GuiMap;
import xaero.map.misc.Misc;

public abstract class MapElementMenuRenderer<E, C> {
    private static final int MENU_RIGHT_PADDING = 27;
    public static final int MAX_MENU_SIZE = 10;
    public static final int MIN_MENU_SIZE = 2;
    protected final MapElementMenuScroll scrollUp = new MapElementMenuScroll("gui.xaero_wm_up", "\u25b3", 1);
    protected final MapElementMenuScroll scrollDown = new MapElementMenuScroll("gui.xaero_wm_down", "\u25bd", -1);
    protected final MapElementMenuHitbox extraHitbox;
    protected final MenuScrollReader scrollReader = new MenuScrollReader();
    protected final MenuHitboxReader hitboxReader;
    protected final C context;
    protected final ElementRenderProvider<E, C> provider;
    protected ArrayList<E> filteredElements;
    private class_342 filterField;
    public int menuOffset = 0;
    protected Pattern searchPattern = null;
    protected Pattern searchStartPattern = null;
    protected final class_310 mc;

    protected MapElementMenuRenderer(C context, ElementRenderProvider<E, C> provider) {
        this.extraHitbox = new MapElementMenuHitbox(-150, 0, 177, 0);
        this.hitboxReader = new MenuHitboxReader();
        this.mc = class_310.method_1551();
        this.context = context;
        this.provider = provider;
    }

    public void onMapInit(GuiMap screen, class_310 mc, int width, int height) {
        String searchText = this.filterField == null ? "" : this.filterField.method_1882();
        this.filterField = new class_342(mc.field_1772, screen.field_22789 - 172, this.menuStartPos(height) + 3 + this.menuSearchPadding(), 150, 20, (class_2561)class_2561.method_43471((String)this.getFilterPlaceholder()));
        this.filterField.method_1852(searchText);
        this.filterField.method_1863(s -> this.updateSearch());
        screen.method_25429(this.filterField);
    }

    public HoveredMapElementHolder<?, ?> renderMenu(class_332 guiGraphics, GuiMap gui, double scale, int width, int height, int mouseX, int mouseY, boolean leftMousePressed, boolean leftMouseClicked, HoveredMapElementHolder<?, ?> oldHovered, class_310 mc) {
        int menuStartPos;
        if (this.filteredElements == null) {
            this.updateFilteredList();
        }
        ArrayList<E> elements = this.filteredElements;
        int menuElementCount = MapElementMenuRenderer.getMenuElementCount(this.menuStartPos(height));
        if (this.menuOffset + menuElementCount > elements.size()) {
            this.menuOffset = elements.size() - menuElementCount;
        }
        if (this.menuOffset < 0) {
            this.menuOffset = 0;
        }
        int offset = this.menuOffset;
        Object viewed = null;
        int yPos = menuStartPos = this.menuStartPos(height);
        int elementCount = MapElementMenuRenderer.getMenuElementCount(yPos);
        this.beforeMenuRender();
        viewed = this.renderMenuElement(guiGraphics, this.scrollDown, width, yPos -= 8, mouseX, mouseY, viewed, leftMousePressed, gui, offset > 0, mc);
        yPos -= 8;
        for (int i = offset; i < elements.size(); ++i) {
            viewed = this.renderMenuElement(guiGraphics, elements.get(i), width, yPos -= 8, mouseX, mouseY, viewed, leftMousePressed, gui, true, mc);
            yPos -= 8;
            if (i - offset == elementCount - 1) break;
        }
        viewed = this.renderMenuElement(guiGraphics, this.scrollUp, width, yPos -= 8, mouseX, mouseY, viewed, leftMousePressed, gui, offset < elements.size() - elementCount, mc);
        yPos -= 8;
        if (viewed != null && leftMouseClicked) {
            class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_15015, (float)1.0f));
        }
        if (leftMousePressed && viewed instanceof MapElementMenuScroll) {
            int direction = ((MapElementMenuScroll)viewed).scroll();
            this.menuOffset += direction;
        }
        if (viewed == null) {
            this.extraHitbox.setH(menuStartPos - yPos);
            this.extraHitbox.setY(yPos - menuStartPos);
            viewed = this.renderMenuElement(guiGraphics, this.extraHitbox, width, menuStartPos, mouseX, mouseY, viewed, leftMousePressed, gui, true, mc);
        }
        this.afterMenuRender();
        return oldHovered != null && oldHovered.equals(viewed) ? oldHovered : (viewed == null ? null : MapElementRenderHandler.createResult(viewed, this.getAnyRenderer(viewed)));
    }

    protected abstract void beforeMenuRender();

    protected abstract void afterMenuRender();

    public void postMapRender(class_332 guiGraphics, GuiMap gui, int scaledMouseX, int scaledMouseY, int width, int height, float partialTicks) {
        String searchText = this.filterField.method_1882();
        boolean searchFieldPlaceHolder = searchText.isEmpty() && !this.filterField.method_25370();
        boolean invalidRegex = false;
        if (searchFieldPlaceHolder) {
            Misc.setFieldText(this.filterField, class_1074.method_4662((String)this.getFilterPlaceholder(), (Object[])new Object[0]), -11184811);
        } else if (!searchText.isEmpty() && this.searchPattern == null) {
            invalidRegex = true;
        }
        this.filterField.method_25394(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        if (searchFieldPlaceHolder) {
            Misc.setFieldText(this.filterField, "");
        } else if (invalidRegex) {
            String errorMessage = class_1074.method_4662((String)"gui.xaero_wm_search_invalid_regex", (Object[])new Object[0]);
            guiGraphics.method_25303(this.mc.field_1772, errorMessage, width - 176 - this.mc.field_1772.method_1727(errorMessage), this.filterField.method_46427() + 6, -43691);
        }
    }

    public static int getMenuElementCount(int menuStartPos) {
        return Math.min(10, Math.max(2, (menuStartPos - 34) / 16 - 2));
    }

    private <O> Object renderMenuElement(class_332 guiGraphics, O element, int width, int yPos, int mouseX, int mouseY, Object viewed, boolean leftMousePressed, class_437 gui, boolean enabled, class_310 mc) {
        boolean hovered;
        MenuScrollReader reader = element == this.scrollDown || element == this.scrollUp ? this.scrollReader : (element == this.extraHitbox ? this.hitboxReader : this.getAnyRenderer(element).getReader());
        int xPos = width - 27;
        boolean bl = hovered = viewed == null && reader.isMouseOverMenuElement(element, xPos, yPos, mouseX, mouseY, mc);
        if (hovered) {
            viewed = element;
        }
        if (element != this.extraHitbox) {
            this.renderMenuElement(reader, element, guiGraphics, gui, xPos, yPos, mouseX, mouseY, 1.0, enabled, hovered, mc, leftMousePressed);
        }
        return viewed;
    }

    public <O> void renderMenuElement(ElementReader<O, ?, ?> reader, O element, class_332 guiGraphics, class_437 gui, int x, int y, int mouseX, int mouseY, double scale, boolean enabled, boolean hovered, class_310 mc, boolean pressed) {
        Matrix3x2fStack matrixStack = guiGraphics.method_51448();
        matrixStack.pushMatrix();
        if (hovered) {
            matrixStack.translate(pressed ? 1.0f : 2.0f, 0.0f);
        }
        matrixStack.translate((float)x, (float)y);
        matrixStack.scale((float)scale, (float)scale);
        matrixStack.translate(-4.0f, -4.0f);
        String name = reader.getMenuName(element);
        int len = mc.field_1772.method_1727(name);
        int textX = -3 - len;
        guiGraphics.method_25294(textX - 2 - reader.getMenuTextFillLeftPadding(element), -2, textX + len + 2, 11, 0x77000000);
        guiGraphics.method_25303(mc.field_1772, name, textX, 0, enabled ? -1 : -11184811);
        matrixStack.translate(4.0f, 4.0f);
        if (element == this.scrollUp || element == this.scrollDown) {
            this.renderScroll((MapElementMenuScroll)element, guiGraphics, gui, mouseX, mouseY, scale, enabled, hovered, mc, pressed);
        } else {
            O elementCast = element;
            this.renderInMenu(elementCast, guiGraphics, gui, mouseX, mouseY, scale, enabled, hovered, mc, pressed, textX);
        }
        matrixStack.popMatrix();
    }

    public void onMapMouseRelease(double par1, double par2, int par3) {
        this.releaseScroll();
    }

    private void releaseScroll() {
        this.scrollUp.onMouseRelease();
        this.scrollDown.onMouseRelease();
    }

    private void renderScroll(MapElementMenuScroll scroll, class_332 guiGraphics, class_437 gui, int mouseX, int mouseY, double scale, boolean enabled, boolean hovered, class_310 mc, boolean pressed) {
        Matrix3x2fStack matrixStack = guiGraphics.method_51448();
        if (enabled && hovered) {
            matrixStack.translate(pressed ? 1.0f : 2.0f, 0.0f);
        }
        matrixStack.translate(-4.0f, -4.0f);
        int color = enabled ? -1 : -11184811;
        guiGraphics.method_25303(mc.field_1772, scroll.getIcon(), 5 - mc.field_1772.method_1727(scroll.getIcon()) / 2, 1, color);
    }

    private void updateSearch() {
        String search = this.filterField.method_1882();
        try {
            this.searchPattern = Pattern.compile(search.toLowerCase());
            if (search.length() > 0) {
                this.searchStartPattern = search.charAt(0) == '^' ? this.searchPattern : Pattern.compile("^" + search.toString().toLowerCase());
            } else {
                this.searchStartPattern = null;
                this.searchPattern = null;
            }
        }
        catch (PatternSyntaxException e) {
            this.searchStartPattern = null;
            this.searchPattern = null;
        }
        this.updateFilteredList();
    }

    public boolean charTyped() {
        return this.filterField.method_25370();
    }

    public boolean keyPressed(GuiMap screen, int keyCode) {
        if (screen.method_25399() == this.filterField) {
            if (keyCode == 257) {
                this.filterField.method_1852("");
            }
            return true;
        }
        return false;
    }

    public void mouseScrolled(int direction) {
        this.scroll(direction);
    }

    public void tick() {
    }

    public void unfocusAll() {
        if (this.filterField != null) {
            this.filterField.method_25365(false);
        }
    }

    public void onMenuClosed() {
        this.menuOffset = 0;
        this.searchPattern = null;
        this.searchStartPattern = null;
        this.updateFilteredList();
        this.filterField = null;
    }

    private void scroll(int direction) {
        this.menuOffset += direction;
    }

    public Pattern getSearchPattern() {
        return this.searchPattern;
    }

    public Pattern getSearchStartPattern() {
        return this.searchStartPattern;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateFilteredList() {
        ElementRenderProvider<E, C> provider = this.provider;
        if (provider == null) {
            this.filteredElements = null;
            return;
        }
        if (this.filteredElements == null) {
            this.filteredElements = new ArrayList();
        } else {
            this.filteredElements.clear();
        }
        Pattern regex = this.searchPattern;
        Pattern regexStartsWith = this.searchStartPattern;
        this.beforeFiltering();
        provider.begin(ElementRenderLocation.WORLD_MAP_MENU, this.context);
        try {
            while (provider.hasNext(ElementRenderLocation.WORLD_MAP_MENU, this.context)) {
                E e = provider.getNext(ElementRenderLocation.WORLD_MAP_MENU, this.context);
                if (regex == null) {
                    this.filteredElements.add(e);
                    continue;
                }
                String filterName = this.getRenderer(e).getReader().getFilterName(e).toLowerCase();
                if (regexStartsWith.matcher(filterName).find()) {
                    this.filteredElements.add(0, e);
                    continue;
                }
                if (!regex.matcher(filterName).find()) continue;
                this.filteredElements.add(e);
            }
        }
        finally {
            provider.end(ElementRenderLocation.WORLD_MAP_MENU, this.context);
        }
    }

    protected <O> ElementRenderer<? super O, ?, ?> getAnyRenderer(O element) {
        if (element == this.scrollDown || element == this.scrollUp || element == this.extraHitbox) {
            return null;
        }
        return this.getRenderer(element);
    }

    protected abstract ElementRenderer<? super E, ?, ?> getRenderer(E var1);

    public abstract int menuStartPos(int var1);

    public abstract int menuSearchPadding();

    public abstract void renderInMenu(E var1, class_332 var2, class_437 var3, int var4, int var5, double var6, boolean var8, boolean var9, class_310 var10, boolean var11, int var12);

    protected abstract String getFilterPlaceholder();

    protected abstract void beforeFiltering();
}

