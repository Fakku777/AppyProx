#!/usr/bin/env node

/**
 * AppyProx Demo - Standalone demonstration of the complete system
 * This shows all components working together without external dependencies
 */

const AppyProx = require('./src/proxy/main.js');

// Track cleanup state and intervals
let isShuttingDown = false;
const intervals = [];
const timeouts = [];

// Helper to manage intervals that can be cleaned up
function managedSetInterval(callback, delay) {
  const id = setInterval(callback, delay);
  intervals.push(id);
  return id;
}

function managedSetTimeout(callback, delay) {
  const id = setTimeout(callback, delay);
  timeouts.push(id);
  return id;
}

// Override configuration for demo mode
const originalLoadConfig = AppyProx.prototype.loadConfig;
AppyProx.prototype.loadConfig = function() {
  const config = originalLoadConfig.call(this);
  
  // Disable Java bridge for standalone demo
  if (config.proxy_client_bridge) {
    config.proxy_client_bridge.enabled = false;
  }
  
  return config;
};

console.log('🚀 Starting AppyProx Demo System...\n');

const appyProx = new AppyProx();

// Disable the built-in signal handlers to prevent conflicts
appyProx.setupGracefulShutdown = function() {
  // Override to prevent duplicate signal handlers
  // We'll handle shutdown in our demo script
};

// Demo: Show system starting
console.log('📋 AppyProx Components:');
console.log('  ✅ ProxyServer - Handles Minecraft connections');
console.log('  ✅ ClusterManager - Coordinates multiple accounts');
console.log('  ✅ AutomationEngine - AI-powered task execution');
console.log('  ✅ CentralNode - Web management interface');
console.log('  ✅ AppyProxAPI - REST API for external control');
console.log('  ⚙️  ProxyClientBridge - Java integration (disabled for demo)');
console.log();

