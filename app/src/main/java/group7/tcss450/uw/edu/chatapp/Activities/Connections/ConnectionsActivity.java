package group7.tcss450.uw.edu.chatapp.Activities.Connections;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import group7.tcss450.uw.edu.chatapp.Models.ChatRoom;
import group7.tcss450.uw.edu.chatapp.R;
import group7.tcss450.uw.edu.chatapp.Utils.ChatRoomViewAdapter;
import group7.tcss450.uw.edu.chatapp.Utils.ConnectionListenManager;
import group7.tcss450.uw.edu.chatapp.Utils.ConnectionsViewAdapter;
import group7.tcss450.uw.edu.chatapp.Utils.ListenManager;

public class ConnectionsActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private ConnectionsViewAdapter mAdapter;


    private ConnectionListenManager mListenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.P1);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");

        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_connections))
                .appendPath(currentUser)
                .build();


        mListenManager = new ConnectionListenManager.Builder(retrieve.toString(),
                this::publishProgress)
                .setExceptionHandler(this::handleError)
                .setDelay(3000)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private void publishProgress(JSONObject result) {
        //final String[] ids;
        Log.d("room", result.toString());
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
                //mAdapter.updateData(newList);
            });
        } else {
            Log.d("err", "received no room!!!");
        }
    }

    private void handleError(Exception e) {
        Log.e("Error in listen", e.toString());
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new CurrentConnections();
            } else if (position == 1) {
                return new IncomingConnections();
            } else if (position == 2) {
                return new OutgoingConnections();
            } else return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
