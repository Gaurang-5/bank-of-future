
#!/bin/bash

# ============================================
# Bank of Future - Application Launcher
# Automated build and run script
# ============================================

# Colors
GREEN='\033[0;32m'
CYAN='\033[0;36m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║   🏦  Bank of Future                    ║${NC}"
echo -e "${CYAN}║   Banking Management System              ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
echo ""

# Create necessary directories
mkdir -p target/classes

# Check for MySQL connector JAR
JDBC_JAR="mysql-connector-java-8.0.33.jar"

if [ ! -f "$JDBC_JAR" ]; then
    echo -e "${CYAN}📦 Downloading MySQL Connector...${NC}"
    curl -sL -o "$JDBC_JAR" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
    
    if [ $? -ne 0 ] || [ ! -f "$JDBC_JAR" ]; then
        echo -e "${RED}✗ Failed to download MySQL connector${NC}"
        echo -e "${YELLOW}  The application will run without MySQL support${NC}"
    else
        echo -e "${GREEN}✓ MySQL connector downloaded${NC}"
    fi
    echo ""
fi

# Set classpath
CLASSPATH="src/main/java:$JDBC_JAR"

# Compile Java files
echo -e "${CYAN}🔨 Compiling Java sources...${NC}"
javac -cp "$CLASSPATH" src/main/java/*.java -d target/classes 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
    echo ""
    
    # Run the application
    echo -e "${CYAN}🚀 Starting application...${NC}"
    echo ""
    java -cp "target/classes:$JDBC_JAR:." BankingApplication
    
    # Capture exit code
    EXIT_CODE=$?
    
    if [ $EXIT_CODE -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✓ Application closed successfully${NC}"
    else
        echo ""
        echo -e "${YELLOW}⚠ Application exited with code $EXIT_CODE${NC}"
    fi
else
    echo -e "${RED}✗ Compilation failed${NC}"
    echo -e "${YELLOW}Please check the error messages above${NC}"
    exit 1
fi
