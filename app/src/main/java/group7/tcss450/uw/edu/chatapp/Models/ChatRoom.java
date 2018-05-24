package group7.tcss450.uw.edu.chatapp.Models;

public class ChatRoom {
    private int mChatId;
    private StringBuilder mName;
    private StringBuilder mLastMsg;
    private StringBuilder mLastSender;


    public ChatRoom() {

    }
    public ChatRoom(int chatId) {
        mChatId = chatId;
        mName = new StringBuilder(" ");
        mLastSender = new StringBuilder(" ");
        mLastMsg = new StringBuilder(" ");
    }

    public ChatRoom(String name, String lastSender) {
        mName = new StringBuilder(name);
        mLastSender = new StringBuilder(lastSender);
    }
    public int getChatId() {
        return mChatId;
    }
    public void setName(String name) {
        mName = new StringBuilder(name);
    }
    public String getName() {
        return mName.toString();
    }
    public void setLastMsg(String msg) {
        mLastMsg = new StringBuilder(msg);
    }
    public String getLastMsg() {
        return mLastMsg.toString();
    }
    public void setLastSender(String username) {
        mLastSender = new StringBuilder(username);
    }
    public String getLastSender() {
        return mLastSender.toString();
    }


}
