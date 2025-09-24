package dev.aprilrenders.appyprox.data;

import java.util.*;
import net.minecraft.util.math.BlockPos;

/**
 * Represents a cluster of proxy accounts working together
 */
public class Cluster {
    private final String id;
    private String name;
    private final List<String> memberIds;
    private String status;
    private String currentTask;
    private String leaderId;
    private Map<String, Object> configuration;
    private BlockPos centerPosition;
    
    public Cluster(String id, String name) {
        this.id = id;
        this.name = name;
        this.memberIds = new ArrayList<>();
        this.status = "idle";
        this.currentTask = null;
        this.leaderId = null;
        this.configuration = new HashMap<>();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getMemberIds() { return new ArrayList<>(memberIds); }
    public String getStatus() { return status; }
    public String getCurrentTask() { return currentTask; }
    public String getLeaderId() { return leaderId; }
    public Map<String, Object> getConfiguration() { return new HashMap<>(configuration); }
    public BlockPos getCenterPosition() { return centerPosition; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrentTask(String currentTask) { this.currentTask = currentTask; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    public void setConfiguration(Map<String, Object> configuration) { 
        this.configuration = new HashMap<>(configuration); 
    }
    public void setCenterPosition(BlockPos centerPosition) { this.centerPosition = centerPosition; }
    
    // Member management
    public void addMember(String accountId) {
        if (!memberIds.contains(accountId)) {
            memberIds.add(accountId);
        }
    }
    
    public void removeMember(String accountId) {
        memberIds.remove(accountId);
    }
    
    public boolean hasMember(String accountId) {
        return memberIds.contains(accountId);
    }
    
    public int getMemberCount() {
        return memberIds.size();
    }
    
    // Configuration helpers
    public void setConfigValue(String key, Object value) {
        configuration.put(key, value);
    }
    
    public Object getConfigValue(String key) {
        return configuration.get(key);
    }
    
    public Object getConfigValue(String key, Object defaultValue) {
        return configuration.getOrDefault(key, defaultValue);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cluster cluster = (Cluster) o;
        return Objects.equals(id, cluster.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Cluster{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", memberIds=" + memberIds +
               ", status='" + status + '\'' +
               ", currentTask='" + currentTask + '\'' +
               ", leaderId='" + leaderId + '\'' +
               '}';
    }
}