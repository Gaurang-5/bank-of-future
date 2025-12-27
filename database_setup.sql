-- Bank of Future - Database Schema
-- Run this script in MySQL to create the database and tables

CREATE DATABASE IF NOT EXISTS bank_of_future;
USE bank_of_future;

-- Staff Table
CREATE TABLE IF NOT EXISTS staff (
    staff_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(20) PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    pincode VARCHAR(10),
    pan_number VARCHAR(20) UNIQUE,
    aadhar_number VARCHAR(20) UNIQUE,
    password VARCHAR(100) NOT NULL,
    failed_login_attempts INT DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    has_transaction_pin BOOLEAN DEFAULT FALSE,
    transaction_pin VARCHAR(10),
    business_type VARCHAR(50),
    company_name VARCHAR(100),
    gst_number VARCHAR(30),
    cin_number VARCHAR(30),
    relationship_manager_id VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_mobile (mobile_number),
    INDEX idx_email (email),
    FOREIGN KEY (relationship_manager_id) REFERENCES staff(staff_id) ON DELETE SET NULL
);

-- Bank Accounts Table
CREATE TABLE IF NOT EXISTS bank_accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    account_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_frozen BOOLEAN DEFAULT FALSE,
    cumulative_fees DECIMAL(15, 2) DEFAULT 0.00,
    cumulative_interest DECIMAL(15, 2) DEFAULT 0.00,
    cheque_book_requests INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_account_type (account_type)
);

-- Transactions Table (Structured)
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT NOT NULL,
    amount DECIMAL(15, 2),
    transaction_type VARCHAR(20),
    balance_after DECIMAL(15, 2),
    remark TEXT,
    FOREIGN KEY (account_number) REFERENCES bank_accounts(account_number) ON DELETE CASCADE,
    INDEX idx_account (account_number),
    INDEX idx_date (transaction_date)
);

-- Transaction Logs (Legacy String format support)
CREATE TABLE IF NOT EXISTS transaction_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    log_entry TEXT NOT NULL,
    entry_order INT NOT NULL,
    FOREIGN KEY (account_number) REFERENCES bank_accounts(account_number) ON DELETE CASCADE
);

-- Fixed Deposits Table
CREATE TABLE IF NOT EXISTS fixed_deposits (
    fd_number VARCHAR(30) PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INT NOT NULL,
    maturity_amount DECIMAL(15, 2),
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES bank_accounts(account_number) ON DELETE CASCADE,
    INDEX idx_account (account_number)
);

-- Recurring Deposits Table
CREATE TABLE IF NOT EXISTS recurring_deposits (
    rd_number VARCHAR(30) PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    monthly_installment DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INT NOT NULL,
    installments_paid INT DEFAULT 0,
    total_deposited DECIMAL(15, 2) DEFAULT 0.00,
    maturity_amount DECIMAL(15, 2),
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES bank_accounts(account_number) ON DELETE CASCADE,
    INDEX idx_account (account_number)
);

-- Nominees Table
CREATE TABLE IF NOT EXISTS nominees (
    nominee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    nominee_name VARCHAR(100) NOT NULL,
    nominee_dob DATE NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES bank_accounts(account_number) ON DELETE CASCADE,
    INDEX idx_account (account_number)
);

-- Customer Account Mapping
CREATE TABLE IF NOT EXISTS customer_accounts (
    customer_id VARCHAR(20) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    PRIMARY KEY (customer_id, account_number),
    FOREIGN KEY (account_number) REFERENCES bank_accounts(account_number) ON DELETE CASCADE
);

-- Bank Ledger (For RBI, Investments, Counters)
CREATE TABLE IF NOT EXISTS bank_ledger (
    id INT PRIMARY KEY DEFAULT 1,
    rbi_loan_balance DECIMAL(15, 2) DEFAULT 0.00,
    invested_amount DECIMAL(15, 2) DEFAULT 0.00,
    account_counter INT DEFAULT 1000,
    customer_counter INT DEFAULT 1000,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT IGNORE INTO bank_ledger (id, rbi_loan_balance, invested_amount) VALUES (1, 0.00, 0.00);

-- Vendors Table
CREATE TABLE IF NOT EXISTS vendors (
    vendor_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    bank_account VARCHAR(20),
    pending_payments DECIMAL(15, 2) DEFAULT 0.00
);

-- Reporting View
CREATE OR REPLACE VIEW account_summary AS
SELECT 
    ba.account_number,
    ba.account_holder_name,
    ba.account_type,
    ba.balance,
    ba.is_active,
    ba.is_frozen,
    c.customer_id,
    c.mobile_number,
    c.email,
    COUNT(DISTINCT fd.fd_number) as total_fds,
    COUNT(DISTINCT rd.rd_number) as total_rds,
    COUNT(DISTINCT t.transaction_id) as total_transactions
FROM bank_accounts ba
LEFT JOIN customers c ON ba.customer_id = c.customer_id
LEFT JOIN fixed_deposits fd ON ba.account_number = fd.account_number AND fd.is_active = TRUE
LEFT JOIN recurring_deposits rd ON ba.account_number = rd.account_number AND rd.is_active = TRUE
LEFT JOIN transactions t ON ba.account_number = t.account_number
GROUP BY ba.account_number, ba.account_holder_name, ba.account_type, ba.balance, 
         ba.is_active, ba.is_frozen, c.customer_id, c.mobile_number, c.email;

COMMIT;
