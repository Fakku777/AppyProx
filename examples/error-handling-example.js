#!/usr/bin/env node

/**
 * AppyProx Error Handling System Example
 * 
 * This example demonstrates how to use the advanced error handling,
 * circuit breaker, and health monitoring features in AppyProx.
 */

const AppyProx = require('../src/proxy/main');
const path = require('path');

class ErrorHandlingExample {
  constructor() {
    this.appyProx = null;
  }

  async run() {
    try {
      console.log('🚀 Starting AppyProx Error Handling Example...\n');
      
      // Initialize AppyProx
      this.appyProx = new AppyProx();
      await this.appyProx.start();
      
      console.log('✅ AppyProx started successfully');
      console.log('📊 Error handling systems initialized\n');
      
      // Demonstrate various error handling features
      await this.demonstrateHealthMonitoring();
      await this.demonstrateCircuitBreaker();
      await this.demonstrateBackupAndRollback();
      await this.demonstrateErrorRecovery();
      await this.demonstrateCustomHealthChecks();
      
      // Show system status
      await this.showSystemStatus();
      
    } catch (error) {
      console.error('❌ Error in example:', error);
    }
  }

  async demonstrateHealthMonitoring() {
    console.log('🏥 === Health Monitoring Demo ===');
    
    // Get current health status
    const health = this.appyProx.getHealthStatus();
    console.log(`System Health: ${health.status}`);
    console.log(`Timestamp: ${new Date(health.timestamp).toISOString()}`);
    
    // Show component health
    console.log('\nComponent Health:');
    for (const [name, componentHealth] of Object.entries(health.components || {})) {
      const status = componentHealth.status;
      const emoji = status === 'healthy' ? '✅' : status === 'degraded' ? '⚠️' : '❌';
      console.log(`  ${emoji} ${name}: ${status}`);
      
      if (componentHealth.reason) {
        console.log(`      Reason: ${componentHealth.reason}`);
      }
    }
    
    // Get system metrics
    const metrics = this.appyProx.healthMonitor.getSystemMetrics();
    console.log('\nSystem Metrics:');
    console.log(`  CPU: ${metrics.cpu.current.toFixed(1)}% (avg: ${metrics.cpu.average.toFixed(1)}%)`);
    console.log(`  Memory: ${metrics.memory.current.toFixed(1)}% (avg: ${metrics.memory.average.toFixed(1)}%)`);
    console.log(`  Uptime: ${Math.floor(metrics.uptime / 3600)}h ${Math.floor((metrics.uptime % 3600) / 60)}m`);
    
    // Check for active alerts
    const activeAlerts = this.appyProx.healthMonitor.getActiveAlerts();
    if (activeAlerts.length > 0) {
      console.log('\n🚨 Active Health Alerts:');
      activeAlerts.forEach(alert => {
        console.log(`  - ${alert.severity.toUpperCase()}: ${alert.message}`);
      });
    } else {
      console.log('\n✅ No active health alerts');
    }
    
    console.log('');
  }

  async demonstrateCircuitBreaker() {
    console.log('🔌 === Circuit Breaker Demo ===');
    
    // Simulate a failing operation
    let attemptCount = 0;
    const simulateFailingOperation = async () => {
      attemptCount++;
      if (attemptCount <= 3) {
        throw new Error(`Simulated failure attempt ${attemptCount}`);
      }
      return `Success after ${attemptCount} attempts`;
    };
    
    const fallbackOperation = async () => {
      return 'Fallback result - circuit breaker is open';
    };
    
    try {
      console.log('Attempting operations that will fail initially...');
      
      // First few attempts will fail and eventually open the circuit
      for (let i = 1; i <= 5; i++) {
        try {
          const result = await this.appyProx.executeWithCircuitBreaker(
            'example_operation',
            simulateFailingOperation,
            fallbackOperation
          );
          console.log(`  Attempt ${i}: ${result}`);
        } catch (error) {
          console.log(`  Attempt ${i}: ${error.message}`);
        }
      }
      
    } catch (error) {
      console.log(`Circuit breaker example error: ${error.message}`);
    }
    
    // Show circuit breaker stats
    const circuitStats = this.appyProx.circuitBreaker.getGlobalStats();
    console.log('\nCircuit Breaker Statistics:');
    console.log(`  Total Breakers: ${circuitStats.totalBreakers}`);
    console.log(`  Open Breakers: ${circuitStats.openBreakers}`);
    console.log(`  Overall Success Rate: ${circuitStats.overallSuccessRate.toFixed(1)}%`);
    
    console.log('');
  }

  async demonstrateBackupAndRollback() {
    console.log('💾 === Backup and Rollback Demo ===');
    
    try {
      // Create a backup
      console.log('Creating system backup...');
      const backupId = await this.appyProx.createBackup('Example-Demo-Backup');
      console.log(`✅ Backup created: ${backupId}`);
      
      // Create a rollback point
      console.log('Creating rollback point...');
      await this.appyProx.errorRecovery.createRollbackPoint('demo-checkpoint');
      console.log('✅ Rollback point created: demo-checkpoint');
      
      // Simulate some changes (in a real scenario, these would be actual system changes)
      console.log('Simulating system changes...');
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Demonstrate rollback (commented out to avoid actually changing system state)
      // console.log('Rolling back to checkpoint...');
      // await this.appyProx.rollback('demo-checkpoint');
      // console.log('✅ Rollback completed');
      
      console.log('Note: Rollback demonstration skipped to preserve system state');
      
    } catch (error) {
      console.log(`Backup/Rollback error: ${error.message}`);
    }
    
    console.log('');
  }

