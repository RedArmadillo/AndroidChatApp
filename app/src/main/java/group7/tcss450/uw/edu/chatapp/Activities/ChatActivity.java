package group7.tcss450.uw.edu.chatapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.Fragment.AddUserDialogFragment;
import group7.tcss450.uw.edu.chatapp.Fragment.ChatFragment;
import group7.tcss450.uw.edu.chatapp.Fragment.ConfirmDialogFragment;
import group7.tcss450.uw.edu.chatapp.R;
import group7.tcss450.uw.edu.chatapp.Utils.SettingMenuActivity;
//import group7.tcss450.uw.edu.chatapp.Utils.SettingMenuActivity;

public class ChatActivity extends SettingMenuActivity implements AddUserDialogFragment.AddUserDialogListener,
        ConfirmDialogFragment.LeaveRoomDialogListener{
    private static final String TAG = "CHATACTIVITY";
    public int mChatId;
    public String mRoomname;
    private String mInviteURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Getting the chat id from the Intent to retrieve the correct chat room
        int chatId = getIntent().getIntExtra(getString(R.string.keys_json_chat_id_lowercase), 1);
        mChatId = chatId;
        mRoomname =  getIntent().getStringExtra(getString(R.string.keys_json_roomname));

        getSupportFragmentManager().beginTransaction()
                .add(R.id.chatLayout, new ChatFragment(chatId, mRoomname))
                .commit();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        // Open the dialog based on user choosing
        switch (item.getItemId()) {
            case R.id.chat_menu_add_item:
                AddUserDialogFragment frag = new AddUserDialogFragment();
                frag.show(fm, "input username");
                return true;
            case R.id.chat_menu_leave_item:
                ConfirmDialogFragment fragment = new ConfirmDialogFragment();
                fragment.show(fm, "leave room");
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // @Param: The return username from AddUserDialog
    // We will collect that username and send them a chat invitation
    @Override
    public void onDialogReturn(String username) {
        JSONObject messageJson = new JSONObject();
        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");

        try {
            messageJson.put(getString(R.string.keys_json_sender), currentUser);
            messageJson.put(getString(R.string.keys_json_receiver), username);
            messageJson.put(getString(R.string.keys_json_chat_id_lowercase), mChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mInviteURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_invitation))
                .build()
                .toString();
        new SendPostAsyncTask.Builder(mInviteURL, messageJson)
                .onPostExecute(this::endOfAdd)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleError(String s) {
        Log.d(TAG ,"can't add a user");
    }

    private void endOfAdd(String result) {
            Log.d(TAG, result);
    }

    // The confirmation return from LeaveRoomDialog
    // Request our server to remove current user out of the room
    @Override
    public void onLeaveDialogReturn() {
        JSONObject messageJson = new JSONObject();
        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        int memberid = prefs.getInt(getString(R.string.keys_prefs_memberid), 0);
        try {
            messageJson.put(getString(R.string.keys_prefs_memberid), memberid);
            messageJson.put(getString(R.string.keys_json_chat_id_lowercase), mChatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String leaveRoomURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_chat_room))
                .appendPath(getString(R.string.ep_leave))
                .build()
                .toString();
        new SendPostAsyncTask.Builder(leaveRoomURL, messageJson)
                .onPostExecute(this::endOfLeave)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void endOfLeave(String result) {
            Log.d(TAG, result);
            Intent intent = new Intent(this, ChatListActivity.class);
            startActivity(intent);
    }
}
