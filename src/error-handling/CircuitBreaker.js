const EventEmitter = require('events');

/**
 * Circuit Breaker Pattern Implementation
 * Prevents cascading failures by temporarily disabling failing operations
 */
class CircuitBreaker extends EventEmitter {
  constructor(name, options = {}) {
    super();
    this.name = name;
    this.options = {
      failureThreshold: 5,        // Number of failures before opening circuit
      recoveryTimeout: 30000,     // Time to wait before trying to close circuit (ms)
      monitoringPeriod: 60000,    // Period for monitoring failures (ms)
      successThreshold: 3,        // Number of successes needed to close circuit
      timeoutDuration: 5000,      // Timeout for protected operations (ms)
      ...options
    };
    
    // Circuit states: CLOSED, OPEN, HALF_OPEN
    this.state = 'CLOSED';
    this.failureCount = 0;
    this.successCount = 0;
    this.nextAttemptTime = 0;
    
    // Statistics
    this.stats = {
      totalRequests: 0,
      successfulRequests: 0,
      failedRequests: 0,
      rejectedRequests: 0,
      timeouts: 0,
      averageResponseTime: 0
    };
    
    // Failure tracking for the monitoring period
    this.failures = [];
    
    this.initialized = Date.now();
  }

  async execute(operation, fallback = null) {
    return new Promise(async (resolve, reject) => {
      this.stats.totalRequests++;
      
      // Check if circuit is open
      if (this.state === 'OPEN') {
        if (Date.now() < this.nextAttemptTime) {
          this.stats.rejectedRequests++;
          this.emit('request_rejected', {
            name: this.name,
            state: this.state,
            nextAttemptTime: this.nextAttemptTime
          });
          
          if (fallback && typeof fallback === 'function') {
            try {
              const fallbackResult = await fallback();
              resolve(fallbackResult);
              return;
            } catch (fallbackError) {
              reject(new Error(`Circuit breaker open, fallback failed: ${fallbackError.message}`));
              return;
            }
          } else {
            reject(new Error(`Circuit breaker open for ${this.name}, next attempt at ${new Date(this.nextAttemptTime)}`));
            return;
          }
        } else {
          // Time to try half-open
          this.state = 'HALF_OPEN';
          this.successCount = 0;
          this.emit('state_change', { name: this.name, from: 'OPEN', to: 'HALF_OPEN' });
        }
      }
      
      // Execute the operation with timeout
      const startTime = Date.now();
      let timeoutHandle;
      
      const timeoutPromise = new Promise((_, timeoutReject) => {
        timeoutHandle = setTimeout(() => {
          timeoutReject(new Error(`Operation timeout after ${this.options.timeoutDuration}ms`));
        }, this.options.timeoutDuration);
      });
      
      try {
        const result = await Promise.race([operation(), timeoutPromise]);
        
        clearTimeout(timeoutHandle);
        
        // Success
        this.onSuccess(Date.now() - startTime);
        resolve(result);
        
      } catch (error) {
        clearTimeout(timeoutHandle);
        
        // Check if it was a timeout
        if (error.message.includes('Operation timeout')) {
          this.stats.timeouts++;
        }
        
        // Failure
        this.onFailure(error);
        
        if (fallback && typeof fallback === 'function') {
          try {
            const fallbackResult = await fallback();
            resolve(fallbackResult);
          } catch (fallbackError) {
            reject(new Error(`Primary operation failed: ${error.message}, fallback failed: ${fallbackError.message}`));
          }
        } else {
          reject(error);
        }
      }
    });
  }

  onSuccess(responseTime) {
    this.stats.successfulRequests++;
    
    // Update average response time
    this.updateAverageResponseTime(responseTime);
    
    if (this.state === 'HALF_OPEN') {
      this.successCount++;
      
      if (this.successCount >= this.options.successThreshold) {
        this.close();
      }
    } else if (this.state === 'CLOSED') {
      // Reset failure count on success
      this.failureCount = 0;
      this.cleanupOldFailures();
    }
    
    this.emit('success', {
      name: this.name,
      state: this.state,
      responseTime,
      successCount: this.successCount
    });
  }

  onFailure(error) {
    this.stats.failedRequests++;
    this.failureCount++;
    
    // Track failure with timestamp
    this.failures.push({
      timestamp: Date.now(),
      error: error.message || error.toString()
    });
    
    // Clean up old failures outside monitoring period
    this.cleanupOldFailures();
    
    // Check if we should open the circuit
    if (this.state === 'CLOSED' || this.state === 'HALF_OPEN') {
      const recentFailures = this.failures.length;
      
      if (recentFailures >= this.options.failureThreshold) {
        this.open();
      }
    }
    
    this.emit('failure', {
      name: this.name,
      state: this.state,
      error: error.message || error.toString(),
      failureCount: this.failureCount,
      recentFailures: this.failures.length
    });
  }

