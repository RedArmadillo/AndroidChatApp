package group7.tcss450.uw.edu.chatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Async.SendPutAsyncTask;
import group7.tcss450.uw.edu.chatapp.Models.Invitation;
import group7.tcss450.uw.edu.chatapp.R;

public class InvitationViewAdapter extends RecyclerView.Adapter {
    private List<Invitation> mInvitationList;
    public InvitationViewAdapter(List<Invitation> list) {
        mInvitationList = list;
    }
    public ProgressBar mBar;


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

    public void sendResponse(Boolean accept, View view, Invitation invitation, InvitationViewHolder holder) {
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
        Log.d("response URL", mResponseURL);
        try {
            message.put(view.getContext().getString(R.string.keys_prefs_memberid),currentMemberId);
            message.put(view.getContext().getString(R.string.keys_json_chat_id_lowercase), invitation.getChatId());
            message.put(view.getContext().getString(R.string.keys_prefs_accept), accept);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mBar = holder.mBar;
        new SendPutAsyncTask.Builder(mResponseURL, message)
                .onPreExecute(this::onPreResponse)
                .onPostExecute(this::onPostResponse)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void onPreResponse() {
        mBar.setVisibility(View.VISIBLE);
    }


    private void handleError(String s) {
        Log.d("ERROR IN RESPONSE TO INVITATION", s);
    }

    private void onPostResponse(String s) {
        Log.d("RESPONSE from RESPONSE", s);
        mBar.setVisibility(View.GONE);
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
        public ProgressBar mBar;
        public InvitationViewHolder(View view) {
            super(view);
            mView = view;
            mView.setClickable(true);
            mSender = (TextView) view.findViewById(R.id.invitationSenderName);
            mRoomName = (TextView) view.findViewById(R.id.invitationRoomName);
            mJoinButton = (Button) view.findViewById(R.id.invitationJoinButton);
            mNoButton = (Button) view.findViewById(R.id.invitationNoButton);
            mBar = (ProgressBar) view.findViewById((R.id.invitationProgressBar));
        }

        public void bind(final Invitation i, InvitationViewHolder holder) {
            mSender.setText(i.getSender());
            mRoomName.setText(i.getRoomName());
//            mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mNoButton.setVisibility(View.VISIBLE);
//                    mJoinButton.setVisibility(View.VISIBLE);
//                }
//            });
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendResponse(false, mView, i, holder);
                    mNoButton.setEnabled(false);
                    mJoinButton.setEnabled(false);
                }
            });

            mJoinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNoButton.setEnabled(false);
                    mJoinButton.setEnabled(false);
                    sendResponse(true, mView,i, holder);
                }
            });
        }

    }

}
