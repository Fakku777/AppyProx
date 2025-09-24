#!/usr/bin/env node

const path = require('path');
const fs = require('fs').promises;
const DeploymentManager = require('../src/deployment/DeploymentManager');
const Logger = require('../src/proxy/utils/Logger');

/**
 * AppyProx Deployment Script
 * Handles Node.js-based deployment operations for different environments
 */
class DeploymentScript {
  constructor() {
    this.logger = new Logger({ level: 'info' });
    this.configPath = path.join(__dirname, '../configs/config.json');
  }

  async loadConfig() {
    try {
      const configData = await fs.readFile(this.configPath, 'utf8');
      return JSON.parse(configData);
    } catch (error) {
      this.logger.error('Failed to load configuration:', error.message);
      process.exit(1);
    }
  }

  async deploy(environment, options = {}) {
    this.logger.info(`Starting deployment to ${environment}...`);
    
    try {
      const config = await this.loadConfig();
      
      // Override config for specific environments
      if (environment === 'production') {
        config.deployment = {
          ...config.deployment,
          mode: 'cluster',
          instances: options.instances || require('os').cpus().length,
          loadBalancer: { enabled: true, algorithm: 'least_connections' },
          scaling: { autoScaling: true, minInstances: 2, maxInstances: 8 }
        };
      } else if (environment === 'staging') {
        config.deployment = {
          ...config.deployment,
          mode: 'cluster',
          instances: options.instances || 2,
          loadBalancer: { enabled: true, algorithm: 'round_robin' },
          scaling: { autoScaling: false }
        };
      } else if (environment === 'development') {
        config.deployment = {
          ...config.deployment,
          mode: 'standalone'
        };
      }

      const deploymentManager = new DeploymentManager(config, this.logger);
      
      // Initialize deployment manager
      await deploymentManager.initialize();
      
      // Generate process management scripts
      if (options.generateScripts) {
        await deploymentManager.generateProcessScripts();
      }
      
      // Perform deployment
      await deploymentManager.deploy(environment);
      
      // Show deployment status
      const status = deploymentManager.getDeploymentStatus();
      
      this.logger.info('Deployment Status:');
      this.logger.info(`  Mode: ${status.mode}`);
      this.logger.info(`  Instances: ${status.instances}`);
      this.logger.info(`  Workers: ${status.workers}`);
      this.logger.info(`  Healthy Instances: ${status.healthyInstances}`);
      this.logger.info(`  Load Balancer: ${status.loadBalancer.enabled ? 'Enabled' : 'Disabled'}`);
      this.logger.info(`  Auto-scaling: ${status.scaling.enabled ? 'Enabled' : 'Disabled'}`);
      
      this.logger.info(`✅ Deployment to ${environment} completed successfully!`);
      
    } catch (error) {
      this.logger.error(`❌ Deployment failed:`, error.message);
      process.exit(1);
    }
  }

  async status() {
    try {
      const config = await this.loadConfig();
      const deploymentManager = new DeploymentManager(config, this.logger);
      
      const status = deploymentManager.getDeploymentStatus();
      const metrics = deploymentManager.getScalingMetrics();
      
      console.log('\n📊 AppyProx Deployment Status');
      console.log('================================');
      console.log(`Mode: ${status.mode}`);
      console.log(`Total Instances: ${status.instances}`);
      console.log(`Workers: ${status.workers}`);
      console.log(`Healthy Instances: ${status.healthyInstances}`);
      console.log(`Load Balancer: ${status.loadBalancer.enabled ? '✅ Active' : '❌ Inactive'}`);
      console.log(`Auto-scaling: ${status.scaling.enabled ? '✅ Enabled' : '❌ Disabled'}`);
      
      if (status.scaling.enabled) {
        console.log('\n📈 Scaling Information');
        console.log(`Current/Min/Max: ${status.scaling.currentInstances}/${status.scaling.minInstances}/${status.scaling.maxInstances}`);
        console.log(`Cooldown Active: ${status.scaling.cooldownActive ? 'Yes' : 'No'}`);
        
        if (status.scaling.lastScaleAction) {
          const action = status.scaling.lastScaleAction;
          const timeAgo = Math.round((Date.now() - action.timestamp) / 60000);
          console.log(`Last Action: ${action.action} (${timeAgo}m ago)`);
        }
      }
      
      if (metrics.averages) {
        console.log('\n📊 Performance Metrics');
        console.log(`CPU Usage: ${metrics.averages.cpu.toFixed(1)}%`);
        console.log(`Memory Usage: ${metrics.averages.memory.toFixed(1)}%`);
        console.log(`Request Rate: ${metrics.averages.requestRate.toFixed(1)}/s`);
        console.log(`Response Time: ${metrics.averages.responseTime.toFixed(0)}ms`);
      }
      
      console.log('\n🔧 Process Management');
      console.log(`PID: ${status.processManagement.pid}`);
      console.log(`SystemD Support: ${status.processManagement.systemd ? 'Yes' : 'No'}`);
      console.log(`PM2 Support: ${status.processManagement.pm2 ? 'Yes' : 'No'}`);
      
      console.log(`\nUptime: ${this.formatUptime(status.uptime)}`);
      console.log('');
      
    } catch (error) {
      this.logger.error('Failed to get deployment status:', error.message);
      process.exit(1);
    }
  }

