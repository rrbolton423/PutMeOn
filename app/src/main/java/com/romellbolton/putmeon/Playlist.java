package com.romellbolton.putmeon;

import android.graphics.Bitmap;

public class Playlist {
    String uri;
    Bitmap image;
    String name;
    String id;
    String owner;

    public Playlist(String uri, Bitmap image, String name, String id, String owner) {
        this.uri = uri;
        this.image = image;
        this.name = name;
        this.id = id;
        this.owner = owner;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}