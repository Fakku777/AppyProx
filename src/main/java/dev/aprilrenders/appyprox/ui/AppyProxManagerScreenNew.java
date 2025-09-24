package dev.aprilrenders.appyprox.ui;

import dev.aprilrenders.appyprox.AppyProxFabricClient;
import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.data.Cluster;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.ui.AccountSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Main UI screen for managing AppyProx clusters and accounts
 * Updated for newer Minecraft versions
 */
public class AppyProxManagerScreenNew extends Screen {
    private final Screen parent;
    private final AppyProxManager manager;
    
    private ButtonWidget toggleProxyModeButton;
    private ButtonWidget refreshDataButton;
    private ButtonWidget createClusterButton;
    private ButtonWidget accountControlButton;
    private ButtonWidget xaerosMapButton;
    private TextFieldWidget clusterNameField;
    
    private List<ProxyAccount> accounts;
    private List<Cluster> clusters;
    private int scrollOffset = 0;
    
    public AppyProxManagerScreenNew(Screen parent) {
        super(Text.literal("AppyProx Manager"));
        this.parent = parent;
        this.manager = AppyProxFabricClient.getManager();
        refreshData();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Toggle proxy mode button
        toggleProxyModeButton = ButtonWidget.builder(
            Text.literal(manager.isProxyMode() ? "Disable Proxy Mode" : "Enable Proxy Mode"),
            (button) -> toggleProxyMode()
        )
        .dimensions(this.width / 2 - 100, 30, 200, 20)
        .build();
        this.addDrawableChild(toggleProxyModeButton);
        
        // Refresh data button
        refreshDataButton = ButtonWidget.builder(
            Text.literal("Refresh"),
            (button) -> refreshData()
        )
        .dimensions(this.width / 2 - 100, 55, 95, 20)
        .build();
        this.addDrawableChild(refreshDataButton);
        
        // Create cluster button
        createClusterButton = ButtonWidget.builder(
            Text.literal("Create Cluster"),
            (button) -> createCluster()
        )
        .dimensions(this.width / 2 + 5, 55, 95, 20)
        .build();
        this.addDrawableChild(createClusterButton);
        
        // Cluster name input field
        clusterNameField = new TextFieldWidget(
            this.textRenderer, this.width / 2 - 100, 80, 200, 20,
            Text.literal("Cluster Name")
        );
        clusterNameField.setMaxLength(32);
        clusterNameField.setText("New Cluster");
        this.addDrawableChild(clusterNameField);
        
        // Account control button
        accountControlButton = ButtonWidget.builder(
            Text.literal("Account Control"),
            (button) -> openAccountControl()
        )
        .dimensions(this.width / 2 - 100, 105, 200, 20)
        .build();
        this.addDrawableChild(accountControlButton);
        
        // Xaeros Map integration button
        xaerosMapButton = ButtonWidget.builder(
            Text.literal("Xaeros Map Integration"),
            (button) -> openXaerosMap()
        )
        .dimensions(this.width / 2 - 100, 130, 200, 20)
        .build();
        this.addDrawableChild(xaerosMapButton);
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Back"),
            (button) -> this.close()
        )
        .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
        .build());
        
        // Update account control button state
        updateAccountControlButtonState();
        
        // Update Xaeros button state
        updateXaerosButtonState();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        
        // Connection status
        Text connectionStatus = manager.isConnected() ? 
            Text.literal("Connected").formatted(Formatting.GREEN) :
            Text.literal("Disconnected").formatted(Formatting.RED);
        context.drawCenteredTextWithShadow(this.textRenderer, connectionStatus, this.width / 2, 110, 0xFFFFFF);
        
        // Proxy mode status
        Text proxyModeStatus = Text.literal("Proxy Mode: " + 
            (manager.isProxyMode() ? "Enabled" : "Disabled"))
            .formatted(manager.isProxyMode() ? Formatting.GREEN : Formatting.YELLOW);
        context.drawCenteredTextWithShadow(this.textRenderer, proxyModeStatus, this.width / 2, 125, 0xFFFFFF);
        
        // Render accounts and clusters
        renderAccountsAndClusters(context, mouseX, mouseY);
        
        // Render text field
        clusterNameField.render(context, mouseX, mouseY, delta);
    }
    
    private void renderAccountsAndClusters(DrawContext context, int mouseX, int mouseY) {
        int startY = 150;
        int currentY = startY;
        
        // Accounts section
        context.drawTextWithShadow(this.textRenderer, "Proxy Accounts:", 20, currentY, 0xFFFFFF);
        currentY += 15;
        
        if (accounts != null && !accounts.isEmpty()) {
            for (ProxyAccount account : accounts) {
                if (currentY > this.height - 60) break; // Don't render beyond visible area
                
                Text accountText = Text.literal(String.format("  %s (%s) - %s", 
                    account.getUsername(), account.getId(), account.getStatus()))
                    .formatted(getStatusColor(account.getStatus()));
                    
                context.drawTextWithShadow(this.textRenderer, accountText, 30, currentY, 0xFFFFFF);
                
                // Add control buttons for each account
                if (mouseX > this.width - 120 && mouseX < this.width - 20 && 
                    mouseY >= currentY - 2 && mouseY < currentY + 12) {
                    context.drawTextWithShadow(this.textRenderer, "[Control]", this.width - 80, currentY, 0x00FF00);
                }
                
                currentY += 15;
            }
        } else {
            context.drawTextWithShadow(this.textRenderer, "  No accounts available", 30, currentY, 0x808080);
            currentY += 15;
        }
        
        currentY += 10;
        
        // Clusters section
        context.drawTextWithShadow(this.textRenderer, "Clusters:", 20, currentY, 0xFFFFFF);
        currentY += 15;
        
        if (clusters != null && !clusters.isEmpty()) {
            for (Cluster cluster : clusters) {
                if (currentY > this.height - 60) break;
                
                Text clusterText = Text.literal(String.format("  %s (%d members) - %s", 
                    cluster.getName(), cluster.getMemberCount(), cluster.getStatus()))
                    .formatted(getStatusColor(cluster.getStatus()));
                    
                context.drawTextWithShadow(this.textRenderer, clusterText, 30, currentY, 0xFFFFFF);
                
                // Show current task if any
                if (cluster.getCurrentTask() != null) {
                    context.drawTextWithShadow(this.textRenderer, 
                        "    Task: " + cluster.getCurrentTask(), 40, currentY + 12, 0xCCCCCC);
                    currentY += 12;
                }
                
                currentY += 15;
            }
        } else {
            context.drawTextWithShadow(this.textRenderer, "  No clusters available", 30, currentY, 0x808080);
        }
    }
    
    private Formatting getStatusColor(String status) {
        if (status == null) return Formatting.GRAY;
        
        switch (status.toLowerCase()) {
            case "active":
            case "online":
                return Formatting.GREEN;
            case "busy":
            case "working":
                return Formatting.YELLOW;
            case "error":
            case "offline":
                return Formatting.RED;
            default:
                return Formatting.WHITE;
        }
    }
    
    private void toggleProxyMode() {
        if (manager.isProxyMode()) {
            manager.disableProxyMode();
        } else {
            manager.enableProxyMode();
        }
        
        // Update button text
        toggleProxyModeButton.setMessage(
            Text.literal(manager.isProxyMode() ? "Disable Proxy Mode" : "Enable Proxy Mode")
        );
    }
    
    private void refreshData() {
        if (manager.getNetworkClient() != null) {
            manager.getNetworkClient().getAccounts().thenAccept(accountList -> {
                this.accounts = accountList;
            });
            
            manager.getNetworkClient().getClusters().thenAccept(clusterList -> {
                this.clusters = clusterList;
            });
        }
        
        // Update button states
        updateAccountControlButtonState();
        updateXaerosButtonState();
    }
    
    private void createCluster() {
        String clusterName = clusterNameField.getText();
        if (clusterName.trim().isEmpty()) {
            clusterName = "New Cluster";
        }
        
        if (manager.getNetworkClient() != null) {
            manager.getNetworkClient().createCluster(clusterName, null)
                .thenAccept(clusterId -> {
                    if (clusterId != null) {
                        // Refresh data to show new cluster
                        refreshData();
                        clusterNameField.setText("New Cluster");
                    }
                });
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle clicks on accounts/clusters for context menus
        if (button == 1) { // Right click
            // TODO: Implement context menus
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (clusterNameField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (clusterNameField.charTyped(chr, modifiers)) {
            return true;
        }
        
        return super.charTyped(chr, modifiers);
    }
    
    private void openAccountControl() {
        this.client.setScreen(new AccountSelectionScreen(this));
    }
    
    private void openXaerosMap() {
        this.client.setScreen(new XaerosMapScreen(this));
    }
    
    private void updateAccountControlButtonState() {
        if (accountControlButton != null && manager != null) {
            boolean canUseAccountControl = manager.isConnected() && manager.isProxyMode();
            accountControlButton.active = canUseAccountControl;
            
            // Update button text based on current state
            if (manager.getDirectAccountController() != null && 
                manager.getDirectAccountController().isControllingProxyAccount()) {
                var controlled = manager.getDirectAccountController().getCurrentControlledAccount();
                accountControlButton.setMessage(Text.literal("Switch Account (" + 
                    (controlled != null ? controlled.getUsername() : "Unknown") + ")"));
            } else {
                accountControlButton.setMessage(Text.literal("Account Control"));
            }
        }
    }
    
    private void updateXaerosButtonState() {
        if (xaerosMapButton != null && manager != null) {
            boolean xaerosAvailable = manager.getXaerosIntegration().isAvailable();
            xaerosMapButton.active = xaerosAvailable;
            
            // Update button text based on availability
            if (xaerosAvailable) {
                xaerosMapButton.setMessage(Text.literal("Xaeros Map Integration"));
            } else {
                xaerosMapButton.setMessage(Text.literal("Xaeros Map (Unavailable)"));
            }
        }
    }
    
    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
