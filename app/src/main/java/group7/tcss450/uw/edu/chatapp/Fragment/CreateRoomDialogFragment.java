package group7.tcss450.uw.edu.chatapp.Fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import group7.tcss450.uw.edu.chatapp.R;


public class CreateRoomDialogFragment extends DialogFragment  {

    private NoticeDialogListener mNoticeListener;

    public interface NoticeDialogListener {
        void onDialogReturn(String roomName);

    }

    private String mTitle = "Create";
    private Button mCreateButton;
    private Button mCancelButton;
    private EditText mInputRoomName;

    public CreateRoomDialogFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_create_room, container, false);
        mInputRoomName = v.findViewById(R.id.createDialogInput);
        mCreateButton = v.findViewById(R.id.createDialogButton);
        mCancelButton = v.findViewById(R.id.cancelDialogButton);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(mTitle);
        mInputRoomName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mCancelButton.setOnClickListener(view1 -> getDialog().dismiss());
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNoticeListener.onDialogReturn(mInputRoomName.getText().toString());
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
            mNoticeListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
