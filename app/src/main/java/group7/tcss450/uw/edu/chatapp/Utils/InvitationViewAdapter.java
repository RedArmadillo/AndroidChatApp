package group7.tcss450.uw.edu.chatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Models.Invitation;
import group7.tcss450.uw.edu.chatapp.R;

public class InvitationViewAdapter extends RecyclerView.Adapter {
    private List<Invitation> mInvitationList;

    public InvitationViewAdapter(List<Invitation> list) {
        mInvitationList = list;
    }


    public void updateData(List<Invitation> roomList) {
        mInvitationList.clear();
        mInvitationList.addAll(roomList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invitation_list_row, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Invitation i = mInvitationList.get(position);
        ((InvitationViewHolder) holder).bind(i, (InvitationViewHolder) holder);
    }

    public void sendResponse(Boolean accept, View view, Invitation invitation) {
        SharedPreferences prefs =
                view.getContext().getSharedPreferences(view.getContext().getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        int currentMemberId = prefs.getInt(view.getContext().getString(R.string.keys_prefs_memberid),0);
        String mResponseURL = new Uri.Builder()
                .scheme("https")
                .authority(view.getContext().getString(R.string.ep_base_url))
                .appendPath(view.getContext().getString(R.string.ep_invitation))
                .appendPath(view.getContext().getString(R.string.ep_response))
                .build().toString();
        JSONObject message = new JSONObject();
        try {
            message.put(view.getContext().getString(R.string.keys_prefs_memberid),currentMemberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mInvitationList.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder  {
        public View mView;
        public TextView mSender;
        public TextView mRoomName;
        public Button mNoButton;
        public Button mJoinButton;
        public InvitationViewHolder(View view) {
            super(view);
            mView = view;
            mView.setClickable(true);
            mSender = (TextView) view.findViewById(R.id.invitationSenderName);
            mRoomName = (TextView) view.findViewById(R.id.invitationRoomName);
            mJoinButton = (Button) view.findViewById(R.id.invitationJoinButton);
            mNoButton = (Button) view.findViewById(R.id.invitationNoButton);
        }

        public void bind(Invitation i, InvitationViewHolder holder) {
            mSender.setText(i.getSender());
            mRoomName.setText(i.getRoomName());
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNoButton.setVisibility(View.VISIBLE);
                    mJoinButton.setVisibility(View.VISIBLE);
                }
            });
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
