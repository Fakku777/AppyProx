#!/usr/bin/env node

/**
 * AppyProx Port Configuration Utility
 * 
 * This script allows easy configuration of AppyProx ports.
 * 
 * Usage:
 *   node scripts/configure-ports.js --help
 *   node scripts/configure-ports.js --show
 *   node scripts/configure-ports.js --web 25577 --ws 25578 --api 3000 --proxy 25565
 *   node scripts/configure-ports.js --preset minecraft  # Sets optimal ports for Minecraft servers
 */

const fs = require('fs');
const path = require('path');

const CONFIG_PATH = path.join(__dirname, '..', 'configs', 'config.json');
const DEFAULT_CONFIG_PATH = path.join(__dirname, '..', 'configs', 'default.json');

// Port presets for different use cases
const PRESETS = {
  'minecraft': {
    description: 'Optimized for Minecraft server environments',
    ports: {
      proxy: 25565,
      api: 3000,
      web: 25577,
      websocket: 25578,
      bridge: 25800
    }
  },
  'development': {
    description: 'Development-friendly ports (high numbers to avoid conflicts)',
    ports: {
      proxy: 25565,
      api: 8000,
      web: 8080,
      websocket: 8081,
      bridge: 8800
    }
  },
  'production': {
    description: 'Production environment with standard web ports',
    ports: {
      proxy: 25565,
      api: 3000,
      web: 80,
      websocket: 443,
      bridge: 25800
    }
  }
};

function loadConfig(filePath) {
  try {
    const content = fs.readFileSync(filePath, 'utf8');
    return JSON.parse(content);
  } catch (error) {
    console.error(`❌ Error reading config file ${filePath}:`, error.message);
    process.exit(1);
  }
}

function saveConfig(filePath, config) {
  try {
    fs.writeFileSync(filePath, JSON.stringify(config, null, 2) + '\n');
  } catch (error) {
    console.error(`❌ Error writing config file ${filePath}:`, error.message);
    process.exit(1);
  }
}

function showCurrentPorts() {
  console.log('📊 Current AppyProx Port Configuration:\n');
  
  const config = loadConfig(CONFIG_PATH);
  
  console.log(`🎯 Minecraft Proxy:     ${config.proxy.port}`);
  console.log(`🌐 API Server:          ${config.api.port}`);
  console.log(`🖥️  Central Node Web:    ${config.central_node.web_interface_port}`);
  console.log(`📡 WebSocket:           ${config.central_node.websocket_port}`);
  console.log(`🔗 Java Bridge:         ${config.proxy_client_bridge.bridge_port}`);
  
  console.log('\n📌 Quick Access URLs:');
  console.log(`   Central Node: http://localhost:${config.central_node.web_interface_port}`);
  console.log(`   API Health:   http://localhost:${config.api.port}/health`);
  console.log(`   API Status:   http://localhost:${config.api.port}/status`);
}

function updatePorts(options) {
  console.log('🔧 Updating AppyProx port configuration...\n');
  
  // Load both config files
  const config = loadConfig(CONFIG_PATH);
  const defaultConfig = loadConfig(DEFAULT_CONFIG_PATH);
  
  let updated = false;
  
  // Update proxy port
  if (options.proxy) {
    console.log(`📝 Proxy port: ${config.proxy.port} → ${options.proxy}`);
    config.proxy.port = options.proxy;
    defaultConfig.proxy.port = options.proxy;
    updated = true;
  }
  
  // Update API port
  if (options.api) {
    console.log(`📝 API port: ${config.api.port} → ${options.api}`);
    config.api.port = options.api;
    defaultConfig.api.port = options.api;
    updated = true;
  }
  
  // Update web interface port
  if (options.web) {
    console.log(`📝 Central Node web port: ${config.central_node.web_interface_port} → ${options.web}`);
    config.central_node.web_interface_port = options.web;
    defaultConfig.central_node.web_interface_port = options.web;
    updated = true;
  }
  
  // Update websocket port
  if (options.ws || options.websocket) {
    const newPort = options.ws || options.websocket;
    console.log(`📝 WebSocket port: ${config.central_node.websocket_port} → ${newPort}`);
    config.central_node.websocket_port = newPort;
    defaultConfig.central_node.websocket_port = newPort;
    updated = true;
  }
  
  // Update bridge port
  if (options.bridge) {
    console.log(`📝 Java Bridge port: ${config.proxy_client_bridge.bridge_port} → ${options.bridge}`);
    config.proxy_client_bridge.bridge_port = options.bridge;
    defaultConfig.proxy_client_bridge.bridge_port = options.bridge;
    updated = true;
  }
  
  if (updated) {
    // Save both config files
    saveConfig(CONFIG_PATH, config);
    saveConfig(DEFAULT_CONFIG_PATH, defaultConfig);
    
    console.log('\n✅ Port configuration updated successfully!');
    console.log('💡 Restart AppyProx for changes to take effect.\n');
    
    showCurrentPorts();
  } else {
    console.log('⚠️  No port changes specified.');
  }
}

