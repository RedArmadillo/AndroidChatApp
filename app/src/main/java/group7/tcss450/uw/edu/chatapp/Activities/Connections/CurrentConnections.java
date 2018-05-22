package group7.tcss450.uw.edu.chatapp.Activities.Connections;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
public class CurrentConnections extends Fragment {


    private View v;
    protected DialogInterface.OnClickListener dialogClickListener;
    protected AlertDialog.Builder builder;
    private String removeUser;
    private String requestUser;
    private String m_Text;
    private ImageButton myRequestButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_connections, container, false);
        GetContactTask task = new GetContactTask();
        v = rootView;
        task.execute("", "", "");
        // Inflate the layout for this fragment

        myRequestButton = (ImageButton) v.findViewById(R.id.AddContactButton);

        myRequestButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Enter a contacts username");

// Set up the input
            final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestUser = input.getText().toString();
                    RequestContactTask task1 = new RequestContactTask();
                    task1.execute("", "", "");
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    RemoveContactTask task1 = new RemoveContactTask();
                    task1.execute("", "", "");
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.cancel();
                    break;
            }
        };

        return rootView;
    }

    public void getDialog() {
        builder =  new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to remove this contact?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("NO", dialogClickListener).show();
    }

    @SuppressLint("StaticFieldLeak")
    class GetContactTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;


            SharedPreferences prefs =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");

            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .appendQueryParameter("username", removeUser)
                    .build();
            try {
                removeUser = null;
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
                JSONArray verified = (JSONArray) resultsJSON.get("verified");
                updateList(verified);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList(JSONArray verified) {
        String[] l1 = verified.toString().split(",");
        for (String a : l1) {
            a = a.replace("[", "");
            a = a.replace("]", "");
            a.replace("\"", "");
        }

        ListView cList = v.findViewById(R.id.listConnections);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, l1);
        cList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("StaticFieldLeak")
    class RemoveContactTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String response = "";
            HttpURLConnection urlConnection = null;


            SharedPreferences prefs =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");

            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .appendPath(getString(R.string.ep_remove_connection))
                    .appendQueryParameter("username", removeUser)
                    .build();
            try {
                removeUser = null;
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
            JSONObject resultsJSON = null;
            try {
                resultsJSON = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                assert resultsJSON != null;
                boolean success = resultsJSON.getBoolean("success");
                if (success) {
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("Contact has been sucessfully removed.");
                    wait(10);
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("");
                } else {
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("Internal Error, please try later.");
                    wait(10);
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class RequestContactTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String response = "";
            HttpURLConnection urlConnection = null;


            SharedPreferences prefs =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");

            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .appendPath(getString(R.string.ep_request_connection))
                    .appendQueryParameter("username", requestUser)
                    .build();
            try {
                requestUser = null;
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
            JSONObject resultsJSON = null;
            try {
                resultsJSON = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                boolean success = resultsJSON.getBoolean("success");
                if (success) {
                    getDialog();
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("Request has been sucessfully sent");
                    wait(10);
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("");



                } else {
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("We could not find that username");
                    wait(10);
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
