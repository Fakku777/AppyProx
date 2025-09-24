package dev.aprilrenders.appyprox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.integrations.XaerosIntegration;
import dev.aprilrenders.appyprox.proxy.AutoDeployManager;
import dev.aprilrenders.appyprox.ui.AppyProxManagerScreenNew;
import dev.aprilrenders.appyprox.ui.MenuIntegration;
import dev.aprilrenders.appyprox.ui.ClusterOverlay;
import dev.aprilrenders.appyprox.ui.QuickCommandScreen;
import dev.aprilrenders.appyprox.ui.AccountSelectionScreen;
import dev.aprilrenders.appyprox.ui.XaerosMapScreen;
import dev.aprilrenders.appyprox.network.AppyProxNetworkClient;

/**
 * Main client-side entry point for AppyProx Fabric mod
 * Integrates with Xaeros World Map and provides proxy management capabilities
 */
public class AppyProxFabricClient implements ClientModInitializer {
    
    public static final String MOD_ID = "appyprox";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Core managers
    private static AppyProxManager manager;
    private static AutoDeployManager autoDeployManager;
    
    // Keybindings
    private static KeyBinding openAppyProxKey;
    private static KeyBinding toggleProxyModeKey;
    private static KeyBinding quickClusterCommandKey;
    private static KeyBinding toggleOverlayKey;
    private static KeyBinding accountControlKey;
    private static KeyBinding xaerosMapKey;
    private static KeyBinding autoDeployKey;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing AppyProx Fabric Client...");
        
