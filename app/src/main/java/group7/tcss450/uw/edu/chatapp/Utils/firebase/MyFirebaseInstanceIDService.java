package group7.tcss450.uw.edu.chatapp.Utils.firebase;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import group7.tcss450.uw.edu.chatapp.R;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString(getString(R.string.token), refreshedToken).apply();

        int memberid = preferences.getInt(getString(R.string.keys_prefs_memberid), 0);
        // Update user's token on server as well
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_update_token))
                .build();
        JSONObject msg = new JSONObject();
        try {
            msg.put(getString(R.string.keys_prefs_memberid), memberid);
            msg.put(getString(R.string.token), refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ASyncTask can't be executed on a worker thread
        // so we gonna let DoInBackground runs manually here

        StringBuilder response = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {
            URL urlObject = new URL(uri.toString());
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

            wr.write(msg.toString());
            wr.flush();
            wr.close();

            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while((s = buffer.readLine()) != null) {
                response.append(s);
            }
            Log.d(TAG, "Finish updating token!");
        } catch (Exception e) {
            response = new StringBuilder("Unable to connect, Reason: "
                    + e.getMessage());
            Log.d(TAG, response.toString());
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


}
