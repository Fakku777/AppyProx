#!/usr/bin/env node

const WebSocket = require('ws');

console.log('🔧 Testing AppyProx Map Visualization...\n');

const ws = new WebSocket('ws://localhost:25577');

const testActions = [
  // Test map centering
  { type: 'map_center', x: 100, z: 200 },
  { type: 'map_center', x: -50, z: 150 },
  
  // Test map zoom
  { type: 'map_zoom', zoom: 2.0 },
  { type: 'map_zoom', zoom: 0.5 },
  { type: 'map_zoom', zoom: 1.0 },
  
  // Test waypoint setting
  { type: 'set_waypoint', name: 'Mining Base', x: 100, z: 200, dimension: 'overworld' },
  { type: 'set_waypoint', name: 'Nether Portal', x: -75, z: 300, dimension: 'overworld' },
  
  // Test player info request (simulated)
  { type: 'request_player_info', playerId: 'test_player' }
];

let actionIndex = 0;

ws.on('open', function open() {
  console.log('✅ WebSocket connected for map testing');
  
  // Start testing actions
  testNextAction();
});

function testNextAction() {
  if (actionIndex >= testActions.length) {
    console.log('\n⏰ All map actions tested, closing connection...');
    ws.close();
    return;
  }
  
  const action = testActions[actionIndex];
  console.log(`📡 Testing action: ${action.type}`);
  
  // Send action via WebSocket
  ws.send(JSON.stringify(action));
  
  actionIndex++;
  
  // Test next action after delay
  setTimeout(() => {
    testNextAction();
  }, 1000);
}

ws.on('message', function message(data) {
  try {
    const parsed = JSON.parse(data);
    
    if (parsed.type === 'map_update') {
      console.log(`   📨 Map update:`, parsed.data);
    } else if (parsed.type === 'player_info') {
      console.log(`   📨 Player info:`, parsed.data);
    } else if (parsed.type === 'initial_state') {
      console.log(`   📨 Initial state received`);
    }
  } catch (e) {
    // Ignore non-JSON messages for this test
  }
});

ws.on('error', function error(err) {
  console.error('❌ WebSocket error:', err.message);
});

ws.on('close', function close() {
  console.log('🔌 Map test connection closed');
  
  // Test final map data via API
  setTimeout(async () => {
    const fetch = require('child_process').exec;
    fetch('curl -s http://localhost:25577/api/map-data', (error, stdout) => {
      if (!error) {
        try {
          const mapData = JSON.parse(stdout);
          console.log('\n📊 Final map data:', mapData);
        } catch (e) {
          console.log('Could not parse final map data');
        }
      }
    });
  }, 500);
});