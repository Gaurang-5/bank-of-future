import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class BankAccount implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String customerId;
    private String accountHolderName;
    private String mobileNumber;
    private double balance;
    private ArrayList<String> transactionHistory;
    private SimpleDateFormat dateFormat;
    private boolean isActive;
    private AccountType accountType;
    private static final double ANNUAL_INTEREST_RATE = 4.5;
    private static final double GST_RATE = 0.18;

    // Bank Branch Details
    private static final String BRANCH_NAME = "Mumbai Main Branch";
    private static final String IFSC_CODE = "BOFU0001234";
    private static final String SWIFT_CODE = "BOFUINBB";

    // Revenue Tracking
    private double cumulativeFees;
    private double cumulativeInterest;

    // Account Management
    private boolean isFrozen;
    private int chequeBookRequests;
    private ArrayList<FixedDeposit> fixedDeposits;
    private ArrayList<RecurringDeposit> recurringDeposits;

    // Corporate Features
    private ArrayList<Vendor> vendors;

    public BankAccount(String accountNumber, String customerId, String accountHolderName,
            String mobileNumber, double initialDeposit, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountHolderName = accountHolderName;
        this.mobileNumber = mobileNumber;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.transactionHistory = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.isActive = true;
        this.cumulativeFees = 0.0;
        this.cumulativeInterest = 0.0;
        this.isFrozen = false;
        this.chequeBookRequests = 0;
        this.fixedDeposits = new ArrayList<>();
        this.recurringDeposits = new ArrayList<>();
        this.vendors = new ArrayList<>();

        addTransaction("Account created - Type: " + accountType.getDisplayName() +
                " | Initial deposit: ₹" + initialDeposit, null);

        // Show benefits
        System.out.println("\n🎉 Account Benefits Unlocked:");
        for (String benefit : accountType.getBenefits()) {
            System.out.println("   ✨ " + benefit);
        }

        // Apply joining fee if applicable
        if (accountType.getJoiningFee() > 0) {
            applyJoiningFee();
        }
    }

    public double getCumulativeFees() {
        return cumulativeFees;
    }

    public double getCumulativeInterest() {
        return cumulativeInterest;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public static double getInterestRate() {
        return ANNUAL_INTEREST_RATE;
    }

    public String getBranchName() {
        return BRANCH_NAME;
    }

    public String getIfscCode() {
        return IFSC_CODE;
    }

    public String getSwiftCode() {
        return SWIFT_CODE;
    }

    public void upgradeAccountType(AccountType newType) {
        AccountType oldType = this.accountType;
        this.accountType = newType;
        addTransaction("Account type changed from " + oldType.getDisplayName() +
                " to " + newType.getDisplayName());
    }

    public void setAccountHolderName(String newName) {
        String oldName = this.accountHolderName;
        this.accountHolderName = newName;
        addTransaction("Account holder name changed from '" + oldName + "' to '" + newName + "'", null);
    }

    public void setMobileNumber(String newMobile) {
        String oldMobile = this.mobileNumber;
        this.mobileNumber = newMobile;
        addTransaction("Mobile number updated from " + oldMobile + " to " + newMobile, null);
    }

    public void closeAccount() {
        this.isActive = false;
        addTransaction("Account closed. Final balance: ₹" + balance, null);
    }

    private void applyJoiningFee() {
        double joiningFee = accountType.getJoiningFee();
        double gst = joiningFee * GST_RATE;
        double total = joiningFee + gst;

        balance -= total;
        cumulativeFees += total; // Track revenue

        // Only log if fee is greater than 0
        if (joiningFee > 0) {
            logFeeWithGst("Joining fee charged", joiningFee, gst, null);
        }
    }

    public void deposit(double amount) {
        if (isFrozen) {
            System.out.println("\u001B[31m❌ Transaction denied! Account is frozen.\u001B[0m");
            return;
        }
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposited: ₹" + amount + " | New Balance: ₹" + balance, null);
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }

    public boolean withdraw(double amount) {
        if (isFrozen) {
            System.out.println("\u001B[31m❌ Transaction denied! Account is frozen.\u001B[0m");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount!");
            return false;
        }

        double withdrawalFee = accountType.getWithdrawalFee();
        double gst = withdrawalFee * GST_RATE;
        double totalDeduction = amount + withdrawalFee + gst;

        double maxWithdrawable = balance + accountType.getOverdraftLimit();

        if (totalDeduction > maxWithdrawable) {
            System.out.println("Insufficient funds! Required: ₹" + String.format("%.2f", totalDeduction));
            if (accountType.getOverdraftLimit() > 0) {
                System.out.println("Available Limit (inc. OD): ₹" + String.format("%.2f", maxWithdrawable));
            }
            return false;
        }

        balance -= totalDeduction;
        cumulativeFees += (withdrawalFee + gst); // Track revenue

        addTransaction("Withdrawal: ₹" + amount + " | New Balance: ₹" + balance, null);
        // Only log fee if greater than 0
        if (withdrawalFee > 0) {
            logFeeWithGst("Withdrawal fee", withdrawalFee, gst, null);
        }

        return true;
    }

    public boolean transferOut(double amount, String toAccount, String remark) {
        if (isFrozen) {
            System.out.println("\u001B[31m❌ Transaction denied! Account is frozen.\u001B[0m");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Invalid transfer amount!");
            return false;
        }

        double transferFee = accountType.getTransferFee();
        double gst = transferFee * GST_RATE;
        double totalDeduction = amount + transferFee + gst;

        double maxWithdrawable = balance + accountType.getOverdraftLimit();

        if (totalDeduction > maxWithdrawable) {
            System.out.println("Insufficient funds! Required: ₹" + String.format("%.2f", totalDeduction));
            if (accountType.getOverdraftLimit() > 0) {
                System.out.println("Available Limit (inc. OD): ₹" + String.format("%.2f", maxWithdrawable));
            }
            return false;
        }

        balance -= totalDeduction;
        cumulativeFees += (transferFee + gst); // Track revenue

        addTransaction("Wire transfer sent to " + toAccount + " | Amount: ₹" + amount +
                " | New Balance: ₹" + balance, remark);
        // Only log fee if greater than 0
        if (transferFee > 0) {
            logFeeWithGst("Transfer fee", transferFee, gst, remark);
        }

        return true;
    }

    public void transferIn(double amount, String fromAccount, String remark) {
        if (isFrozen) {
            System.out.println("\u001B[31m❌ Cannot receive transfer! Account is frozen.\u001B[0m");
            return;
        }
        balance += amount;
        addTransaction("Wire transfer received from " + fromAccount + " | Amount: ₹" + amount +
                " | New Balance: ₹" + balance, remark);
    }

    public double calculateInterest() {
        if (accountType == AccountType.CORPORATE)
            return 0.0; // No interest for Corporate
        double monthlyRate = ANNUAL_INTEREST_RATE / 12 / 100;
        return balance * monthlyRate;
    }

    public void applyInterest(BankAccount treasury) {
        double interest = calculateInterest();
        if (interest > 0) {
            // Attempt to pay from Treasury
            if (treasury.transferOut(interest, accountNumber, "Interest Payment")) {
                balance += interest;
                cumulativeInterest += interest; // Track cost
                addTransaction("Interest credited: ₹" + String.format("%.2f", interest) +
                        " (Rate: " + ANNUAL_INTEREST_RATE + "% p.a.) | New Balance: ₹" + balance);
            } else {
                System.out.println("  [Treasury] Failed to pay interest to " + accountNumber + " (Insufficient Funds)");
            }
        }
    }

    public boolean applyMaintenanceCharge(BankAccount treasury) {
        if (!isActive)
            return false;

        if (balance < accountType.getMinimumBalance()) {
            double maintenanceCharge = accountType.getMaintenanceCharge();
            double gst = maintenanceCharge * GST_RATE;
            double total = maintenanceCharge + gst;

            balance -= total;
            cumulativeFees += total; // Track revenue

            // Transfer to Treasury
            treasury.deposit(total);
            // We add a specific remark to treasury manually?
            // deposit() adds generic log.
            // We can assume Treasury tracks deposits.
            treasury.addTransaction("Received AMC from " + accountNumber + " (" + accountHolderName + ")");

            addTransaction("Account Maintenance Charge (AMC) applied | Reason: Balance below minimum ₹"
                    + accountType.getMinimumBalance() +
                    " | New Balance: ₹" + balance);
            // Only log fee if greater than 0
            if (maintenanceCharge > 0) {
                logFeeWithGst("AMC", maintenanceCharge, gst, null);
            }
            return true;
        }
        return false;
    }

    public void displayAccountInfo() {
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
        String RED = "\u001B[31m";

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "ACCOUNT INFORMATION" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(
                DIM + "  │ " + RESET + BRIGHT_CYAN + "Account Number:  " + RESET + BOLD + accountNumber + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Customer ID:     " + RESET + customerId);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Account Holder:  " + RESET + accountHolderName);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Mobile Number:   " + RESET + mobileNumber);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_YELLOW + "Account Type:    " + RESET + BOLD
                + accountType.getDisplayName() + RESET);
        System.out.println(DIM + "  │ " + RESET + DIM + "Min Balance:     ₹"
                + String.format("%,.2f", accountType.getMinimumBalance()) + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_GREEN + "Current Balance: " + RESET + BOLD + WHITE + "₹ "
                + String.format("%,.2f", balance) + RESET);
        System.out.println(DIM + "  │ " + RESET + "Status:          "
                + (isActive ? GREEN + "● Active" : RED + "● Closed") + RESET);
        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
    }

    public void displayTransactionHistory() {
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
        String RED = "\u001B[31m";

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "ACCOUNT STATEMENT / PASSBOOK" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out
                .println(DIM + "  │ " + RESET + "Account: " + BOLD + accountNumber + RESET + " | " + accountHolderName);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);

        if (transactionHistory.isEmpty()) {
            System.out.println(DIM + "  │ " + RESET + DIM + "No transactions recorded yet" + RESET);
        } else {
            int count = 0;
            int total = transactionHistory.size();

            for (String transaction : transactionHistory) {
                count++;

                // Parse transaction for better formatting
                String icon = "  ";
                String color = WHITE;

                if (transaction.contains("deposit") || transaction.contains("credited")
                        || transaction.contains("Transfer In")) {
                    icon = "↓ ";
                    color = BRIGHT_GREEN;
                } else if (transaction.contains("withdraw") || transaction.contains("debited")
                        || transaction.contains("Transfer Out") || transaction.contains("Charge")) {
                    icon = "↑ ";
                    color = RED;
                } else if (transaction.contains("created")) {
                    icon = "★ ";
                    color = BRIGHT_YELLOW;
                } else if (transaction.contains("Interest")) {
                    icon = "+ ";
                    color = GREEN;
                }

                System.out.println(DIM + "  │ " + RESET + color + icon + RESET + DIM + transaction + RESET);

                // Add separator between transactions (except last one)
                if (count < total) {
                    System.out.println(DIM + "  │" + RESET);
                }
            }
        }

        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + "Total Transactions: " + BOLD + transactionHistory.size() + RESET);
        System.out.println(DIM + "  │ " + RESET + "Current Balance:    " + BRIGHT_GREEN + BOLD + "₹ "
                + String.format("%,.2f", balance) + RESET);
        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
    }

    public void displayQuickStatement() {
        // ANSI Colors
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_RED = "\u001B[91m";
        String BRIGHT_CYAN = "\u001B[96m";
        String WHITE = "\u001B[37m";

        System.out.println();
        printPlainLine(BOLD + CYAN + "QUICK STATEMENT (Last 5 Transactions)" + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Date          Description          Credit        Debit      Balance" + RESET,
                RESET);
        printPlainLine("", RESET);

        if (transactionHistory.isEmpty()) {
            printPlainLine(DIM + "No transactions recorded" + RESET, RESET);
        } else {
            int start = Math.max(0, transactionHistory.size() - 5);
            double runningBalance = balance;

            for (int i = start; i < transactionHistory.size(); i++) {
                String transaction = transactionHistory.get(i);

                String date = "mm/dd/yyyy";
                String description = "Transaction";
                double creditAmt = 0.0;
                double debitAmt = 0.0;

                // Parse date
                if (transaction.startsWith("[")) {
                    int endBracket = transaction.indexOf("]");
                    if (endBracket > 0) {
                        String timestamp = transaction.substring(1, endBracket);
                        try {
                            Date txDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                            date = new SimpleDateFormat("MM/dd/yyyy").format(txDate);
                        } catch (Exception e) {
                            date = timestamp.substring(0, Math.min(10, timestamp.length()));
                        }
                        description = transaction.substring(endBracket + 2).trim();
                    }
                }

                // Extract amount and compress description
                String compressedDesc = compressDescription(description);
                double amount = extractAmount(description);

                // Determine if credit or debit
                if (description.contains("Deposit") || description.contains("received") ||
                        description.contains("Interest credited") || description.contains("Account created")) {
                    creditAmt = amount;
                } else if (description.contains("Withdrawal") || description.contains("sent") ||
                        description.contains("charged") || description.contains("Fee") ||
                        description.contains("GST")) {
                    debitAmt = amount;
                }

                // Format credit and debit columns
                String creditStr = creditAmt > 0 ? String.format("%,13.2f", creditAmt) : String.format("%13s", "0.00");
                String debitStr = debitAmt > 0 ? String.format("%,12.2f", debitAmt) : String.format("%12s", "0.00");

                String row = WHITE + String.format("%-12s", date) + RESET + "  "
                        + DIM + String.format("%-20s", compressedDesc) + RESET
                        + (creditAmt > 0 ? BRIGHT_GREEN : "") + creditStr + RESET + " "
                        + (debitAmt > 0 ? BRIGHT_RED : "") + debitStr + RESET + " "
                        + BOLD + WHITE + String.format("%,13.2f", runningBalance) + RESET;
                printPlainLine(row, RESET);
            }
        }

        printPlainLine("", RESET);
        printPlainLine("Current Balance: ₹ " + String.format("%,.2f", balance), RESET);
    }

    // Helper method to compress description to 3-4 words
    private String compressDescription(String fullDesc) {
        // Remove extra details after | or :
        if (fullDesc.contains("|")) {
            fullDesc = fullDesc.substring(0, fullDesc.indexOf("|")).trim();
        }

        // Extract key words
        if (fullDesc.contains("GST"))
            return "GST Charge";
        if (fullDesc.contains("Deposited:"))
            return "Deposit";
        if (fullDesc.contains("Withdrawal:"))
            return "Withdrawal";
        if (fullDesc.contains("Wire transfer sent"))
            return "Transfer Out";
        if (fullDesc.contains("Wire transfer received"))
            return "Transfer In";
        if (fullDesc.contains("Interest credited"))
            return "Interest Credit";
        if (fullDesc.contains("Account created"))
            return "Account Opening";
        if (fullDesc.contains("Joining fee"))
            return "Joining Fee";
        if (fullDesc.contains("Withdrawal fee"))
            return "Withdrawal Fee";
        if (fullDesc.contains("Transfer fee"))
            return "Transfer Fee";
        if (fullDesc.contains("Maintenance Charge"))
            return "AMC Charge";
        if (fullDesc.contains("Fixed Deposit created"))
            return "FD Created";
        if (fullDesc.contains("FD closed"))
            return "FD Closed";
        if (fullDesc.contains("Recurring Deposit created"))
            return "RD Created";
        if (fullDesc.contains("RD installment"))
            return "RD Payment";
        if (fullDesc.contains("RD closed"))
            return "RD Closed";
        if (fullDesc.contains("Cheque book"))
            return "Cheque Request";

        // Default: take first 3 words
        String[] words = fullDesc.split("\\s+");
        int wordCount = Math.min(3, words.length);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            result.append(words[i]).append(" ");
        }
        return result.toString().trim();
    }

    // Helper method to extract amount from transaction description
    private double extractAmount(String description) {
        try {
            // Look for patterns like "₹1000" or "Amount: ₹1000" or "Deposited: ₹1000"
            if (description.contains("₹")) {
                String[] parts = description.split("₹");
                for (String part : parts) {
                    // Extract number after ₹
                    String numStr = part.trim().split("[\\s|]")[0].replaceAll(",", "");
                    try {
                        return Double.parseDouble(numStr);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            // If parsing fails, return 0
        }
        return 0.0;
    }

    public void addTransaction(String description, String remark) {
        String timestamp = dateFormat.format(new Date());
        String entry = "[" + timestamp + "] " + description;
        if (remark != null && !remark.isEmpty()) {
            entry += " | Remark: " + remark;
        }
        transactionHistory.add(entry);
    }

    public void addTransaction(String description) {
        addTransaction(description, null);
    }

    public void addRestoredTransaction(String entry) {
        transactionHistory.add(entry);
    }

    // Log base fee and GST as two separate transactions (no extra balance changes)
    // Only logs if amounts are greater than 0
    private void logFeeWithGst(String label, double baseAmount, double gstAmount, String remark) {
        if (baseAmount > 0) {
            addTransaction(label + " | Base: ₹" + String.format("%.2f", baseAmount) +
                    " | New Balance: ₹" + balance, remark);
        }
        if (gstAmount > 0) {
            addTransaction("GST Charge on " + label + " | GST: ₹" + String.format("%.2f", gstAmount) +
                    " | New Balance: ₹" + balance, remark);
        }
    }

    // Account Management Methods
    public void freezeAccount() {
        if (!isFrozen) {
            isFrozen = true;
            addTransaction("Account frozen for security");
            System.out.println("✓ Account frozen successfully");
        }
    }

    public void unfreezeAccount() {
        if (isFrozen) {
            isFrozen = false;
            addTransaction("Account unfrozen");
            System.out.println("✓ Account unfrozen successfully");
        }
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void requestChequeBook() {
        chequeBookRequests++;
        addTransaction("Cheque book requested - Request #" + chequeBookRequests);
        System.out.println("✓ Cheque book request submitted. Request ID: CHQ" + chequeBookRequests);
    }

    public int getChequeBookRequests() {
        return chequeBookRequests;
    }

    public java.util.List<String> getStatement() {
        return new java.util.ArrayList<>(transactionHistory);
    }

    // Fixed Deposit Methods
    public String createFixedDeposit(double amount, int tenureMonths) {
        if (amount > balance) {
            System.out.println("✗ Insufficient balance for FD creation");
            return null;
        }

        if (amount < 10000) {
            System.out.println("✗ Minimum FD amount is ₹10,000");
            return null;
        }

        // Deduct from account
        balance -= amount;

        // Determine interest rate based on tenure
        double interestRate = 6.5; // Base rate
        if (tenureMonths >= 12)
            interestRate = 7.0;
        if (tenureMonths >= 24)
            interestRate = 7.5;
        if (tenureMonths >= 36)
            interestRate = 8.0;

        String fdNumber = "FD" + System.currentTimeMillis();
        FixedDeposit fd = new FixedDeposit(fdNumber, accountNumber, amount, tenureMonths, interestRate);
        fixedDeposits.add(fd);

        addTransaction("Fixed Deposit created - " + fdNumber + " | Amount: ₹" + amount +
                " | Tenure: " + tenureMonths + " months | Rate: " + interestRate + "%");

        return fdNumber;
    }

    public void displayFixedDeposits() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "FIXED DEPOSITS" + RESET);

        if (fixedDeposits.isEmpty()) {
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │ " + RESET + DIM + "No fixed deposits" + RESET);
        } else {
            for (FixedDeposit fd : fixedDeposits) {
                fd.displayFDDetails();
            }
        }

        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
    }

    public boolean closeFD(String fdNumber, boolean premature) {
        for (FixedDeposit fd : fixedDeposits) {
            if (fd.getFdNumber().equals(fdNumber) && fd.isActive()) {
                if (premature) {
                    fd.prematureWithdrawal();
                    System.out.println("⚠ Premature withdrawal - 1% penalty applied");
                } else {
                    fd.checkMaturity();
                }

                balance += fd.getMaturityAmount();
                fd.closeFD();
                addTransaction("FD closed - " + fdNumber + " | Amount credited: ₹" +
                        String.format("%.2f", fd.getMaturityAmount()));
                return true;
            }
        }
        return false;
    }

    // Recurring Deposit Methods
    public String createRecurringDeposit(double monthlyAmount, int tenureMonths) {
        if (monthlyAmount < 500) {
            System.out.println("✗ Minimum RD installment is ₹500");
            return null;
        }

        double interestRate = 6.0; // Base rate
        if (tenureMonths >= 12)
            interestRate = 6.5;
        if (tenureMonths >= 24)
            interestRate = 7.0;

        String rdNumber = "RD" + System.currentTimeMillis();
        RecurringDeposit rd = new RecurringDeposit(rdNumber, accountNumber, monthlyAmount, tenureMonths, interestRate);
        recurringDeposits.add(rd);

        addTransaction("Recurring Deposit created - " + rdNumber + " | Monthly: ₹" + monthlyAmount +
                " | Tenure: " + tenureMonths + " months");

        return rdNumber;
    }

    public boolean payRDInstallment(String rdNumber) {
        for (RecurringDeposit rd : recurringDeposits) {
            if (rd.getRdNumber().equals(rdNumber) && rd.isActive()) {
                double installment = rd.getMonthlyInstallment();
                if (balance < installment) {
                    System.out.println("✗ Insufficient balance for RD installment");
                    return false;
                }

                balance -= installment;
                rd.payInstallment();
                addTransaction("RD installment paid - " + rdNumber + " | Amount: ₹" + installment);
                return true;
            }
        }
        return false;
    }

    public void displayRecurringDeposits() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "RECURRING DEPOSITS" + RESET);

        if (recurringDeposits.isEmpty()) {
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │ " + RESET + DIM + "No recurring deposits" + RESET);
        } else {
            for (RecurringDeposit rd : recurringDeposits) {
                rd.displayRDDetails();
            }
        }

        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
    }

    public boolean closeRD(String rdNumber) {
        for (RecurringDeposit rd : recurringDeposits) {
            if (rd.getRdNumber().equals(rdNumber) && rd.isActive()) {
                rd.closeRD();
                balance += rd.getMaturityAmount();
                addTransaction("RD closed - " + rdNumber + " | Amount credited: ₹" +
                        String.format("%.2f", rd.getMaturityAmount()));
                return true;
            }
        }
        return false;
    }

    public ArrayList<FixedDeposit> getFixedDeposits() {
        return fixedDeposits;
    }

    public ArrayList<RecurringDeposit> getRecurringDeposits() {
        return recurringDeposits;
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    // Generate detailed bank statement matching the template format
    public void generateDetailedStatement(String customerAddress) {
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
        String RED = "\u001B[31m";
        String BRIGHT_RED = "\u001B[91m";
        String BLUE = "\u001B[34m";
        String BRIGHT_BLUE = "\u001B[94m";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        // Calculate statement period (last 30 days)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
        String periodStart = dateFormat.format(cal.getTime());

        // Calculate totals and statistics
        double totalCredits = 0;
        double totalDebits = 0;
        double largestCredit = 0;
        double largestDebit = 0;

        // Parse transactions to calculate actual totals
        for (String transaction : transactionHistory) {
            double amount = extractAmount(transaction);

            if (transaction.contains("Deposit") || transaction.contains("received") ||
                    transaction.contains("Interest credited") || transaction.contains("Account created")) {
                totalCredits += amount;

                if (amount > largestCredit)
                    largestCredit = amount;
            } else if (transaction.contains("Withdrawal") || transaction.contains("sent") ||
                    transaction.contains("charged") || transaction.contains("Fee") ||
                    transaction.contains("GST")) {
                totalDebits += amount;

                if (amount > largestDebit)
                    largestDebit = amount;
            }
        }

        // Calculate opening balance
        // Opening Balance + Credits - Debits = Current Balance
        // Therefore: Opening Balance = Current Balance - Credits + Debits
        double openingBalance = balance - totalCredits + totalDebits;

        // Print statement header without borders
        System.out.println();
        String bof1 = BRIGHT_CYAN + "█████  " + "  ████  " + "███████" + RESET;
        String bof2 = BRIGHT_CYAN + "█    █ " + " █    █ " + "█" + RESET;
        String bof3 = BRIGHT_CYAN + "█████  " + " █    █ " + "█████" + RESET + "  " + BOLD + CYAN + "BANK OF FUTURE"
                + RESET;
        String bof4 = BRIGHT_CYAN + "█    █ " + " █    █ " + "█" + RESET + "  Digital Banking Division";
        String bof5 = BRIGHT_CYAN + "█████  " + "  ████  " + "█" + RESET;
        printPlainLine(bof1, RESET);
        printPlainLine(bof2, RESET);
        printPlainLine(bof3, RESET);
        printPlainLine(bof4, RESET);
        printPlainLine(bof5, RESET);
        printPlainLine("", RESET);
        printPlainLine("231 Valley Farms Street            " + BOLD + CYAN + "STATEMENT OF ACCOUNT" + RESET, RESET);
        printPlainLine("Mumbai, Maharashtra 400001", RESET);
        printPlainLine(BLUE + "bankoffuture@domain.com" + RESET, RESET);
        printPlainLine("", RESET);
        printPlainLine(BRIGHT_CYAN + "Account Number:" + RESET + "      " + BOLD + accountNumber + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "IFSC Code:" + RESET + "           " + BOLD + getIfscCode() + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Statement Date:" + RESET + "      " + currentDate, RESET);
        printPlainLine(BRIGHT_CYAN + "Period Covered:" + RESET + "      " + periodStart + "  to  " + currentDate,
                RESET);
        printPlainLine(String.format("%63s", "Page  1  of  1"), RESET);
        printPlainLine("", RESET);
        printPlainLine(BOLD + WHITE + accountHolderName + RESET, RESET);
        printPlainLine(GREEN + customerAddress + RESET, RESET);
        printPlainLine("<" + BRIGHT_YELLOW + BRANCH_NAME + RESET + ">", RESET);
        printPlainLine("", RESET);
        printPlainLine(BOLD + CYAN + "ACCOUNT SUMMARY" + RESET, RESET);
        printPlainLine("", RESET);
        printPlainLine(BRIGHT_CYAN + "Opening Balance:" + RESET + padVisible("", 41) + BRIGHT_BLUE
                + String.format("%,19.2f", openingBalance) + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Total Credit Amount:" + RESET + padVisible("", 36) + BRIGHT_GREEN
                + String.format("%,19.2f", totalCredits) + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Total Debit Amount:" + RESET + padVisible("", 37) + BRIGHT_RED
                + String.format("%,19.2f", totalDebits) + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Closing Balance:" + RESET + padVisible("", 41) + BOLD + BRIGHT_GREEN
                + String.format("%,19.2f", balance) + RESET, RESET);
        printPlainLine("", RESET);
        printPlainLine(BRIGHT_CYAN + "Account Type:" + RESET + padVisible("", 44) + BRIGHT_YELLOW
                + accountType.getDisplayName() + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Number of Transactions:" + RESET + padVisible("", 30)
                + String.format("%d", transactionHistory.size()), RESET);
        printPlainLine("", RESET);

        // TRANSACTIONS LIST
        printPlainLine("═══════════════════════════════════════════════════════════════════════════", RESET);
        printPlainLine(BOLD + CYAN + "TRANSACTIONS" + RESET, RESET);
        printPlainLine("═══════════════════════════════════════════════════════════════════════════", RESET);
        printPlainLine(
                BRIGHT_CYAN + "Date          Description          Credit        Debit      Balance" + RESET,
                RESET);
        printPlainLine("───────────────────────────────────────────────────────────────────────────", RESET);

        // Display transactions
        double runningBalance = openingBalance;
        int displayCount = 0;
        for (String transaction : transactionHistory) {
            if (displayCount >= 15)
                break; // Limit to 15 transactions

            // Parse transaction
            String date = "mm/dd/yyyy";
            String description = "Transaction";
            double creditAmt = 0.0;
            double debitAmt = 0.0;

            if (transaction.startsWith("[")) {
                int endBracket = transaction.indexOf("]");
                if (endBracket > 0) {
                    String timestamp = transaction.substring(1, endBracket);
                    try {
                        Date txDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
                        date = dateFormat.format(txDate);
                    } catch (Exception e) {
                        date = timestamp.substring(0, Math.min(10, timestamp.length()));
                    }
                    description = transaction.substring(endBracket + 2).trim();
                }
            }

            // Extract amount and compress description
            String compressedDesc = compressDescription(description);
            double amount = extractAmount(description);

            // Determine if credit or debit
            if (description.contains("Deposit") || description.contains("received") ||
                    description.contains("Interest credited") || description.contains("Account created")) {
                creditAmt = amount;
                runningBalance += creditAmt; // Update running balance
            } else if (description.contains("Withdrawal") || description.contains("sent") ||
                    description.contains("charged") || description.contains("Fee") ||
                    description.contains("GST")) {
                debitAmt = amount;
                runningBalance -= debitAmt; // Update running balance
            }

            // Format credit and debit columns
            String creditStr = creditAmt > 0 ? String.format("%,13.2f", creditAmt) : String.format("%13s", "0.00");
            String debitStr = debitAmt > 0 ? String.format("%,12.2f", debitAmt) : String.format("%12s", "0.00");

            // Format row: Date(12) + Desc(20) + Credit(13) + Debit(12) + Balance(13)
            String row = WHITE + String.format("%-12s", date) + RESET + "  "
                    + DIM + String.format("%-20s", compressedDesc) + RESET
                    + (creditAmt > 0 ? BRIGHT_GREEN : "") + creditStr + RESET + " "
                    + (debitAmt > 0 ? BRIGHT_RED : "") + debitStr + RESET + " "
                    + BOLD + WHITE + String.format("%,13.2f", runningBalance) + RESET;
            printPlainLine(row, RESET);
            displayCount++;
        }

        printPlainLine("───────────────────────────────────────────────────────────────────────────", RESET);
        printPlainLine(
                DIM + "Showing " + displayCount + " of " + transactionHistory.size() + " total transactions" + RESET,
                RESET);
        printPlainLine("", RESET);

        // FOOTER
        printPlainLine("═══════════════════════════════════════════════════════════════════════════", RESET);
        printPlainLine("", RESET);

        // Generation timestamp
        SimpleDateFormat timestampFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a");
        String generatedAt = timestampFormat.format(new Date());
        printPlainLine(DIM + "Statement generated on: " + generatedAt + RESET, RESET);
        printPlainLine("", RESET);

        // Customer service info
        printPlainLine(BOLD + CYAN + "CUSTOMER SERVICE" + RESET, RESET);
        printPlainLine(BRIGHT_CYAN + "Phone:" + RESET + "  1800-123-4567 (Toll Free)", RESET);
        printPlainLine(BRIGHT_CYAN + "Email:" + RESET + "  support@bankoffuture.com", RESET);
        printPlainLine(BRIGHT_CYAN + "Web:" + RESET + "    www.bankoffuture.com", RESET);
        printPlainLine("", RESET);

        // Important notes
        printPlainLine(DIM + "This is a computer-generated statement and does not require a signature." + RESET, RESET);
        printPlainLine(DIM + "Please verify all transactions and report discrepancies within 30 days." + RESET, RESET);
        printPlainLine("", RESET);
        printPlainLine(BOLD + GREEN + "Thank you for banking with Bank of Future!" + RESET, RESET);
        printPlainLine("", RESET);
        System.out.println();
    }

    // Helpers for precise box layout with ANSI colors
    private static String stripAnsi(String s) {
        if (s == null)
            return "";
        return s.replaceAll("\u001B\\[[;?0-9]*[A-Za-z]", "");
    }

    private static String padVisible(String s, int width) {
        int len = stripAnsi(s).length();
        if (len >= width)
            return s;
        StringBuilder sb = new StringBuilder(s);
        for (int i = len; i < width; i++)
            sb.append(' ');
        return sb.toString();
    }

    private static void printBoxLine(String content, String DIM, String RESET) {
        System.out.println(DIM + "║ " + RESET + padVisible(content, 75) + " " + DIM + "║" + RESET);
    }

    // Print a line without borders but still padded to the same width
    private static void printPlainLine(String content, String RESET) {
        System.out.println(padVisible(content, 75) + RESET);
    }

    // ==================== VENDOR MANAGEMENT (CORPORATE) ====================

    public void addVendor(Vendor vendor) {
        if (vendors == null) {
            vendors = new ArrayList<>();
        }
        vendors.add(vendor);
        System.out.println("\u001B[32m✓ Vendor added successfully!\u001B[0m");
    }

    public boolean removeVendor(String vendorId) {
        if (vendors == null)
            return false;
        return vendors.removeIf(v -> v.getVendorId().equals(vendorId));
    }

    public Vendor findVendor(String vendorId) {
        if (vendors == null)
            return null;
        return vendors.stream()
                .filter(v -> v.getVendorId().equals(vendorId))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Vendor> getVendors() {
        if (vendors == null) {
            vendors = new ArrayList<>();
        }
        return vendors;
    }

    public void displayAllVendors() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String DIM = "\u001B[2m";

        if (vendors == null || vendors.isEmpty()) {
            System.out.println("\n  " + DIM + "No vendors registered" + RESET);
            return;
        }

        System.out.println(
                "\n  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "                    VENDOR DATABASE                            " + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);

        for (Vendor vendor : vendors) {
            vendor.displayVendorInfo();
        }

        System.out.println("\n  " + BOLD + "Total Vendors: " + BRIGHT_CYAN + vendors.size() + RESET);
    }

    public boolean payVendor(String vendorId, double amount, String description) {
        Vendor vendor = findVendor(vendorId);
        if (vendor == null) {
            System.out.println("\u001B[31m❌ Vendor not found!\u001B[0m");
            return false;
        }

        // Perform transfer
        if (transferOut(amount, vendor.getAccountNumber(), description)) {
            vendor.recordPayment(amount);
            System.out.println("\u001B[32m✓ Payment to " + vendor.getVendorName() + " successful!\u001B[0m");
            return true;
        }
        return false;
    }
}
