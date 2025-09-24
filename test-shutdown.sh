#!/bin/bash

echo "🧪 Testing AppyProx Demo shutdown behavior..."
echo "This will start the demo and automatically shut it down after 10 seconds"
echo "Press Ctrl+C to manually test shutdown or wait for automatic shutdown"
echo ""

# Start the demo in background
timeout 10s node demo-appyprox.js &
DEMO_PID=$!

# Wait a bit then send SIGINT
sleep 5
echo ""
echo "🛑 Sending SIGINT to test graceful shutdown..."
kill -INT $DEMO_PID

# Wait for it to finish
wait $DEMO_PID 2>/dev/null
EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ Demo shut down cleanly (exit code: $EXIT_CODE)"
elif [ $EXIT_CODE -eq 143 ]; then
    echo "✅ Demo terminated by signal (exit code: $EXIT_CODE) - this is expected"
else
    echo "⚠️  Demo exit code: $EXIT_CODE"
fi

echo "🏁 Shutdown test completed"