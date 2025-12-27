import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.util.List;

public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, BankAccount> accounts;
    private Map<String, Customer> customers;
    private Map<String, Staff> staffMembers; // Stores username -> Staff
    private List<Message> supportInbox; // Messages from customers
    private int accountCounter;
    private int customerCounter;
    private BankAccount treasuryAccount; // Central Treasury
    private double rbiLoanBalance; // Loan taken from RBI
    private double investedAmount; // Amount invested in market

    public Bank() {
        this.accounts = new ConcurrentHashMap<>();
        this.customers = new ConcurrentHashMap<>();
        this.staffMembers = new ConcurrentHashMap<>();
        this.supportInbox = new ArrayList<>();
        this.accountCounter = 1000;
        this.customerCounter = 1000;
        this.treasuryAccount = new BankAccount("TREASURY_001", "BANK", "Bank Treasury", "N/A", 10000000.0,
                AccountType.TREASURY);
    }

    public BankAccount getTreasuryAccount() {
        return treasuryAccount;
    }

    // Create account for NEW customer
    public String createAccount(String accountHolderName, String mobileNumber,
            String email, java.time.LocalDate dateOfBirth, String address, String city,
            String state, String pincode, String panNumber, String aadharNumber,
            double initialDeposit, AccountType accountType) {
        // Create new customer
        String customerId = "CUS" + (++customerCounter);
        Customer newCustomer = new Customer(customerId, accountHolderName, mobileNumber,
                email, dateOfBirth, address, city, state, pincode, panNumber, aadharNumber);
        customers.put(customerId, newCustomer);

        // Create account for this customer
        return createAccountForExistingCustomer(customerId, initialDeposit, accountType);
    }

    // Create CORPORATE account
    public String createCorporateAccount(String accountHolderName, String mobileNumber,
            String email, java.time.LocalDate dateOfBirth, String address, String city,
            String state, String pincode, String panNumber, String aadharNumber,
            BusinessType businessType, String companyName, String gstNumber, String cinNumber,
            double initialDeposit, AccountType accountType) {
        // Create new corporate customer
        String customerId = "CUS" + (++customerCounter);
        Customer newCustomer = new Customer(customerId, accountHolderName, mobileNumber,
                email, dateOfBirth, address, city, state, pincode, panNumber, aadharNumber,
                businessType, companyName, gstNumber, cinNumber);
        customers.put(customerId, newCustomer);

        // Create account for this customer
        return createAccountForExistingCustomer(customerId, initialDeposit, accountType);
    }

    // Create additional account for EXISTING customer
    public String createAccountForExistingCustomer(String customerId, double initialDeposit, AccountType accountType) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found!");
            return null;
        }

        if (initialDeposit < 0) {
            System.out.println("Initial deposit cannot be negative!");
            return null;
        }

        if (initialDeposit < accountType.getMinimumBalance()) {
            System.out.println("Insufficient initial deposit! Minimum: ₹" + accountType.getMinimumBalance());
            return null;
        }

        // Assign Relationship Manager based on Account Type
        if (accountType == AccountType.WEALTH) {
            Staff rm = getRelationshipManagerForRole(StaffRole.BANK_MANAGER);
            if (rm != null) {
                customer.setRelationshipManagerId(rm.getStaffId());
                System.out.println("  ✓ Assigned Relationship Manager: " + rm.getName() + " (Bank Manager)");
            }
        } else if (accountType == AccountType.PRIORITY) {
            Staff rm = getRelationshipManagerForRole(StaffRole.ASSISTANT_BANK_MANAGER);
            if (rm != null) {
                customer.setRelationshipManagerId(rm.getStaffId());
                System.out.println("  ✓ Assigned Relationship Manager: " + rm.getName() + " (Assistant Bank Manager)");
            }
        }

        String accountNumber = "ACC" + (++accountCounter);
        BankAccount newAccount = new BankAccount(accountNumber, customerId, customer.getCustomerName(),
                customer.getMobileNumber(), initialDeposit, accountType);
        accounts.put(accountNumber, newAccount);
        customer.addAccount(accountNumber);

        System.out.println("\n✓ Account created successfully! Number: " + accountNumber);
        return accountNumber;
    }

    public BankAccount getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public Customer findCustomer(String identifier) {
        for (Customer customer : customers.values()) {
            if (customer.getMobileNumber().equals(identifier) ||
                    customer.getCustomerId().equalsIgnoreCase(identifier)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByMobile(String mobile) {
        return findCustomer(mobile);
    }

    public java.util.ArrayList<BankAccount> getAllAccounts() {
        return new java.util.ArrayList<>(accounts.values());
    }

    public boolean wireTransfer(String fromAccNum, String toAccNum, double amount, String remark) {
        BankAccount fromAccount = getAccount(fromAccNum);
        BankAccount toAccount = getAccount(toAccNum);

        if (fromAccount == null) {
            System.out.println("Invalid sender account.");
            return false;
        }
        if (toAccount == null) {
            System.out.println("Invalid destination account.");
            return false;
        }

        if (fromAccNum.equals(toAccNum)) {
            System.out.println("Cannot transfer to the same account!");
            return false;
        }

        if (fromAccount.transferOut(amount, toAccNum, remark)) {
            toAccount.transferIn(amount, fromAccNum, remark);
            System.out.println("✓ Transfer successful!"); // UI feedback here for simplicity
            return true;
        } else {
            System.out.println("Transfer failed.");
            return false;
        }
    }

    public boolean wireTransfer(String fromAccNum, String toAccNum, double amount) {
        return wireTransfer(fromAccNum, toAccNum, amount, null);
    }

    // Corporate Services
    public void processPayroll(String fromAccNum, List<String> toAccounts, double amountPerEmp) {
        BankAccount fromAccount = getAccount(fromAccNum);
        if (fromAccount == null || fromAccount.getAccountType() != AccountType.CORPORATE) {
            System.out.println("Invalid Corporate Account.");
            return;
        }

        System.out.println("\nProcessing Payroll for " + toAccounts.size() + " employees...");

        int successCount = 0;
        for (String toAccNum : toAccounts) {
            BankAccount toAccount = getAccount(toAccNum);
            if (toAccount != null) {
                if (fromAccount.transferOut(amountPerEmp, toAccNum, "Payroll")) {
                    toAccount.transferIn(amountPerEmp, fromAccNum, "Payroll");
                    successCount++;
                } else {
                    System.out.println("Failed to pay account: " + toAccNum + " (Insufficient funds)");
                    break; // Stop if out of money
                }
            } else {
                System.out.println("Skipping invalid account: " + toAccNum);
            }
        }
        System.out.println("Payroll completed. Successful payments: " + successCount + "/" + toAccounts.size());
    }

    public boolean customerExistsByName(String name) {
        for (Customer customer : customers.values()) {
            if (customer.getCustomerName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void displayAllCustomers() {
        // ANSI Colors
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";
        String BRIGHT_MAGENTA = "\u001B[95m";
        String WHITE = "\u001B[37m";
        String GREEN = "\u001B[32m";
        String RED = "\u001B[31m";

        if (customers.isEmpty()) {
            System.out.println("\n  " + DIM + "No customers in the system." + RESET);
            return;
        }

        System.out.println(
                "\n  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "                    CUSTOMER DATABASE                          " + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);

        int count = 0;
        for (Customer customer : customers.values()) {
            count++;

            // Customer Header
            System.out.println("\n  " + BOLD + BRIGHT_CYAN + "┌─ Customer #" + count
                    + " ─────────────────────────────────────────────────┐" + RESET);

            // Basic Info
            System.out.println(
                    "  " + BRIGHT_CYAN + "│" + RESET + " " + BOLD + "ID:" + RESET + " " + customer.getCustomerId() +
                            "  │  " + BOLD + "Name:" + RESET + " " + customer.getCustomerName());
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + BOLD + "Type:" + RESET + " " +
                    (customer.isCorporate() ? BRIGHT_YELLOW + "Corporate" : BRIGHT_GREEN + "Individual") + RESET);

            // Contact Details
            System.out.println("  " + BRIGHT_CYAN + "├─ Contact Details" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Mobile: " + BRIGHT_GREEN
                    + customer.getMobileNumber() + RESET);
            System.out.println(
                    "  " + BRIGHT_CYAN + "│" + RESET + " " + "Email:  " + BRIGHT_GREEN + customer.getEmail() + RESET);

            // Address
            String fullAddress = customer.getAddress();
            if (customer.getCity() != null && !customer.getCity().isEmpty()) {
                fullAddress += ", " + customer.getCity();
            }
            if (customer.getState() != null && !customer.getState().isEmpty()) {
                fullAddress += ", " + customer.getState();
            }
            if (customer.getPincode() != null && !customer.getPincode().isEmpty()) {
                fullAddress += " - " + customer.getPincode();
            }
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Address: " + fullAddress);

            // Personal Details (for individuals)
            if (!customer.isCorporate()) {
                System.out.println("  " + BRIGHT_CYAN + "├─ Personal Details" + RESET);
                if (customer.getDateOfBirth() != null) {
                    System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "DOB: " + customer.getDateOfBirth());
                    // Calculate age
                    int age = java.time.Period.between(customer.getDateOfBirth(), java.time.LocalDate.now()).getYears();
                    System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Age: " + age + " years");
                }
            }

            // KYC Documents
            System.out.println("  " + BRIGHT_CYAN + "├─ KYC Documents" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "PAN:    " +
                    (customer.getPanNumber() != null ? customer.getPanNumber() : DIM + "Not provided" + RESET));
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Aadhar: " +
                    (customer.getAadharNumber() != null ? maskAadhar(customer.getAadharNumber())
                            : DIM + "Not provided" + RESET));

            // Corporate Details (if applicable)
            if (customer.isCorporate()) {
                System.out.println("  " + BRIGHT_CYAN + "├─ Corporate Details" + RESET);
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Business Type: " +
                        (customer.getBusinessType() != null ? customer.getBusinessType() : "N/A"));
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Company Name:  " +
                        (customer.getCompanyName() != null ? customer.getCompanyName() : "N/A"));
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "GST Number:    " +
                        (customer.getGstNumber() != null ? customer.getGstNumber() : "N/A"));
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "CIN Number:    " +
                        (customer.getCinNumber() != null ? customer.getCinNumber() : "N/A"));
            }

            // Nominee Details
            if (customer.hasNominee()) {
                System.out.println("  " + BRIGHT_CYAN + "├─ Nominee Details" + RESET);
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Name:     " + customer.getNomineeName());
                System.out
                        .println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Relation: " + customer.getNomineeRelation());
                if (customer.getNomineeDoB() != null) {
                    System.out
                            .println("  " + BRIGHT_CYAN + "│" + RESET + " " + "DOB:      " + customer.getNomineeDoB());
                }
            }

            // Account Statistics
            System.out.println("  " + BRIGHT_CYAN + "├─ Account Statistics" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Total Accounts: " + BOLD
                    + customer.getAccountCount() + RESET);

            // Registration Info
            System.out.println("  " + BRIGHT_CYAN + "├─ Registration" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Registered: " +
                    (customer.getRegistrationDate() != null ? customer.getRegistrationDate() : "N/A"));

            // Account Status
            System.out.println("  " + BRIGHT_CYAN + "├─ Security Status" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Account Status: " +
                    (customer.isLocked() ? RED + "🔒 Locked" : GREEN + "✓ Active") + RESET);
            if (customer.getLastLoginDate() != null) {
                // Use DateTimeFormatter for LocalDate
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                        .ofPattern("dd MMM yyyy");
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + "Last Login: " +
                        customer.getLastLoginDate().format(formatter));
            }

            System.out.println(
                    "  " + BRIGHT_CYAN + "└───────────────────────────────────────────────────────────┘" + RESET);
        }

        // Summary
        System.out.println(
                "\n  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println("  " + BOLD + "Total Customers: " + BRIGHT_GREEN + customers.size() + RESET);

        // Count individual vs corporate
        long individualCount = customers.values().stream().filter(c -> !c.isCorporate()).count();
        long corporateCount = customers.values().stream().filter(c -> c.isCorporate()).count();
        System.out.println("  " + "Individual: " + BRIGHT_GREEN + individualCount + RESET +
                "  │  Corporate: " + BRIGHT_YELLOW + corporateCount + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
    }

    // Helper method for masking Aadhar
    private String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() < 4) {
            return "****";
        }
        return "XXXX-XXXX-" + aadhar.substring(aadhar.length() - 4);
    }

    public void displayAllAccounts() {
        // ANSI Colors
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";
        String WHITE = "\u001B[37m";
        String GREEN = "\u001B[32m";

        if (accounts.isEmpty()) {
            System.out.println("\n  " + DIM + "No accounts in the system." + RESET);
            return;
        }

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "ACCOUNT REGISTRY" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);

        int count = 0;
        double totalBalance = 0.0;

        for (BankAccount account : accounts.values()) {
            count++;
            totalBalance += account.getBalance();

            String statusIcon = account.isActive() ? GREEN + "●" + RESET : DIM + "○" + RESET;

            System.out.println(
                    DIM + "  │ " + RESET + statusIcon + " " + BRIGHT_CYAN + account.getAccountNumber() + RESET +
                            " │ " + BOLD + account.getAccountHolderName() + RESET);
            System.out.println(DIM + "  │ " + RESET + DIM + "Type: " + account.getAccountType().getDisplayName() +
                    " | Balance: " + RESET + BRIGHT_GREEN + "₹" + String.format("%,.2f", account.getBalance()) + RESET);

            if (count < accounts.size()) {
                System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            }
        }

        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + "Total Accounts:  " + BOLD + accounts.size() + RESET);
        System.out.println(DIM + "  │ " + RESET + "Total Deposits:  " + BRIGHT_GREEN + BOLD + "₹ "
                + String.format("%,.2f", totalBalance) + RESET);
        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
    }

    // Revenue Analytics
    public void displayRevenueReport() {
        // ANSI Colors
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";
        String BRIGHT_CYAN = "\u001B[96m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String WHITE = "\u001B[37m";

        double totalFees = 0.0;
        double totalInterest = 0.0;

        for (BankAccount account : accounts.values()) {
            totalFees += account.getCumulativeFees();
            totalInterest += account.getCumulativeInterest();
        }

        // Calculate gross revenue (before taxes)
        double grossRevenue = totalFees - totalInterest;

        // Tax calculations
        double gstRate = 0.18; // 18% GST
        double corporateTaxRate = 0.25; // 25% Corporate Tax

        double gstAmount = 0.0;
        double taxableIncome = grossRevenue;
        double corporateTax = 0.0;
        double netProfitAfterTax = grossRevenue;

        if (grossRevenue > 0) {
            // GST is already included in fees, so we extract it
            gstAmount = (totalFees * gstRate) / (1 + gstRate);

            // Taxable income after GST
            taxableIncome = grossRevenue - gstAmount;

            // Corporate tax on taxable income
            corporateTax = taxableIncome * corporateTaxRate;

            // Net profit after all taxes
            netProfitAfterTax = taxableIncome - corporateTax;
        }

        boolean isProfit = netProfitAfterTax >= 0;

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "REVENUE ANALYTICS & TAX REPORT" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);

        // TREASURY SECTION
        System.out.println(DIM + "  │ " + RESET + BOLD + BRIGHT_CYAN + "TREASURY STATUS" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        double treasuryBal = treasuryAccount != null ? treasuryAccount.getBalance() : 0.0;
        String balColor = treasuryBal >= 0 ? GREEN : RED;
        System.out.println(DIM + "  │ " + RESET + "Treasury Balance:      " + RESET + BOLD + balColor + "₹ "
                + String.format("%,.2f", treasuryBal) + RESET);
        System.out.println(DIM + "  │ " + RESET + "RBI Loan Outstanding:  " + RESET + RED + "₹ "
                + String.format("%,.2f", rbiLoanBalance) + RESET);
        System.out.println(DIM + "  │ " + RESET + "Invested Capital:      " + RESET + BRIGHT_GREEN + "₹ "
                + String.format("%,.2f", investedAmount) + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);

        System.out.println(DIM + "  │ " + RESET + BOLD + BRIGHT_CYAN + "INCOME STREAM (YTD)" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_GREEN + "Total Fees Collected:  " + RESET + "₹ "
                + String.format("%,.2f", totalFees));
        System.out.println(DIM + "  │ " + RESET + RED + "Total Interest Paid:   " + RESET + "₹ "
                + String.format("%,.2f", totalInterest));
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_YELLOW + "Gross Revenue:         " + RESET + BOLD + "₹ "
                + String.format("%,.2f", grossRevenue) + RESET);

        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + BRIGHT_CYAN + "TAX DEDUCTIONS" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + RED + "GST @ 18%:             " + RESET + "₹ "
                + String.format("%,.2f", gstAmount));
        System.out.println(DIM + "  │ " + RESET + "Taxable Income:        " + RESET + "₹ "
                + String.format("%,.2f", taxableIncome));
        System.out.println(DIM + "  │ " + RESET + RED + "Corporate Tax @ 25%:   " + RESET + "₹ "
                + String.format("%,.2f", corporateTax));

        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + WHITE + "NET PROFIT AFTER TAX:  " + RESET + BOLD +
                (isProfit ? GREEN : RED) + "₹ " + String.format("%,.2f", Math.abs(netProfitAfterTax)) + RESET);
        System.out.println(DIM + "  │ " + RESET + "Status:                " +
                (isProfit ? GREEN + "● PROFIT" : RED + "● LOSS") + RESET);

        // Show tax summary
        if (grossRevenue > 0) {
            double totalTaxes = gstAmount + corporateTax;
            double effectiveTaxRate = (totalTaxes / grossRevenue) * 100;
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │ " + RESET + DIM + "Total Taxes Paid:      ₹"
                    + String.format("%,.2f", totalTaxes) + RESET);
            System.out.println(DIM + "  │ " + RESET + DIM + "Effective Tax Rate:    "
                    + String.format("%.2f", effectiveTaxRate) + "%" + RESET);
        }

        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
    }

    // RBI & Investment Methods
    public void borrowFromRBI(double amount) {
        if (amount <= 0)
            return;
        rbiLoanBalance += amount;
        treasuryAccount.deposit(amount);
        treasuryAccount.addTransaction("Loan Disbursed from RBI | Amount: ₹" + amount);
        System.out.println("  ✓ Borrowed ₹" + String.format("%,.2f", amount) + " from RBI @ 2%");
    }

    public void repayRBI(double amount) {
        if (amount <= 0)
            return;
        if (rbiLoanBalance < amount) {
            System.out.println("  ✗ Cannot repay more than outstanding loan (₹" + rbiLoanBalance + ")");
            return;
        }
        if (treasuryAccount.getBalance() < amount) {
            System.out.println("  ✗ Insufficient Treasury funds");
            return;
        }
        if (treasuryAccount.transferOut(amount, "RBI_CENTRAL_BANK", "Loan Repayment")) {
            rbiLoanBalance -= amount;
            System.out.println("  ✓ Repaid ₹" + String.format("%,.2f", amount) + " to RBI");
        }
    }

    public void payRBIInterest() {
        if (rbiLoanBalance <= 0) {
            System.out.println("  No outstanding loan.");
            return;
        }
        double interest = rbiLoanBalance * 0.02; // 2% fixed
        if (treasuryAccount.transferOut(interest, "RBI_CENTRAL_BANK", "Loan Interest Payment (2%)")) {
            System.out.println("  ✓ Paid RBI Interest: ₹" + String.format("%,.2f", interest));
        } else {
            System.out.println("  ✗ Failed to pay interest (Insufficient Funds)");
        }
    }

    public double getTotalCustomerDeposits() {
        double total = 0;
        for (BankAccount acc : accounts.values()) {
            total += acc.getBalance();
        }
        return total;
    }

    public void investDeposits(double amount) {
        double totalDeposits = getTotalCustomerDeposits();
        double maxInvestable = totalDeposits * 0.70; // 70% limit (RBI Rule)

        if (investedAmount + amount > maxInvestable) {
            System.out.println("  ✗ Investment exceeds RBI Limit (70% of Deposits)");
            System.out.println("    Current Invested: ₹" + String.format("%,.2f", investedAmount));
            System.out.println("    Max Allowed:      ₹" + String.format("%,.2f", maxInvestable));
            return;
        }

        investedAmount += amount;
        System.out.println("  ✓ Invested ₹" + String.format("%,.2f", amount) + " into Market (Avg 18% Returns)");
    }

    public void realizeInvestmentReturns() {
        if (investedAmount <= 0) {
            System.out.println("  No capital invested.");
            return;
        }
        // Returns are avg 18% (simulated annual, but applied per click for demo)
        double returns = investedAmount * 0.18;
        treasuryAccount.deposit(returns);
        treasuryAccount.addTransaction("Investment Returns Realized | Profit: ₹" + returns);
        System.out.println("  ✓ Realized Returns: ₹" + String.format("%,.2f", returns));
    }

    public void displayCustomerAccounts(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        customer.displayCustomerInfo();

        System.out.println("=== Customer's Accounts ===");
        for (String accNum : customer.getAccountNumbers()) {
            BankAccount account = accounts.get(accNum);
            if (account != null) {
                System.out
                        .println("Account: " + accNum + " | Balance: ₹" + String.format("%.2f", account.getBalance()));
            }
        }
        System.out.println("===========================\n");
    }

    public void applyInterestToAll() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts.");
            return;
        }
        int count = 0;
        for (BankAccount account : accounts.values()) {
            if (account.isActive()) {
                account.applyInterest(treasuryAccount);
                count++;
            }
        }
        System.out.println("Applied interest to " + count + " accounts.");
    }

    public void applyMaintenanceToAll() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts.");
            return;
        }
        int count = 0;
        for (BankAccount account : accounts.values()) {
            if (account.isActive()) {
                if (account.applyMaintenanceCharge(treasuryAccount)) {
                    count++;
                }
            }
        }
        System.out.println("Applied AMC to " + count + " accounts.");
    }

    // Admin ops
    public void updateAccountHolderName(String accountNumber, String newName) {
        BankAccount account = getAccount(accountNumber);
        if (account != null && account.isActive()) {
            account.setAccountHolderName(newName);
            System.out.println("Updated name.");
        }
    }

    public void updateMobileNumber(String accountNumber, String newMobile) {
        BankAccount account = getAccount(accountNumber);
        if (account != null && account.isActive()) {
            account.setMobileNumber(newMobile);
            System.out.println("Updated mobile.");
        }
    }

    public void closeAccount(String accountNumber) {
        BankAccount account = getAccount(accountNumber);
        if (account != null && account.isActive()) {
            account.closeAccount();
            System.out.println("Account closed.");
        }
    }

    // Account Management
    public boolean upgradeAccount(String accountNumber, AccountType newType) {
        BankAccount account = getAccount(accountNumber);
        if (account == null || !account.isActive()) {
            System.out.println("✗ Account not found or inactive");
            return false;
        }

        AccountType currentType = account.getAccountType();

        // Check if it's actually an upgrade
        if (currentType.getMinimumBalance() >= newType.getMinimumBalance()) {
            System.out.println("✗ This is not an upgrade");
            return false;
        }

        // Check if balance meets new minimum
        if (account.getBalance() < newType.getMinimumBalance()) {
            System.out.println("✗ Insufficient balance for upgrade. Required: ₹" + newType.getMinimumBalance());
            return false;
        }

        account.upgradeAccountType(newType);
        System.out.println("✓ Account upgraded from " + currentType.getDisplayName() +
                " to " + newType.getDisplayName());
        return true;
    }

    public boolean downgradeAccount(String accountNumber, AccountType newType) {
        BankAccount account = getAccount(accountNumber);
        if (account == null || !account.isActive()) {
            System.out.println("✗ Account not found or inactive");
            return false;
        }

        AccountType currentType = account.getAccountType();

        // Check if it's actually a downgrade
        if (currentType.getMinimumBalance() <= newType.getMinimumBalance()) {
            System.out.println("✗ This is not a downgrade");
            return false;
        }

        account.upgradeAccountType(newType); // Same method works for both
        System.out.println("✓ Account downgraded from " + currentType.getDisplayName() +
                " to " + newType.getDisplayName());
        return true;
    }

    public void freezeAccount(String accountNumber) {
        BankAccount account = getAccount(accountNumber);
        if (account != null && account.isActive()) {
            account.freezeAccount();
        }
    }

    public void unfreezeAccount(String accountNumber) {
        BankAccount account = getAccount(accountNumber);
        if (account != null && account.isActive()) {
            account.unfreezeAccount();
        }
    }

    public void broadcastMessage(Message message) {
        for (Customer customer : customers.values()) {
            customer.addMessage(message);
        }
    }

    public void addSupportMessage(Message message) {
        supportInbox.add(message);
    }

    public List<Message> getSupportInbox() {
        return supportInbox;
    }

    // Staff Management
    public void addStaff(Staff staff) {
        staffMembers.put(staff.getUsername(), staff);
    }

    public Staff findStaff(String username) {
        return staffMembers.get(username);
    }

    public Staff getStaffById(String staffId) {
        for (Staff staff : staffMembers.values()) {
            if (staff.getStaffId().equals(staffId)) {
                return staff;
            }
        }
        return null;
    }

    public Staff authenticateStaff(String username, String password) {
        Staff staff = staffMembers.get(username);
        if (staff != null && staff.authenticate(password)) {
            return staff;
        }
        return null;
    }

    public List<Staff> getStaffList() {
        return new ArrayList<>(staffMembers.values());
    }

    public Staff getRelationshipManagerForRole(StaffRole role) {
        for (Staff s : staffMembers.values()) {
            if (s.getRole() == role)
                return s;
        }
        return null;
    }

    public java.util.Map<String, Customer> getCustomers() {
        return customers;
    }

    // Database Restore Methods & Getters
    public void restoreCustomer(Customer customer) {
        this.customers.put(customer.getCustomerId(), customer);
    }

    public void restoreAccount(BankAccount account) {
        this.accounts.put(account.getAccountNumber(), account);
    }

    public void setCounters(int customerCount, int accountCount) {
        this.customerCounter = customerCount;
        this.accountCounter = accountCount;
    }

    public void setLedgerData(double rbiLoan, double invested) {
        this.rbiLoanBalance = rbiLoan;
        this.investedAmount = invested;
    }

    public void setTreasuryAccount(BankAccount treasury) {
        this.treasuryAccount = treasury;
    }

    public int getCustomerCounter() {
        return customerCounter;
    }

    public int getAccountCounter() {
        return accountCounter;
    }

    public double getRbiLoanBalance() {
        return rbiLoanBalance;
    }

    public double getInvestedAmount() {
        return investedAmount;
    }

    // Custom deserialization to handle backward compatibility
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (staffMembers == null) {
            staffMembers = new ConcurrentHashMap<>();
        }
        if (supportInbox == null) {
            supportInbox = new ArrayList<>();
        }
        if (treasuryAccount == null) {
            treasuryAccount = new BankAccount("TREASURY_001", "BANK", "Bank Treasury", "N/A", 10000000.0,
                    AccountType.TREASURY);
        }
    }
}
