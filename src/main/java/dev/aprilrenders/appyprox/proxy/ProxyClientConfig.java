package dev.aprilrenders.appyprox.proxy;

import java.util.*;

/**
 * Configuration for proxy clients
 */
public class ProxyClientConfig {
    
    private final boolean headless;
    private final boolean autoRestart;
    private final int maxMemoryMB;
    private final int initialMemoryMB;
    private final String serverAddress;
    private final int serverPort;
    private final String minecraftVersion;
    private final String jvmExecutable;
    private final List<String> modList;
    private final Map<String, String> jvmArguments;
    private final Map<String, String> gameArguments;
    
    private ProxyClientConfig(Builder builder) {
        this.headless = builder.headless;
        this.autoRestart = builder.autoRestart;
        this.maxMemoryMB = builder.maxMemoryMB;
        this.initialMemoryMB = builder.initialMemoryMB;
        this.serverAddress = builder.serverAddress;
        this.serverPort = builder.serverPort;
        this.minecraftVersion = builder.minecraftVersion;
        this.jvmExecutable = builder.jvmExecutable;
        this.modList = Collections.unmodifiableList(new ArrayList<>(builder.modList));
        this.jvmArguments = Collections.unmodifiableMap(new HashMap<>(builder.jvmArguments));
        this.gameArguments = Collections.unmodifiableMap(new HashMap<>(builder.gameArguments));
    }
    
    // Getters
    public boolean isHeadless() {
        return headless;
    }
    
    public boolean isAutoRestart() {
        return autoRestart;
    }
    
    public int getMaxMemoryMB() {
        return maxMemoryMB;
    }
    
    public int getInitialMemoryMB() {
        return initialMemoryMB;
    }
    
    public String getServerAddress() {
        return serverAddress;
    }
    
    public int getServerPort() {
        return serverPort;
    }
    
    public String getMinecraftVersion() {
        return minecraftVersion;
    }
    
    public String getJvmExecutable() {
        return jvmExecutable;
    }
    
    public List<String> getModList() {
        return modList;
    }
    
    public Map<String, String> getJvmArguments() {
        return jvmArguments;
    }
    
    public Map<String, String> getGameArguments() {
        return gameArguments;
    }
    
    public static class Builder {
        private boolean headless = true;
        private boolean autoRestart = true;
        private int maxMemoryMB = 2048;
        private int initialMemoryMB = 1024;
        private String serverAddress = "localhost";
        private int serverPort = 25565;
        private String minecraftVersion = "1.20.4";
        private String jvmExecutable = "java";
        private List<String> modList = new ArrayList<>();
        private Map<String, String> jvmArguments = new HashMap<>();
        private Map<String, String> gameArguments = new HashMap<>();
        
        public Builder setHeadless(boolean headless) {
            this.headless = headless;
            return this;
        }
        
        public Builder setAutoRestart(boolean autoRestart) {
            this.autoRestart = autoRestart;
            return this;
        }
        
        public Builder setMaxMemory(int maxMemoryMB) {
            this.maxMemoryMB = maxMemoryMB;
            return this;
        }
        
        public Builder setInitialMemory(int initialMemoryMB) {
            this.initialMemoryMB = initialMemoryMB;
            return this;
        }
        
        public Builder setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
            return this;
        }
        
        public Builder setServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }
        
        public Builder setMinecraftVersion(String minecraftVersion) {
            this.minecraftVersion = minecraftVersion;
            return this;
        }
        
        public Builder setJvmExecutable(String jvmExecutable) {
            this.jvmExecutable = jvmExecutable;
            return this;
        }
        
        public Builder addMod(String mod) {
            this.modList.add(mod);
            return this;
        }
        
        public Builder setModList(List<String> modList) {
            this.modList = new ArrayList<>(modList);
            return this;
        }
        
        public Builder addJvmArgument(String key, String value) {
            this.jvmArguments.put(key, value);
            return this;
        }
        
        public Builder setJvmArguments(Map<String, String> jvmArguments) {
            this.jvmArguments = new HashMap<>(jvmArguments);
            return this;
        }
        
        public Builder addGameArgument(String key, String value) {
            this.gameArguments.put(key, value);
            return this;
        }
        
        public Builder setGameArguments(Map<String, String> gameArguments) {
            this.gameArguments = new HashMap<>(gameArguments);
            return this;
        }
        
        public ProxyClientConfig build() {
            return new ProxyClientConfig(this);
        }
    }
    
    // Static factory methods
    public static ProxyClientConfig createDefault() {
        return new Builder().build();
    }
    
    public static ProxyClientConfig createAutomationPreset() {
        return new Builder()
            .setHeadless(true)
            .setAutoRestart(true)
            .setMaxMemory(4096)
            .setInitialMemory(2048)
            .addMod("baritone")
            .addMod("litematica")
            .addMod("worldedit")
            .build();
    }
    
    public static ProxyClientConfig createPerformancePreset() {
        return new Builder()
            .setHeadless(true)
            .setAutoRestart(true)
            .setMaxMemory(8192)
            .setInitialMemory(4096)
            .addMod("sodium")
            .addMod("lithium")
            .addMod("phosphor")
            .build();
    }
    
    @Override
    public String toString() {
        return "ProxyClientConfig{" +
                "headless=" + headless +
                ", autoRestart=" + autoRestart +
                ", maxMemoryMB=" + maxMemoryMB +
                ", serverAddress='" + serverAddress + '\'' +
                ", serverPort=" + serverPort +
                ", minecraftVersion='" + minecraftVersion + '\'' +
                ", modCount=" + modList.size() +
                '}';
    }
}