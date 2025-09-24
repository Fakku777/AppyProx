package dev.aprilrenders.appyprox.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.aprilrenders.appyprox.integrations.XaerosIntegration;
import dev.aprilrenders.appyprox.network.AppyProxNetworkClient;
import dev.aprilrenders.appyprox.proxy.ProxyModeManager;
import dev.aprilrenders.appyprox.control.DirectAccountController;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.data.Cluster;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core manager for AppyProx functionality
 * Handles proxy mode switching, account control, and cluster coordination
 */
public class AppyProxManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppyProxManager.class);
    
    // Proxy mode management
    private ProxyModeManager proxyModeManager;
    private boolean proxyModeEnabled = false;
    
    // Network and integration components
    private AppyProxNetworkClient networkClient;
    private XaerosIntegration xaerosIntegration;
    private DirectAccountController directAccountController;
    private boolean connected = false;
    
    // Cluster management
    private final Map<String, Cluster> clusterGroups = new ConcurrentHashMap<>();
    private String activeCluster = null;
    
    // Account control
    private String controlledAccountId = null;
    private boolean directControlMode = false;
    
    // Status tracking
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 1000; // 1 second
    
    public AppyProxManager() {
        LOGGER.info("Initializing AppyProx Manager...");
        
        // Initialize proxy mode manager
        proxyModeManager = new ProxyModeManager();
        
        // Initialize network client
        networkClient = new AppyProxNetworkClient("localhost", 3000, 8081);
        
        // Initialize Xaeros integration
        xaerosIntegration = new XaerosIntegration();
        
        // Initialize direct account controller
        directAccountController = new DirectAccountController(this);
        
        // Connect to backend
        initializeConnection();
        
        LOGGER.info("AppyProx Manager initialized");
    }
    
    public void tick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
            lastUpdateTime = currentTime;
            
            // Update proxy mode manager
            if (proxyModeManager != null) {
                proxyModeManager.tick();
            }
            
            // Update cluster status
            updateClusterStatus();
            
            // Update Xaeros integration
            if (xaerosIntegration != null && xaerosIntegration.isInitialized()) {
                xaerosIntegration.tick();
            }
        }
    }
    
    private void updateClusterStatus() {
        // Update cluster group statuses
        for (Cluster cluster : clusterGroups.values()) {
            // Update cluster status from backend if needed
            
            // Update Xaeros integration with cluster data
            if (xaerosIntegration != null && xaerosIntegration.isAvailable()) {
                xaerosIntegration.showClusterFormation(cluster);
            }
        }
    }
    
    private void initializeConnection() {
        // Initialize Xaeros integration
        try {
            xaerosIntegration.initialize();
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize Xaeros integration: {}", e.getMessage());
        }
        
        // Connect to AppyProx backend
        networkClient.connect().thenAccept(success -> {
            if (success) {
                connected = true;
                LOGGER.info("Connected to AppyProx backend");
                
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(Text.of("§a[AppyProx] Connected to backend"));
                }
            } else {
                LOGGER.warn("Failed to connect to AppyProx backend");
            }
        });
    }
    
    /**
     * Toggle proxy mode on/off
     * This allows switching between direct play and proxy control
     */
    public void toggleProxyMode() {
        proxyModeEnabled = !proxyModeEnabled;
        
        if (proxyModeEnabled) {
            enableProxyMode();
        } else {
            disableProxyMode();
        }
        
        LOGGER.info("Proxy mode {}", proxyModeEnabled ? "enabled" : "disabled");
    }
    
    public void enableProxyMode() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                Text.of("§a[AppyProx] Proxy mode enabled - You can now control remote accounts")
            );
        }
        
        // Initialize proxy mode
        proxyModeManager.enableProxyMode();
    }
    
    public void disableProxyMode() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                Text.of("§c[AppyProx] Proxy mode disabled - Direct play mode")
            );
        }
        
        // Disable proxy mode
        proxyModeManager.disableProxyMode();
        
        // Exit direct control if active
        exitDirectControl();
    }
    
    /**
     * Enter direct control mode for a specific account
     * This allows playing as if you were that account directly
     */
    public void enterDirectControl(String accountId) {
        if (!proxyModeEnabled) {
            LOGGER.warn("Cannot enter direct control - proxy mode is disabled");
            return;
        }
        
        this.controlledAccountId = accountId;
        this.directControlMode = true;
        
        // Switch the client to control this account
        // TODO: Implement direct control switching via proxy mode manager
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                Text.of("§e[AppyProx] Now controlling account: " + accountId)
            );
        }
        
        LOGGER.info("Entered direct control mode for account: {}", accountId);
    }
    
    /**
     * Exit direct control mode and return to proxy management
     */
    public void exitDirectControl() {
        if (!directControlMode) return;
        
        String previousAccount = controlledAccountId;
        this.controlledAccountId = null;
        this.directControlMode = false;
        
        // Return to proxy management mode
        // TODO: Implement direct control exit via proxy mode manager
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                Text.of("§e[AppyProx] Exited control of account: " + previousAccount)
            );
        }
        
        LOGGER.info("Exited direct control mode");
    }
    
    /**
     * Create a new cluster group
     */
    public void createCluster(String clusterName, List<String> accountIds) {
        // Use network client to create cluster on backend
        networkClient.createCluster(clusterName, null)
            .thenAccept(clusterId -> {
                if (clusterId != null) {
                    Cluster cluster = new Cluster(clusterId, clusterName);
                    for (String accountId : accountIds) {
                        cluster.addMember(accountId);
                    }
                    clusterGroups.put(clusterId, cluster);
                    
                    LOGGER.info("Created cluster group: {} with {} accounts", clusterName, accountIds.size());
                    
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        client.player.sendMessage(Text.of("§a[AppyProx] Created cluster: " + clusterName));
                    }
                } else {
                    LOGGER.error("Failed to create cluster: {}", clusterName);
                }
            });
    }
    
    /**
     * Assign a task to a cluster
     */
    public void assignTaskToCluster(String clusterName, String taskType, Map<String, Object> parameters) {
        Cluster cluster = clusterGroups.get(clusterName);
        if (cluster != null) {
            networkClient.startClusterTask(cluster.getId(), taskType, parameters)
                .thenAccept(taskId -> {
                    if (taskId != null) {
                        cluster.setCurrentTask(taskType);
                        LOGGER.info("Assigned task {} to cluster {} (Task ID: {})", taskType, clusterName, taskId);
                        
                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client.player != null) {
                            client.player.sendMessage(Text.of("§a[AppyProx] Task assigned: " + taskType));
                        }
                    } else {
                        LOGGER.error("Failed to assign task {} to cluster {}", taskType, clusterName);
                    }
                });
        } else {
            LOGGER.warn("Cluster not found: {}", clusterName);
        }
    }
    
    /**
     * Set the active cluster for commands
     */
    public void setActiveCluster(String clusterName) {
        if (clusterGroups.containsKey(clusterName)) {
            this.activeCluster = clusterName;
            LOGGER.info("Set active cluster to: {}", clusterName);
        } else {
            LOGGER.warn("Cannot set active cluster - cluster not found: {}", clusterName);
        }
    }
    
    /**
     * Send a command to the active cluster
     */
    public void sendActiveClusterCommand(String command) {
        if (activeCluster != null) {
            Cluster cluster = clusterGroups.get(activeCluster);
            if (cluster != null) {
                networkClient.sendClusterCommand(cluster.getId(), command, null)
                    .thenAccept(success -> {
                        if (success) {
                            LOGGER.info("Sent command to active cluster {}: {}", activeCluster, command);
                        } else {
                            LOGGER.error("Failed to send command to cluster {}: {}", activeCluster, command);
                        }
                    });
            }
        } else {
            LOGGER.warn("No active cluster set");
        }
    }
    
    /**
     * Focus Xaeros map on a specific account
     */
    public void focusMapOnAccount(String accountId) {
        if (xaerosIntegration != null && xaerosIntegration.isAvailable()) {
            xaerosIntegration.focusOnAccount(accountId).thenAccept(success -> {
                if (success) {
                    LOGGER.info("Focused map on account: {}", accountId);
                } else {
                    LOGGER.warn("Failed to focus map on account: {}", accountId);
                }
            });
        } else {
            LOGGER.warn("Xaeros integration not available for map focusing");
        }
    }
    
    /**
     * Focus Xaeros map on specific coordinates
     */
    public void focusMapOnPosition(net.minecraft.util.math.BlockPos position) {
        if (xaerosIntegration != null && xaerosIntegration.isAvailable()) {
            xaerosIntegration.focusOnPosition(position).thenAccept(success -> {
                if (success) {
                    LOGGER.info("Focused map on position: {}", position);
                } else {
                    LOGGER.warn("Failed to focus map on position: {}", position);
                }
            });
        } else {
            LOGGER.warn("Xaeros integration not available for map focusing");
        }
    }
    
    /**
     * Add a task area waypoint to Xaeros map
     */
    public void addTaskAreaToMap(String taskId, String taskName, net.minecraft.util.math.BlockPos center, int radius) {
        if (xaerosIntegration != null && xaerosIntegration.isAvailable()) {
            int color = 0xFF8000; // Orange for task areas
            xaerosIntegration.addTaskAreaWaypoint(taskId, taskName, center, radius, color);
            LOGGER.info("Added task area waypoint: {} at {}", taskName, center);
        }
    }
    
    /**
     * Remove a task area waypoint from Xaeros map
     */
    public void removeTaskAreaFromMap(String taskId) {
        if (xaerosIntegration != null && xaerosIntegration.isAvailable()) {
            xaerosIntegration.removeTaskAreaWaypoint(taskId);
            LOGGER.info("Removed task area waypoint: {}", taskId);
        }
    }
    
    /**
     * Update tracked accounts for Xaeros integration
     */
    public void updateTrackedAccounts(List<ProxyAccount> accounts) {
        if (xaerosIntegration != null && xaerosIntegration.isAvailable()) {
            xaerosIntegration.updateTrackedAccounts(accounts);
            LOGGER.debug("Updated {} tracked accounts for Xaeros integration", accounts.size());
        }
    }
    
    /**
     * Send a command to a specific cluster
     */
    public void sendClusterCommand(String clusterId, String command, Map<String, Object> parameters) {
        LOGGER.info("Sending command '{}' to cluster {}", command, clusterId);
        
        networkClient.sendClusterCommand(clusterId, command, parameters)
            .thenAccept(success -> {
                if (success) {
                    LOGGER.info("Sent command '{}' to cluster {} successfully", command, clusterId);
                } else {
                    LOGGER.error("Failed to send command '{}' to cluster {}", command, clusterId);
                }
            });
    }
    
    /**
     * Start a task for a specific cluster
     */
    public void startClusterTask(String clusterId, String taskType, Map<String, Object> parameters) {
        LOGGER.info("Starting task '{}' for cluster {}", taskType, clusterId);
        
        networkClient.startClusterTask(clusterId, taskType, parameters)
            .thenAccept(taskId -> {
                if (taskId != null) {
                    LOGGER.info("Started task '{}' for cluster {} (Task ID: {})", taskType, clusterId, taskId);
                    
                    // Update local cluster task status if we have it cached
                    Cluster cluster = clusterGroups.get(clusterId);
                    if (cluster != null) {
                        cluster.setCurrentTask(taskType);
                    }
                    
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        client.player.sendMessage(
                            Text.of(String.format("§a[AppyProx] Started task '%s' for cluster", taskType))
                        );
                    }
                } else {
                    LOGGER.error("Failed to start task '{}' for cluster {}", taskType, clusterId);
                    
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        client.player.sendMessage(
                            Text.of(String.format("§c[AppyProx] Failed to start task '%s'", taskType))
                        );
                    }
                }
            });
    }
    
    // Getters
    public boolean isProxyModeEnabled() {
        return proxyModeEnabled;
    }
    
    public boolean isDirectControlMode() {
        return directControlMode;
    }
    
    public String getControlledAccountId() {
        return controlledAccountId;
    }
    
    public String getActiveCluster() {
        return activeCluster;
    }
    
    public Collection<Cluster> getClusterGroups() {
        return clusterGroups.values();
    }
    
    public boolean isConnected() {
        return connected && networkClient.isConnected();
    }
    
    public AppyProxNetworkClient getNetworkClient() {
        return networkClient;
    }
    
    public XaerosIntegration getXaerosIntegration() {
        return xaerosIntegration;
    }
    
    public ProxyModeManager getProxyModeManager() {
        return proxyModeManager;
    }
    
    public DirectAccountController getDirectAccountController() {
        return directAccountController;
    }
    
    public boolean isProxyMode() {
        return proxyModeManager.isProxyModeEnabled();
    }
    
    public void shutdown() {
        LOGGER.info("Shutting down AppyProx Manager");
        
        // Disable proxy mode if enabled
        if (isProxyMode()) {
            proxyModeManager.disableProxyMode();
        }
        
        // Disconnect from backend
        if (networkClient != null) {
            networkClient.disconnect();
        }
        
        // Shutdown Xaeros integration
        if (xaerosIntegration != null) {
            xaerosIntegration.shutdown();
        }
        
        // Shutdown direct account controller
        if (directAccountController != null) {
            directAccountController.shutdown();
        }
        
        clusterGroups.clear();
        proxyModeEnabled = false;
        directControlMode = false;
        connected = false;
    }
}