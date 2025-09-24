# AppyProx
### Advanced Minecraft Proxy System with Multi-Account Clustering & AI Automation

![Version](https://img.shields.io/badge/version-0.0.1--alpha-orange)
![Node.js](https://img.shields.io/badge/node-%3E%3D18.0.0-brightgreen)
![Platform](https://img.shields.io/badge/platform-linux%20%7C%20windows%20%7C%20macos-lightgrey)
![License](https://img.shields.io/badge/license-MIT-blue)

**AppyProx** is a sophisticated Minecraft player proxy system inspired by Zenith Proxy, featuring multi-account clustering, intelligent automation, and centralized management capabilities. Built with Node.js, it provides advanced coordination, pathfinding, and task automation for multiple Minecraft accounts simultaneously.

---

## 🚀 Key Features

### 🎯 **Core Proxy System**
- **Multi-Version Support** - Compatible with Minecraft 1.18.2 - 1.20.4 via ViaVersion
- **High-Performance Networking** - Optimized packet handling with compression and encryption
- **Account Clustering** - Intelligent grouping and coordination of multiple accounts
- **Real-Time Management** - Live monitoring and control via web interface

### 🤖 **AI-Powered Automation**
- **Baritone Integration** - Advanced pathfinding and movement automation
- **Task Planning** - Complex task decomposition with dependency management
- **WikiScraper** - Automatic Minecraft recipe and crafting data retrieval
- **Intelligent Decision Making** - Context-aware automation with error recovery

### 💬 **Advanced Chat System**
- **Real-Time Filtering** - Regex-based message filtering and processing
- **Command Execution** - Priority-based command queue with response tracking
- **Multi-Bot Communication** - Chat bridging and coordination between accounts
- **Auto-Response System** - Configurable automated responses and keyword detection

### 🔐 **Authentication & Security**
- **Microsoft OAuth** - Secure authentication with automatic token refresh
- **Altening Token API** - Support for premium alt account services
- **Encrypted Storage** - Secure credential management with AES encryption
- **Account Switching** - Dynamic account management and session handling

### 🗺️ **Mapping & Visualization**
- **Xaeros Integration** - Real-time world mapping and waypoint management
- **Central Node Dashboard** - Web-based monitoring and control interface
- **Live Statistics** - Real-time performance metrics and health monitoring
- **Formation Tracking** - Visual representation of bot formations and movements

### 📦 **Fabric Mod Support**
- **Client-Side Integration** - Optional Fabric mod for enhanced features
- **Direct Account Control** - In-game management interface
- **Auto-Deployment** - Automated mod installation and configuration
- **Proxy Client Bridge** - Seamless communication between mod and proxy

---

## 🏗️ Architecture Overview

AppyProx follows a modular, event-driven architecture with five core components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   ProxyServer   │    │ ClusterManager  │    │AutomationEngine│
│                 │◄──►│                 │◄──►│                 │
│ • Packet Handle │    │ • Account Groups│    │ • Task Planning │
│ • ViaVersion    │    │ • Coordination  │    │ • Baritone API  │
│ • Multi-Version │    │ • Health Checks │    │ • WikiScraper   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         ▲                       ▲                       ▲
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 ▼
┌─────────────────┐    ┌─────────────────┐
│   CentralNode   │    │   AppyProxAPI   │
│                 │    │                 │
│ • Web Interface │    │ • REST Endpoints│
│ • WebSocket     │    │ • Health Checks │
│ • Xaeros Maps   │    │ • External APIs │
└─────────────────┘    └─────────────────┘
```

---

## 📋 Requirements

- **Node.js** 18.0.0 or higher
- **NPM** or **Yarn** package manager
- **Git** for installation
- **Java 17+** (for Fabric mod, optional)
- **2GB RAM** minimum (4GB recommended)
- **Linux/Windows/macOS** supported

---

## ⚡ Quick Installation

### Automated Installation (Recommended)

```bash
# Download and run the installation script
curl -fsSL https://github.com/Fakku777/AppyProx/releases/download/v0.0.1-alpha/install.sh | bash

# Or with wget
wget -O- https://github.com/Fakku777/AppyProx/releases/download/v0.0.1-alpha/install.sh | bash
```

### Manual Installation

```bash
# Clone the repository
git clone https://github.com/Fakku777/AppyProx.git
cd AppyProx

# Install dependencies
npm install

# Copy default configurations
cp configs/default.json configs/config.json
cp configs/accounts.default.json configs/accounts.json
cp configs/clusters.default.json configs/clusters.json

# Start AppyProx
npm start
```

---

## ⚙️ Configuration

### Basic Setup

1. **Configure Accounts** (`configs/accounts.json`):
```json
{
  "accounts": [
    {
      "id": "account-1",
      "username": "YourMinecraftUsername",
      "authType": "microsoft",
      "enabled": true
    }
  ]
}
```

2. **Proxy Settings** (`configs/config.json`):
```json
{
  "proxy": {
    "host": "0.0.0.0",
    "port": 25565,
    "version": "1.20.4"
  }
}
```

3. **Target Server**:
   - Point your Minecraft client to `localhost:25565`
   - AppyProx will forward connections to your target server

---

## 🎮 Usage Examples

### Starting AppyProx
```bash
# Standard startup
npm start

# Development mode with auto-restart
npm run dev

# With custom configuration
node src/proxy/main.js --config custom-config.json
```

### Web Interface
- **Dashboard**: http://localhost:8080
- **WebSocket**: ws://localhost:8081
- **API**: http://localhost:3000

### Basic Commands
```bash
# Create a backup
npm run backup

# View cluster status
curl http://localhost:3000/clusters

# Start automation task
curl -X POST http://localhost:3000/tasks \
  -H "Content-Type: application/json" \
  -d '{"type": "gather", "resource": "diamond", "quantity": 64}'
```

---

## 📚 Advanced Features

### Chat System
```javascript
// Add custom chat filter
chatManager.addChatFilter({
  pattern: /spam|advertisement/i,
  action: 'block'
});

// Set up auto-response
chatManager.addAutoResponse(/hello/i, "Hello there!");
```

### Automation Tasks
```javascript
// Queue a mining task
await automationEngine.executeTask({
  type: 'mining',
  target: { x: 100, y: 64, z: 200 },
  resource: 'diamond_ore',
  quantity: 32
});
```

### Group Formations
```javascript
// Create formation
const formation = await groupManager.createFormation({
  type: 'line',
  spacing: 2,
  members: ['bot1', 'bot2', 'bot3']
});
```

---

## 🔧 Development

### Project Structure
```
AppyProx/
├── src/
│   ├── proxy/          # Core proxy server
│   ├── clustering/     # Account management
│   ├── automation/     # AI and task systems
│   ├── chat/          # Advanced chat handling
│   ├── auth/          # Authentication systems
│   ├── pathfinding/   # Movement and navigation
│   ├── groups/        # Multi-account coordination
│   └── web-ui/        # Dashboard and interface
├── configs/           # Configuration files
├── AppyProx-FabricMod/ # Optional Fabric mod
└── docs/             # Documentation
```

### Running Tests
```bash
# Run all tests
npm test

# Test specific components
npm run test -- --testPathPattern=chat

# Integration tests
node test-integration.js
```

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup
```bash
# Fork the repository
git clone https://github.com/yourusername/AppyProx.git
cd AppyProx

# Install development dependencies
npm install --include=dev

# Run in development mode
npm run dev
```

---

## 📖 Documentation

- **[Installation Guide](docs/INSTALLATION.md)** - Detailed setup instructions
- **[Configuration Reference](docs/CONFIGURATION.md)** - Complete config options
- **[API Documentation](docs/API.md)** - REST API and WebSocket reference
- **[Automation Guide](docs/AUTOMATION.md)** - Task creation and management
- **[Troubleshooting](docs/TROUBLESHOOTING.md)** - Common issues and solutions

---

## 🐛 Known Issues & Limitations

- **Alpha Release**: Some features may be unstable
- **Memory Usage**: High memory usage with many concurrent accounts
- **Mod Compatibility**: Some mods may conflict with automation features
- **Server Compatibility**: Optimized for vanilla and Spigot servers

---

## 🛣️ Roadmap

### v0.1.0 (Planned)
- [ ] Enhanced UI with real-time graphs
- [ ] Machine learning for behavior patterns
- [ ] Advanced scripting system
- [ ] Performance optimizations

### v0.2.0 (Future)
- [ ] Plugin system for extensions
- [ ] Multi-server support
- [ ] Advanced anti-detection
- [ ] Mobile management app

---

## 🔗 Links

- **Homepage**: https://github.com/Fakku777/AppyProx
- **Issues**: https://github.com/Fakku777/AppyProx/issues
- **Discussions**: https://github.com/Fakku777/AppyProx/discussions
- **Wiki**: https://github.com/Fakku777/AppyProx/wiki

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Zenith Proxy** - Inspiration for the proxy architecture
- **Mineflayer** - Minecraft bot framework
- **Baritone** - Pathfinding and automation
- **PrismarineJS** - Minecraft protocol implementation
- **Fabric** - Minecraft modding platform

---

## ⚠️ Disclaimer

AppyProx is designed for educational and server administration purposes. Users are responsible for compliance with server rules and Minecraft's Terms of Service. Use responsibly and respect server communities.

---

<div align="center">

**AppyProx v0.0.1-alpha** • Built with ❤️ by the AppyProx Team

[⭐ Star us on GitHub](https://github.com/Fakku777/AppyProx) • [🐛 Report Issues](https://github.com/Fakku777/AppyProx/issues) • [💬 Join Discussions](https://github.com/Fakku777/AppyProx/discussions)

</div>