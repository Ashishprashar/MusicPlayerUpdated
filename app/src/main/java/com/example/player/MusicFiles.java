package com.example.player;

import android.util.Log;

public class MusicFiles  {
    String path,title,artist,album;
    int id,fav,duration;
    String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public MusicFiles(String path, String title, String artist, String album, int duration, String uri) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.uri=uri;
        this.album = album;
        this.fav=0;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    @Override
    public String toString() {
        return "MusicFiles{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    public int getduration(){
        return duration;
    }
    public String getDuration() {
        Log.e("check",duration+"");
        float duration = this.duration/1000;
        String totalout;
        String totalnew;
        int seconds = (int)duration%60;
        int min = (int)duration/60;
        totalout = min + ":" + seconds;
        totalnew = min + ":"+"0"+seconds;
        Log.e("check",""+min+""+seconds);
        if(seconds<10){
            return totalnew;
        }
        else{
            return  totalout;
        }
    }

    public void setDuration(int  duration) {
        this.duration = duration;
    }
}
