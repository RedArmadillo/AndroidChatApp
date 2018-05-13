package group7.tcss450.uw.edu.chatapp.Fragment;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.Front_End_Register_Login.LoginFragment;
import group7.tcss450.uw.edu.chatapp.R;

class ResendOnCick implements View.OnClickListener {

    View myView;

    @Override
    public void onClick(View view) {
        myView = view;
        //Takes Email as a paramater
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(view.getContext().getString(R.string.ep_base_url))
                .appendPath(view.getContext().getString(R.string.ep_reverify))
                .build();
        EditText c = view.findViewById(R.id.emailInputRe_Enter);
        String email = c.getText().toString();
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleReSendOnPost)
                .onCancelled(this::handleReSendErrorsInTask)
                .build().execute();
    }

    private void handleReSendErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleReSendOnPost(String result) {
        Log.i("onPostLogin", result);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                TextView frag =
                        myView.findViewById(R.id.ConfirmedEmailMessage);
                frag.setText("Re-Send Sucessfull! please go back and try to log in again!");
            }
            else {
                EditText frag =
                        myView.findViewById(R.id.emailInputRe_Enter);
                frag.setError("Re-Send unsuccessful, please try again with another email.");
            }
        } catch (JSONException e) {
//It appears that the web service didn’t return a JSON formatted String
//or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void loadResendSuccess() {

    }


}
