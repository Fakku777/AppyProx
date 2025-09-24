#!/bin/bash

# AppyProx Release Creation Script
# Creates a GitHub release and uploads assets

set -e

# Configuration
VERSION="0.0.1-alpha"
TAG="v${VERSION}"
REPO="Fakku777/AppyProx"
RELEASE_TITLE="AppyProx v${VERSION} - Alpha Release"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Check if GitHub CLI is installed
check_gh_cli() {
    if ! command -v gh &> /dev/null; then
        error "GitHub CLI (gh) is not installed."
        echo "Please install it from: https://github.com/cli/cli#installation"
        echo ""
        echo "Linux:"
        echo "  Ubuntu/Debian: sudo apt install gh"
        echo "  Arch Linux: sudo pacman -S github-cli"
        echo ""
        echo "macOS:"
        echo "  brew install gh"
        echo ""
        echo "Windows:"
        echo "  winget install GitHub.cli"
        echo "  or download from GitHub releases"
        exit 1
    fi
    log "GitHub CLI found: $(gh --version | head -n 1)"
}

# Check authentication
check_auth() {
    if ! gh auth status &> /dev/null; then
        error "Not authenticated with GitHub CLI."
        echo "Please run: gh auth login"
        exit 1
    fi
    log "GitHub authentication verified"
}

# Create release notes
create_release_notes() {
    cat > release_notes.md << 'EOF'
# 🎉 AppyProx v0.0.1-alpha - First Alpha Release

**AppyProx** is an advanced Minecraft proxy system featuring multi-account clustering, AI automation, and centralized management. This is the first alpha release with core functionality implemented.

## ⚡ Quick Installation

### One-Line Install (Recommended)
```bash
curl -fsSL https://github.com/Fakku777/AppyProx/releases/download/v0.0.1-alpha/install.sh | bash
```

### Manual Installation
```bash
git clone https://github.com/Fakku777/AppyProx.git
cd AppyProx
npm install
cp configs/default.json configs/config.json
cp configs/accounts.default.json configs/accounts.json
npm start
```

## 🚀 What's Included

### ✅ Core Features
- **Multi-Version Proxy** - Minecraft 1.18.2 - 1.20.4 support via ViaVersion
- **Account Clustering** - Intelligent grouping and coordination of accounts
- **Real-Time Management** - Web dashboard and REST API
- **High Performance** - Optimized packet handling with compression

### ✅ Advanced Chat System
- **Message Filtering** - Regex-based filtering and processing
- **Command Execution** - Priority-based command queue with tracking
- **Auto-Response** - Configurable automated responses
- **Multi-Bot Communication** - Chat bridging between accounts

### ✅ AI & Automation
- **Baritone Integration** - Advanced pathfinding and movement
- **Task Planning** - Complex task decomposition and execution
- **WikiScraper** - Automatic Minecraft data retrieval
- **Group Coordination** - Formation-based multi-bot control

### ✅ Authentication & Security
- **Microsoft OAuth** - Secure authentication with token refresh
- **Altening Support** - Premium alt account service integration
- **Encrypted Storage** - AES-encrypted credential management
- **Dynamic Switching** - Runtime account management

### ✅ Visualization & Monitoring
- **Web Dashboard** - Real-time monitoring at http://localhost:8080
- **WebSocket API** - Live updates at ws://localhost:8081
- **REST API** - Full control at http://localhost:3000
- **Xaeros Integration** - World mapping and waypoints

### ✅ Fabric Mod Support
- **Client Integration** - Optional Fabric mod for enhanced features
- **Auto-Deployment** - Automated mod installation
- **Direct Control** - In-game management interface

## 📋 System Requirements

- **Node.js** 18.0.0 or higher
- **NPM** or Yarn package manager
- **Git** for installation
- **2GB RAM** minimum (4GB recommended)
- **Java 17+** (for Fabric mod, optional)
- **Linux/Windows/macOS** supported

## 🎮 Getting Started

1. **Install AppyProx** using the installation script above
2. **Configure Accounts** - Edit `configs/accounts.json` with your Minecraft accounts
3. **Set Target Server** - Modify `configs/config.json` for your server
4. **Start AppyProx** - Run `npm start` or `./start-appyprox.sh`
5. **Connect** - Point your Minecraft client to `localhost:25565`

## 🌐 Web Interfaces

- **📊 Dashboard**: http://localhost:8080 - Main control panel
- **🔗 WebSocket**: ws://localhost:8081 - Real-time updates  
- **🛠️ API**: http://localhost:3000 - REST API endpoints

## ⚠️ Alpha Release Notes

This is an **alpha release** intended for testing and development:

- Some features may be unstable or incomplete
- Performance may not be optimized
- Breaking changes may occur in future versions
- Use at your own risk in production environments
- Report bugs at: https://github.com/Fakku777/AppyProx/issues

## 🛠️ Development

```bash
# Clone for development
git clone https://github.com/Fakku777/AppyProx.git
cd AppyProx
npm install --include=dev

# Run in development mode
npm run dev

# Run tests
npm test
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](https://github.com/Fakku777/AppyProx/blob/master/CONTRIBUTING.md) and check the [Issues](https://github.com/Fakku777/AppyProx/issues) page.

## 📚 Documentation

- **[README](https://github.com/Fakku777/AppyProx#readme)** - Complete documentation
- **[Wiki](https://github.com/Fakku777/AppyProx/wiki)** - Detailed guides
- **[API Docs](https://github.com/Fakku777/AppyProx/blob/master/docs/API.md)** - API reference
- **[Issues](https://github.com/Fakku777/AppyProx/issues)** - Bug reports and feature requests

## 🙏 Acknowledgments

- **Zenith Proxy** - Inspiration for the proxy architecture
- **Mineflayer** - Minecraft bot framework  
- **Baritone** - Pathfinding and automation
- **PrismarineJS** - Minecraft protocol implementation

## 🔒 License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/Fakku777/AppyProx/blob/master/LICENSE) file for details.

---

**Happy proxying with AppyProx! 🎮**

Built with ❤️ by the AppyProx Team
EOF
    log "Release notes created"
}

# Main release creation function
main() {
    step "Creating AppyProx v${VERSION} release..."
    
    check_gh_cli
    check_auth
    create_release_notes
    
    step "Creating GitHub release..."
    
    # Create the release
    if gh release create "$TAG" \
        --repo "$REPO" \
        --title "$RELEASE_TITLE" \
        --notes-file release_notes.md \
        --prerelease; then
        log "Release created successfully"
    else
        error "Failed to create release"
        exit 1
    fi
    
    step "Uploading installation script..."
    
    # Upload the installation script
    if gh release upload "$TAG" install.sh \
        --repo "$REPO"; then
        log "Installation script uploaded"
    else
        warn "Failed to upload installation script"
    fi
    
    # Cleanup
    rm -f release_notes.md
    
    step "Release creation completed!"
    echo ""
    echo -e "${GREEN}🎉 AppyProx v${VERSION} has been released!${NC}"
    echo ""
    echo "📋 Release URL: https://github.com/${REPO}/releases/tag/${TAG}"
    echo "💾 Install Script: https://github.com/${REPO}/releases/download/${TAG}/install.sh"
    echo ""
    echo "🚀 Users can now install with:"
    echo "curl -fsSL https://github.com/${REPO}/releases/download/${TAG}/install.sh | bash"
    echo ""
}

# Run main function
main "$@"