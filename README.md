# AppyProx

![Version](https://img.shields.io/badge/version-0.0.1--alpha-red)
![License](https://img.shields.io/badge/license-MIT-blue)

Advanced Minecraft proxy with clustering, automation, and centralized management capabilities.

## Overview

AppyProx is a sophisticated Minecraft player proxy system inspired by Zenith Proxy, featuring:

- **Multi-Account Clustering**: Manage multiple Minecraft accounts as coordinated groups
- **Intelligent Automation**: AI-driven task completion using Minecraft Wiki data
- **Centralized Management**: Fabric-based central node with enhanced Xaeros World Map
- **Pre-integrated Tools**: ViaVersion, Schematica/Litematica, and Baritone support
- **Extensible API**: Customize and extend functionality for your specific needs

## Features

### Core Functionality
- Server plugin compatibility
- Fabric mod compatibility
- Multi-version support via ViaVersion
- Real-time account clustering and coordination

### Automation & Intelligence
- Minecraft Wiki-powered task optimization
- Resource gathering algorithms
- Recipe analysis and crafting optimization
- Schematic building with Litematica integration
- Baritone pathfinding and automation

### Management Interface
- Enhanced Xaeros World Map integration
- Real-time account monitoring (health, saturation, inventory)
- Interactive account controls and task assignment
- Terminal-based command interface
- Cluster group management

### Data Synchronization
- World/dimension data sharing
- Centralized map exploration tracking
- Cross-account communication
- Backup system with versioning

## Installation

```bash
# Clone the repository
git clone <repository-url>
cd AppyProx

# Install dependencies
npm install

# Create configuration files
cp configs/default.json configs/config.json

# Start the proxy
npm start
```

## Configuration

Main configuration files are located in the `configs/` directory:
- `config.json`: Main proxy configuration
- `accounts.json`: Account credentials and settings
- `clusters.json`: Cluster group definitions
- `tasks.json`: Automation task templates

## Usage

### Starting the Proxy
```bash
npm start
```

### Terminal Commands
```bash
# List all accounts
/accounts list

# Create a cluster group
/cluster create mining_group account1,account2,account3

# Assign a task
/task assign gather_diamonds mining_group

# View world map status
/map status
```

### API Usage
```javascript
const AppyProx = require('./src/api');

const proxy = new AppyProx({
  port: 25565,
  clusters: ['mining', 'building', 'exploration']
});

proxy.createTask('gather_resources', {
  resource: 'diamond',
  quantity: 256,
  cluster: 'mining'
});
```

## Architecture

```
AppyProx/
├── src/
│   ├── proxy/           # Core proxy server
│   ├── clustering/      # Account clustering system
│   ├── automation/      # Task automation engine
│   ├── central-node/    # Management interface
│   └── api/            # Public API
├── configs/            # Configuration files
├── schemas/            # Data schemas
├── mods/              # Bundled mods
└── docs/              # Documentation
```

## Versioning

AppyProx uses semantic versioning with the following scheme:
- **AppyProx-Alpha-x.y.z** where:
  - `z` = alpha version
  - `y` = beta version  
  - `x` = release version

## Contributing

Please read our contributing guidelines before submitting pull requests.

## License

MIT License - see LICENSE file for details.

## Author

**AprilRenders** - Initial work and project maintainer

## Acknowledgments

- Inspired by Zenith Proxy
- Built with minecraft-protocol library
- Integrates ViaVersion, Litematica, Baritone, and Xaeros Map
- Special thanks to the Minecraft modding community