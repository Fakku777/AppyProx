# AppyProx Fabric Mod

A comprehensive Fabric mod that integrates with the AppyProx Minecraft proxy system, providing seamless cluster management, automation controls, and real-time monitoring directly within the Minecraft client.

## 🚀 Features

### Core Functionality
- **Proxy Mode Management**: Toggle between direct play and proxy control modes
- **Multi-Client Instance Support**: Launch and manage multiple Minecraft clients
- **Real-time Backend Integration**: Full API communication with AppyProx Node.js backend
- **Cluster Coordination**: Create, manage, and control groups of proxy accounts

### User Interface
- **Menu Integration**: AppyProx buttons in Multiplayer Menu and Pause Menu
- **Main Management Screen**: Comprehensive interface for cluster and account control
- **Quick Command Interface**: Rapid task assignment and cluster operations
- **Real-time HUD Overlay**: Live status display with account health and cluster information

### Advanced Features
- **Xaeros World Map Integration**: Account tracking and waypoint management (when available)
- **Task Automation**: Pre-built tasks for mining, building, gathering, and base operations
- **Account Health Monitoring**: Real-time health, food, and status tracking
- **Dynamic Mod Management**: System for pushing mods to proxy instances

## 🎮 Controls

### Default Keybindings
- **K** - Open AppyProx Manager Interface
- **P** - Toggle Proxy Mode
- **U** - Quick Cluster Commands
- **H** - Toggle AppyProx HUD Overlay

All keybindings are customizable through Minecraft's standard controls menu.

## 📋 Requirements

- **Minecraft**: 1.20.4+
- **Fabric Loader**: Latest version
- **Fabric API**: Compatible version
- **Java**: 17+
- **AppyProx Backend**: Running Node.js server

### Optional Dependencies
- **Xaeros World Map**: For enhanced map integration and waypoint features
- **ModMenu**: For in-game configuration (recommended)

## 🛠️ Installation

1. **Install Fabric Loader** for Minecraft 1.20.4+
2. **Download Fabric API** and place in `mods` folder
3. **Download AppyProx Fabric Mod** (`appyprox-fabric.jar`) and place in `mods` folder
4. **Start Minecraft** with the Fabric profile

## 🔧 Configuration

The mod automatically connects to `localhost:3000` for the AppyProx backend. Configuration files will be created in:
- `.minecraft/config/appyprox/`

### Backend Setup
Ensure your AppyProx Node.js backend is running on:
- **API Server**: `localhost:3000`
- **WebSocket**: `localhost:8081`

## 🎯 Usage

### Getting Started
1. **Launch Minecraft** with the mod installed
2. **Join a multiplayer server** 
3. **Press K** to open the AppyProx Manager
4. **Enable Proxy Mode** to start controlling remote accounts

### Creating Clusters
1. Open the AppyProx Manager (K key)
2. Enter a cluster name in the text field
3. Click "Create Cluster"
4. Assign accounts to the cluster via the backend interface

### Quick Commands
1. **Press U** for the Quick Command interface
2. Use pre-built buttons for common tasks:
   - **Start Mining**: Begin diamond mining operations
   - **Start Building**: Execute building tasks
   - **Gather Items**: Collect resources
   - **Return to Base**: Send accounts back to base
   - **Stop All**: Halt all current tasks

### Custom Commands
1. Open Quick Commands (U key)
2. Type any Minecraft command in the text field
3. Press Enter or click "Send Command"
4. Commands are sent to all active clusters

### HUD Overlay
- **Toggle with H key** to show/hide real-time information
- **Account Status**: Health, food, current task
- **Cluster Status**: Member count, active tasks
- **Connection Status**: Backend connectivity

## 🏗️ Architecture

### Core Components

#### AppyProxManager
Central coordinator handling:
- Proxy mode state management
- Backend communication
- Cluster coordination
- Task assignment

