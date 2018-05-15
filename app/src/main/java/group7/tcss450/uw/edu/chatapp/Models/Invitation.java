package group7.tcss450.uw.edu.chatapp.Models;

public class Invitation {

    private int mChatId;
    private String mRoomName;
    private String mSender;

    public Invitation(int chatId, String roomName, String sender) {
        mChatId = chatId;
        mRoomName = roomName;
        mSender = sender;
    }

    public int getChatId() { return mChatId;}
    public String getRoomName() { return mRoomName;}

}
