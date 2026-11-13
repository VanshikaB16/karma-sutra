#!/usr/bin/env bash
# ===================================================================
# KARMA SUTRA™: Direct 3D Cosmic Orrery Launcher
# "The universe has a ticketing system."
# ===================================================================

set -e
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

if [ -d "/opt/homebrew/opt/openjdk/bin" ]; then
    export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
fi

echo "=========================================================="
echo " 🌌 Launching Karma Sutra™ 3D Cosmic Universe & Orrery"
echo "=========================================================="

# 1. Start dedicated local MySQL instance if not already running
if ! nc -z 127.0.0.1 3307 2>/dev/null; then
    echo "Starting local MySQL daemon..."
    bash scripts/start-db.sh || true
fi

# 2. Check if user wants Vite dev server directly
if [ "$1" == "--dev" ]; then
    echo "Starting Vite interactive dev server on port 5173..."
    cd cosmic-3d
    npm run dev -- --host 127.0.0.1 --port 5173
    exit 0
fi

# 3. Launch application and auto-open browser
if [ -f "target/karma-sutra-app-1.0.0.jar" ]; then
    echo "Starting application with embedded 3D Orrery server..."
    # Open browser after a brief 1.5s delay
    (sleep 1.5 && open "http://localhost:8108") &
    java -jar target/karma-sutra-app-1.0.0.jar
else
    echo "Building and running via Maven..."
    (sleep 4 && open "http://localhost:8108") &
    mvn compile exec:java
fi
