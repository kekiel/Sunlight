package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init Toolbar - the new ActionBar (aka App Bar)
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        List<String> mWeekForecast;

        public PlaceholderFragment() {
        }

        /**
         * A ViewHolder to be used by the new ForecastAdapter.
         * Holds references to the items in the list elements.
         */
        private class ForecastViewHolder extends RecyclerView.ViewHolder {

            private TextView mItemTextView;
            private String mForecastString;

            // constructor fetches the TextView reference to store it in a member variable
            public ForecastViewHolder(View itemView) {
                super(itemView);
                mItemTextView = (TextView) itemView.findViewById(R.id.list_item_forecast_textview);
            }

            // will be used by the adapter to apply a value to the TextView
            public void bindForecast(String forecast) {
                mForecastString = forecast;
                mItemTextView.setText(forecast);
            }
        }

        /**
         * Adapter for the RecyclerView.
         * Binds the values of mWeekForecast array to the list_item_forecast view.
         */
        private class ForecastAdapter extends RecyclerView.Adapter<ForecastViewHolder> {
            @Override
            public ForecastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                // when initially created, inflate the view and init the ViewHolder
                View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_item_forecast, viewGroup, false);
                return new ForecastViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ForecastViewHolder forecastViewHolder, int i) {
                // each time an item is to be displayed, make use of the ViewHolder to set the value
                String forecastString = mWeekForecast.get(i);
                forecastViewHolder.bindForecast(forecastString);
            }

            @Override
            public int getItemCount() {
                return mWeekForecast.size();
            }
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Create some dummy data for the ListView.  Here's a sample weekly forecast
            String[] data = {
                    "Mon 6/23 - Sunny - 31/17",
                    "Tue 6/24 - Foggy - 21/8",
                    "Wed 6/25 - Cloudy - 22/17",
                    "Thurs 6/26 - Rainy - 18/11",
                    "Fri 6/27 - Foggy - 21/10",
                    "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                    "Sun 6/29 - Sunny - 20/7"
            };
            mWeekForecast = new ArrayList<>(Arrays.asList(data));

            // Now that we have some dummy forecast data, create an Adapter for the RecyclerView.
            // The ForecastAdapter will take data from a source (like our dummy forecast) and
            // use it to populate the RecyclerView it's attached to.

            // inflate the basic view containing the RecyclerView element
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            // Get a reference to the RecyclerView, and attach a  adapter to it.
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_forecast);
            // attach the adapter
            recyclerView.setAdapter(new ForecastAdapter());
            // A RecyclerView.Adapter needs a LayoutManager
            // We simply use a LinearLayout, provided by the framework
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return rootView;
        }
    }
}
