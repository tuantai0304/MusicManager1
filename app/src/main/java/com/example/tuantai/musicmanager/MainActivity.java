package com.example.tuantai.musicmanager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

import static com.example.tuantai.musicmanager.R.drawable.list_selector;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    String[] testString = {"Tai", "Le"};
    MusicItem[] arrMusicItems;
    SeekBar seekBar;

    MediaPlayer player;
    int currentPlayedMusicPosition;

    Handler handler;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (player.isPlaying()) {
                int percentage = (int) ((double) player.getCurrentPosition() / (double) player.getDuration() * 100);
                seekBar.setProgress(percentage);
                Log.d("percent", String.valueOf(seekBar.getProgress()));
                Log.d("percent", String.valueOf(player.getCurrentPosition()));
                Log.d("percent", String.valueOf(player.getDuration()));

//                Repeat it self
                handler.postDelayed(this, 100);
            }
        }
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> myArr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testString);
        lv.setAdapter(myArr);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPlayedMusicPosition = position;
                playMedia_usingService(arrMusicItems[currentPlayedMusicPosition]);
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                handler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(mUpdateTimeTask);
                int seek = seekBar.getProgress() * Integer.parseInt(arrMusicItems[currentPlayedMusicPosition].duration) / 100;
                player.seekTo(seek);
                updateProgressBar();
            }
        });
        handler = new Handler();
//        Using service to playmedia

        update(null);

// Todo: fix this function
        verifyStoragePermissions(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        player.release();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        player.start();
//        updateProgressBar();
    }

    private void playMedia(MusicItem arrMusicItem) {
        if (player == null)
            player = new MediaPlayer();

        player.reset();

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(this, Uri.parse(arrMusicItem.data));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateProgressBar();
    }

    private void playMedia_usingService(MusicItem arrMusicItem) {
        Log.d("Serviced csall", "Yes");
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("uri", arrMusicItem.data);
        startService(intent);
    }

    int count = 0;

    public void updateProgressBar() {
        count++;
        Log.d("Count:", String.valueOf(count));
        handler.postDelayed(mUpdateTimeTask, 100);

    }

    public void stop(View view) {
        player.pause();
    }


    class GetMusic extends AsyncTask<Void, Void, Void> {
        ArrayList<MusicItem> musicItems;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            musicItems = new ArrayList<>();
//            verifyStoragePermissions(MainActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (MusicItem item : musicItems) {
                Log.d("Music", item.data);
            }
            Log.d("Music items count: ", String.valueOf(musicItems.size()));

            arrMusicItems = new MusicItem[musicItems.size()];

            MusicItemsAdapter adapter = new MusicItemsAdapter(MainActivity.this, R.layout.single_row, musicItems.toArray(arrMusicItems));
            lv.setAdapter(adapter);
//            MediaPlayer player = MediaPlayer.create(MainActivity.this, Uri.parse(musicItems.get(0).data));
//
//            player.start();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projections = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projections, null, null, null);

            while (cursor.moveToNext()) {
                int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                String title = cursor.getString(titleCol);
                String duration = cursor.getString(durationCol);
                long id = cursor.getLong(idCol);
                String data = cursor.getString(dataCol);

                musicItems.add(new MusicItem(title, duration, id, data));
            }
            return null;
        }
    }


    public void update(View view) {
        GetMusic task = new GetMusic();
        task.execute();
    }

    public void next(View view) {
        ++currentPlayedMusicPosition;

        if (currentPlayedMusicPosition >= arrMusicItems.length)
            currentPlayedMusicPosition = 0;

//        // TODO: 6/05/2016 How about using adapter.notificationChange
        lv.requestFocusFromTouch();
        lv.setSelection(currentPlayedMusicPosition);
//        lv.setItemChecked(currentPlayedMusicPosition, true);
        playMedia(arrMusicItems[currentPlayedMusicPosition]);

        Log.d("Position", String.valueOf(lv.getSelectedItemPosition()));
//        lv.getChildAt(currentPlayedMusicPosition).setSelected(true);
    }

    public void play(View view) {
        if (player != null)
            player.start();
        else {
            currentPlayedMusicPosition = 0;
            playMedia(arrMusicItems[currentPlayedMusicPosition]);
        }
        updateProgressBar();
    }

    public void prev(View view) {
        --currentPlayedMusicPosition;

        if (currentPlayedMusicPosition < 0)
            currentPlayedMusicPosition = arrMusicItems.length - 1;

        lv.requestFocusFromTouch();
        lv.setSelection(currentPlayedMusicPosition);
        playMedia(arrMusicItems[currentPlayedMusicPosition]);
    }
}
