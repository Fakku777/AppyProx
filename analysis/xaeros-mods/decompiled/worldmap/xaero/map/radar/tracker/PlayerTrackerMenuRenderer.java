/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_640
 */
package xaero.map.radar.tracker;

import java.io.IOException;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_640;
import xaero.map.WorldMap;
import xaero.map.element.MapElementMenuRenderer;
import xaero.map.element.render.ElementRenderer;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.misc.Misc;
import xaero.map.radar.tracker.PlayerTrackerIconRenderer;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderContext;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderProvider;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderer;
import xaero.map.radar.tracker.PlayerTrackerMenuRenderContext;

public final class PlayerTrackerMenuRenderer
extends MapElementMenuRenderer<PlayerTrackerMapElement<?>, PlayerTrackerMenuRenderContext> {
    private final PlayerTrackerIconRenderer iconRenderer;
    private final PlayerTrackerMapElementRenderer renderer;
    private class_4185 showPlayersButton;

    private PlayerTrackerMenuRenderer(PlayerTrackerMapElementRenderer renderer, PlayerTrackerIconRenderer iconRenderer, PlayerTrackerMenuRenderContext context, PlayerTrackerMapElementRenderProvider<PlayerTrackerMenuRenderContext> provider) {
        super(context, provider);
        this.iconRenderer = iconRenderer;
        this.renderer = renderer;
    }

    @Override
    public void onMapInit(GuiMap screen, class_310 mc, int width, int height) {
        super.onMapInit(screen, mc, width, height);
        CursorBox showPlayersTooltip = new CursorBox((class_2561)class_2561.method_43469((String)(WorldMap.settings.trackedPlayers ? "gui.xaero_box_showing_tracked_players" : "gui.xaero_box_hiding_tracked_players"), (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(screen.getTrackedPlayerKeyBinding())).method_27694(s -> s.method_10977(class_124.field_1077))}), true);
        this.showPlayersButton = new GuiTexturedButton(width - 173, height - 33, 20, 20, WorldMap.settings.trackedPlayers ? 197 : 213, 48, 16, 16, WorldMap.guiTextures, b -> this.onShowPlayersButton(screen, width, height), () -> showPlayersTooltip, 256, 256);
        screen.addButton(this.showPlayersButton);
    }

    public void onShowPlayersButton(GuiMap screen, int width, int height) {
        WorldMap.settings.trackedPlayers = !WorldMap.settings.trackedPlayers;
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        screen.method_25423(this.mc, width, height);
        screen.method_25395((class_364)this.showPlayersButton);
    }

    @Override
    protected void beforeMenuRender() {
    }

    @Override
    protected void afterMenuRender() {
    }

    @Override
    public void renderInMenu(PlayerTrackerMapElement<?> element, class_332 guiGraphics, class_437 gui, int mouseX, int mouseY, double scale, boolean enabled, boolean hovered, class_310 mc, boolean pressed, int textX) {
        PlayerTrackerMapElement<?> playerElement = element;
        class_640 info = mc.method_1562().method_2871(playerElement.getPlayerId());
        if (info != null) {
            class_1657 clientPlayer = mc.field_1687.method_18470(playerElement.getPlayerId());
            this.iconRenderer.renderIconGUI(guiGraphics, clientPlayer, this.renderer.getTrackedPlayerIconManager().getPlayerSkin(clientPlayer, info));
        }
    }

    @Override
    protected void beforeFiltering() {
    }

    @Override
    public int menuStartPos(int height) {
        return height - 59;
    }

    @Override
    public int menuSearchPadding() {
        return 1;
    }

    @Override
    protected String getFilterPlaceholder() {
        return "gui.xaero_filter_players_by_name";
    }

    @Override
    protected ElementRenderer<? super PlayerTrackerMapElement<?>, ?, ?> getRenderer(PlayerTrackerMapElement<?> element) {
        return this.renderer;
    }

    public boolean canJumpTo(PlayerTrackerMapElement<?> element) {
        return !this.renderer.getReader().isHidden(element, (PlayerTrackerMapElementRenderContext)this.renderer.getContext());
    }

    public static final class Builder {
        private PlayerTrackerMapElementRenderer renderer;

        private Builder() {
        }

        private Builder setDefault() {
            this.setRenderer(null);
            return this;
        }

        public Builder setRenderer(PlayerTrackerMapElementRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public PlayerTrackerMenuRenderer build() {
            if (this.renderer == null) {
                throw new IllegalStateException();
            }
            return new PlayerTrackerMenuRenderer(this.renderer, new PlayerTrackerIconRenderer(), new PlayerTrackerMenuRenderContext(), new PlayerTrackerMapElementRenderProvider<PlayerTrackerMenuRenderContext>(this.renderer.getCollector()));
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

