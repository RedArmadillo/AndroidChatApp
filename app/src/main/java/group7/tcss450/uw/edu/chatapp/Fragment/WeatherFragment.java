package group7.tcss450.uw.edu.chatapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendGetAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_weather, container, false);
        v.findViewById(R.id.chatSendButton).setOnClickListener(this::getWeatherByZip);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnWeatherFragmentInteractionListener(uri);
        }
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


    /***
     * Takes a zip code from the weather fragment zipcode EditText box and sends a GET request
     * to our weather service, setting the appropriate TextViews to the requested weather data.
     *
     * @param theButton The get weather by zipcode button
     */
    private void getWeatherByZip(final View theButton) {

        String zip = ((EditText) getView().findViewById(R.id.etWeatherZip))
                .getText().toString();

        Uri uri10Day = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_weather_base_url))
                .appendPath(getString(R.string.ep_weather_forecast))
                .appendQueryParameter(getString(R.string.param_weather_zip), zip)
                .appendQueryParameter(getString(R.string.param_weather_api),
                        getString(R.string.keys_weather_API))
                .build();

        new SendGetAsyncTask.Builder(uri10Day.toString(), null)
                .onPostExecute(this::endOfWeatherByZipTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void endOfWeatherByZipTask(final String result) {
        Log.d("response from weather service", result);

        try {
            JSONObject res = new JSONObject(result).getJSONObject("data");
            String currTemp = res.getString("temp");
            String maxTemp = res.getString("max_temp");
            String minTemp = res.getString("min_temp");
            String desc = res.getJSONObject("weather").getString("description");

            ((EditText) getView().findViewById(R.id.tvWeatherCond))
                    .setText("");

        } catch (JSONException e) {
            Log.d("endOfSend", "error");
            e.printStackTrace();
        }
    }

    private void handleError(final String msg) {
        Log.e("WeatherFragment Error", msg);
    }


}
