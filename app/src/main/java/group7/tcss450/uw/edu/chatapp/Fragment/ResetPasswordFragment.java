package group7.tcss450.uw.edu.chatapp.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;

/**
 * This fragment is used when user wants to reset their password to a new one
 */
public class ResetPasswordFragment extends Fragment {


    private static final String TAG = "ResetPasswordFragment" ;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        Button b = v.findViewById(R.id.reset_button);
        b.setOnClickListener(this::sendResetPassword);
        return v;
    }

    // Connect to web service to request change password
    private void sendResetPassword(View view) {
        EditText oldpw =  (EditText) getView().findViewById(R.id.reset_old_pw_input);
        EditText pw1 =  (EditText) getView().findViewById(R.id.reset_pw_input);
        EditText pw2 =  (EditText) getView().findViewById(R.id.reset_pw2_input);

        // Client-side checking user inputs good new password
        if (!pw1.getText().toString().equals(pw2.getText().toString())) {
            pw2.setError("Different new passwords");
            return;
        }
        if (pw1.getText().toString().trim().length() < 6) {
            pw1.setError("Password has to be more than 5 characters length");
            return;
        }
        Uri requestURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_password))
                .build();
        JSONObject messageJson = new JSONObject();
        try {
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String username = prefs.getString(getString(R.string.keys_json_username),"");
            messageJson.put(getString(R.string.keys_json_username), username);
            messageJson.put(getString(R.string.keys_new_password), pw1.getText().toString());
            messageJson.put(getString(R.string.keys_old_password), oldpw.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Access the reset password service
        new SendPostAsyncTask.Builder(requestURL.toString(), messageJson)
                .onPostExecute(this::onPostChangePassword)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void onPostChangePassword(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            // Successfully changed password
            if (resultsJSON.getBoolean(getString(R.string.keys_json_success))){
                Toast.makeText(getContext(), "Password reset successfully. Please login", Toast.LENGTH_LONG).show();
                SharedPreferences prefs =
                        getActivity().getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                prefs.edit().remove(getString(R.string.keys_prefs_username));
                prefs.edit().putBoolean(
                        getString(R.string.keys_prefs_stay_logged_in),
                        false)
                        .apply();
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            // fail case
            else {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void handleError(final String msg) {
        Log.e(TAG, msg);
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
    }


}
