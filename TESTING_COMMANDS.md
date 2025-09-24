# AppyProx WebUI Testing Commands & Browser Instructions

## 🌐 Access the WebUI

### Main Interface
```
http://localhost:25577
```
Open this URL in your browser to see the full Xaeros-style interface with:
- Interactive world map with coordinate grid
- Real-time player tracking
- Task management controls
- System health monitoring
- Dark theme with Minecraft-style colors

## 🧪 API Testing Commands

### Basic Status Checks
```bash
# Overall system status
curl -s "http://localhost:25577/api/status" | jq '.'

# Detailed health metrics  
curl -s "http://localhost:25577/api/health" | jq '.'

# Circuit breaker status
curl -s "http://localhost:25577/api/circuit-breakers" | jq '.'

# Error recovery status
curl -s "http://localhost:25577/api/errors" | jq '.'
```

### Data Endpoints
```bash
# Connected players
curl -s "http://localhost:25577/api/players" | jq '.'

# Active clusters
curl -s "http://localhost:25577/api/clusters" | jq '.'

# Running tasks
curl -s "http://localhost:25577/api/tasks" | jq '.'
```

### Create Test Data
```bash
# Create a test task
curl -s -X POST "http://localhost:25577/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "gather_resource",
    "parameters": {
      "resource": "iron_ore", 
      "quantity": 64
    }
  }' | jq '.'

# Create system backup
curl -s -X POST "http://localhost:25577/api/backup" \
  -H "Content-Type: application/json" \
  -d '{"message": "Test backup"}' | jq '.'
```

### Quick Health Check
```bash
# One-liner system health
curl -s "http://localhost:25577/api/status" | jq '.isRunning, .healthStatus, .connectedAccounts'
```

## 🎮 Minecraft Client Testing

### Connect to Proxy
1. Start Minecraft client
2. Add server: `localhost:25565`
3. Connect and watch the WebUI update in real-time

### Expected Behavior
- Player appears in WebUI player list
- Position updates shown on map
- Connection count increments
- Activity log shows connection events

## 🖥️ Browser Testing Checklist

### Visual Elements
- [ ] Status bar shows system information
- [ ] Map canvas displays coordinate grid
- [ ] Control panels are accessible
- [ ] Health bars show current metrics
- [ ] Activity log displays recent events

### Interactive Features
- [ ] Map zoom in/out works
- [ ] Map panning works
- [ ] Task creation dropdown works
- [ ] Backup button creates backups
- [ ] Health metrics update in real-time

### Real-time Updates
- [ ] WebSocket connection established
- [ ] Status updates automatically
- [ ] Player positions update live
- [ ] Task progress updates shown
- [ ] Health alerts appear as toasts

## 🔧 System Process Checks

### Check AppyProx is Running
```bash
ps aux | grep "node.*main.js" | grep -v grep
```

### Check Port Usage
```bash
ss -tlnp | grep -E "(25565|25577|3000)"
```
Should show:
- 25565: Minecraft proxy server
- 25577: WebUI server  
- 3000: API server

### Check Logs
```bash
tail -f /home/april/Projects/AppyProx/logs/appyprox.log
```

### Stop/Start AppyProx
```bash
# Stop (if running in background)
pkill -f "node.*main.js"

# Start
cd /home/april/Projects/AppyProx && npm start
```

## 🚨 Troubleshooting

### WebUI Not Loading
1. Check if process is running: `ps aux | grep "node.*main.js"`
2. Check if port is open: `ss -tlnp | grep 25577`
3. Test API directly: `curl http://localhost:25577/api/status`

### API Not Responding
1. Check main process health
2. Verify all components initialized successfully
3. Check error logs for initialization issues

### WebSocket Issues
1. Open browser developer tools
2. Check console for WebSocket connection errors
3. Verify WebSocket endpoint accessibility

## 📊 Expected Performance

### System Resources
- Memory: ~140MB (varies with activity)
- CPU: Low usage when idle
- Disk: Minimal I/O for logging

### Response Times
- API endpoints: < 50ms
- WebUI loading: < 2 seconds
- WebSocket updates: Near real-time

### Connection Capacity
- Minecraft connections: 100 concurrent (configurable)
- WebUI connections: Multiple browser tabs supported
- API requests: Rate limited (1000/minute default)

## 🎯 Success Indicators

✅ **WebUI loads at http://localhost:25577**  
✅ **All API endpoints return valid JSON**  
✅ **System health shows all components healthy**  
✅ **WebSocket connection establishes automatically**  
✅ **Task creation and backup operations work**  
✅ **Real-time updates function properly**  

**Your AppyProx WebUI is ready for production testing!** 🚀