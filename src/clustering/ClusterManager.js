const EventEmitter = require('events');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

/**
 * Manages account clusters and coordinates communication between grouped accounts
 */
class ClusterManager extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child('ClusterManager');
    
    // Data structures
    this.clients = new Map(); // clientId -> client info
    this.clusters = new Map(); // clusterId -> cluster info
    this.clientToCluster = new Map(); // clientId -> clusterId
    
    // Configuration paths
    this.clustersConfigPath = path.join(__dirname, '../../configs/clusters.json');
    this.accountsConfigPath = path.join(__dirname, '../../configs/accounts.json');
    
    // Status tracking
    this.isRunning = false;
    this.healthCheckInterval = null;
    this.syncInterval = null;
    
    // Load persisted configurations
    this.loadClustersConfig();
    this.loadAccountsConfig();
  }

  async start() {
    if (this.isRunning) return;

    this.logger.info('Starting cluster manager...');
    
    // Start health check interval
    this.healthCheckInterval = setInterval(() => {
      this.performHealthCheck();
    }, this.config.health_check_interval);
    
    // Start sync interval for cluster coordination
    this.syncInterval = setInterval(() => {
      this.syncClusterStates();
    }, this.config.sync_interval);
    
    this.isRunning = true;
    this.logger.info('Cluster manager started');
  }

  async stop() {
    if (!this.isRunning) return;

    this.logger.info('Stopping cluster manager...');
    
    if (this.healthCheckInterval) {
      clearInterval(this.healthCheckInterval);
      this.healthCheckInterval = null;
    }
    
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
      this.syncInterval = null;
    }
    
    // Save current state
    this.saveClustersConfig();
    
    this.isRunning = false;
    this.logger.info('Cluster manager stopped');
  }

  registerClient(clientInfo) {
    this.clients.set(clientInfo.id, {
      ...clientInfo,
      status: 'connected',
      lastSeen: Date.now(),
      health: {
        ping: 0,
        tps: 20,
        health: 20,
        food: 20,
        position: null
      }
    });

    this.logger.info(`Registered client: ${clientInfo.username} (${clientInfo.id})`);
    
    // Auto-assign to cluster if configured
    this.autoAssignToCluster(clientInfo.id);
    
    this.emit('client_registered', clientInfo);
  }

  unregisterClient(clientInfo) {
    const client = this.clients.get(clientInfo.id);
    if (!client) return;

    // Remove from cluster
    const clusterId = this.clientToCluster.get(clientInfo.id);
    if (clusterId) {
      this.removeClientFromCluster(clientInfo.id, clusterId);
    }

    this.clients.delete(clientInfo.id);
    this.logger.info(`Unregistered client: ${clientInfo.username} (${clientInfo.id})`);
    
    this.emit('client_unregistered', clientInfo);
  }

  autoAssignToCluster(clientId) {
    const client = this.clients.get(clientId);
    if (!client) return;

    // Check if client should be auto-assigned based on configuration
    const accountConfig = this.findAccountConfig(client.username);
    if (accountConfig && accountConfig.cluster) {
      this.assignClientToCluster(clientId, accountConfig.cluster);
    }
  }

  createCluster(clusterName, options = {}) {
    const clusterId = uuidv4();
    const cluster = {
      id: clusterId,
      name: clusterName,
      created: Date.now(),
      maxSize: options.maxSize || this.config.max_accounts_per_cluster,
      autoReconnect: options.autoReconnect !== false,
      reconnectInterval: options.reconnectInterval || this.config.reconnect_interval,
      members: [],
      leader: null,
      status: 'idle',
      currentTask: null,
      settings: {
        followLeader: options.followLeader || false,
        shareInventory: options.shareInventory || false,
        syncMovement: options.syncMovement || false,
        ...options.settings
      }
    };

    this.clusters.set(clusterId, cluster);
    this.logger.info(`Created cluster: ${clusterName} (${clusterId})`);
    
    this.saveClustersConfig();
    this.emit('cluster_created', cluster);
    
    return clusterId;
  }

  assignClientToCluster(clientId, clusterIdOrName) {
    const client = this.clients.get(clientId);
    if (!client) {
      this.logger.warn(`Cannot assign non-existent client ${clientId} to cluster`);
      return false;
    }

    // Find cluster by ID or name
    let cluster = this.clusters.get(clusterIdOrName);
    if (!cluster) {
      cluster = Array.from(this.clusters.values()).find(c => c.name === clusterIdOrName);
    }

    if (!cluster) {
      this.logger.warn(`Cluster not found: ${clusterIdOrName}`);
      return false;
    }

    // Check if cluster is full
    if (cluster.members.length >= cluster.maxSize) {
      this.logger.warn(`Cluster ${cluster.name} is full (${cluster.members.length}/${cluster.maxSize})`);
      return false;
    }

    // Remove from current cluster if assigned
    const currentClusterId = this.clientToCluster.get(clientId);
    if (currentClusterId) {
      this.removeClientFromCluster(clientId, currentClusterId);
    }

    // Add to new cluster
    cluster.members.push(clientId);
    this.clientToCluster.set(clientId, cluster.id);

    // Set as leader if first member
    if (cluster.members.length === 1) {
      cluster.leader = clientId;
    }

    this.logger.info(`Assigned ${client.username} to cluster ${cluster.name}`);
    
    this.saveClustersConfig();
    this.emit('client_assigned_to_cluster', { clientId, clusterId: cluster.id, cluster });
    this.emit('cluster_update', cluster);
    
    return true;
  }

  removeClientFromCluster(clientId, clusterId) {
    const cluster = this.clusters.get(clusterId);
    if (!cluster) return false;

    const memberIndex = cluster.members.indexOf(clientId);
    if (memberIndex === -1) return false;

    cluster.members.splice(memberIndex, 1);
    this.clientToCluster.delete(clientId);

    // Reassign leader if necessary
    if (cluster.leader === clientId && cluster.members.length > 0) {
      cluster.leader = cluster.members[0];
      this.logger.info(`Reassigned cluster leader for ${cluster.name} to ${this.clients.get(cluster.leader)?.username}`);
    } else if (cluster.members.length === 0) {
      cluster.leader = null;
      cluster.status = 'idle';
    }

    const client = this.clients.get(clientId);
    this.logger.info(`Removed ${client?.username || clientId} from cluster ${cluster.name}`);
    
    this.saveClustersConfig();
    this.emit('client_removed_from_cluster', { clientId, clusterId, cluster });
    this.emit('cluster_update', cluster);
    
    return true;
  }

  deleteCluster(clusterIdOrName) {
    let cluster = this.clusters.get(clusterIdOrName);
    if (!cluster) {
      cluster = Array.from(this.clusters.values()).find(c => c.name === clusterIdOrName);
    }

    if (!cluster) {
      this.logger.warn(`Cluster not found: ${clusterIdOrName}`);
      return false;
    }

    // Remove all clients from cluster
    [...cluster.members].forEach(clientId => {
      this.removeClientFromCluster(clientId, cluster.id);
    });

    this.clusters.delete(cluster.id);
    this.logger.info(`Deleted cluster: ${cluster.name}`);
    
    this.saveClustersConfig();
    this.emit('cluster_deleted', cluster);
    
    return true;
  }

  sendCommandToCluster(clusterIdOrName, command, args = {}) {
    let cluster = this.clusters.get(clusterIdOrName);
    if (!cluster) {
      cluster = Array.from(this.clusters.values()).find(c => c.name === clusterIdOrName);
    }

    if (!cluster) {
      this.logger.warn(`Cluster not found: ${clusterIdOrName}`);
      return false;
    }

    const commandData = {
      id: uuidv4(),
      command: command,
      args: args,
      timestamp: Date.now(),
      source: 'cluster_manager'
    };

    let successCount = 0;
    cluster.members.forEach(clientId => {
      if (this.sendCommandToClient(clientId, commandData)) {
        successCount++;
      }
    });

    this.logger.info(`Sent command '${command}' to ${successCount}/${cluster.members.length} members of cluster ${cluster.name}`);
    
    this.emit('cluster_command_sent', {
      clusterId: cluster.id,
      command: commandData,
      successCount,
      totalMembers: cluster.members.length
    });

    return successCount > 0;
  }

  sendCommandToClient(clientId, commandData) {
    const client = this.clients.get(clientId);
    if (!client || client.status !== 'connected') {
      this.logger.debug(`Cannot send command to offline client: ${clientId}`);
      return false;
    }

    // Emit event for proxy to handle
    this.emit('send_command_to_client', {
      clientId: clientId,
      command: commandData
    });

    return true;
  }

  performHealthCheck() {
    const now = Date.now();
    let unhealthyCount = 0;

    for (const [clientId, client] of this.clients) {
      const timeSinceLastSeen = now - client.lastSeen;
      
      if (timeSinceLastSeen > this.config.health_check_interval * 2) {
        client.status = 'unhealthy';
        unhealthyCount++;
        
        // Attempt reconnection if configured
        const clusterId = this.clientToCluster.get(clientId);
        if (clusterId) {
          const cluster = this.clusters.get(clusterId);
          if (cluster && cluster.autoReconnect) {
            this.attemptClientReconnection(clientId);
          }
        }
      }
    }

    if (unhealthyCount > 0) {
      this.logger.warn(`Health check found ${unhealthyCount} unhealthy clients`);
      this.emit('health_check_completed', { unhealthyCount, totalClients: this.clients.size });
    }
  }

  attemptClientReconnection(clientId) {
    const client = this.clients.get(clientId);
    if (!client) return;

    this.logger.info(`Attempting to reconnect client: ${client.username}`);
    
    // Emit event for proxy to handle reconnection
    this.emit('reconnect_client', {
      clientId: clientId,
      client: client
    });
  }

  syncClusterStates() {
    for (const [clusterId, cluster] of this.clusters) {
      if (cluster.members.length === 0) continue;

      // Sync cluster member states based on settings
      if (cluster.settings.followLeader && cluster.leader) {
        this.syncFollowLeader(cluster);
      }

      if (cluster.settings.syncMovement) {
        this.syncMovement(cluster);
      }

      // Update cluster status based on member activities
      this.updateClusterStatus(cluster);
    }
  }

  syncFollowLeader(cluster) {
    const leader = this.clients.get(cluster.leader);
    if (!leader || !leader.position) return;

    const followCommand = {
      command: 'follow_player',
      args: {
        target: leader.username,
        position: leader.position,
        distance: 3
      }
    };

    cluster.members.forEach(clientId => {
      if (clientId !== cluster.leader) {
        this.sendCommandToClient(clientId, followCommand);
      }
    });
  }

  syncMovement(cluster) {
    if (cluster.members.length <= 1) return;

    const leader = this.clients.get(cluster.leader);
    if (!leader || !leader.position) return;

    // Send movement sync commands to followers
    const positions = this.calculateFormationPositions(leader.position, cluster.members.length - 1);
    
    cluster.members.forEach((clientId, index) => {
      if (clientId !== cluster.leader && index - 1 < positions.length) {
        const targetPosition = positions[index - 1];
        const moveCommand = {
          command: 'move_to_position',
          args: {
            position: targetPosition,
            precision: 1.0
          }
        };
        this.sendCommandToClient(clientId, moveCommand);
      }
    });
  }

  calculateFormationPositions(leaderPos, followerCount) {
    const positions = [];
    const spacing = 2; // blocks between players
    
    // Simple line formation behind the leader
    for (let i = 0; i < followerCount; i++) {
      positions.push({
        x: leaderPos.x - (spacing * (i + 1)),
        y: leaderPos.y,
        z: leaderPos.z
      });
    }
    
    return positions;
  }

  updateClusterStatus(cluster) {
    const activeMembers = cluster.members.filter(clientId => {
      const client = this.clients.get(clientId);
      return client && client.status === 'connected';
    });

    if (activeMembers.length === 0) {
      cluster.status = 'offline';
    } else if (activeMembers.length < cluster.members.length) {
      cluster.status = 'partial';
    } else if (cluster.currentTask) {
      cluster.status = 'working';
    } else {
      cluster.status = 'idle';
    }
  }

  updateClientStatus(clientId, statusUpdate) {
    const client = this.clients.get(clientId);
    if (!client) return;

    client.lastSeen = Date.now();
    client.status = 'connected';
    
    // Update health information
    if (statusUpdate.health !== undefined) {
      client.health = { ...client.health, ...statusUpdate.health };
    }
    
    // Update position
    if (statusUpdate.position) {
      client.position = statusUpdate.position;
    }

    this.emit('account_status_update', {
      clientId,
      client,
      statusUpdate
    });
  }

  loadClustersConfig() {
    try {
      if (fs.existsSync(this.clustersConfigPath)) {
        const data = fs.readFileSync(this.clustersConfigPath, 'utf8');
        const config = JSON.parse(data);
        
        config.clusters?.forEach(clusterData => {
          this.clusters.set(clusterData.id, {
            ...clusterData,
            members: [] // Reset members on startup
          });
        });
        
        this.logger.info(`Loaded ${config.clusters?.length || 0} cluster configurations`);
      }
    } catch (error) {
      this.logger.warn('Failed to load clusters config:', error.message);
    }
  }

  saveClustersConfig() {
    try {
      const config = {
        clusters: Array.from(this.clusters.values()).map(cluster => ({
          ...cluster,
          members: [] // Don't persist active members
        }))
      };
      
      fs.writeFileSync(this.clustersConfigPath, JSON.stringify(config, null, 2));
      this.logger.debug('Saved clusters configuration');
    } catch (error) {
      this.logger.error('Failed to save clusters config:', error.message);
    }
  }

  loadAccountsConfig() {
    try {
      if (fs.existsSync(this.accountsConfigPath)) {
        const data = fs.readFileSync(this.accountsConfigPath, 'utf8');
        this.accountsConfig = JSON.parse(data);
        this.logger.info(`Loaded configuration for ${this.accountsConfig.accounts?.length || 0} accounts`);
      } else {
        this.accountsConfig = { accounts: [] };
      }
    } catch (error) {
      this.logger.warn('Failed to load accounts config:', error.message);
      this.accountsConfig = { accounts: [] };
    }
  }

  findAccountConfig(username) {
    return this.accountsConfig.accounts?.find(account => account.username === username);
  }

  getStatus() {
    return {
      isRunning: this.isRunning,
      totalClients: this.clients.size,
      totalClusters: this.clusters.size,
      clusters: Array.from(this.clusters.values()).map(cluster => ({
        id: cluster.id,
        name: cluster.name,
        status: cluster.status,
        memberCount: cluster.members.length,
        maxSize: cluster.maxSize,
        leader: cluster.leader ? this.clients.get(cluster.leader)?.username : null,
        currentTask: cluster.currentTask
      })),
      clients: Array.from(this.clients.values()).map(client => ({
        id: client.id,
        username: client.username,
        status: client.status,
        cluster: this.clientToCluster.get(client.id),
        health: client.health,
        lastSeen: client.lastSeen
      }))
    };
  }

  // Public API methods
  getCluster(clusterIdOrName) {
    let cluster = this.clusters.get(clusterIdOrName);
    if (!cluster) {
      cluster = Array.from(this.clusters.values()).find(c => c.name === clusterIdOrName);
    }
    return cluster;
  }

  getClient(clientId) {
    return this.clients.get(clientId);
  }

  listClusters() {
    return Array.from(this.clusters.values());
  }

  listClients() {
    return Array.from(this.clients.values());
  }
}

module.exports = ClusterManager;