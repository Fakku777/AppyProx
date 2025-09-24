#!/bin/bash

# AppyProx Auto-Deploy Script
# Automatically starts the AppyProx system when the Fabric mod loads

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APPYPROX_ROOT="$(dirname "$SCRIPT_DIR")"
LOG_FILE="$HOME/.minecraft/logs/appyprox-auto-deploy.log"
PID_FILE="$HOME/.minecraft/appyprox.pid"
CONFIG_DIR="$HOME/.minecraft/appyprox"

# Logging function
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Create necessary directories
setup_directories() {
    log "Setting up AppyProx directories..."
    
    mkdir -p "$CONFIG_DIR"
    mkdir -p "$(dirname "$LOG_FILE")"
    mkdir -p "$HOME/.minecraft/mods"
    
    # Create logs directory
    mkdir -p "$HOME/.minecraft/logs"
}

# Copy default configurations if they don't exist
setup_configurations() {
    log "Setting up AppyProx configurations..."
    
    # Copy main AppyProx configs to Minecraft directory
    if [[ ! -f "$CONFIG_DIR/config.json" && -f "$APPYPROX_ROOT/configs/default.json" ]]; then
        log "Copying default configuration..."
        cp "$APPYPROX_ROOT/configs/default.json" "$CONFIG_DIR/config.json"
    fi
    
    if [[ ! -f "$CONFIG_DIR/accounts.json" && -f "$APPYPROX_ROOT/configs/accounts.default.json" ]]; then
        log "Copying default accounts configuration..."
        cp "$APPYPROX_ROOT/configs/accounts.default.json" "$CONFIG_DIR/accounts.json"
    fi
    
    if [[ ! -f "$CONFIG_DIR/clusters.json" && -f "$APPYPROX_ROOT/configs/clusters.default.json" ]]; then
        log "Copying default clusters configuration..."
        cp "$APPYPROX_ROOT/configs/clusters.default.json" "$CONFIG_DIR/clusters.json"
    fi
    
    # Create auto-deploy specific configuration
    cat > "$CONFIG_DIR/auto-deploy.json" << EOF
{
  "enabled": true,
  "startupDelay": 5000,
  "maxRetries": 3,
  "retryDelay": 10000,
  "autoStartProxy": true,
  "autoConnectBridge": true,
  "healthCheckInterval": 30000,
  "shutdownOnMinecraftExit": true,
  "logLevel": "info",
  "paths": {
    "appyproxRoot": "$APPYPROX_ROOT",
    "configDir": "$CONFIG_DIR",
    "logFile": "$LOG_FILE",
    "pidFile": "$PID_FILE"
  },
  "services": {
    "main": {
      "enabled": true,
      "command": "npm start",
      "workingDir": "$APPYPROX_ROOT",
      "port": 3000
    },
    "bridge": {
      "enabled": true,
      "port": 8082,
      "timeout": 30000
    }
  }
}
EOF
    
    log "Auto-deploy configuration created"
}

# Check if AppyProx system is already running
check_running() {
    if [[ -f "$PID_FILE" ]]; then
        local pid=$(cat "$PID_FILE")
        if ps -p "$pid" > /dev/null 2>&1; then
            log "AppyProx system is already running (PID: $pid)"
            return 0
        else
            log "Stale PID file found, removing..."
            rm -f "$PID_FILE"
        fi
    fi
    return 1
}

# Wait for system dependencies
wait_for_dependencies() {
    log "Waiting for system dependencies..."
    
    # Wait for network interfaces to be ready
    local max_wait=30
    local wait_count=0
    
    while [[ $wait_count -lt $max_wait ]]; do
        if ping -c 1 -W 1 127.0.0.1 > /dev/null 2>&1; then
            log "Network interface ready"
            break
        fi
        
        wait_count=$((wait_count + 1))
        sleep 1
    done
    
    if [[ $wait_count -ge $max_wait ]]; then
        log "WARNING: Network interface may not be ready"
    fi
}

