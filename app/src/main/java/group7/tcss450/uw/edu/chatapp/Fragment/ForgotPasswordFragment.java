package group7.tcss450.uw.edu.chatapp.Fragment;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {
    private final String TAG = "ForgotPasswordFragment";

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        Button b = v.findViewById(R.id.forgot_request_button);
        b.setOnClickListener(this::requestToken);
        b = v.findViewById(R.id.forgot_confirm_btn);
        b.setOnClickListener(this::resetPassword);
        return v;
    }

    private void resetPassword(View view)  {

        // Users should have the token by now and input them
        EditText token = (EditText) getView().findViewById(R.id.forgot_token_input);
        EditText password1 = (EditText) getView().findViewById(R.id.forgot_newpw_input);
        EditText password2 = (EditText) getView().findViewById(R.id.forgot_retype_input);
        EditText username = (EditText) getView().findViewById(R.id.forgot_username_input);

        // Client-side checking, make sure they input new password correctly
        if (!password1.getText().toString().equals(password2.getText().toString())) {
            password2.setError("Passwords not match");
            return;
        }
        if (password1.getText().toString().trim().length() < 6) {
            password1.setError("Too short!");
            return;
        }
        if (password2.getText().toString().trim().length() < 6) {
            password2.setError("Too short!");
            return;
        }

        Uri requestURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_password))
                .appendPath(getString(R.string.ep_password_forgot))
                .build();
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_username), username.getText().toString());
            messageJson.put(getString(R.string.token), token.getText().toString());
            messageJson.put(getString(R.string.keys_new_password), password1.getText().toString());
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
        // Inform user that password has been reset
        Toast.makeText(getContext(), "Successfully! Please login", Toast.LENGTH_LONG).show();
        // Go back to Login screen
        getActivity().getSupportFragmentManager()
                .popBackStack();

    }

    private void requestToken(View view) {
        EditText username = (EditText) getView().findViewById(R.id.forgot_username_input);

        // Client-side checking user inputted a username
        if (username.getText().toString().length() == 0) {
            ((EditText) getView().findViewById(R.id.forgot_username_input))
                    .setError("Missing username");
            return;
        }

        Uri requestURL = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_password))
                .appendPath(getString(R.string.ep_password_request_token))
                .build();
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put("username", username.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Access the request token service
        new SendPostAsyncTask.Builder(requestURL.toString(), messageJson)
                .onPostExecute(this::endRequestToken)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void endRequestToken(String s) {
        // Inform user token has been sent
        Toast.makeText(getContext(), "Token sent", Toast.LENGTH_SHORT).show();
        EditText username = (EditText) getView().findViewById(R.id.forgot_username_input);
        username.setEnabled(false);

        // Disable the request button for few seconds in case user has multiple click
        Button request = (Button) getView().findViewById(R.id.forgot_request_button);
        request.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                request.setEnabled(true);
            }
        },6000);
        request.setText("Request new token");

    }

    private void handleError(final String msg) {
        Log.e(TAG, msg);
    }


}
