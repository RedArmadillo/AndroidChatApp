package group7.tcss450.uw.edu.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import group7.tcss450.uw.edu.chatapp.Activities.ChatActivity;
import group7.tcss450.uw.edu.chatapp.Activities.ChatListActivity;
import group7.tcss450.uw.edu.chatapp.Activities.Connections.ConnectionsActivity;
import group7.tcss450.uw.edu.chatapp.Fragment.HomeFragment;
import group7.tcss450.uw.edu.chatapp.Fragment.SettingsFragment;
import group7.tcss450.uw.edu.chatapp.Fragment.WeatherFragment;

public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WeatherFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            if (findViewById(R.id.landingContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.landingContainer, new WeatherFragment())
                        .commit();
            }
        }

        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String currentUser = prefs.getString(getString(R.string.keys_prefs_username), "");
        View headerView = navigationView.getHeaderView(0);
        TextView headerUser = (TextView) headerView.findViewById(R.id.nav_header_username_display);
        headerUser.setText(currentUser);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.landingChat) {
            Intent intent = new Intent(this, ChatActivity.class);
            int chatId = 1;
            intent.putExtra(getString(R.string.keys_json_chat_id_lowercase), chatId);
            startActivity(intent);
            //loadFragment(new ChatFragment());
        } else if (id == R.id.landingConnections) {
            Intent intent = new Intent(getApplicationContext(), ConnectionsActivity.class);
            startActivity(intent);
        } else if (id == R.id.landingHome) {
            loadFragment(new HomeFragment());
        } else if (id == R.id.landingWeather) {
            loadFragment(new WeatherFragment());
        } else if (id == R.id.landingSetting) {
            //loadFragment(new SettingsFragment());
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("TOKEN", "New token: " + refreshedToken);
//            Intent intent = new Intent(getApplicationContext(), SettingMenuActivity.class);
//            startActivity(intent);
        } else if (id == R.id.landingLogout) {
            onLogout();
        } else if (id == R.id.landingChatList) {
            Intent intent = new Intent(this, ChatListActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onLogout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.keys_prefs_username));
        prefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_logged_in),
                false)
                .apply();
//the way to close an app programmatically
        finishAndRemoveTask();
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.landingContainer, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }



    @Override
    public void OnSettingFragmentInteractionListener(Uri uri) {

    }

    @Override
    public void OnWeatherFragmentInteractionListener(Uri uri) {

    }
}
