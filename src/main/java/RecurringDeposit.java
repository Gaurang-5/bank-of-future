import java.io.Serializable;
import java.time.LocalDate;

public class RecurringDeposit implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rdNumber;
    private String accountNumber;
    private double monthlyInstallment;
    private double interestRate;
    private int tenureMonths;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private int installmentsPaid;
    private double totalDeposited;
    private double maturityAmount;
    private boolean isActive;

    public RecurringDeposit(String rdNumber, String accountNumber, double monthlyInstallment,
            int tenureMonths, double interestRate) {
        this.rdNumber = rdNumber;
        this.accountNumber = accountNumber;
        this.monthlyInstallment = monthlyInstallment;
        this.tenureMonths = tenureMonths;
        this.interestRate = interestRate;
        this.startDate = LocalDate.now();
        this.maturityDate = startDate.plusMonths(tenureMonths);
        this.installmentsPaid = 0;
        this.totalDeposited = 0;
        this.isActive = true;
        this.maturityAmount = calculateMaturityAmount();
    }

    private double calculateMaturityAmount() {
        // RD Maturity = P * n * (n+1) * (2n+1) / 6 * r / 12
        // Where P = monthly installment, n = number of months, r = rate of interest
        double n = tenureMonths;
        double r = interestRate / 100.0;
        double principal = monthlyInstallment * n;
        double interest = monthlyInstallment * n * (n + 1) * (2 * n + 1) / 6 * r / 12;
        return principal + interest;
    }

    public boolean payInstallment() {
        if (isActive && installmentsPaid < tenureMonths) {
            installmentsPaid++;
            totalDeposited += monthlyInstallment;
            return true;
        }
        return false;
    }

    public boolean checkMaturity() {
        return installmentsPaid >= tenureMonths &&
                (LocalDate.now().isAfter(maturityDate) || LocalDate.now().isEqual(maturityDate));
    }

    public void closeRD() {
        isActive = false;
        // Recalculate maturity based on actual installments paid
        if (installmentsPaid < tenureMonths) {
            double n = installmentsPaid;
            double r = (interestRate - 1.0) / 100.0; // Penalty
            double principal = monthlyInstallment * n;
            double interest = monthlyInstallment * n * (n + 1) * (2 * n + 1) / 6 * r / 12;
            maturityAmount = principal + interest;
        }
    }

    // Getters
    public String getRdNumber() {
        return rdNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public int getInstallmentsPaid() {
        return installmentsPaid;
    }

    public double getTotalDeposited() {
        return totalDeposited;
    }

    public double getMaturityAmount() {
        return maturityAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void displayRDDetails() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_GREEN = "\u001B[92m";
        String GREEN = "\u001B[32m";
        String RED = "\u001B[31m";

        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + CYAN + "RD Number:          " + RESET + BOLD + rdNumber + RESET);
        System.out
                .println(DIM + "  │ " + RESET + "Monthly Installment: ₹" + String.format("%,.2f", monthlyInstallment));
        System.out.println(DIM + "  │ " + RESET + "Interest Rate:       " + interestRate + "% p.a.");
        System.out.println(DIM + "  │ " + RESET + "Tenure:              " + tenureMonths + " months");
        System.out.println(DIM + "  │ " + RESET + "Installments Paid:   " + installmentsPaid + "/" + tenureMonths);
        System.out.println(DIM + "  │ " + RESET + "Total Deposited:     ₹" + String.format("%,.2f", totalDeposited));
        System.out.println(DIM + "  │ " + RESET + "Maturity Date:       " + maturityDate);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_GREEN + "Maturity Amount:     " + RESET + BOLD + "₹"
                + String.format("%,.2f", maturityAmount) + RESET);
        System.out.println(DIM + "  │ " + RESET + "Status:              "
                + (isActive ? GREEN + "Active" : RED + "Closed") + RESET);
    }
}
