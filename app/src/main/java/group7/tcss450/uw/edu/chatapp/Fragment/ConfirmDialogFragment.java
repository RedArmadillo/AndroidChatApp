package group7.tcss450.uw.edu.chatapp.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group7.tcss450.uw.edu.chatapp.R;

/**
 * This class handle the dialog opened when user wants to leave a chat room
 */
public class ConfirmDialogFragment extends DialogFragment {
    private LeaveRoomDialogListener mNoticeListener;

    public interface LeaveRoomDialogListener {
        void onLeaveDialogReturn();

    }
    public Button mNoButton;
    public Button mYesButton;
    public ConfirmDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.dialog_confirm, container, false);
        mNoButton = v.findViewById(R.id.cancelLeaveDialogButton);
        mYesButton = v.findViewById(R.id.okLeaveDialogButton);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mNoButton.setOnClickListener(view1 -> getDialog().dismiss());
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs =
                        getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                int memberid = prefs.getInt(getString(R.string.keys_prefs_memberid), 0);
                mNoticeListener.onLeaveDialogReturn();
                getDialog().dismiss();
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mNoticeListener = (LeaveRoomDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LeaveRoomDialogListener");
        }
    }
}
