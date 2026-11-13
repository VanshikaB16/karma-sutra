#!/usr/bin/env bash
# Karma Sutra™ Local MySQL Orchestrator
# Runs dedicated local MySQL 8.0 instance on port 3307 without password hassles

DB_DIR="/tmp/karma_sutra_db_data"
PORT=3307
MYSQLX_PORT=33070
SOCKET="/tmp/karma_sutra_mysql.sock"
MYSQLD_BIN="/usr/local/mysql/bin/mysqld"

if [ ! -x "$MYSQLD_BIN" ]; then
    MYSQLD_BIN=$(which mysqld || echo "")
fi

echo "=========================================================="
echo " Karma Sutra™ — Dedicated Local MySQL 8.0 Daemon"
echo "=========================================================="

if nc -z 127.0.0.1 $PORT 2>/dev/null; then
    echo "✓ Karma Sutra MySQL is already active and listening on port $PORT."
    exit 0
fi

if [ -z "$MYSQLD_BIN" ] || [ ! -x "$MYSQLD_BIN" ]; then
    echo "⚠️  mysqld not found at /usr/local/mysql/bin/mysqld"
    exit 1
fi

if [ ! -d "$DB_DIR/mysql" ]; then
    echo "Initializing fresh dedicated data directory at $DB_DIR..."
    mkdir -p "$DB_DIR"
    "$MYSQLD_BIN" --initialize-insecure --datadir="$DB_DIR" --basedir=/usr/local/mysql >/dev/null 2>&1
    echo "✓ Data directory initialized."
fi

echo "Starting mysqld background process on port $PORT..."
"$MYSQLD_BIN" --datadir="$DB_DIR" --basedir=/usr/local/mysql --port=$PORT --mysqlx-port=$MYSQLX_PORT --socket="$SOCKET" > "$DB_DIR/mysqld.log" 2>&1 &
PID=$!
echo "mysqld launched with PID: $PID"

for i in {1..20}; do
    if nc -z 127.0.0.1 $PORT 2>/dev/null; then
        echo "✓ Karma Sutra MySQL server online on 127.0.0.1:$PORT!"
        exit 0
    fi
    sleep 0.5
done

echo "⚠️  Failed to connect to MySQL on port $PORT."
exit 1
