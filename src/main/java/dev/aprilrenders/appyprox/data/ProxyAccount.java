package dev.aprilrenders.appyprox.data;

import net.minecraft.util.math.BlockPos;
import java.util.Objects;

/**
 * Data class representing a proxy-controlled Minecraft account
 */
public class ProxyAccount {
    private String id;
    private String username;
    private String uuid;
    private String accessToken;
    private String status;
    private BlockPos position;
    private int health;
    private int food;
    private String currentTask;
    private String clusterId;
    
    public ProxyAccount() {
        this.id = "";
        this.username = "";
        this.uuid = "";
        this.accessToken = "";
        this.status = "offline";
        this.position = null;
        this.health = 20;
        this.food = 20;
        this.currentTask = null;
        this.clusterId = null;
    }
    
    public ProxyAccount(String id, String username) {
        this.id = id;
        this.username = username;
        this.uuid = "";
        this.accessToken = "";
        this.status = "offline";
        this.position = null;
        this.health = 20;
        this.food = 20;
        this.currentTask = null;
        this.clusterId = null;
    }
    
    public ProxyAccount(String id, String username, String accessToken) {
        this.id = id;
        this.username = username;
        this.uuid = "";
        this.accessToken = accessToken != null ? accessToken : "";
        this.status = "offline";
        this.position = null;
        this.health = 20;
        this.food = 20;
        this.currentTask = null;
        this.clusterId = null;
    }
    
    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getUuid() { return uuid; }
    public String getAccessToken() { return accessToken; }
    public String getStatus() { return status; }
    public BlockPos getPosition() { return position; }
    public int getHealth() { return health; }
    public int getFood() { return food; }
    public String getCurrentTask() { return currentTask; }
    public String getClusterId() { return clusterId; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken != null ? accessToken : ""; }
    public void setStatus(String status) { this.status = status; }
    public void setPosition(BlockPos position) { this.position = position; }
    public void setHealth(int health) { this.health = health; }
    public void setFood(int food) { this.food = food; }
    public void setCurrentTask(String currentTask) { this.currentTask = currentTask; }
    public void setClusterId(String clusterId) { this.clusterId = clusterId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyAccount that = (ProxyAccount) o;
        return health == that.health &&
               food == that.food &&
               Objects.equals(id, that.id) &&
               Objects.equals(username, that.username) &&
               Objects.equals(status, that.status) &&
               Objects.equals(position, that.position) &&
               Objects.equals(currentTask, that.currentTask) &&
               Objects.equals(clusterId, that.clusterId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username, status, position, health, food, currentTask, clusterId);
    }
    
    @Override
    public String toString() {
        return "ProxyAccount{" +
               "id='" + id + '\'' +
               ", username='" + username + '\'' +
               ", status='" + status + '\'' +
               ", position=" + position +
               ", health=" + health +
               ", food=" + food +
               '}';
    }
}