package group7.tcss450.uw.edu.chatapp.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Models.Invitation;
import group7.tcss450.uw.edu.chatapp.R;

public class InvitationViewAdapter extends RecyclerView.Adapter {
    private List<Invitation> mInvitationList;
    //private final OnListFragmentInteractionListener mListener;

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
        ((InvitationViewHolder) holder).bind(i);
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

        public void bind(Invitation i) {
            mSender.setText(i.getSender());
            mRoomName.setText(i.getRoomName());
            mNoButton.setVisibility(View.VISIBLE);
            mJoinButton.setVisibility(View.VISIBLE);
        }

    }

}
