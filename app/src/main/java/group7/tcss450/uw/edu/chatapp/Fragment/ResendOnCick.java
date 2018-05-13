package group7.tcss450.uw.edu.chatapp.Fragment;

import android.net.Uri;
import android.view.View;

import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;

class ResendOnCick implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        //Takes Email as a paramater
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(view.getContext().getString(R.string.ep_base_url))
                .appendPath(view.getContext().getString(R.string.ep_reverify))
                .build();

        JSONObject msg = new JSONObject();
        //Get Email from Client.
        //msg.put

//        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                .onPostExecute(this::handleLoginOnPost)
//                .onCancelled(this::handleErrorsInTask)
//                .build().execute();
    }
}
