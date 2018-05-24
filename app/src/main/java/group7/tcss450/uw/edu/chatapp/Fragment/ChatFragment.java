package group7.tcss450.uw.edu.chatapp.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
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
    private String mSendUrl;
    //private TextView mOutputTextView;
    private ListenManager mListenManager;
    private RecyclerView mRecycleView;
    private MessageListAdapter mAdapter;
    private int mChatId;
    private String mRoomName;
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
        //mOutputTextView = v.findViewById(R.id.chatOutputTextView);
        mRecycleView = v.findViewById(R.id.chatOuputRecycleView);
        mAdapter = new MessageListAdapter(getContext(), messageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(mAdapter);
        return v;

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
            mRecycleView.scrollToPosition(mAdapter.getItemCount() - 1);
            try {
                JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    Message m = new Message(userMessage, username);
                    messageList.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

           getActivity().runOnUiThread(() -> {
               mAdapter.notifyDataSetChanged();
            });

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
