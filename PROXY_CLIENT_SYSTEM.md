# AppyProx Proxy Client Management System

## Overview
The Proxy Client Management System is a sophisticated headless Minecraft client orchestration platform that enables spawning, managing, and controlling multiple Minecraft client instances remotely. This system forms the core of AppyProx's multi-account automation capabilities.

## 🏗️ Architecture Components

### 1. **ProxyClientManager** - Core Orchestrator
The central management class that handles all proxy client operations:

#### **Key Features:**
- **Concurrent Client Management**: Supports up to 10 simultaneous proxy clients
- **Health Monitoring**: Continuous health checks every 5 seconds with auto-recovery
- **Resource Management**: Automatic cleanup and proper shutdown procedures  
- **Dynamic Configuration**: Runtime configuration loading and management
- **Thread Safety**: Full concurrent access support with thread-safe operations

#### **Client Lifecycle Operations:**
```java
// Create and start a proxy client
CompletableFuture<ProxyClientInstance> createProxyClient(ProxyAccount account, ProxyClientConfig config)

// Stop a client gracefully or forcefully
CompletableFuture<Boolean> stopProxyClient(String accountId, boolean graceful)

// Send commands to running clients
CompletableFuture<Boolean> sendCommand(String accountId, String command, Map<String, Object> parameters)

// Deploy mods dynamically
CompletableFuture<Boolean> deployMods(String accountId, List<Path> modPaths)
```

### 2. **ProxyClientInstance** - Individual Client State
Represents a single running headless Minecraft client:

#### **State Management:**
- **Process Control**: Direct JVM process management and monitoring
- **Performance Metrics**: CPU usage, memory consumption, uptime tracking
- **Health Status**: Real-time health checks and heartbeat monitoring
- **Configuration**: Per-instance settings and customizations

#### **Status Tracking:**
- Start time and uptime calculation
- Restart count and reliability metrics  
- Last heartbeat timestamp
- Process health verification

### 3. **ProxyClientConfig** - Configuration Framework
Comprehensive configuration system for proxy clients:

#### **Resource Allocation:**
- Memory allocation (512MB - 32GB range)
- CPU usage limits (10% - 100%)
- Garbage collection optimization (G1GC, SerialGC)

#### **Network Configuration:**
- Target server address and port
- Auto-reconnection settings
- Connection timeout configurations

#### **Behavior Settings:**
- Auto-restart capabilities
- Maximum restart attempts
- Headless mode enforcement
- Logging preferences

#### **Mod Management:**
- Enabled/disabled mod lists
- Per-mod configuration settings
- Dynamic mod deployment support

#### **JVM Optimization:**
```java
// Optimized JVM arguments
-Xmx2048M -Xms1024M
-XX:+UseG1GC
-XX:+UnlockExperimentalVMOptions
-XX:G1NewSizePercent=20
-XX:G1ReservePercent=20
-XX:MaxGCPauseMillis=50
-XX:G1HeapRegionSize=32M
-Djava.awt.headless=true
```

### 4. **ProxyClientStatus** - State Management
Comprehensive status enumeration system:

#### **Status States:**
- **STARTING**: Client initialization phase
- **RUNNING**: Active and responsive client
- **IDLE**: Connected but inactive
- **WORKING**: Actively performing tasks
- **RECONNECTING**: Network reconnection attempt
- **CRASHED**: Error or crash state
- **STOPPING**: Graceful shutdown process
- **STOPPED**: Fully terminated
- **UNKNOWN**: Indeterminate state

#### **Color-Coded Status System:**
Each status has an associated color for UI display:
- 🟢 RUNNING (Green)
- 🟡 WORKING (Orange)  
- 🔵 STARTING (Blue)
- 🟡 RECONNECTING (Yellow)
- 🔴 CRASHED (Red)
- ⚪ STOPPED (Gray)

### 5. **ProxyClientInfo** - Monitoring Data
Rich monitoring and diagnostic information:

#### **Performance Metrics:**
- CPU usage percentage and trends
- Memory consumption and allocation
- Network connection status
- Uptime and availability statistics

#### **Health Scoring System:**
Advanced health scoring (0-100 points):
- **Base Status Score**: 40 points for optimal states
- **Heartbeat Freshness**: 25 points for responsiveness
- **CPU Efficiency**: 15 points for resource usage
- **Memory Optimization**: 10 points for memory management
- **Connection Status**: 10 points for network health

#### **Health Grades:**
- **Excellent** (90-100): Peak performance
- **Good** (80-89): Optimal operation
- **Fair** (70-79): Acceptable performance
- **Poor** (60-69): Degraded operation
- **Critical** (<60): Requires intervention

## 💡 Key Features

### **Intelligent Process Management**
- **Automatic JVM Tuning**: Optimized garbage collection and memory management
- **Resource Monitoring**: Real-time CPU and memory tracking
- **Graceful Shutdown**: Proper cleanup and resource release
- **Crash Recovery**: Automatic restart on failures with exponential backoff

