# Error Handling and Recovery System

AppyProx includes a comprehensive error handling and recovery system that provides automatic error detection, recovery mechanisms, circuit breaker patterns, and proactive health monitoring.

## Components Overview

### 1. ErrorRecoverySystem
The core error handling component that tracks errors, attempts automatic recovery, manages backups, and provides rollback capabilities.

### 2. CircuitBreakerManager
Implements the circuit breaker pattern to prevent cascading failures by temporarily disabling failing operations.

### 3. HealthMonitor
Proactive monitoring system that tracks system and component health to prevent errors before they occur.

## Features

### Automatic Error Recovery
- **Multiple Recovery Strategies**: Component restart, client reconnection, task retry, cluster rebuild, configuration reset, deployment rollback
- **Exponential Backoff**: Intelligent retry timing to avoid overwhelming failing systems
- **Recovery Statistics**: Track success rates and recovery times
- **Pattern Detection**: Identify recurring error patterns for preventive measures

### Circuit Breaker Protection
- **Failure Thresholds**: Configurable failure counts before circuit opens
- **Recovery Timeouts**: Automatic attempts to close circuits after cooldown periods
- **Fallback Mechanisms**: Alternative operations when circuits are open
- **Real-time Statistics**: Monitor request success/failure rates and response times

### Health Monitoring
- **System Metrics**: CPU, memory, disk usage monitoring
- **Component Health**: Individual component status tracking
- **Alert System**: Configurable thresholds with cooldown periods
- **Trend Analysis**: Memory leak detection and performance degradation alerts
- **Custom Health Checks**: Extensible health check framework

### Backup and Rollback
- **Automatic Backups**: System state and configuration snapshots
- **Emergency Backups**: Created before critical operations
- **Rollback Points**: Named restore points for easy recovery
- **Retention Management**: Automatic cleanup of old backups

## Configuration

Add error handling configuration to your `config.json`:

```json
{
  "errorHandling": {
    "maxRetryAttempts": 3,
    "retryBackoffMultiplier": 2,
    "initialRetryDelay": 1000,
    "maxRetryDelay": 60000,
    "errorHistoryLimit": 1000,
    "patternAnalysisWindow": 3600000,
    "autoRecoveryEnabled": true,
    "rollbackEnabled": true,
    "backupRetentionDays": 7
  },
  "healthMonitoring": {
    "checkInterval": 30000,
    "alertThresholds": {
      "cpu": 85,
      "memory": 90,
      "disk": 95,
      "responseTime": 5000,
      "errorRate": 10
    },
    "degradationThresholds": {
      "cpu": 70,
      "memory": 75,
      "disk": 80,
      "responseTime": 2000,
      "errorRate": 5
    },
    "historySize": 100,
    "alertCooldown": 300000
  }
}
```

## Usage Examples

### Basic Error Handling
Errors are automatically tracked and recovery is attempted:

```javascript
// Errors from any component are automatically handled
try {
  await someOperation();
} catch (error) {
  // Error is automatically tracked and recovery attempted
  // No manual intervention required
}
```

### Circuit Breaker Usage
Protect operations from cascading failures:

```javascript
const appyProx = new AppyProx();
await appyProx.start();

// Execute operation with circuit breaker protection
try {
  const result = await appyProx.executeWithCircuitBreaker(
    'database_query',
    async () => {
      return await performDatabaseQuery();
    },
    async () => {
      // Fallback operation if circuit is open
      return await getFromCache();
    }
  );
} catch (error) {
  console.error('Operation failed even with circuit breaker:', error);
}
```

### Manual Backup and Rollback
Create backups and rollback when needed:

```javascript
// Create a backup before risky operations
const backupId = await appyProx.createBackup('Before-Update');

try {
  await performRiskyUpdate();
} catch (error) {
  // Rollback to previous state
  await appyProx.rollback('Before-Update');
}
```

### Health Monitoring
Monitor system health and react to alerts:

```javascript
// Get current health status
const health = appyProx.getHealthStatus();
console.log('System health:', health.status);
console.log('Component statuses:', health.components);

// Get comprehensive health summary
const summary = appyProx.healthMonitor.getHealthSummary();
console.log('CPU status:', summary.system.cpu.status);
console.log('Active alerts:', summary.alerts.active);
```

### Custom Health Checks
Add application-specific health checks:

```javascript
// Add custom health check
appyProx.healthMonitor.addCustomHealthCheck('minecraft_server_ping', async () => {
  try {
    const response = await pingMinecraftServer();
    return {
      status: response.online ? 'healthy' : 'unhealthy',
      lastCheck: Date.now(),
      details: {
        latency: response.latency,
        playerCount: response.players?.online || 0
      }
    };
  } catch (error) {
    return {
      status: 'unhealthy',
      error: error.message,
      lastCheck: Date.now()
    };
  }
});
```

## Error Recovery Strategies

