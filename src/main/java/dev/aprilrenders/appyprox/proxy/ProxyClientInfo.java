package dev.aprilrenders.appyprox.proxy;

import java.time.Instant;

/**
 * Information about a proxy client instance
 */
public class ProxyClientInfo {
    
    public String accountId;
    public String username;
    public ProxyClientStatus status;
    public double healthScore;
    public long memoryUsageMB;
    public double cpuUsagePercent;
    public Instant startTime;
    
    public ProxyClientInfo() {
        this.accountId = "";
        this.username = "";
        this.status = ProxyClientStatus.OFFLINE;
        this.healthScore = 100.0;
        this.memoryUsageMB = 0;
        this.cpuUsagePercent = 0.0;
        this.startTime = Instant.now();
    }
    
    public ProxyClientInfo(String accountId, String username) {
        this.accountId = accountId;
        this.username = username;
        this.status = ProxyClientStatus.OFFLINE;
        this.healthScore = 100.0;
        this.memoryUsageMB = 0;
        this.cpuUsagePercent = 0.0;
        this.startTime = Instant.now();
    }
    
    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public ProxyClientStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProxyClientStatus status) {
        this.status = status;
    }
    
    public double getHealthScore() {
        return healthScore;
    }
    
    public void setHealthScore(double healthScore) {
        this.healthScore = healthScore;
    }
    
    public long getMemoryUsageMB() {
        return memoryUsageMB;
    }
    
    public void setMemoryUsageMB(long memoryUsageMB) {
        this.memoryUsageMB = memoryUsageMB;
    }
    
    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }
    
    public void setCpuUsagePercent(double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Get the uptime in seconds
     */
    public long getUptime() {
        return java.time.Duration.between(startTime, Instant.now()).getSeconds();
    }
    
    @Override
    public String toString() {
        return "ProxyClientInfo{" +
                "accountId='" + accountId + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", healthScore=" + healthScore +
                ", memoryUsageMB=" + memoryUsageMB +
                ", cpuUsagePercent=" + cpuUsagePercent +
                '}';
    }
}