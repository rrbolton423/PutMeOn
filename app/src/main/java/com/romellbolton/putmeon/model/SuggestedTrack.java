package com.romellbolton.putmeon.model;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SuggestedTrack implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "cover_url_64_x_64")
    private String CoverURL64x64;

    @ColumnInfo(name = "cover_url_640_x_636")
    private String CoverURL640x636;

    @ColumnInfo(name = "artist_id")
    private String artistID;

    @ColumnInfo(name = "song_id")
    private String SongID;

    @ColumnInfo(name = "sim")
    private float sim;

    @ColumnInfo(name = "uri")
    private String uri;

    public String getURL() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SuggestedTrack(String artist, String name, String CoverURL64x64, String CoverURL640x636, String artistID, String SongID, String uri) {
        this.artist = artist;
        this.name = name;
        this.CoverURL64x64 = CoverURL64x64;
        this.CoverURL640x636 = CoverURL640x636;
        this.artistID = artistID;
        this.SongID = SongID;
        this.uri = uri;
    }

    public SuggestedTrack(String artist, String name) {
        this.artist = artist;
        this.name = name;
    }

    public SuggestedTrack() {
    }

    public float getSim() {
        return sim;
    }

    public void setSim(float sim) {
        this.sim = sim;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverURL64x64() {
        return CoverURL64x64;
    }

    public void setCoverURL64x64(String CoverURL64x64) {
        this.CoverURL64x64 = CoverURL64x64;
    }

    public String getCoverURL640x636() {
        return CoverURL640x636;
    }

    public void setCoverURL640x636(String CoverURL640x636) {
        this.CoverURL640x636 = CoverURL640x636;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    public String getSongID() {
        return SongID;
    }

    public void setSongID(String SongID) {
        this.SongID = SongID;
    }

    @Override
    public String toString() {
        return "SuggestedTrack{" +
                "artist='" + artist + '\'' +
                ", name='" + name + '\'' +
                ", CoverURL64x64='" + CoverURL64x64 + '\'' +
                ", CoverURL640x636='" + CoverURL640x636 + '\'' +
                ", artistID='" + artistID + '\'' +
                ", SongID='" + SongID + '\'' +
                ", sim=" + sim +
                ", uri='" + uri + '\'' +
                '}';
    }
}