package group7.tcss450.uw.edu.chatapp.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;
import group7.tcss450.uw.edu.chatapp.Utils.ListenManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment {


    public ConnectionsFragment() {
        // Required empty public constructor
    }

    private String mUsername;
    private String mSendUrl;
    private ListView CurrentContacts;
    private ListView PendingOutgoing;
    private ListView PendingIncoming;

    private ListenManager mListenManager;

    private OnFragmentInteractionListener mListener;

    private void searchContacts(final View theButton) {
        JSONObject messageJson = new JSONObject();
        String msg = ((SearchView) getView().findViewById(R.id.SearchConnections))
                .toString();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), mUsername);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }
    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                ((EditText) getView().findViewById(R.id.SearchConnections))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                getString(R.string.keys_prefs_username),
                latestMessage)
                .apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_sendContact))
                .build()
                .toString();
        Uri retrieveCurrent = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_recieveCurrentContacts))
                .appendQueryParameter("username", getString(R.string.keys_prefs_username))
                .build();
        Uri retrieveOutgoingPending = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_recieveOutgoingContacts))
                .appendQueryParameter("username", getString(R.string.keys_prefs_username))
                .build();
        Uri retrieveIncomingPending = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_recieveIncomingContacts))
                .appendQueryParameter("username", getString(R.string.keys_prefs_username))
                .build();

        mListenManager = new ListenManager.Builder(retrieveCurrent.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        mListenManager = new ListenManager.Builder(retrieveOutgoingPending.toString(),
                this::publishProgress)
                .setExceptionHandler(this::handleError)
                .setDelay(1000)
                .build();
        mListenManager = new ListenManager.Builder(retrieveIncomingPending.toString(),
                this::publishProgress)
                .setExceptionHandler(this::handleError)
                .setDelay(1000)
                .build();
    }

    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }
    private void publishProgress(JSONObject messages) {
        final String[] msgs;
        if(messages.has(getString(R.string.keys_json_messages))) {
            try {
                JSONArray jMessages =
                        messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username =
                            msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage =
                            msg.get(getString(R.string.keys_json_message)).toString();
                    msgs[i] = username + ":" + userMessage;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            getActivity().runOnUiThread(() -> {
                for (String msg : msgs) {
                    //TODO: INSERT CORRECT CODE.
//                    CurrentContacts.append(msg);
//                    CurrentContacts.append(System.lineSeparator());
                }
            });
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections, container, false);

        ListView listView = (ListView) v.findViewById(R.id.listConnections);
        //I just put random values over here to testing display
        // When in used, we can change the array of strings to an array of Connections/Friends
        String[] values = getConnections();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1, values);

        v.findViewById(R.id.contactSearchButton).setOnClickListener(this::searchContacts);
        CurrentContacts = v.findViewById(R.id.listConnections);
        listView.setAdapter(adapter);
        return v;
    }
    public String[] getConnections() {
        String[] theConnections = new String[]{};

        return theConnections;
    }


}
