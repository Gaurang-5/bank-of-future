import java.io.*;

public class PersistenceManager {
    private static final String DATA_FILE = "bank_data.ser";

    // Silent save - used for auto-save after transactions (no output for security)
    public static void saveBank(Bank bank) {
        saveBank(bank, false);
    }

    // Verbose save - used during application exit (shows confirmation)
    public static void saveBankVerbose(Bank bank) {
        saveBank(bank, true);
    }

    private static void saveBank(Bank bank, boolean verbose) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(bank);
            if (verbose) {
                System.out.println("✓ Data saved successfully");
            }
        } catch (IOException e) {
            if (verbose) {
                System.out.println("✗ Error saving data: " + e.getMessage());
                e.printStackTrace();
            }
            // Silent failure for auto-save - log to error stream only
            System.err.println("Save error: " + e.getMessage());
        }
    }

    public static Bank loadBank() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("Starting with a new Bank system.");
            return new Bank();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Bank bank = (Bank) ois.readObject();
            System.out.println("✓ Data loaded successfully");
            return bank;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("✗ Error loading data: " + e.getMessage());
            System.out.println("Starting with a new Bank system.");
            return new Bank();
        }
    }
}
