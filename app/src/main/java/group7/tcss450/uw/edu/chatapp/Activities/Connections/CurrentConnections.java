package group7.tcss450.uw.edu.chatapp.Activities.Connections;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import group7.tcss450.uw.edu.chatapp.Activities.MainActivity;
import group7.tcss450.uw.edu.chatapp.Async.SendPostAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentConnections extends Fragment {


    private View v;
    protected DialogInterface.OnClickListener confirmrejectdialogClickListener;
    protected DialogInterface.OnClickListener removedialogClickListener;
    protected CListViewAdapter cAdapter;

    protected AlertDialog.Builder builder;
    private String removeUser;
    private String requestUser;
    private String confirmUser;
    private String m_Text;
    private String rejectUser;

    private final ArrayList<String> cListFiltered;
    //private final Collection<? extends String> oListFiltered;
    //private final Collection<? extends String> iListFiltered;

    public CurrentConnections() {
        cListFiltered = new ArrayList<String>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_connections, container, false);
        GetContactTask task = new GetContactTask();
        v = rootView;
        task.execute("", "", "");
        // Inflate the layout for this fragment




        //END OF ADAPTER CODE

        ImageButton myRequestButton = (ImageButton) v.findViewById(R.id.AddContactButton);

        ListView con = v.findViewById(R.id.listConnections);
        ListView inc = v.findViewById(R.id.listIncoming);
        ListView out = v.findViewById(R.id.listOutgoing);

        con.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                       @Override
                                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                           removeUser = con.getItemAtPosition(i).toString();
                                           Log.d("RemoveUser = ", removeUser);
                                           getRemoveDialog();

                                       }
                                   }
        );

        inc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                       @Override
                                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                           confirmUser = con.getItemAtPosition(i).toString();
                                           rejectUser = con.getItemAtPosition(i).toString();
                                           Log.d("ConfirmationUser = ", confirmUser);
                                           getConfirmRejectDialog();

                                       }
                                   }
        );


        myRequestButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Enter a contacts username");

