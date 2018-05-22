package group7.tcss450.uw.edu.chatapp.Utils;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Activities.ChatActivity;
import group7.tcss450.uw.edu.chatapp.Models.ChatRoom;
import group7.tcss450.uw.edu.chatapp.R;


public class ChatRoomViewAdapter extends RecyclerView.Adapter<ChatRoomViewAdapter.ViewHolder> {

    private List<ChatRoom> mChatList;

    public ChatRoomViewAdapter(List<ChatRoom> list) {
        mChatList = list;
    }
    public void updateData(List<ChatRoom> roomList) {
        mChatList.clear();
        mChatList.addAll(roomList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatroom_list_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatRoom c = mChatList.get(position);
        holder.mLastMsg.setText(c.getLastMsg());
        holder.mLastSender.setText(c.getLastSender());
        holder.mRoomName.setText(c.getName());
        holder.mView.setOnClickListener((View view) -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("chatid", c.getChatId());
            intent.putExtra(holder.itemView.getContext().getString(R.string.keys_json_roomname), c.getName());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public View mView;
        public TextView mLastMsg;
        public TextView mLastSender;
        public TextView mRoomName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setClickable(true);
            mLastMsg = (TextView) view.findViewById(R.id.chatRoomLastMsg);
            mLastSender = (TextView) view.findViewById(R.id.chatRoomLastSender);
            mRoomName = (TextView) view.findViewById(R.id.chatRoomName);
        }

    }
}
