package com.romellbolton.putmeon.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.daprlabs.cardstack.SwipeDeck;
import com.romellbolton.putmeon.R;
import com.romellbolton.putmeon.model.Track;
import com.romellbolton.putmeon.util.AppStatus;
import com.romellbolton.putmeon.viewmodel.TrackViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class TrackRecommendationActivity extends AppCompatActivity {

    private static final String TAG = TrackRecommendationActivity.class.getSimpleName();
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private ArrayList<Track> mUsersRecentArtist = new ArrayList<>();
    private LinkedList<Track> tracks = new LinkedList<>();
    private Call call;
    private SwipeDeck cardStack;
    SwipeDeckAdapter adapter = new SwipeDeckAdapter(null, this);
    private Button newSuggestionButton;
    private String accessToken = "accessToken";
    private String randomArtistID;
    private String randomTrackID;
    private ArrayList<String> artistNames = new ArrayList<>();
    private ArrayList<String> songNames = new ArrayList<>();
    private ArrayList<String> albumImgURLs = new ArrayList<>();
    private ArrayList<String> albumURLs = new ArrayList<>();
    public String currentArtist;
    public String currentSong;
    private TrackViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_recommendation);

        postViewModel = ViewModelProviders.of(this).get(TrackViewModel.class);
        accessToken = getIntent().getStringExtra("accessToken");
        cardStack = findViewById(R.id.swipe_deck);
        newSuggestionButton = findViewById(R.id.new_suggestions_button);
        newSuggestionButton.setOnClickListener(view -> {
            if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                fetchRandomFavoriteSpotifyArtist();
            } else {
                Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        if (AppStatus.getInstance(this).isOnline()) {
            fetchRandomFavoriteSpotifyArtist();
        } else {
            Toast.makeText(this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }

    public void fetchRandomFavoriteSpotifyArtist() {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/recently-played")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        cancelCall();
        call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) {
                try {
                    if (response.isSuccessful()) {
                        tracks = null;
                        songNames.clear();
                        artistNames.clear();
                        albumImgURLs.clear();
                        albumURLs.clear();
                        adapter.notifyDataSetChanged();

                        final JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());

                        mUsersRecentArtist = getArtists(jsonObject);
                        if (mUsersRecentArtist.size() == 0) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), R.string.no_spotify_listening_history, Toast.LENGTH_LONG).show());
                            return;
                        }

                        Random random = new Random();
                        randomArtistID = mUsersRecentArtist.get(random.nextInt(mUsersRecentArtist.size())).getArtistID();
                        randomTrackID = mUsersRecentArtist.get(random.nextInt(mUsersRecentArtist.size())).getSongID();
                        tracks = recommendationsOnSeed(randomArtistID, randomTrackID);
                        Collections.shuffle(tracks);

                        for (int i = 0; i < tracks.size(); i++) {
                            songNames.add(tracks.get(i).getName());
                            artistNames.add(tracks.get(i).getArtist());
                            albumImgURLs.add(tracks.get(i).getCoverURL640x636());
                            albumURLs.add(tracks.get(i).getURL());
                        }

                        runOnUiThread(() -> {
                            adapter = new SwipeDeckAdapter(artistNames, getApplicationContext());
                            cardStack.setAdapter(adapter);
                        });


                        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
                            @Override
                            public void cardSwipedLeft(int position) {
                                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                            }

                            @Override
                            public void cardSwipedRight(int position) {
                                postViewModel.savePost(tracks.get(position));
                                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                            }

                            @Override
                            public void cardsDepleted() {
                                Log.i("MainActivity", "no more cards");
                            }

                            @Override
                            public void cardActionDown() { }

                            @Override
                            public void cardActionUp() { }
                        });

                    } else {
                        alertUserAboutError();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception caught: ", e);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception caught: ", e);
                }
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.e("TAG", accessToken);
            }
        });
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    public ArrayList<Track> getArtists(JSONObject jsonObject) {
        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    JSONObject jsonAlbum = json.getJSONObject("track");
                    String trackName = jsonAlbum.getString("name");

                    String artistName = (String) jsonAlbum.getJSONArray("artists").getJSONObject(0).get("name");
                    String artistID = (String) jsonAlbum.getJSONArray("artists").getJSONObject(0).get("id");
                    String trackID = jsonAlbum.getString("id");

                    Track track_object = new Track(artistName, trackName, null, null, artistID, trackID, null);
                    mUsersRecentArtist.add(track_object); //to be used in Ui later
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mUsersRecentArtist;
    }

    public void respondToSong(String url, String image, String songName, String albumName, String artistName) {
        if (url.contains("null")) {
            Toast.makeText(this, R.string.no_preview_available, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = TrackActivity.newIntent(this, new Track(artistName, songName, null, image, null, null, url));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites:
                Intent intent = new Intent(this, FavoriteTracksActivity.class);
                startActivity(intent);
            case R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public LinkedList<Track> recommendationsOnSeed(String randomArtistID, String randomTrackID) throws IOException, JSONException {
        URL url = new URL("https://api.spotify.com/v1/recommendations?market=US&seed_artists="
                + randomArtistID + "&seed_tracks=" + randomTrackID + "&min_energy=0.4&min_popularity=50");
        System.out.println("GetRecURL: " + url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(false);
        con.setDoInput(true);

        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        int status = con.getResponseCode();
        Reader streamReader;
        if (status == 400) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        BufferedReader br = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder str = new StringBuilder();
        while ((inputLine = br.readLine()) != null) {
            str.append(inputLine);
        }
        br.close();
        con.disconnect();

        String recStr = str.toString();
        JSONObject JsonSpotifyRecommendations = new JSONObject(recStr);
        System.out.println(recStr);
        JSONArray recs = JsonSpotifyRecommendations.getJSONArray("tracks");
        LinkedList<Track> recommendedList = new LinkedList<>();

        for (int i = 0; i < recs.length(); i++) {
            JSONArray artistNameArr = (JSONArray) recs.getJSONObject(i).get("artists");
            String artistName = artistNameArr.getJSONObject(0).getString("name");
            String artistID = artistNameArr.getJSONObject(0).getString("id");

            String songName = (String) recs.getJSONObject(i).get("name");

            JSONObject imagesArr = (JSONObject) recs.getJSONObject(i).get("album");
            JSONArray tmpImageArr = imagesArr.getJSONArray("images");
            String CoverURL640x636 = tmpImageArr.getJSONObject(0).getString("url");

            imagesArr = (JSONObject) recs.getJSONObject(i).get("album");
            tmpImageArr = imagesArr.getJSONArray("images");
            String CoverURL64x64 = tmpImageArr.getJSONObject(2).getString("url");

            String songID = recs.getJSONObject(i).getString("id");
            String previewUrl = recs.getJSONObject(i).getString("preview_url");

            recommendedList.add(new Track(artistName, songName, CoverURL64x64, CoverURL640x636, artistID, songID, previewUrl));
        }
        return recommendedList;

        // TODO: When swiped right, save track to shared preferences, when swiped left, do nothing
        // TODO: Set up SuggestedTrack Adapter screen with list of all favorite songs, allow users to play 30 second clips of it
        // TODO: Set up Favorites / List View Activity displaying the saved suggestedTracks being loaded from device storage
        // TODO: Access favorites list by way of a menu button
    }


    public class SwipeDeckAdapter extends BaseAdapter {

        private List<String> artistName;
        private Context context;

        SwipeDeckAdapter(ArrayList<String> artistNames, Context context) {
            this.artistName = artistNames;
            this.context = context;
        }

        @Override
        public int getCount() {
            return artistName.size();
        }

        @Override
        public Object getItem(int position) {
            return artistName.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.track_card_view, parent, false);
            }

            ((TextView) v.findViewById(R.id.song_name)).setText(songNames.get(position));
            ((TextView) v.findViewById(R.id.artist_name)).setText(artistName.get(position));
            currentArtist = ((TextView) v.findViewById(R.id.artist_name)).getText().toString();
            currentSong = ((TextView) v.findViewById(R.id.song_name)).getText().toString();

            Glide.with(context)
                    .load(albumImgURLs.get(position))
                    .into(((ImageView) v.findViewById(R.id.song_image)));

            v.setOnClickListener(view -> {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    respondToSong(albumURLs.get(position), albumImgURLs.get(position), songNames.get(position), null, artistName.get(position));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            });

            return v;
        }
    }

}



