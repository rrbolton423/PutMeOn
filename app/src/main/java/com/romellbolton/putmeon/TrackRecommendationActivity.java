package com.romellbolton.putmeon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.bumptech.glide.Glide;
import com.daprlabs.cardstack.SwipeDeck;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Request;

public class TrackRecommendationActivity extends AppCompatActivity {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private ArrayList<RecentTrack> mUsersRecentArtist = new ArrayList<>();
    private LinkedList<Track> tracks = new LinkedList<>();
    private Call call;
    private SwipeDeck cardStack;
    SwipeDeckAdapter adapter = new SwipeDeckAdapter(null, this);
    private Button newSuggestionButton;
    private static final String LAST_FM_API_KEY = "dc133e92753caf4b21d826972d94c33e";
    private String accessToken = "accessToken";
    private String randomArtistID;
    private String randomTrackID;
    private ArrayList<String> artistNames = new ArrayList<>();
    private ArrayList<String> songNames = new ArrayList<>();
    private ArrayList<String> albumImgURLs = new ArrayList<>();
    private ArrayList<String> albumURLs = new ArrayList<>();
    private String prefName = "myFavoritesStorage";
    public String currentArtist;
    public String currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_recommendation);

        accessToken = getIntent().getStringExtra("accessToken");

        Log.i("TOKENTOKEN", "onCreate: " + accessToken);

        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        newSuggestionButton = (Button) findViewById(R.id.new_suggestions_button);
        newSuggestionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub
                fetchRandomFavoriteSpotifyArtist();
//                setResponse("Kanye", "Power"); // Test Implementation
            }

        });
