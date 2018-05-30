package group7.tcss450.uw.edu.chatapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import group7.tcss450.uw.edu.chatapp.Front_End_Register_Login.LoginFragment;
import group7.tcss450.uw.edu.chatapp.LandingActivity;
import group7.tcss450.uw.edu.chatapp.R;


public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSettingsInterationListener mListener;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsFragment.OnSettingsInterationListener) {
            mListener = (SettingsFragment.OnSettingsInterationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inflate the layout for this fragment

//            SharedPreferences mSettings =  getActivity().getSharedPreferences("Themes", 0);
//            //SharedPrerefences Editor (to add content to SP)
//            SharedPreferences.Editor editor = mSettings.edit();
            Button rb= v.findViewById(R.id.rb1);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("onCLick:", String.valueOf(R.style.AppTheme));
                    //mListener.ThemeChange(R.style.AppTheme);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.AppTheme)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();

                }
            });

        Button rb2=v.findViewById(R.id.rb2);
            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("onCLick:", String.valueOf(R.style.RedLightBlue));
                    //mListener.ThemeChange(R.style.RedLightBlue);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.RedLightBlue)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();


                }
            });

            Button rb3=v.findViewById(R.id.rb3);
            rb3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mListener.ThemeChange(R.style.LimeTeal);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.LimeTeal)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();

                }
            });
        Button rb4= v.findViewById(R.id.rb4);
            rb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // mListener.ThemeChange(R.style.OrangeBrown);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.OrangeBrown)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();

                }
            });
        Button rb5= v.findViewById(R.id.rb5);
            rb5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mListener.ThemeChange(R.style.IndigoGrey);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.IndigoGrey)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();

                }
            });
        Button rb6= v.findViewById(R.id.rb6);
            rb6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mListener.ThemeChange(R.style.BluePurple);
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.BluePurple)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();

                }
            });
        Button rb7= v.findViewById(R.id.rb7);
            rb7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mListener.ThemeChange(R.style.YellowOrange)
                    SharedPreferences prefs =
                            getActivity().getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    prefs.edit().putString("Style", String.valueOf(R.style.YellowOrange)).apply();
                    Log.d("PREEEFS:", prefs.getString("Style", ""));
                    getActivity().recreate();

                }
            });


        return v;    }


    public interface OnSettingsInterationListener {
        void ThemeChange(int color);
    }
}
