package group7.tcss450.uw.edu.chatapp.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group7.tcss450.uw.edu.chatapp.R;


public class WeatherFragment extends Fragment {
    private TextView myTextView;

    private OnFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);
        myTextView = (TextView) v.findViewById(R.id.testIcon);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        myTextView.setTypeface(typeface);
        myTextView.setText(getActivity().getString(R.string.weather_cloudy));
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface  OnFragmentInteractionListener{
        // TODO: Update argument type and name
        void OnWeatherFragmentInteractionListener(Uri uri);
    }
}
