#!/bin/bash

# AppyProx Auto-Deploy Verification Script
# Verifies that the AppyProx system is running correctly after auto-deployment

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_DIR="$HOME/.minecraft/appyprox"
LOG_FILE="$HOME/.minecraft/logs/appyprox-auto-deploy.log"
PID_FILE="$HOME/.minecraft/appyprox.pid"
STATUS_FILE="$CONFIG_DIR/deploy-status"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Colored output functions
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_header() {
    echo -e "${BLUE}==== $1 ====${NC}"
}

# Check if a port is open
check_port() {
    local port=$1
    local service=$2
    
    if nc -z localhost "$port" 2>/dev/null; then
        print_success "$service is running on port $port"
        return 0
    else
        print_error "$service is not accessible on port $port"
        return 1
    fi
}

# Check HTTP endpoint
check_http_endpoint() {
    local url=$1
    local description=$2
    
    local response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null || echo "000")
    
    if [[ "$response" == "200" ]]; then
        print_success "$description is accessible (HTTP 200)"
        return 0
    elif [[ "$response" == "000" ]]; then
        print_error "$description is not accessible (connection failed)"
        return 1
    else
        print_warning "$description responded with HTTP $response"
        return 1
    fi
}

# Check process status
check_process() {
    if [[ ! -f "$PID_FILE" ]]; then
        print_error "PID file not found: $PID_FILE"
        return 1
    fi
    
    local pid=$(cat "$PID_FILE" 2>/dev/null || echo "")
    
    if [[ -z "$pid" ]]; then
        print_error "PID file is empty"
        return 1
    fi
    
    if ps -p "$pid" > /dev/null 2>&1; then
        print_success "AppyProx process is running (PID: $pid)"
        
        # Get process info
        local process_info=$(ps -p "$pid" -o pid,ppid,cmd --no-headers 2>/dev/null || echo "")
        if [[ -n "$process_info" ]]; then
            print_info "Process info: $process_info"
        fi
        
        return 0
    else
        print_error "AppyProx process is not running (PID: $pid)"
        return 1
    fi
}

# Check deployment status file
check_deployment_status() {
    if [[ ! -f "$STATUS_FILE" ]]; then
        print_warning "Deployment status file not found"
        return 1
    fi
    
    local status=$(cat "$STATUS_FILE" 2>/dev/null || echo "UNKNOWN")
    
    case "$status" in
        "DEPLOYED")
            print_success "Deployment status: $status"
            return 0
            ;;
        "FAILED")
            print_error "Deployment status: $status"
            return 1
            ;;
        *)
            print_warning "Deployment status: $status"
            return 1
            ;;
    esac
}

# Check system health via API
check_system_health() {
    local health_url="http://localhost:3000/health"
    
    local response=$(curl -s "$health_url" 2>/dev/null || echo "")
    
    if [[ -n "$response" ]]; then
        if echo "$response" | grep -q "healthy"; then
            print_success "System health check passed"
            print_info "Health response: $response"
            return 0
        else
            print_warning "System health check returned unexpected response"
            print_info "Health response: $response"
            return 1
        fi
    else
        print_error "Could not retrieve system health"
        return 1
    fi
}

# Check bridge connectivity
check_bridge_connectivity() {
    local bridge_url="http://localhost:3000/proxy-clients"
    
    local response=$(curl -s "$bridge_url" 2>/dev/null || echo "")
    
    if [[ -n "$response" ]]; then
        if echo "$response" | grep -q "clients\|bridge"; then
            print_success "Bridge connectivity check passed"
            return 0
        else
            print_warning "Bridge may not be fully ready"
            print_info "Bridge response: $response"
            return 1
        fi
    else
        print_error "Could not check bridge connectivity"
        return 1
    fi
}

# Check configuration files
check_configuration() {
    local configs=(
        "$CONFIG_DIR/config.json:Main configuration"
        "$CONFIG_DIR/accounts.json:Accounts configuration"
        "$CONFIG_DIR/clusters.json:Clusters configuration"
        "$CONFIG_DIR/auto-deploy.json:Auto-deploy configuration"
    )
    
    local all_ok=true
    
    for config_entry in "${configs[@]}"; do
        local config_file="${config_entry%:*}"
        local config_desc="${config_entry#*:}"
        
        if [[ -f "$config_file" ]]; then
            # Check if it's valid JSON
            if jq empty "$config_file" 2>/dev/null; then
                print_success "$config_desc is valid"
            else
                print_error "$config_desc has invalid JSON syntax"
                all_ok=false
            fi
        else
            print_warning "$config_desc not found: $config_file"
            all_ok=false
        fi
    done
    
    if [[ "$all_ok" == "true" ]]; then
        return 0
    else
        return 1
    fi
}

