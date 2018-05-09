package group7.tcss450.uw.edu.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import group7.tcss450.uw.edu.chatapp.Fragment.ChatFragment;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if (savedInstanceState == null) {
            //if (findViewById(R.id.chatLayout) != null) {
            int chatId = getIntent().getIntExtra(getString(R.string.keys_json_chat_id_lowercase), 1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chatLayout, new ChatFragment(chatId))
                    .commit();
            //}
        }
    }
}
