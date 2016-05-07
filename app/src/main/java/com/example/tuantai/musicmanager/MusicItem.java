package com.example.tuantai.musicmanager;

/**
 * Created by TuanTai on 5/05/2016.
 */
public class MusicItem {
    String title;
    String duration;
    long id;
    String data;

    public MusicItem(String title, String duration, long id, String data) {
        this.title = title;
        this.duration = duration;
        this.id = id;
        this.data = data;

    }
}