/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.minecraft.class_1074
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_410
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5321
 */
package xaero.map.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_1074;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiDimensionOptions;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiMapName;
import xaero.map.gui.GuiMapSwitchingButton;
import xaero.map.gui.TooltipButton;
import xaero.map.gui.dropdown.DropDownWidget;
import xaero.map.misc.KeySortableByOther;
import xaero.map.world.MapConnectionManager;
import xaero.map.world.MapConnectionNode;
import xaero.map.world.MapDimension;

public class GuiMapSwitching {
    private static final class_2561 CONNECT_MAP = class_2561.method_43471((String)"gui.xaero_connect_map");
    private static final class_2561 DISCONNECT_MAP = class_2561.method_43471((String)"gui.xaero_disconnect_map");
    private MapProcessor mapProcessor;
    private MapDimension settingsDimension;
    private String[] mwDropdownValues;
    private DropDownWidget createdDimensionDropdown;
    private DropDownWidget createdMapDropdown;
    private class_4185 switchingButton;
    private class_4185 multiworldTypeOptionButton;
    private class_4185 renameButton;
    private class_4185 connectButton;
    private class_4185 deleteButton;
    private class_4185 confirmButton;
    private CursorBox serverSelectionModeBox = new CursorBox("gui.xaero_mw_server_box");
    private CursorBox mapSelectionBox = new CursorBox("gui.xaero_map_selection_box");
    public boolean active;
    private boolean writableOnInit;
    private boolean uiPausedOnUpdate;
    private boolean mapSwitchingAllowed;

