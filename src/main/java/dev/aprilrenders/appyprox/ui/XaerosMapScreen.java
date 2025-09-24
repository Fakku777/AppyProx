package dev.aprilrenders.appyprox.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.data.Cluster;
import dev.aprilrenders.appyprox.AppyProxFabricClient;

import java.util.List;
import java.util.ArrayList;

/**
 * Screen for managing Xaeros World Map integration features
 * Allows focusing on accounts, adding waypoints, and managing cluster overlays
 */
public class XaerosMapScreen extends Screen {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(XaerosMapScreen.class);
    
    private final Screen parent;
    private final AppyProxManager manager;
    
    // UI components
    private ButtonWidget focusAccountButton;
    private ButtonWidget focusPositionButton;
    private ButtonWidget addWaypointButton;
    private ButtonWidget showClustersButton;
    private ButtonWidget clearWaypointsButton;
    
    // Text fields
    private TextFieldWidget accountIdField;
    private TextFieldWidget xCoordField;
    private TextFieldWidget yCoordField;
    private TextFieldWidget zCoordField;
    private TextFieldWidget waypointNameField;
    
    // Lists for scrollable content
    private List<ProxyAccount> trackedAccounts;
    private List<Cluster> availableClusters;
    
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 6;
    
    public XaerosMapScreen(Screen parent) {
        super(Text.translatable("ui.appyprox.xaeros_map.title"));
        this.parent = parent;
        this.manager = AppyProxFabricClient.getManager();
        this.trackedAccounts = new ArrayList<>();
        this.availableClusters = new ArrayList<>();
        
        // Get current tracked accounts and clusters
        if (manager.getXaerosIntegration().isAvailable()) {
            this.trackedAccounts.addAll(manager.getXaerosIntegration().getTrackedAccounts());
            this.availableClusters.addAll(manager.getClusterGroups());
        }
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;
        
        // Account ID input field
        accountIdField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, 200, 20, Text.empty());
        accountIdField.setPlaceholder(Text.translatable("ui.appyprox.xaeros_map.account_id_placeholder"));
        accountIdField.setMaxLength(50);
        this.addSelectableChild(accountIdField);
        
        startY += spacing;
        
