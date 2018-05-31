package group7.tcss450.uw.edu.chatapp.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.Models.Message;
import group7.tcss450.uw.edu.chatapp.R;
import group7.tcss450.uw.edu.chatapp.Utils.ListenManager;
import group7.tcss450.uw.edu.chatapp.Utils.MessageListAdapter;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private List<Message> messageList = new ArrayList<>();
    private String mUsername;
    private String mSendUrl,  mRetrieveUrl;
    private int oldListSize = 0;
    private ListenManager mListenManager;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecycleView;
    private MessageListAdapter mAdapter;
    private int mChatId;
    private String mRoomName;
    private boolean isUserScrolling = false, didSizeChange;
    private int dateDecrement = -1;
    String TAG = "CHATFRAGMENT";

    private final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            updateListenManager();
            isUserScrolling = false;
        }
    };
    public ChatFragment() {
        mChatId = 1;
    }


    @SuppressLint("ValidFragment")
    public ChatFragment(int chatId, String roomname) {
        mRoomName = roomname;
        mChatId = chatId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        v.findViewById(R.id.chatSendButton).setOnClickListener(this::sendMessage);
        mRecycleView = v.findViewById(R.id.chatOuputRecycleView);
        mAdapter = new MessageListAdapter(getContext(), messageList);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mAdapter);

        // User scroll always up to load old messages
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //detect is the topmost item visible and is user scrolling? if true then only execute
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && (mLayoutManager.findFirstVisibleItemPosition()
                        == 0 || messageList.size() == 0)) {
                    Log.d(TAG,"first visible " + mLayoutManager.findViewByPosition(0));
                    if(isUserScrolling || messageList.size() == 0) {
                        Handler handler = new Handler();
                        handler.postDelayed(myRunnable, 500);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) {
                    isUserScrolling = true;
                } else isUserScrolling = false;
            }
        });
    return v;

    }

    private void updateListenManager() {
        String timestamp;
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, dateDecrement--);
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        timestamp = dateFormat.format(cal.getTime());
        messageList.clear();
        mListenManager.stopListening();
        mListenManager = new ListenManager.Builder(mRetrieveUrl.toString(),
                this::publishProgress)
                .setTimeStamp(timestamp)
                .setExceptionHandler(this::handleError)
                .setDelay(100)
                .build();
        mListenManager.startListening();
        //mRecycleView.smoothScrollToPosition(0);
        Log.d(TAG, timestamp);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");

        mSendUrl = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_send_message))
                .build()
                .toString();
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", Integer.toString(mChatId))
                .build();
        mRetrieveUrl = retrieve.toString();

        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(10)
                    .build();
        }

    }

    private void sendMessage(final View theButton) {
        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) getView().findViewById(R.id.chatInputEditText))
                .getText().toString();
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), mChatId);
            messageJson.put(getContext().getString(R.string.keys_json_roomname), mRoomName);
        } catch (JSONException e) {
            Log.d("ERROR on sendMessage", "!!!!");
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg);
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ((EditText) getView().findViewById(R.id.chatInputEditText))
                        .setText("");
            }
        } catch (JSONException e) {
            Log.d("endOfSend", "error");
            e.printStackTrace();
        }
    }

    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }

    private void publishProgress(JSONObject messages) {
        if(messages.has(getString(R.string.keys_json_messages))) {
            try {
                JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));
                if (jMessages.length() == 0) return;
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    Message m = new Message(userMessage, username);
                    messageList.add(m);
                }
                // Update the list size
                //oldListSize  = jMessages.length();
                getActivity().runOnUiThread(() -> {
                    mAdapter.notifyDataSetChanged();
                });
                mRecycleView.smoothScrollToPosition(mAdapter.getItemCount() - 1);

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = mListenManager.stopListening();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }


}
