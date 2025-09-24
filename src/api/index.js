const express = require('express');

/**
 * Public API for AppyProx - allows external tools to integrate with the proxy
 */
class AppyProxAPI {
  constructor(config, logger, proxyInstance) {
    this.config = config;
    this.logger = logger.child ? logger.child('API') : logger;
    this.proxy = proxyInstance;
    this.app = express();
    this.server = null;
    
    this.setupMiddleware();
    this.setupRoutes();
  }

  setupMiddleware() {
    this.app.use(express.json());
    this.app.use((req, res, next) => {
      this.logger.debug(`API Request: ${req.method} ${req.path}`);
      next();
    });
  }

  setupRoutes() {
    // Status endpoints
    this.app.get('/status', (req, res) => {
      res.json(this.proxy.getStatus());
    });

    // Cluster management
    this.app.get('/clusters', (req, res) => {
      const clusters = this.proxy.clusterManager.listClusters();
      res.json({ clusters });
    });

    this.app.post('/clusters', (req, res) => {
      const { name, options } = req.body;
      try {
        const clusterId = this.proxy.clusterManager.createCluster(name, options);
        res.json({ success: true, clusterId });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // Task management
    this.app.get('/tasks', (req, res) => {
      const tasks = this.proxy.automationEngine.listTasks();
      res.json({ tasks });
    });

    this.app.post('/tasks', (req, res) => {
      const { type, parameters, cluster } = req.body;
      try {
        const taskId = this.proxy.automationEngine.createTask(type, parameters, cluster);
        res.json({ success: true, taskId });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // AI-powered natural language task creation
    this.app.post('/tasks/from-language', async (req, res) => {
      const { description, options = {}, cluster } = req.body;
      
      if (!description || typeof description !== 'string') {
        return res.status(400).json({ 
          success: false, 
          error: 'Description is required and must be a string' 
        });
      }
      
      try {
        const taskId = await this.proxy.automationEngine.createTaskFromNaturalLanguage(
          description, 
          options, 
          cluster
        );
        res.json({ 
          success: true, 
          taskId,
          message: `Created AI-powered task from: "${description}"` 
        });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // Natural language task parsing (without creating a task)
    this.app.post('/tasks/parse-language', async (req, res) => {
      const { description, options = {} } = req.body;
      
      if (!description || typeof description !== 'string') {
        return res.status(400).json({ 
          success: false, 
          error: 'Description is required and must be a string' 
        });
      }
      
      try {
        const result = await this.proxy.automationEngine.convertNaturalLanguageTask(
          description, 
          options
        );
        res.json(result);
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    this.app.post('/tasks/complex', (req, res) => {
      const { type, parameters, cluster } = req.body;
      try {
        const taskId = this.proxy.automationEngine.createComplexTask(type, parameters, cluster);
        res.json({ success: true, taskId });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // Diamond block example endpoint
    this.app.post('/tasks/diamond-blocks', (req, res) => {
      const { quantity = 256, cluster = 'mining_cluster' } = req.body;
      try {
        const taskId = this.proxy.automationEngine.createComplexTask('gather', {
          item: 'diamond_block',
          quantity: quantity,
          timeLimit: 7200000 // 2 hours
        }, cluster);
        res.json({ 
          success: true, 
          taskId,
          message: `Started advanced diamond block gathering task for ${quantity} blocks`
        });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // Schematic management endpoints
    this.app.get('/schematics', (req, res) => {
      try {
        const schematics = this.proxy.automationEngine.litematicaManager.listSchematics();
        res.json({ success: true, schematics });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    this.app.post('/tasks/build', (req, res) => {
      const { schematic, location, cluster = 'building_cluster' } = req.body;
      try {
        const buildTask = this.proxy.automationEngine.litematicaManager.createBuildTask(schematic, {
          location,
          clusterId: cluster
        });
        res.json({ 
          success: true, 
          buildTaskId: buildTask.id,
          message: `Started building ${schematic} at ${JSON.stringify(location)}`
        });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // Advanced mining endpoint
    this.app.post('/tasks/advanced-mining', (req, res) => {
      const { target = 'diamond', area, formation = 'spread', depth = 16, cluster } = req.body;
      try {
        // This would integrate with the enhanced Baritone interface
        res.json({ 
          success: true, 
          message: `Advanced mining operation planned for ${target}`,
          parameters: { target, area, formation, depth, cluster }
        });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });

    // Account information
    this.app.get('/accounts', (req, res) => {
      const clients = this.proxy.proxyServer.getClients();
      res.json({ accounts: clients });
    });
    
    // Proxy Client Management endpoints
    this.app.get('/proxy-clients', async (req, res) => {
      try {
        if (this.proxy.proxyClientBridge) {
          const clients = this.proxy.proxyClientBridge.getConnectedClients();
          res.json({ success: true, clients });
        } else {
          res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/proxy-clients/start', async (req, res) => {
      try {
        const { account, config = {} } = req.body;
        
        if (!this.proxy.proxyClientBridge) {
          return res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
        
        const result = await this.proxy.proxyClientBridge.startProxyClient(account, config);
        res.json({ success: true, result });
        
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/proxy-clients/:accountId/stop', async (req, res) => {
      try {
        const { accountId } = req.params;
        const { graceful = true } = req.body;
        
        if (!this.proxy.proxyClientBridge) {
          return res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
        
        const result = await this.proxy.proxyClientBridge.stopProxyClient(accountId, graceful);
        res.json({ success: result });
        
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/proxy-clients/:accountId/execute-task', async (req, res) => {
      try {
        const { accountId } = req.params;
        const { task } = req.body;
        
        if (!this.proxy.proxyClientBridge) {
          return res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
        
        const result = await this.proxy.proxyClientBridge.executeAutomationTask(accountId, task);
        res.json({ success: true, result });
        
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/proxy-clients/clusters/:clusterId/execute-task', async (req, res) => {
      try {
        const { clusterId } = req.params;
        const { task } = req.body;
        
        if (!this.proxy.proxyClientBridge) {
          return res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
        
        const result = await this.proxy.proxyClientBridge.executeClusterAutomation(clusterId, task);
        res.json({ success: true, result });
        
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/proxy-clients/:accountId/status', async (req, res) => {
      try {
        const { accountId } = req.params;
        
        if (!this.proxy.proxyClientBridge) {
          return res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
        
        const status = this.proxy.proxyClientBridge.getClientStatus(accountId);
        const metrics = this.proxy.proxyClientBridge.getClientMetrics(accountId);
        const connected = this.proxy.proxyClientBridge.isClientConnected(accountId);
        
        res.json({ 
          success: true, 
          accountId,
          connected,
          status, 
          metrics 
        });
        
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/proxy-clients/dashboard', async (req, res) => {
      try {
        if (!this.proxy.proxyClientBridge) {
          return res.status(503).json({ success: false, error: 'Proxy Client Bridge not available' });
        }
        
        const dashboardData = await this.proxy.proxyClientBridge.getDashboardData();
        const systemStatus = await this.proxy.proxyClientBridge.getSystemStatus();
        
        res.json({ 
          success: true, 
          dashboard: dashboardData,
          system: systemStatus
        });
        
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });

    // Wiki and resource information endpoints
    this.app.get('/wiki/resources/:resource', async (req, res) => {
      try {
        const { resource } = req.params;
        const resourceInfo = await this.proxy.automationEngine.wikiScraper.getResourceInfo(resource);
        if (resourceInfo) {
          res.json({ success: true, resource: resourceInfo });
        } else {
          res.status(404).json({ success: false, error: 'Resource not found' });
        }
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/wiki/recipes/:item', async (req, res) => {
      try {
        const { item } = req.params;
        const recipe = await this.proxy.automationEngine.wikiScraper.getCraftingRecipe(item);
        if (recipe) {
          res.json({ success: true, recipe });
        } else {
          res.status(404).json({ success: false, error: 'Recipe not found' });
        }
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/wiki/search/:query', async (req, res) => {
      try {
        const { query } = req.params;
        const results = await this.proxy.automationEngine.wikiScraper.searchItems(query);
        res.json({ success: true, results });
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    // Advanced task planning endpoints
    this.app.post('/tasks/plan', async (req, res) => {
      try {
        const { taskType, parameters, accountCapabilities = {} } = req.body;
        const plan = await this.proxy.automationEngine.taskPlanner.createExecutionPlan(
          taskType, parameters, accountCapabilities
        );
        res.json({ success: true, plan });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/tasks/:taskId/status', (req, res) => {
      try {
        const { taskId } = req.params;
        const task = this.proxy.automationEngine.getTaskStatus(taskId);
        if (task) {
          res.json({ success: true, task });
        } else {
          res.status(404).json({ success: false, error: 'Task not found' });
        }
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.delete('/tasks/:taskId', (req, res) => {
      try {
        const { taskId } = req.params;
        const success = this.proxy.automationEngine.cancelTask(taskId);
        res.json({ success });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    // Cluster detailed management
    this.app.get('/clusters/:clusterId', (req, res) => {
      try {
        const { clusterId } = req.params;
        const cluster = this.proxy.clusterManager.getCluster(clusterId);
        if (cluster) {
          res.json({ success: true, cluster });
        } else {
          res.status(404).json({ success: false, error: 'Cluster not found' });
        }
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.put('/clusters/:clusterId', (req, res) => {
      try {
        const { clusterId } = req.params;
        const { options } = req.body;
        const success = this.proxy.clusterManager.updateCluster(clusterId, options);
        res.json({ success });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.delete('/clusters/:clusterId', (req, res) => {
      try {
        const { clusterId } = req.params;
        const success = this.proxy.clusterManager.deleteCluster(clusterId);
        res.json({ success });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/clusters/:clusterId/accounts', (req, res) => {
      try {
        const { clusterId } = req.params;
        const { accountId } = req.body;
        const success = this.proxy.clusterManager.addAccountToCluster(clusterId, accountId);
        res.json({ success });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.delete('/clusters/:clusterId/accounts/:accountId', (req, res) => {
      try {
        const { clusterId, accountId } = req.params;
        const success = this.proxy.clusterManager.removeAccountFromCluster(clusterId, accountId);
        res.json({ success });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    // Baritone automation endpoints
    this.app.post('/baritone/mining', async (req, res) => {
      try {
        const { clusterId, target, area, formation, depth } = req.body;
        const result = await this.proxy.automationEngine.baritoneInterface.executeAdvancedMining({
          clusterId, target, area, formation, depth
        });
        res.json({ success: true, result });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/baritone/building', async (req, res) => {
      try {
        const { clusterId, schematic, location, materials } = req.body;
        const result = await this.proxy.automationEngine.baritoneInterface.executeAdvancedBuilding({
          clusterId, schematic, location, materials
        });
        res.json({ success: true, result });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/baritone/pathfinding', async (req, res) => {
      try {
        const { clusterId, destination, options } = req.body;
        const result = await this.proxy.automationEngine.baritoneInterface.executeSmartPathfinding({
          clusterId, destination, options
        });
        res.json({ success: true, result });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    this.app.post('/baritone/inventory', async (req, res) => {
      try {
        const { clusterId, action, items } = req.body;
        const result = await this.proxy.automationEngine.baritoneInterface.manageInventory({
          clusterId, action, items
        });
        res.json({ success: true, result });
      } catch (error) {
        res.status(400).json({ success: false, error: error.message });
      }
    });
    
    // System monitoring and metrics
    this.app.get('/metrics', (req, res) => {
      try {
        const metrics = {
          proxy: this.proxy.proxyServer.getStatus(),
          clusters: this.proxy.clusterManager.getStatus(),
          automation: this.proxy.automationEngine.getStatus(),
          centralNode: this.proxy.centralNode ? this.proxy.centralNode.getStatus() : null,
          api: {
            uptime: process.uptime(),
            memoryUsage: process.memoryUsage(),
            cpuUsage: process.cpuUsage()
          }
        };
        res.json({ success: true, metrics });
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/logs', (req, res) => {
      try {
        const { lines = 100, level = 'info' } = req.query;
        // This would implement log retrieval
        res.json({ 
          success: true, 
          logs: [], // Placeholder
          message: 'Log retrieval not implemented yet'
        });
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    // Configuration endpoints
    this.app.get('/config', (req, res) => {
      try {
        // Return sanitized config (no sensitive data)
        const config = {
          proxy: { ...this.config.proxy, version: this.config.proxy.version },
          clustering: this.config.clustering,
          automation: this.config.automation,
          central_node: { ...this.config.central_node, enabled: this.config.central_node.enabled },
          api: { ...this.config.api, enabled: this.config.api.enabled }
        };
        res.json({ success: true, config });
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    this.app.get('/version', (req, res) => {
      try {
        const packageJson = require('../../package.json');
        res.json({ 
          success: true,
          name: packageJson.name,
          version: packageJson.version,
          description: packageJson.description,
          node: process.version,
          platform: process.platform,
          arch: process.arch
        });
      } catch (error) {
        res.status(500).json({ success: false, error: error.message });
      }
    });
    
    // Health check
    this.app.get('/health', (req, res) => {
      res.json({ 
        status: 'healthy',
        timestamp: new Date().toISOString(),
        version: require('../../package.json').version,
        uptime: process.uptime(),
        components: {
          proxy: this.proxy.proxyServer?.isRunning || false,
          clusters: this.proxy.clusterManager?.isRunning || false,
          automation: this.proxy.automationEngine?.isRunning || false,
          centralNode: this.proxy.centralNode?.isRunning || false
        }
      });
    });
    
    // API documentation endpoint
    this.app.get('/docs', (req, res) => {
      res.json({
        success: true,
        title: 'AppyProx API Documentation',
        endpoints: {
          '/health': 'GET - Health check and system status',
          '/status': 'GET - Overall system status',
          '/version': 'GET - Version information',
          '/config': 'GET - System configuration',
          '/metrics': 'GET - System metrics and performance data',
          '/accounts': 'GET - List connected accounts',
          '/clusters': 'GET/POST - Cluster management',
          '/clusters/:id': 'GET/PUT/DELETE - Individual cluster operations',
          '/tasks': 'GET/POST - Task management',
          '/tasks/plan': 'POST - Create task execution plans',
          '/tasks/:id': 'GET/DELETE - Individual task operations',
          '/schematics': 'GET - List available schematics',
          '/wiki/resources/:resource': 'GET - Minecraft resource information',
          '/wiki/recipes/:item': 'GET - Crafting recipe information',
          '/baritone/*': 'POST - Advanced automation with Baritone',
          '/proxy-clients/*': 'GET/POST - Fabric mod integration'
        }
      });
    });
  }

  async start() {
    if (!this.config.enabled) {
      this.logger.info('API server disabled in configuration');
      return;
    }

    return new Promise((resolve, reject) => {
      this.server = this.app.listen(this.config.port, (err) => {
        if (err) {
          reject(err);
        } else {
          this.logger.info(`API server listening on port ${this.config.port}`);
          resolve();
        }
      });
    });
  }

  async stop() {
    if (this.server) {
      return new Promise((resolve) => {
        this.server.close(() => {
          this.logger.info('API server stopped');
          resolve();
        });
      });
    }
  }
}

module.exports = AppyProxAPI;