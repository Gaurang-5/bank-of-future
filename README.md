# 🏦 Bank of Future - Banking Management System

A comprehensive banking application built with Java featuring both individual and corporate account management, investment options (FD/RD), staff management, and MySQL database integration.

## ✨ Features

### Customer Features
- **Account Management**
  - Individual & Corporate account creation
  - Multiple accounts per customer
  - Account freezing/unfreezing
  - Account closure with balance transfer
  
- **Transactions**
  - Deposits & Withdrawals
  - Fund transfers (within bank & to other accounts)
  - Mini statement & detailed passbook
  - Transaction history with filtering
  
- **NetBanking**
  - Secure login with password & transaction PIN
  - Account switching
  - Security settings management
  - Message inbox system
  
- **Investments**
  - Fixed Deposits (FD) with competitive interest rates
  - Recurring Deposits (RD) with monthly installments
  - Interest calculations & maturity tracking
  - Premature closure options

- **Corporate Banking**
  - Business account management
  - Vendor management & payments
  - Staff payroll processing
  - Bulk payment processing

### Staff Features
- Role-based access control (Manager, Cashier, Relationship Manager, IT Admin)
- Customer & account management
- Transaction processing
- Deposit management (FD/RD)
- Reports & analytics
- System administration

### System Features
- Data persistence (file-based & MySQL)
- KYC compliance
- Security features (account locking, PIN protection)
- Color-coded terminal interface
- Automatic data backup on exit

## 🚀 Quick Start

### Prerequisites
- **Java Development Kit (JDK)** 11 or higher
- **MySQL** 8.0 or higher (optional, application can run without it)
- **Terminal/Command Line** access

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/bank-of-future.git
   cd bank-of-future
   ```

2. **Run the application**
   ```bash
   chmod +x run.sh
   ./run.sh
   ```

   The script will automatically:
   - Download MySQL connector if needed
   - Compile all Java files
   - Start the application

### MySQL Setup (Optional)

If you want to use MySQL database:

1. **Create the database**
   ```bash
   mysql -u root -p < database_setup.sql
   ```

2. **Configure database connection**
   Edit `db.properties` with your MySQL credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/bank_of_future
   db.username=root
   db.password=your_password_here
   ```

3. **Initialize database**
   ```bash
   chmod +x setup_mysql.sh
   ./setup_mysql.sh
   ```

## 📁 Project Structure

```
bank-of-future/
├── src/
│   └── main/
│       └── java/
│           ├── BankingApplication.java    # Main application entry point
│           ├── Bank.java                  # Core banking operations
│           ├── Customer.java              # Customer entity
│           ├── BankAccount.java           # Account management
│           ├── Staff.java                 # Staff entity
│           ├── FixedDeposit.java          # FD management
│           ├── RecurringDeposit.java      # RD management
│           ├── Vendor.java                # Vendor management
│           ├── Message.java               # Messaging system
│           ├── DatabaseManager.java       # MySQL operations
│           ├── PersistenceManager.java    # File-based persistence
│           ├── AccountType.java           # Account type enum
│           ├── BusinessType.java          # Business type enum
│           └── StaffRole.java             # Staff role enum
├── database_setup.sql                     # MySQL database schema
├── db.properties                          # Database configuration
├── setup_mysql.sh                         # MySQL setup script
├── run.sh                                 # Application launcher
└── README.md                              # This file
```

## 🎮 Usage Guide

### First Time Setup

1. **Run the application** using `./run.sh`
2. **Create a staff account** (Staff Login → Option 4)
   - Default admin credentials will be shown on first run
3. **Create your first customer account** (Option 1 or 2)
4. **Login via NetBanking** to access all features

### Default Credentials

After first run, you can login as staff with:
- **Username:** admin
- **Password:** admin123

### Account Types

#### Individual Accounts
- **Savings Account**: 4% interest, ₹1000 minimum balance
- **Salary Account**: 3.5% interest, zero balance allowed
- **Senior Citizen Account**: 6% interest, ₹5000 minimum balance
- **Student Account**: 3% interest, zero balance allowed

#### Corporate Accounts
- **Current Account**: No interest, ₹10,000 minimum balance
- **Business Savings**: 2.5% interest, ₹25,000 minimum balance
- **Premium Business**: 3% interest, ₹100,000 minimum balance

### Investment Options

#### Fixed Deposits (FD)
- Tenure: 6-120 months
- Interest Rates: 5-7.5% based on tenure
- Minimum: ₹10,000
- Premature closure available

#### Recurring Deposits (RD)
- Tenure: 12-60 months
- Interest Rate: 6.5%
- Minimum monthly: ₹500
- Auto-debit from linked account

## 🛠️ Development

### Building from Source

```bash
# Compile all Java files
javac -cp ".:mysql-connector-java-8.0.33.jar" src/main/java/*.java -d target/classes

# Run the application
java -cp "target/classes:mysql-connector-java-8.0.33.jar:." BankingApplication
```

### Data Storage

The application uses two storage mechanisms:

1. **File-based (Default)**: Data stored in `bank_data.ser`
2. **MySQL (Optional)**: Full relational database with transaction history

Data is automatically saved on application exit.

## 🔒 Security Features

- Password-based authentication
- Transaction PIN for sensitive operations
- Account locking after failed login attempts
- Role-based access control for staff
- Secure password storage
- Transaction logging and audit trail

## 📊 Key Highlights

- **Object-Oriented Design**: Clean separation of concerns with well-defined classes
- **Enum Support**: Type-safe account types, business types, and staff roles
- **Exception Handling**: Robust error handling throughout
- **Data Persistence**: Dual storage mechanism (file & database)
- **User Experience**: Color-coded terminal interface with clear navigation
- **Scalability**: Supports multiple customers, accounts, and concurrent staff access

## 🐛 Troubleshooting

### MySQL Connection Issues
- Verify MySQL is running: `mysql -u root -p`
- Check credentials in `db.properties`
- Ensure database is created: `CREATE DATABASE bank_of_future;`

### Compilation Errors
- Ensure JDK 11+ is installed: `java -version`
- Check JAVA_HOME environment variable
- Verify MySQL connector JAR is present

### Data Reset
To reset all application data:
```bash
rm -f bank_data.ser src/main/java/bank_data.ser
mysql -u root -p -e "DROP DATABASE bank_of_future; CREATE DATABASE bank_of_future;"
mysql -u root -p bank_of_future < database_setup.sql
```

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👤 Author

**Gaurang Bhatia**

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For support, email your-email@example.com or open an issue in the repository.

## 🎯 Future Enhancements

- [ ] Web-based UI (Spring Boot + React)
- [ ] Mobile app integration
- [ ] Email/SMS notifications
- [ ] Loan management system
- [ ] Credit/Debit card management
- [ ] International transactions
- [ ] Multi-currency support
- [ ] Advanced analytics dashboard
- [ ] API for third-party integrations

---

Made with ❤️ by Gaurang Bhatia