function applyPreset(presetName) {
  if (!PRESETS[presetName]) {
    console.error(`❌ Unknown preset: ${presetName}`);
    console.log('\n📋 Available presets:');
    Object.keys(PRESETS).forEach(name => {
      console.log(`   ${name}: ${PRESETS[name].description}`);
    });
    process.exit(1);
  }
  
  const preset = PRESETS[presetName];
  console.log(`🎨 Applying preset: ${presetName}`);
  console.log(`📝 ${preset.description}\n`);
  
  updatePorts({
    proxy: preset.ports.proxy,
    api: preset.ports.api,
    web: preset.ports.web,
    websocket: preset.ports.websocket,
    bridge: preset.ports.bridge
  });
}

function showHelp() {
  console.log(`🚀 AppyProx Port Configuration Utility

Usage:
  node scripts/configure-ports.js [options]

Options:
  --show                   Show current port configuration
  --proxy PORT            Set Minecraft proxy port (default: 25565)
  --api PORT              Set API server port (default: 3000) 
  --web PORT              Set Central Node web interface port (default: 25577)
  --ws, --websocket PORT  Set WebSocket port (default: 25578)
  --bridge PORT           Set Java Bridge port (default: 25800)
  --preset NAME           Apply a preset configuration
  --help                  Show this help message

Presets:`);

  Object.keys(PRESETS).forEach(name => {
    const preset = PRESETS[name];
    console.log(`  ${name.padEnd(12)} ${preset.description}`);
  });

  console.log(`
Examples:
  # Show current configuration
  node scripts/configure-ports.js --show

  # Change just the web interface port
  node scripts/configure-ports.js --web 9000

  # Change multiple ports
  node scripts/configure-ports.js --web 25577 --ws 25578 --api 4000

  # Apply the minecraft preset
  node scripts/configure-ports.js --preset minecraft

  # Apply development-friendly ports
  node scripts/configure-ports.js --preset development`);
}

// Parse command line arguments
function parseArgs() {
  const args = process.argv.slice(2);
  const options = {};
  
  for (let i = 0; i < args.length; i++) {
    const arg = args[i];
    const nextArg = args[i + 1];
    
    switch (arg) {
      case '--help':
      case '-h':
        showHelp();
        process.exit(0);
        break;
      case '--show':
        showCurrentPorts();
        process.exit(0);
        break;
      case '--proxy':
        options.proxy = parseInt(nextArg);
        i++;
        break;
      case '--api':
        options.api = parseInt(nextArg);
        i++;
        break;
      case '--web':
        options.web = parseInt(nextArg);
        i++;
        break;
      case '--ws':
      case '--websocket':
        options.ws = parseInt(nextArg);
        i++;
        break;
      case '--bridge':
        options.bridge = parseInt(nextArg);
        i++;
        break;
      case '--preset':
        applyPreset(nextArg);
        process.exit(0);
        break;
      default:
        console.error(`❌ Unknown argument: ${arg}`);
        console.log('Use --help for usage information.');
        process.exit(1);
    }
  }
  
  return options;
}

// Main execution
if (require.main === module) {
  console.log('🔧 AppyProx Port Configuration Utility\n');
  
  const options = parseArgs();
  
  if (Object.keys(options).length === 0) {
    console.log('📊 No options provided, showing current configuration:\n');
    showCurrentPorts();
    console.log('\n💡 Use --help for configuration options.');
  } else {
    updatePorts(options);
  }
}

module.exports = { updatePorts, showCurrentPorts, applyPreset, PRESETS };