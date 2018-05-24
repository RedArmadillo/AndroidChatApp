package group7.tcss450.uw.edu.chatapp.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import group7.tcss450.uw.edu.chatapp.Models.Forecast;
import group7.tcss450.uw.edu.chatapp.R;

/***
 * Adapter class for the extended forecast recyclerview
 */
public class WeatherViewAdapter extends RecyclerView.Adapter<WeatherViewAdapter.ViewHolder> {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Forecast> mForecasts;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    // Provide a suitable constructor (depends on the kind of dataset)
    public WeatherViewAdapter(List<Forecast> myDataset) {
        mForecasts = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WeatherViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_list_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Forecast forecast = mForecasts.get(position);
        holder.mDay.setText(forecast.getDay());
        holder.mHighLow.setText(forecast.getHighLow());
        holder.mConditions.setText(forecast.getConditions());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mForecasts.size();
    }

    public void updateData(List<Forecast> forecastList) {
        mForecasts.clear();
        mForecasts.addAll(forecastList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView mDay, mHighLow, mConditions;

        ViewHolder (View view) {
            super(view);
            mView = view;
            mDay = view.findViewById(R.id.tvForecastRowDay);
            mHighLow = view.findViewById(R.id.tvForecastRowTemp);
            mConditions = view.findViewById(R.id.tvForecastRowCond);
        }

    }
}
