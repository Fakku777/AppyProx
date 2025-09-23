# AppyProx Integration Guide

## Overview

AppyProx now features a comprehensive Java-based Proxy Client Management System that seamlessly integrates with the existing JavaScript architecture. This integration enables advanced headless Minecraft client management with sophisticated automation capabilities.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    AppyProx JavaScript Core                     │
├─────────────────┬─────────────────┬─────────────────────────────┤
│ ProxyServer     │ ClusterManager  │ AutomationEngine            │
│ CentralNode     │ AppyProxAPI     │                             │
└─────────────────┴─────────────────┴─────────────────────────────┘
                           │
                  ┌────────┴────────┐
                  │ ProxyClientBridge│ ← JavaScript Bridge
                  └────────┬────────┘
                           │ TCP Socket (Port 25800)
                  ┌────────┴────────┐
                  │  AppyProxMain   │ ← Java Entry Point
                  └─────────────────┘
┌─────────────────────────────────────────────────────────────────┐
│              Java Proxy Client Management System               │
├──────────────────┬─────────────────┬────────────────────────────┤
│ ProxyClientManager│ LifecycleManager│ RemoteControl              │
│ ModDeployer      │ Dashboard       │ Integration                │
│ ProxyClientInstance                │ ProxyClientLauncher        │
└─────────────────────────────────────────────────────────────────┘
```

## Key Components

### JavaScript Side

1. **ProxyClientBridge** (`src/proxy/ProxyClientBridge.js`)
   - Manages communication between JavaScript and Java systems
   - Handles process spawning and monitoring
   - Provides API for proxy client operations

2. **Extended AppyProx Main** (`src/proxy/main.js`)
   - Integrated bridge initialization and lifecycle management
   - Event coordination between systems

3. **Enhanced API** (`src/api/index.js`)
   - New endpoints for proxy client management
   - Dashboard and status monitoring endpoints

### Java Side

1. **AppyProxMain** (`AppyProx-FabricMod/src/main/java/.../AppyProxMain.java`)
   - Entry point for Java system
   - Bridge communication handler
   - Command processing and event publishing

2. **Proxy Management System** (11 core components)
   - Complete headless Minecraft client management
   - Advanced automation and mod deployment
   - Real-time monitoring and health management

## Communication Protocol

The systems communicate via TCP socket using JSON messages:

### Command Format (JavaScript → Java)
```json
{
  "type": "COMMAND",
  "id": 123,
  "command": "START_CLIENT",
  "parameters": {
    "account": {...},
    "config": {...}
  }
}
```

### Response Format (Java → JavaScript)
```json
{
  "type": "COMMAND_RESPONSE", 
  "id": 123,
  "payload": {...},
  "error": null
}
```

### Event Format (Java → JavaScript)
```json
{
  "type": "CLIENT_EVENT",
  "payload": {
    "eventType": "CLIENT_REGISTERED",
    "accountId": "account_123",
    "message": "Client registered",
    "data": {...}
  }
}
```

## Available Commands

### START_CLIENT
Start a new proxy client instance
```json
{
  "command": "START_CLIENT",
  "parameters": {
    "account": {
      "id": "account_123",
      "username": "TestUser",
      "uuid": "...",
      "accessToken": "..."
    },
    "config": {
      "headless": true,
      "autoRestart": true,
      "maxMemoryMB": 1024
    }
  }
}
```

### STOP_CLIENT
Stop a proxy client instance
```json
{
  "command": "STOP_CLIENT", 
  "parameters": {
    "accountId": "account_123",
    "graceful": true
  }
}
```

### EXECUTE_TASK
Execute automation task on a client
```json
{
  "command": "EXECUTE_TASK",
  "parameters": {
    "accountId": "account_123",
    "task": {
      "type": "GATHER_RESOURCES",
      "parameters": {
        "resource": "diamond",
        "quantity": "64"
      }
    }
  }
}
```

## API Endpoints

### Proxy Client Management

- `GET /proxy-clients` - List connected proxy clients
- `POST /proxy-clients/start` - Start new proxy client
- `POST /proxy-clients/:id/stop` - Stop proxy client
- `GET /proxy-clients/:id/status` - Get client status
- `POST /proxy-clients/:id/execute-task` - Execute task on client
- `POST /proxy-clients/clusters/:id/execute-task` - Execute cluster task
- `GET /proxy-clients/dashboard` - Get dashboard data

### Example API Usage

#### Start a Proxy Client
```bash
curl -X POST http://localhost:3000/proxy-clients/start \
  -H "Content-Type: application/json" \
  -d '{
    "account": {
      "id": "test_123",
      "username": "TestBot",
      "uuid": "...",
      "accessToken": "..."
    },
    "config": {
      "headless": true,
      "autoRestart": true,
      "maxMemoryMB": 1024,
      "serverAddress": "play.hypixel.net",
      "serverPort": 25565
    }
  }'
