#!/usr/bin/env node

const { exec } = require('child_process');
const util = require('util');
const execAsync = util.promisify(exec);

console.log('🔧 Testing AppyProx API Integration...\n');

const baseUrl = 'http://localhost:25577';

const apiTests = [
  // System endpoints
  { name: 'System Status', method: 'GET', path: '/api/status' },
  { name: 'Health Check', method: 'GET', path: '/api/health' },
  { name: 'Error Statistics', method: 'GET', path: '/api/errors' },
  { name: 'Circuit Breakers', method: 'GET', path: '/api/circuit-breakers' },
  { name: 'TPS Metrics', method: 'GET', path: '/api/tps' },
  
  // Data endpoints
  { name: 'Players List', method: 'GET', path: '/api/players' },
  { name: 'Clusters List', method: 'GET', path: '/api/clusters' },
  { name: 'Tasks List', method: 'GET', path: '/api/tasks' },
  { name: 'Map Data', method: 'GET', path: '/api/map-data' },
  
  // Groups endpoints
  { name: 'Groups List', method: 'GET', path: '/api/groups' },
  { name: 'Groups Status', method: 'GET', path: '/api/groups-status' },
  
  // Static files
  { name: 'Minecraft Font', method: 'GET', path: '/static/minecraft_font.ttf' },
  { name: 'GUI Textures', method: 'GET', path: '/static/textures/menu_background.png' },
  
  // Main pages
  { name: 'Main Interface', method: 'GET', path: '/' },
  { name: 'Style CSS', method: 'GET', path: '/style.css' },
  { name: 'Client JS', method: 'GET', path: '/script.js' }
];

const postTests = [
  // POST endpoints testing
  {
    name: 'Create Group',
    method: 'POST',
    path: '/api/groups',
    data: { name: 'API Test Group', type: 'mining', maxSize: 3 }
  },
  {
    name: 'Create Task',
    method: 'POST',
    path: '/api/tasks',
    data: { type: 'gather', parameters: { resource: 'stone', quantity: 10 }, clusterId: 'test-cluster' }
  },
  {
    name: 'Set TPS Rate',
    method: 'POST',
    path: '/api/tps/set-rate',
    data: { rate: 15 }
  }
];

async function testGet(test) {
  try {
    const { stdout, stderr } = await execAsync(`curl -s -w "\\n%{http_code}" "${baseUrl}${test.path}"`);
    const lines = stdout.trim().split('\\n');
    const statusCode = lines[lines.length - 1];
    const response = lines.slice(0, -1).join('\\n');
    
    let result = {
      name: test.name,
      path: test.path,
      method: test.method,
      statusCode: parseInt(statusCode),
      success: statusCode >= 200 && statusCode < 300,
      responseSize: response.length
    };
    
    // Try to parse JSON response
    try {
      result.data = JSON.parse(response);
    } catch (e) {
      result.data = response.substring(0, 100) + (response.length > 100 ? '...' : '');
    }
    
    return result;
  } catch (error) {
    return {
      name: test.name,
      path: test.path,
      method: test.method,
      success: false,
      error: error.message
    };
  }
}

async function testPost(test) {
  try {
    const jsonData = JSON.stringify(test.data);
    const { stdout } = await execAsync(
      `curl -s -w "\\n%{http_code}" -X POST -H "Content-Type: application/json" -d '${jsonData}' "${baseUrl}${test.path}"`
    );
    const lines = stdout.trim().split('\\n');
    const statusCode = lines[lines.length - 1];
    const response = lines.slice(0, -1).join('\\n');
    
    let result = {
      name: test.name,
      path: test.path,
      method: test.method,
      statusCode: parseInt(statusCode),
      success: statusCode >= 200 && statusCode < 300,
      responseSize: response.length
    };
    
    try {
      result.data = JSON.parse(response);
    } catch (e) {
      result.data = response;
    }
    
    return result;
  } catch (error) {
    return {
      name: test.name,
      path: test.path,
      method: test.method,
      success: false,
      error: error.message
    };
  }
}

async function runTests() {
  console.log('Testing GET endpoints...');
  let passedTests = 0;
  let totalTests = 0;
  
  for (const test of apiTests) {
    totalTests++;
    const result = await testGet(test);
    
    if (result.success) {
      console.log(`✅ ${result.name}: ${result.statusCode} (${result.responseSize} bytes)`);
      passedTests++;
    } else {
      console.log(`❌ ${result.name}: ${result.statusCode || 'ERROR'} - ${result.error || 'Failed'}`);
    }
  }
  
  console.log('\\nTesting POST endpoints...');
  for (const test of postTests) {
    totalTests++;
    const result = await testPost(test);
    
    if (result.success) {
      console.log(`✅ ${result.name}: ${result.statusCode}`);
      if (result.data && typeof result.data === 'object') {
        console.log(`   Response: ${JSON.stringify(result.data, null, 2).substring(0, 200)}...`);
      }
      passedTests++;
    } else {
      console.log(`❌ ${result.name}: ${result.statusCode || 'ERROR'} - ${result.error || 'Failed'}`);
    }
  }
  
  console.log(`\\n📊 API Test Results: ${passedTests}/${totalTests} tests passed`);
  if (passedTests === totalTests) {
    console.log('🎉 All API tests successful!');
  } else {
    console.log(`⚠️  ${totalTests - passedTests} tests failed`);
  }
}

runTests().catch(console.error);