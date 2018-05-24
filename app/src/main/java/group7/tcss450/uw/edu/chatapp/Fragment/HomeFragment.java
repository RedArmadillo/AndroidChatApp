package group7.tcss450.uw.edu.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telecom.Connection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import group7.tcss450.uw.edu.chatapp.Activities.ChatListActivity;
import group7.tcss450.uw.edu.chatapp.Activities.Connections.ConnectionsActivity;
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


    //private OnSuccessFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton weather = v.findViewById(R.id.weather);
        ImageButton chat = v.findViewById(R.id.chat);
        ImageButton chatlist = v.findViewById(R.id.chatroom);
        ImageButton cconnections = v.findViewById(R.id.connectionsButton);
        weather.setOnClickListener(
                view -> getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.landingContainer, new WeatherFragment()).addToBackStack(null)
                        .commit());
        chat.setOnClickListener(view2 -> getFragmentManager()
            .beginTransaction()
            .replace(R.id.landingContainer, new ChatFragment()).addToBackStack(null)
            .commit());

        chatlist.setOnClickListener(view3 -> {
            Intent intent = new Intent(getActivity(), ChatListActivity.class);
            startActivity(intent);
        });

        cconnections.setOnClickListener(view -> {
            Intent intent4 = new Intent(getActivity(), ConnectionsActivity.class);
            startActivity(intent4);
        });
        // Inflate the layout for this fragment
        return v;
    }

}

