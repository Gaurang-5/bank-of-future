public enum StaffRole {
    BANK_EMPLOYEE("Bank Employee", 1),
    ASSISTANT_BANK_MANAGER("Assistant Bank Manager", 2),
    BANK_MANAGER("Bank Manager", 3);

    private final String displayName;
    private final int level;

    StaffRole(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public boolean canCreate(StaffRole other) {
        return this.level > other.level;
    }
}
