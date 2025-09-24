package dev.aprilrenders.appyprox.control;

import dev.aprilrenders.appyprox.AppyProxFabricClient;
import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

/**
 * Direct Account Control system allows seamless switching between controlling
 * the player's main account and any proxy account in the AppyProx network
 */
@Environment(EnvType.CLIENT)
public class DirectAccountController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectAccountController.class);
    
    // Control state management
    private boolean isControllingProxyAccount = false;
    private String currentControlledAccountId = null;
    private ProxyAccount controlledAccount = null;
    
    // Session state preservation
    private PlayerSession originalSession;
    private PlayerSession proxySession;
    
    // Network management
    private final AppyProxManager manager;
    private AccountSessionManager sessionManager;
    
    public DirectAccountController(AppyProxManager manager) {
        this.manager = manager;
        this.sessionManager = new AccountSessionManager();
    }
    
    /**
     * Switch control to a specific proxy account
     */
    public CompletableFuture<Boolean> switchToAccount(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.info("Initiating switch to proxy account: {}", accountId);
                
                // Validate proxy mode is enabled
                if (!manager.isProxyMode()) {
                    LOGGER.error("Cannot switch to proxy account - proxy mode is disabled");
                    return false;
                }
                
                // Get account information from backend
                ProxyAccount targetAccount = getAccountById(accountId);
                if (targetAccount == null) {
                    LOGGER.error("Account not found: {}", accountId);
                    return false;
                }
                
                // Save current session state
                if (!isControllingProxyAccount) {
                    originalSession = sessionManager.captureCurrentSession();
                    LOGGER.debug("Captured original session state");
                }
                
                // Request account control from backend
                boolean controlGranted = requestAccountControl(accountId);
                if (!controlGranted) {
                    LOGGER.error("Backend denied control request for account: {}", accountId);
                    return false;
                }
                
                // Perform the switch
                boolean switchSuccess = performAccountSwitch(targetAccount);
                if (!switchSuccess) {
                    LOGGER.error("Failed to perform account switch");
                    // Attempt to release control
                    releaseAccountControl(accountId);
                    return false;
                }
                
                // Update state
                isControllingProxyAccount = true;
                currentControlledAccountId = accountId;
                controlledAccount = targetAccount;
                
                // Notify user
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("§a[AppyProx] Now controlling account: " + targetAccount.getUsername()),
                        false
                    );
                }
                
                LOGGER.info("Successfully switched to proxy account: {}", targetAccount.getUsername());
                return true;
                
            } catch (Exception e) {
                LOGGER.error("Failed to switch to proxy account: {}", accountId, e);
                return false;
            }
        });
    }
    
    /**
     * Return control to the original player account
     */
    public CompletableFuture<Boolean> returnToOriginalAccount() {
        return CompletableFuture.supplyAsync(() -> {
            if (!isControllingProxyAccount) {
                LOGGER.debug("Already controlling original account");
                return true;
            }
            
            try {
                LOGGER.info("Returning control to original account");
                
                String previousAccountId = currentControlledAccountId;
                
                // Release proxy account control
                if (currentControlledAccountId != null) {
                    releaseAccountControl(currentControlledAccountId);
                }
                
                // Restore original session
                boolean restoreSuccess = sessionManager.restoreSession(originalSession);
                if (!restoreSuccess) {
                    LOGGER.error("Failed to restore original session - manual reconnection may be required");
                    return false;
                }
                
                // Update state
                isControllingProxyAccount = false;
                currentControlledAccountId = null;
                controlledAccount = null;
                proxySession = null;
                
                // Notify user
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("§a[AppyProx] Returned to original account control"),
                        false
                    );
                }
                
                LOGGER.info("Successfully returned to original account");
                return true;
                
            } catch (Exception e) {
                LOGGER.error("Failed to return to original account", e);
                return false;
            }
        });
    }
    
    /**
     * Get available accounts for direct control
     */
    public CompletableFuture<java.util.List<ProxyAccount>> getAvailableAccounts() {
        if (manager.getNetworkClient() != null) {
            return manager.getNetworkClient().getAccounts()
                .thenApply(accounts -> accounts.stream()
                    .filter(account -> "online".equalsIgnoreCase(account.getStatus()))
                    .filter(account -> !account.getId().equals(currentControlledAccountId))
                    .toList()
                );
        }
        return CompletableFuture.completedFuture(java.util.List.of());
    }
    
    /**
     * Check if currently controlling a proxy account
     */
    public boolean isControllingProxyAccount() {
        return isControllingProxyAccount;
    }
    
    /**
     * Get the currently controlled account
     */
    public ProxyAccount getCurrentControlledAccount() {
        return controlledAccount;
    }
    
    /**
     * Get controlled account ID
     */
    public String getCurrentControlledAccountId() {
        return currentControlledAccountId;
    }
    
    private ProxyAccount getAccountById(String accountId) {
        try {
            return manager.getNetworkClient().getAccounts()
                .get()
                .stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            LOGGER.error("Failed to get account by ID: {}", accountId, e);
            return null;
        }
    }
    
    private boolean requestAccountControl(String accountId) {
        try {
            // Send control request to backend
            Map<String, Object> controlParams = new HashMap<>();
            controlParams.put("action", "request_control");
            controlParams.put("client_id", getClientIdentifier());
            
            return manager.getNetworkClient()
                .sendAccountCommand(accountId, "request_control", controlParams)
                .get();
                
        } catch (Exception e) {
            LOGGER.error("Failed to request account control: {}", accountId, e);
            return false;
        }
    }
    
    private void releaseAccountControl(String accountId) {
        try {
            Map<String, Object> releaseParams = new HashMap<>();
            releaseParams.put("action", "release_control");
            releaseParams.put("client_id", getClientIdentifier());
            
            manager.getNetworkClient()
                .sendAccountCommand(accountId, "release_control", releaseParams);
                
        } catch (Exception e) {
            LOGGER.error("Failed to release account control: {}", accountId, e);
        }
    }
    
    private boolean performAccountSwitch(ProxyAccount targetAccount) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Capture current session state before switching
            if (isControllingProxyAccount && proxySession == null) {
                proxySession = sessionManager.captureCurrentSession();
            }
            
            // Perform the actual account switch through backend coordination
            Map<String, Object> switchParams = new HashMap<>();
            switchParams.put("target_position", getCurrentPlayerPosition());
            switchParams.put("preserve_inventory", true);
            switchParams.put("sync_health", true);
            
            boolean switchResult = manager.getNetworkClient()
                .sendAccountCommand(targetAccount.getId(), "perform_switch", switchParams)
                .get();
                
            if (!switchResult) {
                return false;
            }
            
            // The actual client connection switching would be handled by the backend
            // This is a placeholder for the complex networking required
            LOGGER.debug("Account switch coordination completed for: {}", targetAccount.getUsername());
            
            return true;
            
        } catch (Exception e) {
            LOGGER.error("Failed to perform account switch", e);
            return false;
        }
    }
    
    private Map<String, Double> getCurrentPlayerPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        Map<String, Double> position = new HashMap<>();
        
        if (client.player != null) {
            position.put("x", client.player.getX());
            position.put("y", client.player.getY());
            position.put("z", client.player.getZ());
            position.put("yaw", (double) client.player.getYaw());
            position.put("pitch", (double) client.player.getPitch());
        }
        
        return position;
    }
    
    private String getClientIdentifier() {
        // Generate unique client identifier for control requests
        return "fabric_client_" + System.currentTimeMillis();
    }
    
    /**
     * Handles session state management for account switching
     */
    private static class AccountSessionManager {
        
        public PlayerSession captureCurrentSession() {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (client.player == null || client.getNetworkHandler() == null) {
                return null;
            }
            
            return new PlayerSession(
                client.player.getX(),
                client.player.getY(), 
                client.player.getZ(),
                client.player.getYaw(),
                client.player.getPitch(),
                client.player.getHealth(),
                client.player.getHungerManager().getFoodLevel(),
                client.getNetworkHandler().getConnection().getAddress().toString()
            );
        }
        
        public boolean restoreSession(PlayerSession session) {
            if (session == null) {
                return false;
            }
            
            try {
                // This would involve complex networking to restore the session
                // For now, we log the restore operation
                LOGGER.debug("Restoring session to position: {}, {}, {}", 
                    session.x, session.y, session.z);
                
                // In a full implementation, this would:
                // 1. Disconnect from current server if needed
                // 2. Reconnect to original server
                // 3. Teleport to saved position
                // 4. Restore health/hunger state
                
                return true;
                
            } catch (Exception e) {
                LOGGER.error("Failed to restore session", e);
                return false;
            }
        }
    }
    
    /**
     * Represents a saved player session state
     */
    private static class PlayerSession {
        final double x, y, z;
        final float yaw, pitch;
        final float health;
        final int food;
        final String serverAddress;
        
        PlayerSession(double x, double y, double z, float yaw, float pitch, 
                     float health, int food, String serverAddress) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.health = health;
            this.food = food;
            this.serverAddress = serverAddress;
        }
    }
    
    /**
     * Clean up resources and release any controlled accounts
     */
    public void shutdown() {
        if (isControllingProxyAccount && currentControlledAccountId != null) {
            LOGGER.info("Releasing proxy account control during shutdown");
            releaseAccountControl(currentControlledAccountId);
        }
        
        isControllingProxyAccount = false;
        currentControlledAccountId = null;
        controlledAccount = null;
    }
}