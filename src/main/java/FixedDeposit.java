import java.io.Serializable;
import java.time.LocalDate;

public class FixedDeposit implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fdNumber;
    private String accountNumber;
    private double principalAmount;
    private double interestRate;
    private int tenureMonths;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private double maturityAmount;
    private boolean isActive;
    private boolean isMatured;

    public FixedDeposit(String fdNumber, String accountNumber, double principalAmount,
            int tenureMonths, double interestRate) {
        this.fdNumber = fdNumber;
        this.accountNumber = accountNumber;
        this.principalAmount = principalAmount;
        this.tenureMonths = tenureMonths;
        this.interestRate = interestRate;
        this.startDate = LocalDate.now();
        this.maturityDate = startDate.plusMonths(tenureMonths);
        this.maturityAmount = calculateMaturityAmount();
        this.isActive = true;
        this.isMatured = false;
    }

    private double calculateMaturityAmount() {
        // Compound interest: A = P(1 + r/n)^(nt)
        // For quarterly compounding
        double rate = interestRate / 100.0;
        double n = 4; // Quarterly compounding
        double t = tenureMonths / 12.0;
        return principalAmount * Math.pow(1 + rate / n, n * t);
    }

    public boolean checkMaturity() {
        if (!isMatured && LocalDate.now().isAfter(maturityDate) || LocalDate.now().isEqual(maturityDate)) {
            isMatured = true;
            return true;
        }
        return false;
    }

    public void prematureWithdrawal() {
        if (isActive && !isMatured) {
            // Penalty: 1% less interest
            double penaltyRate = Math.max(0, interestRate - 1.0);
            double rate = penaltyRate / 100.0;
            double n = 4;
            long monthsElapsed = java.time.temporal.ChronoUnit.MONTHS.between(startDate, LocalDate.now());
            double t = monthsElapsed / 12.0;
            maturityAmount = principalAmount * Math.pow(1 + rate / n, n * t);
            isActive = false;
        }
    }

    public void closeFD() {
        isActive = false;
    }

    // Getters
    public String getFdNumber() {
        return fdNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getPrincipalAmount() {
        return principalAmount;
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

    public double getMaturityAmount() {
        return maturityAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isMatured() {
        return isMatured;
    }

    public void displayFDDetails() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String DIM = "\u001B[2m";
        String CYAN = "\u001B[36m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";
        String GREEN = "\u001B[32m";
        String RED = "\u001B[31m";

        System.out.println(DIM + "  ├─────────────────────────────────────────────────────────────┤" + RESET);
        System.out.println(DIM + "  │ " + RESET + CYAN + "FD Number:       " + RESET + BOLD + fdNumber + RESET);
        System.out.println(DIM + "  │ " + RESET + "Principal:       ₹" + String.format("%,.2f", principalAmount));
        System.out.println(DIM + "  │ " + RESET + "Interest Rate:   " + interestRate + "% p.a.");
        System.out.println(DIM + "  │ " + RESET + "Tenure:          " + tenureMonths + " months");
        System.out.println(DIM + "  │ " + RESET + "Start Date:      " + startDate);
        System.out.println(DIM + "  │ " + RESET + "Maturity Date:   " + maturityDate);
        System.out.println(DIM + "  │ " + RESET + BRIGHT_GREEN + "Maturity Amount: " + RESET + BOLD + "₹"
                + String.format("%,.2f", maturityAmount) + RESET);
        System.out.println(DIM + "  │ " + RESET + "Status:          " +
                (isMatured ? BRIGHT_YELLOW + "Matured" : (isActive ? GREEN + "Active" : RED + "Closed")) + RESET);
    }
}
