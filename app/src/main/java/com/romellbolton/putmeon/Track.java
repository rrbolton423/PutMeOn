package com.romellbolton.putmeon;

import android.os.Parcel;
import android.os.Parcelable;

public class Track
{
    private String artist;
    private String name;
    private String CoverURL64x64;
    private String CoverURL640x636;
    private String artistID;
    private String SongID;
    private float sim;

    /**
     *
     * @param artist
     * @param name
     * @param CoverURL64x64
     * @param CoverURL640x636
     * @param artistID
     * @param SongID
     */
    public Track(String artist, String name, String CoverURL64x64, String CoverURL640x636, String artistID, String SongID)
    {
        this.artist = artist;
        this.name = name;
        this.CoverURL64x64 = CoverURL64x64;
        this.CoverURL640x636 = CoverURL640x636;
        this.artistID = artistID;
        this.SongID = SongID;
    }

    /**
     *
     * @param artist
     * @param name
     */
    public Track(String artist, String name)
    {
        this.artist = artist;
        this.name = name;
    }

    /**
     *
     * @return float
     */
    public float getSim()
    {
        return sim;
    }

    /**
     *
     * @param sim
     */
    public void setSim(float sim)
    {
        this.sim = sim;
    }

    /**
     *
     * @return String
     */
    public String getArtist()
    {
        return artist;
    }

    /**
     *
     * @param artist
     */
    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    /**
     *
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     *
     * @return String
     */
    public String getCoverURL64x64()
    {
        return CoverURL64x64;
    }

    /**
     *
     * @param CoverURL64x64
     */
    public void setCoverURL64x64(String CoverURL64x64)
    {
        this.CoverURL64x64 = CoverURL64x64;
    }

    /**
     *
     * @return String
     */
    public String getCoverURL640x636()
    {
        return CoverURL640x636;
    }

    /**
     *
     * @param CoverURL640x636
     */
    public void setCoverURL640x636(String CoverURL640x636)
    {
        this.CoverURL640x636 = CoverURL640x636;
    }

    /**
     *
     * @return String
     */
    public String getArtistID()
    {
        return artistID;
    }

    /**
     *
     * @param artistID
     */
    public void setArtistID(String artistID)
    {
        this.artistID = artistID;
    }

    /**
     *
     * @return String
     */
    public String getSongID()
    {
        return SongID;
    }

    /**
     *
     * @param SongID
     */
    public void setSongID(String SongID)
    {
        this.SongID = SongID;
    }

    @Override
    public String toString()
    {
        return "Track{" + "artist=" + artist + ", name=" + name + ", CoverURL64x64=" + CoverURL64x64 + ", CoverURL640x636=" + CoverURL640x636 + ", artistID=" + artistID + ", SongID=" + SongID + '}';
    }
}