import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String subject;
    private String content;
    private String category; // OFFER, ALERT, INFO, IMPORTANT, PROMOTIONAL
    private Date sentDate;
    private boolean isRead;
    private String priority; // HIGH, MEDIUM, LOW

    public Message(String messageId, String subject, String content, String category, String priority) {
        this.messageId = messageId;
        this.subject = subject;
        this.content = content;
        this.category = category;
        this.priority = priority;
        this.sentDate = new Date();
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    // Getters
    public String getMessageId() {
        return messageId;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getPriority() {
        return priority;
    }

    public void displayMessage() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String BRIGHT_CYAN = "\u001B[96m";
        String BRIGHT_GREEN = "\u001B[92m";
        String BRIGHT_YELLOW = "\u001B[93m";
        String BRIGHT_RED = "\u001B[91m";
        String DIM = "\u001B[2m";
        String WHITE = "\u001B[37m";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");

        // Category color
        String categoryColor = CYAN;
        String categoryIcon = "📧";
        switch (category.toUpperCase()) {
            case "OFFER":
                categoryColor = BRIGHT_GREEN;
                categoryIcon = "🎁";
                break;
            case "ALERT":
                categoryColor = BRIGHT_RED;
                categoryIcon = "⚠️";
                break;
            case "IMPORTANT":
                categoryColor = BRIGHT_YELLOW;
                categoryIcon = "⭐";
                break;
            case "PROMOTIONAL":
                categoryColor = BRIGHT_CYAN;
                categoryIcon = "📢";
                break;
            case "INFO":
                categoryColor = CYAN;
                categoryIcon = "ℹ️";
                break;
        }

        // Priority indicator
        String priorityIndicator = "";
        if (priority.equalsIgnoreCase("HIGH")) {
            priorityIndicator = BRIGHT_RED + " [HIGH PRIORITY]" + RESET;
        } else if (priority.equalsIgnoreCase("MEDIUM")) {
            priorityIndicator = BRIGHT_YELLOW + " [MEDIUM]" + RESET;
        }

        System.out.println(
                "\n  " + BOLD + BRIGHT_CYAN + "┌─ Message Details ────────────────────────────────────────┐" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + BOLD + "ID:" + RESET + " " + messageId +
                "  │  " + (isRead ? DIM + "Read" : BRIGHT_GREEN + "New") + RESET + priorityIndicator);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + categoryIcon + " " + categoryColor
                + category.toUpperCase() + RESET);

        System.out.println("  " + BRIGHT_CYAN + "├─ Subject" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + BOLD + WHITE + subject + RESET);

        System.out.println("  " + BRIGHT_CYAN + "├─ Message" + RESET);
        // Word wrap content
        String[] words = content.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (line.length() + word.length() > 55) {
                System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + line.toString());
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        if (line.length() > 0) {
            System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " " + line.toString());
        }

        System.out.println("  " + BRIGHT_CYAN + "├─ Details" + RESET);
        System.out.println("  " + BRIGHT_CYAN + "│" + RESET + " Sent: " + DIM + dateFormat.format(sentDate) + RESET);

        System.out.println("  " + BRIGHT_CYAN + "└──────────────────────────────────────────────────────────┘" + RESET);
    }

    public String getShortDisplay() {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String BRIGHT_GREEN = "\u001B[92m";
        String DIM = "\u001B[2m";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
        String readStatus = isRead ? DIM : BRIGHT_GREEN + "●" + RESET;
        String truncatedSubject = subject.length() > 40 ? subject.substring(0, 37) + "..." : subject;

        return readStatus + " " + BOLD + truncatedSubject + RESET + " " + DIM + "(" + dateFormat.format(sentDate) + ")"
                + RESET;
    }
}
