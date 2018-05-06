package group7.tcss450.uw.edu.chatapp.Models;

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
