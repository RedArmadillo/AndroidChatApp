package group7.tcss450.uw.edu.chatapp.Activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.Fragment.ReSendEmailFragment;
import group7.tcss450.uw.edu.chatapp.Front_End_Register_Login.Credentials;
import group7.tcss450.uw.edu.chatapp.Front_End_Register_Login.LoginFragment;
import group7.tcss450.uw.edu.chatapp.Front_End_Register_Login.RegisterFragment;
import group7.tcss450.uw.edu.chatapp.LandingActivity;
import group7.tcss450.uw.edu.chatapp.R;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener{

    private static final String TAG = "MAIN ACTIVITY";
    Credentials mCredentials;

    @Override
    public void onRegisterLoadClicked() {
        RegisterFragment userRegistration = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, userRegistration).addToBackStack(null);
// Commit the transaction
        transaction.commit();
    }
    @Override
    public void onLoginAttempt(Credentials credentials) {
//build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();
                //build the JSONObject
        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
//instantiate and execute the AsyncTask.
//Feel free to add a handler for onPreExecution so that a progress bar
//is displayed or maybe disable buttons. You would need a method in
//LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateToken();
        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                SharedPreferences prefs =
                        getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in),
                        false)) {
                    loadHomeNavigation();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainer,
                                    new LoginFragment(),
                                    getString(R.string.keys_fragment_login))
                            .commit();
                }
            }
        }
    }

    private void loadHomeNavigation() {
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
    }


    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            Log.d("Problem:", result);
            if (success) {
                // Update Firebase token
                updateToken();
                // Save user's preference
                checkStayLoggedIn();
                saveMemberid(mCredentials.getUsername());
//Login was successful. Switch to the loadSuccessFragment.
                // Ideally, subscribe should be after Register
                // But i put it here for old users
                FirebaseMessaging.getInstance().subscribeToTopic(mCredentials.getUsername());
                loadHomeNavigation();

            } else if(result.equals("{\"success\":false,\"message\":\"account not verified\"}")) {
                Log.d("HUZZAH", "SUCCESS IS MINE");
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer,
                                new ReSendEmailFragment(),
                                getString(R.string.keys_fragment_ReSendEmailFragment))
                        .commit();
            }
            else {
                LoginFragment frag =
                        (LoginFragment) getSupportFragmentManager()
                                .findFragmentByTag(
                                        getString(R.string.keys_fragment_login));
                frag.setError(result);
            }
        } catch (JSONException e) {
//It appears that the web service didn’t return a JSON formatted String
//or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
           // Log.d("In Register Attempt", resultsJSON.toString());
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                updateToken();
                // The user from now on will subscribe to their username topic to help sending notification
                FirebaseMessaging.getInstance().subscribeToTopic(mCredentials.getUsername());
                Log.d("In Register Attempt", " YAY!");
//Login was successful. Switch to the loadSuccessFragment.
                loadHomeNavigation();
            } else {
                Log.d("In Register Attempt Else", " NEY!");
//Login was unsuccessful. Don’t switch fragments and inform the user
                RegisterFragment frag =
                        (RegisterFragment) getSupportFragmentManager()
                                .findFragmentByTag(
                                        getString(R.string.keys_fragment_register));
                //frag.setError("Log in unsuccessful");
            }
        } catch (JSONException e) {
//It appears that the web service didn’t return a JSON formatted String
//or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

        private void checkStayLoggedIn() {
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
//save the username for later usage
            prefs.edit().putString(
                    getString(R.string.keys_prefs_username),
                    mCredentials.getUsername())
                    .apply();
            // subscribe to a "user" topic for future Firebase tasks
            FirebaseMessaging.getInstance().subscribeToTopic(mCredentials.getUsername());
            if (((CheckBox) findViewById(R.id.checkBox)).isChecked()) {
//save the users “want” to stay logged in
                prefs.edit().putBoolean(
                        getString(R.string.keys_prefs_stay_logged_in),
                        true)
                        .apply();
            }
        }

        private void saveMemberid(String username) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_get_memberid))
                    .build();
            JSONObject user = new JSONObject();
            try {
                user.put(getString(R.string.keys_prefs_username), username);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Retrieve member id from web service
            new SendPostAsyncTask.Builder(uri.toString(), user)
                    .onPostExecute(this::saveMemberidOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }

    // Save memberid as preference
    private void saveMemberidOnPost(String result) {
        int memberid = 0;
        try {
            JSONObject resultsJSON = new JSONObject(result);
            memberid = resultsJSON.getInt("memberid");
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
//save the username for later usage
            prefs.edit().putInt(
                    getString(R.string.keys_prefs_memberid),
                    memberid)
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Every instance of our app will have a distinct firebase token
    // This method retrieves that toke and save on server
    public void updateToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().putString(getString(R.string.token), refreshedToken).apply();

        String u_name = prefs.getString(getString(R.string.keys_prefs_username), "");
        // Update user's token on server as well
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_update_token))
                .build();
        JSONObject msg = new JSONObject();
        try {
            msg.put(getString(R.string.keys_prefs_username), u_name);
            msg.put(getString(R.string.token), refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::updateTokenOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void updateTokenOnPost(String result) {
        Log.d(TAG + " result of update token", result);
    }

    @Override
    public void onRegisterAttempt(Credentials creds) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();
//build the JSONObject
        JSONObject msg = creds.asJSONObject();
        mCredentials = creds;
        Log.d("tag2", mCredentials.getEmail());
        Log.d("tag2", mCredentials.getFirstName());
        Log.d("tag2", mCredentials.getLastName());
        Log.d("tag2", mCredentials.getUsername());

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }


    public static class ThemeChangeActivity extends AppCompatActivity implements View.OnClickListener{
        ListView mListView;
        Button mChangeTheme;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_theme_change);
        }

        @Override
        public void onClick(View view) {

        }
    }
}