// Set up the input
            final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestUser = input.getText().toString();
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
                    JSONObject c = null;
                    try {
                        c = new JSONObject();
                        c.put("username_b", requestUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        c = new JSONObject((requestUser));
                        //TODO: THIS IS WHERE ERROR IS HAPPENING.
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Uri retrieve = new Uri.Builder()
                            .scheme("https")
                            .authority(getString(R.string.ep_lab_url))
                            .appendPath(getString(R.string.ep_connections))
                            .appendPath(currentUser)
                            .appendPath(getString(R.string.ep_request_connection))
                            .build();
                    new SendPostAsyncTask.Builder(retrieve.toString(), c)
                            .onPostExecute(this::onSPostExecute)
                            .onCancelled(this::handleErrorsInTask)
                            .build().execute();
                }

                protected void handleErrorsInTask(String result) {
                    Log.e("ASYNCT_TASK_ERROR", result);
                }

                @SuppressLint("SetTextI18n")
                protected void onSPostExecute(String s) {
                    Log.d("result", s);
                    JSONObject resultsJSON = null;
                    try {
                        resultsJSON = new JSONObject(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        boolean success = resultsJSON.getBoolean("success");
                        if (success) {
                            ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("Request has been sucessfully sent");
                            ((TextView) v.findViewById(R.id.ERRORTEXT)).setTextColor(Color.rgb(0,255,0));


                        } else {
                            ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("We could not find that username");
                            ((TextView) v.findViewById(R.id.ERRORTEXT)).setTextColor(Color.rgb(255,0,0));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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



        removedialogClickListener = (dialog, which) -> {
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

        confirmrejectdialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ConfirmContactTask task1 = new ConfirmContactTask();
                    task1.execute("", "", "");
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    RejectContactTask task2 = new RejectContactTask();
                    task2.execute("", "", "");
                    break;
            }
        };

        return rootView;
    }

    public void getRemoveDialog() {
        builder =  new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to remove this contact?").setPositiveButton("Yes", removedialogClickListener)
                .setNegativeButton("NO", removedialogClickListener).show();
    }

    public void getConfirmRejectDialog() {
        builder =  new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to confirm this contact?").setPositiveButton("Yes", confirmrejectdialogClickListener)
                .setNegativeButton("Reject", confirmrejectdialogClickListener).show();
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
                    //.appendQueryParameter("username", removeUser)
                    .build();
            try {
                //removeUser = null;
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
                JSONArray inc = (JSONArray) resultsJSON.get("incoming");
                JSONArray out = (JSONArray) resultsJSON.get("outgoing");
                updateList(verified,inc,out);

                /**
                 * RESETING ADAPTER TO SEARCH CONTACTS
                 */
                SearchView search = (SearchView) v.findViewById(R.id.searchView);
                cAdapter = new CListViewAdapter(getActivity());
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        ListView la = v.findViewById(R.id.listConnections);
                        la.setAdapter(cAdapter);
                        String text = s;
                        cAdapter.filter(text);
                        return false;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateList(JSONArray verified, JSONArray incoming, JSONArray outgoing) {
        String[] l1 = verified.toString().split(",");
        String[] removal = new String[l1.length];
        for (int x = 0; x < l1.length; x++) {
            removal[x] = l1[x].replace("[", "").replace("]", "").replace("\"", "");
        }
        ListView cList = v.findViewById(R.id.listConnections);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, removal);
        cList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        cListFiltered.addAll(Arrays.asList(removal));


        //////////////////////////////////////////////////////////


        String[] l5 = incoming.toString().split(",");
        String[] sorry = new String[l5.length];
        for (int x = 0; x < l5.length; x++) {
            if(l5[x].equals("[]")) {
                sorry[x] = l5[x].replace("[", "").replace("]", "Empty");
            } else {
                sorry[x] = l5[x].replace("[", "").replace("]", "")
                        .replace("\"", "")  + " (pending)";;
            }
        }
        ListView iList = v.findViewById(R.id.listIncoming);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, sorry);
        iList.setAdapter(adapter5);
        adapter5.notifyDataSetChanged();



        /////////////////////////////////////////////////////////////////////////////////////////


        String[] l3 = outgoing.toString().split(",");
        String[] news = new String[l3.length];
        for (int x = 0; x < l3.length; x++) {
            if(l3[x].equals("[]")) {
                news[x] = l3[x].replace("[", "").replace("]", "Empty");
            } else {
                news[x] = l3[x].replace("[", "").replace("]", "")
                        .replace("\"", "")  + " (pending)";;
            }
        }
        ListView oList = v.findViewById(R.id.listOutgoing);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, news);
        oList.setAdapter(adapter3);
        adapter3.notifyDataSetChanged();

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
            JSONObject c = null;
            try {
                c = new JSONObject();
                c.put("username_b", removeUser);
                removeUser = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                c = new JSONObject();
                c.put("username_b", removeUser);
                //TODO: THIS IS WHERE ERROR IS HAPPENING.
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .appendPath(getString(R.string.ep_remove_connection))
                    .build();
            new SendPostAsyncTask.Builder(retrieve.toString(), c)
                    .onPostExecute(this::onPostRExecute)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
            return response;
        }

        private void handleErrorsInTask(String s) {
            Log.d("ERROR IN TASK", s);
        }

        protected void onPostRExecute(String s) {
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
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ConfirmContactTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String response = "";
            HttpURLConnection urlConnection = null;
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
            JSONObject c = null;
            try {
                c = new JSONObject();
                c.put("username_b", confirmUser);
                confirmUser = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                c = new JSONObject((requestUser));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .appendPath(getString(R.string.ep_confirm_connection))
                    .build();
            new SendPostAsyncTask.Builder(retrieve.toString(), c)
                    .onPostExecute(this::onPostExecute)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
            return response;
        }

        private void handleErrorsInTask(String s) {
            Log.d("ERROR IN TASK", s);
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
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class RejectContactTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String response = "";
            HttpURLConnection urlConnection = null;
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
            JSONObject c = null;
            try {
                c = new JSONObject();
                c.put("username_b", rejectUser);
                confirmUser = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                c = new JSONObject((rejectUser));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri retrieve = new Uri.Builder()
                    .scheme("https")
                    .authority(getString(R.string.ep_lab_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(currentUser)
                    .appendPath(getString(R.string.ep_reject_connection))
                    .build();
            new SendPostAsyncTask.Builder(retrieve.toString(), c)
                    .onPostExecute(this::onPostExecute)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
            return response;
        }

        private void handleErrorsInTask(String s) {
            Log.d("ERROR IN TASK", s);
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
                    ((TextView) v.findViewById(R.id.ERRORTEXT)).setText("Contact Rejected!");
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

    public class CListViewAdapter extends BaseAdapter {


        // Declare Variables

        Context mContext;
        LayoutInflater inflater;
        private ArrayList<String> arraylist;

        public CListViewAdapter(Context context ) {
            mContext = context;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<String>();
            this.arraylist.addAll(cListFiltered);
        }

        public class ViewHolder {
            TextView name;
        }

        @Override
        public int getCount() {
            return cListFiltered.size();
        }

        @Override
        public String getItem(int position) {
            return cListFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listview_item, null);
                // Locate the TextViews in listview_item.xml
                holder.name = (TextView) view.findViewById(R.id.name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.name.setText(cListFiltered.get(position));
            return view;
        }

        // Filter Class
        public void filter(String charText) {
            Log.d("ArrayList[0]", arraylist.get(0));
            charText = charText.toLowerCase(Locale.getDefault());
            cListFiltered.clear();
            if (charText.length() == 0) {
                cListFiltered.addAll(arraylist);
            } else {
                for (String wp : arraylist) {
                    if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                        cListFiltered.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }


}
