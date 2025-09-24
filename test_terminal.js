#!/usr/bin/env node

const WebSocket = require('ws');

console.log('🔧 Testing AppyProx Command Terminal Integration...\n');

const ws = new WebSocket('ws://localhost:25577');

const commands = [
  'help',
  'status',
  'groups',
  'clients', 
  'tasks',
  'clear',
  'invalid_command'
];

let commandIndex = 0;

ws.on('open', function open() {
  console.log('✅ WebSocket connected for terminal testing');
  
  // Start testing commands
  testNextCommand();
});

function testNextCommand() {
  if (commandIndex >= commands.length) {
    console.log('\n⏰ All terminal commands tested, closing connection...');
    ws.close();
    return;
  }
  
  const command = commands[commandIndex];
  console.log(`📡 Testing command: "${command}"`);
  
  // Send command via WebSocket
  ws.send(JSON.stringify({
    type: 'terminal_command',
    command: command,
    timestamp: Date.now()
  }));
  
  commandIndex++;
  
  // Test next command after delay
  setTimeout(() => {
    testNextCommand();
  }, 1000);
}

ws.on('message', function message(data) {
  try {
    const parsed = JSON.parse(data);
    if (parsed.type === 'terminal_response') {
      console.log(`   📨 Response: ${parsed.output || 'No output'}`);
    } else if (parsed.type === 'command_result') {
      console.log(`   📨 Result: ${parsed.result || 'No result'}`);
    }
  } catch (e) {
    // Ignore non-JSON messages for this test
  }
});

ws.on('error', function error(err) {
  console.error('❌ WebSocket error:', err.message);
});

ws.on('close', function close() {
  console.log('🔌 Terminal test connection closed');
});