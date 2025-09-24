# AppyProx v0.0.1-alpha Release Creation Guide

This guide explains how to manually create the v0.0.1-alpha release on GitHub.

## 📋 Prerequisites

1. All code has been committed and pushed to the main branch
2. Git tag `v0.0.1-alpha` has been created and pushed
3. You have admin access to the GitHub repository

## 🚀 Creating the Release

### Step 1: Navigate to GitHub Releases

1. Go to https://github.com/Fakku777/AppyProx
2. Click on "Releases" (on the right side of the repository page)
3. Click "Create a new release"

### Step 2: Configure Release Settings

**Tag version**: `v0.0.1-alpha` (should auto-populate if tag exists)

**Release title**: `AppyProx v0.0.1-alpha - Alpha Release`

**Release notes**: Copy and paste the content below:

```markdown
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
```

### Step 3: Configure Release Options

- ✅ Check "Set as a pre-release" (since this is an alpha)
- ✅ Check "Set as the latest release"

### Step 4: Upload Installation Script

1. Click "Attach binaries by dropping them here or selecting them"
2. Upload the `install.sh` file from the root directory
3. The file should appear as "install.sh" in the assets list

### Step 5: Publish Release

1. Review all settings
2. Click "Publish release"

## ✅ Post-Release Verification

After creating the release:

1. **Verify Release Page**: Visit https://github.com/Fakku777/AppyProx/releases/tag/v0.0.1-alpha
2. **Test Install Script**: Verify the download link works:
   ```bash
   curl -fsSL https://github.com/Fakku777/AppyProx/releases/download/v0.0.1-alpha/install.sh
   ```
3. **Test Installation**: Run the installer in a clean environment:
   ```bash
   curl -fsSL https://github.com/Fakku777/AppyProx/releases/download/v0.0.1-alpha/install.sh | bash
   ```

## 📢 Announcement

After the release is published, consider:

1. Updating the main README.md if needed
2. Creating announcement posts (Discord, Reddit, etc.)
3. Notifying contributors and testers
4. Publishing to package managers if applicable

## 🔄 Automated Alternative

If you have GitHub CLI installed and configured, you can use the automated script:

```bash
./scripts/create-release.sh
```

This script will handle all the above steps automatically.

---

## 📝 Release Checklist

- [ ] All code committed and pushed
- [ ] Version updated to 0.0.1-alpha in package.json
- [ ] Git tag v0.0.1-alpha created and pushed
- [ ] README.md is complete and accurate
- [ ] install.sh script is tested and working
- [ ] LICENSE file exists
- [ ] Release created on GitHub
- [ ] install.sh uploaded as release asset
- [ ] Release marked as pre-release
- [ ] Installation script URL tested
- [ ] Full installation tested in clean environment