package com.romellbolton.putmeon;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

class TrackAsyncTask extends AsyncTask<String, Void, ArrayList<Track>> {

    IData activity;

    public TrackAsyncTask(IData activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected ArrayList<Track> doInBackground(String... params) {
        String stURL = params[0];
        BufferedReader reader = null;
        try {
            URL url = new URL(stURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statusCode = con.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line);
                return TrackUtil.TrackJSONParser.parseTracks(sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(/*progress*/Void... values) { }

    @Override
    protected void onPostExecute(/*result*/ArrayList<Track> result) {
        activity.setUpData(result);
    }

    public static interface IData {
        public void setUpData(ArrayList<Track> trackArrayList);
    }
}
