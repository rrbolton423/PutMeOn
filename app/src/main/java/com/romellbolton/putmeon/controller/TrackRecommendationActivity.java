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
    public String mCurrentArtist;
    private ArrayList<Track> mUsersRecentArtist = new ArrayList<>();
    public String mCurrentSong;
    SwipeDeckAdapter mAdapter = new SwipeDeckAdapter(null, this);
    private Call mCall;
    private LinkedList<Track> mTracks = new LinkedList<>();
    private SwipeDeck mCardStack;
    private ArrayList<String> mArtistNames = new ArrayList<>();
    private ArrayList<String> mSongNames = new ArrayList<>();
    private ArrayList<String> mAlbumImgURLs = new ArrayList<>();
    private ArrayList<String> mAlbumURLs = new ArrayList<>();
    private TrackViewModel mPostViewModel;
    private Button mNewSuggestionButton;
    private String mAccessToken = "accessToken";
    private String mRandomArtistID;
    private String mRandomTrackID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_recommendation);

        mPostViewModel = ViewModelProviders.of(this).get(TrackViewModel.class);
        mAccessToken = getIntent().getStringExtra("accessToken");
        mCardStack = findViewById(R.id.swipe_deck);
        mNewSuggestionButton = findViewById(R.id.new_suggestions_button);
        mNewSuggestionButton.setOnClickListener(view -> {
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
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public void fetchRandomFavoriteSpotifyArtist() {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/recently-played")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) {
                try {
                    if (response.isSuccessful()) {
                        mTracks = null;
                        mSongNames.clear();
                        mArtistNames.clear();
                        mAlbumImgURLs.clear();
                        mAlbumURLs.clear();
                        mAdapter.notifyDataSetChanged();

                        final JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());

                        mUsersRecentArtist = getArtists(jsonObject);
                        if (mUsersRecentArtist.size() == 0) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), R.string.no_spotify_listening_history, Toast.LENGTH_LONG).show());
                            return;
                        }

                        Random random = new Random();
                        mRandomArtistID = mUsersRecentArtist.get(random.nextInt(mUsersRecentArtist.size())).getArtistID();
                        mRandomTrackID = mUsersRecentArtist.get(random.nextInt(mUsersRecentArtist.size())).getSongID();
                        mTracks = recommendationsOnSeed(mRandomArtistID, mRandomTrackID);
                        Collections.shuffle(mTracks);

                        for (int i = 0; i < mTracks.size(); i++) {
                            mSongNames.add(mTracks.get(i).getName());
                            mArtistNames.add(mTracks.get(i).getArtist());
                            mAlbumImgURLs.add(mTracks.get(i).getCoverURL640x636());
                            mAlbumURLs.add(mTracks.get(i).getURL());
                        }

                        runOnUiThread(() -> {
                            mAdapter = new SwipeDeckAdapter(mArtistNames, getApplicationContext());
                            mCardStack.setAdapter(mAdapter);
                        });


                        mCardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
                            @Override
                            public void cardSwipedLeft(int position) {
                                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                            }

                            @Override
                            public void cardSwipedRight(int position) {
                                mPostViewModel.savePost(mTracks.get(position));
                                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                            }

                            @Override
                            public void cardsDepleted() {
                                Log.i("MainActivity", "no more cards");
                            }

                            @Override
                            public void cardActionDown() {
                            }

                            @Override
                            public void cardActionUp() {
                            }
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
                Log.e("TAG", mAccessToken);
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
        con.setRequestProperty("Authorization", "Bearer " + mAccessToken);

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

            ((TextView) v.findViewById(R.id.song_name)).setText(mSongNames.get(position));
            ((TextView) v.findViewById(R.id.artist_name)).setText(artistName.get(position));
            mCurrentArtist = ((TextView) v.findViewById(R.id.artist_name)).getText().toString();
            mCurrentSong = ((TextView) v.findViewById(R.id.song_name)).getText().toString();

            Glide.with(context)
                    .load(mAlbumImgURLs.get(position))
                    .into(((ImageView) v.findViewById(R.id.song_image)));

            v.setOnClickListener(view -> {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    respondToSong(mAlbumURLs.get(position), mAlbumImgURLs.get(position), mSongNames.get(position), null, artistName.get(position));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            });

            return v;
        }
    }

}



