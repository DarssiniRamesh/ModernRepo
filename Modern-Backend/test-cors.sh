#!/bin/bash
# CORS Configuration Test Script
# Tests that CORS is properly configured for the ModernRepo API

set -e

BASE_URL="http://localhost:3001"
TEST_ORIGIN="http://example.com"

echo "======================================"
echo "CORS Configuration Test"
echo "======================================"
echo ""

# Test 1: Preflight Request
echo "Test 1: CORS Preflight Request (OPTIONS)"
echo "----------------------------------------"
PREFLIGHT_RESPONSE=$(curl -s -i -X OPTIONS \
  -H "Origin: $TEST_ORIGIN" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  "$BASE_URL/questions")

echo "$PREFLIGHT_RESPONSE" | grep -E "HTTP|Access-Control"
echo ""

if echo "$PREFLIGHT_RESPONSE" | grep -q "Access-Control-Allow-Origin"; then
    echo "✅ PASS: CORS preflight headers present"
else
    echo "❌ FAIL: CORS preflight headers missing"
    exit 1
fi
echo ""

# Test 2: Actual Request with Origin
echo "Test 2: GET Request with Origin Header"
echo "---------------------------------------"
GET_RESPONSE=$(curl -s -i -H "Origin: $TEST_ORIGIN" "$BASE_URL/questions")

echo "$GET_RESPONSE" | grep -E "HTTP|Access-Control"
echo ""

if echo "$GET_RESPONSE" | grep -q "Access-Control-Allow-Origin"; then
    echo "✅ PASS: CORS headers present on actual request"
else
    echo "❌ FAIL: CORS headers missing on actual request"
    exit 1
fi
echo ""

# Test 3: OpenAPI Spec Server URL
echo "Test 3: OpenAPI Server URL Configuration"
echo "-----------------------------------------"
OPENAPI_RESPONSE=$(curl -s "$BASE_URL/v3/api-docs")

if echo "$OPENAPI_RESPONSE" | grep -q '"url".*"http://localhost:3001"'; then
    echo "✅ PASS: Server URL correctly configured in OpenAPI spec"
    echo "$OPENAPI_RESPONSE" | grep -A2 '"servers"'
else
    echo "❌ FAIL: Server URL not found or incorrect"
    exit 1
fi
echo ""

# Test 4: Swagger UI Accessibility
echo "Test 4: Swagger UI Accessibility"
echo "----------------------------------"
SWAGGER_RESPONSE=$(curl -s -I "$BASE_URL/swagger-ui.html")

if echo "$SWAGGER_RESPONSE" | grep -q "HTTP/1.1 302"; then
    echo "✅ PASS: Swagger UI redirects correctly"
else
    echo "⚠️  WARNING: Swagger UI may not be accessible"
fi
echo ""

# Test 5: Forward Headers Configuration
echo "Test 5: Forward Headers Support"
echo "--------------------------------"
CONFIG_CHECK=$(grep -r "forward-headers-strategy" ../src/main/resources/ || echo "not found")

if echo "$CONFIG_CHECK" | grep -q "framework"; then
    echo "✅ PASS: Forward headers strategy configured"
else
    echo "⚠️  WARNING: Forward headers strategy not found"
fi
echo ""

# Summary
echo "======================================"
echo "CORS Configuration Test Summary"
echo "======================================"
echo "✅ All critical tests passed!"
echo ""
echo "Next steps:"
echo "1. Restart the application to load new configuration"
echo "2. Open http://localhost:3001/swagger-ui.html in browser"
echo "3. Try 'Execute' on any endpoint"
echo "4. Verify no 'Failed to fetch' errors appear"
echo ""
echo "For production deployment, see CORS_CONFIGURATION.md"
echo "to configure specific allowed origins."
