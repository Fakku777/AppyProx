# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

AppyProx is an advanced Minecraft proxy server with clustering, automation, and centralized management capabilities. Built in Node.js, it provides intelligent bot coordination, error handling, and integration with Minecraft mods like Baritone and Litematica.

## Common Development Commands

### Installation and Setup
```bash
npm install
```

### Running the Application
```bash
# Start the proxy server
npm start

# Development mode with auto-restart
npm run dev

# Production build
npm run build
```

### Testing
```bash
# Run tests (Jest)
npm test
```

### Backup and Maintenance
```bash
# Create a backup
npm run backup

# Create backup with custom message
node scripts/backup.js create "Custom backup message"

# List available backups
node scripts/backup.js list
```

### Configuration Management
```bash
# Configure port mappings
node scripts/configure-ports.js

# The main configuration is in configs/config.json
# Default configurations are in configs/default.json
```

## Architecture Overview

### Core Components

**Main Application (`src/proxy/main.js`)**
- Entry point that orchestrates all system components
- Handles graceful startup/shutdown with proper dependency order
- Manages component interconnections and event routing
- Implements comprehensive error handling and recovery systems

**Proxy Server (`src/proxy/ProxyServer.js`)**
- Core Minecraft protocol handler using `minecraft-protocol` library
- Manages client connections and packet forwarding
- Integrates with ViaVersion for multi-version support
- Provides connection statistics and monitoring

**Clustering System (`src/clustering/ClusterManager.js`)**
- Groups Minecraft accounts for coordinated activities
- Automatic cluster assignment based on account configuration
- Health monitoring and cluster rebalancing
- Persistent cluster state management in `configs/clusters.json`

**Automation Engine (`src/automation/AutomationEngine.js`)**
- Intelligent task planning using Minecraft Wiki data scraping
- Integration with Baritone for pathfinding and automated actions
- Support for Litematica schematic building
- Complex task analysis with resource optimization
- Task queue management with priority and retry logic

### Supporting Systems

**Error Handling System**
- `ErrorRecoverySystem`: Automatic error detection and recovery
- `CircuitBreakerManager`: Prevents cascading failures
- `HealthMonitor`: Proactive system health monitoring
- Comprehensive backup and rollback capabilities

**Configuration Management**
- Centralized configuration in `configs/` directory
- Dynamic configuration reloading
- Account management through `configs/accounts.json`
- Profile-based configurations

**API and Web Interface**
- RESTful API server (`src/api/index.js`)
- Web UI server for management interface
- Real-time WebSocket communication
- Health and metrics endpoints

**Deployment System**
- Load balancing capabilities
- Deployment management and rollback
- Java-based proxy client bridge integration

### Key Integrations

**Minecraft Mods**
- **Baritone**: Automated pathfinding and building
- **Litematica**: Schematic loading and construction
- **ViaVersion**: Multi-version protocol support
- **Xaero's Minimap**: Map integration for central node

**External Services**
- Minecraft Wiki scraping for automation intelligence
- Mojang API integration for authentication
- Real-time monitoring and alerting systems

## Configuration Files

- `configs/config.json` - Main configuration (created from default on first run)
- `configs/default.json` - Default configuration template  
- `configs/accounts.json` - Account credentials and cluster assignments
- `configs/clusters.json` - Persistent cluster state
- `configs/profiles.json` - Bot behavior profiles
- `configs/automation.json` - Automation task configurations
- `configs/api.json` - API server settings
- `configs/system.json` - System-level configurations

## Development Notes

### Component Startup Order
The application follows a specific initialization sequence:
1. Configuration loading and validation
2. Error handling systems (CircuitBreaker, ErrorRecovery, HealthMonitor)
3. Core proxy server
4. Cluster manager
5. Automation engine
6. API and Web UI servers
7. Component interconnections

### Error Handling Philosophy
- All components implement graceful error recovery
- Circuit breakers prevent cascading failures
- Health monitoring enables proactive intervention
- Comprehensive logging and metrics collection
- Automatic backup creation before critical operations

### Task System Architecture
Tasks in the automation engine support:
- Complex multi-step execution plans
- Resource requirement analysis
- Cluster coordination
- Progress tracking and resumption
- Intelligent failure recovery

### Configuration Patterns
- Configurations cascade from defaults to environment-specific overrides
- Hot-reloading supported for most configuration changes
- Validation ensures configuration integrity
- Backup configurations maintained automatically

### Mod Integration Approach
- Plugin-based architecture for mod integrations
- Standardized interfaces for common mod operations
- Version compatibility management through ViaVersion
- Graceful degradation when mods are unavailable

## Testing Approach

Jest is configured for testing but no test files currently exist in the main source tree. When writing tests:

- Place test files adjacent to source files with `.test.js` suffix
- Use integration tests for component interactions
- Mock external Minecraft connections for unit tests
- Test error recovery scenarios thoroughly

## Logging and Monitoring

- Structured logging with configurable levels
- Component-specific logger instances
- Health metrics exposed via API endpoints
- Performance statistics tracking
- Error pattern analysis and alerting

## Performance Considerations

- Connection pooling for Minecraft server connections  
- Efficient packet forwarding with minimal processing overhead
- Memory management for long-running automation tasks
- Resource usage monitoring and alerting
- Cluster load balancing for optimal distribution