appyProx.start().then(() => {
  console.log('\n🎉 AppyProx Demo System is now running!\n');
  
  console.log('📊 Available Interfaces:');
  console.log(`  🌐 API Server: http://localhost:3000`);
  console.log(`  🖥️  Central Node: http://localhost:25577`);
  console.log(`  🎯 Minecraft Proxy: localhost:25565`);
  console.log();
  
  console.log('🔧 Test the system:');
  console.log(`  curl http://localhost:3000/health`);
  console.log(`  curl http://localhost:3000/status`);
  console.log(`  curl http://localhost:3000/clusters`);
  console.log();
  
  // Demo: Simulate rich activity data for Central Node
  managedSetTimeout(() => {
    if (isShuttingDown) return;
    
    console.log('📡 Simulating comprehensive cluster activity...');
    
    if (appyProx.clusterManager) {
      // Simulate multiple clients connecting
      const mockClients = [
        {
          id: 'demo_client_1',
          username: 'DiamondMiner01',
          uuid: '550e8400-e29b-41d4-a716-446655440000',
          remoteAddress: '127.0.0.1',
          version: '1.20.4',
          status: 'mining'
        },
        {
          id: 'demo_client_2', 
          username: 'BuilderBot',
          uuid: '550e8400-e29b-41d4-a716-446655440001',
          remoteAddress: '127.0.0.1',
          version: '1.20.4',
          status: 'building'
        },
        {
          id: 'demo_client_3',
          username: 'ExplorerAI',
          uuid: '550e8400-e29b-41d4-a716-446655440002', 
          remoteAddress: '127.0.0.1',
          version: '1.20.4',
          status: 'exploring'
        }
      ];
      
      // Register all demo clients
      mockClients.forEach(client => {
        appyProx.clusterManager.registerClient(client);
        
        // Add client data to Central Node
        if (appyProx.centralNode) {
          appyProx.centralNode.updateAccountStatus({
            clientId: client.id,
            username: client.username,
            status: client.status,
            health: Math.floor(Math.random() * 20) + 80,
            hunger: Math.floor(Math.random() * 20) + 80,
            experience: Math.floor(Math.random() * 100),
            position: {
              x: Math.floor(Math.random() * 200) - 100,
              y: Math.floor(Math.random() * 50) + 10,
              z: Math.floor(Math.random() * 200) - 100
            },
            inventory: {
              diamond: Math.floor(Math.random() * 32),
              iron: Math.floor(Math.random() * 64),
              stone: Math.floor(Math.random() * 128)
            }
          });
        }
      });
      
      console.log('🤖 Demo clients registered with cluster manager');
    }
    
    // Simulate active tasks
    if (appyProx.automationEngine && appyProx.centralNode) {
      const demoTasks = [
        {
          taskId: 'task_diamond_mining',
          type: 'gather_diamond',
          accountId: 'demo_client_1',
          status: 'in_progress',
          progress: 65,
          parameters: { quantity: 32, method: 'branch_mining' },
          startTime: Date.now() - 300000, // Started 5 minutes ago
          estimatedCompletion: Date.now() + 180000 // 3 minutes remaining
        },
        {
          taskId: 'task_castle_build',
          type: 'build_castle',
          accountId: 'demo_client_2', 
          status: 'in_progress',
          progress: 30,
          parameters: { schematic: 'medieval_castle.litematic', location: { x: 100, y: 64, z: 100 } },
          startTime: Date.now() - 600000, // Started 10 minutes ago
          estimatedCompletion: Date.now() + 1200000 // 20 minutes remaining
        },
        {
          taskId: 'task_cave_explore',
          type: 'explore_cave',
          accountId: 'demo_client_3',
          status: 'in_progress', 
          progress: 80,
          parameters: { area: 'deep_caves', radius: 50, objectives: ['map_area', 'gather_resources'] },
          startTime: Date.now() - 900000, // Started 15 minutes ago
          estimatedCompletion: Date.now() + 300000 // 5 minutes remaining
        },
        {
          taskId: 'task_auto_farm',
          type: 'establish_farm',
          accountId: 'demo_client_1',
          status: 'completed',
          progress: 100,
          parameters: { crops: ['wheat', 'carrots'], size: '16x16' },
          startTime: Date.now() - 1800000, // Started 30 minutes ago
          completedTime: Date.now() - 300000 // Completed 5 minutes ago
        }
      ];
      
      // Update Central Node with task progress
      demoTasks.forEach(task => {
        appyProx.centralNode.updateTaskProgress(task);
        
        appyProx.automationEngine.emit('task_progress', task);
      });
      
      console.log('⚡ Demo automation tasks simulated');
    }
    
    // Update system statistics 
    if (appyProx.centralNode) {
      appyProx.centralNode.systemStats.totalConnections = 3;
      appyProx.centralNode.systemStats.activeConnections = 3;
    }
    
  }, 3000);
  
  // Simulate ongoing task progress updates
  const progressUpdateInterval = managedSetInterval(() => {
    if (isShuttingDown) return;
    
    if (appyProx.centralNode) {
      // Update task progress periodically
      const tasks = Array.from(appyProx.centralNode.taskProgress.values());
      tasks.forEach(task => {
        if (task.status === 'in_progress' && task.progress < 100) {
          task.progress = Math.min(task.progress + Math.floor(Math.random() * 5) + 1, 100);
          if (task.progress >= 100) {
            task.status = 'completed';
            task.completedTime = Date.now();
          }
          appyProx.centralNode.updateTaskProgress(task);
        }
      });
      
      // Update account positions (simulate movement)
      const accounts = Array.from(appyProx.centralNode.accountStatuses.values());
      accounts.forEach(account => {
        if (account.position) {
          account.position.x += Math.floor(Math.random() * 6) - 3; // Move -3 to +3 blocks
          account.position.z += Math.floor(Math.random() * 6) - 3;
          account.lastUpdate = Date.now();
        }
      });
    }
  }, 10000); // Update every 10 seconds
  
  // Simulate random events
  managedSetTimeout(() => {
    if (isShuttingDown) return;
    
    const randomEvents = [
      'DiamondMiner01 found a diamond vein!',
      'BuilderBot completed the castle foundation',
      'ExplorerAI discovered a new cave system',
      'Auto-farm produced 128 wheat'
    ];
    
    const randomEvent = randomEvents[Math.floor(Math.random() * randomEvents.length)];
    console.log(`🎆 Random event: ${randomEvent}`);
    
    // Add event to Central Node if there was an events system
  }, 15000);
  
  // Show system status periodically
  managedSetInterval(() => {
    if (isShuttingDown) return;
    
    try {
      const status = appyProx.getStatus();
      console.log('\n📈 System Status:');
      console.log(`  Running: ${status.running}`);
      console.log(`  Proxy: ${status.proxy ? 'Active' : 'Inactive'}`);
      console.log(`  Clusters: ${status.clusters ? Object.keys(status.clusters.clusters || {}).length : 0}`);
      console.log(`  API: ${status.api.enabled ? `Port ${status.api.port}` : 'Disabled'}`);
    } catch (error) {
      // Ignore errors during shutdown
    }
  }, 30000);
  
}).catch(error => {
  console.error('❌ Failed to start AppyProx Demo:', error.message);
  process.exit(1);
});

// Cleanup function
function cleanup() {
  if (isShuttingDown) return;
  isShuttingDown = true;
  
  console.log('\n🛑 Shutting down AppyProx Demo...');
  
  // Clear all intervals and timeouts
  intervals.forEach(id => clearInterval(id));
  timeouts.forEach(id => clearTimeout(id));
  
  // Force shutdown after timeout
  const forceShutdownTimeout = setTimeout(() => {
    console.log('⚠️  Force shutdown timeout reached, exiting...');
    process.exit(1);
  }, 3000);
  
  // Try graceful shutdown
  Promise.race([
    appyProx.stop(),
    new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Shutdown timeout')), 2500)
    )
  ])
    .then(() => {
      clearTimeout(forceShutdownTimeout);
      console.log('👋 AppyProx Demo stopped cleanly');
      process.exit(0);
    })
    .catch(error => {
      clearTimeout(forceShutdownTimeout);
      console.log(`⚠️  Shutdown error (${error.message}), forcing exit...`);
      process.exit(0); // Still exit cleanly since this is expected in demo
    });
}

// Handle various shutdown signals
process.on('SIGINT', cleanup);
process.on('SIGTERM', cleanup);
process.on('SIGHUP', cleanup);

// Handle uncaught exceptions
process.on('uncaughtException', (error) => {
  console.error('❌ Uncaught Exception:', error);
  cleanup();
});

process.on('unhandledRejection', (reason, promise) => {
  console.error('❌ Unhandled Rejection at:', promise, 'reason:', reason);
  cleanup();
});
