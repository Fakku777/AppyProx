# AppyProx Port Configuration

AppyProx uses several ports for different services. This document explains how to configure and manage these ports.

## Default Port Configuration

| Service | Port | Purpose |
|---------|------|---------|
| Minecraft Proxy | 25565 | Main proxy server for Minecraft client connections |
| API Server | 3000 | REST API for programmatic control |
| Central Node Web | 25577 | Web management interface |
| WebSocket | 25578 | Real-time communication for Central Node |
| Java Bridge | 25800 | Communication with Fabric mod components |

## Quick Port Configuration

### Show Current Configuration
```bash
node scripts/configure-ports.js --show
```

### Change Individual Ports
```bash
# Change web interface port
node scripts/configure-ports.js --web 9000

# Change API port  
node scripts/configure-ports.js --api 4000

# Change multiple ports at once
node scripts/configure-ports.js --web 25577 --ws 25578 --api 3000
```

### Use Presets

#### Minecraft Preset (Default)
Optimized for Minecraft server environments:
```bash
node scripts/configure-ports.js --preset minecraft
```
- Proxy: 25565 (standard Minecraft)
- API: 3000 (common web service)
- Web: 25577 (avoids common conflicts)
- WebSocket: 25578
- Bridge: 25800

#### Development Preset  
Development-friendly ports:
```bash
node scripts/configure-ports.js --preset development
```
- Proxy: 25565
- API: 8000 (common dev port)
- Web: 8080 (common dev web port)
- WebSocket: 8081
- Bridge: 8800

#### Production Preset
Production environment with standard web ports:
```bash
node scripts/configure-ports.js --preset production
```
- Proxy: 25565
- API: 3000
- Web: 80 (standard HTTP)
- WebSocket: 443 (standard HTTPS)
- Bridge: 25800

## Manual Configuration

You can also edit the configuration files directly:

### Configuration Files
- `configs/config.json` - Active configuration
- `configs/default.json` - Template/backup configuration

### Port Locations in Config
```json
{
  "proxy": {
    "port": 25565
  },
  "central_node": {
    "web_interface_port": 25577,
    "websocket_port": 25578
  },
  "api": {
    "port": 3000
  },
  "proxy_client_bridge": {
    "bridge_port": 25800
  }
}
```

## Common Port Conflicts

### Port 8080 Conflicts
Port 8080 is commonly used by many applications. If you encounter conflicts:
```bash
# Check what's using port 8080
sudo netstat -tlnp | grep :8080
# or
sudo lsof -i :8080

# Change to a different port
node scripts/configure-ports.js --web 25577
```

### Port 3000 Conflicts
Port 3000 is popular for development servers:
```bash
# Change API port if needed
node scripts/configure-ports.js --api 4000
```

### Minecraft Port Conflicts
If running multiple Minecraft servers:
```bash
# Use different proxy port
node scripts/configure-ports.js --proxy 25566
```

## Firewall Configuration

Remember to update your firewall rules when changing ports:

### UFW (Ubuntu/Debian)
```bash
sudo ufw allow 25577/tcp  # Central Node Web
sudo ufw allow 25578/tcp  # WebSocket  
sudo ufw allow 3000/tcp   # API Server
sudo ufw allow 25565/tcp  # Minecraft Proxy
```

### Firewalld (RHEL/CentOS)
```bash
sudo firewall-cmd --permanent --add-port=25577/tcp
sudo firewall-cmd --permanent --add-port=25578/tcp
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --permanent --add-port=25565/tcp
sudo firewall-cmd --reload
```

## Docker Configuration

If using Docker, update your port mappings:
```yaml
# docker-compose.yml
services:
  appyprox:
    ports:
      - "25565:25565"  # Minecraft Proxy
      - "3000:3000"    # API Server  
      - "25577:25577"  # Central Node Web
      - "25578:25578"  # WebSocket
      - "25800:25800"  # Java Bridge
```

## Troubleshooting

### Check Port Availability
```bash
# Check if a port is in use
netstat -an | grep :25577
# or
ss -tlnp | grep :25577
```

### Test Port Connectivity
```bash
# Test web interface
curl http://localhost:25577

# Test API
curl http://localhost:3000/health

# Test if port is listening
telnet localhost 25577
```

### Reset to Defaults
```bash
# Apply minecraft preset to reset to defaults
node scripts/configure-ports.js --preset minecraft
```

## Notes

- Port changes require restarting AppyProx to take effect
- The port configuration utility updates both `config.json` and `default.json`
- Always verify port availability before changing configuration
- Consider using higher port numbers (>1024) to avoid requiring root privileges