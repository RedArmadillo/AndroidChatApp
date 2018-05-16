package group7.tcss450.uw.edu.chatapp.Models;

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
