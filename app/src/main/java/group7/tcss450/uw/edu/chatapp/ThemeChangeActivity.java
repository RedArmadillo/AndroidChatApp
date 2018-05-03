package group7.tcss450.uw.edu.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ThemeChangeActivity extends AppCompatActivity implements View.OnClickListener{
    ListView mListView;
    Button mChangeTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_change);
    }

    @Override
    public void onClick(View view) {

    }
}
