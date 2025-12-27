#!/bin/bash

# Bank of Future - Quick Setup Script
# This script helps you set up MySQL integration

echo "🏦 Bank of Future - MySQL Setup"
echo "================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Check if MySQL is installed
echo -e "${CYAN}Checking MySQL installation...${NC}"
if command -v mysql &> /dev/null; then
    echo -e "${GREEN}✓ MySQL is installed${NC}"
    mysql --version
else
    echo -e "${RED}✗ MySQL is not installed${NC}"
    echo -e "${YELLOW}Install MySQL using: brew install mysql${NC}"
    exit 1
fi

# Check if MySQL is running
echo ""
echo -e "${CYAN}Checking MySQL service...${NC}"
if pgrep -x mysqld > /dev/null; then
    echo -e "${GREEN}✓ MySQL is running${NC}"
else
    echo -e "${YELLOW}⚠ MySQL is not running${NC}"
    echo -e "${YELLOW}Starting MySQL...${NC}"
    brew services start mysql
    sleep 3
fi

# Download MySQL Connector if not present
echo ""
echo -e "${CYAN}Checking MySQL JDBC Driver...${NC}"
JDBC_JAR="mysql-connector-java-8.0.33.jar"
if [ -f "$JDBC_JAR" ]; then
    echo -e "${GREEN}✓ JDBC driver found${NC}"
else
    echo -e "${YELLOW}⚠ JDBC driver not found${NC}"
    echo -e "${CYAN}Downloading MySQL Connector/J...${NC}"
    curl -L -o "$JDBC_JAR" "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"
    if [ -f "$JDBC_JAR" ]; then
        echo -e "${GREEN}✓ JDBC driver downloaded${NC}"
    else
        echo -e "${RED}✗ Failed to download JDBC driver${NC}"
        exit 1
    fi
fi

# Prompt for MySQL password
echo ""
echo -e "${CYAN}Enter your MySQL root password:${NC}"
read -s MYSQL_PASSWORD

# Test MySQL connection
echo ""
echo -e "${CYAN}Testing MySQL connection...${NC}"
mysql -u root -p"$MYSQL_PASSWORD" -e "SELECT 1;" &> /dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ MySQL connection successful${NC}"
else
    echo -e "${RED}✗ MySQL connection failed${NC}"
    echo -e "${YELLOW}Please check your password${NC}"
    exit 1
fi

# Create database
echo ""
echo -e "${CYAN}Creating database...${NC}"
mysql -u root -p"$MYSQL_PASSWORD" < database_setup.sql
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database created successfully${NC}"
else
    echo -e "${RED}✗ Failed to create database${NC}"
    exit 1
fi

# Update db.properties
echo ""
echo -e "${CYAN}Updating database configuration...${NC}"
cat > db.properties << EOF
# MySQL Database Configuration for Bank of Future
db.url=jdbc:mysql://localhost:3306/bank_of_future
db.username=root
db.password=$MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
db.useSSL=false
db.serverTimezone=UTC
db.allowPublicKeyRetrieval=true
EOF
echo -e "${GREEN}✓ Configuration updated${NC}"

# Test database connection with Java
echo ""
echo -e "${CYAN}Testing Java database connection...${NC}"
cd src/main/java
javac -cp ".:../../../$JDBC_JAR" DatabaseManager.java
java -cp ".:../../../$JDBC_JAR" DatabaseManager
cd ../../..

echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✓ Setup Complete!${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo -e "${CYAN}To run the application:${NC}"
echo -e "${YELLOW}  cd src/main/java${NC}"
echo -e "${YELLOW}  javac -cp \".:../../../$JDBC_JAR\" *.java${NC}"
echo -e "${YELLOW}  java -cp \".:../../../$JDBC_JAR\" BankingApplication${NC}"
echo ""
echo -e "${CYAN}Or use the run script:${NC}"
echo -e "${YELLOW}  ./run.sh${NC}"
echo ""
