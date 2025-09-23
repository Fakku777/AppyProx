#!/usr/bin/env node

/**
 * Demo script for AppyProx Xaeros Interface
 * Runs the interface with mock data to showcase all features
 */

const path = require('path');
const MockDataGenerator = require('./src/utils/MockDataGenerator');

// Override the API endpoints to use mock data
class DemoCentralNode {
  constructor(port = 25577) {
    this.port = port;
    this.mockData = new MockDataGenerator();
    this.server = null;
    
    console.log('🎮 Starting AppyProx Demo Interface...');
    console.log('📊 Generating mock players, tasks, and logs...');
  }

  async start() {
    const http = require('http');
    const url = require('url');
    
    // Import the original CentralNode for HTML/CSS/JS generation
    const CentralNode = require('./src/central-node/CentralNode');
    const originalNode = new CentralNode();
    
    this.server = http.createServer((req, res) => {
      const parsedUrl = url.parse(req.url, true);
      const { pathname } = parsedUrl;
      
      // Handle static files (HTML, CSS, JS)
      if (pathname === '/' || pathname === '/index.html') {
        res.setHeader('Content-Type', 'text/html');
        res.writeHead(200);
        res.end(this.generateDemoHTML());
      } else if (pathname === '/style.css') {
        res.setHeader('Content-Type', 'text/css');
        res.writeHead(200);
        res.end(originalNode.generateCSS());
      } else if (pathname === '/script.js') {
        res.setHeader('Content-Type', 'application/javascript');
        res.writeHead(200);
        res.end(originalNode.generateJavaScript());
      } 
      // Handle API endpoints with mock data
      else if (pathname === '/api/status') {
        const data = this.mockData.getCurrentData();
        res.setHeader('Content-Type', 'application/json');
        res.writeHead(200);
        res.end(JSON.stringify(data.status));
      } else if (pathname === '/api/accounts') {
        const data = this.mockData.getCurrentData();
        res.setHeader('Content-Type', 'application/json');
        res.writeHead(200);
        res.end(JSON.stringify(data.accounts));
      } else if (pathname === '/api/tasks') {
        const data = this.mockData.getCurrentData();
        res.setHeader('Content-Type', 'application/json');
        res.writeHead(200);
        res.end(JSON.stringify(data.tasks));
      } else if (pathname === '/api/logs') {
        const data = this.mockData.getCurrentData();
        res.setHeader('Content-Type', 'application/json');
        res.writeHead(200);
        res.end(JSON.stringify(data.logs));
      } else if (pathname === '/api/health') {
        const healthData = this.mockData.getHealthMetrics();
        res.setHeader('Content-Type', 'application/json');
        res.writeHead(200);
        res.end(JSON.stringify(healthData));
      } else {
        res.writeHead(404);
        res.end('Not Found');
      }
    });

    this.server.listen(this.port, () => {
      console.log(`🚀 Demo interface running on http://localhost:${this.port}`);
      console.log('🗺️  Features enabled:');
      console.log('   • Interactive Xaeros-style world map');
      console.log('   • 6 simulated bot players with live movement');
      console.log('   • Real-time task progress tracking');
      console.log('   • Live system logs generation');
      console.log('   • Integrated terminal with commands');
      console.log('   • API testing interface');
      console.log('');
      console.log('🎯 Try these commands in the terminal:');
      console.log('   help              - Show available commands');
      console.log('   status            - Display system status');
      console.log('   players           - List connected players');
      console.log('   tp 100 200        - Teleport map view to coordinates');
      console.log('   waypoint base 0 0 - Add waypoint at spawn');
      console.log('');
      console.log('🖱️  Mouse controls:');
      console.log('   Click & Drag      - Pan around the map');
      console.log('   Mouse Wheel       - Zoom in/out');
      console.log('   Right Click       - Context menu');
      console.log('');
      console.log('📊 Mock data includes:');
      console.log(`   • ${this.mockData.mockPlayers.length} simulated players`);
      console.log(`   • ${this.mockData.mockTasks.length} active tasks`);
      console.log('   • Live log generation every 4 seconds');
      console.log('   • Player movement simulation every 3 seconds');
      console.log('');
      console.log('✨ Press Ctrl+C to stop the demo');
    });
  }

  generateDemoHTML() {
    const CentralNode = require('./src/central-node/CentralNode');
    const node = new CentralNode();
    
    // Get the original HTML but modify the title
    let html = node.generateDashboardHTML();
    
    // Replace title to indicate it's a demo
    html = html.replace(
      '<title>AppyProx - Xaeros World Map Interface</title>',
      '<title>AppyProx Demo - Xaeros World Map Interface</title>'
    );
    
    // Add demo banner
    html = html.replace(
      '<div id="status-bar">',
      `<div id="demo-banner" style="background: linear-gradient(45deg, #ff6b6b, #4ecdc4); padding: 8px; text-align: center; color: white; font-weight: bold; font-size: 14px; box-shadow: 0 2px 4px rgba(0,0,0,0.3);">\n        🎮 DEMO MODE - Live simulated data with 6 bot players, real-time tasks, and interactive features\n      </div>\n      <div id="status-bar">`
    );
    
    return html;
  }

  stop() {
    if (this.server) {
      this.server.close();
      console.log('\n🛑 Demo stopped');
    }
  }
}

// Start the demo if this script is run directly
if (require.main === module) {
  const demo = new DemoCentralNode(25577);
  
  demo.start().catch(error => {
    console.error('❌ Failed to start demo:', error);
    process.exit(1);
  });
  
  // Graceful shutdown
  process.on('SIGINT', () => {
    console.log('\n🛑 Shutting down demo...');
    demo.stop();
    process.exit(0);
  });
  
  process.on('SIGTERM', () => {
    console.log('\n🛑 Shutting down demo...');
    demo.stop();
    process.exit(0);
  });
}

module.exports = DemoCentralNode;
