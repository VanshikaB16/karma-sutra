#!/usr/bin/env bash
# ===================================================================
# KARMA SUTRA™: Divine Grievance & Resolution Office
# One-Click Launch Script
# "The universe has a ticketing system."
# ===================================================================

set -e
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# Ensure OpenJDK is in PATH
if [ -d "/opt/homebrew/opt/openjdk/bin" ]; then
    export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
fi

echo "=========================================================="
echo " Starting Karma Sutra™ Divine Complaint Management System"
echo "=========================================================="

# 1. Start dedicated local MySQL instance if not already running
if ! nc -z 127.0.0.1 3307 2>/dev/null; then
    echo "Starting local MySQL daemon..."
    bash scripts/start-db.sh
fi

# 2. Launch the application
if [ -f "target/karma-sutra-app-1.0.0.jar" ]; then
    echo "Launching standalone executable JAR..."
    java -jar target/karma-sutra-app-1.0.0.jar "$@"
else
    echo "Running via Maven..."
    mvn compile exec:java "$@"
fi
