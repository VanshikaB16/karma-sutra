#!/usr/bin/env bash
# Karma Sutra™ Local MySQL Stopper
pkill -f "karma_sutra_db_data" || true
echo "✓ Local Karma Sutra MySQL process stopped."
