package group7.tcss450.uw.edu.chatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Models.Message;
import group7.tcss450.uw.edu.chatapp.R;

public class MessageListAdapter extends RecyclerView.Adapter {

    private List<Message> messageList;
    private Context mContext;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        this.messageList = messageList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_sent_message, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_received_message, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    public int getItemViewType(int position) {
        Message m = messageList.get(position);
        SharedPreferences prefs =
                mContext.getSharedPreferences(mContext.getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String currentUser = prefs.getString(mContext.getString(R.string.keys_prefs_username), "");
        if (m.getUsername().equals(currentUser)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message m = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(m);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(m);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView message, username;
        SentMessageHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.sentMessage);
            username = itemView.findViewById(R.id.sentMessageSender);
        }

        void bind(Message m) {
            message.setText(m.getMessage());
            username.setText(m.getUsername());
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView message, username;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.receivedMessage);
            username = itemView.findViewById(R.id.receivedMessageSender);
        }

        void bind(Message m) {
            message.setText(m.getMessage());
            username.setText(m.getUsername());
        }
    }
}
