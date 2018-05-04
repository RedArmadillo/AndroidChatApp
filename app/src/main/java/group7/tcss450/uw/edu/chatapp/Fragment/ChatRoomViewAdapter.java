package group7.tcss450.uw.edu.chatapp.Fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Models.ChatRoom;
import group7.tcss450.uw.edu.chatapp.R;


public class ChatRoomViewAdapter extends RecyclerView.Adapter<ChatRoomViewAdapter.ViewHolder> {

    private final List<ChatRoom> mChatList;
    //private final OnListFragmentInteractionListener mListener;

    public ChatRoomViewAdapter(List<ChatRoom> list) {
        mChatList = list;
        //mListener = listener;
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
        holder.mView.setOnClickListener(view -> {
            Toast.makeText(view.getContext(),c.getName() + " is clicked!", Toast.LENGTH_LONG).show();
        });
        /*holder.mView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentTransaction transaction = activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.landingContainer, new ChatFragment(c.getChatId()))
                    .addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }); */
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mLastMsg;
        public TextView mLastSender;
        public TextView mRoomName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLastMsg = (TextView) view.findViewById(R.id.chatRoomLastMsg);
            mLastSender = (TextView) view.findViewById(R.id.chatRoomLastSender);
            mRoomName = (TextView) view.findViewById(R.id.chatRoomName);
        }

       // @Override
       // public String toString() {
         //   return super.toString() + " '" + mContentView.getText() + "'";
        //}
    }
}
