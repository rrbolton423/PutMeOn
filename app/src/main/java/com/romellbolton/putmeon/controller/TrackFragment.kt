package com.romellbolton.putmeon.controller

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.romellbolton.putmeon.R
import com.romellbolton.putmeon.model.Track
import com.romellbolton.putmeon.util.AppStatus.Companion.getInstance
import com.squareup.picasso.Picasso
import java.io.IOException
import java.net.URL
import java.util.*

class TrackFragment : Fragment() {
    private lateinit var mArtistNameTextView: TextView
    private lateinit var mTrackNameTextView: TextView
    private lateinit var mTrackImageView: ImageView
    private lateinit var mPauseButton: ImageButton
    private lateinit var mShareButton: Button
    private var mArtistName: String? = null
    private var mTrackName: String? = null
    private var mImageURL: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMediaPlayer = MediaPlayer()
        assert(arguments != null)
        mSpotifyTrack = arguments!!.getSerializable(TRACK_KEY) as Track
        assert(mSpotifyTrack != null)
        mArtistName = mSpotifyTrack!!.artist
        mTrackName = mSpotifyTrack!!.name
        mPreviewURL = mSpotifyTrack!!.url
        mImageURL = mSpotifyTrack!!.coverURL640x636
        if (Objects.requireNonNull(context)?.let { getInstance(it).isOnline }!!) {
            DownloadImage().execute()
        } else {
            Toast.makeText(context, R.string.check_internet_connection, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TRACK_POSITION, mMediaPlayer!!.currentPosition)
        Log.i(TAG, "Position now before rotate: " + mMediaPlayer!!.currentPosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val position = savedInstanceState.getInt(TRACK_POSITION)
            Log.i(TAG, "Position now after rotate: $position")
            mMediaPlayer!!.seekTo(position)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            val position = savedInstanceState.getInt(TRACK_POSITION)
            mMediaPlayer!!.seekTo(position)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_track_playing, container, false)
        mArtistNameTextView = v.findViewById(R.id.spotify_track_playing_artist_name)
        mTrackImageView = v.findViewById(R.id.spotify_track_playing_image)
        mTrackNameTextView = v.findViewById(R.id.spotify_track_playing_title)
        mPauseButton = v.findViewById(R.id.spotify_track_playing_button_pause)
        mPauseButton.setOnClickListener(View.OnClickListener {
            if (mMediaPlayer!!.isPlaying) {
                mPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                mMediaPlayer!!.pause()
            } else {
                mPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
                mMediaPlayer!!.start()
            }
        })
        mShareButton = v.findViewById(R.id.spotify_track_playing_share)
        mShareButton.setOnClickListener(View.OnClickListener {
            val title = getString(R.string.listen_to_this_song)
            val text = getString(R.string.message_currently_listening_to,
                    mTrackNameTextView.text,
                    mArtistNameTextView.text,
                    mPreviewURL)
            var i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, text)
            i.putExtra(Intent.EXTRA_TITLE, title)
            i = Intent.createChooser(i, "Share to others via")
            startActivity(i)
        })
        return v
    }

    private fun setActivityTitle(text: String?) {
        Objects.requireNonNull((Objects.requireNonNull(activity) as AppCompatActivity).supportActionBar)!!.title = text
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadImage : AsyncTask<Void?, Void?, Bitmap>() {
        override fun onPreExecute() {
            super.onPreExecute()
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setTitle(getString(R.string.loading_track))
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = false
            mProgressDialog!!.show()
        }

        override fun doInBackground(vararg p0: Void?): Bitmap? {
            var bitmap = BitmapFactory.decodeResource(resources, R.drawable.no_image)
            if (mSpotifyTrack != null) {
                val imageURL = mSpotifyTrack!!.coverURL640x636
                try {
                    val input = URL(imageURL).openStream()
                    bitmap = BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap) {
            Picasso.get().load(mImageURL).into(mTrackImageView)
            mTrackNameTextView.text = mTrackName
            mArtistNameTextView.text = mArtistName
            setActivityTitle(mTrackName)
            bootstrapPlayer()
            mProgressDialog!!.dismiss()
        }

        private fun bootstrapPlayer() {
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.setOnPreparedListener { obj: MediaPlayer -> obj.start() }
            mMediaPlayer!!.setOnCompletionListener { mPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp) }
            playSpotifyTrack(mPreviewURL)
        }

        private fun playSpotifyTrack(url: String?) {
            try {
                mMediaPlayer!!.setDataSource(url)
                mMediaPlayer!!.prepare()
                mMediaPlayer!!.start()
                Toast.makeText(context, getString(R.string.now_playing) + mTrackName + getString(R.string.by) + mArtistName, Toast.LENGTH_LONG).show()
            } catch (ioe: IOException) {
                Log.e(TAG, getString(R.string.unable_to_play_track), ioe)
                Toast.makeText(context, R.string.unable_to_play_track, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val TAG = "SpotifyTrackFragment"
        private const val TRACK_POSITION = "spotify_track_position"
        private const val TRACK_KEY = "TRACK"
        private var mSpotifyTrack: Track? = null
        private var mMediaPlayer: MediaPlayer? = null
        private var mProgressDialog: ProgressDialog? = null
        private var mPreviewURL: String? = null
        fun newInstance(track: Track?): TrackFragment {
            val args = Bundle()
            args.putSerializable(TRACK_KEY, track)
            val fragment = TrackFragment()
            fragment.arguments = args
            return fragment
        }
    }
}