package com.romellbolton.putmeon.controller

import android.R
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.romellbolton.putmeon.model.Track

class TrackActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment? {
        return TrackFragment.newInstance(intent.getSerializableExtra(TRACK_KEY) as Track)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TRACK_KEY = "TRACK"

        @JvmStatic
        fun newIntent(context: Context?, track: Track?): Intent {
            val i = Intent(context, TrackActivity::class.java)
            i.putExtra(TRACK_KEY, track)
            return i
        }
    }
}