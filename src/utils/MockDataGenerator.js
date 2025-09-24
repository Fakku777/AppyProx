/**
 * Mock Data Generator for testing the Xaeros interface
 * Generates realistic Minecraft proxy data for demonstration
 */
class MockDataGenerator {
  constructor() {
    this.startTime = Date.now();
    this.mockPlayers = this.generateMockPlayers();
    this.mockTasks = this.generateMockTasks();
    this.logBuffer = [];
    
    // Start simulation loops
    this.startPlayerMovement();
    this.startTaskProgress();
    this.startLogGeneration();
  }

  /**
   * Generate mock player accounts
   */
  generateMockPlayers() {
    const playerNames = [
      'MiningBot_Alpha', 'BuilderBot_Beta', 'ExplorerBot_Gamma',
      'FarmBot_Delta', 'CombatBot_Epsilon', 'RedstoneBot_Zeta'
    ];

    const statuses = ['mining', 'building', 'exploring', 'farming', 'idle'];
    const dimensions = ['overworld', 'nether', 'end'];

    return playerNames.map((name, index) => ({
      clientId: `client_${index + 1}`,
      username: name,
      status: statuses[index % statuses.length],
      dimension: dimensions[index % dimensions.length],
      health: 20,
      food: 20,
      level: Math.floor(Math.random() * 50) + 1,
      position: {
        x: (Math.random() - 0.5) * 2000, // Random position within 2000 blocks
        y: Math.random() * 64 + 64, // Y between 64-128
        z: (Math.random() - 0.5) * 2000
      },
      rotation: {
        yaw: Math.random() * 360,
        pitch: (Math.random() - 0.5) * 180
      },
      inventory: this.generateMockInventory(),
      lastUpdate: new Date().toISOString(),
      connected: true,
      ping: Math.floor(Math.random() * 100) + 20
    }));
  }

  /**
   * Generate mock inventory for a player
   */
  generateMockInventory() {
    const items = [
      'minecraft:diamond_pickaxe', 'minecraft:iron_sword', 'minecraft:bread',
      'minecraft:torch', 'minecraft:dirt', 'minecraft:cobblestone',
      'minecraft:coal', 'minecraft:iron_ingot', 'minecraft:diamond'
    ];

    const inventory = [];
    for (let i = 0; i < 36; i++) { // Standard inventory size
      if (Math.random() > 0.4) { // 60% chance of having an item
        inventory.push({
          slot: i,
          item: items[Math.floor(Math.random() * items.length)],
          count: Math.floor(Math.random() * 64) + 1,
          damage: Math.floor(Math.random() * 100)
        });
      }
    }
    return inventory;
  }

  /**
   * Generate mock automation tasks
   */
  generateMockTasks() {
    const taskTypes = [
      'gather_resource', 'build_structure', 'explore_area', 
      'farm_crops', 'mine_tunnel', 'collect_xp'
    ];

    const tasks = [];
    for (let i = 0; i < 3; i++) {
      tasks.push({
        taskId: `task_${Date.now()}_${i}`,
        type: taskTypes[i % taskTypes.length],
        status: Math.random() > 0.3 ? 'running' : 'paused',
        progress: Math.floor(Math.random() * 100),
        assignedBot: this.mockPlayers[i % this.mockPlayers.length].username,
        target: {
          resource: 'diamond_ore',
          quantity: 64,
          location: {
            x: Math.floor(Math.random() * 1000),
            z: Math.floor(Math.random() * 1000)
          }
        },
        startTime: new Date(Date.now() - Math.random() * 3600000).toISOString(),
        estimatedCompletion: new Date(Date.now() + Math.random() * 1800000).toISOString()
      });
    }
    return tasks;
  }

  /**
   * Start simulating player movement
   */
  startPlayerMovement() {
    setInterval(() => {
      this.mockPlayers.forEach(player => {
        if (player.connected && player.status !== 'idle') {
          // Simulate movement based on status
          const moveDistance = this.getMoveDistanceForStatus(player.status);
          const angle = Math.random() * 2 * Math.PI;
          
          player.position.x += Math.cos(angle) * moveDistance;
          player.position.z += Math.sin(angle) * moveDistance;
          
          // Occasional Y movement for mining/building
          if (player.status === 'mining') {
            player.position.y += (Math.random() - 0.7) * 2; // Tend to go down
          } else if (player.status === 'building') {
            player.position.y += (Math.random() - 0.3) * 3; // Tend to go up
          }
          
          // Keep within reasonable bounds
          player.position.x = Math.max(-2000, Math.min(2000, player.position.x));
          player.position.z = Math.max(-2000, Math.min(2000, player.position.z));
          player.position.y = Math.max(1, Math.min(256, player.position.y));
          
          // Update rotation
          player.rotation.yaw += (Math.random() - 0.5) * 30;
          player.rotation.yaw = player.rotation.yaw % 360;
          
          player.lastUpdate = new Date().toISOString();
        }
      });
    }, 3000); // Update every 3 seconds
  }

  /**
   * Get movement distance based on player status
   */
  getMoveDistanceForStatus(status) {
    switch (status) {
      case 'mining': return Math.random() * 5 + 1;
      case 'building': return Math.random() * 3 + 0.5;
      case 'exploring': return Math.random() * 15 + 5;
      case 'farming': return Math.random() * 8 + 2;
      default: return Math.random() * 2;
    }
  }

