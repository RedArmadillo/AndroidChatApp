package group7.tcss450.uw.edu.chatapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import group7.tcss450.uw.edu.chatapp.Fragment.ChatFragment;
import group7.tcss450.uw.edu.chatapp.Utils.SettingMenuActivity;

public class ChatActivity extends SettingMenuActivity {
    public int mChatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            //if (findViewById(R.id.chatLayout) != null) {
            int chatId = getIntent().getIntExtra(getString(R.string.keys_json_chat_id_lowercase), 1);
            mChatId = chatId;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chatLayout, new ChatFragment(chatId))
                    .commit();
            //}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_menu:
                Toast.makeText(this,"Chat room: " + mChatId, Toast.LENGTH_LONG).show();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
