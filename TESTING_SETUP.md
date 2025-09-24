# AppyProx Testing Setup Guide

This guide explains how to set up and run comprehensive tests for the AppyProx system.

## Setup Complete! 🎉

Your AppyProx testing environment is now ready. Here's what has been set up:

### 1. Dependencies Verified ✅
- Node.js and npm
- Java 17+
- Gradle (via wrapper)

### 2. Project Structure ✅
- All necessary directories created
- Configuration files prepared
- Build systems ready

### 3. Testing Scripts Created ✅
- `run-all-tests.sh` - Comprehensive test runner
- `start-dev.sh` - Development environment startup
- `start-testing.sh` - Testing environment startup
- `test-integration.js` - Integration test suite

## Quick Start Testing

### Basic System Test
```bash
# Start AppyProx
./start-testing.sh

# In another terminal, run integration tests
./test-integration.js
```

### Comprehensive Testing
```bash
# Run all tests (unit, integration, build)
./run-all-tests.sh
```

### Fabric Mod Testing
```bash
cd AppyProx-FabricMod
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Build and run client
./gradlew build
./gradlew runClient
```

### Auto-Deploy Testing
```bash
cd AppyProx-FabricMod

# Test deployment verification
./verify-deployment.sh

# Test full auto-deploy (run client)
./gradlew runClient
# Look for auto-deploy messages in console
```

## Test Scenarios

### 1. Core System Functionality
- ✅ Proxy server startup and client connections
- ✅ Cluster management and account coordination  
- ✅ Automation engine task execution
- ✅ Central node web interface
- ✅ API endpoint functionality

### 2. Proxy Client Bridge Integration
- ✅ Java bridge compilation and startup
- ✅ Client management via bridge
- ✅ Task execution through bridge
- ✅ Real-time monitoring and dashboard

### 3. Fabric Mod Integration
- ✅ Mod loading and initialization
- ✅ Auto-deploy system activation
- ✅ Keybinding and manual control
- ✅ Backend communication

### 4. End-to-End Workflows
- ✅ Complete client lifecycle (start → run → stop)
- ✅ Automation task from mod to execution
- ✅ Cluster coordination with multiple clients
- ✅ Monitoring and health management

## Configuration Files

### Test Accounts (`configs/accounts.test.json`)
- Pre-configured test accounts
- Safe for testing without real Minecraft credentials
- Automatically used by testing scripts

### Development Config (`configs/config.json`)
- Updated with proxy client bridge configuration
- Optimized for testing and development
- Debug logging enabled

## Troubleshooting

### Port Conflicts
- AppyProx API: 3000
- Central Node Web: 8080  
- Central Node WebSocket: 8081
- Proxy Client Bridge: 25800
- Minecraft Proxy: 25565

### Java Issues
```bash
# Check Java version
java --version

# Set correct Java home
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Build Issues
```bash
# Clean and rebuild everything
npm run build
cd AppyProx-FabricMod && ./gradlew clean build
```

## Development Workflow

1. **Start Development Environment**
   ```bash
   ./start-dev.sh
   ```

2. **Run Tests During Development**
   ```bash
   # Quick integration test
   node test-integration.js
   
   # Full test suite
   ./run-all-tests.sh
   ```

3. **Test Fabric Mod Changes**
   ```bash
   cd AppyProx-FabricMod
   ./gradlew build && ./gradlew runClient
   ```

4. **Verify Auto-Deploy Changes**
   ```bash
   cd AppyProx-FabricMod
   ./verify-deployment.sh
   ```

## Success Indicators

✅ **System Healthy**: All services start without errors
✅ **API Responsive**: Integration tests pass
✅ **Bridge Connected**: Java bridge connects successfully  
✅ **Mod Loads**: Fabric client starts with mod
✅ **Auto-Deploy Works**: Deployment starts automatically
✅ **End-to-End**: Can control clients from mod to backend

Happy testing! 🚀
