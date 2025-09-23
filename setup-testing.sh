#!/bin/bash

# AppyProx Complete Testing Setup Script
# This script prepares the entire project for comprehensive testing

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Project root
PROJECT_ROOT="/home/april/Projects/AppyProx"
FABRIC_MOD_DIR="$PROJECT_ROOT/AppyProx-FabricMod"

print_header() {
    echo -e "${PURPLE}"
    echo "=============================================="
    echo "         AppyProx Testing Setup"
    echo "=============================================="
    echo -e "${NC}"
}

print_step() {
    echo -e "${CYAN}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_dependencies() {
    print_step "Checking system dependencies..."
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed. Please install Node.js 18+ first."
        exit 1
    fi
    
    NODE_VERSION=$(node --version | cut -c 2-)
    print_success "Node.js version: $NODE_VERSION"
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        print_error "npm is not installed."
        exit 1
    fi
    
    NPM_VERSION=$(npm --version)
    print_success "npm version: $NPM_VERSION"
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17+ first."
        exit 1
    fi
    
    JAVA_VERSION=$(java --version 2>&1 | head -n 1)
    print_success "Java: $JAVA_VERSION"
    
    # Check Gradle (in Fabric mod directory)
    if [[ -f "$FABRIC_MOD_DIR/gradlew" ]]; then
        print_success "Gradle wrapper found in Fabric mod directory"
    else
        print_warning "Gradle wrapper not found in Fabric mod directory"
    fi
}

setup_directories() {
    print_step "Setting up project directories..."
    
    cd "$PROJECT_ROOT"
    
    # Create necessary directories if they don't exist
    mkdir -p logs
    mkdir -p cache
    mkdir -p data/backups
    mkdir -p mods
    mkdir -p schemas
    
    print_success "Project directories verified"
}

install_dependencies() {
    print_step "Installing Node.js dependencies..."
    
    cd "$PROJECT_ROOT"
    
    if [[ ! -d "node_modules" ]]; then
        npm install
        print_success "Node.js dependencies installed"
    else
        print_success "Node.js dependencies already installed"
    fi
}

setup_configuration() {
    print_step "Setting up configuration files..."
    
    cd "$PROJECT_ROOT/configs"
    
    # Create accounts.json if it doesn't exist
    if [[ ! -f "accounts.json" ]]; then
        cp accounts.default.json accounts.json
        print_success "Created accounts.json from default"
    else
        print_success "accounts.json already exists"
    fi
    
    # Create clusters.json if it doesn't exist  
    if [[ ! -f "clusters.json" ]]; then
        cp clusters.default.json clusters.json
        print_success "Created clusters.json from default"
    else
        print_success "clusters.json already exists"
    fi
    
    print_success "Configuration files ready"
}

build_fabric_mod() {
    print_step "Building Fabric mod..."
    
    if [[ -d "$FABRIC_MOD_DIR" ]]; then
        cd "$FABRIC_MOD_DIR"
        
        # Set Java 17 environment
        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
        
        # Build the mod
        ./gradlew build -x test
        
        if [[ $? -eq 0 ]]; then
            print_success "Fabric mod built successfully"
        else
            print_error "Failed to build Fabric mod"
            return 1
        fi
    else
        print_warning "Fabric mod directory not found"
    fi
}

verify_auto_deploy_scripts() {
    print_step "Verifying auto-deploy scripts..."
    
    if [[ -f "$FABRIC_MOD_DIR/auto-deploy.sh" ]]; then
        chmod +x "$FABRIC_MOD_DIR/auto-deploy.sh"
        print_success "auto-deploy.sh is executable"
    else
        print_warning "auto-deploy.sh not found"
    fi
    
    if [[ -f "$FABRIC_MOD_DIR/verify-deployment.sh" ]]; then
        chmod +x "$FABRIC_MOD_DIR/verify-deployment.sh"
        print_success "verify-deployment.sh is executable"
    else
        print_warning "verify-deployment.sh not found"
    fi
}

create_test_accounts() {
    print_step "Creating test account configuration..."
    
    cd "$PROJECT_ROOT/configs"
    
    # Create a test accounts file
    cat > accounts.test.json << 'EOF'
{
  "accounts": [
    {
      "id": "test_account_1",
      "username": "TestBot1",
      "uuid": "550e8400-e29b-41d4-a716-446655440001",
      "accessToken": "test_access_token_1",
      "enabled": false,
      "cluster": "test_cluster",
      "proxy_client": {
        "enabled": true,
        "auto_start": false,
        "config": {
          "headless": true,
          "serverAddress": "localhost",
          "serverPort": 25565
        }
      }
    },
    {
      "id": "test_account_2", 
      "username": "TestBot2",
      "uuid": "550e8400-e29b-41d4-a716-446655440002",
      "accessToken": "test_access_token_2",
      "enabled": false,
      "cluster": "test_cluster",
      "proxy_client": {
        "enabled": true,
        "auto_start": false,
        "config": {
          "headless": true,
          "serverAddress": "localhost",
          "serverPort": 25565
        }
      }
    }
  ]
}
EOF
    
    print_success "Test accounts configuration created"
}

