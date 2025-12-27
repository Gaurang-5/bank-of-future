public enum BusinessType {
    INDIVIDUAL("Individual"),
    PROPRIETORSHIP("Proprietorship"),
    PARTNERSHIP("Partnership"),
    PRIVATE_LIMITED("Private Limited Company"),
    PUBLIC_LIMITED("Public Limited Company"),
    LLP("Limited Liability Partnership");

    private final String displayName;

    BusinessType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
