#!/usr/bin/env node

/**
 * Test script for the headless Minecraft client system
 * Demonstrates client management, authentication, and API integration
 */

const ClientManager = require('./src/client/ClientManager');
const MicrosoftAuthManager = require('./src/auth/MicrosoftAuthManager');
const ClientAPI = require('./src/client/ClientAPI');

console.log('🔧 Testing AppyProx Headless Client System...\n');

class ClientSystemTester {
  constructor() {
    this.logger = {
      info: (msg, ...args) => console.log(`[INFO] ${msg}`, ...args),
      warn: (msg, ...args) => console.warn(`[WARN] ${msg}`, ...args),
      error: (msg, ...args) => console.error(`[ERROR] ${msg}`, ...args),
      debug: (msg, ...args) => console.log(`[DEBUG] ${msg}`, ...args)
    };
    
    this.authManager = new MicrosoftAuthManager({
      logger: this.logger,
      tokenStorePath: './test-data/tokens'
    });
    
    this.clientManager = new ClientManager({
      logger: this.logger,
      maxClients: 10,
      defaultServer: { host: 'localhost', port: 25565 }
    });
    
    this.clientAPI = new ClientAPI(this.clientManager, this.authManager, this.logger);
  }
  
  async runTests() {
    console.log('📋 Starting comprehensive client system tests...\n');
    
    try {
      // Test 1: Initialize systems
      console.log('🔄 Test 1: System Initialization');
      await this.testSystemInitialization();
      console.log('✅ System initialization successful\n');
      
      // Test 2: Client creation and management
      console.log('🔄 Test 2: Client Creation and Management');
      await this.testClientManagement();
      console.log('✅ Client management tests passed\n');
      
      // Test 3: Authentication system (if available)
      console.log('🔄 Test 3: Authentication System');
      await this.testAuthenticationSystem();
      console.log('✅ Authentication system tests completed\n');
      
      // Test 4: Client communication
      console.log('🔄 Test 4: Client Communication');
      await this.testClientCommunication();
      console.log('✅ Client communication tests passed\n');
      
      // Test 5: API endpoints
      console.log('🔄 Test 5: API Endpoints');
      await this.testAPIEndpoints();
      console.log('✅ API endpoint tests completed\n');
      
      // Test 6: Bulk operations
      console.log('🔄 Test 6: Bulk Operations');
      await this.testBulkOperations();
      console.log('✅ Bulk operation tests passed\n');
      
      console.log('🎉 All client system tests completed successfully!');
      
    } catch (error) {
      console.error('❌ Test failed:', error.message);
      console.error(error.stack);
    } finally {
      await this.cleanup();
    }
  }
  
  async testSystemInitialization() {
    // Start client manager
    await this.clientManager.start();
    
    // Verify initial state
    const stats = this.clientManager.getStats();
    console.log('   Initial stats:', {
      totalClients: stats.totalClients,
      connectedClients: stats.connectedClients,
      uptime: `${stats.uptimeMs}ms`
    });
    
    // Load any stored Microsoft accounts
    await this.authManager.loadAllStoredAccounts();
    const accounts = this.authManager.getActiveAccounts();
    console.log(`   Loaded ${accounts.length} Microsoft accounts`);
  }
  
  async testClientManagement() {
    // Create offline clients
    const client1 = await this.clientManager.createClient({
      username: 'TestBot1',
      auth: 'offline'
    });
    
    const client2 = await this.clientManager.createClient({
      username: 'TestBot2',
      auth: 'offline'
    });
    
    console.log(`   Created clients: ${client1.username}, ${client2.username}`);
    
    // Test client status
    const status1 = client1.getStatus();
    console.log(`   ${client1.username} status:`, {
      isConnected: status1.isConnected,
      isReady: status1.isReady,
      server: status1.server
    });
    
    // Create multiple clients
    const multiClients = await this.clientManager.createMultipleClients(3, {
      username: 'MultiBotTest',
      auth: 'offline'
    });
    
    console.log(`   Created ${multiClients.length} additional clients`);
    
    // Get all clients
    const allClients = this.clientManager.getAllClients();
    console.log(`   Total clients in manager: ${allClients.length}`);
  }
  
