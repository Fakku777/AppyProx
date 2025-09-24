#!/usr/bin/env node

const WebSocket = require('ws');

console.log('🔧 Testing WebSocket Connection to AppyProx Web UI...\n');

const ws = new WebSocket('ws://localhost:25577');

ws.on('open', function open() {
  console.log('✅ WebSocket connected successfully!');
  
  // Test sending a command
  console.log('📡 Sending test command...');
  ws.send(JSON.stringify({
    type: 'command',
    command: 'status',
    timestamp: Date.now()
  }));
  
  // Test subscribing to updates
  console.log('📡 Subscribing to real-time updates...');
  ws.send(JSON.stringify({
    type: 'subscribe',
    events: ['system_stats', 'group_updates', 'task_updates'],
    timestamp: Date.now()
  }));
});

ws.on('message', function message(data) {
  try {
    const parsed = JSON.parse(data);
    console.log('📨 Received message:', parsed.type || 'unknown');
    console.log('   Data:', JSON.stringify(parsed, null, 2));
  } catch (e) {
    console.log('📨 Received raw message:', data.toString());
  }
});

ws.on('error', function error(err) {
  console.error('❌ WebSocket error:', err.message);
});

ws.on('close', function close() {
  console.log('🔌 WebSocket connection closed');
});

// Keep the connection alive for testing
setTimeout(() => {
  console.log('\n⏰ Test completed, closing connection...');
  ws.close();
}, 5000);