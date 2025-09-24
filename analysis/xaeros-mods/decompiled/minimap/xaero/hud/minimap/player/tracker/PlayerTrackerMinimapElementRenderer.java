/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1068
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_640
 *  net.minecraft.class_742
 */
package xaero.hud.minimap.player.tracker;

import java.util.UUID;
import net.minecraft.class_1068;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_640;
import net.minecraft.class_742;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.player.tracker.PlayerTrackerIconRenderer;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElement;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementCollector;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementReader;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderContext;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderProvider;
import xaero.hud.render.util.RenderBufferUtil;

public final class PlayerTrackerMinimapElementRenderer
extends MinimapElementRenderer<PlayerTrackerMinimapElement<?>, PlayerTrackerMinimapElementRenderContext> {
    private final double WORLD_MINIMUM_DISTANCE = 10.0;
    private final double WORLD_FADING_LENGTH = 10.0;
    private class_4597.class_4598 minimapBufferSource;
    private final PlayerTrackerMinimapElementCollector elementCollector;
    private final PlayerTrackerIconRenderer playerTrackerIconRenderer;
    private final IXaeroMinimap modMain;
    private float nameScale;

    private PlayerTrackerMinimapElementRenderer(PlayerTrackerMinimapElementCollector elementCollector, IXaeroMinimap modMain, PlayerTrackerMinimapElementRenderContext context, PlayerTrackerMinimapElementRenderProvider<PlayerTrackerMinimapElementRenderContext> provider, PlayerTrackerMinimapElementReader reader, PlayerTrackerIconRenderer playerTrackerIconRenderer) {
        super(reader, provider, context);
        this.elementCollector = elementCollector;
        this.modMain = modMain;
        this.playerTrackerIconRenderer = playerTrackerIconRenderer;
    }

    public class_2960 getPlayerSkin(class_1657 player, class_640 info) {
        class_2960 skinTextureLocation;
        class_2960 class_29602 = skinTextureLocation = player instanceof class_742 ? ((class_742)player).method_52814().comp_1626() : info.method_52810().comp_1626();
        if (skinTextureLocation == null) {
            skinTextureLocation = class_1068.method_4648((UUID)player.method_5667()).comp_1626();
        }
        return skinTextureLocation;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        vanillaBufferSource.method_22993();
        this.minimapBufferSource = this.modMain.getHudRenderer().getCustomVertexConsumers().getBetterPVPRenderTypeBuffers();
        ((PlayerTrackerMinimapElementRenderContext)this.context).coloredBackgroundConsumer = this.minimapBufferSource.getBuffer(CustomRenderTypes.COLORED_WAYPOINTS_BGS);
        ((PlayerTrackerMinimapElementRenderContext)this.context).uniqueTextureUIObjectRenderer = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.GUI);
        ((PlayerTrackerMinimapElementRenderContext)this.context).renderEntityDimId = renderInfo.renderEntityDimension;
        ((PlayerTrackerMinimapElementRenderContext)this.context).mapDimId = renderInfo.mapDimension;
        ((PlayerTrackerMinimapElementRenderContext)this.context).iconScale = renderInfo.location == MinimapElementRenderLocation.IN_WORLD ? HudMod.INSTANCE.getSettings().getTrackedPlayerWorldIconScale() : HudMod.INSTANCE.getSettings().getTrackedPlayerMinimapIconScale();
        this.nameScale = HudMod.INSTANCE.getSettings().getTrackedPlayerWorldNameScale();
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, class_4597.class_4598 vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        rendererProvider.draw(((PlayerTrackerMinimapElementRenderContext)this.context).uniqueTextureUIObjectRenderer);
        this.minimapBufferSource.method_22993();
        this.elementCollector.resetRenderedOnRadarFlags();
    }

    @Override
    public boolean renderElement(PlayerTrackerMinimapElement<?> e, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, MinimapElementGraphics guiGraphics, class_4597.class_4598 vanillaBufferSource) {
        float alpha;
        double trackedZ;
        double offZ;
        double trackedY;
        double offY;
        if (!outOfBounds && renderInfo.location != MinimapElementRenderLocation.IN_WORLD && e.wasRenderedOnRadar()) {
            return false;
        }
        class_4587 matrixStack = guiGraphics.pose();
        class_310 mc = class_310.method_1551();
        class_640 info = mc.method_1562().method_2871(e.getPlayerId());
        if (info == null) {
            return false;
        }
        class_1657 clientPlayer = mc.field_1687.method_18470(e.getPlayerId());
        double trackedX = clientPlayer == null ? e.getX() : EntityUtils.getEntityX((class_1297)clientPlayer, renderInfo.partialTicks);
        double offX = trackedX - renderInfo.renderEntityPos.field_1352;
        double distance = Math.sqrt(offX * offX + (offY = (trackedY = clientPlayer == null ? e.getY() : EntityUtils.getEntityY((class_1297)clientPlayer, renderInfo.partialTicks)) - renderInfo.renderEntityPos.field_1351) * offY + (offZ = (trackedZ = clientPlayer == null ? e.getZ() : EntityUtils.getEntityZ((class_1297)clientPlayer, renderInfo.partialTicks)) - renderInfo.renderEntityPos.field_1350) * offZ);
        if (distance < 10.0) {
            return false;
        }
        matrixStack.method_22903();
        matrixStack.method_22904(0.0, 0.0, optionalDepth);
        boolean inWorld = renderInfo.location == MinimapElementRenderLocation.IN_WORLD;
        float f = alpha = inWorld ? 0.5f : 1.0f;
        if (highlighted && inWorld) {
            alpha = 0.8f;
        }
        if (!highlighted && inWorld && distance < 20.0) {
            alpha *= (float)((distance - 10.0) / 10.0);
        }
        matrixStack.method_22904(0.0, 0.0, 0.01);
        matrixStack.method_22903();
        matrixStack.method_22905(((PlayerTrackerMinimapElementRenderContext)this.context).iconScale, ((PlayerTrackerMinimapElementRenderContext)this.context).iconScale, 1.0f);
        RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), ((PlayerTrackerMinimapElementRenderContext)this.context).coloredBackgroundConsumer, -5.0f, -5.0f, 10, 10, 1.0f, 1.0f, 1.0f, alpha);
        this.playerTrackerIconRenderer.renderIcon(mc, ((PlayerTrackerMinimapElementRenderContext)this.context).uniqueTextureUIObjectRenderer, matrixStack, clientPlayer, this.getPlayerSkin(clientPlayer, info), alpha);
        matrixStack.method_22909();
        if (highlighted && inWorld) {
            matrixStack.method_46416(-5.0f * ((PlayerTrackerMinimapElementRenderContext)this.context).iconScale, 0.0f, 0.0f);
            matrixStack.method_22905(this.nameScale, this.nameScale, 1.0f);
            String playerName = info.method_2966().getName();
            int playerNameWidth = mc.field_1772.method_1727(playerName);
            float labelAlpha = 0.3529412f;
            RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), ((PlayerTrackerMinimapElementRenderContext)this.context).coloredBackgroundConsumer, -playerNameWidth - 1, -5.0f, playerNameWidth + 1, 10, 0.0f, 0.0f, 0.0f, labelAlpha);
            mc.field_1772.method_27521(playerName, (float)(-playerNameWidth), -4.0f, -1, false, matrixStack.method_23760().method_23761(), (class_4597)this.minimapBufferSource, class_327.class_6415.field_33993, 0, 0xF000F0);
        }
        matrixStack.method_22909();
        return true;
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        return location != MinimapElementRenderLocation.IN_WORLD && this.modMain.getSettings().displayTrackedPlayersOnMap || location == MinimapElementRenderLocation.IN_WORLD && this.modMain.getSettings().displayTrackedPlayersInWorld;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    public PlayerTrackerMinimapElementCollector getCollector() {
        return this.elementCollector;
    }

    public static final class Builder {
        private final IXaeroMinimap modMain;

        private Builder(IXaeroMinimap modMain) {
            this.modMain = modMain;
        }

        private Builder setDefault() {
            return this;
        }

        public PlayerTrackerMinimapElementRenderer build() {
            PlayerTrackerMinimapElementCollector collector = new PlayerTrackerMinimapElementCollector(this.modMain.getRenderedPlayerTrackerManager());
            return new PlayerTrackerMinimapElementRenderer(collector, this.modMain, new PlayerTrackerMinimapElementRenderContext(), new PlayerTrackerMinimapElementRenderProvider<PlayerTrackerMinimapElementRenderContext>(collector), new PlayerTrackerMinimapElementReader(), new PlayerTrackerIconRenderer());
        }

        public static Builder begin(IXaeroMinimap modMain) {
            return new Builder(modMain).setDefault();
        }
    }
}

