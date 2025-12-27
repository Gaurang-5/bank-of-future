import java.util.Scanner;
import java.util.List;

public class BankingApplication {
    private static Bank bank;
    private static Scanner scanner = new Scanner(System.in);
    private static Staff currentStaff; // Logged in staff member

    // Enhanced Color Palette
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";

    // Colors
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";

    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    // Bright variants
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_BLUE = "\u001B[94m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_CYAN = "\u001B[96m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BRIGHT_MAGENTA = "\u001B[95m";

    // Background colors

    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_CYAN = "\u001B[46m";

    public static void main(String[] args) {
        bank = PersistenceManager.loadBank();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PersistenceManager.saveBank(bank);
        }));

        clearScreen();
        showWelcomeScreen();

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    customerMenu();
                    break;
                case 2:
                    corporateAccountMenu();
                    break;
                case 3:
                    login();
                    break;
                case 4:
                    staffLoginMenu();
                    break;
                case 5:
                    showExitScreen();
                    PersistenceManager.saveBankVerbose(bank);
                    running = false;
                    break;
                default:
                    showError("Invalid choice. Please try again.");
                    pause();
            }
        }
        scanner.close();
    }

    // ==================== UI COMPONENTS ====================

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void showWelcomeScreen() {
        System.out.println("\n\n\n");
        System.out
                .println("    " + BRIGHT_CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();
        System.out.println("              " + BOLD + BRIGHT_CYAN + "🏦  BANK OF FUTURE" + RESET);
        System.out.println();
        System.out.println("          " + DIM + WHITE + "Your Trusted Partner in Financial Excellence" + RESET);
        System.out.println();
        System.out
                .println("    " + BRIGHT_CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n\n");
        pause();
    }

    private static void showExitScreen() {
        clearScreen();
        System.out.println("\n\n\n");
        System.out
                .println("    " + BRIGHT_CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();
        System.out.println("                  " + BOLD + WHITE + "Thank You for Banking with Us!" + RESET);
        System.out.println();
        System.out.println("              " + DIM + WHITE + "Your session has been saved securely" + RESET);
        System.out.println();
        System.out
                .println("    " + BRIGHT_CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println("\n\n");
    }

    private static void printSectionHeader(String title) {
        System.out.println("\n");
        System.out.println("  " + BOLD + BRIGHT_CYAN + title + RESET);
        System.out
                .println("  " + BRIGHT_BLUE + "─────────────────────────────────────────────────────────────" + RESET);
        System.out.println();
    }

    private static void printCard(String title, String... lines) {
        System.out.println();
        System.out.println("  " + BOLD + CYAN + title + RESET);
        System.out.println("  " + DIM + "─────────────────────────────────────────────────────────────" + RESET);
        for (String line : lines) {
            System.out.println("  " + line);
        }
        System.out.println();
    }

    private static void showSuccess(String message) {
        System.out.println(
                "\n  " + BG_GREEN + BLACK + " ✓ SUCCESS " + RESET + " " + BRIGHT_GREEN + message + RESET + "\n");
    }

    private static void showError(String message) {
        System.out.println("\n  " + BG_RED + WHITE + " ✗ ERROR " + RESET + " " + RED + message + RESET + "\n");
    }

    private static void showInfo(String message) {
        System.out.println("\n  " + BG_CYAN + BLACK + " ℹ INFO " + RESET + " " + CYAN + message + RESET + "\n");
    }

    private static final String BLACK = "\u001B[30m";
    private static final String BG_RED = "\u001B[41m";

    // ==================== MAIN MENU ====================

    private static void displayMainMenu() {
        clearScreen();
        System.out.println("\n\n");
        System.out.println("  " + BOLD + BRIGHT_CYAN + "BANK OF FUTURE" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();

        System.out.println("  " + BRIGHT_GREEN + "[1]" + RESET + "  Open Individual Account");
        System.out.println("  " + BRIGHT_GREEN + "[2]" + RESET + "  Open Corporate Account");
        System.out.println("  " + BRIGHT_CYAN + "[3]" + RESET + "  NetBanking Login");
        System.out.println("  " + BRIGHT_YELLOW + "[4]" + RESET + "  Staff Login");
        System.out.println("  " + RED + "[5]" + RESET + "  Exit Application");
        System.out.println();

        System.out.print("  " + BRIGHT_BLUE + "→" + RESET + " Enter your choice: ");
    }

    // ==================== CUSTOMER FLOWS ====================

    private static Customer currentCustomer;
    private static BankAccount currentAccount;

    private static void customerMenu() {
        clearScreen();
        printSectionHeader("NEW ACCOUNT APPLICATION");

        System.out.println("  " + CYAN + "Please provide the following details for KYC compliance:" + RESET + "\n");

        // Personal Details
        System.out.print("  " + CYAN + "Full Name:" + RESET + " ");
        String name = scanner.nextLine().trim();
        if (name.length() < 3) {
            showError("Name must be at least 3 characters long");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Date of Birth (YYYY-MM-DD):" + RESET + " ");
        String dobStr = scanner.nextLine().trim();
        java.time.LocalDate dob;
        try {
            dob = java.time.LocalDate.parse(dobStr);
        } catch (Exception e) {
            showError("Invalid date format. Please use YYYY-MM-DD");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Email Address:" + RESET + " ");
        String email = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "Mobile Number (10 digits):" + RESET + " ");
        String mobile = scanner.nextLine().trim();
        if (!mobile.matches("\\d{10}")) {
            showError("Invalid mobile number format");
            pause();
            return;
        }

        if (bank.findCustomerByMobile(mobile) != null) {
            showError("Customer already exists. Please use NetBanking Login.");
            pause();
            return;
        }

        // Address Details
        System.out.println("\n  " + YELLOW + "Address Details:" + RESET);
        System.out.print("  " + CYAN + "Street Address:" + RESET + " ");
        String address = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "City:" + RESET + " ");
        String city = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "State:" + RESET + " ");
        String state = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "Pincode (6 digits):" + RESET + " ");
        String pincode = scanner.nextLine().trim();

        // KYC Documents
        System.out.println("\n  " + YELLOW + "KYC Documents:" + RESET);
        System.out.print("  " + CYAN + "PAN Number:" + RESET + " ");
        String pan = scanner.nextLine().trim().toUpperCase();

        System.out.print("  " + CYAN + "Aadhar Number (12 digits):" + RESET + " ");
        String aadhar = scanner.nextLine().trim();

        // Account Selection
        System.out.println();
        AccountType.displayIndividualAccountTypes();
        AccountType type = selectAccountType(false); // false = exclude corporate
        if (type == null)
            return;

        System.out.print("\n  " + CYAN + "Initial Deposit (₹):" + RESET + " ");
        double deposit = getDoubleInputDirect();

        String accNum = bank.createAccount(name, mobile, email, dob, address, city, state, pincode, pan, aadhar,
                deposit, type);
        Customer cust = bank.findCustomerByMobile(mobile);

        if (accNum != null && cust != null) {
            clearScreen();
            printSectionHeader("ACCOUNT CREATED SUCCESSFULLY");

            printCard("Your Account Details",
                    BRIGHT_GREEN + "Account Number:  " + RESET + BOLD + accNum + RESET,
                    BRIGHT_CYAN + "Customer ID:     " + RESET + cust.getCustomerId(),
                    BRIGHT_YELLOW + "Account Type:    " + RESET + type.getDisplayName(),
                    "",
                    MAGENTA + "Login Credentials:" + RESET,
                    "  Username: " + BOLD + cust.getCustomerId() + RESET,
                    "  Password: " + BOLD + cust.getPassword() + RESET,
                    "",
                    DIM + "Please save these credentials securely" + RESET);

            showSuccess("Your account is now active and ready to use!");
        }
        pause();
    }

    private static void corporateAccountMenu() {
        clearScreen();
        printSectionHeader("CORPORATE ACCOUNT APPLICATION");

        System.out.println(
                "  " + CYAN + "Business Account Opening - Please provide the following details:" + RESET + "\n");

        // Business Type Selection
        System.out.println("  " + YELLOW + "Select Business Type:" + RESET);
        System.out.println("  1. Proprietorship (Individual ownership)");
        System.out.println("  2. Partnership");
        System.out.println("  3. Private Limited Company");
        System.out.println("  4. Public Limited Company");
        System.out.println("  5. Limited Liability Partnership (LLP)");
        System.out.print("\n  " + CYAN + "Choice:" + RESET + " ");

        int businessChoice = getIntInputDirect();
        BusinessType businessType;

        switch (businessChoice) {
            case 1:
                businessType = BusinessType.PROPRIETORSHIP;
                break;
            case 2:
                businessType = BusinessType.PARTNERSHIP;
                break;
            case 3:
                businessType = BusinessType.PRIVATE_LIMITED;
                break;
            case 4:
                businessType = BusinessType.PUBLIC_LIMITED;
                break;
            case 5:
                businessType = BusinessType.LLP;
                break;
            default:
                showError("Invalid business type");
                pause();
                return;
        }

        // Company Details
        System.out.println("\n  " + YELLOW + "Company Details:" + RESET);
        System.out.print("  " + CYAN + "Company/Business Name:" + RESET + " ");
        String companyName = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "GST Number:" + RESET + " ");
        String gstNumber = scanner.nextLine().trim().toUpperCase();

        String cinNumber = null;
        if (businessType == BusinessType.PRIVATE_LIMITED || businessType == BusinessType.PUBLIC_LIMITED) {
            System.out.print("  " + CYAN + "CIN Number:" + RESET + " ");
            cinNumber = scanner.nextLine().trim().toUpperCase();
        }

        // Authorized Person Details
        System.out.println("\n  " + YELLOW + "Authorized Person Details:" + RESET);
        System.out.print("  " + CYAN + "Full Name:" + RESET + " ");
        String name = scanner.nextLine().trim();
        if (name.length() < 3) {
            showError("Name must be at least 3 characters long");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Date of Birth (YYYY-MM-DD):" + RESET + " ");
        String dobStr = scanner.nextLine().trim();
        java.time.LocalDate dob;
        try {
            dob = java.time.LocalDate.parse(dobStr);
        } catch (Exception e) {
            showError("Invalid date format. Please use YYYY-MM-DD");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Email Address:" + RESET + " ");
        String email = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "Mobile Number (10 digits):" + RESET + " ");
        String mobile = scanner.nextLine().trim();
        if (!mobile.matches("\\d{10}")) {
            showError("Invalid mobile number format");
            pause();
            return;
        }

        if (bank.findCustomerByMobile(mobile) != null) {
            showError("Customer already exists. Please use NetBanking Login.");
            pause();
            return;
        }

        // Registered Office Address
        System.out.println("\n  " + YELLOW + "Registered Office Address:" + RESET);
        System.out.print("  " + CYAN + "Street Address:" + RESET + " ");
        String address = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "City:" + RESET + " ");
        String city = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "State:" + RESET + " ");
        String state = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "Pincode (6 digits):" + RESET + " ");
        String pincode = scanner.nextLine().trim();

        // KYC Documents
        System.out.println("\n  " + YELLOW + "KYC Documents:" + RESET);
        System.out.print("  " + CYAN + "PAN Number (Company PAN):" + RESET + " ");
        String pan = scanner.nextLine().trim().toUpperCase();

        System.out.print("  " + CYAN + "Aadhar Number (Authorized Person):" + RESET + " ");
        String aadhar = scanner.nextLine().trim();

        // Account Selection - Corporate accounts only
        System.out.println("\n  " + YELLOW + "Account Type:" + RESET);
        System.out.println("  Corporate accounts are automatically set to CORPORATE type");
        AccountType type = AccountType.CORPORATE;

        System.out.print("\n  " + CYAN + "Initial Deposit (₹):" + RESET + " ");
        double deposit = getDoubleInputDirect();

        String accNum = bank.createCorporateAccount(name, mobile, email, dob, address, city, state, pincode,
                pan, aadhar, businessType, companyName, gstNumber, cinNumber,
                deposit, type);
        Customer cust = bank.findCustomerByMobile(mobile);

        if (accNum != null && cust != null) {
            clearScreen();
            printSectionHeader("CORPORATE ACCOUNT CREATED SUCCESSFULLY");

            printCard("Your Corporate Account Details",
                    BRIGHT_GREEN + "Account Number:  " + RESET + BOLD + accNum + RESET,
                    BRIGHT_CYAN + "Customer ID:     " + RESET + cust.getCustomerId(),
                    BRIGHT_YELLOW + "Business Type:   " + RESET + businessType.getDisplayName(),
                    BRIGHT_YELLOW + "Company Name:    " + RESET + companyName,
                    "",
                    MAGENTA + "Login Credentials:" + RESET,
                    "  Username: " + BOLD + cust.getCustomerId() + RESET,
                    "  Password: " + BOLD + cust.getPassword() + RESET,
                    "",
                    DIM + "Please save these credentials securely" + RESET);

            showSuccess("Your corporate account is now active!");
        }
        pause();
    }

    private static void login() {
        clearScreen();
        printSectionHeader("NETBANKING LOGIN");

        System.out.print("  " + CYAN + "Customer ID / Mobile:" + RESET + " ");
        String identifier = scanner.nextLine().trim();

        currentCustomer = bank.findCustomer(identifier);

        if (currentCustomer == null) {
            showError("Customer not found. Please check your credentials.");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Password:" + RESET + " ");
        String password = scanner.nextLine().trim();

        if (!currentCustomer.validatePassword(password)) {
            if (currentCustomer.isLocked()) {
                showError("Account locked due to multiple failed login attempts. Please contact administrator.");
            } else {
                int attemptsLeft = 3 - currentCustomer.getFailedLoginAttempts();
                showError("Incorrect password. " + attemptsLeft + " attempts remaining before account lock.");
            }
            currentCustomer = null;
            pause();
            return;
        }

        selectAccount();

        boolean loggedIn = true;
        while (loggedIn) {
            int choice = displayDashboard();

            switch (choice) {
                case 1:
                    clearScreen();
                    printSectionHeader("QUICK STATEMENT");
                    currentAccount.displayQuickStatement();
                    pause();
                    break;
                case 2:
                    generateDetailedBankStatement();
                    break;
                case 3:
                    viewAccountDetails();
                    break;
                case 4:
                    depositFunds();
                    break;
                case 5:
                    withdrawFunds();
                    break;
                case 6:
                    transferFunds();
                    break;
                case 7:
                    viewBenefits();
                    break;
                case 8:
                    securitySettingsMenu();
                    break;
                case 9:
                    investmentsMenu();
                    break;
                case 10:
                    accountManagementMenu();
                    break;
                case 11:
                    selectAccount();
                    break;
                case 12:
                    openAdditionalAccount();
                    break;
                case 13:
                    if (currentAccount.getAccountType() == AccountType.CORPORATE) {
                        corporateServices();
                    } else {
                        showError("Invalid option");
                        pause();
                    }
                    break;
                case 14:
                    mailboxMenu();
                    break;
                case 0:
                    loggedIn = false;
                    currentCustomer = null;
                    currentAccount = null;
                    showInfo("Logged out successfully");
                    pause();
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    private static int displayDashboard() {
        clearScreen();

        // Account Summary Card
        System.out.println("\n\n");
        System.out.println("  " + BOLD + BRIGHT_CYAN + "ACCOUNT DASHBOARD" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "═══════════════════════════════════════════════════════════" + RESET);
        System.out.println();

        System.out.println("  " + CYAN + "Customer:  " + RESET + BOLD + currentCustomer.getCustomerName() + RESET);
        System.out.println("  " + DIM + "ID: " + currentCustomer.getCustomerId() + RESET);

        if (currentCustomer.getRelationshipManagerId() != null) {
            Staff rm = bank.getStaffById(currentCustomer.getRelationshipManagerId());
            if (rm != null) {
                System.out.println("  " + BRIGHT_MAGENTA + "Relationship Manager: " + RESET + rm.getName() + " ("
                        + rm.getTypeDisplay() + ")");
            }
        }

        System.out.println();
        System.out.println("  " + YELLOW + "Active Account:  " + RESET + BOLD
                + currentAccount.getAccountNumber() + RESET);
        System.out.println(
                "  " + DIM + "Type: " + currentAccount.getAccountType().getDisplayName() + RESET);
        System.out.println();
        System.out.println("  " + BRIGHT_GREEN + "Available Balance:  " + RESET + BOLD + WHITE + "₹ "
                + String.format("%,.2f", currentAccount.getBalance()) + RESET);
        System.out.println();

        // Menu Options
        System.out.println("  " + BOLD + CYAN + "BANKING SERVICES" + RESET);
        System.out.println("  " + DIM + "─────────────────────────────────────────────────────────────" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "[1]" + RESET + "  Quick Statement (Last 5)");
        System.out.println("  " + BRIGHT_CYAN + "[2]" + RESET + "  Detailed Passbook (All Transactions)");
        System.out.println("  " + BRIGHT_CYAN + "[3]" + RESET + "  Account Details & Information");
        System.out.println("  " + BRIGHT_GREEN + "[4]" + RESET + "  Deposit Funds");
        System.out.println("  " + BRIGHT_YELLOW + "[5]" + RESET + "  Withdraw Funds");
        System.out.println("  " + BRIGHT_BLUE + "[6]" + RESET + "  Transfer Funds");
        System.out.println("  " + MAGENTA + "[7]" + RESET + "  Benefits & Rewards");
        System.out.println();

        System.out.println("  " + BOLD + YELLOW + "ADVANCED FEATURES" + RESET);
        System.out.println("  " + DIM + "─────────────────────────────────────────────────────────────" + RESET);
        System.out.println("  " + BRIGHT_MAGENTA + "[8]" + RESET + "  Security Settings (Password, PIN)");
        System.out.println("  " + BRIGHT_GREEN + "[9]" + RESET + "  Investments (FD/RD)");
        System.out.println("  " + BRIGHT_YELLOW + "[10]" + RESET + " Account Management");
        System.out.println();

        System.out.println("  " + BOLD + GREEN + "ACCOUNT OPTIONS" + RESET);
        System.out.println("  " + DIM + "─────────────────────────────────────────────────────────────" + RESET);
        System.out.println("  " + CYAN + "[11]" + RESET + " Switch Account");
        System.out.println("  " + GREEN + "[12]" + RESET + " Open New Account");

        if (currentAccount.getAccountType() == AccountType.CORPORATE) {
            System.out.println("  " + YELLOW + "[13]" + RESET + " Corporate Services");
        }

        // Mailbox display with unread count
        int unreadCount = currentCustomer.getUnreadMessageCount();
        String notification = unreadCount > 0 ? BRIGHT_RED + " (" + unreadCount + " new)" + RESET : "";
        System.out.println(
                "  " + BRIGHT_BLUE + "[14]" + RESET + " Mailbox" + BRIGHT_GREEN + " [NEW]" + RESET + notification);

        System.out.println();
        System.out.println("  " + RED + "[0]" + RESET + "  Logout");
        System.out.println();

        System.out.print("  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
        return getIntInputDirect();
    }

    private static void viewAccountDetails() {
        clearScreen();
        printSectionHeader("ACCOUNT DETAILS & INFORMATION");

        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";
        String WHITE = "\u001B[37m";
        String GREEN = "\u001B[32m";
        String MAGENTA = "\u001B[35m";

        // ACCOUNT INFORMATION
        System.out.println("\n  " + BOLD + CYAN + "ACCOUNT INFORMATION" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_CYAN + "Account Number:      " + RESET + BOLD
                + currentAccount.getAccountNumber() + RESET);
        System.out.println("  " + BRIGHT_CYAN + "Account Type:        " + RESET + BOLD
                + currentAccount.getAccountType().getDisplayName() + RESET);
        System.out.println("  " + BRIGHT_CYAN + "Account Status:      " + RESET
                + (currentAccount.isActive() ? GREEN + "● Active" : "○ Inactive") + RESET);
        System.out.println("  " + BRIGHT_CYAN + "Frozen Status:       " + RESET
                + (currentAccount.isFrozen() ? "🔒 Frozen" : GREEN + "✓ Normal" + RESET));
        System.out.println("  " + BRIGHT_GREEN + "Current Balance:     " + RESET + BOLD + "₹ "
                + String.format("%,.2f", currentAccount.getBalance()) + RESET);
        System.out.println("  " + DIM + "Minimum Balance:     ₹"
                + String.format("%,.2f", currentAccount.getAccountType().getMinimumBalance()) + RESET);

        // CUSTOMER INFORMATION
        System.out.println("\n  " + BOLD + CYAN + "CUSTOMER INFORMATION" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_CYAN + "Customer ID:         " + RESET + currentCustomer.getCustomerId());
        System.out.println("  " + BRIGHT_CYAN + "Full Name:           " + RESET + BOLD
                + currentCustomer.getCustomerName() + RESET);
        System.out.println("  " + BRIGHT_CYAN + "Date of Birth:       " + RESET + currentCustomer.getDateOfBirth());

        java.time.LocalDate dob = currentCustomer.getDateOfBirth();
        java.time.LocalDate now = java.time.LocalDate.now();
        int age = java.time.Period.between(dob, now).getYears();
        System.out.println("  " + DIM + "Age:                 " + age + " years" + RESET);

        // CONTACT INFORMATION
        System.out.println("\n  " + BOLD + CYAN + "CONTACT INFORMATION" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_CYAN + "Mobile Number:       " + RESET + currentCustomer.getMobileNumber());
        System.out.println("  " + BRIGHT_CYAN + "Email Address:       " + RESET + currentCustomer.getEmail());
        System.out.println("  " + BRIGHT_CYAN + "Address:             " + RESET + currentCustomer.getAddress());
        System.out.println("  " + BRIGHT_CYAN + "City:                " + RESET + currentCustomer.getCity());
        System.out.println("  " + BRIGHT_CYAN + "State:               " + RESET + currentCustomer.getState());
        System.out.println("  " + BRIGHT_CYAN + "PIN Code:            " + RESET + currentCustomer.getPincode());

        // KYC DOCUMENTS
        System.out.println("\n  " + BOLD + CYAN + "KYC DOCUMENTS" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println(
                "  " + BRIGHT_CYAN + "PAN Number:          " + RESET + BOLD + currentCustomer.getPanNumber() + RESET);
        System.out.println("  " + BRIGHT_CYAN + "Aadhar Number:       " + RESET + BOLD
                + maskAadhar(currentCustomer.getAadharNumber()) + RESET);
        System.out.println("  " + DIM + "KYC Status:          " + GREEN + "✓ Verified" + RESET);

        // CORPORATE DETAILS (if applicable)
        if (currentCustomer.getBusinessType() != null) {
            System.out.println("\n  " + BOLD + MAGENTA + "CORPORATE DETAILS" + RESET);
            System.out.println("  ═══════════════════════════════════════════════════════════════════════");
            System.out.println();
            System.out
                    .println("  " + BRIGHT_CYAN + "Business Type:       " + RESET + currentCustomer.getBusinessType());
            System.out.println("  " + BRIGHT_CYAN + "Company Name:        " + RESET + BOLD
                    + currentCustomer.getCompanyName() + RESET);
            System.out.println("  " + BRIGHT_CYAN + "GST Number:          " + RESET + currentCustomer.getGstNumber());
            System.out.println("  " + BRIGHT_CYAN + "CIN Number:          " + RESET + currentCustomer.getCinNumber());
        }

        // BANK BRANCH DETAILS
        System.out.println("\n  " + BOLD + CYAN + "BANK BRANCH DETAILS" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_CYAN + "Branch Name:         " + RESET + currentAccount.getBranchName());
        System.out.println(
                "  " + BRIGHT_CYAN + "IFSC Code:           " + RESET + BOLD + currentAccount.getIfscCode() + RESET);
        System.out.println(
                "  " + BRIGHT_CYAN + "SWIFT Code:          " + RESET + BOLD + currentAccount.getSwiftCode() + RESET);

        // ACCOUNT BENEFITS
        System.out.println("\n  " + BOLD + BRIGHT_YELLOW + "ACCOUNT BENEFITS" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        for (String benefit : currentAccount.getAccountType().getBenefits()) {
            System.out.println("  ✨ " + benefit);
        }

        // ACCOUNT STATISTICS
        System.out.println("\n  " + BOLD + CYAN + "ACCOUNT STATISTICS" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_CYAN + "Total Accounts:      " + RESET + currentCustomer.getAccountCount());
        System.out.println("  " + BRIGHT_CYAN + "Cheque Books:        " + RESET + currentAccount.getChequeBookRequests()
                + " requested");
        System.out.println(
                "  " + BRIGHT_CYAN + "Interest Rate:       " + RESET + currentAccount.getInterestRate() + "% p.a.");

        System.out.println();
        pause();
    }

    private static String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() < 4) {
            return "****";
        }
        return "XXXX-XXXX-" + aadhar.substring(aadhar.length() - 4);
    }

    private static void depositFunds() {
        clearScreen();
        printSectionHeader("DEPOSIT FUNDS");

        System.out.print("  " + CYAN + "Amount to Deposit (₹):" + RESET + " ");
        double amount = getDoubleInputDirect();

        currentAccount.deposit(amount);
        PersistenceManager.saveBank(bank); // Auto-save after transaction
        pause();
    }

    private static void withdrawFunds() {
        clearScreen();
        printSectionHeader("WITHDRAW FUNDS");

        System.out.print("  " + CYAN + "Amount to Withdraw (₹):" + RESET + " ");
        double amount = getDoubleInputDirect();

        currentAccount.withdraw(amount);
        PersistenceManager.saveBank(bank); // Auto-save after transaction
        pause();
    }

    private static void transferFunds() {
        clearScreen();
        printSectionHeader("FUND TRANSFER");

        System.out.print("  " + CYAN + "Beneficiary Account Number:" + RESET + " ");
        String toAcc = scanner.nextLine().trim();

        BankAccount dest = bank.getAccount(toAcc);
        if (dest == null) {
            showError("Beneficiary account not found");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Amount to Transfer (₹):" + RESET + " ");
        double amount = getDoubleInputDirect();

        System.out.print("  " + CYAN + "Remark (optional):" + RESET + " ");
        String remark = scanner.nextLine().trim();

        // Review Screen
        System.out.println("\n" + YELLOW + "  ┌─────────────────────────────────────────────────────────────┐");
        System.out.println("  │ " + BOLD + "TRANSFER REVIEW" + RESET + YELLOW);
        System.out.println("  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(YELLOW + "  │ " + RESET + "From:        " + currentAccount.getAccountNumber());
        System.out.println(YELLOW + "  │ " + RESET + "To:          " + dest.getAccountNumber() + " ("
                + dest.getAccountHolderName() + ")");
        System.out.println(
                YELLOW + "  │ " + RESET + "Amount:      " + BOLD + "₹ " + String.format("%,.2f", amount) + RESET);
        System.out.println(YELLOW + "  │ " + RESET + "Remark:      " + (remark.isEmpty() ? "-" : remark));
        System.out.println(YELLOW + "  └─────────────────────────────────────────────────────────────┘" + RESET);

        System.out.print("\n  " + BRIGHT_YELLOW + "Confirm transfer? (Y/N):" + RESET + " ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            bank.wireTransfer(currentAccount.getAccountNumber(), toAcc, amount, remark);
            PersistenceManager.saveBank(bank); // Auto-save after transaction
        } else {
            showInfo("Transfer cancelled");
        }
        pause();
    }

    private static void viewBenefits() {
        clearScreen();
        printSectionHeader("BENEFITS & REWARDS");

        List<String> benefits = currentAccount.getAccountType().getBenefits();

        System.out.println("  " + CYAN + "Available benefits for " + BOLD
                + currentAccount.getAccountType().getDisplayName() + RESET + "\n");

        for (int i = 0; i < benefits.size(); i++) {
            System.out.println("  " + BRIGHT_GREEN + (i + 1) + RESET + ". " + benefits.get(i));
        }
        System.out.println("  " + RED + "0" + RESET + ". Back");

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Request benefit: ");
        int choice = getIntInputDirect();

        if (choice > 0 && choice <= benefits.size()) {
            showSuccess("Request for '" + benefits.get(choice - 1) + "' submitted successfully!");
            showInfo("Reference ID: REF" + System.currentTimeMillis());
        }
        pause();
    }

    // ==================== SECURITY SETTINGS ====================

    private static void securitySettingsMenu() {
        boolean inSecurityMenu = true;
        while (inSecurityMenu) {
            clearScreen();
            printSectionHeader("SECURITY SETTINGS");

            System.out
                    .println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
            System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "ACCOUNT SECURITY" + RESET);
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_CYAN + "1" + RESET + "  │  Change Password");
            System.out.println(
                    DIM + "  │" + RESET + "  " + BRIGHT_GREEN + "2" + RESET + "  │  Setup/Change Transaction PIN");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_YELLOW + "3" + RESET + "  │  Freeze Account");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_BLUE + "4" + RESET + "  │  Unfreeze Account");
            System.out.println(DIM + "  │" + RESET + "  " + MAGENTA + "5" + RESET + "  │  View Security Status");
            System.out.println(DIM + "  │" + RESET + "  " + RED + "0" + RESET + "  │  Back to Dashboard");
            System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    changePassword();
                    break;
                case 2:
                    setupTransactionPIN();
                    break;
                case 3:
                    currentAccount.freezeAccount();
                    pause();
                    break;
                case 4:
                    currentAccount.unfreezeAccount();
                    pause();
                    break;
                case 5:
                    viewSecurityStatus();
                    break;
                case 0:
                    inSecurityMenu = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    private static void changePassword() {
        clearScreen();
        printSectionHeader("CHANGE PASSWORD");

        System.out.print("\n  " + CYAN + "Current Password:" + RESET + " ");
        String oldPassword = scanner.nextLine().trim();

        if (!currentCustomer.validatePassword(oldPassword)) {
            showError("Incorrect current password");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "New Password (min 6 characters):" + RESET + " ");
        String newPassword = scanner.nextLine().trim();

        if (newPassword.length() < 6) {
            showError("Password must be at least 6 characters");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Confirm New Password:" + RESET + " ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match");
            pause();
            return;
        }

        try {
            currentCustomer.setPassword(oldPassword, newPassword);
            showSuccess("Password changed successfully!");
        } catch (Exception e) {
            showError(e.getMessage());
        }
        pause();
    }

    private static void setupTransactionPIN() {
        clearScreen();
        printSectionHeader("TRANSACTION PIN SETUP");

        if (currentCustomer.hasTransactionPin()) {
            System.out.println("\n  " + YELLOW + "You already have a PIN set. This will change it." + RESET);
        }

        System.out.print("\n  " + CYAN + "Enter 4-digit PIN:" + RESET + " ");
        String pin = scanner.nextLine().trim();

        if (!pin.matches("\\d{4}")) {
            showError("PIN must be exactly 4 digits");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Confirm PIN:" + RESET + " ");
        String confirmPin = scanner.nextLine().trim();

        if (!pin.equals(confirmPin)) {
            showError("PINs do not match");
            pause();
            return;
        }

        try {
            currentCustomer.setTransactionPin(pin);
            showSuccess("Transaction PIN set successfully!");
        } catch (Exception e) {
            showError(e.getMessage());
        }
        pause();
    }

    private static void viewSecurityStatus() {
        clearScreen();
        printSectionHeader("SECURITY STATUS");

        System.out.println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "ACCOUNT SECURITY STATUS" + RESET);
        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Account Status:      " + RESET +
                (currentAccount.isFrozen() ? RED + "FROZEN" : GREEN + "ACTIVE") + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Transaction PIN:     " + RESET +
                (currentCustomer.hasTransactionPin() ? GREEN + "SET" : YELLOW + "NOT SET") + RESET);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Login Attempts:      " + RESET +
                currentCustomer.getFailedLoginAttempts() + "/3");
        System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Account Lock Status: " + RESET +
                (currentCustomer.isLocked() ? RED + "LOCKED" : GREEN + "UNLOCKED") + RESET);

        if (currentCustomer.hasNominee()) {
            System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Nominee:             " + RESET +
                    GREEN + "REGISTERED" + RESET);
        }

        System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
        pause();
    }

    // ==================== INVESTMENTS MENU ====================

    private static void investmentsMenu() {
        boolean inInvestmentsMenu = true;
        while (inInvestmentsMenu) {
            clearScreen();
            printSectionHeader("INVESTMENTS - FIXED & RECURRING DEPOSITS");

            System.out
                    .println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
            System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "FIXED DEPOSITS" + RESET);
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_GREEN + "1" + RESET + "  │  Create Fixed Deposit");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_CYAN + "2" + RESET + "  │  View All Fixed Deposits");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_YELLOW + "3" + RESET + "  │  Close Fixed Deposit");
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │ " + RESET + BOLD + YELLOW + "RECURRING DEPOSITS" + RESET);
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out
                    .println(DIM + "  │" + RESET + "  " + BRIGHT_GREEN + "4" + RESET + "  │  Create Recurring Deposit");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_CYAN + "5" + RESET + "  │  Pay RD Installment");
            System.out.println(
                    DIM + "  │" + RESET + "  " + BRIGHT_BLUE + "6" + RESET + "  │  View All Recurring Deposits");
            System.out
                    .println(DIM + "  │" + RESET + "  " + BRIGHT_YELLOW + "7" + RESET + "  │  Close Recurring Deposit");
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │" + RESET + "  " + RED + "0" + RESET + "  │  Back to Dashboard");
            System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    createFixedDeposit();
                    break;
                case 2:
                    clearScreen();
                    printSectionHeader("YOUR FIXED DEPOSITS");
                    currentAccount.displayFixedDeposits();
                    pause();
                    break;
                case 3:
                    closeFixedDeposit();
                    break;
                case 4:
                    createRecurringDeposit();
                    break;
                case 5:
                    payRDInstallment();
                    break;
                case 6:
                    clearScreen();
                    printSectionHeader("YOUR RECURRING DEPOSITS");
                    currentAccount.displayRecurringDeposits();
                    pause();
                    break;
                case 7:
                    closeRecurringDeposit();
                    break;
                case 0:
                    inInvestmentsMenu = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    private static void createFixedDeposit() {
        clearScreen();
        printSectionHeader("CREATE FIXED DEPOSIT");

        System.out.println("\n  " + CYAN + "Interest Rates:" + RESET);
        System.out.println("  6 months:  6.5% p.a.");
        System.out.println("  12 months: 7.0% p.a.");
        System.out.println("  24 months: 7.5% p.a.");
        System.out.println("  36+ months: 8.0% p.a.");
        System.out.println("\n  " + DIM + "Minimum: ₹10,000 | Compounded Quarterly" + RESET);

        System.out.print("\n  " + CYAN + "FD Amount (₹):" + RESET + " ");
        double amount = getDoubleInputDirect();

        System.out.print("  " + CYAN + "Tenure (months):" + RESET + " ");
        int tenure = getIntInputDirect();

        if (tenure < 6) {
            showError("Minimum tenure is 6 months");
            pause();
            return;
        }

        String fdNumber = currentAccount.createFixedDeposit(amount, tenure);

        if (fdNumber != null) {
            showSuccess("Fixed Deposit created successfully!");
            System.out.println("\n  " + BRIGHT_GREEN + "FD Number: " + RESET + BOLD + fdNumber + RESET);
        }
        pause();
    }

    private static void closeFixedDeposit() {
        clearScreen();
        printSectionHeader("CLOSE FIXED DEPOSIT");

        currentAccount.displayFixedDeposits();

        System.out.print("\n  " + CYAN + "Enter FD Number to close:" + RESET + " ");
        String fdNumber = scanner.nextLine().trim();

        System.out.print("  " + YELLOW + "Is this premature withdrawal? (yes/no):" + RESET + " ");
        String premature = scanner.nextLine().trim().toLowerCase();

        boolean isPremature = premature.equals("yes") || premature.equals("y");

        if (currentAccount.closeFD(fdNumber, isPremature)) {
            showSuccess("FD closed and amount credited to your account!");
        } else {
            showError("FD not found or already closed");
        }
        pause();
    }

    private static void createRecurringDeposit() {
        clearScreen();
        printSectionHeader("CREATE RECURRING DEPOSIT");

        System.out.println("\n  " + CYAN + "Interest Rates:" + RESET);
        System.out.println("  6 months:  6.0% p.a.");
        System.out.println("  12 months: 6.5% p.a.");
        System.out.println("  24+ months: 7.0% p.a.");
        System.out.println("\n  " + DIM + "Minimum: ₹500/month" + RESET);

        System.out.print("\n  " + CYAN + "Monthly Installment (₹):" + RESET + " ");
        double amount = getDoubleInputDirect();

        System.out.print("  " + CYAN + "Tenure (months):" + RESET + " ");
        int tenure = getIntInputDirect();

        if (tenure < 6) {
            showError("Minimum tenure is 6 months");
            pause();
            return;
        }

        String rdNumber = currentAccount.createRecurringDeposit(amount, tenure);

        if (rdNumber != null) {
            showSuccess("Recurring Deposit created successfully!");
            System.out.println("\n  " + BRIGHT_GREEN + "RD Number: " + RESET + BOLD + rdNumber + RESET);
            System.out.println("  " + YELLOW + "Remember to pay monthly installments!" + RESET);
        }
        pause();
    }

    private static void payRDInstallment() {
        clearScreen();
        printSectionHeader("PAY RD INSTALLMENT");

        currentAccount.displayRecurringDeposits();

        System.out.print("\n  " + CYAN + "Enter RD Number:" + RESET + " ");
        String rdNumber = scanner.nextLine().trim();

        if (currentAccount.payRDInstallment(rdNumber)) {
            showSuccess("Installment paid successfully!");
        } else {
            showError("Payment failed. Check RD number and balance.");
        }
        pause();
    }

    private static void closeRecurringDeposit() {
        clearScreen();
        printSectionHeader("CLOSE RECURRING DEPOSIT");

        currentAccount.displayRecurringDeposits();

        System.out.print("\n  " + CYAN + "Enter RD Number to close:" + RESET + " ");
        String rdNumber = scanner.nextLine().trim();

        if (currentAccount.closeRD(rdNumber)) {
            showSuccess("RD closed and maturity amount credited to your account!");
        } else {
            showError("RD not found or already closed");
        }
        pause();
    }

    // ==================== ACCOUNT MANAGEMENT ====================

    private static void accountManagementMenu() {
        boolean inAccountMenu = true;
        while (inAccountMenu) {
            clearScreen();
            printSectionHeader("ACCOUNT MANAGEMENT");

            System.out
                    .println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
            System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "MANAGE YOUR ACCOUNT" + RESET);
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_CYAN + "1" + RESET + "  │  Add/Update Nominee");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_GREEN + "2" + RESET + "  │  Request Cheque Book");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_YELLOW + "3" + RESET + "  │  Upgrade Account");
            System.out.println(DIM + "  │" + RESET + "  " + BRIGHT_BLUE + "4" + RESET + "  │  Downgrade Account");
            System.out.println(DIM + "  │" + RESET + "  " + MAGENTA + "5" + RESET + "  │  View Nominee Details");
            System.out.println(DIM + "  │" + RESET + "  " + RED + "0" + RESET + "  │  Back to Dashboard");
            System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    addUpdateNominee();
                    break;
                case 2:
                    currentAccount.requestChequeBook();
                    pause();
                    break;
                case 3:
                    upgradeAccount();
                    break;
                case 4:
                    downgradeAccount();
                    break;
                case 5:
                    viewNomineeDetails();
                    break;
                case 0:
                    inAccountMenu = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    private static void addUpdateNominee() {
        clearScreen();
        printSectionHeader("NOMINEE MANAGEMENT");

        if (currentCustomer.hasNominee()) {
            System.out.println("\n  " + YELLOW + "Current Nominee: " + currentCustomer.getNomineeName() + RESET);
            System.out.println("  " + DIM + "This will update the existing nominee" + RESET + "\n");
        }

        System.out.print("  " + CYAN + "Nominee Full Name:" + RESET + " ");
        String name = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "Relationship:" + RESET + " ");
        String relation = scanner.nextLine().trim();

        System.out.print("  " + CYAN + "Nominee Date of Birth (YYYY-MM-DD):" + RESET + " ");
        String dobStr = scanner.nextLine().trim();

        try {
            java.time.LocalDate dob = java.time.LocalDate.parse(dobStr);
            currentCustomer.setNominee(name, relation, dob);
            showSuccess("Nominee details saved successfully!");
        } catch (Exception e) {
            showError("Invalid date format. Please use YYYY-MM-DD");
        }
        pause();
    }

    private static void upgradeAccount() {
        clearScreen();
        printSectionHeader("UPGRADE ACCOUNT");

        System.out.println("\n  " + CYAN + "Current Account Type: " + RESET + BOLD +
                currentAccount.getAccountType().getDisplayName() + RESET);
        System.out.println("  " + CYAN + "Current Balance: " + RESET + "₹" +
                String.format("%,.2f", currentAccount.getBalance()));

        System.out.println("\n  " + YELLOW + "Available Upgrades:" + RESET);
        AccountType.displayAccountTypes();

        System.out.print("\n  " + CYAN + "Select new account type (1-4):" + RESET + " ");
        int typeChoice = getIntInputDirect();

        AccountType newType = null;
        switch (typeChoice) {
            case 1:
                newType = AccountType.BASIC;
                break;
            case 2:
                newType = AccountType.PRIORITY;
                break;
            case 3:
                newType = AccountType.WEALTH;
                break;
            case 4:
                newType = AccountType.CORPORATE;
                break;
            default:
                showError("Invalid choice");
                pause();
                return;
        }

        bank.upgradeAccount(currentAccount.getAccountNumber(), newType);
        pause();
    }

    private static void downgradeAccount() {
        clearScreen();
        printSectionHeader("DOWNGRADE ACCOUNT");

        System.out.println("\n  " + CYAN + "Current Account Type: " + RESET + BOLD +
                currentAccount.getAccountType().getDisplayName() + RESET);

        System.out.println("\n  " + YELLOW + "Available Account Types:" + RESET);
        AccountType.displayAccountTypes();

        System.out.print("\n  " + CYAN + "Select new account type (1-4):" + RESET + " ");
        int typeChoice = getIntInputDirect();

        AccountType newType = null;
        switch (typeChoice) {
            case 1:
                newType = AccountType.BASIC;
                break;
            case 2:
                newType = AccountType.PRIORITY;
                break;
            case 3:
                newType = AccountType.WEALTH;
                break;
            case 4:
                newType = AccountType.CORPORATE;
                break;
            default:
                showError("Invalid choice");
                pause();
                return;
        }

        bank.downgradeAccount(currentAccount.getAccountNumber(), newType);
        pause();
    }

    private static void viewNomineeDetails() {
        clearScreen();
        printSectionHeader("NOMINEE DETAILS");

        if (!currentCustomer.hasNominee()) {
            System.out.println("\n  " + YELLOW + "No nominee registered" + RESET);
            System.out.println("  " + DIM + "Please add a nominee for account security" + RESET);
        } else {
            System.out
                    .println("\n" + DIM + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
            System.out.println(DIM + "  │ " + RESET + BOLD + CYAN + "REGISTERED NOMINEE" + RESET);
            System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
            System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Name:         " + RESET +
                    currentCustomer.getNomineeName());
            System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Relationship: " + RESET +
                    currentCustomer.getNomineeRelation());
            System.out.println(DIM + "  │ " + RESET + BRIGHT_CYAN + "Date of Birth:" + RESET +
                    currentCustomer.getNomineeDoB());
            System.out.println(DIM + "  └─────────────────────────────────────────────────────────────┘" + RESET);
        }
        pause();
    }

    // ==================== CORPORATE SERVICES ====================

    private static void corporateServices() {
        boolean inCorporate = true;
        while (inCorporate) {
            clearScreen();
            printSectionHeader("CORPORATE BANKING SERVICES");

            System.out.println("\n  " + BOLD + CYAN + "BUSINESS OVERVIEW" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            System.out.println("  " + BRIGHT_CYAN + "[1]" + RESET + "  Corporate Dashboard        " + DIM
                    + "Business metrics" + RESET);

            System.out.println("\n  " + BOLD + GREEN + "VENDOR & PAYMENTS" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            System.out.println("  " + BRIGHT_GREEN + "[2]" + RESET + "  Vendor Management          " + DIM
                    + "Add/View vendors" + RESET);
            System.out.println("  " + BRIGHT_GREEN + "[3]" + RESET + "  Pay Vendor                 " + DIM
                    + "Quick payment" + RESET);
            System.out.println("  " + BRIGHT_GREEN + "[4]" + RESET + "  Bulk Payments              " + DIM
                    + "Pay multiple vendors" + RESET);

            System.out.println("\n  " + BOLD + YELLOW + "PAYROLL & OPERATIONS" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            System.out.println("  " + BRIGHT_YELLOW + "[5]" + RESET + "  Process Payroll            " + DIM
                    + "Employee salaries" + RESET);
            System.out.println("  " + BRIGHT_YELLOW + "[6]" + RESET + "  View Overdraft Limit       " + DIM
                    + "Credit facility" + RESET);

            System.out.println("\n  " + RED + "[0]" + RESET + "  Back to Dashboard");
            System.out.println();

            System.out.print("  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    showCorporateDashboard();
                    break;
                case 2:
                    vendorManagementMenu();
                    break;
                case 3:
                    payVendorQuick();
                    break;
                case 4:
                    bulkPayments();
                    break;
                case 5:
                    processPayroll();
                    break;
                case 6:
                    viewOverdraftLimit();
                    break;
                case 0:
                    inCorporate = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    // Corporate Dashboard
    private static void showCorporateDashboard() {
        clearScreen();
        printSectionHeader("CORPORATE DASHBOARD");

        System.out.println(
                "\n  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "                    BUSINESS OVERVIEW                          " + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);

        // Account Summary
        System.out.println("\n  " + BOLD + "Account Information" + RESET);
        System.out.println("  ─────────────────────────────────────────────────────────────");
        System.out.println("  Account Number:  " + BRIGHT_CYAN + currentAccount.getAccountNumber() + RESET);
        System.out.println(
                "  Account Type:    " + BRIGHT_GREEN + currentAccount.getAccountType().getDisplayName() + RESET);
        System.out.println("  Current Balance: " + BRIGHT_YELLOW + "₹ "
                + String.format("%,.2f", currentAccount.getBalance()) + RESET);

        double overdraft = currentAccount.getAccountType().getOverdraftLimit();
        if (overdraft > 0) {
            System.out.println("  Overdraft Limit: " + BRIGHT_GREEN + "₹ " + String.format("%,.2f", overdraft) + RESET);
            System.out.println("  Total Available: " + BOLD + BRIGHT_GREEN + "₹ " +
                    String.format("%,.2f", currentAccount.getBalance() + overdraft) + RESET);
        }

        // Vendor Statistics
        System.out.println("\n  " + BOLD + "Vendor Statistics" + RESET);
        System.out.println("  ─────────────────────────────────────────────────────────────");
        int vendorCount = currentAccount.getVendors().size();
        double totalPaid = currentAccount.getVendors().stream()
                .mapToDouble(v -> v.getTotalPaid())
                .sum();
        System.out.println("  Total Vendors:   " + BRIGHT_CYAN + vendorCount + RESET);
        System.out.println("  Total Paid:      " + BRIGHT_YELLOW + "₹ " + String.format("%,.2f", totalPaid) + RESET);

        // Recent Activity
        System.out.println("\n  " + BOLD + "Recent Activity" + RESET);
        System.out.println("  ─────────────────────────────────────────────────────────────");
        System.out.println("  Transactions:    " + currentAccount.getTransactionHistory().size());

        // Quick Actions
        System.out.println("\n  " + BOLD + "Quick Actions" + RESET);
        System.out.println("  ─────────────────────────────────────────────────────────────");
        System.out.println("  [1] Manage Vendors");
        System.out.println("  [2] Process Payroll");
        System.out.println("  [3] Bulk Payments");
        System.out.println("  [0] Back");

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
        int choice = getIntInputDirect();

        switch (choice) {
            case 1:
                vendorManagementMenu();
                break;
            case 2:
                processPayroll();
                break;
            case 3:
                bulkPayments();
                break;
        }
    }

    // Vendor Management Menu
    private static void vendorManagementMenu() {
        boolean inVendor = true;
        while (inVendor) {
            clearScreen();
            printSectionHeader("VENDOR MANAGEMENT");

            System.out.println("\n  " + CYAN + "Vendor Operations:" + RESET);
            System.out.println("  [1] Add New Vendor");
            System.out.println("  [2] View All Vendors");
            System.out.println("  [3] Search Vendor");
            System.out.println("  [4] Remove Vendor");
            System.out.println("  [0] Back");

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    addVendor();
                    break;
                case 2:
                    viewAllVendors();
                    break;
                case 3:
                    searchVendor();
                    break;
                case 4:
                    removeVendor();
                    break;
                case 0:
                    inVendor = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    // Add Vendor
    private static void addVendor() {
        clearScreen();
        printSectionHeader("ADD NEW VENDOR");

        System.out.println("\n  " + CYAN + "Enter Vendor Details:" + RESET + "\n");

        System.out.print("  Vendor Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            showError("Vendor name required");
            pause();
            return;
        }

        System.out.print("  Account Number: ");
        String accNum = scanner.nextLine().trim();

        System.out.print("  IFSC Code: ");
        String ifsc = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Bank Name: ");
        String bankName = scanner.nextLine().trim();

        System.out.print("  Category (e.g., Supplier, Contractor): ");
        String category = scanner.nextLine().trim();

        System.out.print("  Contact Person: ");
        String contact = scanner.nextLine().trim();

        System.out.print("  Mobile Number: ");
        String mobile = scanner.nextLine().trim();

        System.out.print("  Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("  GST Number (optional): ");
        String gst = scanner.nextLine().trim();

        // Generate vendor ID
        String vendorId = "VEN" + (currentAccount.getVendors().size() + 1001);

        Vendor vendor = new Vendor(vendorId, name, accNum, ifsc, bankName, category,
                contact, mobile, email, gst);

        currentAccount.addVendor(vendor);
        PersistenceManager.saveBank(bank);

        System.out.println("\n  " + GREEN + "✓ Vendor added successfully!" + RESET);
        System.out.println("  Vendor ID: " + BRIGHT_CYAN + vendorId + RESET);
        pause();
    }

    // View All Vendors
    private static void viewAllVendors() {
        clearScreen();
        printSectionHeader("VENDOR DATABASE");
        currentAccount.displayAllVendors();
        pause();
    }

    // Search Vendor
    private static void searchVendor() {
        clearScreen();
        printSectionHeader("SEARCH VENDOR");

        System.out.print("\n  Enter Vendor ID: ");
        String vendorId = scanner.nextLine().trim();

        Vendor vendor = currentAccount.findVendor(vendorId);
        if (vendor != null) {
            vendor.displayVendorInfo();
        } else {
            showError("Vendor not found!");
        }
        pause();
    }

    // Remove Vendor
    private static void removeVendor() {
        clearScreen();
        printSectionHeader("REMOVE VENDOR");

        System.out.print("\n  Enter Vendor ID: ");
        String vendorId = scanner.nextLine().trim();

        Vendor vendor = currentAccount.findVendor(vendorId);
        if (vendor == null) {
            showError("Vendor not found!");
            pause();
            return;
        }

        vendor.displayVendorInfo();
        System.out.print("\n  " + RED + "Confirm removal (Y/N): " + RESET);
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            if (currentAccount.removeVendor(vendorId)) {
                PersistenceManager.saveBank(bank);
                showSuccess("Vendor removed successfully!");
            } else {
                showError("Failed to remove vendor");
            }
        }
        pause();
    }

    // Pay Vendor Quick
    private static void payVendorQuick() {
        clearScreen();
        printSectionHeader("PAY VENDOR");

        if (currentAccount.getVendors().isEmpty()) {
            System.out.println("\n  " + DIM + "No vendors registered. Add vendors first." + RESET);
            pause();
            return;
        }

        // Show vendors
        System.out.println("\n  " + CYAN + "Available Vendors:" + RESET + "\n");
        for (Vendor v : currentAccount.getVendors()) {
            System.out.println("  [" + v.getVendorId() + "] " + v.getVendorName() +
                    " - " + DIM + v.getCategory() + RESET);
        }

        System.out.print("\n  Enter Vendor ID: ");
        String vendorId = scanner.nextLine().trim();

        Vendor vendor = currentAccount.findVendor(vendorId);
        if (vendor == null) {
            showError("Vendor not found!");
            pause();
            return;
        }

        System.out.println("\n  Paying to: " + BOLD + vendor.getVendorName() + RESET);
        System.out.println("  Account: " + vendor.getAccountNumber());

        System.out.print("\n  Amount (₹): ");
        double amount = getDoubleInputDirect();
        scanner.nextLine(); // consume newline

        System.out.print("  Description: ");
        String desc = scanner.nextLine().trim();

        if (currentAccount.payVendor(vendorId, amount, desc)) {
            PersistenceManager.saveBank(bank);
            showSuccess("Payment successful!");
        } else {
            showError("Payment failed!");
        }
        pause();
    }

    // Bulk Payments
    private static void bulkPayments() {
        clearScreen();
        printSectionHeader("BULK PAYMENTS");

        if (currentAccount.getVendors().isEmpty()) {
            System.out.println("\n  " + DIM + "No vendors registered. Add vendors first." + RESET);
            pause();
            return;
        }

        System.out.println("\n  " + CYAN + "Enter payments (Vendor ID, Amount):" + RESET);
        System.out.println("  " + DIM + "Format: VEN1001,5000 (one per line, empty to finish)" + RESET + "\n");

        java.util.List<String[]> payments = new java.util.ArrayList<>();

        while (true) {
            System.out.print("  Payment: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                break;

            String[] parts = input.split(",");
            if (parts.length == 2) {
                payments.add(parts);
            } else {
                System.out.println("  " + RED + "Invalid format. Use: VendorID,Amount" + RESET);
            }
        }

        if (payments.isEmpty()) {
            System.out.println("\n  " + DIM + "No payments entered" + RESET);
            pause();
            return;
        }

        // Confirm
        System.out.println("\n  " + BOLD + "Payment Summary:" + RESET);
        double total = 0;
        for (String[] payment : payments) {
            String vendorId = payment[0].trim();
            double amount = Double.parseDouble(payment[1].trim());
            Vendor v = currentAccount.findVendor(vendorId);
            if (v != null) {
                System.out.println("  " + v.getVendorName() + ": ₹" + String.format("%,.2f", amount));
                total += amount;
            }
        }
        System.out.println("  " + BOLD + "Total: ₹" + String.format("%,.2f", total) + RESET);

        System.out.print("\n  Confirm bulk payment (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            int success = 0;
            int failed = 0;

            for (String[] payment : payments) {
                String vendorId = payment[0].trim();
                double amount = Double.parseDouble(payment[1].trim());

                if (currentAccount.payVendor(vendorId, amount, "Bulk payment")) {
                    success++;
                } else {
                    failed++;
                }
            }

            PersistenceManager.saveBank(bank);
            System.out.println("\n  " + GREEN + "✓ Bulk payment complete!" + RESET);
            System.out.println("  Successful: " + success + " | Failed: " + failed);
        }
        pause();
    }

    // Process Payroll
    private static void processPayroll() {
        clearScreen();
        printSectionHeader("PROCESS PAYROLL");

        System.out.print("\n  " + CYAN + "Employee Account Numbers (comma-separated):" + RESET + " ");
        String rawAccs = scanner.nextLine().trim();

        List<String> accs = new java.util.ArrayList<>();
        for (String s : rawAccs.split(",")) {
            if (!s.trim().isEmpty())
                accs.add(s.trim());
        }

        if (accs.isEmpty()) {
            showError("No accounts entered");
            pause();
            return;
        }

        System.out.print("  " + CYAN + "Salary per Employee (₹):" + RESET + " ");
        double salary = getDoubleInputDirect();

        bank.processPayroll(currentAccount.getAccountNumber(), accs, salary);
        pause();
    }

    // View Overdraft Limit
    private static void viewOverdraftLimit() {
        clearScreen();
        printSectionHeader("OVERDRAFT FACILITY");

        printCard("Overdraft Facility",
                "Credit Limit:     ₹ " + String.format("%,.2f", currentAccount.getAccountType().getOverdraftLimit()),
                "Current Balance:  ₹ " + String.format("%,.2f", currentAccount.getBalance()),
                "Total Available:  " + BRIGHT_GREEN + "₹ " + String.format("%,.2f",
                        currentAccount.getBalance() + currentAccount.getAccountType().getOverdraftLimit()) + RESET);
        pause();
    }

    private static void openAdditionalAccount() {
        clearScreen();
        printSectionHeader("OPEN ADDITIONAL ACCOUNT");

        // Display individual account types only (exclude corporate)
        AccountType.displayIndividualAccountTypes();
        AccountType type = selectAccountType(false); // false = exclude corporate
        if (type == null)
            return;

        System.out.print("  " + CYAN + "Initial Deposit (₹):" + RESET + " ");
        double deposit = getDoubleInputDirect();

        bank.createAccountForExistingCustomer(currentCustomer.getCustomerId(), deposit, type);
        pause();
    }

    // ==================== ADMIN PANEL ====================

    private static void staffLoginMenu() {
        if (currentStaff != null) {
            staffDashboard();
            return;
        }

        clearScreen();
        printSectionHeader("STAFF ACCESS");

        System.out.println("  " + BRIGHT_CYAN + "[1]" + RESET + "  Login");
        System.out.println("  " + BRIGHT_GREEN + "[2]" + RESET + "  Register Staff (Verification Key Required)");
        System.out.println("  " + RED + "[0]" + RESET + "  Back to Main Menu");

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
        int choice = getIntInputDirect();

        switch (choice) {
            case 1:
                performStaffLogin();
                break;
            case 2:
                performStaffRegistration(true);
                break;
            case 0:
                return;
            default:
                showError("Invalid choice");
                pause();
        }
    }

    private static void performStaffLogin() {
        clearScreen();
        printSectionHeader("STAFF LOGIN");

        System.out.print("  Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("  Password: ");
        String password = scanner.nextLine().trim();

        Staff staff = bank.authenticateStaff(username, password);
        if (staff != null) {
            currentStaff = staff;
            showSuccess("Welcome, " + staff.getName());
            pause();
            staffDashboard();
        } else {
            showError("Invalid credentials");
            pause();
        }
    }

    private static void performStaffRegistration(boolean requireKey) {
        clearScreen();
        printSectionHeader("REGISTER NEW STAFF");

        if (requireKey) {
            System.out.print("  " + YELLOW + "Enter Verification Key:" + RESET + " ");
            String key = scanner.nextLine().trim();
            if (!key.equals("BOF6969")) {
                showError("Invalid Verification Key!");
                pause();
                return;
            }
        }

        // If we are here, we are authorized (either via key or manager call).

        System.out.println("\n  " + CYAN + "Select Staff Role:" + RESET);
        System.out.println("  [1] Bank Employee");
        System.out.println("  [2] Assistant Bank Manager");
        System.out.println("  [3] Bank Manager");

        System.out.print("\n  Select: ");
        int roleChoice = getIntInputDirect();
        StaffRole role = null;
        if (roleChoice == 1)
            role = StaffRole.BANK_EMPLOYEE;
        else if (roleChoice == 2)
            role = StaffRole.ASSISTANT_BANK_MANAGER;
        else if (roleChoice == 3)
            role = StaffRole.BANK_MANAGER;
        else {
            showError("Invalid role");
            pause();
            return;
        }

        // Check hierarchy if Manager is creating
        if (!requireKey && currentStaff != null) {
            if (!currentStaff.getRole().canCreate(role)) {
                showError("You cannot create a role equal to or higher than your own.");
                pause();
                return;
            }
        }

        System.out.print("  Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.length() < 3)
            return;

        System.out.print("  Username: ");
        String username = scanner.nextLine().trim();
        if (bank.findStaff(username) != null) {
            showError("Username already exists");
            pause();
            return;
        }

        System.out.print("  Password: ");
        String password = scanner.nextLine().trim();

        Staff newStaff = new Staff("S" + System.currentTimeMillis(), name, username, password, role);
        bank.addStaff(newStaff);
        PersistenceManager.saveBank(bank);

        showSuccess("Staff account created successfully for " + name + " (" + role.getDisplayName() + ")");
        pause();
    }

    private static void viewAssignedClients() {
        clearScreen();
        printSectionHeader("MY ASSIGNED CLIENTS");

        java.util.List<Customer> myClients = new java.util.ArrayList<>();
        for (Customer c : bank.getCustomers().values()) {
            if (currentStaff.getStaffId().equals(c.getRelationshipManagerId())) {
                myClients.add(c);
            }
        }

        if (myClients.isEmpty()) {
            System.out.println("\n  " + DIM + "No clients assigned to you." + RESET);
        } else {
            System.out.println("\n  " + BOLD + CYAN + "Total Clients: " + myClients.size() + RESET + "\n");
            for (Customer c : myClients) {
                System.out.println("  ID: " + BRIGHT_CYAN + c.getCustomerId() + RESET + "  Name: " + BOLD
                        + c.getCustomerName() + RESET);
                System.out.println("  Mobile: " + c.getMobileNumber() + "  Email: " + c.getEmail());
                System.out.println("  Accounts: " + c.getAccountNumbers().size());
                System.out.println("  " + DIM + "────────────────────────────────" + RESET);
            }
        }
        pause();
    }

    private static void viewStaffInbox() {
        clearScreen();
        printSectionHeader("MY STAFF INBOX");

        java.util.List<Message> inbox = currentStaff.getInbox();
        if (inbox.isEmpty()) {
            System.out.println("\n  " + DIM + "No messages in your personal inbox." + RESET);
        } else {
            java.util.Collections.sort(inbox, (m1, m2) -> m2.getSentDate().compareTo(m1.getSentDate()));

            int index = 1;
            for (Message msg : inbox) {
                System.out.println("  [" + index++ + "] " + msg.getShortDisplay());
            }

            System.out.println("\n  [#] Read Message  [0] Back");
            System.out.print("  Select number: ");
            int choice = getIntInputDirect();

            if (choice > 0 && choice <= inbox.size()) {
                Message selected = inbox.get(choice - 1);
                clearScreen();
                selected.markAsRead();
                selected.displayMessage();
                pause();
            } else if (choice != 0) {
                showError("Invalid message number.");
                pause();
            }
        }
        pause();
    }

    private static void assignRelationshipManagerAdmin() {
        clearScreen();
        printSectionHeader("ASSIGN RELATIONSHIP MANAGER");

        System.out.print("\n  " + CYAN + "Enter Customer ID:" + RESET + " ");
        String custId = scanner.nextLine().trim();
        Customer cust = bank.findCustomer(custId);
        if (cust == null) {
            showError("Customer not found.");
            pause();
            return;
        }

        System.out.println("  " + BOLD + "Selected Customer: " + cust.getCustomerName() + RESET);
        String currentRmId = cust.getRelationshipManagerId();
        if (currentRmId != null) {
            Staff currentRm = bank.getStaffById(currentRmId);
            System.out.println(
                    "  Current RM: " + (currentRm != null ? currentRm.getName() : "Unknown (ID: " + currentRmId + ")"));
        } else {
            System.out.println("  Current RM: None");
        }

        System.out.print("\n  " + CYAN + "Enter New Staff ID or Username:" + RESET + " ");
        String staffInput = scanner.nextLine().trim();
        Staff newRm = bank.getStaffById(staffInput);
        if (newRm == null) {
            newRm = bank.findStaff(staffInput);
        }

        if (newRm == null) {
            showError("Staff member not found.");
            pause();
            return;
        }

        System.out.println("\n  " + YELLOW + "Assigning " + newRm.getName() + " (" + newRm.getRole().getDisplayName()
                + ") to " + cust.getCustomerName() + "." + RESET);
        System.out.print("  Type 'CONFIRM' to proceed: ");
        if (scanner.nextLine().trim().equalsIgnoreCase("CONFIRM")) {
            cust.setRelationshipManagerId(newRm.getStaffId());
            PersistenceManager.saveBank(bank);
            showSuccess("Relationship Manager Assigned Successfully!");
        } else {
            showError("Assignment Cancelled.");
        }
        pause();
    }

    private static void staffDashboard() {
        boolean inAdmin = true;
        while (inAdmin) {
            clearScreen();
            String staffInfo = currentStaff != null
                    ? currentStaff.getName() + " (" + currentStaff.getTypeDisplay() + ")"
                    : "Administrator"; // Fallback if bypassed
            printSectionHeader("STAFF DASHBOARD | " + staffInfo);

            int level = currentStaff != null ? currentStaff.getRole().getLevel() : 3;

            boolean isEffectiveRM = level >= 2;
            if (!isEffectiveRM && currentStaff != null) {
                for (Customer c : bank.getCustomers().values()) {
                    if (currentStaff.getStaffId().equals(c.getRelationshipManagerId())) {
                        isEffectiveRM = true;
                        break;
                    }
                }
            }
            boolean hasInbox = currentStaff != null && !currentStaff.getInbox().isEmpty();

            System.out.println("\n  " + BOLD + CYAN + "REPORTS & ANALYTICS" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            System.out.println("  " + BRIGHT_CYAN + "[1]" + RESET + "  Customer Database           " + DIM
                    + "View all customers" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "[2]" + RESET + "  Account Registry            " + DIM
                    + "View all accounts" + RESET);
            if (level >= 3)
                System.out.println("  " + BRIGHT_CYAN + "[3]" + RESET + "  Revenue Analytics           " + DIM
                        + "Financial reports" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "[4]" + RESET + "  Transaction Summary         " + DIM
                    + "Today's activity" + RESET);

            System.out.println("\n  " + BOLD + GREEN + "ACCOUNT OPERATIONS" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            System.out.println("  " + BRIGHT_GREEN + "[5]" + RESET + "  Search Customer/Account     " + DIM
                    + "Find by ID/Mobile" + RESET);
            if (level >= 2)
                System.out.println("  " + BRIGHT_GREEN + "[6]" + RESET + "  Freeze/Unfreeze Account     " + DIM
                        + "Security action" + RESET);
            if (level >= 2)
                System.out.println("  " + BRIGHT_GREEN + "[7]" + RESET + "  Close Account               " + DIM
                        + "Permanent closure" + RESET);
            System.out.println("  " + BRIGHT_GREEN + "[8]" + RESET + "  Update Customer Details     " + DIM
                    + "Modify info" + RESET);

            System.out.println("\n  " + BOLD + YELLOW + "FINANCIAL OPERATIONS" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            if (level >= 3)
                System.out.println("  " + BRIGHT_YELLOW + "[9]" + RESET + "  Apply Interest to All       " + DIM
                        + "Credit interest" + RESET);
            if (level >= 3)
                System.out.println("  " + BRIGHT_YELLOW + "[20]" + RESET + " Apply Maintenance Charges   " + DIM
                        + "Deduct AMC" + RESET);
            if (level >= 3)
                System.out.println("  " + BRIGHT_YELLOW + "[21]" + RESET + " Treasury & Investments      " + DIM
                        + "RBI/Market" + RESET);
            System.out.println("  " + BRIGHT_YELLOW + "[10]" + RESET + " Approve Cheque Book         " + DIM
                    + "Process requests" + RESET);
            if (level >= 2)
                System.out.println("  " + BRIGHT_YELLOW + "[11]" + RESET + " View Pending FD/RD          " + DIM
                        + "Investment status" + RESET);

            if (isEffectiveRM || level >= 2 || hasInbox) {
                System.out.println("\n  " + BOLD + MAGENTA + "SYSTEM TOOLS" + RESET);
                System.out.println("  ─────────────────────────────────────────────────────────────");
                if (level >= 3)
                    System.out.println("  " + BRIGHT_MAGENTA + "[12]" + RESET + " System Statistics           " + DIM
                            + "Overview" + RESET);
                if (level >= 3)
                    System.out.println("  " + BRIGHT_MAGENTA + "[13]" + RESET + " Backup Data                 " + DIM
                            + "Save backup" + RESET);
                if (level >= 2)
                    System.out.println("  " + BRIGHT_MAGENTA + "[14]" + RESET + " Customer Communications     " + DIM
                            + "Send messages" + RESET);
                if (isEffectiveRM)
                    System.out.println("  " + BRIGHT_MAGENTA + "[15]" + RESET + " My Assigned Clients         " + DIM
                            + "View my portfolio" + RESET);
                if (isEffectiveRM || hasInbox)
                    System.out.println("  " + BRIGHT_MAGENTA + "[16]" + RESET + " My Staff Inbox              " + DIM
                            + "Read client messages" + RESET);
            }

            if (level >= 3) {
                System.out.println("\n  " + BOLD + MAGENTA + "STAFF MANAGEMENT" + RESET);
                System.out.println("  ─────────────────────────────────────────────────────────────");
                System.out.println("  " + BRIGHT_MAGENTA + "[98]" + RESET + " Assign RM to Customer    " + DIM
                        + "Manage portfolio" + RESET);
                System.out.println("  " + BRIGHT_MAGENTA + "[99]" + RESET + " Create New Staff         " + DIM
                        + "Add employee/manager" + RESET);
            }

            System.out.println("\n  " + RED + "[0]" + RESET + "  Logout");
            System.out.println();

            System.out.print("  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    clearScreen();
                    printSectionHeader("CUSTOMER DATABASE");
                    bank.displayAllCustomers();
                    pause();
                    break;
                case 2:
                    clearScreen();
                    printSectionHeader("ACCOUNT REGISTRY");
                    bank.displayAllAccounts();
                    pause();
                    break;
                case 3:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        clearScreen();
                        printSectionHeader("REVENUE ANALYTICS");
                        bank.displayRevenueReport();
                        pause();
                    }
                    break;
                case 4:
                    displayTransactionSummary();
                    break;
                case 5:
                    searchCustomerOrAccount();
                    break;
                case 6:
                    if (level < 2) {
                        showError("Access Denied");
                        pause();
                    } else {
                        freezeUnfreezeAccount();
                    }
                    break;
                case 7:
                    if (level < 2) {
                        showError("Access Denied");
                        pause();
                    } else {
                        closeAccountAdmin();
                    }
                    break;
                case 8:
                    updateCustomerDetails();
                    break;
                case 9:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        clearScreen();
                        printSectionHeader("APPLY INTEREST");
                        System.out.println(
                                "\n  " + YELLOW + "Processing interest for all eligible accounts..." + RESET + "\n");
                        bank.applyInterestToAll();
                        showSuccess("Interest deposited successfully!");
                        pause();
                    }
                    break;
                case 20:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        clearScreen();
                        printSectionHeader("APPLY AMC");
                        System.out.println(
                                "\n  " + YELLOW + "Applying Maintenance Charges to eligible accounts..." + RESET);
                        bank.applyMaintenanceToAll();
                        showSuccess("Maintenance Charges Processed!");
                        pause();
                    }
                    break;
                case 21:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        treasuryAndInvestmentMenu();
                    }
                    break;
                case 10:
                    approveChequeBook();
                    break;
                case 11:
                    if (level < 2) {
                        showError("Access Denied");
                        pause();
                    } else {
                        viewPendingInvestments();
                    }
                    break;
                case 12:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        displaySystemStatistics();
                    }
                    break;
                case 13:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        backupData();
                    }
                    break;
                case 14:
                    if (level < 2) {
                        showError("Access Denied");
                        pause();
                    } else {
                        customerCommunicationsMenu();
                    }
                    break;
                case 15:
                    if (!isEffectiveRM) {
                        showError("Access Denied");
                        pause();
                    } else {
                        viewAssignedClients();
                    }
                    break;
                case 16:
                    if (!isEffectiveRM && !hasInbox) {
                        showError("Access Denied");
                        pause();
                    } else {
                        viewStaffInbox();
                    }
                    break;
                case 98:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        assignRelationshipManagerAdmin();
                    }
                    break;
                case 99:
                    if (level < 3) {
                        showError("Access Denied");
                        pause();
                    } else {
                        performStaffRegistration(false);
                    }
                    break;
                case 0:
                    currentStaff = null;
                    inAdmin = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    // New Admin Functions

    private static void displayTransactionSummary() {
        clearScreen();
        printSectionHeader("TODAY'S TRANSACTION SUMMARY");

        System.out.println("\n  " + BOLD + CYAN + "Transaction Activity" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_GREEN + "Total Deposits Today:        " + RESET + "₹ 0.00");
        System.out.println("  " + BRIGHT_RED + "Total Withdrawals Today:     " + RESET + "₹ 0.00");
        System.out.println("  " + BRIGHT_CYAN + "Total Transfers Today:       " + RESET + "₹ 0.00");
        System.out.println();
        System.out.println("  " + BRIGHT_YELLOW + "New Accounts Today:          " + RESET + "0");
        System.out.println("  " + BRIGHT_MAGENTA + "Closed Accounts Today:       " + RESET + "0");
        System.out.println();
        System.out.println("  " + DIM + "Note: Real-time tracking coming soon" + RESET);

        pause();
    }

    private static void searchCustomerOrAccount() {
        clearScreen();
        printSectionHeader("SEARCH CUSTOMER/ACCOUNT");

        System.out.println("\n  " + CYAN + "Search by:" + RESET);
        System.out.println("  [1] Customer ID");
        System.out.println("  [2] Mobile Number");
        System.out.println("  [3] Account Number");
        System.out.println("  [0] Cancel");

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
        int choice = getIntInputDirect();

        switch (choice) {
            case 1:
                System.out.print("\n  Enter Customer ID: ");
                String custId = scanner.nextLine().trim();
                Customer cust = bank.getCustomer(custId);
                if (cust != null) {
                    cust.displayCustomerInfo();
                    bank.displayCustomerAccounts(custId);
                } else {
                    showError("Customer not found!");
                }
                break;
            case 2:
                System.out.print("\n  Enter Mobile Number: ");
                String mobile = scanner.nextLine().trim();
                Customer custByMobile = bank.findCustomerByMobile(mobile);
                if (custByMobile != null) {
                    custByMobile.displayCustomerInfo();
                    bank.displayCustomerAccounts(custByMobile.getCustomerId());
                } else {
                    showError("Customer not found!");
                }
                break;
            case 3:
                System.out.print("\n  Enter Account Number: ");
                String accNum = scanner.nextLine().trim();
                BankAccount acc = bank.getAccount(accNum);
                if (acc != null) {
                    System.out.println("\n  " + GREEN + "✓ Account Found" + RESET);
                    System.out.println("  Account: " + acc.getAccountNumber());
                    System.out.println("  Holder: " + acc.getAccountHolderName());
                    System.out.println("  Balance: ₹" + String.format("%,.2f", acc.getBalance()));
                    System.out.println("  Type: " + acc.getAccountType().getDisplayName());
                    System.out.println("  Status: " + (acc.isActive() ? GREEN + "Active" : RED + "Inactive") + RESET);
                } else {
                    showError("Account not found!");
                }
                break;
        }
        pause();
    }

    private static void freezeUnfreezeAccount() {
        clearScreen();
        printSectionHeader("FREEZE/UNFREEZE ACCOUNT");

        System.out.print("\n  Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount acc = bank.getAccount(accNum);

        if (acc == null) {
            showError("Account not found!");
            pause();
            return;
        }

        System.out.println("\n  Account: " + acc.getAccountNumber());
        System.out.println("  Holder: " + acc.getAccountHolderName());
        System.out.println("  Current Status: " + (acc.isFrozen() ? RED + "Frozen" : GREEN + "Active") + RESET);

        System.out.println("\n  " + CYAN + "Action:" + RESET);
        System.out.println("  [1] Freeze Account");
        System.out.println("  [2] Unfreeze Account");
        System.out.println("  [0] Cancel");

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
        int choice = getIntInputDirect();

        switch (choice) {
            case 1:
                bank.freezeAccount(accNum);
                showSuccess("Account frozen successfully!");
                break;
            case 2:
                bank.unfreezeAccount(accNum);
                showSuccess("Account unfrozen successfully!");
                break;
        }
        pause();
    }

    private static void closeAccountAdmin() {
        clearScreen();
        printSectionHeader("CLOSE ACCOUNT");

        System.out.print("\n  Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount acc = bank.getAccount(accNum);

        if (acc == null) {
            showError("Account not found!");
            pause();
            return;
        }

        System.out.println("\n  " + RED + "⚠ WARNING: Account Closure" + RESET);
        System.out.println("  Account: " + acc.getAccountNumber());
        System.out.println("  Holder: " + acc.getAccountHolderName());
        System.out.println("  Balance: ₹" + String.format("%,.2f", acc.getBalance()));

        if (acc.getBalance() > 0) {
            System.out.println("\n  " + YELLOW + "⚠ Account has balance. Please withdraw before closing." + RESET);
            pause();
            return;
        }

        System.out.print("\n  Confirm closure (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            bank.closeAccount(accNum);
            showSuccess("Account closed successfully!");
            PersistenceManager.saveBank(bank);
        }
        pause();
    }

    private static void updateCustomerDetails() {
        clearScreen();
        printSectionHeader("UPDATE CUSTOMER DETAILS");

        System.out.print("\n  Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount acc = bank.getAccount(accNum);

        if (acc == null) {
            showError("Account not found!");
            pause();
            return;
        }

        System.out.println("\n  Current Details:");
        System.out.println("  Name: " + acc.getAccountHolderName());
        System.out.println("  Mobile: " + acc.getMobileNumber());

        System.out.println("\n  " + CYAN + "Update:" + RESET);
        System.out.println("  [1] Update Name");
        System.out.println("  [2] Update Mobile");
        System.out.println("  [0] Cancel");

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
        int choice = getIntInputDirect();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                System.out.print("\n  Enter New Name: ");
                String newName = scanner.nextLine().trim();
                bank.updateAccountHolderName(accNum, newName);
                showSuccess("Name updated successfully!");
                PersistenceManager.saveBank(bank);
                break;
            case 2:
                System.out.print("\n  Enter New Mobile: ");
                String newMobile = scanner.nextLine().trim();
                bank.updateMobileNumber(accNum, newMobile);
                showSuccess("Mobile number updated successfully!");
                PersistenceManager.saveBank(bank);
                break;
        }
        pause();
    }

    private static void approveChequeBook() {
        clearScreen();
        printSectionHeader("APPROVE CHEQUE BOOK REQUESTS");

        System.out.println("\n  " + DIM + "No pending cheque book requests" + RESET);
        System.out.println("  " + DIM + "Feature coming soon..." + RESET);

        pause();
    }

    private static void viewPendingInvestments() {
        clearScreen();
        printSectionHeader("PENDING FD/RD INVESTMENTS");

        System.out.println("\n  " + DIM + "No pending investment requests" + RESET);
        System.out.println("  " + DIM + "Feature coming soon..." + RESET);

        pause();
    }

    private static void displaySystemStatistics() {
        clearScreen();
        printSectionHeader("SYSTEM STATISTICS");

        // Count statistics (simplified - would need proper tracking)
        System.out.println("\n  " + BOLD + CYAN + "System Overview" + RESET);
        System.out.println("  ═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  " + BRIGHT_CYAN + "Total Customers:             " + RESET + "Coming soon");
        System.out.println("  " + BRIGHT_GREEN + "Total Accounts:              " + RESET + "Coming soon");
        System.out.println("  " + BRIGHT_YELLOW + "Active Accounts:             " + RESET + "Coming soon");
        System.out.println("  " + BRIGHT_RED + "Frozen Accounts:             " + RESET + "Coming soon");
        System.out.println();
        System.out.println("  " + BRIGHT_GREEN + "Total Deposits:              " + RESET + "₹ Coming soon");
        System.out.println("  " + BRIGHT_CYAN + "Total FD Amount:             " + RESET + "₹ Coming soon");
        System.out.println("  " + BRIGHT_MAGENTA + "Total RD Amount:             " + RESET + "₹ Coming soon");
        System.out.println();
        System.out.println("  " + DIM + "Real-time statistics coming soon" + RESET);

        pause();
    }

    private static void backupData() {
        clearScreen();
        printSectionHeader("BACKUP DATA");

        System.out.println("\n  " + YELLOW + "Creating backup..." + RESET);
        PersistenceManager.saveBankVerbose(bank);
        System.out.println("\n  " + GREEN + "✓ Backup created successfully!" + RESET);
        System.out.println("  " + DIM + "Location: bank_data.ser" + RESET);

        pause();
    }

    // ==================== HELPER METHODS ====================

    private static AccountType selectAccountType(boolean includeCorporate) {
        System.out.println("\n  " + CYAN + "Select Account Type:" + RESET + "\n");

        AccountType[] allTypes = AccountType.values();
        // Filter out CORPORATE if not included
        AccountType[] types;
        if (includeCorporate) {
            types = allTypes;
        } else {
            // Count non-corporate types
            int count = 0;
            for (AccountType type : allTypes) {
                if (type != AccountType.CORPORATE) {
                    count++;
                }
            }
            // Create array without CORPORATE
            types = new AccountType[count];
            int index = 0;
            for (AccountType type : allTypes) {
                if (type != AccountType.CORPORATE) {
                    types[index++] = type;
                }
            }
        }

        for (int i = 0; i < types.length; i++) {
            System.out.println("  " + BRIGHT_GREEN + (i + 1) + RESET + ". " + types[i].getDisplayName() +
                    " " + DIM + "(Min: ₹" + String.format("%,.0f", types[i].getMinBalance()) + ")" + RESET);
        }

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Your choice: ");
        int choice = getIntInputDirect();

        if (choice < 1 || choice > types.length) {
            showError("Invalid selection");
            pause();
            return null;
        }

        return types[choice - 1];
    }

    private static void selectAccount() {
        if (currentCustomer.getAccountCount() == 1) {
            currentAccount = bank.getAccount(currentCustomer.getAccountNumbers().get(0));
            return;
        }

        clearScreen();
        printSectionHeader("SELECT ACCOUNT");

        List<String> accs = currentCustomer.getAccountNumbers();
        for (int i = 0; i < accs.size(); i++) {
            BankAccount acc = bank.getAccount(accs.get(i));
            System.out.println("  " + BRIGHT_CYAN + (i + 1) + RESET + ". " + accs.get(i) +
                    " " + DIM + "(Balance: ₹" + String.format("%,.2f", acc.getBalance()) + ")" + RESET);
        }

        System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select account: ");
        int idx = getIntInputDirect() - 1;

        if (idx >= 0 && idx < accs.size()) {
            currentAccount = bank.getAccount(accs.get(idx));
        } else {
            showError("Invalid selection. Using first account.");
            currentAccount = bank.getAccount(accs.get(0));
            pause();
        }
    }

    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("  " + RED + "Invalid input. Try again:" + RESET + " ");
        }
        int i = scanner.nextInt();
        scanner.nextLine();
        return i;
    }

    private static int getIntInputDirect() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("  " + RED + "Invalid input. Try again:" + RESET + " ");
        }
        int i = scanner.nextInt();
        scanner.nextLine();
        return i;
    }

    private static double getDoubleInputDirect() {
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("  " + RED + "Invalid input. Try again:" + RESET + " ");
        }
        double d = scanner.nextDouble();
        scanner.nextLine();
        return d;
    }

    private static void pause() {
        System.out.print("\n  " + DIM + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }

    private static void generateDetailedBankStatement() {
        clearScreen();
        printSectionHeader("GENERATE DETAILED BANK STATEMENT");

        // Get customer address from the Customer object
        String address = currentCustomer.getAddress();
        if (address == null || address.trim().isEmpty()) {
            address = "Address on file";
        }

        currentAccount.generateDetailedStatement(address);
        pause();
    }

    // ==================== MAILBOX SYSTEM ====================

    private static void mailboxMenu() {
        boolean inMailbox = true;
        while (inMailbox) {
            clearScreen();
            currentCustomer.displayMailbox();

            System.out.println("  Options:");
            System.out.println("  [1] Read Message");
            System.out.println("  [2] Contact Support");
            System.out.println("  [0] Back to Dashboard");

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    readMessage();
                    break;
                case 2:
                    sendMessageToSupport();
                    break;
                case 0:
                    inMailbox = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    private static void sendMessageToSupport() {
        clearScreen();
        printSectionHeader("CONTACT SUPPORT");

        // Check assigned RM
        Staff rm = null;
        if (currentCustomer.getRelationshipManagerId() != null) {
            rm = bank.getStaffById(currentCustomer.getRelationshipManagerId());
        }

        int destination = 1; // Default to General Support
        if (rm != null) {
            System.out.println("  Where would you like to send your message?");
            System.out.println("  [1] General Customer Support");
            System.out.println("  [2] Relationship Manager (" + rm.getName() + ")");
            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
            int choice = getIntInputDirect();
            if (choice == 2)
                destination = 2;
            else if (choice != 1) {
                showError("Invalid option");
                pause();
                return;
            }
        }

        System.out.println("\n  " + CYAN + "Compose Message:" + RESET);

        System.out.print("  Subject: ");
        String subject = scanner.nextLine().trim();
        if (subject.isEmpty()) {
            showError("Subject cannot be empty");
            pause();
            return;
        }

        System.out.print("  Message: ");
        String content = scanner.nextLine().trim();
        if (content.isEmpty()) {
            showError("Message cannot be empty");
            pause();
            return;
        }

        String msgId = "MSG" + System.currentTimeMillis();
        String titlePrefix = (destination == 2) ? "[RM] " : "[Support] ";

        Message msg = new Message(msgId,
                titlePrefix + subject + " - " + currentCustomer.getCustomerName(),
                "From: " + currentCustomer.getCustomerName() + " (" + currentCustomer.getCustomerId() + ")\n\n"
                        + content,
                "IMPORTANT", "HIGH");

        if (destination == 2 && rm != null) {
            rm.receiveMessage(msg); // Send to RM personal inbox
            showSuccess("Message sent to Relationship Manager!");
        } else {
            bank.addSupportMessage(msg); // Send to General Support
            showSuccess("Message sent to support team!");
        }
        PersistenceManager.saveBank(bank);
        pause();
    }

    private static void readMessage() {
        System.out.print("\n  Enter Message Number: ");
        int msgNum = getIntInputDirect();

        if (msgNum < 1 || msgNum > currentCustomer.getMailbox().size()) {
            showError("Invalid message number");
            pause();
            return;
        }

        Message msg = currentCustomer.getMailbox().get(msgNum - 1);
        msg.markAsRead();

        clearScreen();
        msg.displayMessage();

        System.out.println("\n  Press Enter to return...");
        scanner.nextLine();
    }

    // ==================== ADMIN COMMUNICATIONS ====================

    private static void customerCommunicationsMenu() {
        boolean inComm = true;
        while (inComm) {
            clearScreen();
            printSectionHeader("CUSTOMER COMMUNICATIONS");

            System.out.println("\n  " + BOLD + CYAN + "Communication Hub" + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────");
            System.out.println("  " + BRIGHT_CYAN + "[1]" + RESET + "  Send Message to Customer     " + DIM
                    + "Direct message" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "[2]" + RESET + "  Broadcast to All             " + DIM
                    + "Mass alert/promo" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "[3]" + RESET + "  View Support Inbox           " + DIM
                    + "Read customer msgs" + RESET);
            System.out.println("  " + RED + "[0]" + RESET + "  Back to Admin Menu");

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    sendMessageToCustomer();
                    break;
                case 2:
                    broadcastMessage();
                    break;
                case 3:
                    viewSupportInbox();
                    break;
                case 0:
                    inComm = false;
                    break;
                default:
                    showError("Invalid option");
                    pause();
            }
        }
    }

    private static void sendMessageToCustomer() {
        clearScreen();
        printSectionHeader("SEND DIRECT MESSAGE");

        System.out.print("\n  " + CYAN + "Enter Customer ID:" + RESET + " ");
        String custId = scanner.nextLine().trim();

        Customer cust = bank.findCustomer(custId);
        if (cust == null) {
            showError("Customer not found!");
            pause();
            return;
        }

        System.out.println("  Sending to: " + BOLD + cust.getCustomerName() + RESET);

        System.out.print("\n  Subject: ");
        String subject = scanner.nextLine().trim();

        System.out.print("  Message: ");
        String content = scanner.nextLine().trim();

        System.out.println("\n  Category (1=INFO, 2=ALERT, 3=OFFER, 4=IMPORTANT): ");
        System.out.print("  Select: ");
        int catChoice = getIntInputDirect();
        String category = "INFO";
        if (catChoice == 2)
            category = "ALERT";
        else if (catChoice == 3)
            category = "OFFER";
        else if (catChoice == 4)
            category = "IMPORTANT";

        System.out.println("\n  Priority (1=LOW, 2=MEDIUM, 3=HIGH): ");
        System.out.print("  Select: ");
        int priChoice = getIntInputDirect();
        String priority = "LOW";
        if (priChoice == 2)
            priority = "MEDIUM";
        else if (priChoice == 3)
            priority = "HIGH";

        String msgId = "MSG" + System.currentTimeMillis();
        Message msg = new Message(msgId, subject, content, category, priority);

        cust.addMessage(msg);
        PersistenceManager.saveBank(bank);
        showSuccess("Message sent successfully!");
        pause();
    }

    private static void broadcastMessage() {
        clearScreen();
        printSectionHeader("BROADCAST MESSAGE");

        System.out.println("\n  " + YELLOW + "Warning: This message will be sent to ALL customers." + RESET);

        System.out.print("\n  Subject: ");
        String subject = scanner.nextLine().trim();

        System.out.print("  Message: ");
        String content = scanner.nextLine().trim();

        System.out.println("\n  Category (1=INFO, 2=ALERT, 3=OFFER, 4=IMPORTANT, 5=PROMOTIONAL): ");
        System.out.print("  Select: ");
        int catChoice = getIntInputDirect();
        String category = "INFO";
        if (catChoice == 2)
            category = "ALERT";
        else if (catChoice == 3)
            category = "OFFER";
        else if (catChoice == 4)
            category = "IMPORTANT";
        else if (catChoice == 5)
            category = "PROMOTIONAL";

        System.out.println("\n  Priority (1=LOW, 2=MEDIUM, 3=HIGH): ");
        System.out.print("  Select: ");
        int priChoice = getIntInputDirect();
        String priority = "LOW";
        if (priChoice == 2)
            priority = "MEDIUM";
        else if (priChoice == 3)
            priority = "HIGH";

        System.out.print("\n  " + RED + "Confirm Broadcast (Y/N): " + RESET);
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            int count = 0;
            // Need a way to iterate all customers. Bank.java has customers map.
            // Ideally ensure bank.getAllCustomers() exists or similar.
            // Assuming we added displayAllCustomers iteration logic there.
            // But we can't access private map.
            // Let's check Bank.java methods quickly.
            // Wait, I can't check now mid-tool.
            // I'll assume I need to add a method to Bank to broadcast or get list.
            // For now, I'll use a placeholder loop if I can't access map.
            // ACTUALLY, I'll add a method to Bank.java first if needed.
            // But let's check if I can just assume it works or if I need to modify
            // Bank.java too.
            // Bank.java has `customers` map but no public accessor for values collection
            // except `displayAllCustomers`.
            // I should add `broadcastToAll(Message)` to Bank.java.

            // To be safe in this edit, I will call bank.broadcastMessage(msg) which I will
            // implement next.
            bank.broadcastMessage(
                    new Message("MSG" + System.currentTimeMillis(), subject, content, category, priority));
            PersistenceManager.saveBank(bank);
            showSuccess("Broadcast initiated!");
        }
        pause();
    }

    private static void viewSupportInbox() {
        clearScreen();
        printSectionHeader("SUPPORT INBOX");

        java.util.List<Message> inbox = bank.getSupportInbox();
        if (inbox.isEmpty()) {
            System.out.println("\n  " + DIM + "No messages from customers." + RESET);
        } else {
            System.out.println("\n  " + BOLD + "Received Messages:" + RESET);
            System.out.println("  ═══════════════════════════════════════════════════════════════════════");

            int index = 1;
            for (Message msg : inbox) {
                System.out.println("  [" + index++ + "] " + msg.getShortDisplay());
            }
            System.out.println("  ═══════════════════════════════════════════════════════════════════════");

            System.out.println("\n  [#] Read Message  [0] Back");
            System.out.print("  Select number: ");
            int choice = getIntInputDirect();

            if (choice > 0 && choice <= inbox.size()) {
                Message m = inbox.get(choice - 1);
                clearScreen();
                m.displayMessage();
                System.out.println("\n  Press Enter to continue...");
                scanner.nextLine();
            } else if (choice != 0) {
                showError("Invalid message number.");
            }
        }
        pause();
    }

    private static void treasuryAndInvestmentMenu() {
        while (true) {
            clearScreen();
            printSectionHeader("TREASURY & INVESTMENTS");
            double tBal = 0;
            if (bank.getTreasuryAccount() != null)
                tBal = bank.getTreasuryAccount().getBalance();

            System.out.println("  " + BOLD + "Treasury Balance: " + (tBal >= 0 ? GREEN : RED) + "₹ "
                    + String.format("%,.2f", tBal) + RESET);
            System.out.println("  ─────────────────────────────────────────────────────────────\n");

            System.out.println("  " + BRIGHT_CYAN + "[1]" + RESET + " Borrow from RBI (2% Interest)");
            System.out.println("  " + BRIGHT_CYAN + "[2]" + RESET + " Repay RBI Loan");
            System.out.println("  " + BRIGHT_CYAN + "[3]" + RESET + " Pay RBI Interest");
            System.out.println("  " + BRIGHT_GREEN + "[4]" + RESET + " Invest Deposits (Avg 18% Returns)");
            System.out.println("  " + BRIGHT_GREEN + "[5]" + RESET + " Realize Investment Returns");
            System.out.println("\n  " + RED + "[0]" + RESET + " Back");

            System.out.print("\n  " + BRIGHT_BLUE + "→" + RESET + " Select option: ");
            int choice = getIntInputDirect();

            switch (choice) {
                case 1:
                    System.out.print("\n  Enter Amount to Borrow: ₹");
                    double amount = 0;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (Exception e) {
                    }
                    bank.borrowFromRBI(amount);
                    PersistenceManager.saveBank(bank);
                    pause();
                    break;
                case 2:
                    System.out.print("\n  Enter Amount to Repay: ₹");
                    double repay = 0;
                    try {
                        repay = Double.parseDouble(scanner.nextLine());
                    } catch (Exception e) {
                    }
                    bank.repayRBI(repay);
                    PersistenceManager.saveBank(bank);
                    pause();
                    break;
                case 3:
                    bank.payRBIInterest();
                    PersistenceManager.saveBank(bank);
                    pause();
                    break;
                case 4:
                    System.out.print("\n  Enter Amount to Invest: ₹");
                    double inv = 0;
                    try {
                        inv = Double.parseDouble(scanner.nextLine());
                    } catch (Exception e) {
                    }
                    bank.investDeposits(inv);
                    PersistenceManager.saveBank(bank);
                    pause();
                    break;
                case 5:
                    bank.realizeInvestmentReturns();
                    PersistenceManager.saveBank(bank);
                    pause();
                    break;
                case 0:
                    return;
                default:
                    showError("Invalid Option");
                    pause();
            }
        }
    }
}
