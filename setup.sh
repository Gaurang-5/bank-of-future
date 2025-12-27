#!/bin/bash

# Quick Setup Script for Bank of Future
# This script performs first-time setup and initial run

echo "╔══════════════════════════════════════════╗"
echo "║   Bank of Future - Quick Setup          ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# Check Java installation
echo "Checking prerequisites..."
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed!"
    echo "Please install Java 11 or higher from: https://adoptium.net/"
    exit 1
fi

if ! command -v javac &> /dev/null; then
    echo "❌ Java compiler (javac) is not installed!"
    echo "Please install JDK 11 or higher from: https://adoptium.net/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Java version 11 or higher is required!"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

echo "✓ Java $JAVA_VERSION detected"
echo ""

# Make scripts executable
echo "Setting up scripts..."
chmod +x run.sh reset.sh setup_mysql.sh
echo "✓ Scripts configured"
echo ""

# Ask about MySQL setup
read -p "Do you want to setup MySQL database? (recommended) (y/n): " setup_mysql

if [ "$setup_mysql" = "y" ] || [ "$setup_mysql" = "Y" ]; then
    if command -v mysql &> /dev/null; then
        echo ""
        echo "Setting up MySQL database..."
        ./setup_mysql.sh
    else
        echo "⚠️  MySQL is not installed. Skipping database setup."
        echo "   Application will use file-based storage."
    fi
fi

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║   ✓ Setup Complete!                     ║"
echo "╚══════════════════════════════════════════╝"
echo ""
echo "To start the application, run:"
echo "  ./run.sh"
echo ""
echo "To reset all data, run:"
echo "  ./reset.sh"
echo ""
echo "Enjoy using Bank of Future! 🏦"