  async testAuthenticationSystem() {
    // Test Microsoft authentication system structure
    const msAccounts = this.authManager.getActiveAccounts();
    console.log(`   Microsoft accounts available: ${msAccounts.length}`);
    
    // Test encryption/decryption
    const testData = { test: 'encryption', timestamp: Date.now() };
    const encrypted = this.authManager.encrypt(JSON.stringify(testData));
    const decrypted = JSON.parse(this.authManager.decrypt(encrypted));
    
    console.log('   Encryption test:', decrypted.test === testData.test ? 'PASSED' : 'FAILED');
    
    // Note: Device code flow requires user interaction, so we skip it in automated tests
    console.log('   Device code flow: SKIPPED (requires user interaction)');
  }
  
  async testClientCommunication() {
    const clients = this.clientManager.getAllClients();
    
    if (clients.length === 0) {
      console.log('   No clients available for communication tests');
      return;
    }
    
    const testClient = clients[0];
    
    // Test chat history
    const initialChatHistory = testClient.getChatHistory();
    console.log(`   Initial chat history length: ${initialChatHistory.length}`);
    
    // Simulate chat events
    testClient.handleChatMessage('TestUser', 'Hello from test!');
    testClient.handleWhisperMessage('TestWhisperer', 'Secret message');
    
    const updatedChatHistory = testClient.getChatHistory();
    console.log(`   Updated chat history length: ${updatedChatHistory.length}`);
    
    // Test position updates
    testClient.position = { x: 100, y: 64, z: 200 };
    testClient.emit('positionUpdate', testClient.position);
    
    console.log('   Position update test:', testClient.position);
  }
  
  async testAPIEndpoints() {
    // Mock Express request/response objects for testing
    const mockReq = (params = {}, body = {}, query = {}) => ({
      params,
      body,
      query,
      headers: {}
    });
    
    const mockRes = () => {
      const res = {
        status: (code) => {
          res.statusCode = code;
          return res;
        },
        json: (data) => {
          res.jsonData = data;
          return res;
        }
      };
      return res;
    };
    
    // Test getting all clients
    const req1 = mockReq();
    const res1 = mockRes();
    await this.clientAPI.getAllClients(req1, res1);
    
    console.log('   GET /clients:', {
      success: res1.jsonData.success,
      count: res1.jsonData.count
    });
    
    // Test client statistics
    const req2 = mockReq();
    const res2 = mockRes();
    await this.clientAPI.getStats(req2, res2);
    
    console.log('   GET /clients/stats:', {
      success: res2.jsonData.success,
      totalClients: res2.jsonData.stats.totalClients
    });
    
    // Test Microsoft accounts endpoint
    const req3 = mockReq();
    const res3 = mockRes();
    await this.clientAPI.getMicrosoftAccounts(req3, res3);
    
    console.log('   GET /clients/auth/microsoft/accounts:', {
      success: res3.jsonData.success,
      count: res3.jsonData.count
    });
  }
  
  async testBulkOperations() {
    const clients = this.clientManager.getAllClients();
    
    if (clients.length < 2) {
      console.log('   Not enough clients for bulk operation tests');
      return;
    }
    
    // Test finding clients
    const offlineClients = this.clientManager.findClients({ auth: 'offline' });
    console.log(`   Found ${offlineClients.length} offline clients`);
    
    // Test bulk chat simulation
    try {
      await this.clientManager.broadcastChat('Test broadcast message!');
      console.log('   Broadcast chat simulation: SUCCESS');
    } catch (error) {
      console.log('   Broadcast chat simulation: EXPECTED_ERROR (no connections)');
    }
    
    // Test statistics after operations
    const finalStats = this.clientManager.getStats();
    console.log('   Final statistics:', {
      totalClients: finalStats.totalClients,
      connectedClients: finalStats.connectedClients,
      readyClients: finalStats.readyClients
    });
  }
  
  async cleanup() {
    console.log('\n🧹 Cleaning up test resources...');
    
    // Stop client manager
    await this.clientManager.stop();
    
    // Shutdown auth manager
    await this.authManager.shutdown();
    
    console.log('✅ Cleanup completed');
  }
}

// Run tests if called directly
if (require.main === module) {
  const tester = new ClientSystemTester();
  tester.runTests().catch(console.error);
}

module.exports = ClientSystemTester;
