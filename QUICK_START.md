# 🚀 Quick Reference Guide

## Getting Started

### First-Time Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/bank-of-future.git
cd bank-of-future

# Run quick setup (recommended)
./setup.sh

# OR manually run the application
./run.sh
```

### Common Commands
```bash
# Start the application
./run.sh

# Reset all data
./reset.sh

# Setup MySQL (optional)
./setup_mysql.sh
```

## Default Credentials

### Staff Login (After First Run)
- **Username:** admin
- **Password:** admin123

## Quick Feature Guide

### Customer Features
| Feature | Location | Description |
|---------|----------|-------------|
| Open Account | Main Menu → 1/2 | Individual or corporate account |
| NetBanking | Main Menu → 3 | Login to manage accounts |
| Deposit | Dashboard → 4 | Add funds to account |
| Withdraw | Dashboard → 5 | Withdraw funds |
| Transfer | Dashboard → 6 | Send money to other accounts |
| Fixed Deposit | Dashboard → 9 → 1 | Create FD investment |
| Recurring Deposit | Dashboard → 9 → 2 | Create RD with monthly installments |

### Staff Features
| Feature | Location | Description |
|---------|----------|-------------|
| Staff Login | Main Menu → 4 | Access staff functions |
| Create Customer | Staff Menu → 1 | Register new customer |
| Create Account | Staff Menu → 2 | Open account for customer |
| Process Transaction | Staff Menu → 3 | Deposit/withdraw for customers |
| Reports | Staff Menu → 6 | View analytics |

## Account Types

### Individual Accounts
- **Savings Account**: 4% interest, ₹1000 min balance
- **Salary Account**: 3.5% interest, zero balance
- **Senior Citizen**: 6% interest, ₹5000 min balance
- **Student Account**: 3% interest, zero balance

### Corporate Accounts
- **Current Account**: No interest, ₹10,000 min balance
- **Business Savings**: 2.5% interest, ₹25,000 min balance
- **Premium Business**: 3% interest, ₹100,000 min balance

## Investment Options

### Fixed Deposit (FD)
- **Tenure:** 6-120 months
- **Interest:** 5-7.5% (based on tenure)
- **Minimum:** ₹10,000
- **Features:** Premature closure available

### Recurring Deposit (RD)
- **Tenure:** 12-60 months
- **Interest:** 6.5%
- **Minimum:** ₹500/month
- **Features:** Auto-debit from account

## Troubleshooting

### Issue: MySQL Connection Failed
**Solution:** 
1. Check if MySQL is running: `mysql -u root -p`
2. Verify credentials in `db.properties`
3. Application works without MySQL (uses file storage)

### Issue: Compilation Failed
**Solution:**
1. Check Java version: `java -version` (need 11+)
2. Ensure JAVA_HOME is set
3. Try: `rm -rf target/classes && ./run.sh`

### Issue: Account Locked
**Solution:**
1. Login as staff (admin/admin123)
2. Go to Staff Menu → 7 (Unlock Account)
3. Enter customer ID

### Issue: Reset Application
**Solution:**
```bash
./reset.sh
# Removes all data and resets to fresh state
```

## File Structure

```
bank-of-future/
├── README.md                # Main documentation
├── CONTRIBUTING.md          # Contribution guidelines
├── LICENSE                  # MIT License
├── .gitignore              # Git ignore rules
├── run.sh                  # Main application launcher
├── setup.sh                # First-time setup script
├── reset.sh                # Data reset script
├── setup_mysql.sh          # MySQL setup helper
├── database_setup.sql      # MySQL schema
├── db.properties           # Database configuration
└── src/main/java/          # Java source files
    ├── BankingApplication.java
    ├── Bank.java
    ├── Customer.java
    ├── BankAccount.java
    ├── Staff.java
    ├── FixedDeposit.java
    ├── RecurringDeposit.java
    ├── DatabaseManager.java
    └── ... (other classes)
```

## GitHub Workflow

### Initial Push
```bash
# Already initialized - just add remote and push
git remote add origin https://github.com/yourusername/bank-of-future.git
git branch -M main
git push -u origin main
```

### Making Changes
```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and commit
git add .
git commit -m "Add: description of changes"

# Push to GitHub
git push origin feature/new-feature

# Create Pull Request on GitHub
```

## Support

- **Documentation:** See [README.md](README.md)
- **Issues:** https://github.com/yourusername/bank-of-future/issues
- **Contributing:** See [CONTRIBUTING.md](CONTRIBUTING.md)

## System Requirements

- **Java:** JDK 11 or higher
- **MySQL:** 8.0+ (optional)
- **OS:** macOS, Linux, Windows
- **Memory:** 512MB minimum
- **Storage:** 100MB

---

**Made with ❤️ for learning and education**
