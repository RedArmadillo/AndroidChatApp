package group7.tcss450.uw.edu.chatapp.Models;

/**
 * This class represents a message object with necessary information
 * Mainly be used for the RecyclerView adapter
 */
public class Message {
    String message;
    String username;

    public Message(String m, String u) {
        username = u;
        message = m;
    }
    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