//        setResponse("Kanye", "Power"); // Test Implementation
        fetchRandomFavoriteSpotifyArtist(); // Production Implementation, uncomment when using real Spotify account
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }

    public void fetchRandomFavoriteSpotifyArtist() {
        final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/player/recently-played")
                    .addHeader("Authorization","Bearer " + accessToken)
                    .build();

        Log.d("THUS TAGGY", "fetchRandomFavoriteSpotifyArtist: " + request);
        Log.d("THUS TAGGY", "fetchRandomFavoriteSpotifyArtist: " + request.toString());
            cancelCall();
            call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                    try
                    {

                        tracks = null;
                        songNames.clear();
                        artistNames.clear();
                        albumImgURLs.clear();
                        albumURLs.clear();
                        adapter.notifyDataSetChanged();

                        final JSONObject jsonObject = new JSONObject(response.body().string());

                        mUsersRecentArtist = getArtists(jsonObject);
                        Random random = new Random();
                        randomArtistID = mUsersRecentArtist.get(random.nextInt(mUsersRecentArtist.size())).artistID;
                        randomTrackID = mUsersRecentArtist.get(random.nextInt(mUsersRecentArtist.size())).trackID;
                        tracks = recommendationsOnSeed(randomArtistID, randomTrackID);
                        Collections.shuffle(tracks);

                        for(int i = 0; i < tracks.size(); i++)
                        {
                            songNames.add(tracks.get(i).getName());
                            artistNames.add(tracks.get(i).getArtist());
                            albumImgURLs.add(tracks.get(i).getCoverURL640x636());
                            albumURLs.add(tracks.get(i).getURL());
//            cardStack.setLeftImage(R.id.left_image);
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                adapter = new SwipeDeckAdapter(artistNames, getApplicationContext());
                                cardStack.setAdapter(adapter);

                            }
                        });


                        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
                            @Override
                            public void cardSwipedLeft(int position) {
                                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                            }

                            @Override
                            public void cardSwipedRight(int position) {
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

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
//
                }

                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext() , "FILED to make Call ", Toast.LENGTH_SHORT).show();
                    Log.e("TAG" , accessToken);
                }
            });
    }

    //Method used to Parse the Api responses
    public ArrayList<RecentTrack> getArtists(JSONObject jsonObject) {

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    // Get track name
                    JSONObject jsonAlbum = json.getJSONObject("track");
                    String trackName = jsonAlbum.getString("name");



                    String artistName = (String) jsonAlbum.getJSONArray("artists").getJSONObject(0).get("name");
                    String artistID = (String) jsonAlbum.getJSONArray("artists").getJSONObject(0).get("id");
                    String trackID = jsonAlbum.getString("id");



                    RecentTrack track_object = new RecentTrack(trackName, artistName, trackID, artistID);
                    mUsersRecentArtist.add(track_object); //to be used in Ui later

                    Log.i("MainActivity", mUsersRecentArtist.get(0).trackName);
                    Log.i("MainActivity", mUsersRecentArtist.get(0).artistName);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mUsersRecentArtist;
    }

    public void respondToSong(String url, String image, String songName, String albumName,String artistName) {

            Bundle args = new Bundle();
            args.putString(PlaybackScreenFragment.SONG_URL, url);
            args.putString(PlaybackScreenFragment.IMAGE_URL, image);
            args.putString(PlaybackScreenFragment.SONG_NAME, songName);
            args.putString(PlaybackScreenFragment.ALBUM_NAME, albumName);
            args.putString(PlaybackScreenFragment.ARTIST_NAME,artistName);
            PlaybackScreenFragment fragment = new PlaybackScreenFragment();
            fragment.setArguments(args);
            FragmentManager manager = getFragmentManager();
            fragment.show(manager, "Playback Fragment");

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
//                Intent aboutIntent = new Intent(TrackRecommendationActivity.this, FavoritesListActivity.class);
//                startActivity(aboutIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public LinkedList<Track> recommendationsOnSeed(String randomArtistID, String randomTrackID) throws MalformedURLException, IOException, JSONException {

        URL url = new URL("https://api.spotify.com/v1/recommendations?market=US&seed_artists="
                + randomArtistID + "&seed_tracks=" + randomTrackID+ "&min_energy=0.4&min_popularity=50");
//        URL url = new URL("https://api.spotify.com/v1/recommendations?seed_artists=4NHQUGzhtTLFvgF5SZesLK&seed_tracks=0c6xIDDpzE81m2q797ordA&min_energy=0.4&min_popularity=50&market=US");
        System.out.println("GetRecURL: "+url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(false);
        con.setDoInput(true);

        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        //Reading the response
        int status = con.getResponseCode();
        Reader streamReader = null;
        if (status == 400)
        {
            streamReader = new InputStreamReader(con.getErrorStream());
        }
        else
        {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        BufferedReader br = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer str = new StringBuffer();
        while ((inputLine = br.readLine()) != null)
        {
            str.append(inputLine);
        }
        br.close();
        con.disconnect();

        String recStr = str.toString();
        JSONObject JsonSpotifyRecommendations = new JSONObject(recStr);
        System.out.println(recStr);
        JSONArray recs = JsonSpotifyRecommendations.getJSONArray("tracks");
        LinkedList<Track> recommendedList = new LinkedList<>();

        for (int i = 0; i < recs.length(); i++)
        {
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

            String songID = (String) recs.getJSONObject(i).getString("id");
            String songURL = (String) recs.getJSONObject(i).getString("href");

            recommendedList.add(new Track(artistName, songName, CoverURL64x64, CoverURL640x636, artistID, songID, songURL));
        }
        return recommendedList;

        // TODO: When swiped right, save track to shared preferences, when swiped left, do nothing
        // TODO: Set up Track Adapter screen with list of all favorite songs, allow users to play 30 second clips of it
        // TODO: Set up Player Activity to play a selected track for 30 seconds
        // TODO: Set up Favorites / List View Activity displaying the saved tracks being loaded from Shared Prefs
        // TODO: When clicked on a track on the favorites screen, or on track recommendation, navigate to player Activity and
        // let user play 30 seconds of the selected track
        // TODO: Access favorites list by way of a menu button
        // TODO: Eventually, when track is clicked, let user play it using Spotify's API (Very very last implementation)



    }


    public class SwipeDeckAdapter extends BaseAdapter {

        private List<String> artistNames;
        private List<String> artistName;
        //        private List<String> artistName;
        private Context context;

        public SwipeDeckAdapter(ArrayList<String> artistNames, Context context) {
//            this.artistNames = artistNames;
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
            if(v == null){
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.track_card_view, parent, false);
            }

            ((TextView) v.findViewById(R.id.song_name)).setText(songNames.get(position));
            ((TextView) v.findViewById(R.id.artist_name)).setText(artistName.get(position));
            currentArtist = ((TextView) v.findViewById(R.id.artist_name)).getText().toString();
            currentSong = ((TextView) v.findViewById(R.id.song_name)).getText().toString();


            Glide.with(context)
                    .load(albumImgURLs.get(position))
                    .into( ((ImageView) v.findViewById(R.id.song_image)));

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    respondToSong(albumURLs.get(position), albumImgURLs.get(position), songNames.get(position), "", artistName.get(position));
                }
            });

            return v;
        }
    }

}



