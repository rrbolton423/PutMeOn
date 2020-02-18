package com.romellbolton.putmeon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TrackUtil {
    static public class TrackJSONParser {
        static ArrayList<Track> parseTracks(String in) throws JSONException {

            Log.i("TAGGY", "parseTracks: " + in);
            JSONObject root = new JSONObject(in);
            JSONArray JSONArrayTrack = null;
            if (root.has("results")) {
                JSONArrayTrack = root.getJSONObject("results").getJSONObject("trackmatches").getJSONArray("track");
                return getSearchedTracks(JSONArrayTrack);
            } else if (root.has("similartracks")) {
                JSONArrayTrack = root.getJSONObject("similartracks").getJSONArray("track");
                return getSimilarTracks(JSONArrayTrack);
            }
            return null;
        }

        static ArrayList<Track> getSearchedTracks(JSONArray JSONArrayTrack) throws JSONException {
            ArrayList<Track> trackArrayList = new ArrayList<Track>();
            for (int i = 0; i < JSONArrayTrack.length(); i++) {
                JSONObject JSONObjectTrack = JSONArrayTrack.getJSONObject(i);
                Track track = new Track();
                track.setName(JSONObjectTrack.getString("name"));
                track.setArtist(JSONObjectTrack.getString("artist"));
                track.setUrl(JSONObjectTrack.getString("url"));
                track.setSmallImageURL(JSONObjectTrack.getJSONArray("image").getJSONObject(0).getString("#text"));
                track.setLargeImageURL(JSONObjectTrack.getJSONArray("image").getJSONObject(2).getString("#text"));
                trackArrayList.add(track);
            }
            return trackArrayList;
        }
        static ArrayList<Track> getSimilarTracks(JSONArray JSONArrayTrack) throws JSONException {
            ArrayList<Track> trackArrayList = new ArrayList<Track>();
            for (int i = 0; i < JSONArrayTrack.length(); i++) {
                JSONObject JSONObjectTrack = JSONArrayTrack.getJSONObject(i);
                Track track = new Track();
                track.setName(JSONObjectTrack.getString("name"));
                track.setArtist(JSONObjectTrack.getJSONObject("artist").getString("name"));
                track.setUrl(JSONObjectTrack.getString("url"));
                track.setSmallImageURL(JSONObjectTrack.getJSONArray("image").getJSONObject(0).getString("#text"));
                track.setLargeImageURL(JSONObjectTrack.getJSONArray("image").getJSONObject(2).getString("#text"));
                trackArrayList.add(track);
            }
            return trackArrayList;
        }
    }

    static ArrayList<Track> getFavouriteTracks(JSONArray JSONArrayTracks) throws JSONException {
        ArrayList<Track> trackArrayList = new ArrayList<Track>();
        for (int i = 0; i < JSONArrayTracks.length(); i++) {
            JSONObject JSONObjectTrack = JSONArrayTracks.getJSONObject(i);
            Track track = new Track();
            track.setName(JSONObjectTrack.getString("name"));
            track.setArtist(JSONObjectTrack.getString("artist"));
            track.setUrl(JSONObjectTrack.getString("url"));
            track.setSmallImageURL(JSONObjectTrack.getString("smallImageURL"));
            track.setLargeImageURL(JSONObjectTrack.getString("largeImageURL"));
            trackArrayList.add(track);
        }
        return trackArrayList;

    }

    static ArrayList<Track> getTracksFromPref(SharedPreferences pref) throws JSONException {
        JSONArray jsonArray = new JSONArray(pref.getString("favourites", "[]"));
        ArrayList<Track> trackArrayList = new ArrayList<Track>();
        trackArrayList.addAll(getFavouriteTracks(jsonArray));
        return trackArrayList;
    }

    static void addTrackToPreferences(Track track, Context mContext) {

        String name = mContext.getPackageName();
        SharedPreferences pref = mContext.getApplicationContext().getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (pref.getString("favourites", null) == null) {
            editor.putString("favourites", "[]");
            editor.commit();
        }

        String jObj = pref.getString("favourites", null);
        int index = jObj.lastIndexOf(']');
        String jsonObject = null;

        if (jObj.lastIndexOf('}') == jObj.length() - 2) {
            jsonObject = jObj.substring(0, index).concat(", " + track.toString()) + "]";
        } else {
            jsonObject = jObj.substring(0, index).concat(track.toString()) + "]";
        }
        editor.putString("favourites", jsonObject);
        editor.commit();
    }

    static ArrayList<Track> removeTrackFromPreferences(Track track, Context mContext) throws JSONException {
        String name = mContext.getPackageName();
        SharedPreferences pref = mContext.getApplicationContext().getSharedPreferences(name, MODE_PRIVATE);

        ArrayList<Track> trackArrayList = new ArrayList<>();
        trackArrayList.addAll(getTracksFromPref(pref));
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        for (int i = 0; i < trackArrayList.size(); i++) {
            Track t = trackArrayList.get(i);
            if (t.getName().equalsIgnoreCase(track.getName())) {
                trackArrayList.remove(t);
            }
        }
        for (int i = 0; i < trackArrayList.size(); i++) {
            Track t = trackArrayList.get(i);
            addTrackToPreferences(t, mContext);
        }
        return trackArrayList;
    }
}
