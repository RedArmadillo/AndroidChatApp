package group7.tcss450.uw.edu.chatapp.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateRoomDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRoomDialogFragment extends DialogFragment  {

    private NoticeDialogListener mNoticeListener;

    public interface NoticeDialogListener {
        void onDialogReturn(String roomName);

    }


    private String mTitle = "Create";
    private Button mCreateButton;
    private Button mCancelButton;
    private EditText mInputRoomName;
    private String mCreateRoomURL;

    public CreateRoomDialogFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_room_diaglog, container, false);
        mInputRoomName = v.findViewById(R.id.createDialogInput);
        mCreateButton = v.findViewById(R.id.createDialogButton);
        mCancelButton = v.findViewById(R.id.cancelDialogButton);

        //mOkButton.setOnClickListener(this::createNewRoom);
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

    public void createNewRoom(View view) {
        JSONObject messageJson = new JSONObject();

        try {
            //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            messageJson.put("room name", mInputRoomName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mCreateRoomURL, messageJson)
                .onPostExecute(this::endOfCreateRoom)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleError(String s) {
    }

    private void endOfCreateRoom(String s) {

    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
        mCreateRoomURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_create_room))
                .appendQueryParameter("username", currentUser)
                .build()
                .toString();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNoticeListener = null;
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