  open() {
    if (this.state !== 'OPEN') {
      const previousState = this.state;
      this.state = 'OPEN';
      this.nextAttemptTime = Date.now() + this.options.recoveryTimeout;
      
      this.emit('state_change', {
        name: this.name,
        from: previousState,
        to: 'OPEN',
        nextAttemptTime: this.nextAttemptTime,
        reason: `${this.failures.length} failures in ${this.options.monitoringPeriod}ms`
      });
      
      this.emit('circuit_opened', {
        name: this.name,
        failureCount: this.failureCount,
        recentFailures: this.failures.length,
        recoveryTime: this.nextAttemptTime
      });
    }
  }

  close() {
    if (this.state !== 'CLOSED') {
      const previousState = this.state;
      this.state = 'CLOSED';
      this.failureCount = 0;
      this.successCount = 0;
      this.failures = [];
      
      this.emit('state_change', {
        name: this.name,
        from: previousState,
        to: 'CLOSED'
      });
      
      this.emit('circuit_closed', {
        name: this.name,
        successCount: this.successCount
      });
    }
  }

  halfOpen() {
    if (this.state === 'OPEN') {
      this.state = 'HALF_OPEN';
      this.successCount = 0;
      
      this.emit('state_change', {
        name: this.name,
        from: 'OPEN',
        to: 'HALF_OPEN'
      });
    }
  }

  forceOpen() {
    this.state = 'OPEN';
    this.nextAttemptTime = Date.now() + this.options.recoveryTimeout;
    
    this.emit('circuit_forced_open', {
      name: this.name,
      nextAttemptTime: this.nextAttemptTime
    });
  }

  forceClose() {
    this.close();
    
    this.emit('circuit_forced_closed', {
      name: this.name
    });
  }

  cleanupOldFailures() {
    const cutoffTime = Date.now() - this.options.monitoringPeriod;
    this.failures = this.failures.filter(failure => failure.timestamp > cutoffTime);
  }

  updateAverageResponseTime(responseTime) {
    const totalSuccessful = this.stats.successfulRequests;
    if (totalSuccessful === 1) {
      this.stats.averageResponseTime = responseTime;
    } else {
      // Calculate running average
      this.stats.averageResponseTime = ((this.stats.averageResponseTime * (totalSuccessful - 1)) + responseTime) / totalSuccessful;
    }
  }

  getState() {
    return {
      name: this.name,
      state: this.state,
      failureCount: this.failureCount,
      successCount: this.successCount,
      nextAttemptTime: this.nextAttemptTime,
      recentFailures: this.failures.length,
      stats: { ...this.stats },
      failures: [...this.failures],
      uptime: Date.now() - this.initialized,
      isHealthy: this.state === 'CLOSED' && this.failures.length < this.options.failureThreshold / 2
    };
  }

  getStats() {
    const successRate = this.stats.totalRequests > 0 ? 
      (this.stats.successfulRequests / this.stats.totalRequests) * 100 : 100;
    
    const errorRate = this.stats.totalRequests > 0 ? 
      (this.stats.failedRequests / this.stats.totalRequests) * 100 : 0;
    
    return {
      ...this.stats,
      successRate: parseFloat(successRate.toFixed(2)),
      errorRate: parseFloat(errorRate.toFixed(2)),
      state: this.state,
      isHealthy: this.getState().isHealthy
    };
  }

  reset() {
    this.state = 'CLOSED';
    this.failureCount = 0;
    this.successCount = 0;
    this.failures = [];
    this.nextAttemptTime = 0;
    
    // Reset stats
    this.stats = {
      totalRequests: 0,
      successfulRequests: 0,
      failedRequests: 0,
      rejectedRequests: 0,
      timeouts: 0,
      averageResponseTime: 0
    };
    
    this.emit('circuit_reset', { name: this.name });
  }
}

/**
 * Circuit Breaker Manager
 * Manages multiple circuit breakers for different operations
 */
class CircuitBreakerManager extends EventEmitter {
  constructor(logger) {
    super();
    this.logger = logger?.child ? logger.child('CircuitBreaker') : logger;
    this.breakers = new Map();
    this.globalStats = {
      totalBreakers: 0,
      openBreakers: 0,
      halfOpenBreakers: 0,
      closedBreakers: 0
    };
  }