  async demonstrateErrorRecovery() {
    console.log('🔧 === Error Recovery Demo ===');
    
    // Show error recovery statistics
    const errorStats = this.appyProx.getErrorRecoveryStats();
    console.log('Error Recovery Statistics:');
    console.log(`  Total Errors: ${errorStats.totalErrors}`);
    console.log(`  Recovered Errors: ${errorStats.recoveredErrors}`);
    console.log(`  Failed Recoveries: ${errorStats.failedRecoveries}`);
    console.log(`  Critical Errors: ${errorStats.criticalErrors}`);
    console.log(`  Rollbacks Executed: ${errorStats.rollbacksExecuted}`);
    
    if (errorStats.totalErrors > 0) {
      const recoveryRate = (errorStats.recoveredErrors / errorStats.totalErrors) * 100;
      console.log(`  Recovery Rate: ${recoveryRate.toFixed(1)}%`);
      console.log(`  Average Recovery Time: ${errorStats.averageRecoveryTime}ms`);
    }
    
    // Show detected error patterns
    const errorPatterns = Object.entries(errorStats.errorTypes || {});
    if (errorPatterns.length > 0) {
      console.log('\nDetected Error Patterns:');
      errorPatterns.forEach(([type, pattern]) => {
        console.log(`  ${type}: ${pattern.totalCount} occurrences (${pattern.frequency.toFixed(1)}/hour)`);
      });
    }
    
    console.log('');
  }

  async demonstrateCustomHealthChecks() {
    console.log('🔍 === Custom Health Checks Demo ===');
    
    // Add a custom health check
    this.appyProx.healthMonitor.addCustomHealthCheck('example_service', async () => {
      // Simulate checking an external service
      const isHealthy = Math.random() > 0.3; // 70% chance of being healthy
      const responseTime = Math.floor(Math.random() * 1000) + 100;
      
      return {
        status: isHealthy ? 'healthy' : 'degraded',
        lastCheck: Date.now(),
        details: {
          responseTime,
          service: 'example-external-service',
          version: '1.0.0'
        },
        reason: isHealthy ? null : 'Slow response time detected'
      };
    });
    
    console.log('✅ Added custom health check: example_service');
    
    // Wait for next health check cycle
    console.log('Waiting for next health check cycle...');
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // Check the results
    const healthSummary = this.appyProx.healthMonitor.getHealthSummary();
    const customCheck = healthSummary.components['example_service'];
    
    if (customCheck) {
      console.log(`Custom Health Check Result: ${customCheck.status}`);
      if (customCheck.details) {
        console.log(`  Response Time: ${customCheck.details.responseTime}ms`);
        console.log(`  Service: ${customCheck.details.service}`);
      }
    }
    
    // Remove the custom health check
    this.appyProx.healthMonitor.removeCustomHealthCheck('example_service');
    console.log('✅ Removed custom health check');
    
    console.log('');
  }

  async showSystemStatus() {
    console.log('📈 === System Status Summary ===');
    
    const status = this.appyProx.getStatus();
    
    console.log(`System Running: ${status.running ? '✅' : '❌'}`);
    
    // Show component statuses
    console.log('\nCore Components:');
    const components = ['proxy', 'clusters', 'automation', 'centralNode'];
    components.forEach(component => {
      if (status[component]) {
        const componentStatus = status[component];
        console.log(`  ${component}: ${componentStatus.running || componentStatus.status || 'active'}`);
      }
    });
    
    // Show error handling status
    if (status.errorHandling) {
      console.log('\nError Handling Systems:');
      
      if (status.errorHandling.healthMonitor) {
        const health = status.errorHandling.healthMonitor;
        console.log(`  Health Monitor: ${health.overall} (${health.alerts.active} active alerts)`);
      }
      
      if (status.errorHandling.circuitBreaker) {
        const cb = status.errorHandling.circuitBreaker;
        console.log(`  Circuit Breakers: ${cb.closedBreakers}/${cb.totalBreakers} healthy`);
      }
      
      if (status.errorHandling.errorRecovery) {
        const er = status.errorHandling.errorRecovery;
        console.log(`  Error Recovery: ${er.recoveredErrors}/${er.totalErrors} recovered`);
      }
    }
    
    console.log('\n✨ Error handling demonstration completed!');
    console.log('\nThe system is now running with full error handling capabilities:');
    console.log('  • Automatic error detection and recovery');
    console.log('  • Circuit breaker protection for external operations');
    console.log('  • Continuous health monitoring with alerts');
    console.log('  • Backup and rollback capabilities');
    console.log('  • Pattern detection for proactive measures');
    
    console.log('\n📚 Check docs/ERROR_HANDLING.md for detailed documentation');
    console.log('🔗 Access the web interface for real-time monitoring');
  }

  async cleanup() {
    if (this.appyProx) {
      console.log('\n🧹 Cleaning up...');
      await this.appyProx.stop();
      console.log('✅ Cleanup completed');
    }
  }
}

// Run the example if this file is executed directly
if (require.main === module) {
  const example = new ErrorHandlingExample();
  
  // Setup graceful shutdown
  const gracefulShutdown = async (signal) => {
    console.log(`\n📡 Received ${signal}, shutting down gracefully...`);
    await example.cleanup();
    process.exit(0);
  };
  
  process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));
  process.on('SIGINT', () => gracefulShutdown('SIGINT'));
  
  example.run().catch(async (error) => {
    console.error('❌ Example failed:', error);
    await example.cleanup();
    process.exit(1);
  });
}

module.exports = ErrorHandlingExample;