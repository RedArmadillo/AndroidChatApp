package group7.tcss450.uw.edu.chatapp.Models;

/**
 * This class represents an invitation object with necessary information
 * Mainly be used for the RecyclerView adapter
 */
public class Invitation extends ChatRoom{

    private int mChatId;
    private String mRoomName;
    private String mSender;

    public Invitation(int chatId, String roomName, String sender) {
        super();
        mChatId = chatId;
        mRoomName = roomName;
        mSender = sender;
    }


    public int getChatId() { return mChatId;}
    public String getSender() { return mSender;}
    public String getRoomName() { return mRoomName;}

}