  createBreaker(name, options = {}) {
    if (this.breakers.has(name)) {
      throw new Error(`Circuit breaker '${name}' already exists`);
    }
    
    const breaker = new CircuitBreaker(name, options);
    
    // Forward events
    breaker.on('state_change', (event) => {
      this.logger?.info(`Circuit breaker ${event.name}: ${event.from} -> ${event.to}`);
      this.emit('breaker_state_change', event);
      this.updateGlobalStats();
    });
    
    breaker.on('circuit_opened', (event) => {
      this.logger?.warn(`Circuit breaker opened: ${event.name} (${event.recentFailures} failures)`);
      this.emit('breaker_opened', event);
    });
    
    breaker.on('circuit_closed', (event) => {
      this.logger?.info(`Circuit breaker closed: ${event.name}`);
      this.emit('breaker_closed', event);
    });
    
    breaker.on('request_rejected', (event) => {
      this.logger?.debug(`Request rejected by circuit breaker: ${event.name}`);
      this.emit('request_rejected', event);
    });
    
    breaker.on('failure', (event) => {
      this.logger?.debug(`Circuit breaker failure: ${event.name} - ${event.error}`);
      this.emit('breaker_failure', event);
    });
    
    breaker.on('success', (event) => {
      this.logger?.debug(`Circuit breaker success: ${event.name}`);
      this.emit('breaker_success', event);
    });
    
    this.breakers.set(name, breaker);
    this.globalStats.totalBreakers++;
    this.updateGlobalStats();
    
    return breaker;
  }

  getBreaker(name) {
    return this.breakers.get(name);
  }

  removeBreaker(name) {
    const breaker = this.breakers.get(name);
    if (breaker) {
      breaker.removeAllListeners();
      this.breakers.delete(name);
      this.globalStats.totalBreakers--;
      this.updateGlobalStats();
      return true;
    }
    return false;
  }

  async execute(name, operation, fallback = null) {
    const breaker = this.breakers.get(name);
    if (!breaker) {
      throw new Error(`Circuit breaker '${name}' not found`);
    }
    
    return breaker.execute(operation, fallback);
  }

  updateGlobalStats() {
    this.globalStats.openBreakers = 0;
    this.globalStats.halfOpenBreakers = 0;
    this.globalStats.closedBreakers = 0;
    
    for (const breaker of this.breakers.values()) {
      switch (breaker.state) {
        case 'OPEN':
          this.globalStats.openBreakers++;
          break;
        case 'HALF_OPEN':
          this.globalStats.halfOpenBreakers++;
          break;
        case 'CLOSED':
          this.globalStats.closedBreakers++;
          break;
      }
    }
  }

  getAllBreakers() {
    const result = [];
    for (const breaker of this.breakers.values()) {
      result.push(breaker.getState());
    }
    return result;
  }

  getGlobalStats() {
    this.updateGlobalStats();
    
    const allStats = Array.from(this.breakers.values()).map(b => b.getStats());
    const totalRequests = allStats.reduce((sum, stats) => sum + stats.totalRequests, 0);
    const totalSuccessful = allStats.reduce((sum, stats) => sum + stats.successfulRequests, 0);
    const totalFailed = allStats.reduce((sum, stats) => sum + stats.failedRequests, 0);
    const totalRejected = allStats.reduce((sum, stats) => sum + stats.rejectedRequests, 0);
    
    return {
      ...this.globalStats,
      totalRequests,
      totalSuccessful,
      totalFailed,
      totalRejected,
      overallSuccessRate: totalRequests > 0 ? (totalSuccessful / totalRequests) * 100 : 100,
      overallErrorRate: totalRequests > 0 ? (totalFailed / totalRequests) * 100 : 0,
      averageResponseTime: allStats.length > 0 ? 
        allStats.reduce((sum, stats) => sum + stats.averageResponseTime, 0) / allStats.length : 0
    };
  }

  getHealthyBreakers() {
    return Array.from(this.breakers.values())
      .filter(breaker => breaker.getState().isHealthy)
      .map(breaker => breaker.getState());
  }

  getUnhealthyBreakers() {
    return Array.from(this.breakers.values())
      .filter(breaker => !breaker.getState().isHealthy)
      .map(breaker => breaker.getState());
  }

  resetAllBreakers() {
    for (const breaker of this.breakers.values()) {
      breaker.reset();
    }
    this.updateGlobalStats();
    this.emit('all_breakers_reset');
  }

  // Create common circuit breakers for AppyProx components
  initializeDefaultBreakers() {
    // Database operations
    this.createBreaker('database', {
      failureThreshold: 5,
      recoveryTimeout: 30000,
      timeoutDuration: 10000
    });
    
    // Minecraft server connections
    this.createBreaker('minecraft_server', {
      failureThreshold: 3,
      recoveryTimeout: 60000,
      timeoutDuration: 15000
    });
    
    // API calls
    this.createBreaker('api_calls', {
      failureThreshold: 10,
      recoveryTimeout: 20000,
      timeoutDuration: 5000
    });
    
    // File operations
    this.createBreaker('file_operations', {
      failureThreshold: 5,
      recoveryTimeout: 10000,
      timeoutDuration: 3000
    });
    
    // External services
    this.createBreaker('external_services', {
      failureThreshold: 3,
      recoveryTimeout: 45000,
      timeoutDuration: 8000
    });
    
    // Task execution
    this.createBreaker('task_execution', {
      failureThreshold: 5,
      recoveryTimeout: 30000,
      timeoutDuration: 30000
    });
    
    this.logger?.info('Default circuit breakers initialized');
  }
}

module.exports = { CircuitBreaker, CircuitBreakerManager };