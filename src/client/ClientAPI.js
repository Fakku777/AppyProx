/**
 * ClientAPI - API endpoints for managing headless Minecraft clients
 * Integrates with the main AppyProx API system
 */

const express = require('express');
const { body, param, query, validationResult } = require('express-validator');

class ClientAPI {
  constructor(clientManager, microsoftAuthManager, logger) {
    this.clientManager = clientManager;
    this.authManager = microsoftAuthManager;
    this.logger = logger;
    this.router = express.Router();
    
    this.setupRoutes();
    this.setupMiddleware();
  }
  
  setupMiddleware() {
    // Error handling middleware
    this.router.use((error, req, res, next) => {
      if (error) {
        this.logger.error('Client API Error:', error);
        res.status(500).json({
          success: false,
          error: 'Internal server error',
          message: error.message
        });
      } else {
        next();
      }
    });
  }
  
  setupRoutes() {
    // Get all clients
    this.router.get('/', this.getAllClients.bind(this));
    
    // Get client by ID
    this.router.get('/:clientId', 
      param('clientId').isUUID(),
      this.getClient.bind(this)
    );
    
    // Create new client
    this.router.post('/', 
      body('username').optional().isString().isLength({ min: 1, max: 16 }),
      body('server.host').optional().isString(),
      body('server.port').optional().isInt({ min: 1, max: 65535 }),
      body('auth').optional().isIn(['offline', 'microsoft', 'altening']),
      body('accountId').optional().isString(),
      this.createClient.bind(this)
    );
    
    // Connect client
    this.router.post('/:clientId/connect',
      param('clientId').isUUID(),
      this.connectClient.bind(this)
    );
    
    // Disconnect client
    this.router.post('/:clientId/disconnect',
      param('clientId').isUUID(),
      body('reason').optional().isString(),
      this.disconnectClient.bind(this)
    );
    
    // Remove client
    this.router.delete('/:clientId',
      param('clientId').isUUID(),
      this.removeClient.bind(this)
    );
    
    // Send chat message
    this.router.post('/:clientId/chat',
      param('clientId').isUUID(),
      body('message').isString().isLength({ min: 1, max: 256 }),
      this.sendChat.bind(this)
    );
    
    // Execute command
    this.router.post('/:clientId/command',
      param('clientId').isUUID(),
      body('command').isString().isLength({ min: 1, max: 256 }),
      body('timeout').optional().isInt({ min: 1000, max: 30000 }),
      this.executeCommand.bind(this)
    );
    
    // Get chat history
    this.router.get('/:clientId/chat',
      param('clientId').isUUID(),
      query('limit').optional().isInt({ min: 1, max: 500 }),
      this.getChatHistory.bind(this)
    );
    
    // Bulk operations
    this.router.post('/bulk/create',
      body('count').isInt({ min: 1, max: 20 }),
      body('baseOptions').optional().isObject(),
      this.createMultipleClients.bind(this)
    );
    
    this.router.post('/bulk/connect',
      body('clientIds').isArray(),
      this.connectMultipleClients.bind(this)
    );
    
    this.router.post('/bulk/disconnect',
      body('clientIds').isArray(),
      body('reason').optional().isString(),
      this.disconnectMultipleClients.bind(this)
    );
    
    this.router.post('/bulk/command',
      body('clientIds').isArray(),
      body('command').isString(),
      body('timeout').optional().isInt({ min: 1000, max: 30000 }),
      this.executeBulkCommand.bind(this)
    );
    
    // Statistics
    this.router.get('/stats', this.getStats.bind(this));
    
    // Microsoft Authentication routes
    this.router.post('/auth/microsoft/start', this.startMicrosoftAuth.bind(this));
    this.router.get('/auth/microsoft/accounts', this.getMicrosoftAccounts.bind(this));
    this.router.delete('/auth/microsoft/:accountId', 
      param('accountId').isString(),
      this.removeMicrosoftAccount.bind(this)
    );
  }
  