# Start AppyProx main system
start_appyprox_main() {
    log "Starting AppyProx main system..."
    
    cd "$APPYPROX_ROOT"
    
    # Check if node_modules exists
    if [[ ! -d "node_modules" ]]; then
        log "Installing AppyProx dependencies..."
        npm install
        if [[ $? -ne 0 ]]; then
            log "ERROR: Failed to install dependencies"
            return 1
        fi
    fi
    
    # Set environment variables for integration
    export APPYPROX_CONFIG_PATH="$CONFIG_DIR"
    export APPYPROX_LOG_FILE="$LOG_FILE"
    export APPYPROX_AUTO_DEPLOY="true"
    export APPYPROX_MINECRAFT_MODE="true"
    
    # Start AppyProx in background
    log "Launching AppyProx system..."
    
    # Create a startup script that handles the process
    cat > "$CONFIG_DIR/startup.sh" << 'EOF'
#!/bin/bash
cd "$1"
npm start > "$2" 2>&1 &
echo $! > "$3"
EOF
    
    chmod +x "$CONFIG_DIR/startup.sh"
    
    # Start the system
    nohup bash "$CONFIG_DIR/startup.sh" "$APPYPROX_ROOT" "$LOG_FILE" "$PID_FILE" &
    local startup_pid=$!
    
    # Wait a moment for the system to start
    sleep 5
    
    # Check if the main process started successfully
    if [[ -f "$PID_FILE" ]]; then
        local main_pid=$(cat "$PID_FILE")
        if ps -p "$main_pid" > /dev/null 2>&1; then
            log "AppyProx main system started successfully (PID: $main_pid)"
            return 0
        else
            log "ERROR: AppyProx main system failed to start"
            return 1
        fi
    else
        log "ERROR: PID file not created, startup may have failed"
        return 1
    fi
}

# Wait for AppyProx services to be ready
wait_for_services() {
    log "Waiting for AppyProx services to be ready..."
    
    local max_wait=60
    local wait_count=0
    
    # Wait for main API to be ready
    while [[ $wait_count -lt $max_wait ]]; do
        if curl -s -f http://localhost:3000/health > /dev/null 2>&1; then
            log "AppyProx API is ready"
            break
        fi
        
        wait_count=$((wait_count + 1))
        sleep 1
        
        if [[ $((wait_count % 10)) -eq 0 ]]; then
            log "Still waiting for AppyProx API... ($wait_count/$max_wait)"
        fi
    done
    
    if [[ $wait_count -ge $max_wait ]]; then
        log "WARNING: AppyProx API may not be ready"
        return 1
    fi
    
    # Wait for bridge to be ready
    wait_count=0
    while [[ $wait_count -lt $max_wait ]]; do
        if nc -z localhost 8082 > /dev/null 2>&1; then
            log "AppyProx bridge is ready"
            return 0
        fi
        
        wait_count=$((wait_count + 1))
        sleep 1
        
        if [[ $((wait_count % 10)) -eq 0 ]]; then
            log "Still waiting for AppyProx bridge... ($wait_count/$max_wait)"
        fi
    done
    
    log "WARNING: AppyProx bridge may not be ready"
    return 1
}

