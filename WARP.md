# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## AppyProx - Minecraft Proxy System

AppyProx is a sophisticated Minecraft player proxy system inspired by Zenith Proxy, featuring multi-account clustering, intelligent automation, and centralized management capabilities.

## Development Commands

### Core Operations
```bash
# Start the proxy server
npm start

# Development mode with hot reloading
npm run dev

# Run tests
npm test

# Build for production
npm run build

# Create a backup with versioning
npm run backup

# List available backups
node scripts/backup.js list

# Create backup with custom message
node scripts/backup.js create "Feature implementation complete"
```

### Configuration Setup
```bash
# Copy default configurations for first-time setup
cp configs/default.json configs/config.json
cp configs/accounts.default.json configs/accounts.json
cp configs/clusters.default.json configs/clusters.json
```

### Testing and Development
```bash
# Run a single test file
npx jest path/to/test.js

# Run tests with coverage
npm test -- --coverage

# Development with nodemon (auto-restart on changes)
npm run dev
```

## Architecture Overview

AppyProx follows a modular, event-driven architecture with five core components that communicate via EventEmitter patterns:

### Core Components

1. **ProxyServer** (`src/proxy/`) - Core Minecraft protocol handling
   - Manages client connections to the proxy
   - Handles packet forwarding between clients and target servers
   - Supports ViaVersion for multi-version compatibility

2. **ClusterManager** (`src/clustering/`) - Account grouping and coordination
   - Manages clusters of Minecraft accounts working together
   - Handles auto-assignment based on account configuration
   - Tracks member health, status, and leadership roles
   - Persists cluster state to `configs/clusters.json`

3. **AutomationEngine** (`src/automation/`) - AI-driven task automation
   - Uses WikiScraper to fetch Minecraft recipe/crafting data
   - TaskPlanner creates detailed execution plans for complex tasks
   - BaritoneInterface integrates with Baritone pathfinding mod
   - Supports tasks like resource gathering, building, and exploration

4. **CentralNode** (`src/central-node/`) - Management interface
   - Web interface on port 8080 for real-time monitoring
   - WebSocket on port 8081 for live updates
   - Integrates with Xaeros World Map for visualization
   - Tracks account health, inventory, and task progress

5. **AppyProxAPI** (`src/api/`) - REST API for external integration
   - RESTful endpoints for cluster and task management
   - Health checks and status monitoring
   - Programmatic access to all proxy functionality

### Component Communication Flow

The main entry point (`src/proxy/main.js`) orchestrates component lifecycle and establishes event-driven communication:

- ProxyServer events → ClusterManager (client registration)
- ClusterManager events → AutomationEngine (cluster updates)  
- AutomationEngine events → CentralNode (task progress)
- All components → Logger for centralized logging

### Configuration System

AppyProx uses a layered configuration approach:

- `configs/default.json` - Template with all available options
- `configs/config.json` - Runtime configuration (created from default)
- `configs/accounts.json` - Minecraft account credentials and cluster assignments
- `configs/clusters.json` - Persistent cluster definitions and state

### Key Design Patterns

- **Event-driven architecture**: All components extend EventEmitter
- **Configuration-driven**: Behavior controlled via JSON configs
- **Graceful shutdown**: Proper cleanup on SIGTERM/SIGINT
- **Backup versioning**: Semantic versioning with git tags (AppyProx-Alpha-x.y.z)
- **Health monitoring**: Regular health checks and reconnection logic

### Integration Points

- **Minecraft Protocol**: Uses minecraft-protocol library for packet handling
- **Baritone Mod**: Pathfinding and automated movement via BaritoneInterface
- **Litematica/Schematica**: Building automation with schematic support
- **Xaeros World Map**: Enhanced mapping and exploration tracking
- **ViaVersion**: Multi-version server compatibility

## Development Guidelines

### Adding New Task Types
When implementing new automation tasks in AutomationEngine:
1. Add task type to TaskPlanner for execution planning
2. Implement execution logic in AutomationEngine.executeTaskStep()
3. Update WikiScraper if new Minecraft data is needed
4. Add appropriate error handling and progress reporting

### Extending Cluster Functionality
For new cluster behaviors:
1. Add configuration options to cluster settings schema
2. Implement logic in ClusterManager event handlers
3. Update cluster state persistence in saveClustersConfig()
4. Add API endpoints if external access is needed

### Configuration Changes
- Always update `configs/default.json` with new options
- Maintain backward compatibility in config loading
- Document new settings in INSTALLATION.md
- Consider migration logic for existing installations

### Error Handling
- Use structured logging with component-specific child loggers
- Emit events for error conditions that other components need to handle
- Implement retry logic for network operations
- Ensure graceful degradation when components fail

## API Usage Examples

```javascript
// Creating a new cluster via API
const cluster = await fetch('http://localhost:3000/clusters', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'Custom Mining Group',
    options: { maxSize: 5, autoReconnect: true }
  })
});

// Starting a resource gathering task
const task = await fetch('http://localhost:3000/tasks', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    type: 'gather_resource',
    parameters: { resource: 'diamond', quantity: 64 },
    cluster: 'mining-cluster-id'
  })
});
```

## Common Development Scenarios

### Testing Cluster Behavior
1. Configure test accounts in `configs/accounts.json`
2. Create test clusters with `npm start` and API calls
3. Monitor cluster coordination via central node web interface
4. Check logs in `logs/appyprox.log` for detailed debugging

### Implementing New Automation Features
1. Study existing task types in AutomationEngine
2. Add WikiScraper methods for new Minecraft data if needed
3. Implement task execution logic with proper error handling
4. Test with small clusters before scaling up

### Debugging Connection Issues
1. Check proxy configuration in `configs/config.json`
2. Verify target server accessibility
3. Monitor client connection events in logs
4. Use health check endpoints to verify component status