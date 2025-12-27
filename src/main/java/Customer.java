import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Customer implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String customerId;
    private String customerName;
    private String mobileNumber;
    private String email;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String panNumber;
    private String aadharNumber;
    private ArrayList<String> accountNumbers;
    private SimpleDateFormat dateFormat;
    private String registrationDate;
    private String password;

    // Security Features
    private String transactionPin;
    private int failedLoginAttempts;
    private boolean isLocked;
    private LocalDate lastLoginDate;

    // Account Management
    private String nomineeName;
    private String nomineeRelation;
    private LocalDate nomineeDoB;

    // For Corporate Accounts
    private BusinessType businessType;
    private String companyName;
    private String gstNumber;
    private String cinNumber;

    // Mailbox for messages
    private ArrayList<Message> mailbox;

    // Relationship Manager (for Wealth/Priority customers)
    private String relationshipManagerId;

    // Constructor for Individual/Proprietorship
    public Customer(String customerId, String customerName, String mobileNumber,
            String email, LocalDate dateOfBirth, String address, String city,
            String state, String pincode, String panNumber, String aadharNumber) {
        this(customerId, customerName, mobileNumber, email, dateOfBirth, address, city,
                state, pincode, panNumber, aadharNumber, BusinessType.INDIVIDUAL, null, null, null);
    }

    // Constructor for Corporate Accounts
    public Customer(String customerId, String customerName, String mobileNumber,
            String email, LocalDate dateOfBirth, String address, String city,
            String state, String pincode, String panNumber, String aadharNumber,
            BusinessType businessType, String companyName, String gstNumber, String cinNumber) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.panNumber = panNumber;
        this.aadharNumber = aadharNumber;
        this.businessType = businessType;
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.cinNumber = cinNumber;
        this.accountNumbers = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.registrationDate = dateFormat.format(new Date());
        this.mailbox = new ArrayList<>(); // Initialize mailbox

        // Security initialization
        this.failedLoginAttempts = 0;
        this.isLocked = false;
        this.transactionPin = null;

        // Generate Default Password
        String namePart = customerName.replaceAll("\\s+", "");
        if (namePart.length() > 4)
            namePart = namePart.substring(0, 4);

        String mobilePart = mobileNumber;
        if (mobilePart.length() > 4)
            mobilePart = mobilePart.substring(mobilePart.length() - 4);

        this.password = namePart + mobilePart;
    }

    public boolean validatePassword(String inputPassword) {
        if (isLocked) {
            return false;
        }

        if (this.password.equals(inputPassword)) {
            failedLoginAttempts = 0;
            lastLoginDate = LocalDate.now();
            return true;
        } else {
            failedLoginAttempts++;
            if (failedLoginAttempts >= 3) {
                isLocked = true;
            }
            return false;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String oldPassword, String newPassword) {
        if (this.password.equals(oldPassword)) {
            this.password = newPassword;
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }

    public void setPasswordDirect(String password) {
        this.password = password;
    }

    public boolean validateTransactionPin(String pin) {
        return transactionPin != null && transactionPin.equals(pin);
    }

    public void setTransactionPin(String pin) {
        if (pin != null && pin.matches("\\d{4}")) {
            this.transactionPin = pin;
        } else {
            throw new IllegalArgumentException("PIN must be 4 digits");
        }
    }

    public boolean hasTransactionPin() {
        return transactionPin != null;
    }

    public void unlockAccount() {
        this.isLocked = false;
        this.failedLoginAttempts = 0;
    }

    public void setNominee(String name, String relation, LocalDate dob) {
        this.nomineeName = name;
        this.nomineeRelation = relation;
        this.nomineeDoB = dob;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public ArrayList<String> getAccountNumbers() {
        return accountNumbers;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setCustomerName(String newName) {
        this.customerName = newName;
    }

    public void setMobileNumber(String newMobile) {
        this.mobileNumber = newMobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Security getters
    public boolean isLocked() {
        return isLocked;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    // Nominee getters
    public String getNomineeName() {
        return nomineeName;
    }

    public String getNomineeRelation() {
        return nomineeRelation;
    }

    public LocalDate getNomineeDoB() {
        return nomineeDoB;
    }

    public boolean hasNominee() {
        return nomineeName != null;
    }

    // Corporate getters
    public BusinessType getBusinessType() {
        return businessType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public String getCinNumber() {
        return cinNumber;
    }

    public boolean isCorporate() {
        return businessType != null && businessType != BusinessType.INDIVIDUAL;
    }

    public void addAccount(String accountNumber) {
        accountNumbers.add(accountNumber);
    }

    public void removeAccount(String accountNumber) {
        accountNumbers.remove(accountNumber);
    }

    public int getAccountCount() {
        return accountNumbers.size();
    }

    public void displayCustomerInfo() {
        System.out.println("\n=== Customer Information ===");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Customer Name: " + customerName);
        System.out.println("Mobile Number: " + mobileNumber);
        System.out.println("Email: " + email);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Address: " + address + ", " + city + ", " + state + " - " + pincode);
        System.out.println("PAN: " + panNumber);
        System.out.println("Aadhar: " + maskAadhar(aadharNumber));
        System.out.println("Registration Date: " + registrationDate);
        System.out.println("Total Accounts: " + accountNumbers.size());
        if (!accountNumbers.isEmpty()) {
            System.out.println("Account Numbers:");
            for (String accNum : accountNumbers) {
                System.out.println("  - " + accNum);
            }
        }
        System.out.println("===========================\n");
    }

    private String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() < 4)
            return "****";
        return "XXXX-XXXX-" + aadhar.substring(aadhar.length() - 4);
    }

    // ==================== MAILBOX METHODS ====================

    public void addMessage(Message message) {
        if (mailbox == null) {
            mailbox = new ArrayList<>();
        }
        mailbox.add(message);
    }

    public ArrayList<Message> getMailbox() {
        if (mailbox == null) {
            mailbox = new ArrayList<>();
        }
        return mailbox;
    }

    public int getUnreadMessageCount() {
        if (mailbox == null)
            return 0;
        return (int) mailbox.stream().filter(m -> !m.isRead()).count();
    }

    public int getTotalMessageCount() {
        if (mailbox == null)
            return 0;
        return mailbox.size();
    }

    public Message getMessage(String messageId) {
        if (mailbox == null)
            return null;
        return mailbox.stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst()
                .orElse(null);
    }

    public void markMessageAsRead(String messageId) {
        Message msg = getMessage(messageId);
        if (msg != null) {
            msg.markAsRead();
        }
    }

    public void displayMailbox() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String BRIGHT_GREEN = "\u001B[92m";
        String DIM = "\u001B[2m";

        if (mailbox == null || mailbox.isEmpty()) {
            System.out.println("\n  " + DIM + "No messages in your mailbox" + RESET);
            return;
        }

        System.out.println(
                "\n  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "                    YOUR MAILBOX                               " + RESET);
        System.out.println(
                "  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);

        int unread = getUnreadMessageCount();
        System.out.println("\n  " + BRIGHT_GREEN + "●" + RESET + " " + unread + " unread  │  " +
                "Total: " + mailbox.size() + " messages\n");

        for (int i = 0; i < mailbox.size(); i++) {
            Message msg = mailbox.get(i);
            System.out.println("  [" + (i + 1) + "] " + msg.getShortDisplay());
        }

        System.out.println(
                "\n  " + BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
    }

    public String getRelationshipManagerId() {
        return relationshipManagerId;
    }

    public void setRelationshipManagerId(String relationshipManagerId) {
        this.relationshipManagerId = relationshipManagerId;
    }
}
