package dev.aprilrenders.appyprox.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 * Manages automatic deployment of the AppyProx system when the Fabric mod loads
 * Handles script execution, status monitoring, and integration with the main system
 */
public class AutoDeployManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeployManager.class);
    private static final Gson GSON = new Gson();
    
    // Configuration paths
    private final Path minecraftDir;
    private final Path configDir;
    private final Path logFile;
    private final Path pidFile;
    private final Path statusFile;
    private final Path autoDeployScript;
    
    // Deployment state
    private volatile DeploymentStatus status = DeploymentStatus.NOT_STARTED;
    private Process deployProcess = null;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    
    // Configuration
    private AutoDeployConfig config;
    private boolean enabled = true;
    
    public enum DeploymentStatus {
        NOT_STARTED,
        STARTING,
        RUNNING,
        DEPLOYED,
        FAILED,
        STOPPING,
        STOPPED
    }
    
    public static class AutoDeployConfig {
        public boolean enabled = true;
        public long startupDelay = 5000;
        public int maxRetries = 3;
        public long retryDelay = 10000;
        public boolean autoStartProxy = true;
        public boolean autoConnectBridge = true;
        public long healthCheckInterval = 30000;
        public boolean shutdownOnMinecraftExit = true;
        public String logLevel = "info";
    }
    
    public AutoDeployManager() {
        // Initialize paths
        this.minecraftDir = Paths.get(System.getProperty("user.home"), ".minecraft");
        this.configDir = minecraftDir.resolve("appyprox");
        this.logFile = minecraftDir.resolve("logs").resolve("appyprox-auto-deploy.log");
        this.pidFile = minecraftDir.resolve("appyprox.pid");
        this.statusFile = configDir.resolve("deploy-status");
        
        // Find the auto-deploy script
        this.autoDeployScript = findAutoDeployScript();
        
        // Load configuration
        loadConfiguration();
        
        LOGGER.info("AutoDeployManager initialized - Script: {}", autoDeployScript);
    }
    
    /**
     * Find the auto-deploy script in the mod's directory structure
     */
    private Path findAutoDeployScript() {
        // Try to find the script relative to the running mod
        String[] possiblePaths = {
            "../auto-deploy.sh",
            "../../auto-deploy.sh", 
            "../../../auto-deploy.sh",
            "/home/april/Projects/AppyProx/AppyProx-FabricMod/auto-deploy.sh"
        };
        
        for (String pathStr : possiblePaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path) && Files.isExecutable(path)) {
                try {
                    return path.toAbsolutePath();
                } catch (Exception e) {
                    LOGGER.debug("Could not resolve path: {}", path);
                }
            }
        }
        
        // Fallback - assume it's in the project directory
        return Paths.get("/home/april/Projects/AppyProx/AppyProx-FabricMod/auto-deploy.sh");
    }
    
    /**
     * Load auto-deploy configuration
     */
    private void loadConfiguration() {
        Path configFile = configDir.resolve("auto-deploy.json");
        
        if (Files.exists(configFile)) {
            try {
                String configJson = Files.readString(configFile);
                JsonObject configObj = GSON.fromJson(configJson, JsonObject.class);
                
                config = new AutoDeployConfig();
                if (configObj.has("enabled")) {
                    config.enabled = configObj.get("enabled").getAsBoolean();
                }
                if (configObj.has("startupDelay")) {
                    config.startupDelay = configObj.get("startupDelay").getAsLong();
                }
                if (configObj.has("maxRetries")) {
                    config.maxRetries = configObj.get("maxRetries").getAsInt();
                }
                if (configObj.has("autoStartProxy")) {
                    config.autoStartProxy = configObj.get("autoStartProxy").getAsBoolean();
                }
                
                this.enabled = config.enabled;
                LOGGER.info("Loaded auto-deploy configuration: enabled={}", enabled);
                
            } catch (Exception e) {
                LOGGER.warn("Failed to load auto-deploy configuration, using defaults: {}", e.getMessage());
                config = new AutoDeployConfig();
            }
        } else {
            config = new AutoDeployConfig();
            LOGGER.info("Using default auto-deploy configuration");
        }
    }
    
    /**
     * Start the auto-deployment process
     */
    public CompletableFuture<Boolean> startAutoDeployment() {
        if (!enabled) {
            LOGGER.info("Auto-deployment is disabled");
            return CompletableFuture.completedFuture(false);
        }
        
        if (status == DeploymentStatus.RUNNING || status == DeploymentStatus.DEPLOYED) {
            LOGGER.info("Auto-deployment already running/deployed");
            return CompletableFuture.completedFuture(true);
        }
        
        status = DeploymentStatus.STARTING;
        
        LOGGER.info("Starting AppyProx auto-deployment...");
        
        // Show in-game message
        showPlayerMessage("§e[AppyProx] Starting auto-deployment...", false);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Wait for startup delay
                if (config.startupDelay > 0) {
                    LOGGER.info("Waiting {}ms before starting deployment...", config.startupDelay);
                    Thread.sleep(config.startupDelay);
                }
                
                // Execute deployment script
                return executeDeploymentScript();
                
            } catch (Exception e) {
                LOGGER.error("Auto-deployment failed", e);
                status = DeploymentStatus.FAILED;
                showPlayerMessage("§c[AppyProx] Auto-deployment failed: " + e.getMessage(), true);
                return false;
            }
        }, executorService);
    }
    
    /**
     * Execute the deployment script
     */
    private boolean executeDeploymentScript() throws IOException, InterruptedException {
        LOGGER.info("Executing deployment script: {}", autoDeployScript);
        
        if (!Files.exists(autoDeployScript)) {
            throw new FileNotFoundException("Auto-deploy script not found: " + autoDeployScript);
        }
        
        ProcessBuilder pb = new ProcessBuilder("bash", autoDeployScript.toString(), "deploy");
        pb.directory(autoDeployScript.getParent().toFile());
        
        // Set up environment variables
        pb.environment().put("APPYPROX_MINECRAFT_MODE", "true");
        pb.environment().put("APPYPROX_AUTO_DEPLOY", "true");
        
        // Redirect output to log file
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
        pb.redirectError(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
        
        try {
            deployProcess = pb.start();
            status = DeploymentStatus.RUNNING;
            
            LOGGER.info("Deployment script started, waiting for completion...");
            
            // Wait for the script to complete with timeout
            boolean completed = deployProcess.waitFor(120, TimeUnit.SECONDS);
            
            if (!completed) {
                LOGGER.warn("Deployment script timed out, terminating...");
                deployProcess.destroyForcibly();
                status = DeploymentStatus.FAILED;
                return false;
            }
            
            int exitCode = deployProcess.exitValue();
            LOGGER.info("Deployment script completed with exit code: {}", exitCode);
            
            if (exitCode == 0) {
                status = DeploymentStatus.DEPLOYED;
                
                // Start monitoring the deployed system
                startHealthMonitoring();
                
                showPlayerMessage("§a[AppyProx] System deployed successfully!", false);
                LOGGER.info("AppyProx system deployed successfully");
                return true;
            } else {
                status = DeploymentStatus.FAILED;
                showPlayerMessage("§c[AppyProx] Deployment failed (exit code: " + exitCode + ")", true);
                return false;
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to execute deployment script", e);
            status = DeploymentStatus.FAILED;
            throw e;
        }
    }
    
    /**
     * Start health monitoring of the deployed system
     */
    private void startHealthMonitoring() {
        if (config.healthCheckInterval <= 0) {
            return;
        }
        
        LOGGER.info("Starting health monitoring (interval: {}ms)", config.healthCheckInterval);
        
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                checkSystemHealth();
            } catch (Exception e) {
                LOGGER.error("Health check failed", e);
            }
        }, config.healthCheckInterval, config.healthCheckInterval, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Check the health of the deployed system
     */
    private void checkSystemHealth() {
        try {
            // Check if PID file exists and process is running
            if (Files.exists(pidFile)) {
                String pidStr = Files.readString(pidFile).trim();
                int pid = Integer.parseInt(pidStr);
                
                // Check if process is running (Linux-specific)
                Process checkProcess = Runtime.getRuntime().exec("ps -p " + pid);
                boolean isRunning = checkProcess.waitFor() == 0;
                
                if (!isRunning) {
                    LOGGER.warn("AppyProx system process is not running (PID: {})", pid);
                    status = DeploymentStatus.FAILED;
                    showPlayerMessage("§c[AppyProx] System process stopped unexpectedly", true);
                }
                
            } else {
                LOGGER.warn("PID file not found, system may not be running");
                status = DeploymentStatus.FAILED;
            }
            
            // Check status file
            if (Files.exists(statusFile)) {
                String statusStr = Files.readString(statusFile).trim();
                if ("FAILED".equals(statusStr)) {
                    status = DeploymentStatus.FAILED;
                    showPlayerMessage("§c[AppyProx] System reported failure status", true);
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("Health check error", e);
        }
    }
    
    /**
     * Stop the auto-deployed system
     */
    public CompletableFuture<Boolean> stopAutoDeployment() {
        if (status == DeploymentStatus.NOT_STARTED || status == DeploymentStatus.STOPPED) {
            return CompletableFuture.completedFuture(true);
        }
        
        LOGGER.info("Stopping AppyProx auto-deployment...");
        status = DeploymentStatus.STOPPING;
        
        showPlayerMessage("§e[AppyProx] Stopping system...", false);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Execute stop command
                ProcessBuilder pb = new ProcessBuilder("bash", autoDeployScript.toString(), "stop");
                pb.directory(autoDeployScript.getParent().toFile());
                
                Process stopProcess = pb.start();
                boolean completed = stopProcess.waitFor(30, TimeUnit.SECONDS);
                
                if (!completed) {
                    stopProcess.destroyForcibly();
                }
                
                status = DeploymentStatus.STOPPED;
                showPlayerMessage("§a[AppyProx] System stopped", false);
                LOGGER.info("AppyProx system stopped successfully");
                
                return true;
                
            } catch (Exception e) {
                LOGGER.error("Failed to stop deployment", e);
                status = DeploymentStatus.FAILED;
                return false;
            }
        }, executorService);
    }
    
    /**
     * Get the current deployment status
     */
    public DeploymentStatus getStatus() {
        return status;
    }
    
    /**
     * Check if the system is deployed and running
     */
    public boolean isDeployed() {
        return status == DeploymentStatus.DEPLOYED;
    }
    
    /**
     * Get deployment logs
     */
    public String getDeploymentLogs() {
        try {
            if (Files.exists(logFile)) {
                return Files.readString(logFile);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read deployment logs", e);
        }
        return "No logs available";
    }
    
    /**
     * Show a message to the player
     */
    private void showPlayerMessage(String message, boolean isError) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.of(message));
            }
            
            if (isError) {
                LOGGER.error(message.replaceAll("§.", "")); // Remove color codes for logging
            } else {
                LOGGER.info(message.replaceAll("§.", "")); // Remove color codes for logging
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to show player message: {}", message, e);
        }
    }
    
    /**
     * Shutdown the auto-deploy manager
     */
    public void shutdown() {
        LOGGER.info("Shutting down AutoDeployManager...");
        
        // Stop health monitoring
        scheduledExecutor.shutdown();
        
        // Stop deployment if running
        if (status == DeploymentStatus.DEPLOYED && config.shutdownOnMinecraftExit) {
            try {
                stopAutoDeployment().get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("Failed to stop deployment during shutdown", e);
            }
        }
        
        // Shutdown executors
        executorService.shutdown();
        
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        LOGGER.info("AutoDeployManager shutdown complete");
    }
    
    /**
     * Get configuration
     */
    public AutoDeployConfig getConfig() {
        return config;
    }
    
    /**
     * Check if auto-deployment is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enable or disable auto-deployment
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        LOGGER.info("Auto-deployment {}", enabled ? "enabled" : "disabled");
    }
}