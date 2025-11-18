#!/bin/bash

# Test Recorder - Build and Test Script

echo "========================================"
echo "Test Recorder - Build and Test Script"
echo "========================================"
echo ""

# Check prerequisites
echo "Checking prerequisites..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed"
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed"
    exit 1
fi

echo "✓ Java version: $(java -version 2>&1 | head -1)"
echo "✓ Maven version: $(mvn -version | head -1)"
echo ""

# Clean and compile
echo "Building project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

echo "✓ Build successful"
echo ""

# Run tests
echo "Running Cucumber tests..."
mvn test

if [ $? -ne 0 ]; then
    echo "WARNING: Some tests failed"
else
    echo "✓ All tests passed"
fi

echo ""
echo "========================================"
echo "Build Complete!"
echo "========================================"
echo ""
echo "Test reports:"
echo "  HTML: target/cucumber-reports.html"
echo "  JSON: target/cucumber.json"
echo ""
echo "To run the application:"
echo "  java -cp target/test-recorder-1.0.0.jar com.testrecorder.TestRecorderApplication"
echo ""
