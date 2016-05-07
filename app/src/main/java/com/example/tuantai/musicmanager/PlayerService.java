package com.example.tuantai.musicmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by TuanTai on 7/05/2016.
 */
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {
    MediaPlayer player;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        playMedia(uri);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(PlayerService.this, "Service created", Toast.LENGTH_SHORT).show();
        String songName = "some name";
// assign the song name to songName
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

//        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//        notification.setLatestEventInfo(getApplicationContext(), "MusicPlayerSample",
//                "Playing: " + songName, pi);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentInfo("Title");
        builder.setOngoing(true);
        builder.setContentIntent(pi);
        builder.setContentText("Playing song + " + songName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.build();
        }

        Notification notification = builder.getNotification();
        startForeground(100, notification);
    }
    private void playMedia(Uri uri) {
        if (player == null)
            player = new MediaPlayer();

        player.reset();

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(this ,uri);
            player.setOnPreparedListener(this);
            player.prepareAsync();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(PlayerService.this, "Im killed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
