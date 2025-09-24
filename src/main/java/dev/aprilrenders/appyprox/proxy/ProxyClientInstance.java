package dev.aprilrenders.appyprox.proxy;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a running proxy client instance
 */
public class ProxyClientInstance {
    
    private final String accountId;
    private final String username;
    private ProxyClientStatus status;
    private Process process;
    private Path instanceDirectory;
    
    public ProxyClientInstance(String accountId, String username) {
        this.accountId = accountId;
        this.username = username;
        this.status = ProxyClientStatus.OFFLINE;
        this.process = null;
        this.instanceDirectory = Paths.get("instances", accountId);
    }
    
    // Getters
    public String getAccountId() {
        return accountId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public ProxyClientStatus getStatus() {
        return status;
    }
    
    public Process getProcess() {
        return process;
    }
    
    public Path getInstanceDirectory() {
        return instanceDirectory;
    }
    
    // Setters
    public void setStatus(ProxyClientStatus status) {
        this.status = status;
    }
    
    public void setProcess(Process process) {
        this.process = process;
    }
    
    public void setInstanceDirectory(Path instanceDirectory) {
        this.instanceDirectory = instanceDirectory;
    }
    
    /**
     * Check if the instance is running
     */
    public boolean isRunning() {
        return status == ProxyClientStatus.RUNNING && process != null && process.isAlive();
    }
    
    /**
     * Stop the instance
     */
    public void stop() {
        if (process != null && process.isAlive()) {
            process.destroy();
            status = ProxyClientStatus.STOPPING;
        }
    }
    
    /**
     * Force stop the instance
     */
    public void forceStop() {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
            status = ProxyClientStatus.OFFLINE;
        }
    }
    
    @Override
    public String toString() {
        return "ProxyClientInstance{" +
                "accountId='" + accountId + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", isRunning=" + isRunning() +
                '}';
    }
}