  async generateScripts() {
    try {
      this.logger.info('Generating process management scripts...');
      
      const config = await this.loadConfig();
      const deploymentManager = new DeploymentManager(config, this.logger);
      
      await deploymentManager.generateProcessScripts();
      
      this.logger.info('✅ Process management scripts generated successfully!');
      this.logger.info('Files created:');
      this.logger.info('  - ./deployments/systemd/appyprox.service');
      this.logger.info('  - ./ecosystem.config.js');
      this.logger.info('  - ./scripts/start.sh');
      this.logger.info('  - ./scripts/stop.sh');
      this.logger.info('\nTo install as systemd service:');
      this.logger.info('  sudo cp deployments/systemd/appyprox.service /etc/systemd/system/');
      this.logger.info('  sudo systemctl daemon-reload');
      this.logger.info('  sudo systemctl enable appyprox');
      this.logger.info('  sudo systemctl start appyprox');
      
    } catch (error) {
      this.logger.error('Failed to generate scripts:', error.message);
      process.exit(1);
    }
  }

  formatUptime(uptime) {
    const seconds = Math.floor(uptime / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    
    if (days > 0) {
      return `${days}d ${hours % 24}h ${minutes % 60}m`;
    } else if (hours > 0) {
      return `${hours}h ${minutes % 60}m`;
    } else {
      return `${minutes}m ${seconds % 60}s`;
    }
  }

  showHelp() {
    console.log(`
AppyProx Node.js Deployment Tool

Usage:
  node scripts/deploy.js <command> [options]

Commands:
  deploy <env>           Deploy to environment (development, staging, production)
  status                 Show current deployment status
  scripts               Generate process management scripts (systemd, PM2, start/stop)
  help                   Show this help message

Deploy Options:
  --instances <n>        Number of instances to deploy (cluster mode only)
  --generate-scripts     Generate process management scripts during deployment

Examples:
  node scripts/deploy.js deploy production --instances 4 --generate-scripts
  node scripts/deploy.js deploy staging --instances 2
  node scripts/deploy.js deploy development
  node scripts/deploy.js status
  node scripts/deploy.js scripts

Process Management:
  ./scripts/start.sh standalone    # Start single instance
  ./scripts/start.sh cluster       # Start cluster with PM2 or Node.js cluster
  ./scripts/start.sh daemon        # Start as background daemon
  ./scripts/stop.sh                # Stop all instances
  
  pm2 start ecosystem.config.js    # Using PM2
  sudo systemctl start appyprox    # Using systemd (after installing service)
`);
  }
}

// CLI execution
async function main() {
  const script = new DeploymentScript();
  const args = process.argv.slice(2);
  
  if (args.length === 0) {
    script.showHelp();
    process.exit(0);
  }
  
  const command = args[0];
  const options = {};
  
  // Parse options
  for (let i = 2; i < args.length; i += 2) {
    const flag = args[i];
    const value = args[i + 1];
    
    switch (flag) {
      case '--instances':
        options.instances = parseInt(value);
        break;
      case '--generate-scripts':
        options.generateScripts = true;
        i--; // No value for this flag
        break;
    }
  }
  
  try {
    switch (command) {
      case 'deploy':
        const environment = args[1];
        if (!environment) {
          console.error('Error: Environment is required for deploy command');
          script.showHelp();
          process.exit(1);
        }
        await script.deploy(environment, options);
        break;
        
      case 'status':
        await script.status();
        break;
        
      case 'scripts':
        await script.generateScripts();
        break;
        
      case 'help':
        script.showHelp();
        break;
        
      default:
        console.error(`Error: Unknown command '${command}'`);
        script.showHelp();
        process.exit(1);
    }
    
  } catch (error) {
    console.error('Deployment script failed:', error.message);
    process.exit(1);
  }
}

if (require.main === module) {
  main();
}

module.exports = DeploymentScript;