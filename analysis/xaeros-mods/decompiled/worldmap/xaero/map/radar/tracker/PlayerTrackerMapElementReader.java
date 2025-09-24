/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_640
 */
package xaero.map.radar.tracker;

import java.util.ArrayList;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_640;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.element.render.ElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.SupportMods;
import xaero.map.radar.tracker.PlayerTeleporter;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderContext;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderer;

public class PlayerTrackerMapElementReader
extends ElementReader<PlayerTrackerMapElement<?>, PlayerTrackerMapElementRenderContext, PlayerTrackerMapElementRenderer> {
    @Override
    public boolean isHidden(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context) {
        return class_310.method_1551().field_1687.method_27983() != element.getDimension() && context.mapDimId != element.getDimension();
    }

    @Override
    public double getRenderX(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        if (class_310.method_1551().field_1687.method_27983() != element.getDimension()) {
            return element.getX() * context.mapDimDiv;
        }
        return element.getX();
    }

    @Override
    public double getRenderZ(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        if (class_310.method_1551().field_1687.method_27983() != element.getDimension()) {
            return element.getZ() * context.mapDimDiv;
        }
        return element.getZ();
    }

    @Override
    public int getInteractionBoxLeft(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return -16;
    }

    @Override
    public int getInteractionBoxRight(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return 16;
    }

    @Override
    public int getInteractionBoxTop(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return -16;
    }

    @Override
    public int getInteractionBoxBottom(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return 16;
    }

    @Override
    public int getRenderBoxLeft(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return -20;
    }

    @Override
    public int getRenderBoxRight(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return 20;
    }

    @Override
    public int getRenderBoxTop(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return -20;
    }

    @Override
    public int getRenderBoxBottom(PlayerTrackerMapElement<?> element, PlayerTrackerMapElementRenderContext context, float partialTicks) {
        return 20;
    }

    @Override
    public int getLeftSideLength(PlayerTrackerMapElement<?> element, class_310 mc) {
        class_640 info = class_310.method_1551().method_1562().method_2871(element.getPlayerId());
        if (info == null) {
            return 9;
        }
        return 9 + mc.field_1772.method_1727(info.method_2966().getName());
    }

    @Override
    public String getMenuName(PlayerTrackerMapElement<?> element) {
        class_640 info = class_310.method_1551().method_1562().method_2871(element.getPlayerId());
        if (info == null) {
            return String.valueOf(element.getPlayerId());
        }
        return info.method_2966().getName();
    }

    @Override
    public String getFilterName(PlayerTrackerMapElement<?> element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(PlayerTrackerMapElement<?> element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(PlayerTrackerMapElement<?> element) {
        return -11184641;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public boolean isRightClickValid(PlayerTrackerMapElement<?> element) {
        return WorldMap.trackedPlayerRenderer.getCollector().playerExists(element.getPlayerId());
    }

    @Override
    public ArrayList<RightClickOption> getRightClickOptions(final PlayerTrackerMapElement<?> element, IRightClickableElement target) {
        ArrayList<RightClickOption> rightClickOptions = new ArrayList<RightClickOption>();
        rightClickOptions.add(new RightClickOption(this, this.getMenuName(element), rightClickOptions.size(), target){

            @Override
            public void onAction(class_437 screen) {
            }
        });
        rightClickOptions.add(new RightClickOption(this, "", rightClickOptions.size(), target){

            @Override
            public String getName() {
                if (!WorldMap.settings.coordinates) {
                    return "hidden";
                }
                return String.format("X: %d, Y: %s, Z: %d", (int)Math.floor(element.getX()), (int)Math.floor(element.getY()), (int)Math.floor(element.getZ()));
            }

            @Override
            public void onAction(class_437 screen) {
            }
        });
        rightClickOptions.add(new RightClickOption(this, "gui.xaero_right_click_player_teleport", rightClickOptions.size(), target){

            @Override
            public void onAction(class_437 screen) {
                WorldMapSession session = WorldMapSession.getCurrentSession();
                new PlayerTeleporter().teleportToPlayer(screen, session.getMapProcessor().getMapWorld(), element);
            }
        }.setNameFormatArgs("T"));
        if (SupportMods.pac()) {
            rightClickOptions.add(new RightClickOption(this, "gui.xaero_right_click_player_config", rightClickOptions.size(), target){

                @Override
                public void onAction(class_437 screen) {
                    SupportMods.xaeroPac.openPlayerConfigScreen(screen, screen, element);
                }

                @Override
                public boolean isActive() {
                    return class_310.method_1551().field_1724.method_64475(2) && class_310.method_1551().method_1562().method_2871(element.getPlayerId()) != null;
                }
            }.setNameFormatArgs("C"));
        }
        return rightClickOptions;
    }

    @Override
    public boolean isInteractable(ElementRenderLocation location, PlayerTrackerMapElement<?> element) {
        return true;
    }
}

