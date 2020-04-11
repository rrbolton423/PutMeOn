package com.romellbolton.putmeon.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Track : Serializable {
    @kotlin.jvm.JvmField
    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @ColumnInfo(name = "artist")
    var artist: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "cover_url_64_x_64")
    var coverURL64x64: String? = null

    @ColumnInfo(name = "cover_url_640_x_636")
    var coverURL640x636: String? = null

    @ColumnInfo(name = "artist_id")
    var artistID: String? = null

    @ColumnInfo(name = "song_id")
    var songID: String? = null

    @ColumnInfo(name = "sim")
    var sim = 0f

    @ColumnInfo(name = "uri")
    var url: String? = null

    constructor(artist: String?, name: String?, CoverURL64x64: String?, CoverURL640x636: String?, artistID: String?, SongID: String?, url: String?) {
        this.artist = artist
        this.name = name
        this.coverURL64x64 = CoverURL64x64
        this.coverURL640x636 = CoverURL640x636
        this.artistID = artistID
        this.songID = SongID
        this.url = url
    }

    constructor(artist: String?, name: String?) {
        this.artist = artist
        this.name = name
    }
}