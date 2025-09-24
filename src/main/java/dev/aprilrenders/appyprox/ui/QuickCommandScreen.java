package dev.aprilrenders.appyprox.ui;

import dev.aprilrenders.appyprox.AppyProxFabricClient;
import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.data.Cluster;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

/**
 * Quick command interface for rapid cluster operations
 */
public class QuickCommandScreen extends Screen {
    private final Screen parent;
    private final AppyProxManager manager;
    
    private TextFieldWidget commandField;
    private ButtonWidget sendCommandButton;
    private ButtonWidget startMiningButton;
    private ButtonWidget startBuildingButton;
    private ButtonWidget gatherItemsButton;
    private ButtonWidget returnToBaseButton;
    private ButtonWidget stopAllButton;
    
    private String selectedClusterId = null;
    
    public QuickCommandScreen(Screen parent) {
        super(Text.literal("AppyProx Quick Commands"));
        this.parent = parent;
        this.manager = AppyProxFabricClient.getManager();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Command input field
        commandField = new TextFieldWidget(
            this.textRenderer, this.width / 2 - 100, 50, 200, 20,
            Text.literal("Enter command...")
        );
        commandField.setMaxLength(100);
        commandField.setText("/say Hello from cluster!");
        this.addDrawableChild(commandField);
        
        // Send custom command button
        sendCommandButton = ButtonWidget.builder(
            Text.literal("Send Command"),
            (button) -> sendCustomCommand()
        )
        .dimensions(this.width / 2 - 50, 80, 100, 20)
        .build();
        this.addDrawableChild(sendCommandButton);
        
        // Quick action buttons
        int buttonWidth = 90;
        int buttonHeight = 20;
        int spacing = 95;
        int startY = 120;
        
        startMiningButton = ButtonWidget.builder(
            Text.literal("Start Mining"),
            (button) -> startQuickTask("mining")
        )
        .dimensions(this.width / 2 - spacing, startY, buttonWidth, buttonHeight)
        .build();
        this.addDrawableChild(startMiningButton);
        
        startBuildingButton = ButtonWidget.builder(
            Text.literal("Start Building"),
            (button) -> startQuickTask("building")
        )
        .dimensions(this.width / 2, startY, buttonWidth, buttonHeight)
        .build();
        this.addDrawableChild(startBuildingButton);
        
        gatherItemsButton = ButtonWidget.builder(
            Text.literal("Gather Items"),
            (button) -> startQuickTask("gather")
        )
        .dimensions(this.width / 2 - spacing, startY + 30, buttonWidth, buttonHeight)
        .build();
        this.addDrawableChild(gatherItemsButton);
        
        returnToBaseButton = ButtonWidget.builder(
            Text.literal("Return to Base"),
            (button) -> startQuickTask("return_base")
        )
        .dimensions(this.width / 2, startY + 30, buttonWidth, buttonHeight)
        .build();
        this.addDrawableChild(returnToBaseButton);
        
        stopAllButton = ButtonWidget.builder(
            Text.literal("§cStop All Tasks"),
            (button) -> stopAllTasks()
        )
        .dimensions(this.width / 2 - 50, startY + 60, 100, buttonHeight)
        .build();
        this.addDrawableChild(stopAllButton);
        
        // Close button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Close"),
            (button) -> this.close()
        )
        .dimensions(this.width / 2 - 50, this.height - 40, 100, 20)
        .build());
        
        // Load available clusters
        loadClusters();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Instructions
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            Text.literal("Quick commands for active clusters").formatted(Formatting.GRAY),
            this.width / 2, 
            35, 
            0xFFFFFF
        );
        
        // Show selected cluster or status
        String statusText;
        if (manager == null || !manager.isConnected()) {
            statusText = "§cNot connected to AppyProx backend";
        } else if (selectedClusterId != null) {
            statusText = "§aSelected cluster: " + selectedClusterId;
        } else {
            statusText = "§eNo cluster selected - commands will go to all clusters";
        }
        
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(statusText),
            this.width / 2,
            100,
            0xFFFFFF
        );
        
        // Render command field
        commandField.render(context, mouseX, mouseY, delta);
    }
    
    private void sendCustomCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        
        if (manager != null && manager.isConnected()) {
            if (selectedClusterId != null) {
                // Send to specific cluster
                manager.sendClusterCommand(selectedClusterId, command, null);
            } else {
                // Send to all clusters
                manager.getNetworkClient().getClusters().thenAccept(clusters -> {
                    for (Cluster cluster : clusters) {
                        manager.sendClusterCommand(cluster.getId(), command, null);
                    }
                });
            }
        }
        
        // Clear the command field
        commandField.setText("");
    }
    
    private void startQuickTask(String taskType) {
        if (manager == null || !manager.isConnected()) {
            return;
        }
        
        Map<String, Object> taskParams = createTaskParameters(taskType);
        
        if (selectedClusterId != null) {
            manager.startClusterTask(selectedClusterId, taskType, taskParams);
        } else {
            // Start task for all clusters
            manager.getNetworkClient().getClusters().thenAccept(clusters -> {
                for (Cluster cluster : clusters) {
                    manager.startClusterTask(cluster.getId(), taskType, taskParams);
                }
            });
        }
    }
    
    private void stopAllTasks() {
        if (manager == null || !manager.isConnected()) {
            return;
        }
        
        if (selectedClusterId != null) {
            manager.sendClusterCommand(selectedClusterId, "stop", null);
        } else {
            manager.getNetworkClient().getClusters().thenAccept(clusters -> {
                for (Cluster cluster : clusters) {
                    manager.sendClusterCommand(cluster.getId(), "stop", null);
                }
            });
        }
    }
    
    private Map<String, Object> createTaskParameters(String taskType) {
        Map<String, Object> params = new HashMap<>();
        
        switch (taskType) {
            case "mining":
                params.put("resource", "diamond");
                params.put("quantity", 64);
                params.put("depth", "y=11");
                break;
            case "building":
                params.put("schematic", "default");
                params.put("material", "stone");
                break;
            case "gather":
                params.put("items", new String[]{"wood", "food", "stone"});
                params.put("quantity", 128);
                break;
            case "return_base":
                params.put("waypoint", "base");
                params.put("deposit_items", true);
                break;
        }
        
        return params;
    }
    
    private void loadClusters() {
        if (manager != null && manager.getNetworkClient() != null) {
            manager.getNetworkClient().getClusters().thenAccept(clusters -> {
                if (!clusters.isEmpty()) {
                    // Auto-select first cluster if only one exists
                    if (clusters.size() == 1) {
                        selectedClusterId = clusters.get(0).getId();
                    }
                }
            });
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (commandField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        // Enter key sends the command
        if (keyCode == 257) { // Enter key
            sendCustomCommand();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (commandField.charTyped(chr, modifiers)) {
            return true;
        }
        
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}