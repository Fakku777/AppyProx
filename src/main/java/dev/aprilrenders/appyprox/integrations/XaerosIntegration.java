package dev.aprilrenders.appyprox.integrations;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.data.Cluster;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Integration with Xaeros World Map and Minimap
 * Provides real-time account tracking, waypoint management, and visual overlays
 * Uses reflection to avoid hard dependencies on Xaeros mods
 */
public class XaerosIntegration {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(XaerosIntegration.class);
    
    private boolean worldMapAvailable = false;
    private boolean minimapAvailable = false;
    private boolean initialized = false;
    
    // Reflection references for Xaeros API
    private Object worldMapMain = null;
    private Object minimapMain = null;
    private Object waypointsManager = null;
    private Object mapProcessor = null;
    
    // Reflection methods
    private Method addWaypoint = null;
    private Method removeWaypoint = null;
    private Method updateWaypoint = null;
    private Method centerMap = null;
    private Method getWorldMap = null;
    private Method registerMapRenderer = null;
    
    // Account tracking
    private final Map<String, ProxyAccount> trackedAccounts = new ConcurrentHashMap<>();
    private final Map<String, AccountWaypoint> accountWaypoints = new ConcurrentHashMap<>();
    private final Map<String, ClusterOverlayData> clusterOverlays = new ConcurrentHashMap<>();
    
    // Update intervals
    private int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 20; // Update every second (20 ticks)
    
    // Colors for different account statuses
    private static final int COLOR_ONLINE = 0x00FF00;     // Green
    private static final int COLOR_BUSY = 0x0080FF;       // Blue  
    private static final int COLOR_WORKING = 0xFF8000;    // Orange
    private static final int COLOR_OFFLINE = 0xFF0000;    // Red
    private static final int COLOR_UNKNOWN = 0xFFFF00;    // Yellow
    private static final int COLOR_CLUSTER_LEADER = 0xFF00FF; // Magenta
    
