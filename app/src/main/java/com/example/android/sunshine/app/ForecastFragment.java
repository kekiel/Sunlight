/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates fetching the forecast and displaying it in a {@link RecyclerView} layout.
 */
public class ForecastFragment extends Fragment {

    List<String> mWeekForecast;

    public ForecastFragment() {
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

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

}