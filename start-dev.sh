#!/bin/bash

# Development startup script for AppyProx

echo "🚀 Starting AppyProx Development Environment"
echo "==========================================="

# Check if logs directory exists
mkdir -p logs

# Start with development logging
export NODE_ENV=development
export DEBUG=appyprox:*

# Start AppyProx
echo "Starting AppyProx server..."
npm run dev
