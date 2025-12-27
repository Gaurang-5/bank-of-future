#!/bin/bash

# ============================================
# Bank of Future - Reset Script
# Removes all application data and resets to fresh state
# ============================================

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║   Bank of Future - Data Reset Tool      ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
echo ""

# Warning
echo -e "${YELLOW}⚠️  WARNING: This will delete ALL application data!${NC}"
echo -e "${YELLOW}   - All customer accounts${NC}"
echo -e "${YELLOW}   - All transactions${NC}"
echo -e "${YELLOW}   - All staff records${NC}"
echo -e "${YELLOW}   - All deposits (FD/RD)${NC}"
echo ""
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo -e "${CYAN}ℹ️  Reset cancelled${NC}"
    exit 0
fi

echo ""
echo -e "${CYAN}🔄 Resetting application data...${NC}"

# Remove serialized data files
echo -e "${CYAN}  → Removing data files...${NC}"
rm -f bank_data.ser
rm -f src/main/java/bank_data.ser
rm -f target/classes/bank_data.ser

# Remove compiled classes
echo -e "${CYAN}  → Cleaning compiled classes...${NC}"
rm -f src/main/java/*.class
rm -rf target/classes/*.class
mkdir -p target/classes

echo -e "${GREEN}✓ File-based data cleared${NC}"

# Reset MySQL database if configured
if [ -f "db.properties" ]; then
    echo ""
    read -p "Do you want to reset MySQL database as well? (yes/no): " reset_db
    
    if [ "$reset_db" = "yes" ]; then
        echo -e "${CYAN}  → Resetting MySQL database...${NC}"
        
        # Read MySQL credentials from db.properties
        DB_USER=$(grep "db.username" db.properties | cut -d'=' -f2)
        DB_PASS=$(grep "db.password" db.properties | cut -d'=' -f2)
        
        if [ -z "$DB_PASS" ] || [ "$DB_PASS" = "your_password_here" ]; then
            read -sp "Enter MySQL password: " DB_PASS
            echo ""
        fi
        
        # Drop and recreate database
        mysql -u "$DB_USER" -p"$DB_PASS" -e "DROP DATABASE IF EXISTS bank_of_future; CREATE DATABASE bank_of_future;" 2>/dev/null
        
        if [ $? -eq 0 ]; then
            # Recreate schema
            mysql -u "$DB_USER" -p"$DB_PASS" bank_of_future < database_setup.sql 2>/dev/null
            echo -e "${GREEN}✓ MySQL database reset successfully${NC}"
        else
            echo -e "${RED}✗ Failed to reset MySQL database${NC}"
            echo -e "${YELLOW}  Please ensure MySQL is running and credentials are correct${NC}"
        fi
    fi
fi

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║   ✓ Application Reset Complete          ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════╝${NC}"
echo ""
echo -e "${CYAN}You can now run the application with: ${NC}${GREEN}./run.sh${NC}"
echo ""
