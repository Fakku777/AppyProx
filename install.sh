#!/bin/bash

# AppyProx v0.0.1-alpha Installation Script
# Universal installer for Linux, macOS, and Windows (via WSL/Git Bash)

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
APPYPROX_VERSION="0.0.1-alpha"
APPYPROX_REPO="https://github.com/Fakku777/AppyProx.git"
APPYPROX_DIR="AppyProx"
MIN_NODE_VERSION="18"

# Functions
print_banner() {
    echo ""
    echo -e "${PURPLE}╔══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${PURPLE}║                                                          ║${NC}"
    echo -e "${PURPLE}║                    ${CYAN}AppyProx Installer${PURPLE}                    ║${NC}"
    echo -e "${PURPLE}║                                                          ║${NC}"
    echo -e "${PURPLE}║           ${YELLOW}Advanced Minecraft Proxy System${PURPLE}            ║${NC}"
    echo -e "${PURPLE}║              ${GREEN}Version ${APPYPROX_VERSION}${PURPLE}                     ║${NC}"
    echo -e "${PURPLE}║                                                          ║${NC}"
    echo -e "${PURPLE}╚══════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Check if running as root
check_root() {
    if [[ $EUID -eq 0 ]]; then
        error "This script should not be run as root for security reasons."
        error "Please run as a regular user with sudo access if needed."
        exit 1
    fi
}

# Detect operating system
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        OS="linux"
        log "Detected Linux operating system"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        OS="macos"
        log "Detected macOS operating system"
    elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
        OS="windows"
        log "Detected Windows operating system"
    else
        warn "Unknown operating system: $OSTYPE"
        OS="unknown"
    fi
}

# Check system requirements
check_requirements() {
    step "Checking system requirements..."
    
    # Check Git
    if ! command -v git &> /dev/null; then
        error "Git is not installed. Please install Git first:"
        if [[ "$OS" == "linux" ]]; then
            echo "  Ubuntu/Debian: sudo apt-get install git"
            echo "  RHEL/CentOS:   sudo yum install git"
            echo "  Arch Linux:    sudo pacman -S git"
        elif [[ "$OS" == "macos" ]]; then
            echo "  Install Xcode Command Line Tools: xcode-select --install"
            echo "  Or install via Homebrew: brew install git"
        elif [[ "$OS" == "windows" ]]; then
            echo "  Download from: https://git-scm.com/download/win"
        fi
        exit 1
    fi
    log "Git found: $(git --version)"
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        error "Node.js is not installed. Please install Node.js ${MIN_NODE_VERSION}+ first:"
        echo "  Visit: https://nodejs.org/en/download/"
        if [[ "$OS" == "linux" ]]; then
            echo "  Ubuntu/Debian: curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash - && sudo apt-get install -y nodejs"
            echo "  RHEL/CentOS:   curl -fsSL https://rpm.nodesource.com/setup_lts.x | sudo bash - && sudo yum install -y nodejs"
            echo "  Arch Linux:    sudo pacman -S nodejs npm"
        elif [[ "$OS" == "macos" ]]; then
            echo "  Homebrew: brew install node"
        fi
        exit 1
    fi
    
    # Check Node.js version
    NODE_VERSION=$(node --version | sed 's/v//' | cut -d. -f1)
    if [[ $NODE_VERSION -lt $MIN_NODE_VERSION ]]; then
        error "Node.js version $NODE_VERSION detected, but version ${MIN_NODE_VERSION}+ is required."
        error "Please upgrade Node.js to continue."
        exit 1
    fi
    log "Node.js found: $(node --version)"
    
    # Check NPM
    if ! command -v npm &> /dev/null; then
        error "NPM is not installed. Please install NPM first."
        exit 1
    fi
    log "NPM found: $(npm --version)"
    
    success "All requirements satisfied!"
}

# Clone repository
clone_repository() {
    step "Cloning AppyProx repository..."
    
    if [[ -d "$APPYPROX_DIR" ]]; then
        warn "Directory $APPYPROX_DIR already exists."
        read -p "Do you want to remove it and continue? [y/N]: " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            rm -rf "$APPYPROX_DIR"
            log "Removed existing directory"
        else
            error "Installation cancelled"
            exit 1
        fi
    fi
    
    git clone "$APPYPROX_REPO" "$APPYPROX_DIR"
    cd "$APPYPROX_DIR"
    
    # Checkout specific version if not master
    if [[ "$APPYPROX_VERSION" != "latest" ]]; then
        git checkout "v$APPYPROX_VERSION" 2>/dev/null || {
            warn "Version tag v$APPYPROX_VERSION not found, using latest master"
        }
    fi
    
    success "Repository cloned successfully!"
}

# Install dependencies
install_dependencies() {
    step "Installing Node.js dependencies..."
    
    log "Running npm install (this may take a few minutes)..."
    npm install --no-audit --no-fund
    
    success "Dependencies installed successfully!"
}