  /**
   * Start simulating task progress
   */
  startTaskProgress() {
    setInterval(() => {
      this.mockTasks.forEach(task => {
        if (task.status === 'running' && task.progress < 100) {
          task.progress += Math.random() * 5; // Random progress increase
          task.progress = Math.min(100, task.progress);
          
          if (task.progress >= 100) {
            task.status = 'completed';
            task.completionTime = new Date().toISOString();
          }
        }
      });
      
      // Occasionally add new tasks
      if (Math.random() < 0.1 && this.mockTasks.length < 6) {
        this.mockTasks.push(...this.generateMockTasks().slice(0, 1));
      }
      
      // Remove completed tasks after some time
      const now = Date.now();
      this.mockTasks = this.mockTasks.filter(task => {
        if (task.status === 'completed' && task.completionTime) {
          const completedTime = new Date(task.completionTime).getTime();
          return (now - completedTime) < 30000; // Keep for 30 seconds
        }
        return true;
      });
    }, 2000);
  }

  /**
   * Start generating log messages
   */
  startLogGeneration() {
    const logMessages = [
      { level: 'INFO', message: 'Player {player} started mining at {x}, {z}' },
      { level: 'INFO', message: 'Task {task} completed successfully' },
      { level: 'WARN', message: 'Player {player} health low: {health}/20' },
      { level: 'INFO', message: 'Found {item} at coordinates {x}, {y}, {z}' },
      { level: 'DEBUG', message: 'Pathfinding recalculated for {player}' },
      { level: 'ERROR', message: 'Connection timeout for {player}' },
      { level: 'INFO', message: 'Cluster coordination updated' },
      { level: 'WARN', message: 'Inventory almost full for {player}' }
    ];

    setInterval(() => {
      if (Math.random() < 0.7) { // 70% chance of generating a log
        const template = logMessages[Math.floor(Math.random() * logMessages.length)];
        const randomPlayer = this.mockPlayers[Math.floor(Math.random() * this.mockPlayers.length)];
        const randomTask = this.mockTasks[Math.floor(Math.random() * this.mockTasks.length)];
        
        let message = template.message
          .replace('{player}', randomPlayer.username)
          .replace('{task}', randomTask ? randomTask.taskId : 'task_unknown')
          .replace('{health}', randomPlayer.health)
          .replace('{item}', 'Diamond Ore')
          .replace('{x}', Math.round(randomPlayer.position.x))
          .replace('{y}', Math.round(randomPlayer.position.y))
          .replace('{z}', Math.round(randomPlayer.position.z));

        const logEntry = {
          timestamp: new Date().toISOString(),
          level: template.level,
          message: message,
          component: 'MockDataGenerator'
        };

        this.logBuffer.push(logEntry);
        
        // Keep only last 50 log entries
        if (this.logBuffer.length > 50) {
          this.logBuffer.shift();
        }
      }
    }, 4000); // Generate log every 4 seconds
  }

  /**
   * Get current mock data for API responses
   */
  getCurrentData() {
    return {
      status: {
        isRunning: true,
        uptime: Date.now() - this.startTime,
        connectedAccounts: this.mockPlayers.filter(p => p.connected).length,
        activeTasks: this.mockTasks.filter(t => t.status === 'running').length,
        totalConnections: this.mockPlayers.length,
        activeConnections: this.mockPlayers.filter(p => p.connected).length,
        webInterface: {
          port: 25577,
          enabled: true
        }
      },
      accounts: this.mockPlayers,
      tasks: this.mockTasks,
      logs: this.logBuffer
    };
  }

  /**
   * Simulate player connection/disconnection
   */
  togglePlayerConnection(username) {
    const player = this.mockPlayers.find(p => p.username === username);
    if (player) {
      player.connected = !player.connected;
      player.lastUpdate = new Date().toISOString();
      
      const logEntry = {
        timestamp: new Date().toISOString(),
        level: 'INFO',
        message: `Player ${username} ${player.connected ? 'connected' : 'disconnected'}`,
        component: 'MockDataGenerator'
      };
      this.logBuffer.push(logEntry);
    }
  }

  /**
   * Add a new waypoint (for testing waypoint system)
   */
  addWaypoint(name, x, z, color = '#ff6b6b') {
    return {
      id: Date.now(),
      name: name,
      x: x,
      z: z,
      color: color,
      dimension: 'overworld',
      created: new Date().toISOString()
    };
  }

  /**
   * Get system health metrics
   */
  getHealthMetrics() {
    const connectedPlayers = this.mockPlayers.filter(p => p.connected);
    const runningTasks = this.mockTasks.filter(t => t.status === 'running');
    
    return {
      playerHealth: {
        average: connectedPlayers.reduce((sum, p) => sum + p.health, 0) / connectedPlayers.length,
        lowest: Math.min(...connectedPlayers.map(p => p.health))
      },
      taskEfficiency: {
        completionRate: this.mockTasks.filter(t => t.status === 'completed').length / this.mockTasks.length,
        averageProgress: runningTasks.reduce((sum, t) => sum + t.progress, 0) / runningTasks.length
      },
      systemLoad: {
        cpu: Math.random() * 100,
        memory: Math.random() * 100,
        network: Math.random() * 100
      }
    };
  }
}

module.exports = MockDataGenerator;
