#!/bin/bash

# Testing startup script for AppyProx

echo "🧪 Starting AppyProx in Testing Mode"
echo "===================================="

# Backup original accounts if they exist
if [[ -f "configs/accounts.json" ]] && [[ ! -f "configs/accounts.backup.json" ]]; then
    cp configs/accounts.json configs/accounts.backup.json
    echo "Backed up original accounts.json"
fi

# Use test accounts
if [[ -f "configs/accounts.test.json" ]]; then
    cp configs/accounts.test.json configs/accounts.json
    echo "Using test accounts configuration"
fi

# Create logs directory
mkdir -p logs

# Start AppyProx
echo "Starting AppyProx with test configuration..."
npm start
