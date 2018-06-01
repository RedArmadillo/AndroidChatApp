package group7.tcss450.uw.edu.chatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Async.SendPutAsyncTask;
import group7.tcss450.uw.edu.chatapp.Models.Invitation;
import group7.tcss450.uw.edu.chatapp.R;

/**
 * Adapter class for the recyclerview which displays list of invitation
 */
public class InvitationViewAdapter extends RecyclerView.Adapter {
    private List<Invitation> mInvitationList;
    public InvitationViewAdapter(List<Invitation> list) {
        mInvitationList = list;
    }
    private ProgressBar mBar;



    public void updateData(List<Invitation> roomList) {
        mInvitationList.clear();
        mInvitationList.addAll(roomList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_invitation_list, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Invitation i = mInvitationList.get(position);
        ((InvitationViewHolder) holder).bind(i, (InvitationViewHolder) holder);
    }

    // Handle user responds to an invitation
    // Send response to server depends on which button user clicked on

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
        mBar.setVisibility(View.GONE);
        notifyDataSetChanged();
    }




    @Override
    public int getItemCount() {
        return mInvitationList.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder  {
        private View mView;
        private TextView mSender;
        private TextView mRoomName;
        private Button mNoButton;
        private Button mJoinButton;
        private ProgressBar mBar;
        private InvitationViewHolder(View view) {
            super(view);
            mView = view;
            mView.setClickable(true);
            mSender = (TextView) view.findViewById(R.id.invitationSenderName);
            mRoomName = (TextView) view.findViewById(R.id.invitationRoomName);
            mJoinButton = (Button) view.findViewById(R.id.invitationJoinButton);
            mNoButton = (Button) view.findViewById(R.id.invitationNoButton);
            mBar = (ProgressBar) view.findViewById((R.id.invitationProgressBar));
        }

        void bind(final Invitation i, InvitationViewHolder holder) {
            mSender.setText(i.getSender());
            mRoomName.setText(i.getRoomName());
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendResponse(false, mView, i, holder);
                }
            });
            mJoinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendResponse(true, mView,i, holder);
                }
            });
        }

    }

}