```

#### Execute Automation Task
```bash
curl -X POST http://localhost:3000/proxy-clients/test_123/execute-task \
  -H "Content-Type: application/json" \
  -d '{
    "task": {
      "type": "GATHER_RESOURCES",
      "parameters": {
        "resource": "diamond",
        "quantity": "64"
      },
      "priority": 8
    }
  }'
```

## Configuration

Add to `configs/default.json`:

```json
{
  "proxy_client_bridge": {
    "enabled": true,
    "bridge_port": 25800,
    "java_executable": "java",
    "auto_build": true,
    "restart_on_failure": true,
    "max_restart_attempts": 5,
    "startup_timeout": 60000,
    "command_timeout": 30000
  }
}
```

## Setup Instructions

### 1. Build the Java System
```bash
cd AppyProx-FabricMod
./gradlew build
```

### 2. Start AppyProx
```bash
npm start
```

The system will automatically:
- Start the JavaScript core
- Launch the Proxy Client Bridge
- Spawn and connect to the Java system
- Initialize all proxy management components

### 3. Verify Integration
```bash
# Check system status
curl http://localhost:3000/status

# Check bridge status  
curl http://localhost:3000/proxy-clients/dashboard
```

## Supported Features

### Headless Client Management
- Full Minecraft client lifecycle management
- Automated startup, shutdown, and restart
- Health monitoring and recovery

### Mod Integration
- **Baritone** - Advanced pathfinding and automation
- **Litematica** - Schematic building automation  
- **Xaeros World Map** - Enhanced exploration tracking
- **Sodium/Lithium** - Performance optimization
- Dynamic mod deployment per client

### Automation Capabilities
- Resource gathering tasks
- Building automation with schematics
- Exploration and mapping
- Cluster coordination
- Custom task execution

### Monitoring & Control
- Real-time dashboard with metrics
- Health scoring and alerts
- Performance tracking
- Remote command execution
- WebSocket live updates

## Integration Benefits

1. **Unified Management** - Single interface for all proxy operations
2. **Scalability** - Java system handles multiple concurrent clients efficiently
3. **Advanced Automation** - Sophisticated task planning and execution
4. **Real-time Monitoring** - Comprehensive health and performance tracking
5. **Mod Support** - Dynamic deployment and configuration of client mods
6. **API Access** - Full programmatic control via REST API
7. **Event-Driven** - Real-time updates and coordination between systems

## Development

### Adding New Commands

1. **JavaScript Bridge** (`ProxyClientBridge.js`)
   - Add command method to public API
   - Handle response processing

2. **Java Main** (`AppyProxMain.java`)
   - Add case to `executeCommand()` switch
   - Implement command handler method

3. **API Endpoint** (`api/index.js`)
   - Add REST endpoint
   - Call bridge method

### Extending Automation

1. **Task Types** (`ProxyClientIntegration.java`)
   - Add new TaskType enum values
   - Implement execution logic

2. **Client Features** (`ProxyClientLauncher.java`)
   - Add mod configurations
   - Update deployment logic

## Troubleshooting

### Common Issues

1. **Bridge Connection Failed**
   - Check if port 25800 is available
   - Verify Java executable is in PATH
   - Check build output for errors

2. **Client Launch Failed**
   - Verify Minecraft authentication
   - Check mod compatibility
   - Review client logs

3. **Task Execution Failed**
   - Validate task parameters
   - Check client connectivity
   - Review automation engine logs

### Log Locations

- **JavaScript logs**: `logs/appyprox.log`
- **Java system logs**: Console output or configured log files
- **Client logs**: `proxy_clients/instances/{id}/logs/`

## Future Enhancements

1. **Web Interface** - Rich web dashboard for visual management
2. **Machine Learning** - Intelligent task optimization
3. **Multi-Server** - Support for multiple target servers
4. **Advanced Clustering** - Dynamic load balancing
5. **Plugin System** - Extensible mod and automation plugins

This integration provides a powerful foundation for advanced Minecraft proxy automation while maintaining the flexibility and extensibility of the original AppyProx architecture.