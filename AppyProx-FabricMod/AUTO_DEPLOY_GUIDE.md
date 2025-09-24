# AppyProx Auto-Deploy System Guide

This guide explains how to use the AppyProx Auto-Deploy system that automatically starts the AppyProx proxy system when the Fabric mod loads in Minecraft.

## 🚀 Overview

The AppyProx Auto-Deploy system consists of:

1. **Auto-Deploy Script** (`auto-deploy.sh`) - Shell script that manages the AppyProx system lifecycle
2. **AutoDeployManager** - Java class that handles deployment from within the Fabric mod
3. **Verification Script** (`verify-deployment.sh`) - Script to verify deployment status and health
4. **Integration** - Seamless integration with the AppyProxFabricClient mod

## 🎯 Features

- **Automatic startup** when Minecraft with the AppyProx mod loads
- **Health monitoring** with automatic failure detection
- **Graceful shutdown** when Minecraft exits
- **Manual control** via keybindings and scripts
- **Status verification** and troubleshooting tools
- **Configuration management** with auto-setup
- **Retry logic** for robust deployment

## 📋 Prerequisites

- AppyProx main system installed and configured
- AppyProx Fabric mod installed in Minecraft
- Node.js and npm available for AppyProx system
- Network ports 3000, 8081, and 8082 available

## 🎮 In-Game Usage

### Keybindings

- **L** (default) - Auto-Deploy Toggle
  - If system is not running: Starts auto-deployment
  - If system is running: Stops auto-deployment
  - Shows status messages in chat

### Status Messages

The mod will show colored messages in chat:
- 🟢 **Green** messages indicate success
- 🟡 **Yellow** messages indicate warnings or in-progress operations
- 🔴 **Red** messages indicate errors

### Automatic Behavior

When you start Minecraft with the AppyProx mod:
1. The AutoDeployManager initializes
2. After a configurable delay (default 5 seconds), auto-deployment begins
3. The system starts the AppyProx backend automatically
4. Health monitoring begins once deployment is successful
5. When you exit Minecraft, the system shuts down gracefully

## 🛠️ Command Line Usage

### Auto-Deploy Script

```bash
# Start deployment (default action)
./auto-deploy.sh
./auto-deploy.sh deploy

# Stop the deployed system
./auto-deploy.sh stop

# Check if system is running
./auto-deploy.sh status

# Restart the system
./auto-deploy.sh restart
```

### Verification Script

```bash
# Full verification check (recommended)
./verify-deployment.sh

# Quick essential checks
./verify-deployment.sh quick

# Check specific components
./verify-deployment.sh health      # System health only
./verify-deployment.sh process     # Process status only
./verify-deployment.sh status      # Deployment status only

# Follow deployment logs in real-time
./verify-deployment.sh logs
```

## ⚙️ Configuration

### Auto-Deploy Configuration

The system creates `~/.minecraft/appyprox/auto-deploy.json` with these settings:

```json
{
  "enabled": true,
  "startupDelay": 5000,
  "maxRetries": 3,
  "retryDelay": 10000,
  "autoStartProxy": true,
  "autoConnectBridge": true,
  "healthCheckInterval": 30000,
  "shutdownOnMinecraftExit": true,
  "logLevel": "info"
}
```

**Configuration Options:**
- `enabled` - Enable/disable auto-deployment
- `startupDelay` - Delay before starting deployment (ms)
- `maxRetries` - Maximum deployment retry attempts
- `retryDelay` - Delay between retry attempts (ms)
- `autoStartProxy` - Automatically start proxy system
- `autoConnectBridge` - Automatically connect to bridge
- `healthCheckInterval` - Health check frequency (ms)
- `shutdownOnMinecraftExit` - Stop system when Minecraft exits
- `logLevel` - Logging level (debug, info, warn, error)

### File Locations

- **Configuration**: `~/.minecraft/appyprox/`
- **Logs**: `~/.minecraft/logs/appyprox-auto-deploy.log`
- **PID file**: `~/.minecraft/appyprox.pid`
- **Status file**: `~/.minecraft/appyprox/deploy-status`

## 🔍 Monitoring and Troubleshooting

### Status Indicators

The system uses several status files:

1. **Deploy Status** (`~/.minecraft/appyprox/deploy-status`)
   - `DEPLOYED` - System successfully deployed
   - `FAILED` - Deployment failed
   
