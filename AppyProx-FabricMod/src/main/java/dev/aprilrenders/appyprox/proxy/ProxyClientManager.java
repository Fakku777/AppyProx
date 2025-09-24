package dev.aprilrenders.appyprox.proxy;

import dev.aprilrenders.appyprox.network.AppyProxNetworkClient;
import dev.aprilrenders.appyprox.data.ProxyAccount;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Manages proxy client instances
 */
public class ProxyClientManager {
    
    private final AppyProxNetworkClient networkClient;
    private final Map<String, ProxyClientInfo> clientInfo = new ConcurrentHashMap<>();
    private final Map<String, ProxyClientInstance> clientInstances = new ConcurrentHashMap<>();
    
    public ProxyClientManager(AppyProxNetworkClient networkClient) {
        this.networkClient = networkClient;
    }
    
    /**
     * Get all client info
     */
    public Map<String, ProxyClientInfo> getAllClientInfo() {
        return new ConcurrentHashMap<>(clientInfo);
    }
    
    /**
     * Get client info by account ID
     */
    public Optional<ProxyClientInfo> getClientInfo(String accountId) {
        return Optional.ofNullable(clientInfo.get(accountId));
    }
    
    /**
     * Update client info
     */
    public void updateClientInfo(String accountId, ProxyClientInfo info) {
        clientInfo.put(accountId, info);
    }
    
    /**
     * Start a proxy client
     */
    public CompletableFuture<ProxyClientInstance> startClient(ProxyAccount account, ProxyClientConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            ProxyClientInstance instance = new ProxyClientInstance(account.getId(), account.getUsername());
            instance.setStatus(ProxyClientStatus.STARTING);
            clientInstances.put(account.getId(), instance);
            
            // Update client info
            ProxyClientInfo info = new ProxyClientInfo(account.getId(), account.getUsername());
            info.setStatus(ProxyClientStatus.STARTING);
            clientInfo.put(account.getId(), info);
            
            // Simulate startup process
            try {
                Thread.sleep(2000);
                instance.setStatus(ProxyClientStatus.RUNNING);
                info.setStatus(ProxyClientStatus.RUNNING);
                clientInfo.put(account.getId(), info);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                instance.setStatus(ProxyClientStatus.FAILED);
                info.setStatus(ProxyClientStatus.FAILED);
                clientInfo.put(account.getId(), info);
                throw new RuntimeException("Client startup interrupted", e);
            }
            
            return instance;
        });
    }
    
    /**
     * Stop a proxy client
     */
    public CompletableFuture<Boolean> stopClient(String accountId) {
        return CompletableFuture.supplyAsync(() -> {
            ProxyClientInstance instance = clientInstances.get(accountId);
            if (instance != null) {
                instance.setStatus(ProxyClientStatus.STOPPING);
                ProxyClientInfo info = clientInfo.get(accountId);
                if (info != null) {
                    info.setStatus(ProxyClientStatus.STOPPING);
                }
                
                // Simulate shutdown process
                try {
                    Thread.sleep(1000);
                    instance.setStatus(ProxyClientStatus.OFFLINE);
                    if (info != null) {
                        info.setStatus(ProxyClientStatus.OFFLINE);
                    }
                    clientInstances.remove(accountId);
                    return true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return false;
        });
    }
    
    /**
     * Get a client instance
     */
    public Optional<ProxyClientInstance> getClientInstance(String accountId) {
        return Optional.ofNullable(clientInstances.get(accountId));
    }
    
    /**
     * Check if client is running
     */
    public boolean isClientRunning(String accountId) {
        return clientInstances.containsKey(accountId) && 
               clientInstances.get(accountId).getStatus() == ProxyClientStatus.RUNNING;
    }
    
    /**
     * Get client count
     */
    public int getClientCount() {
        return clientInstances.size();
    }
    
    /**
     * Get running client count
     */
    public int getRunningClientCount() {
        return (int) clientInstances.values().stream()
                .filter(instance -> instance.getStatus() == ProxyClientStatus.RUNNING)
                .count();
    }
    
    /**
     * Get active client count (alias for getRunningClientCount)
     */
    public int getActiveClientCount() {
        return getRunningClientCount();
    }
    
    /**
     * Validate client configuration
     */
    public void validateConfig(ProxyClientConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        
        if (config.getMaxMemoryMB() <= 0) {
            throw new IllegalArgumentException("Max memory must be positive");
        }
        
        if (config.getInitialMemoryMB() <= 0) {
            throw new IllegalArgumentException("Initial memory must be positive");
        }
        
        if (config.getMaxMemoryMB() < config.getInitialMemoryMB()) {
            throw new IllegalArgumentException("Max memory must be greater than or equal to initial memory");
        }
        
        if (config.getServerAddress() == null || config.getServerAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Server address cannot be null or empty");
        }
        
        if (config.getServerPort() <= 0 || config.getServerPort() > 65535) {
            throw new IllegalArgumentException("Server port must be between 1 and 65535");
        }
    }
    
    /**
     * Shutdown the manager
     */
    public void shutdown() {
        // Stop all clients
        clientInstances.keySet().forEach(this::stopClient);
        clientInfo.clear();
        clientInstances.clear();
    }
}