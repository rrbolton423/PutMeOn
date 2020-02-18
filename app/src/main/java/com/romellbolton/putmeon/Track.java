package com.romellbolton.putmeon;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {
    String name;
    String artist;
    String url;
    String smallImageURL;
    String largeImageURL;
    String mbid;

    public Track() { }

    public Track(Parcel in) {
        this.name = in.readString();
        this.artist = in.readString();
        this.url = in.readString();
        this.smallImageURL = in.readString();
        this.largeImageURL = in.readString();
        this.mbid = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSmallImageURL() {
        return smallImageURL;
    }

    public void setSmallImageURL(String smallImageURL) {
        this.smallImageURL = smallImageURL;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL) {
        this.largeImageURL = largeImageURL;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    @Override
    public String toString() {
        String stTrack = "{" +
                "\"name\":\"" + this.name + "\"," +
                "\"artist\":\"" + this.artist + "\"," +
                "\"url\":\"" + this.url + "\"," +
                "\"smallImageURL\":\"" + this.smallImageURL + "\"," +
                "\"largeImageURL\":\"" + this.largeImageURL + "\"," +
                "\"mbid\":\"" + this.mbid + "\"}";

        return stTrack;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(artist);
        parcel.writeString(url);
        parcel.writeString(smallImageURL);
        parcel.writeString(largeImageURL);
        parcel.writeString(mbid);

    }
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}