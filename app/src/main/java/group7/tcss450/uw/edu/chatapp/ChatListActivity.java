package group7.tcss450.uw.edu.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.Models.ChatRoom;
import group7.tcss450.uw.edu.chatapp.Utils.ChatRoomViewAdapter;
import group7.tcss450.uw.edu.chatapp.Utils.ListenManager;

public class ChatListActivity extends AppCompatActivity {
    private List<ChatRoom> chatRoomList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatRoomViewAdapter mAdapter;
    private String mCreateRoomURL;
    private ListenManager mListenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycle_view);

        mAdapter = new ChatRoomViewAdapter(chatRoomList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.chatRoomAddButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Implementation needed", Toast.LENGTH_SHORT).show();
            }
        });
        fab.setOnClickListener(this::createNewRoom);
        //loadRooms("");
    }

    public void createNewRoom(View view) {
        JSONObject messageJson = new JSONObject();

        try {
            //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            messageJson.put("room name", "new room");
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

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                //int newChatID = res.getInt(getString(R.string.keys_json_new_chat_id));
                //chatRoomList.add(new ChatRoom(newChatID, "new room", "hello", "me"));
                //mAdapter.notifyDataSetChanged();
               //recyclerView.smoothScrollToPosition(0);
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
        mCreateRoomURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_create_room))
                .appendQueryParameter("username", currentUser)
                .build()
                .toString();


        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_get_rooms))
                .appendQueryParameter("username", currentUser)
                .build();

        mListenManager = new ListenManager.Builder(retrieve.toString(),
                this::publishProgress)
                .setExceptionHandler(this::handleError)
                .setDelay(100000)
                .build();
    }

    private void publishProgress(JSONObject chatids) {
        //final String[] ids;
        if(chatids.has(getString(R.string.keys_json_chat_id))) {
            Log.d("Success", "Received list of room....");
            try {
                JSONArray jIds = chatids.getJSONArray(getString(R.string.keys_json_chat_id));
                for (int i = 0; i < jIds.length(); i++) {
                    JSONObject id = jIds.getJSONObject(i);
                    int roomId = id.getInt(getString(R.string.keys_json_chat_id_lowercase));
                    Log.d("id: " , Integer.toString(roomId));
                    ChatRoom c = new ChatRoom(roomId);
                    chatRoomList.add(c);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d("err", "received no room!!!");
        }
    }

    private void handleError(Exception e) {
        Log.e("Error in listen", e.toString());
    }


    private void loadRooms(String result) {
        ChatRoom one = new ChatRoom(1, "one", "hello", "me");
        chatRoomList.add(one);
        ChatRoom two = new ChatRoom(2, "two", "world", "me again");
        chatRoomList.add(two);
        ChatRoom three = new ChatRoom(3, "three", "this", "is");
        chatRoomList.add(three);
        ChatRoom four = new ChatRoom(4, "four", "me", "testing");
        chatRoomList.add(four);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }

}
