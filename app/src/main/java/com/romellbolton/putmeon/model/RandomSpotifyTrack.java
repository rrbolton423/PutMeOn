package com.romellbolton.putmeon.model;

public class RandomSpotifyTrack {

    public String trackName;
    public String artistName;
    public String trackID;
    public String artistID;

    public RandomSpotifyTrack() {
    }

    public RandomSpotifyTrack(String trackName, String artistName, String trackID, String artistID) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackID = trackID;
        this.artistID = artistID;
    }
}