    public GuiMapSwitching(MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
        this.mapSelectionBox.setStartWidth(200);
        this.serverSelectionModeBox.setStartWidth(200);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(GuiMap mapScreen, class_310 minecraft, int width, int height) {
        boolean dimensionDDWasOpen = this.createdDimensionDropdown != null && !this.createdDimensionDropdown.isClosed();
        boolean mapDDWasOpen = this.createdMapDropdown != null && !this.createdMapDropdown.isClosed();
        this.createdDimensionDropdown = null;
        this.createdMapDropdown = null;
        this.switchingButton = null;
        this.multiworldTypeOptionButton = null;
        this.renameButton = null;
        this.deleteButton = null;
        this.confirmButton = null;
        this.settingsDimension = this.mapProcessor.getMapWorld().getFutureDimension();
        this.mapSwitchingAllowed = this.settingsDimension != null;
        Object object = this.mapProcessor.uiPauseSync;
        synchronized (object) {
            this.uiPausedOnUpdate = this.isUIPaused();
            this.switchingButton = new GuiMapSwitchingButton(this.active, 0, height - 20, b -> {
                Object object = this.mapProcessor.uiPauseSync;
                synchronized (object) {
                    if (!this.canToggleThisScreen()) {
                        return;
                    }
                    this.active = !this.active;
                    mapScreen.method_25423(minecraft, width, height);
                    mapScreen.method_25395((class_364)this.switchingButton);
                }
            });
            mapScreen.addButton(this.switchingButton);
            if (this.mapSwitchingAllowed) {
                this.writableOnInit = this.settingsDimension.futureMultiworldWritable;
                if (this.active) {
                    this.createdDimensionDropdown = this.createDimensionDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
                    this.createdMapDropdown = this.createMapDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
                    mapScreen.method_25429(this.createdDimensionDropdown);
                    mapScreen.method_25429(this.createdMapDropdown);
                    if (dimensionDDWasOpen) {
                        this.createdDimensionDropdown.setClosed(false);
                    }
                    if (mapDDWasOpen) {
                        this.createdMapDropdown.setClosed(false);
                    }
                    this.multiworldTypeOptionButton = new TooltipButton(width / 2 - 90, 24, 180, 20, (class_2561)class_2561.method_43470((String)this.getMultiworldTypeButtonMessage()), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (this.isMapSelectionOptionEnabled()) {
                                this.mapProcessor.toggleMultiworldType(this.settingsDimension);
                                b.method_25355((class_2561)class_2561.method_43470((String)this.getMultiworldTypeButtonMessage()));
                            }
                        }
                    }, this.settingsDimension.isFutureMultiworldServerBased() ? () -> this.serverSelectionModeBox : () -> this.mapSelectionBox);
                    mapScreen.addButton(this.multiworldTypeOptionButton);
                    this.renameButton = class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_rename"), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (!this.canRenameMap()) {
                                return;
                            }
                            String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
                            if (currentMultiworld == null) {
                                return;
                            }
                            minecraft.method_1507((class_437)new GuiMapName(this.mapProcessor, mapScreen, mapScreen, this.settingsDimension, currentMultiworld));
                        }
                    }).method_46434(width / 2 + 109, 80, 60, 20).method_46431();
                    mapScreen.addButton(this.renameButton);
                    this.connectButton = class_4185.method_46430((class_2561)this.getConnectButtonLabel(), b -> {
                        if (!this.canConnectMap()) {
                            return;
                        }
                        MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
                        if (playerMapKey == null) {
                            return;
                        }
                        MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
                        if (destinationMapKey == null) {
                            return;
                        }
                        String autoMapName = playerMapKey.getNamedString(this.settingsDimension.getMapWorld());
                        String selectedMapName = destinationMapKey.getNamedString(this.settingsDimension.getMapWorld());
                        String connectionDisplayString = autoMapName + "   \u00a7e<=>\u00a7r   " + selectedMapName;
                        MapConnectionManager mapConnections = this.settingsDimension.getMapWorld().getMapConnections();
                        boolean connected = mapConnections.isConnected(playerMapKey, destinationMapKey);
                        BooleanConsumer confirmationConsumer = result -> {
                            if (result) {
                                Object object = this.mapProcessor.uiSync;
                                synchronized (object) {
                                    if (connected) {
                                        mapConnections.removeConnection(playerMapKey, destinationMapKey);
                                    } else {
                                        mapConnections.addConnection(playerMapKey, destinationMapKey);
                                    }
                                    b.method_25355(this.getConnectButtonLabel());
                                    this.settingsDimension.getMapWorld().saveConfig();
                                }
                            }
                            minecraft.method_1507((class_437)mapScreen);
                        };
                        if (connected) {
                            minecraft.method_1507((class_437)new class_410(confirmationConsumer, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_disconnect_from_auto_msg"), (class_2561)class_2561.method_43470((String)connectionDisplayString)));
                        } else {
                            minecraft.method_1507((class_437)new class_410(confirmationConsumer, (class_2561)class_2561.method_43471((String)"gui.xaero_wm_connect_with_auto_msg"), (class_2561)class_2561.method_43470((String)connectionDisplayString)));
                        }
                    }).method_46434(width / 2 + 109, 102, 60, 20).method_46431();
                    mapScreen.addButton(this.connectButton);
                    this.deleteButton = class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_delete"), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (!this.canDeleteMap()) {
                                return;
                            }
                            String selectedMWId = this.settingsDimension.getFutureCustomSelectedMultiworld();
                            minecraft.method_1507((class_437)new class_410(result -> {
                                if (result) {
                                    String mapNameAndIdLine = class_1074.method_4662((String)"gui.xaero_delete_map_msg4", (Object[])new Object[0]) + ": " + this.settingsDimension.getMultiworldName(selectedMWId) + " (" + selectedMWId + ")";
                                    minecraft.method_1507((class_437)new class_410(result2 -> {
                                        if (result2) {
                                            Object object = this.mapProcessor.uiSync;
                                            synchronized (object) {
                                                if (this.mapProcessor.getMapWorld() == this.settingsDimension.getMapWorld()) {
                                                    MapDimension currentDimension;
                                                    MapDimension mapDimension = currentDimension = !this.mapProcessor.isMapWorldUsable() ? null : this.mapProcessor.getMapWorld().getCurrentDimension();
                                                    if (this.settingsDimension == currentDimension && this.settingsDimension.getCurrentMultiworld().equals(selectedMWId)) {
                                                        if (WorldMap.settings.debug) {
                                                            WorldMap.LOGGER.info("Delayed map deletion!");
                                                        }
                                                        this.mapProcessor.requestCurrentMapDeletion();
                                                    } else {
                                                        if (WorldMap.settings.debug) {
                                                            WorldMap.LOGGER.info("Instant map deletion!");
                                                        }
                                                        this.settingsDimension.deleteMultiworldMapDataUnsynced(selectedMWId);
                                                    }
                                                    this.settingsDimension.deleteMultiworldId(selectedMWId);
                                                    this.settingsDimension.pickDefaultCustomMultiworldUnsynced();
                                                    this.settingsDimension.saveConfigUnsynced();
                                                    this.settingsDimension.futureMultiworldWritable = false;
                                                }
                                            }
                                        }
                                        minecraft.method_1507((class_437)mapScreen);
                                    }, (class_2561)class_2561.method_43471((String)"gui.xaero_delete_map_msg3"), (class_2561)class_2561.method_43470((String)mapNameAndIdLine)));
                                } else {
                                    minecraft.method_1507((class_437)mapScreen);
                                }
                            }, (class_2561)class_2561.method_43471((String)"gui.xaero_delete_map_msg1"), (class_2561)class_2561.method_43471((String)"gui.xaero_delete_map_msg2")));
                        }
                    }).method_46434(width / 2 - 168, 80, 60, 20).method_46431();
                    mapScreen.addButton(this.deleteButton);
                    this.confirmButton = class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_confirm"), b -> {
                        Object object = this.mapProcessor.uiPauseSync;
                        synchronized (object) {
                            if (!this.canConfirm()) {
                                return;
                            }
                            this.confirm(mapScreen, minecraft, width, height);
                        }
                    }).method_46434(width / 2 - 50, 104, 100, 20).method_46431();
                    mapScreen.addButton(this.confirmButton);
                    this.updateButtons(mapScreen, width, minecraft);
                } else {
                    this.switchingButton.field_22763 = this.canToggleThisScreen();
                }
            } else {
                this.switchingButton.field_22763 = false;
            }
        }
    }

    public static GuiDimensionOptions getSortedDimensionOptions(MapDimension dim) {
        int selected = 0;
        class_5321<class_1937> currentDim = dim.getDimId();
        ArrayList sortableList = new ArrayList();
        for (MapDimension dimension : dim.getMapWorld().getDimensionsList()) {
            sortableList.add(new KeySortableByOther<class_5321<class_1937>>(dimension.getDimId(), new Comparable[]{dimension.getDimId().method_29177().toString()}));
        }
        Collections.sort(sortableList);
        selected = GuiMapSwitching.getDropdownSelectionIdFromValue(sortableList, currentDim);
        class_5321[] values = new class_5321[]{};
        values = sortableList.stream().map(KeySortableByOther::getKey).collect(ArrayList::new, ArrayList::add, ArrayList::addAll).toArray(values);
        return new GuiDimensionOptions(selected, values);
    }

    private DropDownWidget createDimensionDropdown(boolean paused, int width, GuiMap mapScreen, class_310 minecraft) {
        GuiDimensionOptions dimOptions = GuiMapSwitching.getSortedDimensionOptions(this.settingsDimension);
        ArrayList<String> dropdownLabels = new ArrayList<String>();
        class_5321 currentWorldDim = this.mapProcessor.getWorld() == null ? null : this.mapProcessor.getWorld().method_27983();
        for (class_5321<class_1937> k : dimOptions.values) {
            Object result = k.method_29177().toString();
            if (((String)result).startsWith("minecraft:")) {
                result = ((String)result).substring(10);
            }
            if (k == currentWorldDim) {
                result = (String)result + " (auto)";
            }
            dropdownLabels.add((String)result);
        }
        class_5321<class_1937>[] finalValues = dimOptions.values;
        DropDownWidget result = DropDownWidget.Builder.begin().setOptions(dropdownLabels.toArray(new String[0])).setX(width / 2 - 100).setY(64).setW(200).setSelected(dimOptions.selected).setCallback((dd, i) -> {
            class_5321 selectedValue = finalValues[i];
            this.settingsDimension = this.settingsDimension.getMapWorld().getDimension((class_5321<class_1937>)selectedValue);
            if (selectedValue == currentWorldDim) {
                selectedValue = null;
            }
            this.settingsDimension.getMapWorld().setCustomDimensionId((class_5321<class_1937>)selectedValue);
            this.mapProcessor.checkForWorldUpdate();
            DropDownWidget newDropDown = this.createMapDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
            mapScreen.replaceWidget(this.createdMapDropdown, newDropDown);
            this.createdMapDropdown = newDropDown;
            this.updateButtons(mapScreen, width, minecraft);
            return true;
        }).setContainer(mapScreen).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui_xaero_wm_dropdown_dimension_select")).build();
        return result;
    }

    private DropDownWidget createMapDropdown(boolean paused, int width, GuiMap mapScreen, class_310 minecraft) {
        List<CallSite> mwDropdownNames;
        int selected = 0;
        if (!paused) {
            int currentIndex;
            String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
            ArrayList sortableList = new ArrayList();
            for (String mwId : this.settingsDimension.getMultiworldIdsCopy()) {
                sortableList.add(new KeySortableByOther<String>(mwId, new Comparable[]{this.settingsDimension.getMultiworldName(mwId).toLowerCase()}));
            }
            if (currentMultiworld != null && (currentIndex = GuiMapSwitching.getDropdownSelectionIdFromValue(sortableList, currentMultiworld)) == -1) {
                sortableList.add(new KeySortableByOther<String>(currentMultiworld, new Comparable[]{this.settingsDimension.getMultiworldName(currentMultiworld).toLowerCase()}));
            }
            Collections.sort(sortableList);
            if (currentMultiworld != null) {
                selected = GuiMapSwitching.getDropdownSelectionIdFromValue(sortableList, currentMultiworld);
            }
            this.mwDropdownValues = sortableList.stream().map(KeySortableByOther::getKey).collect(ArrayList::new, ArrayList::add, ArrayList::addAll).toArray(new String[0]);
            mwDropdownNames = sortableList.stream().map(KeySortableByOther::getKey).map(this.settingsDimension::getMultiworldName).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            mwDropdownNames.add((CallSite)((Object)("\u00a78" + class_1074.method_4662((String)"gui.xaero_create_new_map", (Object[])new Object[0]))));
        } else {
            mwDropdownNames = new ArrayList<CallSite>();
            this.mwDropdownValues = null;
            mwDropdownNames.add((CallSite)((Object)("\u00a77" + class_1074.method_4662((String)"gui.xaero_map_menu_please_wait", (Object[])new Object[0]))));
        }
        DropDownWidget result = DropDownWidget.Builder.begin().setOptions(mwDropdownNames.toArray(new String[0])).setX(width / 2 - 100).setY(84).setW(200).setSelected(selected).setCallback((dd, i) -> {
            Object object = this.mapProcessor.uiPauseSync;
            synchronized (object) {
                if (this.isUIPaused() || this.uiPausedOnUpdate) {
                    return false;
                }
                if (i < this.mwDropdownValues.length) {
                    this.mapProcessor.setMultiworld(this.settingsDimension, this.mwDropdownValues[i]);
                    this.updateButtons(mapScreen, width, minecraft);
                    return true;
                }
                minecraft.method_1507((class_437)new GuiMapName(this.mapProcessor, mapScreen, mapScreen, this.settingsDimension, null));
                return false;
            }
        }).setContainer(mapScreen).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui_xaero_wm_dropdown_map_select")).build();
        result.setActive(!paused);
        return result;
    }

    private boolean isUIPaused() {
        return this.mapProcessor.isUIPaused() || this.mapProcessor.isWaitingForWorldUpdate();
    }

    private boolean isMapSelectionOptionEnabled() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureMultiworldServerBased() && this.settingsDimension.getMapWorld().isMultiplayer();
    }

    private boolean canToggleThisScreen() {
        return !this.isUIPaused() && this.settingsDimension != null && this.settingsDimension.futureMultiworldWritable;
    }

    private boolean canDeleteMap() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureUsingWorldSaveUnsynced() && this.mwDropdownValues != null && this.mwDropdownValues.length > 1 && this.settingsDimension.getFutureCustomSelectedMultiworld() != null;
    }

    private boolean canRenameMap() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureUsingWorldSaveUnsynced();
    }

    private boolean canConnectMap() {
        if (!this.mapProcessor.getMapWorld().isMultiplayer()) {
            return false;
        }
        MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
        if (playerMapKey == null) {
            return false;
        }
        MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
        if (destinationMapKey == null) {
            return false;
        }
        return !destinationMapKey.equals(playerMapKey);
    }

    private boolean canConfirm() {
        return !this.isUIPaused();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private class_2561 getConnectButtonLabel() {
        Object object = this.mapProcessor.uiPauseSync;
        synchronized (object) {
            if (this.isUIPaused()) {
                return CONNECT_MAP;
            }
            MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
            if (playerMapKey == null) {
                return CONNECT_MAP;
            }
            MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
            if (destinationMapKey == null) {
                return CONNECT_MAP;
            }
            MapConnectionManager mapConnections = this.settingsDimension.getMapWorld().getMapConnections();
            if (mapConnections.isConnected(playerMapKey, destinationMapKey)) {
                return DISCONNECT_MAP;
            }
            return CONNECT_MAP;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateButtons(GuiMap mapScreen, int width, class_310 minecraft) {
        Object object = this.mapProcessor.uiPauseSync;
        synchronized (object) {
            boolean isPaused = this.isUIPaused();
            if (this.uiPausedOnUpdate != isPaused) {
                DropDownWidget newDropDown;
                DropDownWidget dropDownWidget = newDropDown = !this.active ? null : this.createMapDropdown(isPaused, width, mapScreen, minecraft);
                if (newDropDown != null) {
                    if (this.createdMapDropdown != null) {
                        mapScreen.replaceWidget(this.createdMapDropdown, newDropDown);
                    } else {
                        mapScreen.method_25429(newDropDown);
                    }
                } else if (this.createdMapDropdown != null) {
                    mapScreen.method_37066((class_364)this.createdMapDropdown);
                }
                this.createdMapDropdown = !this.active ? null : newDropDown;
                this.uiPausedOnUpdate = isPaused;
            }
            this.switchingButton.field_22763 = this.canToggleThisScreen();
            if (this.deleteButton != null) {
                this.deleteButton.field_22763 = this.canDeleteMap();
            }
            if (this.renameButton != null) {
                this.renameButton.field_22763 = this.canRenameMap();
            }
            if (this.connectButton != null) {
                this.connectButton.field_22763 = this.canConnectMap();
                this.connectButton.method_25355(this.getConnectButtonLabel());
            }
            if (this.multiworldTypeOptionButton != null) {
                this.multiworldTypeOptionButton.field_22763 = this.isMapSelectionOptionEnabled();
            }
            if (this.confirmButton != null) {
                this.confirmButton.field_22763 = this.canConfirm();
            }
        }
    }

    private String getMultiworldTypeButtonMessage() {
        int multiworldType = this.settingsDimension.getMapWorld().getFutureMultiworldType(this.settingsDimension);
        return class_1074.method_4662((String)"gui.xaero_map_selection", (Object[])new Object[0]) + ": " + class_1074.method_4662((String)(this.settingsDimension.isFutureMultiworldServerBased() ? "gui.xaero_mw_server" : (multiworldType == 0 ? "gui.xaero_mw_single" : (multiworldType == 1 ? "gui.xaero_mw_manual" : "gui.xaero_mw_spawn"))), (Object[])new Object[0]);
    }

    public void confirm(GuiMap mapScreen, class_310 minecraft, int width, int height) {
        if (this.mapProcessor.confirmMultiworld(this.settingsDimension)) {
            this.active = false;
            mapScreen.method_25423(minecraft, width, height);
        }
    }

    private static <S> int getDropdownSelectionIdFromValue(List<KeySortableByOther<S>> values, S value) {
        for (int selected = 0; selected < values.size(); ++selected) {
            if (!values.get(selected).getKey().equals(value)) continue;
            return selected;
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void preMapRender(GuiMap mapScreen, class_310 minecraft, int width, int height) {
        String currentDropdownSelection;
        String currentMultiworld;
        if (!this.active && this.settingsDimension != null && !this.settingsDimension.futureMultiworldWritable) {
            this.active = true;
            mapScreen.method_25423(minecraft, width, height);
        }
        if (this.mapSwitchingAllowed && (this.createdMapDropdown == null || this.createdMapDropdown.isClosed())) {
            Object object = this.mapProcessor.uiPauseSync;
            synchronized (object) {
                if (this.uiPausedOnUpdate != this.isUIPaused()) {
                    this.updateButtons(mapScreen, width, minecraft);
                }
            }
        }
        if (this.active && this.settingsDimension != null && this.createdMapDropdown.isClosed() && !this.uiPausedOnUpdate && (currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced()) != null && (!currentMultiworld.equals(currentDropdownSelection = this.mwDropdownValues[this.createdMapDropdown.getSelected()]) || this.writableOnInit != this.settingsDimension.futureMultiworldWritable)) {
            mapScreen.method_25423(minecraft, width, height);
        }
    }

    public void renderText(class_332 guiGraphics, class_310 minecraft, int mouseX, int mouseY, int width, int height) {
        if (!this.active) {
            return;
        }
        String selectMapString = class_1074.method_4662((String)"gui.xaero_select_map", (Object[])new Object[0]) + ":";
        class_4597.class_4598 renderTypeBuffers = this.mapProcessor.getCvc().getRenderTypeBuffers();
        class_4588 backgroundVertexBuffer = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_COLOR_OVERLAY);
        MapRenderHelper.drawStringWithBackground(guiGraphics, minecraft.field_1772, selectMapString, width / 2 - minecraft.field_1772.method_1727(selectMapString) / 2, 49, -1, 0.0f, 0.0f, 0.0f, 0.4f);
        renderTypeBuffers.method_22993();
    }

    public void postMapRender(class_332 guiGraphics, class_310 minecraft, int mouseX, int mouseY, int width, int height) {
    }
}

