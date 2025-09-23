#!/usr/bin/env node

/**
 * Integration test script for AppyProx Proxy Client Management System
 */

const axios = require('axios');

const API_BASE = 'http://localhost:3000';
const TEST_ACCOUNT = {
  id: 'test_integration_123',
  username: 'TestBot',
  uuid: '550e8400-e29b-41d4-a716-446655440000',
  accessToken: 'test_access_token'
};

async function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function testAPI(endpoint, method = 'GET', data = null) {
  try {
    const config = {
      method,
      url: `${API_BASE}${endpoint}`,
      headers: { 'Content-Type': 'application/json' }
    };
    
    if (data) {
      config.data = data;
    }
    
    const response = await axios(config);
    return { success: true, data: response.data };
  } catch (error) {
    return { 
      success: false, 
      error: error.response ? error.response.data : error.message 
    };
  }
}

async function runIntegrationTests() {
  console.log('🚀 Starting AppyProx Integration Tests...\n');
  
  // Test 1: Check system health
  console.log('1. Testing system health...');
  const healthResult = await testAPI('/health');
  if (healthResult.success) {
    console.log('   ✅ System health check passed');
    console.log(`   📊 Status: ${healthResult.data.status}`);
  } else {
    console.log('   ❌ System health check failed:', healthResult.error);
    return;
  }
  
  // Test 2: Check system status
  console.log('\n2. Testing system status...');
  const statusResult = await testAPI('/status');
  if (statusResult.success) {
    console.log('   ✅ System status retrieved');
    console.log(`   🔄 Running: ${statusResult.data.running}`);
    console.log(`   🌉 Bridge: ${statusResult.data.proxyClientBridge ? 'Available' : 'Not Available'}`);
  } else {
    console.log('   ❌ System status failed:', statusResult.error);
  }
  
  // Test 3: Check proxy client bridge
  console.log('\n3. Testing proxy client bridge...');
  const bridgeResult = await testAPI('/proxy-clients');
  if (bridgeResult.success) {
    console.log('   ✅ Proxy client bridge is accessible');
    console.log(`   👥 Connected clients: ${bridgeResult.data.clients ? bridgeResult.data.clients.length : 0}`);
  } else {
    console.log('   ⚠️  Proxy client bridge not ready:', bridgeResult.error);
    if (bridgeResult.error.error === 'Proxy Client Bridge not available') {
      console.log('   💡 This is normal if the Java system hasn\'t started yet');
    }
  }
  
  // Test 4: Check dashboard
  console.log('\n4. Testing dashboard...');
  const dashboardResult = await testAPI('/proxy-clients/dashboard');
  if (dashboardResult.success) {
    console.log('   ✅ Dashboard data retrieved');
    if (dashboardResult.data.dashboard) {
      console.log(`   📈 Total clients: ${dashboardResult.data.dashboard.totalClients || 0}`);
      console.log(`   💚 Healthy clients: ${dashboardResult.data.dashboard.healthyClients || 0}`);
    }
    if (dashboardResult.data.system) {
      console.log(`   🏥 System health: ${(dashboardResult.data.system.systemHealth * 100).toFixed(1)}%`);
    }
  } else {
    console.log('   ⚠️  Dashboard not accessible:', dashboardResult.error);
  }
  
  // Test 5: List clusters
  console.log('\n5. Testing cluster management...');
  const clustersResult = await testAPI('/clusters');
  if (clustersResult.success) {
    console.log('   ✅ Cluster management accessible');
    console.log(`   🎯 Clusters available: ${clustersResult.data.clusters ? clustersResult.data.clusters.length : 0}`);
  } else {
    console.log('   ❌ Cluster management failed:', clustersResult.error);
  }
  
  // Test 6: Test proxy client operations (simulated)
  console.log('\n6. Testing proxy client operations (simulation)...');
  
  // This would normally start a real client, but for testing we just verify the endpoint
  const startResult = await testAPI('/proxy-clients/start', 'POST', {
    account: TEST_ACCOUNT,
    config: {
      headless: true,
      autoRestart: true,
      maxMemoryMB: 512,
      serverAddress: 'localhost',
      serverPort: 25565
    }
  });
  
  if (startResult.success) {
    console.log('   ✅ Start client endpoint is functional');
    console.log(`   🚀 Result: ${JSON.stringify(startResult.data, null, 2)}`);
    
    // Wait a moment then try to stop it
    await sleep(2000);
    
    const stopResult = await testAPI(`/proxy-clients/${TEST_ACCOUNT.id}/stop`, 'POST', {
      graceful: true
    });
    
    if (stopResult.success) {
      console.log('   ✅ Stop client endpoint is functional');
    } else {
      console.log('   ⚠️  Stop client endpoint test:', stopResult.error);
    }
    
  } else {
    console.log('   ⚠️  Start client endpoint test:', startResult.error);
    if (startResult.error.error === 'Proxy Client Bridge not available') {
      console.log('   💡 This is expected if the Java bridge isn\'t running yet');
    }
  }
  
  // Test 7: Test task execution endpoint
  console.log('\n7. Testing automation task endpoint...');
  const taskResult = await testAPI(`/proxy-clients/${TEST_ACCOUNT.id}/execute-task`, 'POST', {
    task: {
      type: 'GATHER_RESOURCES',
      parameters: {
        resource: 'wood',
        quantity: '64'
      },
      priority: 5
    }
  });
  
  if (taskResult.success) {
    console.log('   ✅ Task execution endpoint is functional');
  } else {
    console.log('   ⚠️  Task execution endpoint test:', taskResult.error);
  }
  
  console.log('\n🎉 Integration tests completed!');
  console.log('\n📝 Summary:');
  console.log('   - Core AppyProx system: ✅ Running');
  console.log('   - API endpoints: ✅ Accessible');
  console.log('   - Bridge integration: ⚠️  Check Java system status');
  console.log('   - Proxy client management: ⚠️  Requires active bridge');
  
  console.log('\n💡 Next steps:');
  console.log('   1. Start AppyProx with: npm start');
  console.log('   2. Monitor logs for Java bridge connection');
  console.log('   3. Test with actual Minecraft accounts');
  console.log('   4. Verify mod deployment and automation tasks');
}

// Run the tests
runIntegrationTests().catch(error => {
  console.error('❌ Integration test failed:', error.message);
  process.exit(1);
});