package group7.tcss450.uw.edu.chatapp.Fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import group7.tcss450.uw.edu.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserDialogFragment extends DialogFragment {

    private AddUserDialogListener mNoticeListener;

    public interface AddUserDialogListener {
        void onDialogReturn(String username);

    }

    public EditText mInputUsername;
    public Button mCancelButton;
    public Button mSendButton;

    public AddUserDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_user, container, false);
        mInputUsername = v.findViewById(R.id.addDialogInput);
        mSendButton = v.findViewById(R.id.sendAddDialogButton);
        mCancelButton = v.findViewById(R.id.cancelAddDialogButton);

        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputUsername.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mCancelButton.setOnClickListener(view1 -> getDialog().dismiss());
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNoticeListener.onDialogReturn(mInputUsername.getText().toString());
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
            mNoticeListener = (AddUserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddUserDialogListener");
        }
    }
}