create_testing_scripts() {
    print_step "Creating additional testing scripts..."
    
    cd "$PROJECT_ROOT"
    
    # Create a comprehensive test runner
    cat > run-all-tests.sh << 'EOF'
#!/bin/bash

# Comprehensive AppyProx Test Runner

echo "🧪 Running AppyProx Comprehensive Tests"
echo "======================================="

# 1. Run Node.js unit tests
echo "1. Running Node.js unit tests..."
npm test

# 2. Build Fabric mod with tests
echo "2. Building and testing Fabric mod..."
cd AppyProx-FabricMod
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
./gradlew build
cd ..

# 3. Run integration tests (requires system to be running)
echo "3. Running integration tests..."
echo "   Note: Start AppyProx with 'npm start' in another terminal first"
read -p "   Is AppyProx running? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    node test-integration.js
else
    echo "   Skipping integration tests - start AppyProx first"
fi

# 4. Test auto-deployment system
echo "4. Testing auto-deployment verification..."
if [[ -f "AppyProx-FabricMod/verify-deployment.sh" ]]; then
    cd AppyProx-FabricMod
    ./verify-deployment.sh
    cd ..
else
    echo "   Auto-deployment verification script not found"
fi

echo "🎉 All tests completed!"
EOF
    chmod +x run-all-tests.sh
    
    # Create a development startup script
    cat > start-dev.sh << 'EOF'
#!/bin/bash

# Development startup script for AppyProx

echo "🚀 Starting AppyProx Development Environment"
echo "==========================================="

# Check if logs directory exists
mkdir -p logs

# Start with development logging
export NODE_ENV=development
export DEBUG=appyprox:*

# Start AppyProx
echo "Starting AppyProx server..."
npm run dev
EOF
    chmod +x start-dev.sh
    
    # Create a testing startup script
    cat > start-testing.sh << 'EOF'
#!/bin/bash

# Testing startup script for AppyProx

echo "🧪 Starting AppyProx in Testing Mode"
echo "===================================="

# Backup original accounts if they exist
if [[ -f "configs/accounts.json" ]] && [[ ! -f "configs/accounts.backup.json" ]]; then
    cp configs/accounts.json configs/accounts.backup.json
    echo "Backed up original accounts.json"
fi

# Use test accounts
if [[ -f "configs/accounts.test.json" ]]; then
    cp configs/accounts.test.json configs/accounts.json
    echo "Using test accounts configuration"
fi

# Create logs directory
mkdir -p logs

# Start AppyProx
echo "Starting AppyProx with test configuration..."
npm start
EOF
    chmod +x start-testing.sh
    
    print_success "Testing scripts created"
}

create_readme_testing() {
    print_step "Creating testing documentation..."
    
    cd "$PROJECT_ROOT"
    
    cat > TESTING_SETUP.md << 'EOF'
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
EOF

    print_success "Testing documentation created"
}

run_quick_verification() {
    print_step "Running quick verification tests..."
    
    cd "$PROJECT_ROOT"
    
    # Test Node.js syntax
    echo "Checking Node.js files..."
    node -c src/proxy/main.js
    node -c test-integration.js
    
    # Test Fabric mod build
    if [[ -d "$FABRIC_MOD_DIR" ]]; then
        echo "Testing Fabric mod build..."
        cd "$FABRIC_MOD_DIR"
        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
        ./gradlew compileJava -x test --quiet
        
        if [[ $? -eq 0 ]]; then
            print_success "Fabric mod compilation test passed"
        else
            print_warning "Fabric mod compilation test failed"
        fi
        cd "$PROJECT_ROOT"
    fi
    
    print_success "Quick verification completed"
}

# Main execution
main() {
    print_header
    
    echo -e "Setting up comprehensive testing environment for AppyProx...\n"
    
    check_dependencies
    echo
    
    setup_directories  
    echo
    
    install_dependencies
    echo
    
    setup_configuration
    echo
    
    build_fabric_mod
    echo
    
    verify_auto_deploy_scripts
    echo
    
    create_test_accounts
    echo
    
    create_testing_scripts
    echo
    
    create_readme_testing
    echo
    
    run_quick_verification
    echo
    
    print_header
    echo -e "${GREEN}🎉 AppyProx Testing Setup Complete!${NC}\n"
    
    echo -e "${CYAN}Next Steps:${NC}"
    echo -e "1. ${YELLOW}Read the testing guide:${NC} cat TESTING_SETUP.md"
    echo -e "2. ${YELLOW}Start testing:${NC} ./start-testing.sh"
    echo -e "3. ${YELLOW}Run integration tests:${NC} ./test-integration.js"
    echo -e "4. ${YELLOW}Test Fabric mod:${NC} cd AppyProx-FabricMod && ./gradlew runClient"
    echo -e "5. ${YELLOW}Run all tests:${NC} ./run-all-tests.sh"
    
    echo -e "\n${GREEN}Happy Testing! 🚀${NC}"
}

# Execute main function
main "$@"