#!/bin/bash

# Comprehensive AppyProx Test Runner

echo "🧪 Running AppyProx Comprehensive Tests"
echo "======================================="

# 1. Run Node.js unit tests
echo "1. Running Node.js unit tests..."
npm test

# 2. Build Fabric mod with tests
echo "2. Building and testing Fabric mod..."
cd AppyProx-FabricMod
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
./gradlew build
cd ..

# 3. Run integration tests (requires system to be running)
echo "3. Running integration tests..."
echo "   Note: Start AppyProx with 'npm start' in another terminal first"
read -p "   Is AppyProx running? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    node test-integration.js
else
    echo "   Skipping integration tests - start AppyProx first"
fi

# 4. Test auto-deployment system
echo "4. Testing auto-deployment verification..."
if [[ -f "AppyProx-FabricMod/verify-deployment.sh" ]]; then
    cd AppyProx-FabricMod
    ./verify-deployment.sh
    cd ..
else
    echo "   Auto-deployment verification script not found"
fi

echo "🎉 All tests completed!"
