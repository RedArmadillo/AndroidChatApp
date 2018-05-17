package group7.tcss450.uw.edu.chatapp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.Fragment.CreateRoomDialogFragment;
import group7.tcss450.uw.edu.chatapp.Models.ChatRoom;
import group7.tcss450.uw.edu.chatapp.Models.Invitation;
import group7.tcss450.uw.edu.chatapp.R;
import group7.tcss450.uw.edu.chatapp.Utils.ChatRoomViewAdapter;
import group7.tcss450.uw.edu.chatapp.Utils.InvitationViewAdapter;
import group7.tcss450.uw.edu.chatapp.Utils.ListenManager;

public class ChatListActivity extends AppCompatActivity implements CreateRoomDialogFragment.NoticeDialogListener{

    private List<ChatRoom> chatRoomList = new ArrayList<>();
    private List<Invitation> invitationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView invRecyclerView;
    private ChatRoomViewAdapter mAdapter;
    private InvitationViewAdapter mInvAdapter;
    private String mCreateRoomURL;
    private String mGetInvitationURL;
    private ListenManager mListenManager;
    private ListenManager mInvitationListenManager;
    private Button mRecentButton;
    private Button mInvitationButton;
    private FloatingActionButton myFabButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycle_view);
        invRecyclerView = (RecyclerView) findViewById(R.id.invitation_recycle_view);

        mAdapter = new ChatRoomViewAdapter(chatRoomList);
        mInvAdapter = new InvitationViewAdapter(invitationList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager invLayoutManager = new LinearLayoutManager(getApplicationContext());

        invRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        invRecyclerView.setLayoutManager(invLayoutManager);
        invRecyclerView.setItemAnimator(new DefaultItemAnimator());
        invRecyclerView.setAdapter(mInvAdapter);

        myFabButton = findViewById(R.id.chatRoomAddButton);
        myFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRoomDialogFragment frag = new CreateRoomDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                frag.show(fm, "input room name");
            }
        });
        mRecentButton = findViewById(R.id.chat_Recent);
        mRecentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoomInCharge();
            }
        });
        mInvitationButton = findViewById(R.id.chat_Invitation);
        mInvitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitationInCharge();
            }
        });
    }

    public void chatRoomInCharge() {
        myFabButton.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        invRecyclerView.setVisibility(View.INVISIBLE);

    }

    public void invitationInCharge() {
        // "turn off" chat stuff
        myFabButton.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        invRecyclerView.setVisibility(View.VISIBLE);

    }
    public void createNewRoom(String roomName) {
        JSONObject messageJson = new JSONObject();

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            SharedPreferences prefs =
                    getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
            messageJson.put("room name", roomName);
            messageJson.put("username", currentUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mCreateRoomURL, messageJson)
                .onPostExecute(this::endOfCreateRoom)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void endOfCreateRoom(String result) {
        try {
            JSONObject res = new JSONObject(result);
            Log.d("response from service for create new room", result);

            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                //int newChatID = res.getInt(getString(R.string.keys_json_new_chat_id));
                //chatRoomList.add(new ChatRoom(newChatID, "new room", "hello", "me"));
                //mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }
        } catch (JSONException e) {
            Log.d("endOfUpdateNewRoom", "error");
            e.printStackTrace();
        }
    }


    private void handleError(final String msg) {
        Log.e("Can't create new room!!!", msg.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
        int currentMemberId = prefs.getInt(getString(R.string.keys_prefs_memberid),0);
        mCreateRoomURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_chat_room))
                .build()
                .toString();


        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_chat_room))
                .appendQueryParameter("username", currentUser)
                .build();


        mListenManager = new ListenManager.Builder(retrieve.toString(),
                this::publishProgress)
                .setExceptionHandler(this::handleError)
                .setDelay(1000)
                .build();

        mGetInvitationURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_invitation))
                .appendQueryParameter("memberid", String.valueOf(currentMemberId))
                .build().toString();
        Log.d("INVITATION URL", mGetInvitationURL);
        mInvitationListenManager = new ListenManager.Builder(mGetInvitationURL,
                this::invitationPublishProgress)
                .setExceptionHandler(this::handleInvitationError)
                .setDelay(10000)
                .build();
    }

    private void handleInvitationError(Exception e) {
        Log.i("INVITATION ERROR", e.toString());
    }

    public void invitationPublishProgress(JSONObject result) {
        Log.i("INVITATION PROGRESS", result.toString());
        if (result.has(getString(R.string.keys_json_invitations))) {
            List<Invitation> newList = new ArrayList<>();
            try {
                JSONArray jInvitations = result.getJSONArray(getString(R.string.keys_json_invitations));
                for (int i = 0; i < jInvitations.length(); i++) {
                    JSONObject invitation = jInvitations.getJSONObject(i);
                    String sender = invitation.getString(getString(R.string.keys_prefs_username));
                    String roomName = invitation.getString("name");
                    int id = invitation.getInt(getString(R.string.keys_json_chat_id_lowercase));
                    Invitation inv = new Invitation(id, roomName, sender);
                    newList.add(inv);
                    Log.d("INVITATION PROGRESS", "Passing a room : " + roomName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            runOnUiThread(() -> {
                mInvAdapter.updateData(newList);
            });
        } else {
            Log.d("err", "received no room!!!");
        }
    }

    private void publishProgress(JSONObject result) {
        //final String[] ids;
        if (result.has(getString(R.string.keys_json_success))) {
            List<ChatRoom> newList = new ArrayList<>();
            try {
                JSONArray jIds = result.getJSONArray(getString(R.string.keys_json_success));
                for (int i = 0; i < jIds.length(); i++) {
                    JSONObject id = jIds.getJSONObject(i);
                    int roomId = id.getInt(getString(R.string.keys_json_chat_id_lowercase));
                    String lastMsg = id.getString(getString(R.string.keys_json_message));
                    String roomName = id.getString("name");
                    ChatRoom c = new ChatRoom(roomId);
                    c.setName(roomName);
                    c.setLastMsg(lastMsg);
                    newList.add(c);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            runOnUiThread(() -> {
                mAdapter.updateData(newList);
            });
        } else {
            Log.d("err", "received no room!!!");
        }
    }

    private void handleError(Exception e) {
        Log.e("Error in listen", e.toString());
    }


    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
        mInvitationListenManager.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mListenManager.stopListening();
        mInvitationListenManager.stopListening();

    }


    @Override
    public void onDialogReturn(String roomName) {
        Log.d("DIALOG", "Creating room");
        createNewRoom(roomName);
    }


}
