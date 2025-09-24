/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_5481
 */
package xaero.common.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import xaero.common.HudMod;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.GuiWaypointContainers;
import xaero.common.gui.GuiWaypointSets;
import xaero.common.gui.GuiWaypointWorlds;
import xaero.common.gui.MySmallButton;
import xaero.common.gui.MySuperTinyButton;
import xaero.common.gui.ScreenBase;
import xaero.common.gui.TooltipButton;
import xaero.common.gui.WaypointEditForm;
import xaero.common.gui.dropdown.DropDownWidget;
import xaero.common.gui.dropdown.IDropDownWidgetCallback;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.common.validator.NumericFieldValidator;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.path.XaeroPath;

public class GuiAddWaypoint
extends ScreenBase
implements IDropDownWidgetCallback {
    private static final CursorBox VISIBILITY_TYPE_TOOLTIP = new CursorBox("gui.xaero_box_visibility_type");
    private static final CursorBox TYPE_TOOLTIP = new CursorBox("gui.xaero_box_waypoint_type");
    private final MinimapSession session;
    private MinimapWorldManager manager;
    protected String screenTitle;
    private class_4185 leftButton;
    private class_4185 rightButton;
    private class_4185 modeSwitchButton;
    private class_4185 resetButton;
    private class_342 nameTextField;
    private class_342 xTextField;
    private class_342 yTextField;
    private class_342 zTextField;
    private class_342 yawTextField;
    private class_342 initialTextField;
    private WaypointEditForm mutualForm;
    private ArrayList<WaypointEditForm> editForms;
    private int selectedWaypointIndex;
    private int defaultContainer;
    private MinimapWorld defaultWorld;
    private GuiWaypointContainers containers;
    private GuiWaypointWorlds worlds;
    private GuiWaypointSets sets;
    private DropDownWidget containersDD;
    private DropDownWidget worldsDD;
    private DropDownWidget setsDD;
    private DropDownWidget colorDD;
    private String fromSet;
    private ArrayList<Waypoint> waypointsEdited;
    private class_4185 disableButton;
    private class_4185 visibilityTypeButton;
    private NumericFieldValidator fieldValidator;
    private NumericFieldValidator fieldYValidator;
    private boolean adding;
    private boolean prefilled;
    private boolean startPrefilled;
    private String namePlaceholder;
    private String xPlaceholder;
    private String yPlaceholder;
    private String zPlaceholder;
    private String yawPlaceholder;
    private String initialPlaceholder;
    private String colorPlaceholder;
    private class_4185 defaultYawButton;
    private class_4185 defaultDisabledButton;
    private class_4185 defaultVisibilityTypeButton;
    protected class_4185 confirmButton;
    private boolean censorCoordsIfNeeded;
    private final XaeroPath frozenAutoWorldPath;
    private BiFunction<String, Integer, String> censoredTextFormatterString;
    private BiFunction<String, Integer, class_5481> censoredTextFormatter;
    private boolean hasForcedPlayerPos;
    private int forcedPlayerX;
    private int forcedPlayerY;
    private int forcedPlayerZ;
    private double forcedPlayerScale;
    private MinimapWorld forcedCoordSrcWorld;
    private boolean ignoreEditBoxChanges = true;
    private boolean canBeLabyMod = true;

    public GuiAddWaypoint(HudMod modMain, MinimapSession session, class_437 par1GuiScreen, class_437 escapeScreen, ArrayList<Waypoint> waypointsEdited, XaeroPath defaultParentContainer, MinimapWorld defaultWorld, String waypointSet, boolean adding, boolean hasForcedPlayerPos, int forcedPlayerX, int forcedPlayerY, int forcedPlayerZ, double forcedPlayerScale, MinimapWorld forcedCoordSrcWorld) {
        super(modMain, par1GuiScreen, escapeScreen, (class_2561)class_2561.method_43470((String)""));
        this.session = session;
        this.hasForcedPlayerPos = hasForcedPlayerPos;
        this.forcedPlayerX = forcedPlayerX;
        this.forcedPlayerY = forcedPlayerY;
        this.forcedPlayerZ = forcedPlayerZ;
        this.forcedPlayerScale = forcedPlayerScale;
        this.forcedCoordSrcWorld = forcedCoordSrcWorld;
        this.waypointsEdited = waypointsEdited;
        this.manager = session.getWorldManager();
        this.fromSet = waypointSet;
        this.defaultWorld = defaultWorld;
        this.frozenAutoWorldPath = session.getWorldState().getAutoWorldPath();
        this.containers = new GuiWaypointContainers(modMain, this.manager, defaultParentContainer, this.frozenAutoWorldPath);
        this.defaultContainer = this.containers.current;
        this.worlds = new GuiWaypointWorlds(this.manager.getRootWorldContainer(defaultParentContainer), session, defaultWorld.getFullPath(), this.frozenAutoWorldPath);
        this.sets = new GuiWaypointSets(false, defaultWorld, this.fromSet);
        this.prefilled = !waypointsEdited.isEmpty();
        this.startPrefilled = this.prefilled;
        this.createForms();
        this.fieldValidator = modMain.getFieldValidators().getNumericFieldValidator();
        this.fieldYValidator = modMain.getFieldValidators().getWpCoordFieldValidator();
        this.adding = adding;
        this.namePlaceholder = "- " + class_1074.method_4662((String)"gui.xaero_waypoint_name", (Object[])new Object[0]);
        this.xPlaceholder = "- x";
        this.yPlaceholder = "- y";
        this.zPlaceholder = "- z";
        this.yawPlaceholder = "- " + class_1074.method_4662((String)"gui.xaero_yaw", (Object[])new Object[0]);
        this.initialPlaceholder = "- " + class_1074.method_4662((String)"gui.xaero_initial", (Object[])new Object[0]);
        this.colorPlaceholder = "\u00a78-";
        this.censorCoordsIfNeeded = true;
        this.censoredTextFormatterString = (p_195610_0_, p_195610_1_) -> {
            if (!this.censorCoordsIfNeeded) {
                return p_195610_0_;
            }
            int formatIndex = p_195610_0_.indexOf("\u00a7".charAt(0));
            if (formatIndex == -1) {
                return p_195610_0_.replaceAll(".", "#");
            }
            return p_195610_0_.substring(0, formatIndex).replaceAll(".", "#") + p_195610_0_.substring(formatIndex);
        };
        class_2583 defaultTextStyle = class_2583.field_24360;
        this.censoredTextFormatter = (s, i) -> {
            String censoredString = this.censoredTextFormatterString.apply((String)s, (Integer)i);
            return cc -> {
                for (int j = 0; j < censoredString.length(); ++j) {
                    cc.accept(j, defaultTextStyle, (int)censoredString.charAt(j));
                }
                return true;
            };
        };
        this.canSkipWorldRender = true;
    }

    public GuiAddWaypoint(HudMod modMain, MinimapSession session, class_437 par1GuiScreen, class_437 escapeScreen, ArrayList<Waypoint> waypointsEdited, XaeroPath defaultParentContainer, MinimapWorld defaultWorld, String waypointSet, boolean adding) {
        this(modMain, session, par1GuiScreen, escapeScreen, waypointsEdited, defaultParentContainer, defaultWorld, waypointSet, adding, false, 0, 0, 0, class_310.method_1551().field_1687.method_8597().comp_646(), null);
    }

    public GuiAddWaypoint(HudMod modMain, MinimapSession session, class_437 par1GuiScreen, ArrayList<Waypoint> waypointsEdited, XaeroPath defaultParentContainer, MinimapWorld defaultWorld, boolean adding) {
        this(modMain, session, par1GuiScreen, null, waypointsEdited, defaultParentContainer, defaultWorld, defaultWorld.getCurrentWaypointSetId(), adding);
    }

    private void fillFormWaypoint(WaypointEditForm form, Waypoint w) {
        form.name = w.getLocalizedName();
        form.xText = "" + w.getX();
        form.yText = w.isYIncluded() ? "" + w.getY() : "~";
        form.zText = "" + w.getZ();
        form.yawText = w.isRotation() ? "" + w.getYaw() : "";
        form.initial = w.getInitials();
        form.disabledOrTemporary = w.isDestination() ? 3 : (w.isTemporary() ? 2 : (w.isDisabled() ? 1 : 0));
        form.color = w.getWaypointColor();
        form.visibilityType = w.getVisibility();
        if (form.initial.length() == 0) {
            form.autoInitial = true;
        }
    }

    private double getDimDiv(double waypointDimScale) {
        double playerDimScale = this.hasForcedPlayerPos ? this.forcedPlayerScale : this.field_22787.field_1719.method_37908().method_8597().comp_646();
        return playerDimScale / waypointDimScale;
    }

    private int getAutomaticX(double waypointDimScale) {
        int playerX = this.hasForcedPlayerPos ? this.forcedPlayerX : OptimizedMath.myFloor(this.field_22787.field_1719.method_23317());
        return OptimizedMath.myFloor((double)playerX * this.getDimDiv(waypointDimScale));
    }

    private String getAutomaticYInput(MinimapWorld destinationWorld) {
        if (this.hasForcedPlayerPos && (this.forcedPlayerY == Short.MAX_VALUE || this.forcedCoordSrcWorld != null && this.forcedCoordSrcWorld != destinationWorld)) {
            return "~";
        }
        int playerY = this.hasForcedPlayerPos ? this.forcedPlayerY : OptimizedMath.myFloor(this.field_22787.field_1719.method_23318() + 0.0625);
        return "" + OptimizedMath.myFloor(playerY);
    }

    private int getAutomaticZ(double waypointDimScale) {
        int playerZ = this.hasForcedPlayerPos ? this.forcedPlayerZ : OptimizedMath.myFloor(this.field_22787.field_1719.method_23321());
        return OptimizedMath.myFloor((double)playerZ * this.getDimDiv(waypointDimScale));
    }

    private void fillFormAutomatic(WaypointEditForm form) {
        form.xText = "";
        form.yText = "";
        form.zText = "";
        form.color = WaypointColor.getRandom();
        form.autoInitial = true;
    }

    private void createForms() {
        this.editForms = new ArrayList();
        this.mutualForm = new WaypointEditForm();
        for (int i = 0; i < this.waypointsEdited.size(); ++i) {
            Waypoint w = this.waypointsEdited.get(i);
            WaypointEditForm form = new WaypointEditForm();
            this.fillFormWaypoint(form, w);
            this.editForms.add(form);
        }
        if (!this.startPrefilled) {
            WaypointEditForm createdForm = new WaypointEditForm();
            this.fillFormAutomatic(createdForm);
            this.editForms.add(createdForm);
        }
        this.updateMutual();
    }

    private void resetCurrentForm() {
        if (this.selectedWaypointIndex >= this.waypointsEdited.size()) {
            WaypointEditForm freshForm = new WaypointEditForm();
            this.fillFormAutomatic(freshForm);
            this.editForms.set(this.selectedWaypointIndex, freshForm);
        } else {
            Waypoint w = this.waypointsEdited.get(this.selectedWaypointIndex);
            WaypointEditForm freshForm = new WaypointEditForm();
            this.fillFormWaypoint(freshForm, w);
            this.editForms.set(this.selectedWaypointIndex, freshForm);
        }
    }

    private void updateMutual() {
        String nameTextMutual = "";
        String initialMutual = "";
        String yawMutual = "";
        String xTextMutual = "";
        String yTextMutual = "";
        String zTextMutual = "";
        int waypointDisabledOrTemporaryMutual = 0;
        WaypointVisibilityType waypointVisibilityTypeMutual = WaypointVisibilityType.LOCAL;
        WaypointColor colorMutual = null;
        xTextMutual = "";
        yTextMutual = "";
        zTextMutual = "";
        WaypointEditForm firstForm = this.editForms.get(0);
        this.mutualForm.keepName = this.differentValues(WaypointEditForm::getName);
        this.mutualForm.keepXText = this.editForms.size() > 1 && firstForm.xText.isEmpty() || this.differentValues(WaypointEditForm::getxText);
        this.mutualForm.keepYText = this.editForms.size() > 1 && firstForm.yText.isEmpty() || this.differentValues(WaypointEditForm::getyText);
        this.mutualForm.keepZText = this.editForms.size() > 1 && firstForm.zText.isEmpty() || this.differentValues(WaypointEditForm::getzText);
        this.mutualForm.defaultKeepYawText = this.mutualForm.keepYawText = this.differentValues(WaypointEditForm::getYawText);
        this.mutualForm.keepInitial = this.differentValues(WaypointEditForm::getInitial);
        this.mutualForm.autoInitial = this.editForms.size() == 1 && firstForm.autoInitial;
        this.mutualForm.defaultKeepDisabledOrTemporary = this.mutualForm.keepDisabledOrTemporary = this.differentValues(WaypointEditForm::getDisabledOrTemporary);
        this.mutualForm.defaultKeepVisibilityType = this.mutualForm.keepVisibilityType = this.differentValues(WaypointEditForm::getVisibilityType);
        this.mutualForm.defaultKeepColor = this.differentValues(WaypointEditForm::getColor);
        if (!this.mutualForm.keepName) {
            nameTextMutual = firstForm.name;
        }
        if (!this.mutualForm.keepXText) {
            xTextMutual = firstForm.xText;
        }
        if (!this.mutualForm.keepYText) {
            yTextMutual = firstForm.yText;
        }
        if (!this.mutualForm.keepZText) {
            zTextMutual = firstForm.zText;
        }
        if (!this.mutualForm.keepYawText) {
            yawMutual = firstForm.yawText;
        }
        if (!this.mutualForm.keepInitial) {
            initialMutual = firstForm.initial;
        }
        if (!this.mutualForm.keepDisabledOrTemporary) {
            waypointDisabledOrTemporaryMutual = firstForm.disabledOrTemporary;
        }
        if (!this.mutualForm.keepVisibilityType) {
            waypointVisibilityTypeMutual = firstForm.visibilityType;
        }
        if (!this.mutualForm.defaultKeepColor) {
            colorMutual = firstForm.color;
        }
        this.mutualForm.name = nameTextMutual;
        this.mutualForm.xText = xTextMutual;
        this.mutualForm.yText = yTextMutual;
        this.mutualForm.zText = zTextMutual;
        this.mutualForm.yawText = yawMutual;
        this.mutualForm.initial = initialMutual;
        this.mutualForm.disabledOrTemporary = waypointDisabledOrTemporaryMutual;
        this.mutualForm.visibilityType = waypointVisibilityTypeMutual;
        this.mutualForm.color = colorMutual;
    }

    private void confirmMutual() {
        for (int i = 0; i < this.editForms.size(); ++i) {
            WaypointEditForm individualForm = this.editForms.get(i);
            if (!this.mutualForm.keepName) {
                individualForm.name = this.mutualForm.name;
            }
            if (!this.mutualForm.keepXText) {
                individualForm.xText = this.mutualForm.xText;
            }
            if (!this.mutualForm.keepYText) {
                individualForm.yText = this.mutualForm.yText;
            }
            if (!this.mutualForm.keepZText) {
                individualForm.zText = this.mutualForm.zText;
            }
            if (!this.mutualForm.keepYawText) {
                individualForm.yawText = this.mutualForm.yawText;
            }
            if (!this.mutualForm.keepInitial) {
                if (!individualForm.initial.equals(this.mutualForm.initial)) {
                    individualForm.autoInitial = false;
                }
                individualForm.initial = this.mutualForm.initial;
            }
            if (!this.mutualForm.keepDisabledOrTemporary) {
                individualForm.disabledOrTemporary = this.mutualForm.disabledOrTemporary;
            }
            if (!this.mutualForm.keepVisibilityType) {
                individualForm.visibilityType = this.mutualForm.visibilityType;
            }
            if (this.mutualForm.color == null) continue;
            individualForm.color = this.mutualForm.color;
        }
    }

    private boolean differentValues(Function<WaypointEditForm, Object> s) {
        if (this.editForms.size() == 1) {
            return false;
        }
        WaypointEditForm testWaypoint = this.editForms.get(0);
        for (int i = 1; i < this.editForms.size(); ++i) {
            WaypointEditForm w = this.editForms.get(i);
            if (s.apply(w).equals(s.apply(testWaypoint))) continue;
            return true;
        }
        return false;
    }

    public String[] createColorOptions() {
        boolean unchangedOption = this.getCurrent().defaultKeepColor;
        String[] options = new String[WaypointColor.values().length + (unchangedOption ? 1 : 0)];
        if (unchangedOption) {
            options[0] = this.colorPlaceholder;
        }
        for (int i = 0; i < WaypointColor.values().length; ++i) {
            options[i + (unchangedOption ? 1 : 0)] = i == 0 ? WaypointColor.values()[i].getName().getString() : "\u00a7" + WaypointColor.values()[i].getFormat() + WaypointColor.values()[i].getName().getString();
        }
        return options;
    }

    @Override
    public void method_25426() {
        super.method_25426();
        this.ignoreEditBoxChanges = true;
        String string = this.screenTitle = this.adding ? class_1074.method_4662((String)"gui.xaero_new_waypoint", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_edit_waypoint", (Object[])new Object[0]);
        if (this.editForms.size() > 1) {
            this.screenTitle = this.screenTitle + (String)(this.editForms.size() > 1 ? " (" + (String)(this.modMain.getSettings().waypointsMutualEdit ? "" : this.selectedWaypointIndex + 1 + "/") + this.editForms.size() + ")" : "");
        }
        this.nameTextField = this.applyEditBoxResponder(new class_342(this.field_22793, this.field_22789 / 2 - 100, 104, 200, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_waypoint_name")));
        this.xTextField = this.applyEditBoxResponder(new class_342(this.field_22793, this.field_22789 / 2 - 109, 134, 50, 20, (class_2561)class_2561.method_43470((String)"x")));
        this.yTextField = this.applyEditBoxResponder(new class_342(this.field_22793, this.field_22789 / 2 - 53, 134, 50, 20, (class_2561)class_2561.method_43470((String)"y")));
        this.zTextField = this.applyEditBoxResponder(new class_342(this.field_22793, this.field_22789 / 2 + 3, 134, 50, 20, (class_2561)class_2561.method_43470((String)"z")));
        if (this.modMain.getSettings().hideWaypointCoordinates) {
            this.xTextField.method_1854(this.censoredTextFormatter);
            this.yTextField.method_1854(this.censoredTextFormatter);
            this.zTextField.method_1854(this.censoredTextFormatter);
        }
        this.yawTextField = this.applyEditBoxResponder(new class_342(this.field_22793, this.field_22789 / 2 + 59, 134, 50, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_yaw")));
        this.initialTextField = this.applyEditBoxResponder(new class_342(this.field_22793, this.field_22789 / 2 - 25, 164, 50, 20, (class_2561)class_2561.method_43471((String)"gui.xaero_initial")));
        this.method_25429(this.nameTextField);
        this.method_25429(this.xTextField);
        this.method_25429(this.yTextField);
        this.method_25429(this.zTextField);
        this.method_25429(this.yawTextField);
        this.method_25429(this.initialTextField);
        this.confirmButton = new MySmallButton(0, this.field_22789 / 2 - 155, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43471((String)"gui.xaero_confirm"), b -> {
            if (this.modMain.getSettings().waypointsMutualEdit) {
                this.confirmMutual();
            }
            boolean creatingAWaypoint = this.adding && this.waypointsEdited.size() < this.editForms.size();
            XaeroPath destinationWorldKeys = (XaeroPath)this.worlds.getCurrentKey();
            MinimapWorld destinationWorld = this.manager.getWorld(destinationWorldKeys);
            double waypointDimScale = this.session.getDimensionHelper().getDimCoordinateScale(destinationWorld);
            int initialEditedWaypointsSize = this.waypointsEdited.size();
            for (int i = 0; i < this.editForms.size(); ++i) {
                Waypoint w;
                int z;
                boolean shouldCreate;
                boolean bl = shouldCreate = i >= initialEditedWaypointsSize;
                if (!creatingAWaypoint && shouldCreate) break;
                WaypointEditForm waypointForm = this.editForms.get(i);
                String nameString = waypointForm.name;
                String xString = waypointForm.xText;
                String yString = waypointForm.yText;
                if (yString.equals("-") || yString.isEmpty()) {
                    yString = this.getAutomaticYInput(destinationWorld);
                }
                String zString = waypointForm.zText;
                String initialString = waypointForm.initial;
                WaypointColor color = waypointForm.color;
                boolean yIncluded = !yString.equals("~");
                int x = xString.equals("-") || xString.isEmpty() ? this.getAutomaticX(waypointDimScale) : Integer.parseInt(xString);
                int y = !yIncluded ? 0 : Integer.parseInt(yString);
                int n = z = zString.equals("-") || zString.isEmpty() ? this.getAutomaticZ(waypointDimScale) : Integer.parseInt(zString);
                if (shouldCreate) {
                    w = new Waypoint(x, y, z, nameString, initialString, color, WaypointPurpose.NORMAL, false, yIncluded);
                    this.waypointsEdited.add(w);
                } else {
                    w = this.waypointsEdited.get(i);
                    if (w.getPurpose() != WaypointPurpose.DEATH || !nameString.equals(class_1074.method_4662((String)"gui.xaero_deathpoint", (Object[])new Object[0]))) {
                        w.setName(nameString);
                        if (w.getPurpose() != WaypointPurpose.NORMAL) {
                            w.setPurpose(WaypointPurpose.NORMAL);
                        }
                    }
                    w.setX(x);
                    w.setY(y);
                    w.setZ(z);
                    w.setInitials(initialString);
                    w.setWaypointColor(color);
                    w.setYIncluded(yIncluded);
                }
                String yawText = waypointForm.yawText;
                int disableOrTemporary = waypointForm.disabledOrTemporary;
                boolean yawIsUsable = yawText.length() > 0 && !yawText.equals("-");
                w.setRotation(yawIsUsable);
                if (yawIsUsable) {
                    w.setYaw(Integer.parseInt(yawText));
                }
                if (w.isDestination() != (disableOrTemporary == 3)) {
                    w.setPurpose(disableOrTemporary == 3 ? WaypointPurpose.DESTINATION : WaypointPurpose.NORMAL);
                }
                w.setDisabled(disableOrTemporary == 1);
                if (disableOrTemporary == 2) {
                    w.setTemporary(true);
                }
                w.setVisibility(waypointForm.visibilityType);
            }
            MinimapWorld sourceWorld = this.defaultWorld;
            WaypointSet sourceSet = sourceWorld.getWaypointSet(this.fromSet);
            String destinationSetKey = this.sets.getCurrentSetKey();
            WaypointSet destinationSet = destinationWorld.getWaypointSet(destinationSetKey);
            if (this.adding || sourceSet != destinationSet) {
                if (!this.modMain.getSettings().waypointsBottom) {
                    destinationSet.addAll(this.waypointsEdited, true);
                } else {
                    destinationSet.addAll(this.waypointsEdited);
                }
            }
            if (sourceSet != destinationSet) {
                sourceSet.removeAll(this.waypointsEdited);
            }
            try {
                this.session.getWorldManagerIO().saveWorld(sourceWorld);
                if (destinationWorld != sourceWorld) {
                    this.session.getWorldManagerIO().saveWorld(destinationWorld);
                }
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            this.goBack();
        });
        this.method_37063((class_364)this.confirmButton);
        this.method_37063((class_364)new MySmallButton(0, this.field_22789 / 2 + 5, this.field_22790 / 6 + 168, (class_2561)class_2561.method_43469((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.leftButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"<"), b -> {
            --this.selectedWaypointIndex;
            if (this.selectedWaypointIndex < 0) {
                this.selectedWaypointIndex = 0;
            }
            boolean restoreFocus = this.method_25399() == b;
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
            if (restoreFocus) {
                boolean activeBU = this.leftButton.field_22763;
                this.leftButton.field_22763 = true;
                this.method_25395((class_364)this.leftButton);
                this.leftButton.field_22763 = activeBU;
            }
        }).method_46434(this.field_22789 / 2 - 203, 104, 20, 20).method_46431();
        this.rightButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)">"), b -> {
            ++this.selectedWaypointIndex;
            if (this.selectedWaypointIndex >= this.editForms.size()) {
                this.selectedWaypointIndex = this.editForms.size() - 1;
            }
            boolean restoreFocus = this.method_25399() == b;
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
            if (restoreFocus) {
                boolean activeBU = this.rightButton.field_22763;
                this.rightButton.field_22763 = true;
                this.method_25395((class_364)this.rightButton);
                this.rightButton.field_22763 = activeBU;
            }
        }).method_46434(this.field_22789 / 2 + 183, 104, 20, 20).method_46431();
        this.modeSwitchButton = class_4185.method_46430((class_2561)class_2561.method_43471((String)(this.modMain.getSettings().waypointsMutualEdit ? "gui.xaero_waypoints_edit_mode_all" : "gui.xaero_waypoints_edit_mode_individually")), b -> {
            this.modMain.getSettings().waypointsMutualEdit = !this.modMain.getSettings().waypointsMutualEdit;
            try {
                this.modMain.getSettings().saveSettings();
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            if (this.modMain.getSettings().waypointsMutualEdit) {
                this.prefilled = true;
                this.updateMutual();
            } else {
                this.confirmMutual();
            }
            boolean restoreFocus = this.method_25399() == b;
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
            if (restoreFocus) {
                boolean activeBU = this.modeSwitchButton.field_22763;
                this.modeSwitchButton.field_22763 = true;
                this.method_25395((class_364)this.modeSwitchButton);
                this.modeSwitchButton.field_22763 = activeBU;
            }
        }).method_46434(this.field_22789 / 2 + 106, 56, 99, 20).method_46431();
        if (this.editForms.size() > 1) {
            this.method_37063((class_364)this.leftButton);
            this.method_37063((class_364)this.rightButton);
            this.method_37063((class_364)this.modeSwitchButton);
        }
        this.resetButton = class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_waypoints_edit_reset"), b -> {
            if (this.modMain.getSettings().waypointsMutualEdit) {
                this.createForms();
                boolean restoreFocus = this.method_25399() == b;
                this.method_25423(this.field_22787, this.field_22789, this.field_22790);
                if (restoreFocus) {
                    boolean activeBU = this.resetButton.field_22763;
                    this.resetButton.field_22763 = true;
                    this.method_25395((class_364)this.resetButton);
                    this.resetButton.field_22763 = activeBU;
                }
            } else {
                this.resetCurrentForm();
                boolean restoreFocus = this.method_25399() == b;
                this.method_25423(this.field_22787, this.field_22789, this.field_22790);
                if (restoreFocus) {
                    boolean activeBU = this.resetButton.field_22763;
                    this.resetButton.field_22763 = true;
                    this.method_25395((class_364)this.resetButton);
                    this.resetButton.field_22763 = activeBU;
                }
            }
        }).method_46434(this.field_22789 / 2 - 204, 56, 99, 20).method_46431();
        this.method_37063((class_364)this.resetButton);
        this.nameTextField.method_1852(this.getCurrent().name);
        this.xTextField.method_1852(this.getCurrent().xText);
        this.yTextField.method_1852(this.getCurrent().yText);
        this.zTextField.method_1852(this.getCurrent().zText);
        this.yawTextField.method_1852(this.getCurrent().yawText);
        this.initialTextField.method_1852(this.getCurrent().initial);
        this.disableButton = new TooltipButton(this.field_22789 / 2 + 31, 164, 79, 20, this.getDisableButtonText(), b -> {
            this.getCurrent().disabledOrTemporary = (this.getCurrent().disabledOrTemporary + 1) % 4;
            this.disableButton.method_25355(this.getDisableButtonText());
            this.getCurrent().keepDisabledOrTemporary = false;
            if (this.defaultDisabledButton != null) {
                this.defaultDisabledButton.field_22763 = true;
            }
        }, () -> TYPE_TOOLTIP);
        this.method_37063((class_364)this.disableButton);
        this.visibilityTypeButton = new TooltipButton(this.field_22789 / 2 - 109, 164, 79, 20, this.getCurrent().visibilityType.getTranslation(), b -> {
            this.getCurrent().visibilityType = WaypointVisibilityType.values()[(this.getCurrent().visibilityType.ordinal() + 1) % WaypointVisibilityType.values().length];
            this.visibilityTypeButton.method_25355(this.getCurrent().visibilityType.getTranslation());
            this.getCurrent().keepVisibilityType = false;
            if (this.defaultVisibilityTypeButton != null) {
                this.defaultVisibilityTypeButton.field_22763 = true;
            }
        }, () -> VISIBILITY_TYPE_TOOLTIP);
        this.method_37063((class_364)this.visibilityTypeButton);
        if (this.getCurrent().defaultKeepYawText) {
            this.defaultYawButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"-"), b -> {
                this.getCurrent().keepYawText = true;
                this.getCurrent().yawText = "";
                this.yawTextField.method_1852(this.getCurrent().yawText);
                b.field_22763 = false;
            }).method_46434(this.field_22789 / 2 + 111, 134, 20, 20).method_46431();
            this.method_37063((class_364)this.defaultYawButton);
            boolean bl = this.defaultYawButton.field_22763 = !this.getCurrent().keepYawText;
        }
        if (this.getCurrent().defaultKeepDisabledOrTemporary) {
            this.defaultDisabledButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"-"), b -> {
                this.getCurrent().keepDisabledOrTemporary = true;
                this.getCurrent().disabledOrTemporary = 0;
                this.disableButton.method_25355(this.getDisableButtonText());
                b.field_22763 = false;
            }).method_46434(this.field_22789 / 2 + 110, 164, 20, 20).method_46431();
            this.method_37063((class_364)this.defaultDisabledButton);
            boolean bl = this.defaultDisabledButton.field_22763 = !this.getCurrent().keepDisabledOrTemporary;
        }
        if (this.getCurrent().defaultKeepVisibilityType) {
            this.defaultVisibilityTypeButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"-"), b -> {
                this.getCurrent().keepVisibilityType = true;
                this.getCurrent().visibilityType = WaypointVisibilityType.LOCAL;
                this.visibilityTypeButton.method_25355(this.getCurrent().visibilityType.getTranslation());
                b.field_22763 = false;
            }).method_46434(this.field_22789 / 2 - 130, 164, 20, 20).method_46431();
            this.method_37063((class_364)this.defaultVisibilityTypeButton);
            boolean bl = this.defaultVisibilityTypeButton.field_22763 = !this.getCurrent().keepVisibilityType;
        }
        if (this.modMain.getSettings().hideWaypointCoordinates) {
            this.method_37063((class_364)new MySuperTinyButton(this.field_22789 / 2 + 115, 134, (class_2561)class_2561.method_43471((String)(this.censorCoordsIfNeeded ? "gui.xaero_waypoints_edit_show" : "gui.xaero_waypoints_edit_hide")), b -> {
                this.censorCoordsIfNeeded = !this.censorCoordsIfNeeded;
                b.method_25355((class_2561)class_2561.method_43471((String)(this.censorCoordsIfNeeded ? "gui.xaero_waypoints_edit_show" : "gui.xaero_waypoints_edit_hide")));
            }));
        }
        WaypointColor currentColor = this.getCurrent().color;
        this.colorDD = DropDownWidget.Builder.begin().setOptions(this.createColorOptions()).setX(this.field_22789 / 2 - 60).setY(82).setW(120).setSelected((currentColor == null ? -1 : currentColor.ordinal()) + (this.getCurrent().defaultKeepColor ? 1 : 0)).setCallback(this).setContainer(this).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui.xaero_dropdown_waypoint_color")).build();
        this.method_25429(this.colorDD);
        this.setsDD = this.createSetsDropdown();
        this.method_25429(this.setsDD);
        this.containersDD = this.createContainersDropdown();
        this.method_25429(this.containersDD);
        this.worldsDD = this.createWorldsDropdown();
        this.method_25429(this.worldsDD);
        this.method_25395((class_364)this.nameTextField);
        this.nameTextField.method_25365(true);
        this.updateConfirmButton();
    }

    protected void method_56131() {
    }

    public class_342 applyEditBoxResponder(class_342 box) {
        box.method_1863(s -> {
            if (!this.ignoreEditBoxChanges) {
                this.postType((class_364)box);
            }
        });
        return box;
    }

    private DropDownWidget createSetsDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.sets.getOptions()).setX(this.field_22789 / 2 - 101).setY(60).setW(201).setSelected(this.sets.getCurrentSet()).setCallback(this).setContainer(this).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui.xaero_dropdown_waypoint_set")).build();
    }

    private DropDownWidget createContainersDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.containers.options).setX(this.field_22789 / 2 - 203).setY(38).setW(200).setSelected(this.containers.current).setCallback(this).setContainer(this).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui.xaero_dropdown_waypoint_container")).build();
    }

    private DropDownWidget createWorldsDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.worlds.options).setX(this.field_22789 / 2 + 2).setY(38).setW(200).setSelected(this.worlds.current).setCallback(this).setContainer(this).setNarrationTitle((class_2561)class_2561.method_43471((String)"gui.xaero_dropdown_waypoint_world")).build();
    }

    private class_2561 getDisableButtonText() {
        return class_2561.method_43471((String)(this.getCurrent().disabledOrTemporary == 3 ? "gui.xaero_destination" : (this.getCurrent().disabledOrTemporary == 1 ? "gui.xaero_toggle_disabled" : (this.getCurrent().disabledOrTemporary == 0 ? "gui.xaero_toggle_enabled" : "gui.xaero_temporary2"))));
    }

    private WaypointEditForm getCurrent() {
        return this.modMain.getSettings().waypointsMutualEdit ? this.mutualForm : this.editForms.get(this.selectedWaypointIndex);
    }

    public boolean method_25404(int par1, int par2, int par3) {
        class_364 focused = this.method_25399();
        this.preType(focused);
        this.ignoreEditBoxChanges = false;
        boolean result = super.method_25404(par1, par2, par3);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        if (focused instanceof class_342 && this.canConfirm() && (par1 == 257 || par1 == 335)) {
            this.confirmButton.method_25348(0.0, 0.0);
            return true;
        }
        return result;
    }

    public boolean method_25400(char par1, int par2) {
        class_364 focused = this.method_25399();
        this.preType(focused);
        this.ignoreEditBoxChanges = false;
        boolean result = super.method_25400(par1, par2);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        return result;
    }

    @Override
    public boolean method_25402(double mouseX, double mouseY, int button) {
        this.ignoreEditBoxChanges = false;
        boolean result = super.method_25402(mouseX, mouseY, button);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        return result;
    }

    @Override
    public boolean method_25406(double mouseX, double mouseY, int button) {
        this.ignoreEditBoxChanges = false;
        boolean result = super.method_25406(mouseX, mouseY, button);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        return result;
    }

    private void preType(class_364 focused) {
        if (focused == null) {
            return;
        }
    }

    private void postType(class_364 focused) {
        this.ignoreEditBoxChanges = true;
        if (focused == null) {
            return;
        }
        if (this.nameTextField == focused) {
            if (!(!this.getCurrent().autoInitial || this.nameTextField.method_1882().length() <= 0 || this.getCurrent().keepInitial && this.modMain.getSettings().waypointsMutualEdit)) {
                this.initialTextField.method_1852(this.nameTextField.method_1882().substring(0, 1).toUpperCase());
            }
        } else if (this.initialTextField == focused) {
            this.getCurrent().autoInitial = false;
        }
        this.checkFields(focused);
        this.updateConfirmButton();
    }

    public void method_25395(class_364 l) {
        this.preType(l);
        class_364 currentFocused = this.method_25399();
        if (currentFocused != null && currentFocused != l && currentFocused instanceof class_342) {
            ((class_342)currentFocused).method_25365(false);
        }
        super.method_25395(l);
    }

    private boolean canConfirm() {
        WaypointEditForm current = this.getCurrent();
        return !(!current.keepName && current.name.length() <= 0 || !current.keepInitial && current.initial.length() <= 0);
    }

    private void updateConfirmButton() {
        this.confirmButton.field_22763 = this.modeSwitchButton.field_22763 = this.canConfirm();
        this.leftButton.field_22763 = !this.modMain.getSettings().waypointsMutualEdit && this.canConfirm() && this.selectedWaypointIndex > 0;
        this.rightButton.field_22763 = !this.modMain.getSettings().waypointsMutualEdit && this.canConfirm() && this.selectedWaypointIndex < this.editForms.size() - 1;
    }

    private void handleCoordinateInputSpaces(class_342 coordinateBox, class_342 nextBox) {
        String startingBoxValue = coordinateBox.method_1882();
        int indexOfFirstSpace = startingBoxValue.indexOf(32);
        if (indexOfFirstSpace != -1) {
            String subStringToCut = startingBoxValue.substring(indexOfFirstSpace + 1);
            coordinateBox.method_1852(startingBoxValue.substring(0, indexOfFirstSpace));
            coordinateBox.method_1870(false);
            nextBox.method_1852(nextBox.method_1882() + subStringToCut);
            if (this.method_25399() == coordinateBox) {
                coordinateBox.method_25365(false);
                nextBox.method_25365(true);
                this.method_25395((class_364)nextBox);
                nextBox.method_1872(false);
            }
        }
    }

    protected void checkFields(class_364 focused) {
        this.handleCoordinateInputSpaces(this.xTextField, this.yTextField);
        this.handleCoordinateInputSpaces(this.yTextField, this.zTextField);
        this.handleCoordinateInputSpaces(this.zTextField, this.yawTextField);
        this.fieldValidator.validate(this.yawTextField);
        if (this.yawTextField == focused) {
            this.getCurrent().keepYawText = false;
            if (this.defaultYawButton != null) {
                this.defaultYawButton.field_22763 = true;
            }
        }
        this.fieldValidator.validate(this.xTextField);
        this.fieldYValidator.validate(this.yTextField);
        this.fieldValidator.validate(this.zTextField);
        WaypointEditForm current = this.getCurrent();
        current.name = this.nameTextField.method_1882();
        current.xText = this.xTextField.method_1882();
        current.yText = this.yTextField.method_1882();
        current.zText = this.zTextField.method_1882();
        current.yawText = this.yawTextField.method_1882();
        current.initial = this.initialTextField.method_1882();
        if (current.initial.length() > 2) {
            current.initial = current.initial.substring(0, 2);
            this.initialTextField.method_1852(current.initial);
        }
        if (current.yawText.length() > 4) {
            current.yawText = current.yawText.substring(0, 4);
            this.yawTextField.method_1852(current.yawText);
        }
        if (this.prefilled && this.editForms.size() > 1 && this.modMain.getSettings().waypointsMutualEdit) {
            current.keepName = current.name.isEmpty();
            current.keepXText = current.xText.isEmpty();
            current.keepYText = current.yText.isEmpty();
            current.keepZText = current.zText.isEmpty();
            current.keepInitial = current.initial.isEmpty();
        }
    }

    public void method_25393() {
        if (this.field_22787.field_1719 == null) {
            this.field_22787.method_1507(null);
            return;
        }
    }

    public void method_25420(class_332 guiGraphics, int par1, int par2, float par3) {
        this.renderEscapeScreen(guiGraphics, 0, 0, par3);
        super.method_25420(guiGraphics, par1, par2, par3);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int par1, int par2, float par3) {
        super.method_25394(guiGraphics, par1, par2, par3);
        super.renderTooltips(guiGraphics, par1, par2, par3);
    }

    @Override
    protected void renderPreDropdown(class_332 guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.method_25300(this.field_22793, this.screenTitle, this.field_22789 / 2, 20, -1);
        WaypointEditForm current = this.getCurrent();
        if (!this.canBeLabyMod) {
            this.ignoreEditBoxChanges = true;
        }
        if (this.ignoreEditBoxChanges) {
            if (!this.nameTextField.method_25370() && current.keepName) {
                Misc.setFieldText(this.nameTextField, this.namePlaceholder, -11184811);
                this.nameTextField.method_1883(0, false);
            }
            XaeroPath destinationWorldKeys = (XaeroPath)this.worlds.getCurrentKey();
            MinimapWorld destinationWorld = this.manager.getWorld(destinationWorldKeys);
            double waypointDimScale = this.session.getDimensionHelper().getDimCoordinateScale(destinationWorld);
            if (current.keepXText) {
                if (!this.xTextField.method_25370()) {
                    Misc.setFieldText(this.xTextField, this.xPlaceholder, -11184811);
                }
            } else if (current.xText.isEmpty()) {
                Misc.setFieldText(this.xTextField, "" + this.getAutomaticX(waypointDimScale), -11184811);
                this.xTextField.method_1883(0, false);
            }
            if (current.keepYText) {
                if (!this.yTextField.method_25370()) {
                    Misc.setFieldText(this.yTextField, this.yPlaceholder, -11184811);
                }
            } else if (current.yText.isEmpty()) {
                Misc.setFieldText(this.yTextField, this.getAutomaticYInput(destinationWorld), -11184811);
                this.yTextField.method_1883(0, false);
            }
            if (current.keepZText) {
                if (!this.zTextField.method_25370()) {
                    Misc.setFieldText(this.zTextField, this.zPlaceholder, -11184811);
                }
            } else if (current.zText.isEmpty()) {
                Misc.setFieldText(this.zTextField, "" + this.getAutomaticZ(waypointDimScale), -11184811);
                this.zTextField.method_1883(0, false);
            }
            if (!this.yawTextField.method_25370() && current.yawText.isEmpty()) {
                if (current.keepYawText) {
                    Misc.setFieldText(this.yawTextField, this.yawPlaceholder, -11184811);
                } else {
                    Misc.setFieldText(this.yawTextField, class_1074.method_4662((String)"gui.xaero_yaw", (Object[])new Object[0]), -11184811);
                }
                this.yawTextField.method_1883(0, false);
            }
            if (!this.initialTextField.method_25370() && current.initial.isEmpty()) {
                if (current.keepInitial) {
                    Misc.setFieldText(this.initialTextField, this.initialPlaceholder, -11184811);
                } else {
                    Misc.setFieldText(this.initialTextField, class_1074.method_4662((String)"gui.xaero_initial", (Object[])new Object[0]), -11184811);
                }
                this.initialTextField.method_1883(0, false);
            }
        }
        this.nameTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.xTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.yTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.zTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.yawTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.initialTextField.method_25394(guiGraphics, mouseX, mouseY, partial);
        if (this.ignoreEditBoxChanges) {
            Misc.setFieldText(this.nameTextField, current.name);
            Misc.setFieldText(this.xTextField, current.xText);
            Misc.setFieldText(this.yTextField, current.yText);
            Misc.setFieldText(this.zTextField, current.zText);
            Misc.setFieldText(this.yawTextField, current.yawText);
            Misc.setFieldText(this.initialTextField, current.initial);
        }
        this.ignoreEditBoxChanges = true;
    }

    @Override
    public boolean onSelected(DropDownWidget menu, int selected) {
        if (menu == this.setsDD) {
            this.sets.setCurrentSet(selected);
            if (this.session.getWorldState().getCurrentWorldPath().equals(this.worlds.getCurrentKey())) {
                this.manager.getCurrentWorld().setCurrentWaypointSetId(this.sets.getCurrentSetKey());
                try {
                    this.session.getWorldManagerIO().saveWorld(this.manager.getCurrentWorld());
                }
                catch (IOException e) {
                    MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
                }
            }
        } else if (menu == this.colorDD) {
            this.getCurrent().color = !this.getCurrent().defaultKeepColor ? WaypointColor.fromIndex(selected) : (selected == 0 ? null : WaypointColor.fromIndex(selected - 1));
        } else if (menu == this.containersDD) {
            this.containers.current = selected;
            MinimapWorld currentWorld = this.containers.current != this.defaultContainer ? this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()).getFirstWorld() : this.defaultWorld;
            this.sets = new GuiWaypointSets(false, currentWorld, this.containers.current == this.defaultContainer ? this.fromSet : currentWorld.getCurrentWaypointSetId());
            this.worlds = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()), this.session, currentWorld.getFullPath(), this.frozenAutoWorldPath);
            this.setsDD = this.createSetsDropdown();
            this.replaceWidget(this.setsDD, this.setsDD);
            this.worldsDD = this.createWorldsDropdown();
            this.replaceWidget(this.worldsDD, this.worldsDD);
        } else if (menu == this.worldsDD) {
            MinimapWorld currentWorld;
            this.worlds.current = selected;
            XaeroPath worldKeys = (XaeroPath)this.worlds.getCurrentKey();
            this.sets = new GuiWaypointSets(false, currentWorld, (currentWorld = this.manager.getWorld(worldKeys)) == this.defaultWorld ? this.fromSet : currentWorld.getCurrentWaypointSetId());
            this.setsDD = this.createSetsDropdown();
            this.replaceWidget(this.setsDD, this.setsDD);
        }
        return true;
    }
}

