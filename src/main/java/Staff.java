import java.io.Serializable;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    private String staffId;
    private String name;
    private String username;
    private String password;
    private StaffRole role;
    private java.util.List<Message> inbox;

    public Staff(String staffId, String name, String username, String password, StaffRole role) {
        this.staffId = staffId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.inbox = new java.util.ArrayList<>();
    }

    public void receiveMessage(Message message) {
        if (this.inbox == null)
            this.inbox = new java.util.ArrayList<>();
        this.inbox.add(message);
    }

    public java.util.List<Message> getInbox() {
        if (this.inbox == null)
            this.inbox = new java.util.ArrayList<>();
        return inbox;
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Getters
    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public StaffRole getRole() {
        return role;
    }

    public String getTypeDisplay() {
        return role.getDisplayName();
    }
}
