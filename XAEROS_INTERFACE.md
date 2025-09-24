# 🗺️ AppyProx Xaeros World Map Interface

The new Central Node interface provides a comprehensive Xaeros-style world map with integrated terminal functionality for managing your Minecraft proxy operations.

## 🚀 Quick Start

1. **Start AppyProx**: `npm start`
2. **Open Interface**: Navigate to `http://localhost:25577`
3. **Test Setup**: Open `test-interface.html` to verify everything is working

## 🎮 Interface Overview

### Left Side: Interactive World Map
- **🗺️ Main Map Canvas**: Interactive world view with zoom and pan
- **🧭 Minimap**: Overview of current area with player positions
- **⚙️ Map Controls**: Zoom, center, grid toggle, waypoint management
- **📍 Waypoints Panel**: Add, view, and manage waypoints
- **👥 Players Overlay**: Real-time player positions and status

### Right Side: Integrated Terminal
- **💻 Console Tab**: Command execution with built-in terminal
- **📋 Logs Tab**: System logs with filtering and export
- **⚡ Tasks Tab**: Active task monitoring with progress bars
- **🔧 API Tab**: REST API testing interface

## 🎯 Key Features

### Map Navigation
- **Mouse Controls**:
  - **Click & Drag**: Pan around the world
  - **Mouse Wheel**: Zoom in/out (0.1x to 10x)
  - **Right Click**: Context menu (teleport, waypoint, center)
- **Keyboard Shortcuts**:
  - Use terminal commands for precise navigation

### Terminal Commands
```bash
help                      # Show all available commands
status                    # Display system status
players                   # List connected players
tasks                     # Show active tasks
tp <x> <z>                # Teleport map view to coordinates
waypoint <name> <x> <z>   # Add waypoint at coordinates
clear                     # Clear terminal output
```

### Real-time Updates
- **Player Tracking**: Live position updates every 5 seconds
- **System Monitoring**: Uptime, connections, task progress
- **Status Indicators**: Color-coded player status (mining, building, exploring)

### Waypoint System
- **Add Waypoints**: Right-click map or use terminal commands
- **Visual Markers**: Color-coded waypoint indicators on map
- **Management**: View list, delete unwanted waypoints
- **Persistence**: Waypoints saved in browser session

### API Integration
- **Built-in Tester**: Test API endpoints directly from interface
- **Live Data**: Real-time integration with AppyProx backend
- **JSON Support**: Full request/response handling

## 🎨 Visual Design

### Minecraft-Inspired Theme
- **Dark Color Scheme**: Easy on the eyes for extended use
- **Pixel-Perfect Fonts**: JetBrains Mono for authentic feel
- **Retro Styling**: Consistent with Minecraft/Xaeros aesthetics
- **Responsive Layout**: Adapts to different screen sizes

### Color Coding
- **Players**: 
  - 🟢 Online/Idle: Green
  - 🟠 Mining: Orange  
  - 🔵 Building: Blue
  - 🟢 Exploring: Green
- **Status**:
  - 🟢 Online: Green dot
  - 🔴 Offline: Red dot
- **Logs**:
  - 🔵 INFO: Blue
  - 🟡 WARN: Yellow
  - 🔴 ERROR: Red

## 🔧 Technical Details

### Browser Requirements
- **Modern Browser**: Chrome, Firefox, Safari, Edge
- **JavaScript**: Enabled for full functionality
- **Canvas Support**: Required for map rendering
- **WebSocket**: For real-time updates (future feature)

### Performance
- **Canvas Rendering**: Hardware-accelerated 2D graphics
- **Efficient Updates**: Only re-renders when necessary
- **Memory Management**: Automatic cleanup of old data
- **Responsive**: Smooth interactions even with many players

### API Endpoints
```
GET  /api/status      # System status
GET  /api/accounts    # Connected players
GET  /api/tasks       # Active tasks
POST /api/clusters    # Cluster management
```

## 🛠️ Development

### File Structure
```
src/central-node/
├── CentralNode.js          # Main server and HTML generation
├── routes/                 # API route handlers
└── static/                 # Static assets (if needed)
```

### Key Components
1. **HTML Generator** (`generateDashboardHTML()`): Creates interface structure
2. **CSS Generator** (`generateCSS()`): Minecraft-inspired styling  
3. **JavaScript Engine** (`generateJavaScript()`): Interactive functionality
4. **Helper Functions**: Player lists, waypoints, tasks display

### Customization
- **Colors**: Modify CSS color variables
- **Layout**: Adjust container dimensions
- **Features**: Add new terminal commands or map tools
- **Themes**: Create alternative visual themes

## 🐛 Troubleshooting

### Common Issues

**Map not loading?**
- Check browser console for JavaScript errors
- Verify server is running on port 25577
- Ensure Canvas is supported

**Terminal not responding?**
- Check if JavaScript is enabled
- Verify API endpoints are accessible
- Look for network connectivity issues

**Players not showing?**
- Confirm accounts are connected to proxy
- Check API response at `/api/accounts`
- Verify player position data is available

**Styling issues?**
- Clear browser cache
- Check if external fonts are loading
- Verify CSS is being served correctly

### Debug Mode
```bash
# Enable verbose logging
DEBUG=appyprox:* npm start

# Check specific component
DEBUG=appyprox:central-node npm start
```

## 🔮 Future Enhancements

### Planned Features
- **WebSocket Integration**: Real-time updates without polling
- **Chunk Loading**: Actual Minecraft world data display
- **Route Planning**: Pathfinding visualization with Baritone
- **Multi-dimension**: Seamless switching between dimensions
- **Player Inventories**: Live inventory tracking and display
- **Task Visualization**: Show automation tasks on map
- **Voice Commands**: Voice control for common operations
- **Mobile Support**: Touch-optimized interface for tablets

### Plugin System
- **Custom Overlays**: Add your own map layers
- **Command Extensions**: Create custom terminal commands  
- **Theme Support**: Switch between visual themes
- **Widget Framework**: Add custom UI components

## 📝 Credits

Inspired by:
- **Xaeros World Map**: Visual design and functionality
- **Minecraft**: Color scheme and aesthetic
- **Zenith Proxy**: Architecture and automation concepts
- **JourneyMap**: Interactive mapping features

Built with:
- **Node.js**: Backend server
- **HTML5 Canvas**: Map rendering
- **Modern JavaScript**: Interactive functionality
- **Responsive CSS**: Mobile-friendly design