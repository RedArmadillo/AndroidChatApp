package group7.tcss450.uw.edu.chatapp.Activities.Connections;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import group7.tcss450.uw.edu.chatapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class OutgoingConnections extends Fragment {

    private View v;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_outgoing_connections, container, false);
        v = rootView;
        GetWebServiceTask task = new GetWebServiceTask();
        task.execute("", "", "");

        return rootView;
    }

    class GetWebServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            //instead of using a hard coded (found in end_points.xml) url for our web service
            // address, here we will build the URL from parts. This can be helpful when
            // sending arguments via GET. In this example, we are sending plain text.
            String url = strings[0];
            String endPoint = strings[1];
            String args = strings[2];

            SharedPreferences prefs =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");

            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .build();
            try {
                URL urlObject = new URL(retrieve.toString());
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject resultsJSON = new JSONObject(s);
                JSONArray outgoing = (JSONArray) resultsJSON.get("outgoing");

                Log.d("outgoing", outgoing.toString());
                updateList(outgoing);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList(JSONArray outgoing) {


        String[] l3 = outgoing.toString().split(",");
        String[] removal = new String[l3.length];
        for (int x = 0; x < l3.length; x++) {
            removal[x] = l3[x].replace("[", "").replace("]", "").replace("\"", "") + " (pending)";
        }
        ListView oList = v.findViewById(R.id.outgoingListConnectionList);



        ArrayAdapter<String> adapter3 = new ArrayAdapter<>( getActivity(), android.R.layout.simple_list_item_1, removal);
        oList.setAdapter(adapter3);
        adapter3.notifyDataSetChanged();

    }

}
