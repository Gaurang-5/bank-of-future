import java.io.Serializable;

public class Vendor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vendorId;
    private String vendorName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String category;
    private String contactPerson;
    private String mobile;
    private String email;
    private String gstNumber;
    private double totalPaid;
    private int transactionCount;

    public Vendor(String vendorId, String vendorName, String accountNumber, String ifscCode,
            String bankName, String category, String contactPerson, String mobile,
            String email, String gstNumber) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.bankName = bankName;
        this.category = category;
        this.contactPerson = contactPerson;
        this.mobile = mobile;
        this.email = email;
        this.gstNumber = gstNumber;
        this.totalPaid = 0.0;
        this.transactionCount = 0;
    }

    public void recordPayment(double amount) {
        this.totalPaid += amount;
        this.transactionCount++;
    }

    // Getters
    public String getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCategory() {
        return category;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    // Setters
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public void displayVendorInfo() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";

        System.out.println(
                "\n  " + BOLD + BRIGHT_CYAN + "┌─ Vendor Details ─────────────────────────────────────────┐" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + BOLD + "ID:" + RESET + " " + vendorId +
                "  │  " + BOLD + "Name:" + RESET + " " + vendorName);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + BOLD + "Category:" + RESET + " " +
                (category != null ? category : "General"));

        System.out.println("  " + BRIGHT_CYAN + "├─ Bank Details" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Account: " + BRIGHT_GREEN + accountNumber + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " IFSC:    " + BRIGHT_GREEN + ifscCode + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Bank:    " + bankName);

        System.out.println("  " + BRIGHT_CYAN + "├─ Contact Details" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Person:  " + contactPerson);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Mobile:  " + mobile);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Email:   " + email);

        if (gstNumber != null && !gstNumber.isEmpty()) {
            System.out.println("  " + BRIGHT_CYAN + "├─ Tax Details" + RESET);
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " GST:     " + gstNumber);
        }

        System.out.println("  " + BRIGHT_CYAN + "├─ Payment Statistics" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Total Paid:    " + BRIGHT_YELLOW + "₹ " +
                String.format("%,.2f", totalPaid) + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Transactions:  " + transactionCount);

        System.out.println("  " + BRIGHT_CYAN + "└──────────────────────────────────────────────────────────┘" + RESET);
    }
}
