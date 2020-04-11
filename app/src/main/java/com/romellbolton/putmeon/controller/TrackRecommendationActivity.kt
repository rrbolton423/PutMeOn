package com.romellbolton.putmeon.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.daprlabs.cardstack.SwipeDeck
import com.daprlabs.cardstack.SwipeDeck.SwipeEventCallback
import com.romellbolton.putmeon.R
import com.romellbolton.putmeon.controller.TrackActivity.Companion.newIntent
import com.romellbolton.putmeon.model.Track
import com.romellbolton.putmeon.util.AppStatus.Companion.getInstance
import com.romellbolton.putmeon.viewmodel.TrackViewModel
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class TrackRecommendationActivity : AppCompatActivity() {
    private val mOkHttpClient = OkHttpClient()
    var mCurrentArtist: String? = null
    private var mUsersRecentArtist = ArrayList<Track>()
    var mCurrentSong: String? = null
    var mAdapter = SwipeDeckAdapter(null, this)
    private var mCall: Call? = null
    private var mTracks: LinkedList<Track?>? = LinkedList()
    private var mCardStack: SwipeDeck? = null
    private val mArtistNames = ArrayList<String?>()
    private val mSongNames = ArrayList<String?>()
    private val mAlbumImgURLs = ArrayList<String?>()
    private val mAlbumURLs = ArrayList<String?>()
    private var mPostViewModel: TrackViewModel? = null
    private lateinit var mNewSuggestionButton: Button
    private var mAccessToken = "accessToken"
    private var mRandomArtistID: String? = null
    private var mRandomTrackID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_recommendation)
        mPostViewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        mAccessToken = intent.getStringExtra("accessToken")
        mCardStack = findViewById(R.id.swipe_deck)
        mNewSuggestionButton = findViewById(R.id.new_suggestions_button)
        mNewSuggestionButton.setOnClickListener(View.OnClickListener {
            if (getInstance(applicationContext).isOnline) {
                fetchRandomFavoriteSpotifyArtist()
            } else {
                Toast.makeText(applicationContext, R.string.check_internet_connection, Toast.LENGTH_SHORT).show()
            }
        })
        if (getInstance(this).isOnline) {
            fetchRandomFavoriteSpotifyArtist()
        } else {
            Toast.makeText(this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelCall() {
        if (mCall != null) {
            mCall!!.cancel()
        }
    }

    fun fetchRandomFavoriteSpotifyArtist() {
        val request = Request.Builder()
                .url("https://api.spotify.com/v1/me/player/recently-played")
                .addHeader("Authorization", "Bearer $mAccessToken")
                .build()
        cancelCall()
        mCall = mOkHttpClient.newCall(request)
        mCall!!.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        mTracks = null
                        mSongNames.clear()
                        mArtistNames.clear()
                        mAlbumImgURLs.clear()
                        mAlbumURLs.clear()
                        mAdapter.notifyDataSetChanged()
                        val jsonObject = JSONObject(Objects.requireNonNull(response.body)!!.string())
                        mUsersRecentArtist = getArtists(jsonObject)
                        if (mUsersRecentArtist.size == 0) {
                            runOnUiThread { Toast.makeText(applicationContext, R.string.no_spotify_listening_history, Toast.LENGTH_LONG).show() }
                            return
                        }
                        val random = Random()
                        mRandomArtistID = mUsersRecentArtist[random.nextInt(mUsersRecentArtist.size)].artistID
                        mRandomTrackID = mUsersRecentArtist[random.nextInt(mUsersRecentArtist.size)].songID
                        mTracks = recommendationsOnSeed(mRandomArtistID, mRandomTrackID)
                        Collections.shuffle(mTracks)
                        for (i in mTracks!!.indices) {
                            mSongNames.add(mTracks!![i]!!.name)
                            mArtistNames.add(mTracks!![i]!!.artist)
                            mAlbumImgURLs.add(mTracks!![i]!!.coverURL640x636)
                            mAlbumURLs.add(mTracks!![i]!!.url)
                        }
                        runOnUiThread {
                            mAdapter = SwipeDeckAdapter(mArtistNames, applicationContext)
                            mCardStack!!.setAdapter(mAdapter)
                        }
                        mCardStack!!.setEventCallback(object : SwipeEventCallback {
                            override fun cardSwipedLeft(position: Int) {
                                Log.i("MainActivity", "card was swiped left, position in adapter: $position")
                            }

                            override fun cardSwipedRight(position: Int) {
                                mPostViewModel!!.savePost(mTracks!![position])
                                Log.i("MainActivity", "card was swiped right, position in adapter: $position")
                            }

                            override fun cardsDepleted() {
                                Log.i("MainActivity", "no more cards")
                            }

                            override fun cardActionDown() {}
                            override fun cardActionUp() {}
                        })
                    } else {
                        alertUserAboutError()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "IO Exception caught: ", e)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception caught: ", e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("TAG", mAccessToken)
            }
        })
    }

    private fun alertUserAboutError() {
        val dialog = AlertDialogFragment()
        dialog.show(fragmentManager, "error_dialog")
    }

    fun getArtists(jsonObject: JSONObject): ArrayList<Track> {
        if (jsonObject.has("items")) {
            try {
                val jsonArray = jsonObject.getJSONArray("items")
                val len = jsonArray.length()
                for (i in 0 until len) {
                    val json = jsonArray.getJSONObject(i)
                    val jsonAlbum = json.getJSONObject("track")
                    val trackName = jsonAlbum.getString("name")
                    val artistName = jsonAlbum.getJSONArray("artists").getJSONObject(0)["name"] as String
                    val artistID = jsonAlbum.getJSONArray("artists").getJSONObject(0)["id"] as String
                    val trackID = jsonAlbum.getString("id")
                    val track_object = Track(artistName, trackName, null, null, artistID, trackID, null)
                    mUsersRecentArtist.add(track_object) //to be used in Ui later
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return mUsersRecentArtist
    }

    fun respondToSong(url: String?, image: String?, songName: String?, artistName: String?) {
        if (url!!.contains("null")) {
            Toast.makeText(this, R.string.no_preview_available, Toast.LENGTH_SHORT).show()
        } else {
            val intent = newIntent(this, Track(artistName, songName, null, image, null, null, url))
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val findMenuItems = menuInflater
        findMenuItems.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorites -> {
                val intent = Intent(this, FavoriteTracksActivity::class.java)
                startActivity(intent)
                onBackPressed()
            }
            R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(IOException::class, JSONException::class)
    fun recommendationsOnSeed(randomArtistID: String?, randomTrackID: String?): LinkedList<Track?> {
        val url = URL("https://api.spotify.com/v1/recommendations?market=US&seed_artists="
                + randomArtistID + "&seed_tracks=" + randomTrackID + "&min_energy=0.4&min_popularity=50")
        println("GetRecURL: $url")
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.doOutput = false
        con.doInput = true
        con.setRequestProperty("Accept", "application/json")
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Authorization", "Bearer $mAccessToken")
        val status = con.responseCode
        val streamReader: Reader
        streamReader = if (status == 400) {
            InputStreamReader(con.errorStream)
        } else {
            InputStreamReader(con.inputStream)
        }
        val br = BufferedReader(streamReader)
        var inputLine: String?
        val str = StringBuilder()
        while (br.readLine().also { inputLine = it } != null) {
            str.append(inputLine)
        }
        br.close()
        con.disconnect()
        val recStr = str.toString()
        val JsonSpotifyRecommendations = JSONObject(recStr)
        println(recStr)
        val recs = JsonSpotifyRecommendations.getJSONArray("tracks")
        val recommendedList = LinkedList<Track?>()
        for (i in 0 until recs.length()) {
            val artistNameArr = recs.getJSONObject(i)["artists"] as JSONArray
            val artistName = artistNameArr.getJSONObject(0).getString("name")
            val artistID = artistNameArr.getJSONObject(0).getString("id")
            val songName = recs.getJSONObject(i)["name"] as String
            var imagesArr = recs.getJSONObject(i)["album"] as JSONObject
            var tmpImageArr = imagesArr.getJSONArray("images")
            val CoverURL640x636 = tmpImageArr.getJSONObject(0).getString("url")
            imagesArr = recs.getJSONObject(i)["album"] as JSONObject
            tmpImageArr = imagesArr.getJSONArray("images")
            val CoverURL64x64 = tmpImageArr.getJSONObject(2).getString("url")
            val songID = recs.getJSONObject(i).getString("id")
            val previewUrl = recs.getJSONObject(i).getString("preview_url")
            recommendedList.add(Track(artistName, songName, CoverURL64x64, CoverURL640x636, artistID, songID, previewUrl))
        }
        return recommendedList
    }

    inner class SwipeDeckAdapter internal constructor(artistNames: List<String?>?, context: Context) : BaseAdapter() {
        private val artistName: List<String>?
        private val context: Context
        override fun getCount(): Int {
            return artistName!!.size
        }

        override fun getItem(position: Int): Any {
            return artistName!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v = convertView
            if (v == null) {
                val inflater = layoutInflater
                v = inflater.inflate(R.layout.track_card_view, parent, false)
            }
            (v?.findViewById<View>(R.id.song_name) as TextView).text = mSongNames[position]
            (v.findViewById<View>(R.id.artist_name) as TextView).text = artistName!![position]
            mCurrentArtist = (v.findViewById<View>(R.id.artist_name) as TextView).text.toString()
            mCurrentSong = (v.findViewById<View>(R.id.song_name) as TextView).text.toString()
            Glide.with(context)
                    .load(mAlbumImgURLs[position])
                    .into((v.findViewById<View>(R.id.song_image) as ImageView))
            v.setOnClickListener {
                if (getInstance(applicationContext).isOnline) {
                    respondToSong(mAlbumURLs[position], mAlbumImgURLs[position], mSongNames[position], artistName[position])
                } else {
                    Toast.makeText(applicationContext, R.string.check_internet_connection, Toast.LENGTH_SHORT).show()
                }
            }
            return v
        }

        init {
            this.artistName = artistNames as List<String>?
            this.context = context
        }
    }

    companion object {
        private val TAG = TrackRecommendationActivity::class.java.simpleName
    }
}