package group7.tcss450.uw.edu.chatapp.Utils;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Fragment.ChatFragment;
import group7.tcss450.uw.edu.chatapp.Models.ChatRoom;
import group7.tcss450.uw.edu.chatapp.R;


public class ChatRoomViewAdapter extends RecyclerView.Adapter<ChatRoomViewAdapter.ViewHolder> {

    private List<ChatRoom> mChatList;
    //private final OnListFragmentInteractionListener mListener;

    public ChatRoomViewAdapter(List<ChatRoom> list) {
        mChatList = list;
        //mListener = listener;
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
        ChatFragment frag = new ChatFragment(c.getChatId());
        holder.mLastMsg.setText(c.getLastMsg());
        holder.mLastSender.setText(c.getLastSender());
        holder.mRoomName.setText(c.getName());
        holder.mView.setOnClickListener((View view) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Log.d("activity: ", activity.toString());
            FragmentTransaction transaction = activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chat_list_layout, frag)
                    .addToBackStack(null);
            // Commit the transaction
            transaction.commit();
            if (!frag.isAdded()) {
                frag.onAttach(view.getContext());
            }
            Toast.makeText(view.getContext(),c.getName() + " is clicked!", Toast.LENGTH_LONG).show();
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