        try {
            // Initialize auto-deploy manager first
            initializeAutoDeployManager();
            
            // Initialize core systems
            initializeCore();
            
            // Setup keybindings
            setupKeybindings();
            
            // Initialize integrations
            initializeIntegrations();
            
            // Register event handlers
            registerEventHandlers();
            
            // Initialize menu integration
            MenuIntegration.initialize();
            
            // Initialize cluster overlay
            ClusterOverlay.initialize();
            
            // Start auto-deployment
            startAutoDeployment();
            
            LOGGER.info("AppyProx Fabric Client initialized successfully!");
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize AppyProx Fabric Client", e);
        }
    }
    
    private void initializeCore() {
        LOGGER.debug("Initializing core AppyProx systems...");
        
        // Initialize the main AppyProx manager
        manager = new AppyProxManager();
        
        LOGGER.debug("Core systems initialized");
    }
    
    private void setupKeybindings() {
        LOGGER.debug("Setting up keybindings...");
        
        // Main AppyProx interface key (default: K)
        openAppyProxKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.open_interface",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.appyprox.general"
        ));
        
        // Toggle proxy mode (default: P)
        toggleProxyModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.toggle_proxy_mode",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.appyprox.general"
        ));
        
        // Quick cluster command (default: U)
        quickClusterCommandKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.quick_cluster_command",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "category.appyprox.general"
        ));
        
        // Toggle overlay (default: H)
        toggleOverlayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.toggle_overlay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.appyprox.general"
        ));
        
        // Account control (default: J)
        accountControlKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.account_control",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.appyprox.general"
        ));
        
        // Xaeros Map integration (default: M)
        xaerosMapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.xaeros_map",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.appyprox.general"
        ));
        
        // Auto-deploy control (default: L)
        autoDeployKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.appyprox.auto_deploy",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "category.appyprox.general"
        ));
        
        LOGGER.debug("Keybindings registered");
    }
    
    private void initializeIntegrations() {
        LOGGER.debug("Initializing mod integrations...");
        
        // Integrations are handled by the manager
        LOGGER.debug("Integrations initialized");
    }
    
    private void registerEventHandlers() {
        LOGGER.debug("Registering event handlers...");
        
        // Client tick events for keybinding handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleKeybindings();
            
            // Update AppyProx systems
            if (manager != null) {
                manager.tick();
            }
        });
        
        LOGGER.debug("Event handlers registered");
    }
    
    private void handleKeybindings() {
        // Handle AppyProx interface opening
        while (openAppyProxKey.wasPressed()) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            client.setScreen(new AppyProxManagerScreenNew(client.currentScreen));
        }
        
        // Handle proxy mode toggle
        while (toggleProxyModeKey.wasPressed()) {
            if (manager != null) {
                manager.toggleProxyMode();
            }
        }
        
        // Handle quick cluster commands
        while (quickClusterCommandKey.wasPressed()) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            client.setScreen(new QuickCommandScreen(client.currentScreen));
        }
        
        // Handle overlay toggle
        while (toggleOverlayKey.wasPressed()) {
            ClusterOverlay.toggleEnabled();
        }
        
        // Handle account control
        while (accountControlKey.wasPressed()) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            client.setScreen(new AccountSelectionScreen(client.currentScreen));
        }
        
        // Handle Xaeros Map integration
        while (xaerosMapKey.wasPressed()) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            client.setScreen(new XaerosMapScreen(client.currentScreen));
        }
        
        // Handle auto-deploy toggle
        while (autoDeployKey.wasPressed()) {
            handleAutoDeployToggle();
        }
    }
    
    // Public getters for other classes
    public static AppyProxManager getManager() {
        return manager;
    }
    
    public static XaerosIntegration getXaerosIntegration() {
        return manager != null ? manager.getXaerosIntegration() : null;
    }
    
    public static AppyProxNetworkClient getNetworkClient() {
        return manager != null ? manager.getNetworkClient() : null;
    }
    
    public static boolean isXaerosAvailable() {
        XaerosIntegration integration = getXaerosIntegration();
        return integration != null && integration.isAvailable();
    }
    
    public static AutoDeployManager getAutoDeployManager() {
        return autoDeployManager;
    }
    
    /**
     * Initialize the auto-deploy manager
     */
    private void initializeAutoDeployManager() {
        LOGGER.debug("Initializing auto-deploy manager...");
        
        try {
            autoDeployManager = new AutoDeployManager();
            LOGGER.info("Auto-deploy manager initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize auto-deploy manager", e);
        }
    }
    
    /**
     * Start the auto-deployment process
     */
    private void startAutoDeployment() {
        if (autoDeployManager == null) {
            LOGGER.warn("Auto-deploy manager not initialized, skipping deployment");
            return;
        }
        
        if (!autoDeployManager.isEnabled()) {
            LOGGER.info("Auto-deployment is disabled, skipping");
            return;
        }
        
        LOGGER.info("Starting auto-deployment...");
        
        autoDeployManager.startAutoDeployment().thenAccept(success -> {
            if (success) {
                LOGGER.info("Auto-deployment completed successfully");
            } else {
                LOGGER.warn("Auto-deployment failed or was skipped");
            }
        }).exceptionally(throwable -> {
            LOGGER.error("Auto-deployment failed with exception", throwable);
            return null;
        });
    }
    
    /**
     * Handle auto-deploy toggle keybinding
     */
    private void handleAutoDeployToggle() {
        if (autoDeployManager == null) {
            LOGGER.warn("Auto-deploy manager not available");
            return;
        }
        
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        
        switch (autoDeployManager.getStatus()) {
            case NOT_STARTED:
            case STOPPED:
            case FAILED:
                LOGGER.info("Starting auto-deployment via keybinding");
                autoDeployManager.startAutoDeployment().thenAccept(success -> {
                    if (client.player != null) {
                        String message = success ? "§a[AppyProx] Deployment started successfully" : "§c[AppyProx] Failed to start deployment";
                        client.player.sendMessage(Text.of(message));
                    }
                });
                break;
                
            case STARTING:
            case RUNNING:
                if (client.player != null) {
                    client.player.sendMessage(Text.of("§e[AppyProx] Deployment already in progress..."));
                }
                break;
                
            case DEPLOYED:
                LOGGER.info("Stopping auto-deployment via keybinding");
                autoDeployManager.stopAutoDeployment().thenAccept(success -> {
                    if (client.player != null) {
                        String message = success ? "§a[AppyProx] Deployment stopped" : "§c[AppyProx] Failed to stop deployment";
                        client.player.sendMessage(Text.of(message));
                    }
                });
                break;
                
            case STOPPING:
                if (client.player != null) {
                    client.player.sendMessage(Text.of("§e[AppyProx] Stopping deployment in progress..."));
                }
                break;
        }
    }
    
    /**
     * Called when the mod is being shut down
     */
    public static void shutdown() {
        LOGGER.info("Shutting down AppyProx Fabric Client...");
        
        // Shutdown auto-deploy manager first
        if (autoDeployManager != null) {
            autoDeployManager.shutdown();
        }
        
        if (manager != null) {
            manager.shutdown();
        }
        
        LOGGER.info("AppyProx Fabric Client shut down");
    }
}