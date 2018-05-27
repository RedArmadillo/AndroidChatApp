package group7.tcss450.uw.edu.chatapp.Fragment;


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
        EditText token = (EditText) getView().findViewById(R.id.forgot_token_input);
        EditText password1 = (EditText) getView().findViewById(R.id.forgot_newpw_input);
        EditText password2 = (EditText) getView().findViewById(R.id.forgot_retype_input);
        EditText username = (EditText) getView().findViewById(R.id.forgot_username_input);

        if (!password1.getText().toString().equals(password2.getText().toString())) {
            password2.setError("Passwords not match");
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
            messageJson.put(getString(R.string.keys_json_password), password1.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(requestURL.toString(), messageJson)
                .onPostExecute(this::onPostChangePassword)
                .onCancelled(this::handleError)
                .build().execute();


    }

    private void onPostChangePassword(String s) {
        Toast.makeText(getContext(), "Password reset successfully", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .popBackStack();

    }

    private void requestToken(View view) {
        EditText username = (EditText) getView().findViewById(R.id.forgot_username_input);
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
        new SendPostAsyncTask.Builder(requestURL.toString(), messageJson)
                .onPostExecute(this::endRequestToken)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void endRequestToken(String s) {
        Toast.makeText(getContext(), "Token sent", Toast.LENGTH_SHORT).show();
        EditText username = (EditText) getView().findViewById(R.id.forgot_username_input);
        username.setEnabled(false);

    }

    private void handleError(final String msg) {
        Log.e(TAG, msg);
    }


}