        // Focus on account button
        focusAccountButton = ButtonWidget.builder(
            Text.translatable("ui.appyprox.xaeros_map.focus_account"),
            button -> focusOnAccount()
        ).dimensions(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(focusAccountButton);
        
        startY += spacing * 2;
        
        // Coordinate input fields (X, Y, Z)
        int fieldWidth = 60;
        int fieldStartX = centerX - (fieldWidth * 3 + 20) / 2;
        
        xCoordField = new TextFieldWidget(this.textRenderer, fieldStartX, startY, fieldWidth, 20, Text.empty());
        xCoordField.setPlaceholder(Text.literal("X"));
        xCoordField.setMaxLength(10);
        this.addSelectableChild(xCoordField);
        
        yCoordField = new TextFieldWidget(this.textRenderer, fieldStartX + fieldWidth + 10, startY, fieldWidth, 20, Text.empty());
        yCoordField.setPlaceholder(Text.literal("Y"));
        yCoordField.setMaxLength(10);
        this.addSelectableChild(yCoordField);
        
        zCoordField = new TextFieldWidget(this.textRenderer, fieldStartX + fieldWidth * 2 + 20, startY, fieldWidth, 20, Text.empty());
        zCoordField.setPlaceholder(Text.literal("Z"));
        zCoordField.setMaxLength(10);
        this.addSelectableChild(zCoordField);
        
        startY += spacing;
        
        // Focus on position button
        focusPositionButton = ButtonWidget.builder(
            Text.translatable("ui.appyprox.xaeros_map.focus_position"),
            button -> focusOnPosition()
        ).dimensions(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(focusPositionButton);
        
        startY += spacing * 2;
        
        // Waypoint name field
        waypointNameField = new TextFieldWidget(this.textRenderer, centerX - 100, startY, 200, 20, Text.empty());
        waypointNameField.setPlaceholder(Text.translatable("ui.appyprox.xaeros_map.waypoint_name_placeholder"));
        waypointNameField.setMaxLength(50);
        this.addSelectableChild(waypointNameField);
        
        startY += spacing;
        
        // Add waypoint button
        addWaypointButton = ButtonWidget.builder(
            Text.translatable("ui.appyprox.xaeros_map.add_waypoint"),
            button -> addCustomWaypoint()
        ).dimensions(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(addWaypointButton);
        
        startY += spacing * 2;
        
        // Show clusters button
        showClustersButton = ButtonWidget.builder(
            Text.translatable("ui.appyprox.xaeros_map.show_clusters"),
            button -> showAllClusters()
        ).dimensions(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(showClustersButton);
        
        startY += spacing;
        
        // Clear waypoints button
        clearWaypointsButton = ButtonWidget.builder(
            Text.translatable("ui.appyprox.xaeros_map.clear_waypoints"),
            button -> clearAllWaypoints()
        ).dimensions(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(clearWaypointsButton);
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.back"),
            button -> this.client.setScreen(parent)
        ).dimensions(10, this.height - 30, 80, 20).build());
        
        // Update button states based on Xaeros availability
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        boolean xaerosAvailable = manager.getXaerosIntegration().isAvailable();
        
        focusAccountButton.active = xaerosAvailable;
        focusPositionButton.active = xaerosAvailable;
        addWaypointButton.active = xaerosAvailable;
        showClustersButton.active = xaerosAvailable;
        clearWaypointsButton.active = xaerosAvailable;
    }
    
    private void focusOnAccount() {
        String accountId = accountIdField.getText().trim();
        if (!accountId.isEmpty()) {
            manager.focusMapOnAccount(accountId);
            
            // Show confirmation message
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.focusing_account", accountId));
            }
        }
    }
    
    private void focusOnPosition() {
        try {
            int x = Integer.parseInt(xCoordField.getText());
            int y = Integer.parseInt(yCoordField.getText());
            int z = Integer.parseInt(zCoordField.getText());
            
            BlockPos position = new BlockPos(x, y, z);
            manager.focusMapOnPosition(position);
            
            // Show confirmation message
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.focusing_position", x, y, z));
            }
            
        } catch (NumberFormatException e) {
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.invalid_coordinates"));
            }
        }
    }
    
    private void addCustomWaypoint() {
        String name = waypointNameField.getText().trim();
        if (name.isEmpty()) {
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.waypoint_name_required"));
            }
            return;
        }
        
        try {
            int x = Integer.parseInt(xCoordField.getText());
            int y = Integer.parseInt(yCoordField.getText());
            int z = Integer.parseInt(zCoordField.getText());
            
            BlockPos position = new BlockPos(x, y, z);
            String waypointId = "custom_" + System.currentTimeMillis();
            int color = 0x00FFFF; // Cyan for custom waypoints
            
            manager.getXaerosIntegration().addCustomWaypoint(waypointId, name, position, color, "Custom waypoint added via AppyProx")
                .thenAccept(success -> {
                    if (client.player != null) {
                        if (success) {
                            client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.waypoint_added", name));
                        } else {
                            client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.waypoint_add_failed", name));
                        }
                    }
                });
            
            // Clear fields
            waypointNameField.setText("");
            
        } catch (NumberFormatException e) {
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.invalid_coordinates"));
            }
        }
    }
    
    private void showAllClusters() {
        for (Cluster cluster : availableClusters) {
            manager.getXaerosIntegration().showClusterFormation(cluster);
        }
        
        if (client.player != null) {
            client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.showing_clusters", availableClusters.size()));
        }
    }
    
    private void clearAllWaypoints() {
        // This would clear all AppyProx waypoints from Xaeros
        // For now, just show a message
        if (client.player != null) {
            client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.cleared_waypoints"));
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Render title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Render status information
        int statusY = 30;
        String xaerosStatus = manager.getXaerosIntegration().isAvailable() ? 
            "§aXaeros integration active" : "§cXaeros integration unavailable";
        context.drawTextWithShadow(this.textRenderer, Text.literal(xaerosStatus), 10, statusY, 0xFFFFFF);
        
        if (manager.getXaerosIntegration().isAvailable()) {
            statusY += 12;
            String worldMapStatus = manager.getXaerosIntegration().isWorldMapAvailable() ? "§aWorld Map: Yes" : "§7World Map: No";
            context.drawTextWithShadow(this.textRenderer, Text.literal(worldMapStatus), 10, statusY, 0xFFFFFF);
            
            statusY += 12;
            String minimapStatus = manager.getXaerosIntegration().isMinimapAvailable() ? "§aMinimap: Yes" : "§7Minimap: No";
            context.drawTextWithShadow(this.textRenderer, Text.literal(minimapStatus), 10, statusY, 0xFFFFFF);
        }
        
        // Render field labels
        int centerX = this.width / 2;
        context.drawTextWithShadow(this.textRenderer, Text.translatable("ui.appyprox.xaeros_map.account_id"), centerX - 100, 42, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("ui.appyprox.xaeros_map.coordinates"), centerX - 90, 120, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("ui.appyprox.xaeros_map.waypoint_name"), centerX - 100, 187, 0xFFFFFF);
        
        // Render tracked accounts list (right side)
        int listStartX = this.width - 200;
        int listStartY = 50;
        context.drawTextWithShadow(this.textRenderer, Text.translatable("ui.appyprox.xaeros_map.tracked_accounts"), listStartX, listStartY - 15, 0xFFFFFF);
        
        int accountIndex = 0;
        for (ProxyAccount account : trackedAccounts) {
            if (accountIndex >= scrollOffset && accountIndex < scrollOffset + ITEMS_PER_PAGE) {
                int yPos = listStartY + (accountIndex - scrollOffset) * 25;
                
                // Account name
                String accountText = account.getUsername();
                context.drawTextWithShadow(this.textRenderer, Text.literal(accountText), listStartX, yPos, 0xFFFFFF);
                
                // Account status with color
                String statusText = account.getStatus();
                int statusColor = switch (account.getStatus().toLowerCase()) {
                    case "online" -> 0x00FF00;
                    case "busy" -> 0x0080FF;
                    case "working" -> 0xFF8000;
                    case "offline" -> 0xFF0000;
                    default -> 0xFFFF00;
                };
                context.drawTextWithShadow(this.textRenderer, Text.literal(statusText), listStartX, yPos + 10, statusColor);
                
                // Focus button for this account
                if (mouseX >= listStartX + 100 && mouseX <= listStartX + 150 && 
                    mouseY >= yPos && mouseY <= yPos + 20) {
                    // Highlight on hover
                    context.fill(listStartX + 100, yPos, listStartX + 150, yPos + 20, 0x80FFFFFF);
                }
            }
            accountIndex++;
        }
        
        // Render text fields
        accountIdField.render(context, mouseX, mouseY, delta);
        xCoordField.render(context, mouseX, mouseY, delta);
        yCoordField.render(context, mouseX, mouseY, delta);
        zCoordField.render(context, mouseX, mouseY, delta);
        waypointNameField.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle account list clicks
        int listStartX = this.width - 200;
        int listStartY = 50;
        
        if (mouseX >= listStartX + 100 && mouseX <= listStartX + 150) {
            int accountIndex = scrollOffset + (int) ((mouseY - listStartY) / 25);
            if (accountIndex >= 0 && accountIndex < trackedAccounts.size()) {
                ProxyAccount account = trackedAccounts.get(accountIndex);
                manager.focusMapOnAccount(account.getId());
                
                if (client.player != null) {
                    client.player.sendMessage(Text.translatable("ui.appyprox.xaeros_map.focusing_account", account.getUsername()));
                }
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle Enter key in text fields
        if (keyCode == 257) { // Enter key
            if (accountIdField.isFocused() && !accountIdField.getText().isEmpty()) {
                focusOnAccount();
                return true;
            }
            if (waypointNameField.isFocused() && !waypointNameField.getText().isEmpty()) {
                addCustomWaypoint();
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}