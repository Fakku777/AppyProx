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

    // Health check
    this.app.get('/health', (req, res) => {
      res.json({ 
        status: 'healthy',
        timestamp: new Date().toISOString(),
        version: require('../../package.json').version
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