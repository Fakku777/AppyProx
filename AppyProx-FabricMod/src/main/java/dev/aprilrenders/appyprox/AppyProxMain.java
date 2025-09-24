package dev.aprilrenders.appyprox;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.proxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for AppyProx Java Proxy Client Management System
 * Handles integration with the JavaScript AppyProx system via TCP bridge
 */
public class AppyProxMain {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppyProxMain.class);
    private static final Gson GSON = new Gson();
    
    // Configuration
    private String bridgeHost;
    private int bridgePort;
    private String configPath;
    
    // Components
    private ProxyClientManager clientManager;
    private ProxyClientLifecycleManager lifecycleManager;
    private ProxyClientRemoteControl remoteControl;
    private ProxyClientModDeployer modDeployer;
    private ProxyClientDashboard dashboard;
    private ProxyClientIntegration integration;
    
    // Bridge communication
    private Socket bridgeSocket;
    private PrintWriter bridgeWriter;
    private BufferedReader bridgeReader;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    
    private volatile boolean running = false;
    
    public static void main(String[] args) {
        AppyProxMain main = new AppyProxMain();
        
        try {
            main.initialize();
            main.start();
            main.keepAlive();
        } catch (Exception e) {
            LOGGER.error("Failed to start AppyProx Java system", e);
            System.exit(1);
        }
    }
    
    private void initialize() {
        LOGGER.info("Initializing AppyProx Java Proxy Client Management System...");
        
        // Load configuration from environment/system properties
        bridgeHost = System.getProperty("appyprox.bridge.host", 
                                      System.getenv("APPYPROX_BRIDGE_HOST"));
        if (bridgeHost == null) {
            bridgeHost = "127.0.0.1";
        }
        
        String portStr = System.getProperty("appyprox.bridge.port", 
                                          System.getenv("APPYPROX_BRIDGE_PORT"));
        bridgePort = portStr != null ? Integer.parseInt(portStr) : 25800;
        
        configPath = System.getProperty("appyprox.config.path", 
                                       System.getenv("APPYPROX_CONFIG_PATH"));
        if (configPath == null) {
            configPath = "./configs";
        }
        
        LOGGER.info("Bridge configuration: {}:{}", bridgeHost, bridgePort);
        LOGGER.info("Config path: {}", configPath);
        
        // Initialize proxy management components
        initializeComponents();
    }
    
    private void initializeComponents() {
        try {
            LOGGER.info("Initializing proxy management components...");
            
            // Initialize components
            // Create a stub network client for now
            dev.aprilrenders.appyprox.network.AppyProxNetworkClient networkClient = 
                new dev.aprilrenders.appyprox.network.AppyProxNetworkClient("localhost", 3000, 8081);
            clientManager = new ProxyClientManager(networkClient);
            lifecycleManager = new ProxyClientLifecycleManager(clientManager);
            remoteControl = new ProxyClientRemoteControl();
            modDeployer = new ProxyClientModDeployer(Paths.get(configPath, "mods"));
            dashboard = new ProxyClientDashboard(clientManager, lifecycleManager, remoteControl, modDeployer);
            integration = new ProxyClientIntegration(clientManager, lifecycleManager, remoteControl, modDeployer, dashboard);
            
            LOGGER.info("Proxy management components initialized successfully");
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize components", e);
            throw new RuntimeException("Component initialization failed", e);
        }
    }
    
    private void start() throws IOException {
        LOGGER.info("Starting AppyProx Java system...");
        
        // Connect to JavaScript bridge
        connectToBridge();
        
        // Start command processing
        startCommandProcessing();
        
        // Start periodic status updates
        startStatusUpdates();
        
        running = true;
        LOGGER.info("AppyProx Java system started successfully");
    }
    
    private void connectToBridge() throws IOException {
        LOGGER.info("Connecting to bridge at {}:{}", bridgeHost, bridgePort);
        
        int maxRetries = 10;
        int retryDelay = 2000; // 2 seconds
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                bridgeSocket = new Socket(bridgeHost, bridgePort);
                bridgeWriter = new PrintWriter(
                    new OutputStreamWriter(bridgeSocket.getOutputStream()), true);
                bridgeReader = new BufferedReader(
                    new InputStreamReader(bridgeSocket.getInputStream()));
                
                LOGGER.info("Connected to bridge successfully");
                return;
                
            } catch (IOException e) {
                LOGGER.warn("Bridge connection attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Connection interrupted", ie);
                    }
                } else {
                    throw new IOException("Failed to connect to bridge after " + maxRetries + " attempts", e);
                }
            }
        }
    }
    
    private void startCommandProcessing() {
        executorService.submit(() -> {
            try {
                String line;
                while (running && (line = bridgeReader.readLine()) != null) {
                    try {
                        processCommand(line);
                    } catch (Exception e) {
                        LOGGER.error("Error processing command: " + line, e);
                    }
                }
            } catch (IOException e) {
                if (running) {
                    LOGGER.error("Bridge communication error", e);
                    // Attempt reconnection
                    reconnectToBridge();
                }
            }
        });
    }
    
    private void processCommand(String commandJson) {
        try {
            JsonObject command = GSON.fromJson(commandJson, JsonObject.class);
            
            String type = command.get("type").getAsString();
            int commandId = command.get("id").getAsInt();
            String commandName = command.get("command").getAsString();
            JsonObject parameters = command.getAsJsonObject("parameters");
            
            LOGGER.debug("Processing command: {} ({})", commandName, commandId);
            
            CompletableFuture<JsonObject> resultFuture = executeCommand(commandName, parameters);
            
            resultFuture.whenComplete((result, error) -> {
                sendCommandResponse(commandId, result, error);
            });
            
        } catch (Exception e) {
            LOGGER.error("Error parsing command", e);
        }
    }
    
    private CompletableFuture<JsonObject> executeCommand(String command, JsonObject parameters) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject result = new JsonObject();
                
                switch (command) {
                    case "START_CLIENT":
                        result = handleStartClient(parameters);
                        break;
                        
                    case "STOP_CLIENT":
                        result = handleStopClient(parameters);
                        break;
                        
                    case "EXECUTE_TASK":
                        result = handleExecuteTask(parameters);
                        break;
                        
                    case "EXECUTE_CLUSTER_TASK":
                        result = handleExecuteClusterTask(parameters);
                        break;
                        
                    case "GET_SYSTEM_STATUS":
                        result = handleGetSystemStatus();
                        break;
                        
                    case "GET_DASHBOARD_DATA":
                        result = handleGetDashboardData();
                        break;
                        
                    case "SYSTEM_SHUTDOWN":
                        result = handleSystemShutdown();
                        break;
                        
                    default:
                        result.addProperty("error", "Unknown command: " + command);
                }
                
                return result;
                
            } catch (Exception e) {
                LOGGER.error("Error executing command: " + command, e);
                JsonObject errorResult = new JsonObject();
                errorResult.addProperty("error", e.getMessage());
                return errorResult;
            }
        });
    }
    
    private JsonObject handleStartClient(JsonObject parameters) {
        try {
            JsonObject accountJson = parameters.getAsJsonObject("account");
            JsonObject configJson = parameters.getAsJsonObject("config");
            
            // Convert JSON to Java objects
            ProxyAccount account = convertJsonToAccount(accountJson);
            ProxyClientConfig config = convertJsonToConfig(configJson);
            
            ProxyClientIntegration.IntegrationOptions options = new ProxyClientIntegration.IntegrationOptions();
            options.enableAutomation = true;
            options.autoJoinCluster = true;
            
            CompletableFuture<ProxyClientInstance> future = 
                integration.startIntegratedClient(account, config, options);
            
            ProxyClientInstance instance = future.get();
            
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("instanceId", instance.getAccountId());
            result.addProperty("status", instance.getStatus().name());
            
            // Send client registered event
            sendClientEvent("CLIENT_REGISTERED", account.getId(), "Client registered", null);
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("Failed to start client", e);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("error", e.getMessage());
            return result;
        }
    }
    
    private JsonObject handleStopClient(JsonObject parameters) {
        try {
            String accountId = parameters.get("accountId").getAsString();
            boolean graceful = parameters.has("graceful") ? parameters.get("graceful").getAsBoolean() : true;
            
            boolean stopped = integration.stopIntegratedClient(accountId, graceful).get();
            
            JsonObject result = new JsonObject();
            result.addProperty("success", stopped);
            
            if (stopped) {
                sendClientEvent("CLIENT_UNREGISTERED", accountId, "Client stopped", null);
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("Failed to stop client", e);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("error", e.getMessage());
            return result;
        }
    }
    
    private JsonObject handleExecuteTask(JsonObject parameters) {
        try {
            String accountId = parameters.get("accountId").getAsString();
            JsonObject taskJson = parameters.getAsJsonObject("task");
            
            ProxyClientIntegration.AutomationTask task = new ProxyClientIntegration.AutomationTask();
            task.type = ProxyClientIntegration.TaskType.valueOf(taskJson.get("type").getAsString());
            
            // Convert parameters
            if (taskJson.has("parameters")) {
                JsonObject taskParams = taskJson.getAsJsonObject("parameters");
                for (Map.Entry<String, com.google.gson.JsonElement> entry : taskParams.entrySet()) {
                    task.parameters.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
            
            ProxyClientIntegration.AutomationResult result = 
                integration.executeAutomationTask(accountId, task).get();
            
            JsonObject response = new JsonObject();
            response.addProperty("success", result.success);
            response.addProperty("taskId", task.id);
            if (result.error != null) {
                response.addProperty("error", result.error);
            }
            
            // Send task completion event
            String eventType = result.success ? "AUTOMATION_COMPLETED" : "AUTOMATION_FAILED";
            sendClientEvent(eventType, accountId, "Task " + task.type + " completed", 
                           GSON.toJsonTree(result).getAsJsonObject());
            
            return response;
            
        } catch (Exception e) {
            LOGGER.error("Failed to execute task", e);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("error", e.getMessage());
            return result;
        }
    }
    
    private JsonObject handleExecuteClusterTask(JsonObject parameters) {
        try {
            String clusterId = parameters.get("clusterId").getAsString();
            JsonObject taskJson = parameters.getAsJsonObject("task");
            
            ProxyClientIntegration.AutomationTask task = new ProxyClientIntegration.AutomationTask();
            task.type = ProxyClientIntegration.TaskType.valueOf(taskJson.get("type").getAsString());
            
            Map<String, ProxyClientIntegration.AutomationResult> results = 
                integration.executeClusterAutomation(clusterId, task).get();
            
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("clusterId", clusterId);
            response.addProperty("totalClients", results.size());
            response.addProperty("successfulClients", 
                (int) results.values().stream().mapToInt(r -> r.success ? 1 : 0).sum());
            
            return response;
            
        } catch (Exception e) {
            LOGGER.error("Failed to execute cluster task", e);
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("error", e.getMessage());
            return result;
        }
    }
    
    private JsonObject handleGetSystemStatus() {
        ProxyClientIntegration.IntegrationStatus status = integration.getIntegrationStatus();
        
        JsonObject result = new JsonObject();
        result.addProperty("totalClients", status.totalClients);
        result.addProperty("connectedClients", status.connectedClients);
        result.addProperty("automationActiveClients", status.automationActiveClients);
        result.addProperty("systemHealth", status.systemHealth);
        
        return result;
    }
    
    private JsonObject handleGetDashboardData() {
        ProxyClientDashboard.DashboardOverview overview = dashboard.getDashboardOverview();
        
        JsonObject result = new JsonObject();
        result.addProperty("totalClients", overview.totalClients);
        result.addProperty("runningClients", overview.runningClients);
        result.addProperty("healthyClients", overview.healthyClients);
        result.addProperty("averageHealthScore", overview.averageHealthScore);
        result.addProperty("totalMemoryUsageMB", overview.totalMemoryUsageMB);
        result.addProperty("averageCpuUsage", overview.averageCpuUsage);
        
        return result;
    }
    
    private JsonObject handleSystemShutdown() {
        LOGGER.info("Received shutdown command");
        
        executorService.schedule(() -> {
            shutdown();
        }, 1000, TimeUnit.MILLISECONDS);
        
        JsonObject result = new JsonObject();
        result.addProperty("success", true);
        result.addProperty("message", "Shutdown initiated");
        
        return result;
    }
    
    private void sendCommandResponse(int commandId, JsonObject result, Throwable error) {
        try {
            JsonObject response = new JsonObject();
            response.addProperty("type", "COMMAND_RESPONSE");
            response.addProperty("id", commandId);
            
            if (error != null) {
                response.addProperty("error", error.getMessage());
            } else {
                response.add("payload", result);
            }
            
            String responseJson = GSON.toJson(response);
            bridgeWriter.println(responseJson);
            
        } catch (Exception e) {
            LOGGER.error("Failed to send command response", e);
        }
    }
    
    private void sendClientEvent(String eventType, String accountId, String message, JsonObject data) {
        try {
            JsonObject event = new JsonObject();
            event.addProperty("type", "CLIENT_EVENT");
            
            JsonObject payload = new JsonObject();
            payload.addProperty("eventType", eventType);
            payload.addProperty("accountId", accountId);
            payload.addProperty("message", message);
            if (data != null) {
                payload.add("data", data);
            }
            
            event.add("payload", payload);
            
            String eventJson = GSON.toJson(event);
            bridgeWriter.println(eventJson);
            
        } catch (Exception e) {
            LOGGER.error("Failed to send client event", e);
        }
    }
    
    private void startStatusUpdates() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                // Send periodic status updates
                ProxyClientIntegration.IntegrationStatus status = integration.getIntegrationStatus();
                
                JsonObject statusUpdate = new JsonObject();
                statusUpdate.addProperty("type", "SYSTEM_STATUS");
                
                JsonObject payload = new JsonObject();
                payload.addProperty("totalClients", status.totalClients);
                payload.addProperty("connectedClients", status.connectedClients);
                payload.addProperty("systemHealth", status.systemHealth);
                
                statusUpdate.add("payload", payload);
                
                String statusJson = GSON.toJson(statusUpdate);
                bridgeWriter.println(statusJson);
                
            } catch (Exception e) {
                LOGGER.error("Failed to send status update", e);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
    
    private void reconnectToBridge() {
        if (!running) return;
        
        LOGGER.info("Attempting to reconnect to bridge...");
        
        executorService.schedule(() -> {
            try {
                connectToBridge();
                startCommandProcessing();
                LOGGER.info("Reconnected to bridge successfully");
            } catch (IOException e) {
                LOGGER.error("Failed to reconnect to bridge", e);
                reconnectToBridge(); // Try again
            }
        }, 5, TimeUnit.SECONDS);
    }
    
    private ProxyAccount convertJsonToAccount(JsonObject json) {
        ProxyAccount account = new ProxyAccount();
        account.setId(json.get("id").getAsString());
        account.setUsername(json.get("username").getAsString());
        account.setUuid(json.get("uuid").getAsString());
        account.setAccessToken(json.get("accessToken").getAsString());
        return account;
    }
    
    private ProxyClientConfig convertJsonToConfig(JsonObject json) {
        ProxyClientConfig.Builder builder = new ProxyClientConfig.Builder();
        
        if (json.has("headless")) {
            builder.setHeadless(json.get("headless").getAsBoolean());
        }
        if (json.has("autoRestart")) {
            builder.setAutoRestart(json.get("autoRestart").getAsBoolean());
        }
        if (json.has("maxMemoryMB")) {
            builder.setMaxMemory(json.get("maxMemoryMB").getAsInt());
        }
        if (json.has("serverAddress")) {
            builder.setServerAddress(json.get("serverAddress").getAsString());
        }
        if (json.has("serverPort")) {
            builder.setServerPort(json.get("serverPort").getAsInt());
        }
        
        return builder.build();
    }
    
    private void keepAlive() {
        // Keep main thread alive
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void shutdown() {
        LOGGER.info("Shutting down AppyProx Java system...");
        
        running = false;
        
        try {
            if (integration != null) {
                integration.shutdown();
            }
            if (dashboard != null) {
                dashboard.shutdown();
            }
            if (remoteControl != null) {
                remoteControl.shutdown();
            }
            if (lifecycleManager != null) {
                lifecycleManager.shutdown();
            }
            if (clientManager != null) {
                clientManager.shutdown();
            }
            
            executorService.shutdown();
            
            if (bridgeSocket != null) {
                bridgeSocket.close();
            }
            
        } catch (Exception e) {
            LOGGER.error("Error during shutdown", e);
        }
        
        LOGGER.info("AppyProx Java system shutdown complete");
        System.exit(0);
    }
}