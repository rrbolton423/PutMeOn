package com.romellbolton.putmeon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.daprlabs.cardstack.SwipeDeck;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Request;

public class TrackRecommendationActivity extends AppCompatActivity implements TrackAsyncTask.IData {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private ArrayList<TopTrack> mUsersRandomTopArtistList = new ArrayList<>();
    private ArrayList<Track> tracks = new ArrayList<>();
    private Call call;
    private SwipeDeck cardStack;
    private static final String LAST_FM_API_KEY = "dc133e92753caf4b21d826972d94c33e";
    private String accessToken = "accessToken";
    private String randomArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_actvity);

        accessToken = getIntent().getStringExtra("accessToken");

        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        final ArrayList<String> testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, this);
        cardStack.setAdapter(adapter);
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

        fetchRandomFavoriteSpotifyArtist();
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }

    public void fetchRandomFavoriteSpotifyArtist() {
        final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/artists")
                    .addHeader("Authorization","Bearer " + accessToken)
                    .build();

            cancelCall();
            call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e("TAG" , jsonObject.toString());
                        mUsersRandomTopArtistList = getArtists(jsonObject);
                        Random random = new Random();
                        randomArtist = mUsersRandomTopArtistList.get(random.nextInt(mUsersRandomTopArtistList.size())).artist_name;
                        setResponse(randomArtist);
                        Toast.makeText(getApplicationContext() , "DONE", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    public ArrayList<TopTrack> getArtists(JSONObject jsonObject) {

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    // Get list of artists
                    JSONObject jsonAlbum = json.getJSONObject("album");
                    String album_name = jsonAlbum.getString("name");

                    JSONArray artist_list = jsonAlbum.getJSONArray("artists");
                    JSONObject artist = artist_list.getJSONObject(0);
                    String artist_name = artist.getString("name");

                    TopTrack track_object = new TopTrack(artist_name);
                    mUsersRandomTopArtistList.add(track_object); //to be used in Ui later
                    Toast.makeText(this, mUsersRandomTopArtistList.get(0).artist_name, Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", mUsersRandomTopArtistList.get(0).artist_name);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mUsersRandomTopArtistList;
    }

    private void setResponse(String randomArtist) {
        String url = "http://ws.audioscrobbler.com/2.0/?" +
                "method=track.getsimilar&artist="+ randomArtist.replace(" ", "%20") +
                "&api_key=" + LAST_FM_API_KEY +
                "&format=json&limit=10";
        // http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist=cher&api_key=1bc96f65cb9bfdcdcfdc4ded472f8c71&format=json
        new TrackAsyncTask(this).execute(url);
    }

    @Override
    public void setUpData(ArrayList<Track> trackArrayList) {
        // TODO: Use returned tracklist and set up in Card stack to swipe left and right on track
        // TODO: When swiped right, save track to shared preferences, when swiped left, do nothing
        // TODO: Set up Track Adapter screen with list of all favorited songs, allow users to play 30 second clips of it
        // TODO: Access favorites list by way of a menu button
        // TODO: Add track image, track name, album name, etc
        // TODO: Eventually, when track is clicked, let user play it using Spotify's API
    }

    public class SwipeDeckAdapter extends BaseAdapter {

        private List<String> data;
        private Context context;

        public SwipeDeckAdapter(List<String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
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
            ((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = (String)getItem(position);
                    Log.i("MainActivity", item);
                }
            });

            return v;
        }
    }
}
