#!/bin/bash
# Test script to verify OpenAPI server URL configuration

echo "Testing OpenAPI Configuration..."
echo ""

# Check if the application is running
if ! curl -s http://localhost:3001/actuator/health > /dev/null 2>&1; then
    echo "❌ Application is not running. Start it first with ./start.sh"
    exit 1
fi

echo "✅ Application is running"
echo ""

# Test 1: Check OpenAPI spec without forwarded headers (local)
echo "Test 1: Local environment (no forwarded headers)"
echo "Expected: No explicit server URL or relative paths"
RESPONSE=$(curl -s http://localhost:3001/v3/api-docs | jq -r '.servers // empty')
if [ -z "$RESPONSE" ]; then
    echo "✅ No explicit servers configured (using relative paths)"
else
    echo "Response: $RESPONSE"
fi
echo ""

# Test 2: Check OpenAPI spec with forwarded headers (preview simulation)
echo "Test 2: Preview environment simulation (with X-Forwarded headers)"
echo "Expected: Server URL derived from headers"
RESPONSE=$(curl -s -H "X-Forwarded-Proto: https" \
    -H "X-Forwarded-Host: vscode-internal-32563-beta.beta01.cloud.kavia.ai" \
    -H "X-Forwarded-Port: 3001" \
    http://localhost:3001/v3/api-docs | jq -r '.servers[0].url // empty')

if [ "$RESPONSE" = "https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001" ]; then
    echo "✅ Server URL correctly derived: $RESPONSE"
elif [ -n "$RESPONSE" ]; then
    echo "⚠️  Server URL found but doesn't match expected: $RESPONSE"
else
    echo "❌ No server URL found in response"
fi
echo ""

# Test 3: Check if OpenApiServerCustomizer class exists
echo "Test 3: Verify OpenApiServerCustomizer class exists"
if [ -f "src/main/java/com/example/postgresdemo/config/OpenApiServerCustomizer.java" ]; then
    echo "✅ OpenApiServerCustomizer.java exists"
else
    echo "❌ OpenApiServerCustomizer.java not found"
fi
echo ""

# Test 4: Check application properties
echo "Test 4: Verify forward headers configuration"
if grep -q "server.forward-headers-strategy=framework" src/main/resources/application.properties; then
    echo "✅ Forward headers strategy configured"
else
    echo "❌ Forward headers strategy not configured"
fi
echo ""

echo "Testing complete!"