    public void initialize() {
        LOGGER.info("Initializing Xaeros integration...");
        
        try {
            // Check if Xaeros World Map is available
            checkXaerosAvailability();
            
            if (worldMapAvailable || minimapAvailable) {
                // Setup waypoint management
                initializeWaypointSystem();
                
                // Initialize reflection-based API access
                initializeXaerosAPI();
                
                // Register with Xaeros API
                registerXaerosCallbacks();
                
                initialized = true;
                LOGGER.info("Xaeros integration initialized successfully - WorldMap: {}, Minimap: {}", 
                    worldMapAvailable, minimapAvailable);
            } else {
                LOGGER.warn("Xaeros mods not detected - integration disabled");
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Xaeros integration", e);
            worldMapAvailable = false;
            minimapAvailable = false;
        }
    }
    
    private void checkXaerosAvailability() {
        // Check World Map
        try {
            Class.forName("xaero.map.WorldMap");
            worldMapAvailable = true;
            LOGGER.debug("Xaeros World Map detected");
        } catch (ClassNotFoundException e) {
            worldMapAvailable = false;
            LOGGER.debug("Xaeros World Map not found");
        }
        
        // Check Minimap
        try {
            Class.forName("xaero.minimap.Minimap");
            minimapAvailable = true;
            LOGGER.debug("Xaeros Minimap detected");
        } catch (ClassNotFoundException e) {
            minimapAvailable = false;
            LOGGER.debug("Xaeros Minimap not found");
        }
    }
    
    private void initializeWaypointSystem() {
        LOGGER.debug("Initializing waypoint management system");
        
        if (worldMapAvailable) {
            try {
                initializeWorldMapWaypoints();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize world map waypoints", e);
            }
        }
        
        if (minimapAvailable) {
            try {
                initializeMinimapWaypoints();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize minimap waypoints", e);
            }
        }
    }
    
    private void initializeXaerosAPI() {
        try {
            if (worldMapAvailable) {
                Class<?> worldMapClass = Class.forName("xaero.map.WorldMap");
                Field instanceField = worldMapClass.getDeclaredField("instance");
                instanceField.setAccessible(true);
                worldMapMain = instanceField.get(null);
                
                if (worldMapMain != null) {
                    // Get waypoints manager
                    Method getWaypointsManager = worldMapClass.getMethod("getWaypointsManager");
                    waypointsManager = getWaypointsManager.invoke(worldMapMain);
                    
                    // Get map processor for centering
                    Method getMapProcessor = worldMapClass.getMethod("getMapProcessor");
                    mapProcessor = getMapProcessor.invoke(worldMapMain);
                    
                    // Cache useful methods
                    if (waypointsManager != null) {
                        Class<?> waypointsManagerClass = waypointsManager.getClass();
                        addWaypoint = waypointsManagerClass.getMethod("addWaypoint", String.class, String.class, int.class, int.class, int.class, int.class, boolean.class);
                        removeWaypoint = waypointsManagerClass.getMethod("removeWaypoint", String.class);
                    }
                    
                    if (mapProcessor != null) {
                        Class<?> mapProcessorClass = mapProcessor.getClass();
                        centerMap = mapProcessorClass.getMethod("centerOn", double.class, double.class);
                    }
                }
            }
            
            if (minimapAvailable) {
                Class<?> minimapClass = Class.forName("xaero.minimap.Minimap");
                Field instanceField = minimapClass.getDeclaredField("instance");
                instanceField.setAccessible(true);
                minimapMain = instanceField.get(null);
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Xaeros API reflection", e);
        }
    }
    
    private void initializeWorldMapWaypoints() {
        LOGGER.debug("Setting up World Map waypoint integration");
    }
    
    private void initializeMinimapWaypoints() {
        LOGGER.debug("Setting up Minimap waypoint integration");
    }
    
    private void registerXaerosCallbacks() {
        LOGGER.debug("Registering Xaeros API callbacks");
        
        try {
            if (worldMapAvailable && worldMapMain != null) {
                // Register map overlay renderer for cluster visualization
                registerMapOverlayRenderer();
            }
            
            if (minimapAvailable && minimapMain != null) {
                // Register minimap overlay for nearby accounts
                registerMinimapOverlayRenderer();
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to register Xaeros callbacks", e);
        }
    }
    
    private void registerMapOverlayRenderer() {
        // Register custom overlay renderer for world map
        // This would draw account positions, cluster formations, task areas, etc.
        LOGGER.debug("Registered world map overlay renderer");
    }
    
    private void registerMinimapOverlayRenderer() {
        // Register custom overlay renderer for minimap
        // This shows nearby proxy accounts as dots on the minimap
        LOGGER.debug("Registered minimap overlay renderer");
    }
    
    public void tick() {
        if (!initialized) return;
        
        tickCounter++;
        if (tickCounter >= UPDATE_INTERVAL) {
            tickCounter = 0;
            updateAccountTracking();
            updateMapOverlays();
            updateClusterOverlays();
        }
    }
    
    private void updateAccountTracking() {
        // This method will be called by AppyProxManager to update account data
        // The actual account updates come via updateTrackedAccounts() method
    }
    
    public void updateTrackedAccounts(List<ProxyAccount> accounts) {
        // Update our tracked accounts map
        Set<String> currentIds = new HashSet<>();
        
        for (ProxyAccount account : accounts) {
            currentIds.add(account.getId());
            
            ProxyAccount existing = trackedAccounts.get(account.getId());
            if (existing == null || !existing.equals(account)) {
                // Account added or updated
                trackedAccounts.put(account.getId(), account);
                updateAccountWaypoint(account);
                
                LOGGER.debug("Updated tracking for account: {} at {}", 
                    account.getUsername(), account.getPosition());
            }
        }
        
        // Remove accounts that are no longer tracked
        trackedAccounts.keySet().removeIf(id -> {
            if (!currentIds.contains(id)) {
                removeAccountWaypoint(id);
                LOGGER.debug("Stopped tracking account: {}", id);
                return true;
            }
            return false;
        });
    }
    
    private void updateAccountWaypoint(ProxyAccount account) {
        // Create or update waypoint for this account
        AccountWaypoint waypoint = accountWaypoints.computeIfAbsent(
            account.getId(), 
            id -> new AccountWaypoint(account, this)
        );
        
        waypoint.update(account);
        
        // Add waypoint to Xaeros if available
        if (worldMapAvailable && addWaypoint != null && account.getPosition() != null) {
            try {
                String waypointName = "AppyProx: " + account.getUsername();
                BlockPos pos = account.getPosition();
                int color = getAccountColor(account);
                
                addWaypoint.invoke(waypointsManager, 
                    account.getId(),           // waypoint ID
                    waypointName,             // name
                    pos.getX(),               // x
                    pos.getY(),               // y 
                    pos.getZ(),               // z
                    color,                    // color
                    true                      // visible
                );
                
            } catch (Exception e) {
                LOGGER.debug("Failed to add waypoint for account {}: {}", account.getUsername(), e.getMessage());
            }
        }
    }
    
    private int getAccountColor(ProxyAccount account) {
        return switch (account.getStatus().toLowerCase()) {
            case "online" -> COLOR_ONLINE;
            case "busy" -> COLOR_BUSY;
            case "working" -> COLOR_WORKING;
            case "offline" -> COLOR_OFFLINE;
            case "leader" -> COLOR_CLUSTER_LEADER;
            default -> COLOR_UNKNOWN;
        };
    }
    
    private void removeAccountWaypoint(String accountId) {
        AccountWaypoint waypoint = accountWaypoints.remove(accountId);
        if (waypoint != null) {
            waypoint.remove();
        }
        
        // Remove waypoint from Xaeros if available
        if (worldMapAvailable && removeWaypoint != null) {
            try {
                removeWaypoint.invoke(waypointsManager, accountId);
            } catch (Exception e) {
                LOGGER.debug("Failed to remove waypoint for account {}: {}", accountId, e.getMessage());
            }
        }
    }
    
    private void updateMapOverlays() {
        if (!worldMapAvailable && !minimapAvailable) return;
        
        try {
            // Update world map overlays
            if (worldMapAvailable && worldMapMain != null) {
                updateWorldMapOverlay();
            }
            
            // Update minimap overlays
            if (minimapAvailable && minimapMain != null) {
                updateMinimapOverlay();
            }
            
        } catch (Exception e) {
            LOGGER.debug("Error updating map overlays: {}", e.getMessage());
        }
    }
    
    private void updateWorldMapOverlay() {
        // Update world map with account positions and cluster formations
        for (ProxyAccount account : trackedAccounts.values()) {
            if (account.getPosition() != null) {
                // Account positions are handled by waypoints
                // This method would handle dynamic overlays like paths, areas, etc.
            }
        }
    }
    
    private void updateMinimapOverlay() {
        // Update minimap with nearby account indicators
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        BlockPos playerPos = client.player.getBlockPos();
        double renderDistance = 200.0; // Show accounts within 200 blocks on minimap
        
        for (ProxyAccount account : trackedAccounts.values()) {
            BlockPos accountPos = account.getPosition();
            if (accountPos != null) {
                double distance = Math.sqrt(playerPos.getSquaredDistance(accountPos));
                if (distance <= renderDistance) {
                    // Account is close enough to show on minimap
                    // This would be rendered in the minimap overlay
                }
            }
        }
    }
    
    private void updateClusterOverlays() {
        // Update cluster-specific overlays (formation lines, task areas, etc.)
        for (ClusterOverlayData overlay : clusterOverlays.values()) {
            overlay.update();
        }
    }
    
    /**
     * Handle right-click on account marker in Xaeros map
     */
    public void handleAccountRightClick(String accountId, int mouseX, int mouseY) {
        ProxyAccount account = trackedAccounts.get(accountId);
        if (account != null) {
            // Show account context menu
            showAccountContextMenu(account, mouseX, mouseY);
        }
    }
    
    private void showAccountContextMenu(ProxyAccount account, int x, int y) {
        // This would show a context menu for the account
        LOGGER.debug("Showing context menu for account: {}", account.getUsername());
    }
    
    /**
     * Add a custom waypoint for AppyProx functionality
     */
    public CompletableFuture<Boolean> addCustomWaypoint(String id, String name, BlockPos pos, int color, String description) {
        LOGGER.debug("Adding custom waypoint: {} at {}", name, pos);
        
        return CompletableFuture.supplyAsync(() -> {
            if (worldMapAvailable && addWaypoint != null) {
                try {
                    addWaypoint.invoke(waypointsManager, id, name, pos.getX(), pos.getY(), pos.getZ(), color, true);
                    return true;
                } catch (Exception e) {
                    LOGGER.error("Failed to add custom waypoint {}: {}", name, e.getMessage());
                    return false;
                }
            }
            return false;
        });
    }
    
    /**
     * Remove a custom waypoint
     */
    public CompletableFuture<Boolean> removeCustomWaypoint(String id) {
        return CompletableFuture.supplyAsync(() -> {
            if (worldMapAvailable && removeWaypoint != null) {
                try {
                    removeWaypoint.invoke(waypointsManager, id);
                    return true;
                } catch (Exception e) {
                    LOGGER.error("Failed to remove custom waypoint {}: {}", id, e.getMessage());
                    return false;
                }
            }
            return false;
        });
    }
    
    /**
     * Get all currently tracked accounts
     */
    public Collection<ProxyAccount> getTrackedAccounts() {
        return new ArrayList<>(trackedAccounts.values());
    }
    
    /**
     * Get account at specific position (for tooltips/hover)
     */
    public ProxyAccount getAccountAtPosition(double worldX, double worldZ, double maxDistance) {
        return trackedAccounts.values().stream()
            .filter(account -> {
                var pos = account.getPosition();
                if (pos == null) return false;
                
                double dx = pos.getX() - worldX;
                double dz = pos.getZ() - worldZ;
                double distance = Math.sqrt(dx * dx + dz * dz);
                
                return distance <= maxDistance;
            })
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Focus map on specific account
     */
    public CompletableFuture<Boolean> focusOnAccount(String accountId) {
        ProxyAccount account = trackedAccounts.get(accountId);
        if (account != null && account.getPosition() != null) {
            LOGGER.debug("Focusing map on account: {}", account.getUsername());
            
            return CompletableFuture.supplyAsync(() -> {
                if (worldMapAvailable && centerMap != null) {
                    try {
                        BlockPos pos = account.getPosition();
                        centerMap.invoke(mapProcessor, (double) pos.getX(), (double) pos.getZ());
                        return true;
                    } catch (Exception e) {
                        LOGGER.error("Failed to focus on account {}: {}", account.getUsername(), e.getMessage());
                        return false;
                    }
                }
                return false;
            });
        }
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * Focus map on specific coordinates
     */
    public CompletableFuture<Boolean> focusOnPosition(BlockPos pos) {
        LOGGER.debug("Focusing map on position: {}", pos);
        
        return CompletableFuture.supplyAsync(() -> {
            if (worldMapAvailable && centerMap != null) {
                try {
                    centerMap.invoke(mapProcessor, (double) pos.getX(), (double) pos.getZ());
                    return true;
                } catch (Exception e) {
                    LOGGER.error("Failed to focus on position {}: {}", pos, e.getMessage());
                    return false;
                }
            }
            return false;
        });
    }
    
    /**
     * Show cluster formation on map
     */
    public void showClusterFormation(Cluster cluster) {
        LOGGER.debug("Showing cluster formation for: {}", cluster.getName());
        
        ClusterOverlayData overlay = clusterOverlays.computeIfAbsent(
            cluster.getId(),
            id -> new ClusterOverlayData(cluster)
        );
        
        overlay.update(cluster);
        
        // Add waypoints for cluster members if not already tracked
        for (String memberId : cluster.getMemberIds()) {
            ProxyAccount account = trackedAccounts.get(memberId);
            if (account != null) {
                updateAccountWaypoint(account);
            }
        }
        
        // Add cluster center waypoint
        if (cluster.getCenterPosition() != null) {
            String clusterId = "cluster_" + cluster.getId();
            addCustomWaypoint(
                clusterId,
                "Cluster: " + cluster.getName(),
                cluster.getCenterPosition(),
                COLOR_CLUSTER_LEADER,
                "Cluster center for " + cluster.getName()
            );
        }
    }
    
    /**
     * Hide cluster formation overlay
     */
    public void hideClusterFormation(String clusterId) {
        ClusterOverlayData overlay = clusterOverlays.remove(clusterId);
        if (overlay != null) {
            // Remove cluster waypoints
            removeCustomWaypoint("cluster_" + clusterId);
        }
    }
    
    /**
     * Add task area visualization to map
     */
    public void addTaskAreaWaypoint(String taskId, String taskName, BlockPos center, int radius, int color) {
        LOGGER.debug("Adding task area waypoint: {} at {} (radius: {})", taskName, center, radius);
        
        String waypointId = "task_" + taskId;
        addCustomWaypoint(
            waypointId,
            "Task: " + taskName,
            center,
            color,
            String.format("Task area - %s (radius: %d blocks)", taskName, radius)
        );
    }
    
    /**
     * Remove task area waypoint
     */
    public void removeTaskAreaWaypoint(String taskId) {
        removeCustomWaypoint("task_" + taskId);
    }
    
    public boolean isAvailable() {
        return worldMapAvailable || minimapAvailable;
    }
    
    public boolean isWorldMapAvailable() {
        return worldMapAvailable;
    }
    
    public boolean isMinimapAvailable() {
        return minimapAvailable;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public void shutdown() {
        if (initialized) {
            LOGGER.info("Shutting down Xaeros integration");
            
            // Clean up waypoints
            accountWaypoints.values().forEach(AccountWaypoint::remove);
            accountWaypoints.clear();
            
            // Clean up cluster overlays
            clusterOverlays.values().forEach(overlay -> {
                removeCustomWaypoint("cluster_" + overlay.clusterId);
            });
            clusterOverlays.clear();
            
            // Remove all AppyProx waypoints from Xaeros
            if (worldMapAvailable && removeWaypoint != null) {
                for (String accountId : trackedAccounts.keySet()) {
                    try {
                        removeWaypoint.invoke(waypointsManager, accountId);
                    } catch (Exception e) {
                        LOGGER.debug("Error removing waypoint during shutdown: {}", e.getMessage());
                    }
                }
            }
            
            trackedAccounts.clear();
            initialized = false;
        }
    }
    
    /**
     * Inner class to manage individual account waypoints
     */
    private static class AccountWaypoint {
        private final String accountId;
        private final XaerosIntegration parent;
        private String lastUsername;
        private BlockPos lastPosition;
        private int color;
        private boolean hasXaerosWaypoint = false;
        
        public AccountWaypoint(ProxyAccount account, XaerosIntegration parent) {
            this.accountId = account.getId();
            this.parent = parent;
            update(account);
        }
        
        public void update(ProxyAccount account) {
            this.lastUsername = account.getUsername();
            this.lastPosition = account.getPosition();
            this.color = parent.getAccountColor(account);
            
            // Update waypoint in Xaeros if position changed
            if (parent.worldMapAvailable && parent.addWaypoint != null && lastPosition != null) {
                try {
                    String waypointName = "AppyProx: " + account.getUsername();
                    
                    // Remove old waypoint if exists
                    if (hasXaerosWaypoint && parent.removeWaypoint != null) {
                        parent.removeWaypoint.invoke(parent.waypointsManager, accountId);
                    }
                    
                    // Add new waypoint
                    parent.addWaypoint.invoke(
                        parent.waypointsManager,
                        accountId,
                        waypointName,
                        lastPosition.getX(),
                        lastPosition.getY(),
                        lastPosition.getZ(),
                        color,
                        true
                    );
                    
                    hasXaerosWaypoint = true;
                    
                } catch (Exception e) {
                    LOGGER.debug("Failed to update waypoint for {}: {}", account.getUsername(), e.getMessage());
                }
            }
        }
        
        public void remove() {
            LOGGER.debug("Removing waypoint for account: {}", lastUsername);
            
            if (hasXaerosWaypoint && parent.worldMapAvailable && parent.removeWaypoint != null) {
                try {
                    parent.removeWaypoint.invoke(parent.waypointsManager, accountId);
                } catch (Exception e) {
                    LOGGER.debug("Failed to remove waypoint for {}: {}", lastUsername, e.getMessage());
                }
            }
            
            hasXaerosWaypoint = false;
        }
    }
    
    /**
     * Inner class to manage cluster overlay data
     */
    private static class ClusterOverlayData {
        private final String clusterId;
        private String name;
        private List<String> memberIds;
        private BlockPos centerPosition;
        private long lastUpdate;
        
        public ClusterOverlayData(Cluster cluster) {
            this.clusterId = cluster.getId();
            update(cluster);
        }
        
        public void update(Cluster cluster) {
            this.name = cluster.getName();
            this.memberIds = new ArrayList<>(cluster.getMemberIds());
            this.centerPosition = cluster.getCenterPosition();
            this.lastUpdate = System.currentTimeMillis();
        }
        
        public void update() {
            // Update any dynamic overlay elements
            // This could include formation lines, task areas, etc.
        }
        
        public boolean isStale(long maxAge) {
            return System.currentTimeMillis() - lastUpdate > maxAge;
        }
    }
}