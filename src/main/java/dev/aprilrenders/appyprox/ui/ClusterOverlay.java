package dev.aprilrenders.appyprox.ui;

import dev.aprilrenders.appyprox.AppyProxFabricClient;
import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.data.Cluster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * HUD overlay for displaying real-time cluster and account information
 */
@Environment(EnvType.CLIENT)
public class ClusterOverlay {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterOverlay.class);
    
    private static boolean enabled = true;
    private static boolean showAccountList = true;
    private static boolean showClusterStatus = true;
    private static boolean showProxyModeStatus = true;
    
    // Cache for account and cluster data
    private static List<ProxyAccount> cachedAccounts;
    private static List<Cluster> cachedClusters;
    private static long lastDataUpdate = 0;
    private static final long DATA_REFRESH_INTERVAL = 5000; // 5 seconds
    
    /**
     * Initialize the cluster overlay system
     */
    public static void initialize() {
        LOGGER.info("Initializing cluster overlay system...");
        
        // Register HUD rendering callback
        HudRenderCallback.EVENT.register(ClusterOverlay::renderHud);
        
        LOGGER.info("Cluster overlay system initialized");
    }
    
    /**
     * Render the HUD overlay
     */
    private static void renderHud(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        AppyProxManager manager = AppyProxFabricClient.getManager();
        
        // Only render in multiplayer and if enabled
        if (!enabled || client.player == null || client.getNetworkHandler() == null || manager == null) {
            return;
        }
        
        // Update cached data periodically
        updateCachedData(manager);
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Render different components
        int yOffset = 10;
        
        if (showProxyModeStatus) {
            yOffset = renderProxyModeStatus(context, manager, screenWidth, yOffset);
        }
        
        if (showClusterStatus) {
            yOffset = renderClusterStatus(context, screenWidth, yOffset);
        }
        
        if (showAccountList) {
            yOffset = renderAccountList(context, screenWidth, yOffset);
        }
    }
    
    /**
     * Render proxy mode status at the top right
     */
    private static int renderProxyModeStatus(DrawContext context, AppyProxManager manager, int screenWidth, int yOffset) {
        String statusText = "AppyProx: ";
        Formatting color = Formatting.GRAY;
        
        if (!manager.isConnected()) {
            statusText += "Offline";
            color = Formatting.RED;
        } else if (manager.isProxyMode()) {
            int activeInstances = manager.getProxyModeManager().getActiveInstanceCount();
            statusText += String.format("Active (%d)", activeInstances);
            color = Formatting.GREEN;
        } else {
            statusText += "Ready";
            color = Formatting.YELLOW;
        }
        
        Text statusDisplay = Text.literal(statusText).formatted(color);
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(statusDisplay);
        
        context.drawTextWithShadow(
            MinecraftClient.getInstance().textRenderer,
            statusDisplay,
            screenWidth - textWidth - 10,
            yOffset,
            0xFFFFFF
        );
        
        return yOffset + 12;
    }
    
    /**
     * Render cluster status information
     */
    private static int renderClusterStatus(DrawContext context, int screenWidth, int yOffset) {
        if (cachedClusters == null || cachedClusters.isEmpty()) {
            return yOffset;
        }
        
        // Title
        context.drawTextWithShadow(
            MinecraftClient.getInstance().textRenderer,
            Text.literal("Clusters:").formatted(Formatting.AQUA),
            screenWidth - 120,
            yOffset,
            0xFFFFFF
        );
        yOffset += 12;
        
        // List active clusters
        for (Cluster cluster : cachedClusters) {
            if (yOffset > MinecraftClient.getInstance().getWindow().getScaledHeight() - 50) {
                break; // Stop if we're getting close to the bottom
            }
            
            String clusterInfo = String.format("  %s (%d)", cluster.getName(), cluster.getMemberCount());
            Formatting statusColor = getStatusFormatting(cluster.getStatus());
            
            context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                Text.literal(clusterInfo).formatted(statusColor),
                screenWidth - 118,
                yOffset,
                0xFFFFFF
            );
            yOffset += 10;
            
            // Show current task if any
            if (cluster.getCurrentTask() != null) {
                String taskInfo = String.format("    → %s", cluster.getCurrentTask());
                context.drawTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    Text.literal(taskInfo).formatted(Formatting.GRAY),
                    screenWidth - 115,
                    yOffset,
                    0xFFFFFF
                );
                yOffset += 10;
            }
        }
        
        return yOffset + 5;
    }
    
    /**
     * Render list of proxy accounts
     */
    private static int renderAccountList(DrawContext context, int screenWidth, int yOffset) {
        if (cachedAccounts == null || cachedAccounts.isEmpty()) {
            return yOffset;
        }
        
        // Title
        context.drawTextWithShadow(
            MinecraftClient.getInstance().textRenderer,
            Text.literal("Proxy Accounts:").formatted(Formatting.AQUA),
            screenWidth - 120,
            yOffset,
            0xFFFFFF
        );
        yOffset += 12;
        
        // List accounts (limit to prevent overflow)
        int maxAccounts = Math.min(cachedAccounts.size(), 8);
        for (int i = 0; i < maxAccounts; i++) {
            ProxyAccount account = cachedAccounts.get(i);
            
            String accountInfo = String.format("  %s", account.getUsername());
            Formatting statusColor = getStatusFormatting(account.getStatus());
            
            context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                Text.literal(accountInfo).formatted(statusColor),
                screenWidth - 118,
                yOffset,
                0xFFFFFF
            );
            
            // Show health/food if available
            if (account.getHealth() < 20 || account.getFood() < 20) {
                String healthInfo = String.format("❤%d 🍖%d", account.getHealth(), account.getFood());
                context.drawTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    Text.literal("    " + healthInfo).formatted(Formatting.RED),
                    screenWidth - 115,
                    yOffset + 10,
                    0xFFFFFF
                );
                yOffset += 10;
            }
            
            yOffset += 10;
        }
        
        // Show "and X more..." if there are more accounts
        if (cachedAccounts.size() > maxAccounts) {
            int remaining = cachedAccounts.size() - maxAccounts;
            context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                Text.literal(String.format("  ... and %d more", remaining)).formatted(Formatting.GRAY),
                screenWidth - 118,
                yOffset,
                0xFFFFFF
            );
            yOffset += 10;
        }
        
        return yOffset;
    }
    
    /**
     * Update cached data from the backend
     */
    private static void updateCachedData(AppyProxManager manager) {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastDataUpdate < DATA_REFRESH_INTERVAL) {
            return; // Too soon to refresh
        }
        
        lastDataUpdate = currentTime;
        
        // Async update to avoid blocking the render thread
        if (manager.getNetworkClient() != null) {
            CompletableFuture.allOf(
                manager.getNetworkClient().getAccounts()
                    .thenAccept(accounts -> cachedAccounts = accounts)
                    .exceptionally(throwable -> {
                        LOGGER.debug("Failed to fetch accounts for overlay: {}", throwable.getMessage());
                        return null;
                    }),
                    
                manager.getNetworkClient().getClusters()
                    .thenAccept(clusters -> cachedClusters = clusters)
                    .exceptionally(throwable -> {
                        LOGGER.debug("Failed to fetch clusters for overlay: {}", throwable.getMessage());
                        return null;
                    })
            );
        }
    }
    
    /**
     * Get appropriate formatting for status strings
     */
    private static Formatting getStatusFormatting(String status) {
        if (status == null) return Formatting.GRAY;
        
        switch (status.toLowerCase()) {
            case "active":
            case "online":
            case "healthy":
                return Formatting.GREEN;
            case "busy":
            case "working":
            case "mining":
            case "building":
                return Formatting.YELLOW;
            case "error":
            case "offline":
            case "disconnected":
                return Formatting.RED;
            case "idle":
                return Formatting.AQUA;
            default:
                return Formatting.WHITE;
        }
    }
    
    // Toggle methods for different overlay components
    public static void toggleEnabled() {
        enabled = !enabled;
        LOGGER.info("Cluster overlay {}", enabled ? "enabled" : "disabled");
    }
    
    public static void toggleAccountList() {
        showAccountList = !showAccountList;
        LOGGER.info("Account list overlay {}", showAccountList ? "enabled" : "disabled");
    }
    
    public static void toggleClusterStatus() {
        showClusterStatus = !showClusterStatus;
        LOGGER.info("Cluster status overlay {}", showClusterStatus ? "enabled" : "disabled");
    }
    
    public static void toggleProxyModeStatus() {
        showProxyModeStatus = !showProxyModeStatus;
        LOGGER.info("Proxy mode status overlay {}", showProxyModeStatus ? "enabled" : "disabled");
    }
    
    // Getters for current state
    public static boolean isEnabled() { return enabled; }
    public static boolean isShowingAccountList() { return showAccountList; }
    public static boolean isShowingClusterStatus() { return showClusterStatus; }
    public static boolean isShowingProxyModeStatus() { return showProxyModeStatus; }
}