### **Dynamic Configuration**
- **Runtime Reconfiguration**: Update settings without restart
- **Profile-Based Configs**: Pre-defined performance profiles
- **Validation System**: Configuration validation with error reporting
- **Factory Methods**: Easy creation of common configurations

### **Advanced Health Monitoring**
- **Heartbeat System**: Regular connectivity checks
- **Multi-Metric Health**: CPU, memory, network, and process health
- **Predictive Analytics**: Health trend analysis and alerting
- **Auto-Recovery**: Automatic remediation for common issues

### **Mod Management Integration**
- **Dynamic Loading**: Hot-swap mods without client restart
- **Selective Deployment**: Deploy mods to specific clients only
- **Version Management**: Track and update mod versions
- **Conflict Resolution**: Automatic dependency management

## 🔧 Usage Examples

### **Basic Client Creation**
```java
// Create manager
ProxyClientManager manager = new ProxyClientManager(networkClient);

// Create account and config
ProxyAccount account = new ProxyAccount("uuid", "username", "accessToken");
ProxyClientConfig config = ProxyClientConfig.createDefault();

// Start client
manager.createProxyClient(account, config)
    .thenAccept(instance -> {
        System.out.println("Client started: " + instance.getAccount().getUsername());
    });
```

### **High-Performance Configuration**
```java
// Create high-performance config
ProxyClientConfig config = ProxyClientConfig.createHighPerformance();
config.setMemoryMB(4096);
config.setMaxCpuPercent(90);
config.addEnabledMod("baritone");
config.addEnabledMod("litematica");
```

### **Health Monitoring**
```java
// Get detailed client information
Optional<ProxyClientInfo> info = manager.getClientInfo(accountId);
info.ifPresent(clientInfo -> {
    System.out.println("Health Score: " + clientInfo.getHealthScore());
    System.out.println("Health Grade: " + clientInfo.getHealthGrade());
    System.out.println("Uptime: " + clientInfo.getUptimeFormatted());
    System.out.println("CPU: " + clientInfo.getCpuUsageFormatted());
    System.out.println("Memory: " + clientInfo.getMemoryUsageFormatted());
});
```

### **Mod Deployment**
```java
// Deploy mods to specific client
List<Path> mods = Arrays.asList(
    Paths.get("mods/baritone.jar"),
    Paths.get("mods/litematica.jar")
);

manager.deployMods(accountId, mods)
    .thenAccept(success -> {
        if (success) {
            System.out.println("Mods deployed successfully");
        }
    });
```

## 🔒 Security & Safety

### **Process Isolation**
- Each proxy client runs in its own JVM process
- Sandboxed execution with limited system access
- Resource limits to prevent system overload
- Proper cleanup on termination

### **Authentication Management**
- Secure access token storage and handling
- Token refresh capabilities
- Authentication validation before client start
- Encrypted communication channels

### **Error Handling**
- Comprehensive exception handling at all levels
- Graceful degradation on failures
- Automatic recovery mechanisms
- Detailed error logging and reporting

## 🚀 Performance Optimizations

### **JVM Tuning**
- G1 garbage collector for low-latency performance
- Optimized heap sizing based on available memory
- Advanced GC tuning for Minecraft workloads
- CPU affinity and NUMA awareness

### **Resource Management**
- Memory pooling and reuse
- Connection pooling for network operations
- Thread pool optimization
- Efficient data structures and algorithms

### **Monitoring Efficiency**
- Minimal overhead health checking
- Batch operations for multiple clients
- Asynchronous monitoring updates
- Smart polling intervals based on client state

## 📊 Monitoring & Metrics

### **Real-Time Dashboards**
- Client status overview
- Performance metrics visualization
- Health trend analysis
- Resource utilization graphs

### **Alerting System**
- Configurable health thresholds
- Automatic notifications for critical issues
- Performance degradation alerts
- Capacity planning warnings

### **Logging Integration**
- Structured logging with correlation IDs
- Performance metrics collection
- Error tracking and analysis
- Audit trail for all operations

## 🛠️ Integration Points

### **AppyProx Core Integration**
- Seamless integration with main AppyProx system
- Cluster coordination capabilities
- Task assignment and execution
- Real-time status synchronization

### **Xaeros Map Integration**
- Client position tracking on world map
- Visual status indicators
- Interactive client management
- Coordinate-based operations

### **Network Communication**
- WebSocket-based real-time communication
- REST API for management operations
- Event-driven status updates
- Secure command transmission

## 🔮 Future Extensions

### **Planned Enhancements**
- **Container Integration**: Docker/Kubernetes support
- **Cloud Deployment**: AWS/GCP integration
- **Advanced Analytics**: ML-based performance optimization
- **API Extensions**: GraphQL query interface
- **Plugin System**: Third-party extension support

### **Scalability Improvements**
- **Multi-Node Support**: Distributed client management
- **Load Balancing**: Intelligent client distribution
- **Auto-Scaling**: Dynamic client provisioning
- **Resource Optimization**: AI-driven resource allocation

This comprehensive Proxy Client Management System provides the foundation for sophisticated multi-account Minecraft automation with enterprise-grade reliability, monitoring, and management capabilities.