  // Validation helper
  checkValidation(req, res) {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      res.status(400).json({
        success: false,
        error: 'Validation failed',
        details: errors.array()
      });
      return false;
    }
    return true;
  }
  
  // Get all clients
  async getAllClients(req, res) {
    try {
      const clients = this.clientManager.getAllClients();
      const clientData = clients.map(client => client.getStatus());
      
      res.json({
        success: true,
        clients: clientData,
        count: clientData.length
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Get client by ID
  async getClient(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      const client = this.clientManager.getClient(clientId);
      
      if (!client) {
        return res.status(404).json({
          success: false,
          error: 'Client not found'
        });
      }
      
      res.json({
        success: true,
        client: client.getStatus()
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Create new client
  async createClient(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const options = {
        username: req.body.username,
        server: req.body.server,
        auth: req.body.auth || 'offline',
        accountId: req.body.accountId
      };
      
      // If using Microsoft auth, get credentials
      if (options.auth === 'microsoft' && options.accountId) {
        try {
          const authData = await this.authManager.getAuthData(options.accountId);
          options.credentials = {
            accessToken: authData.accessToken,
            clientToken: authData.clientToken
          };
          options.username = authData.profile.name;
        } catch (authError) {
          return res.status(400).json({
            success: false,
            error: 'Authentication failed',
            message: authError.message
          });
        }
      }
      
      const client = await this.clientManager.createClient(options);
      
      res.status(201).json({
        success: true,
        client: client.getStatus()
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Connect client
  async connectClient(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      const client = await this.clientManager.connectClient(clientId);
      
      res.json({
        success: true,
        client: client.getStatus()
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Disconnect client
  async disconnectClient(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      const { reason } = req.body;
      
      const client = await this.clientManager.disconnectClient(clientId, reason);
      
      res.json({
        success: true,
        client: client.getStatus()
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Remove client
  async removeClient(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      await this.clientManager.removeClient(clientId);
      
      res.json({
        success: true,
        message: `Client ${clientId} removed`
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Send chat message
  async sendChat(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      const { message } = req.body;
      
      const client = this.clientManager.getClient(clientId);
      if (!client) {
        return res.status(404).json({
          success: false,
          error: 'Client not found'
        });
      }
      
      await client.sendChat(message);
      
      res.json({
        success: true,
        message: 'Chat message sent'
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Execute command
  async executeCommand(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      const { command, timeout = 5000 } = req.body;
      
      const client = this.clientManager.getClient(clientId);
      if (!client) {
        return res.status(404).json({
          success: false,
          error: 'Client not found'
        });
      }
      
      await client.executeCommand(command, timeout);
      
      res.json({
        success: true,
        message: 'Command executed'
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Get chat history
  async getChatHistory(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientId } = req.params;
      const { limit = 50 } = req.query;
      
      const client = this.clientManager.getClient(clientId);
      if (!client) {
        return res.status(404).json({
          success: false,
          error: 'Client not found'
        });
      }
      
      const chatHistory = client.getChatHistory(parseInt(limit));
      
      res.json({
        success: true,
        chatHistory,
        count: chatHistory.length
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Create multiple clients
  async createMultipleClients(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { count, baseOptions = {} } = req.body;
      
      const clients = await this.clientManager.createMultipleClients(count, baseOptions);
      
      res.status(201).json({
        success: true,
        clients: clients.map(client => client.getStatus()),
        count: clients.length
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Connect multiple clients
  async connectMultipleClients(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientIds } = req.body;
      
      const results = await Promise.allSettled(
        clientIds.map(id => this.clientManager.connectClient(id))
      );
      
      const successful = results
        .filter(result => result.status === 'fulfilled')
        .map(result => result.value.getStatus());
      
      const failed = results
        .map((result, index) => ({
          clientId: clientIds[index],
          error: result.status === 'rejected' ? result.reason.message : null
        }))
        .filter(item => item.error);
      
      res.json({
        success: true,
        connected: successful,
        failed,
        stats: {
          total: clientIds.length,
          successful: successful.length,
          failed: failed.length
        }
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Disconnect multiple clients
  async disconnectMultipleClients(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientIds, reason } = req.body;
      
      const results = await Promise.allSettled(
        clientIds.map(id => this.clientManager.disconnectClient(id, reason))
      );
      
      const successful = results
        .filter(result => result.status === 'fulfilled')
        .map(result => result.value.getStatus());
      
      const failed = results
        .map((result, index) => ({
          clientId: clientIds[index],
          error: result.status === 'rejected' ? result.reason.message : null
        }))
        .filter(item => item.error);
      
      res.json({
        success: true,
        disconnected: successful,
        failed,
        stats: {
          total: clientIds.length,
          successful: successful.length,
          failed: failed.length
        }
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Execute bulk command
  async executeBulkCommand(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { clientIds, command, timeout = 5000 } = req.body;
      
      await this.clientManager.executeCommandOnClients(clientIds, command, timeout);
      
      res.json({
        success: true,
        message: `Command executed on ${clientIds.length} clients`
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Get statistics
  async getStats(req, res) {
    try {
      const stats = this.clientManager.getStats();
      
      res.json({
        success: true,
        stats
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Start Microsoft authentication
  async startMicrosoftAuth(req, res) {
    try {
      // Start device code flow
      const authData = await this.authManager.startDeviceCodeFlow();
      
      res.json({
        success: true,
        account: {
          id: authData.profile.id,
          name: authData.profile.name,
          type: authData.type
        }
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Get Microsoft accounts
  async getMicrosoftAccounts(req, res) {
    try {
      const accounts = this.authManager.getActiveAccounts();
      
      res.json({
        success: true,
        accounts,
        count: accounts.length
      });
    } catch (error) {
      res.status(500).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Remove Microsoft account
  async removeMicrosoftAccount(req, res) {
    if (!this.checkValidation(req, res)) return;
    
    try {
      const { accountId } = req.params;
      await this.authManager.removeAccount(accountId);
      
      res.json({
        success: true,
        message: `Microsoft account ${accountId} removed`
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        error: error.message
      });
    }
  }
  
  // Get router for mounting
  getRouter() {
    return this.router;
  }
}

module.exports = ClientAPI;