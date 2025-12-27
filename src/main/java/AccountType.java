import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public enum AccountType {
    BASIC("Basic Account", 1000.0, 200.0, 50.0, 20.0, 0.0, 0.0,
            Collections.singletonList("Checkbook")),
    PRIORITY("Priority Account", 25000.0, 100.0, 10.0, 5.0, 0.0, 0.0,
            Arrays.asList("Lounge Access (2/yr)", "Priority Support", "Checkbook")),
    WEALTH("Wealth Account", 100000.0, 0.0, 0.0, 0.0, 5000.0, 0.0,
            Arrays.asList("Unlimited Lounge Access", "Golf Club Membership", "Spa Vouchers", "Dedicated RM")),
    CORPORATE("Corporate Account", 500000.0, 0.0, 0.0, 0.0, 10000.0, 1000000.0,
            Arrays.asList("Bulk Payments", "Payroll Services", "OD Facility", "Tax Advisory")),
    TREASURY("Bank Treasury", 0.0, 0.0, 0.0, 0.0, 0.0, 1000000000.0,
            Collections.singletonList("Internal Bank Operations"));

    private final String displayName;
    private final double minimumBalance;
    private final double maintenanceCharge;
    private final double transferFee;
    private final double withdrawalFee;
    private final double joiningFee;
    private final double overdraftLimit;
    private final List<String> benefits;

    AccountType(String displayName, double minimumBalance, double maintenanceCharge,
            double transferFee, double withdrawalFee, double joiningFee, double overdraftLimit, List<String> benefits) {
        this.displayName = displayName;
        this.minimumBalance = minimumBalance;
        this.maintenanceCharge = maintenanceCharge;
        this.transferFee = transferFee;
        this.withdrawalFee = withdrawalFee;
        this.joiningFee = joiningFee;
        this.overdraftLimit = overdraftLimit;
        this.benefits = benefits;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double getMinBalance() {
        return minimumBalance;
    }

    public double getMaintenanceCharge() {
        return maintenanceCharge;
    }

    public double getTransferFee() {
        return transferFee;
    }

    public double getWithdrawalFee() {
        return withdrawalFee;
    }

    public double getJoiningFee() {
        return joiningFee;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public static void displayAccountTypes() {
        System.out.println("\n  ACCOUNT TYPES & BENEFITS");
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();

        for (AccountType type : AccountType.values()) {
            System.out.println("  " + String.format("%-20s", type.displayName) + " | Min: ₹"
                    + String.format("%-9s", type.minimumBalance) +
                    " | Fee: ₹" + String.format("%-5s", type.joiningFee));
            System.out.println("  Benefits: " + String.join(", ", type.benefits));
            System.out.println("  ───────────────────────────────────────────────────────────────────────");
        }
        System.out.println();
    }

    public static void displayIndividualAccountTypes() {
        System.out.println("\n  ACCOUNT TYPES & BENEFITS");
        System.out.println("  ═══════════════════════════════════════════════════════════════════════");
        System.out.println();

        for (AccountType type : AccountType.values()) {
            // Skip CORPORATE account type for individual accounts
            if (type == AccountType.CORPORATE) {
                continue;
            }
            System.out.println("  " + String.format("%-20s", type.displayName) + " | Min: ₹"
                    + String.format("%-9s", type.minimumBalance) +
                    " | Fee: ₹" + String.format("%-5s", type.joiningFee));
            System.out.println("  Benefits: " + String.join(", ", type.benefits));
            System.out.println("  ───────────────────────────────────────────────────────────────────────");
        }
        System.out.println();
    }
}