#### ProxyModeManager  
Advanced multi-client management:
- Instance launching and monitoring
- Mod synchronization across instances
- Process lifecycle management
- Failure recovery

#### Network Layer
Robust communication system:
- HTTP REST API integration
- WebSocket real-time updates
- Async operation handling
- Connection state management

#### UI System
Modern Minecraft-integrated interface:
- Native Fabric screen integration
- Real-time data rendering
- Keyboard shortcut support
- Menu button integration

### Integration Points

#### Xaeros World Map
- Real-time account position tracking
- Dynamic waypoint creation
- Cluster formation visualization
- Interactive map controls

#### Fabric API
- Client-side mod integration
- Event system integration
- Screen and GUI management
- Keybinding registration

## 📊 Status Indicators

### Connection States
- **🟢 Connected**: Full backend connectivity
- **🟡 Ready**: Connected but proxy mode disabled
- **🔴 Offline**: Backend unreachable

### Account Status
- **🟢 Online**: Account active and responsive
- **🟡 Busy**: Account executing tasks
- **🔴 Offline**: Account disconnected
- **🔵 Idle**: Account waiting for commands

### Task Status
- **Mining**: Resource extraction in progress
- **Building**: Construction/schematic execution
- **Gathering**: Item collection tasks
- **Returning**: Moving back to base
- **Idle**: No active tasks

## 🔧 Development

### Building from Source
```bash
git clone <repository>
cd AppyProx-FabricMod
./gradlew build
```

### Development Environment
- **Java 17+** required for compilation
- **Gradle 8.8** for build automation
- **Fabric Development Kit** for mod APIs

### Project Structure
```
src/main/java/dev/aprilrenders/appyprox/
├── core/           # Core management systems
├── ui/             # User interface components
├── network/        # Backend communication
├── proxy/          # Multi-client management
├── integrations/   # Third-party mod integration
└── data/           # Data models and structures
```

## 🤝 Integration with AppyProx Backend

### API Endpoints
- `GET /accounts` - Retrieve proxy accounts
- `GET /clusters` - Retrieve active clusters  
- `POST /clusters` - Create new clusters
- `POST /tasks` - Start cluster tasks
- `POST /clusters/{id}/command` - Send cluster commands

### WebSocket Events
- Real-time account updates
- Task progress notifications
- Cluster state changes
- Health status updates

## 📝 Troubleshooting

### Common Issues

**Mod not loading**
- Verify Fabric Loader and API versions
- Check mod compatibility with Minecraft version
- Review latest.log for error details

**Backend connection failed**
- Ensure AppyProx Node.js server is running
- Verify localhost:3000 is accessible
- Check firewall settings

**Keybindings not working**
- Check for conflicts in Controls settings
- Reset to default keybindings if needed
- Verify mod is properly loaded

**Overlay not showing**
- Press H to toggle overlay visibility
- Check that you're in multiplayer mode
- Verify backend connection status

## 🎯 Future Enhancements

### Planned Features
- **Direct Account Control**: Switch to controlling specific proxy accounts
- **Enhanced Xaeros Integration**: Full waypoint and overlay system
- **Baritone Integration**: Advanced pathfinding coordination
- **Litematica Support**: Schematic building automation
- **Voice Commands**: Audio-based cluster control
- **Mobile App Integration**: Cross-platform management

### Roadmap
1. **v1.1**: Direct account control implementation
2. **v1.2**: Full Xaeros World Map integration
3. **v1.3**: Baritone pathfinding integration  
4. **v1.4**: Advanced automation features
5. **v2.0**: Complete ecosystem integration

## 📄 License

This project is part of the AppyProx ecosystem. See the main AppyProx repository for licensing information.

## 🙏 Contributing

Contributions are welcome! Please see the main AppyProx repository for contribution guidelines and development setup instructions.

---

**AppyProx Fabric Mod** - Seamlessly bridging Minecraft gameplay with advanced proxy automation.