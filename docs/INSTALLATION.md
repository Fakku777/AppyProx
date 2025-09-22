# AppyProx Installation Guide

## Prerequisites

- Node.js 16+ and npm
- Java 17+ (for Minecraft server compatibility)
- Git

## Quick Start

1. **Clone and setup the project:**
```bash
git clone <repository-url>
cd AppyProx
npm install
```

2. **Configure the proxy:**
```bash
# Copy default configurations
cp configs/default.json configs/config.json
cp configs/accounts.default.json configs/accounts.json
cp configs/clusters.default.json configs/clusters.json
```

3. **Edit your configurations:**
   - Update `configs/config.json` with your preferred settings
   - Add your Minecraft accounts to `configs/accounts.json`
   - Configure cluster groups in `configs/clusters.json`

4. **Start the proxy:**
```bash
npm start
```

## Configuration Files

### Main Configuration (`configs/config.json`)
- Proxy server settings (host, port, version)
- Clustering parameters
- Automation settings
- Central node configuration
- API settings

### Accounts Configuration (`configs/accounts.json`)
- Minecraft account credentials
- Auto-assignment to clusters
- Account-specific settings

### Clusters Configuration (`configs/clusters.json`)
- Predefined cluster groups
- Cluster behavior settings
- Task automation preferences

## First Run

After starting AppyProx, you can:

1. **Connect Minecraft clients to the proxy:**
   - Server address: `localhost:25565` (or your configured host/port)
   - Use in-game commands like `/ap status` to check proxy status

2. **Access the API:**
   - Health check: `http://localhost:3000/health`
   - Status: `http://localhost:3000/status`
   - Full API documentation coming soon

3. **Use the backup system:**
```bash
# Create a backup
npm run backup

# List backups
node scripts/backup.js list
```

## Troubleshooting

- Check logs in the `logs/` directory
- Ensure no other services are using the configured ports
- Verify Minecraft account credentials are correct
- Check network connectivity for Wiki scraping features

## Next Steps

- Set up your first cluster group
- Configure automated tasks
- Integrate with external tools via the API
- Explore the central management interface (when implemented)