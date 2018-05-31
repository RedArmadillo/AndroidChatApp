package group7.tcss450.uw.edu.chatapp.Activities.Connections;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

import group7.tcss450.uw.edu.chatapp.Front_End_Register_Login.LoginFragment;
import group7.tcss450.uw.edu.chatapp.R;

public class ConnectionsActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String u_name = getSharedPreferences(getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).getString("Style", "");
        if (u_name == "") {
            u_name = String.valueOf(R.style.AppTheme);
            Log.d("Restart", "DEFAULT THEME");

        }
        Log.d("Shared Prefs Style :", u_name);
        ThemeChange(Integer.valueOf(u_name));

        setContentView(R.layout.activity_connections);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.FLAY,
                        new CurrentConnections(),
                       "cFrag")
                .commit();



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void ThemeChange(int color) {
        Log.d("Theme:", String.valueOf(color));
        //ContextThemeWrapper w = new ContextThemeWrapper(this, color);
        setTheme(color);
        getApplication().setTheme(color);
    }



}
