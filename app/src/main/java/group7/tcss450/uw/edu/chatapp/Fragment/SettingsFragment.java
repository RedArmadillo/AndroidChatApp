package group7.tcss450.uw.edu.chatapp.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import group7.tcss450.uw.edu.chatapp.R;


public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        // Inflate the layout for this fragment

        RadioGroup rg =  v.findViewById(R.id.RadGrp);
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            SharedPreferences mSettings =  getActivity().getSharedPreferences("Themes", 0);
            //SharedPrerefences Editor (to add content to SP)
            SharedPreferences.Editor editor = mSettings.edit();
            switch (i){
                case 1:
                    getActivity().getApplication().setTheme(R.style.AppTheme);
                    editor.putInt("theme", R.style.AppTheme);
                    editor.apply();
                    break;
                case 2:
                    getActivity().getApplication().setTheme(R.style.RedLightBlue);
                    editor.putInt("theme", R.style.RedLightBlue);
                    editor.apply();
                    break;
                case 3:
                    getActivity().getApplication().setTheme(R.style.LimeTeal);
                    editor.putInt("theme", R.style.LimeTeal);
                    editor.apply();
                    break;
                case 4:
                    getActivity().getApplication().setTheme(R.style.OrangeBrown);
                    editor.putInt("theme", R.style.OrangeBrown);
                    editor.apply();
                    break;
                case 5:
                    getActivity().getApplication().setTheme(R.style.IndigoGrey);
                    editor.putInt("theme", R.style.IndigoGrey);
                    editor.apply();
                    break;
                case 6:
                    getActivity().getApplication().setTheme(R.style.BluePurple);
                    editor.putInt("theme", R.style.BluePurple);
                    editor.apply();
                    break;
                case 7:
                    getActivity().getApplication().setTheme(R.style.YellowOrange);
                    editor.putInt("theme", R.style.YellowOrange);
                    editor.apply();
                    break;

            }



        });





        return v;    }


}
