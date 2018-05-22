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
import android.widget.Toast;

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
    public int mChatId;
    public String mRoomname;
    private String mInviteURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            //if (findViewById(R.id.chatLayout) != null) {
            int chatId = getIntent().getIntExtra(getString(R.string.keys_json_chat_id_lowercase), 1);
            mChatId = chatId;
            mRoomname =  getIntent().getStringExtra(getString(R.string.keys_json_roomname));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chatLayout, new ChatFragment(chatId, mRoomname))
                    .commit();
            //}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
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
        Log.d("CHATACTIVITY", "can't add a user");
    }

    private void endOfAdd(String result) {
        try {
            JSONObject res = new JSONObject(result);
            Log.d("response from service for add a user to room", result);
            Toast.makeText(this, "User added!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

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
        try {
            JSONObject res = new JSONObject(result);
            Log.d("response from service for leaving room", result);
            Intent intent = new Intent(this, ChatListActivity.class);
            startActivity(intent);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