# Check log files
check_logs() {
    if [[ -f "$LOG_FILE" ]]; then
        local log_size=$(stat -f%z "$LOG_FILE" 2>/dev/null || stat -c%s "$LOG_FILE" 2>/dev/null || echo "0")
        print_success "Deployment log exists (size: ${log_size} bytes)"
        
        # Check for recent errors in logs
        local recent_errors=$(tail -n 50 "$LOG_FILE" | grep -i "error\|failed\|exception" | wc -l)
        if [[ "$recent_errors" -gt 0 ]]; then
            print_warning "Found $recent_errors recent errors in logs"
            print_info "Recent errors:"
            tail -n 50 "$LOG_FILE" | grep -i "error\|failed\|exception" | tail -n 5 | while read -r line; do
                echo "  $line"
            done
        else
            print_success "No recent errors found in logs"
        fi
        
        return 0
    else
        print_warning "Deployment log file not found"
        return 1
    fi
}

# Get system metrics
get_system_metrics() {
    print_header "System Metrics"
    
    # Memory usage
    if command -v free >/dev/null; then
        local memory_info=$(free -h | grep Mem)
        print_info "Memory: $memory_info"
    fi
    
    # Load average
    if [[ -r /proc/loadavg ]]; then
        local load_avg=$(cat /proc/loadavg)
        print_info "Load average: $load_avg"
    fi
    
    # Disk usage for Minecraft directory
    if [[ -d "$HOME/.minecraft" ]]; then
        local disk_usage=$(du -sh "$HOME/.minecraft" 2>/dev/null || echo "Unknown")
        print_info "Minecraft directory size: $disk_usage"
    fi
    
    # Network connections
    local connections=$(netstat -tn 2>/dev/null | grep -E ":3000|:8081|:8082|:25565" | wc -l || echo "Unknown")
    print_info "AppyProx related network connections: $connections"
}

# Main verification function
main() {
    print_header "AppyProx Auto-Deploy Verification"
    echo "Starting verification at $(date)"
    echo
    
    local overall_status=0
    
    # 1. Check deployment status
    print_header "Deployment Status"
    if ! check_deployment_status; then
        overall_status=1
    fi
    echo
    
    # 2. Check process
    print_header "Process Status"
    if ! check_process; then
        overall_status=1
    fi
    echo
    
    # 3. Check configuration
    print_header "Configuration Check"
    if ! check_configuration; then
        overall_status=1
    fi
    echo
    
    # 4. Check network services
    print_header "Network Services"
    if ! check_port 3000 "AppyProx API"; then
        overall_status=1
    fi
    if ! check_port 8081 "AppyProx WebSocket"; then
        overall_status=1
    fi
    if ! check_port 8082 "AppyProx Bridge"; then
        overall_status=1
    fi
    echo
    
    # 5. Check HTTP endpoints
    print_header "HTTP Endpoints"
    if ! check_http_endpoint "http://localhost:3000/health" "Health endpoint"; then
        overall_status=1
    fi
    if ! check_http_endpoint "http://localhost:3000/status" "Status endpoint"; then
        overall_status=1
    fi
    echo
    
    # 6. Check system health
    print_header "System Health"
    if ! check_system_health; then
        overall_status=1
    fi
    echo
    
    # 7. Check bridge connectivity
    print_header "Bridge Connectivity"
    if ! check_bridge_connectivity; then
        overall_status=1
    fi
    echo
    
    # 8. Check logs
    print_header "Log Analysis"
    check_logs
    echo
    
    # 9. System metrics
    get_system_metrics
    echo
    
    # Overall result
    print_header "Overall Result"
    if [[ $overall_status -eq 0 ]]; then
        print_success "All verification checks passed!"
        print_info "AppyProx auto-deployment is working correctly"
    else
        print_warning "Some verification checks failed"
        print_info "Please check the issues above and review the logs"
    fi
    
    echo
    echo "Verification completed at $(date)"
    
    return $overall_status
}

# Handle command line arguments
case "${1:-verify}" in
    "verify")
        main
        ;;
    "health")
        check_system_health
        ;;
    "process")
        check_process
        ;;
    "logs")
        if [[ -f "$LOG_FILE" ]]; then
            tail -f "$LOG_FILE"
        else
            echo "Log file not found: $LOG_FILE"
            exit 1
        fi
        ;;
    "status")
        check_deployment_status
        ;;
    "quick")
        # Quick check - just the essentials
        print_header "Quick Verification"
        check_deployment_status && check_process && check_port 3000 "AppyProx API"
        ;;
    *)
        echo "Usage: $0 {verify|health|process|logs|status|quick}"
        echo "  verify  - Full verification (default)"
        echo "  health  - Check system health only"
        echo "  process - Check process status only"
        echo "  logs    - Follow deployment logs"
        echo "  status  - Check deployment status only"
        echo "  quick   - Quick essential checks"
        exit 1
        ;;
esac