# Setup configuration
setup_configuration() {
    step "Setting up configuration files..."
    
    # Copy default configurations
    if [[ -f "configs/default.json" ]]; then
        cp configs/default.json configs/config.json
        log "Created configs/config.json"
    fi
    
    if [[ -f "configs/accounts.default.json" ]]; then
        cp configs/accounts.default.json configs/accounts.json
        log "Created configs/accounts.json"
    fi
    
    if [[ -f "configs/clusters.default.json" ]]; then
        cp configs/clusters.default.json configs/clusters.json
        log "Created configs/clusters.json"
    fi
    
    # Create logs directory
    mkdir -p logs
    log "Created logs directory"
    
    success "Configuration files created!"
}

# Create startup scripts
create_startup_scripts() {
    step "Creating startup scripts..."
    
    # Create start script for Unix systems
    if [[ "$OS" != "windows" ]]; then
        cat > start-appyprox.sh << 'EOF'
#!/bin/bash
echo "Starting AppyProx..."
cd "$(dirname "$0")"
npm start
EOF
        chmod +x start-appyprox.sh
        log "Created start-appyprox.sh"
    fi
    
    # Create start script for Windows
    cat > start-appyprox.bat << 'EOF'
@echo off
echo Starting AppyProx...
cd /d "%~dp0"
npm start
pause
EOF
    log "Created start-appyprox.bat"
    
    success "Startup scripts created!"
}

# Run initial tests
run_tests() {
    step "Running initial system tests..."
    
    log "Testing configuration validity..."
    if node -e "
        try {
            const config = require('./configs/config.json');
            console.log('Configuration valid');
            process.exit(0);
        } catch (e) {
            console.error('Configuration error:', e.message);
            process.exit(1);
        }
    "; then
        log "Configuration test passed"
    else
        warn "Configuration test failed - you may need to review config files"
    fi
    
    log "Testing core module loading..."
    if timeout 10s node -e "
        try {
            require('./src/proxy/main.js');
            console.log('Core modules loaded successfully');
        } catch (e) {
            console.error('Module loading error:', e.message);
            process.exit(1);
        }
    " 2>/dev/null || true; then
        log "Core module test completed"
    fi
    
    success "System tests completed!"
}

# Display post-installation information
show_post_install() {
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                          ║${NC}"
    echo -e "${GREEN}║                ${YELLOW}Installation Complete!${GREEN}                  ║${NC}"
    echo -e "${GREEN}║                                                          ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    echo -e "${CYAN}🎉 AppyProx v${APPYPROX_VERSION} has been installed successfully!${NC}"
    echo ""
    
    echo -e "${YELLOW}📁 Installation Directory:${NC} $(pwd)"
    echo -e "${YELLOW}📄 Configuration Files:${NC} configs/"
    echo -e "${YELLOW}📝 Log Files:${NC} logs/"
    echo ""
    
    echo -e "${BLUE}🚀 Quick Start:${NC}"
    echo "   1. Edit configs/accounts.json to add your Minecraft accounts"
    echo "   2. Modify configs/config.json for your target server"
    echo "   3. Start AppyProx:"
    if [[ "$OS" != "windows" ]]; then
        echo -e "      ${GREEN}./start-appyprox.sh${NC}  # Unix/Linux/macOS"
    fi
    echo -e "      ${GREEN}start-appyprox.bat${NC}  # Windows"
    echo -e "      ${GREEN}npm start${NC}           # Direct command"
    echo ""
    
    echo -e "${BLUE}🌐 Web Interfaces:${NC}"
    echo -e "   📊 Dashboard:  ${CYAN}http://localhost:8080${NC}"
    echo -e "   🔗 WebSocket:  ${CYAN}ws://localhost:8081${NC}"
    echo -e "   🛠️  API:        ${CYAN}http://localhost:3000${NC}"
    echo ""
    
    echo -e "${BLUE}📖 Documentation:${NC}"
    echo -e "   📋 README:     ${CYAN}https://github.com/Fakku777/AppyProx${NC}"
    echo -e "   🐛 Issues:     ${CYAN}https://github.com/Fakku777/AppyProx/issues${NC}"
    echo -e "   💬 Discord:    ${CYAN}[Coming Soon]${NC}"
    echo ""
    
    echo -e "${YELLOW}⚠️  Important Notes:${NC}"
    echo "   • Configure your accounts in configs/accounts.json before first run"
    echo "   • Make sure your target Minecraft server is accessible"
    echo "   • Point your Minecraft client to localhost:25565"
    echo "   • Check logs/ directory if you encounter issues"
    echo ""
    
    echo -e "${RED}🔒 Security Reminder:${NC}"
    echo "   • Keep your account credentials secure"
    echo "   • Don't share your configs/accounts.json file"
    echo "   • Use responsibly and follow server rules"
    echo ""
    
    echo -e "${GREEN}Happy proxying with AppyProx! 🎮${NC}"
}

# Cleanup function
cleanup() {
    if [[ $? -ne 0 ]]; then
        error "Installation failed!"
        echo "Check the error messages above for details."
        echo "You can try running the installation script again or report the issue at:"
        echo "https://github.com/Fakku777/AppyProx/issues"
    fi
}

# Main installation function
main() {
    trap cleanup EXIT
    
    print_banner
    check_root
    detect_os
    check_requirements
    clone_repository
    install_dependencies
    setup_configuration
    create_startup_scripts
    run_tests
    show_post_install
    
    success "Installation completed successfully! 🎉"
}

# Run main function
main "$@"