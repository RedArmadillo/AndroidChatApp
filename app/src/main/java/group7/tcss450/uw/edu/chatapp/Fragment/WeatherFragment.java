package group7.tcss450.uw.edu.chatapp.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group7.tcss450.uw.edu.chatapp.Async.SendGetAsyncTask;
import group7.tcss450.uw.edu.chatapp.R;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    private static final int MY_PERMISSIONS_LOCATIONS = 814;

    private OnFragmentInteractionListener mListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        
        View v = inflater.inflate(R.layout.fragment_weather, container, false);
        v.findViewById(R.id.btnWeatherZip).setOnClickListener(this::getWeatherByZip);
        v.findViewById(R.id.btnWeatherLoc).setOnClickListener(this::getWeatherByCurrentLoc);
        v.findViewById(R.id.btnWeatherMap).setOnClickListener(this::getWeatherByPlacePicker);
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
                .appendPath(getString(R.string.ep_weather_v2_0))
                .appendPath(getString(R.string.ep_weather_forecast))
                .appendPath(getString(R.string.ep_weather_daily))
                .appendQueryParameter(getString(R.string.param_weather_zip), zip)
                .appendQueryParameter(getString(R.string.param_weather_units), "I")
                .appendQueryParameter(getString(R.string.param_weather_api),
                        getString(R.string.keys_weather_API))
                .build();

        new SendGetAsyncTask.Builder(uri10Day.toString(), new JSONObject())
                .onPostExecute(this::endOfWeatherByZipTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /***
     * Callback method called by getWeatherByZip
     * @param result
     */
    private void endOfWeatherByZipTask(final String result) {
        Log.d("response from weather service", result);

        try {
            JSONArray data = new JSONObject(result).getJSONArray("data");
            JSONObject res = data.getJSONObject(0);
            String currTemp = res.getString("temp");
            String maxTemp = res.getString("max_temp");
            String minTemp = res.getString("min_temp");
            String desc = res.getJSONObject("weather").getString("description");

            ((TextView) getView().findViewById(R.id.tvWeatherTemp)).setText(currTemp);
            ((TextView) getView().findViewById(R.id.tvWeatherHighLow)).setText(
                    maxTemp + " / " + minTemp
            );
            ((TextView) getView().findViewById(R.id.tvWeatherCond)).setText(desc);

        } catch (JSONException e) {
            Log.d("endOfSend", "error");
            e.printStackTrace();
        }
    }

    /***
     * Takes the device's current location and sends a GET request
     * to our weather service, setting the appropriate TextViews to the requested weather data.
     *
     * @param theButton The location button
     */
    private void getWeatherByCurrentLoc(final View theButton) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(),
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                endOfGetWeatherByCurrentLocTask(location);
                                Log.d("Weather by current loc",
                                        Double.toString(location.getLatitude())
                                        + " " + Double.toString(location.getLatitude())
                                );

                            } else {
                                Log.d("Weather by current loc", "last location is null");
                            }
                        }
                    });
        }
    }

    /***
     * Callback method called by getWeatherByCurrentLoc
     * @param theLocation
     */
    public void endOfGetWeatherByCurrentLocTask (Location theLocation) {
        Location loc = theLocation;
        String lat = Double.toString(loc.getLatitude());
        String lon = Double.toString(loc.getLongitude());

        Uri uri10Day = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_weather_base_url))
                .appendPath(getString(R.string.ep_weather_v2_0))
                .appendPath(getString(R.string.ep_weather_forecast))
                .appendPath(getString(R.string.ep_weather_daily))
                .appendQueryParameter(getString(R.string.param_weather_lat), lat)
                .appendQueryParameter(getString(R.string.param_weather_lon), lon)
                .appendQueryParameter(getString(R.string.param_weather_units), "I")
                .appendQueryParameter(getString(R.string.param_weather_api),
                        getString(R.string.keys_weather_API))
                .build();

        new SendGetAsyncTask.Builder(uri10Day.toString(), new JSONObject())
                .onPostExecute(this::endOfWeatherByZipTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void getWeatherByPlacePicker (final View theButton) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), 1);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Location Picker", "Play services not available");
            Log.e("Location Picker", e.toString());
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Location Picker", "Play services repairable exception");
            Log.e("Location Picker", e.toString());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(getContext(), data);
                double lat = place.getLatLng().latitude;
                double lon = place.getLatLng().longitude;
                Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLatitude(lat);
                loc.setLongitude(lon);
                endOfGetWeatherByCurrentLocTask(loc);

            }
        }
    }


    /***
     * Generic error handler helper
     * @param msg
     */
    private void handleError(final String msg) {
        Log.e("WeatherFragment Error", msg);
    }


}