# Verify system health
verify_system_health() {
    log "Verifying AppyProx system health..."
    
    # Check API health endpoint
    local health_response=$(curl -s http://localhost:3000/health 2>/dev/null || echo "")
    if [[ "$health_response" == *"healthy"* ]]; then
        log "AppyProx system health check passed"
    else
        log "WARNING: AppyProx system health check failed"
        return 1
    fi
    
    # Check system status
    local status_response=$(curl -s http://localhost:3000/status 2>/dev/null || echo "")
    if [[ "$status_response" == *"running"* ]]; then
        log "AppyProx system status check passed"
    else
        log "WARNING: AppyProx system status check failed"
        return 1
    fi
    
    return 0
}

# Setup shutdown handler for when Minecraft exits
setup_shutdown_handler() {
    log "Setting up shutdown handler..."
    
    # Create shutdown script
    cat > "$CONFIG_DIR/shutdown.sh" << EOF
#!/bin/bash

LOG_FILE="$LOG_FILE"
PID_FILE="$PID_FILE"

log() {
    echo "[\$(date +'%Y-%m-%d %H:%M:%S')] \$1" >> "\$LOG_FILE"
}

log "AppyProx auto-deploy shutdown initiated..."

if [[ -f "\$PID_FILE" ]]; then
    PID=\$(cat "\$PID_FILE")
    if ps -p "\$PID" > /dev/null 2>&1; then
        log "Stopping AppyProx system (PID: \$PID)..."
        kill -TERM "\$PID"
        
        # Wait for graceful shutdown
        for i in {1..10}; do
            if ! ps -p "\$PID" > /dev/null 2>&1; then
                log "AppyProx system stopped gracefully"
                break
            fi
            sleep 1
        done
        
        # Force kill if still running
        if ps -p "\$PID" > /dev/null 2>&1; then
            log "Force stopping AppyProx system..."
            kill -KILL "\$PID"
        fi
    fi
    
    rm -f "\$PID_FILE"
fi

log "AppyProx auto-deploy shutdown complete"
EOF
    
    chmod +x "$CONFIG_DIR/shutdown.sh"
    
    # Register shutdown handler
    trap "$CONFIG_DIR/shutdown.sh" EXIT
}

# Main deployment function
main() {
    log "=== AppyProx Auto-Deploy Started ==="
    log "Fabric mod initialization triggered auto-deployment"
    
    # Check if already running
    if check_running; then
        log "AppyProx system already running, skipping deployment"
        exit 0
    fi
    
    # Setup directories and configurations
    setup_directories
    setup_configurations
    
    # Wait for system dependencies
    wait_for_dependencies
    
    # Setup shutdown handler
    setup_shutdown_handler
    
    # Start AppyProx system with retries
    local max_retries=3
    local retry_count=0
    
    while [[ $retry_count -lt $max_retries ]]; do
        log "Starting AppyProx system (attempt $((retry_count + 1))/$max_retries)..."
        
        if start_appyprox_main; then
            # Wait for services to be ready
            if wait_for_services; then
                # Verify system health
                if verify_system_health; then
                    log "AppyProx system deployed successfully!"
                    log "=== Auto-Deploy Complete ==="
                    
                    # Create status file for mod to check
                    echo "DEPLOYED" > "$CONFIG_DIR/deploy-status"
                    exit 0
                else
                    log "System health verification failed"
                fi
            else
                log "Services failed to become ready"
            fi
        else
            log "Failed to start AppyProx main system"
        fi
        
        retry_count=$((retry_count + 1))
        
        if [[ $retry_count -lt $max_retries ]]; then
            log "Retrying in 10 seconds..."
            sleep 10
        fi
    done
    
    log "ERROR: Failed to deploy AppyProx system after $max_retries attempts"
    echo "FAILED" > "$CONFIG_DIR/deploy-status"
    exit 1
}

# Handle command line arguments
case "${1:-deploy}" in
    "deploy")
        main
        ;;
    "stop")
        log "Manual stop requested"
        if [[ -f "$CONFIG_DIR/shutdown.sh" ]]; then
            "$CONFIG_DIR/shutdown.sh"
        else
            log "No shutdown script found"
        fi
        ;;
    "status")
        if check_running; then
            echo "AppyProx system is running"
            exit 0
        else
            echo "AppyProx system is not running"
            exit 1
        fi
        ;;
    "restart")
        log "Restart requested"
        "$0" stop
        sleep 5
        "$0" deploy
        ;;
    *)
        echo "Usage: $0 {deploy|stop|status|restart}"
        echo "  deploy  - Start AppyProx system (default)"
        echo "  stop    - Stop AppyProx system"
        echo "  status  - Check if system is running"
        echo "  restart - Restart AppyProx system"
        exit 1
        ;;
esac