### Component Restart
Automatically restarts failed components:
- Stops the component gracefully
- Waits for cleanup
- Restarts the component
- Verifies successful restart

### Client Reconnection
Handles connection failures:
- Detects client disconnections
- Attempts to reconnect clients
- Maintains session state where possible
- Reports connection status

### Task Retry
Retries failed automation tasks:
- Identifies retriable task failures
- Implements exponential backoff
- Tracks retry attempts
- Falls back to manual intervention if needed

### Cluster Rebuild
Reconstructs failed clusters:
- Detects cluster health issues
- Redistributes cluster members
- Rebuilds cluster coordination
- Maintains cluster state consistency

### Configuration Reset
Resets invalid configurations:
- Detects configuration errors
- Reverts to last known good configuration
- Validates configuration changes
- Maintains configuration history

### Deployment Rollback
Rolls back failed deployments:
- Detects deployment failures
- Reverts to previous deployment version
- Maintains deployment history
- Verifies rollback success

## Monitoring and Alerts

### System Alerts
- **High CPU Usage**: CPU > 85%
- **High Memory Usage**: Memory > 90%
- **High Disk Usage**: Disk > 95%
- **Performance Degradation**: Response time > 5s

### Component Alerts
- **Component Unhealthy**: Component not responding
- **Component Degraded**: Component performing poorly
- **Configuration Invalid**: Configuration validation failed
- **Connection Issues**: Network or database connectivity problems

### Error Pattern Alerts
- **High Error Frequency**: > 10 errors/hour
- **Recurring Failures**: Same error type repeating
- **Cascade Detection**: Multiple components failing together
- **Memory Leak Warning**: Steady memory increase

## Best Practices

### 1. Configure Appropriate Thresholds
```javascript
// Set thresholds based on your infrastructure
const config = {
  healthMonitoring: {
    alertThresholds: {
      cpu: 80,      // Lower for critical systems
      memory: 85,   // Leave headroom for spikes
      disk: 90,     // Prevent disk full errors
      errorRate: 5  // Strict error tolerance
    }
  }
};
```

### 2. Implement Custom Health Checks
```javascript
// Monitor application-specific metrics
appyProx.healthMonitor.addCustomHealthCheck('player_connections', async () => {
  const activeConnections = await getActivePlayerCount();
  return {
    status: activeConnections > 0 ? 'healthy' : 'warning',
    details: { activeConnections }
  };
});
```

### 3. Use Circuit Breakers for External Services
```javascript
// Protect against external service failures
const result = await appyProx.executeWithCircuitBreaker(
  'mojang_api',
  () => fetchFromMojangAPI(),
  () => getCachedPlayerData()
);
```

### 4. Create Strategic Rollback Points
```javascript
// Before major operations
await appyProx.createBackup('before-server-update');
await updateMinecraftServer();

// Before configuration changes
await appyProx.errorRecovery.createRollbackPoint('stable-config');
await updateConfiguration();
```

### 5. Monitor Recovery Statistics
```javascript
// Check system health regularly
const stats = appyProx.getErrorRecoveryStats();
console.log(`Recovery rate: ${(stats.recoveredErrors / stats.totalErrors * 100).toFixed(1)}%`);
console.log(`Average recovery time: ${stats.averageRecoveryTime}ms`);
```

## Troubleshooting

### High Error Rates
1. Check system resources (CPU, memory, disk)
2. Review error patterns for common causes
3. Verify network connectivity
4. Check component configurations

### Circuit Breakers Opening
1. Investigate underlying service health
2. Review failure thresholds
3. Check fallback mechanisms
4. Monitor service dependencies

### Health Alerts
1. Verify alert thresholds are appropriate
2. Check for resource constraints
3. Review component logs for details
4. Consider scaling resources if needed

### Recovery Failures
1. Check component restart mechanisms
2. Verify backup integrity
3. Review recovery strategy selection
4. Check for cascading failures

## Performance Impact

The error handling system is designed to be lightweight:
- **Memory Usage**: ~5-10MB for tracking and history
- **CPU Overhead**: <1% during normal operations
- **Storage**: Error logs and backups use ~100MB/day
- **Network**: Minimal impact, local operations only

## Integration with Existing Systems

The error handling system integrates seamlessly with:
- **Logging Systems**: All events are logged
- **Monitoring Tools**: Metrics exposed via API
- **Alerting Systems**: Events can trigger external alerts
- **Backup Systems**: Can integrate with external backup solutions

## API Endpoints

Health and error information is exposed via REST API:

```
GET /health              - Current system health
GET /health/history      - Health history
GET /errors/stats        - Error recovery statistics  
GET /errors/patterns     - Detected error patterns
GET /circuit-breakers    - Circuit breaker status
POST /backup             - Create manual backup
POST /rollback           - Execute rollback
```

This comprehensive error handling system ensures AppyProx maintains high availability and recovers gracefully from failures while providing detailed insights into system health and performance.