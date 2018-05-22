package group7.tcss450.uw.edu.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import group7.tcss450.uw.edu.chatapp.Activities.ChatListActivity;
import group7.tcss450.uw.edu.chatapp.R;
import group7.tcss450.uw.edu.chatapp.Utils.SettingMenuActivity;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton weather;
    private ImageButton chat;
    private ImageButton Chatlist;
    private ImageButton settings;


    //private OnSuccessFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        weather = v.findViewById(R.id.weatherButton);
        chat = v.findViewById(R.id.chatButton2);
        Chatlist = v.findViewById(R.id.chatRoomButton);
        settings = v.findViewById(R.id.settingButton2);
        weather.setOnClickListener(
                view -> getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FLAY, new WeatherFragment()).addToBackStack(null)
                        .commit());

        chat.setOnClickListener(view -> getFragmentManager()
                .beginTransaction()
                .replace(R.id.FLAY, new ChatFragment()).addToBackStack(null)
                .commit());

        Chatlist.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChatListActivity.class);
            startActivity(intent);
        });

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SettingMenuActivity.class);
            startActivity(intent);
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}