2. **PID File** (`~/.minecraft/appyprox.pid`)
   - Contains process ID of running AppyProx system
   
3. **Log File** (`~/.minecraft/logs/appyprox-auto-deploy.log`)
   - Detailed deployment and operation logs

### Health Checks

The system performs regular health checks:
- Process existence verification
- API endpoint accessibility (port 3000)
- Bridge connectivity (port 8082)
- WebSocket availability (port 8081)
- System health API responses

### Common Issues and Solutions

#### 1. "Auto-deployment failed to start"
```bash
# Check logs for specific errors
./verify-deployment.sh logs

# Verify AppyProx main system is properly configured
cd /path/to/AppyProx && npm install
```

#### 2. "Bridge not available"
```bash
# Check if Java is available
java --version

# Verify network ports are free
netstat -tulpn | grep -E '3000|8081|8082'

# Manual restart
./auto-deploy.sh restart
```

#### 3. "System health check failed"
```bash
# Run full verification
./verify-deployment.sh

# Check specific endpoint
curl http://localhost:3000/health
```

#### 4. Configuration issues
```bash
# Verify configuration files
./verify-deployment.sh
```

### Debug Mode

Enable debug logging by editing the configuration:
```json
{
  "logLevel": "debug"
}
```

Or set environment variable:
```bash
export DEBUG=appyprox:*
```

## 🚨 Emergency Procedures

### Force Stop Everything
```bash
# Stop via script
./auto-deploy.sh stop

# Manual force stop if needed
pkill -f "appyprox\|npm start"

# Clean up files
rm -f ~/.minecraft/appyprox.pid
```

### Reset Auto-Deploy System
```bash
# Stop system
./auto-deploy.sh stop

# Remove status files
rm -f ~/.minecraft/appyprox/deploy-status
rm -f ~/.minecraft/appyprox.pid

# Clear logs (optional)
rm -f ~/.minecraft/logs/appyprox-auto-deploy.log

# Restart
./auto-deploy.sh deploy
```

### Disable Auto-Deploy
Edit `~/.minecraft/appyprox/auto-deploy.json`:
```json
{
  "enabled": false
}
```

Or remove the configuration file to prevent auto-deployment.

## 🔧 Development and Debugging

### Testing the System

1. **Test deployment script directly:**
   ```bash
   ./auto-deploy.sh deploy
   ./verify-deployment.sh
   ```

2. **Test in Minecraft:**
   - Start Minecraft with the mod
   - Watch for chat messages about deployment
   - Use the L key to toggle deployment
   - Check logs in `~/.minecraft/logs/`

3. **Test health monitoring:**
   ```bash
   ./verify-deployment.sh health
   ```

### Integration with AppyProx Development

The auto-deploy system integrates with your AppyProx development workflow:

1. Make changes to AppyProx code
2. The system will use the latest code on next deployment
3. Use `./auto-deploy.sh restart` to pick up changes
4. Monitor via `./verify-deployment.sh` for any issues

### Adding Custom Monitoring

You can extend the verification script to add custom checks by modifying `verify-deployment.sh` and adding new functions.

## 📚 API Integration

The auto-deploy system exposes the same APIs as the main AppyProx system:

- **Health**: `http://localhost:3000/health`
- **Status**: `http://localhost:3000/status`
- **Proxy Clients**: `http://localhost:3000/proxy-clients`
- **Dashboard**: `http://localhost:3000/proxy-clients/dashboard`

## 🎮 Minecraft Mod Integration

The Fabric mod provides these capabilities:

- **Automatic startup** on mod initialization
- **Status display** in chat messages
- **Keybinding control** for manual management
- **Error reporting** with user-friendly messages
- **Graceful shutdown** on game exit

## 🔄 Updates and Maintenance

To update the auto-deploy system:

1. Update the AppyProx main system as usual
2. The auto-deploy system will use the updated version
3. Restart deployment: `./auto-deploy.sh restart`
4. Verify: `./verify-deployment.sh`

## 📞 Support

If you encounter issues:

1. Run full verification: `./verify-deployment.sh`
2. Check logs: `./verify-deployment.sh logs`
3. Review configuration files in `~/.minecraft/appyprox/`
4. Try manual restart: `./auto-deploy.sh restart`

The auto-deploy system is designed to be robust and self-healing, but these tools will help you diagnose any issues that may arise.