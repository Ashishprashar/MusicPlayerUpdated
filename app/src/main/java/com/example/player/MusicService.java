package com.example.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.player.ApplicationClass.ACTION_NEXT;
import static com.example.player.ApplicationClass.ACTION_PLAY;
import static com.example.player.ApplicationClass.ACTION_PREVIOUS;
import static com.example.player.ApplicationClass.CHANNEL_ID_2;
import static com.example.player.PlayerActivity.listSongs;

public class MusicService  extends Service implements MediaPlayer.OnCompletionListener{
    IBinder mBinder = new MyBinder();
     static MediaPlayer mediaPlayer;
    Uri uri;
    int position=-1;
    ArrayList<MusicFiles> musicFiles=new ArrayList<>();
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    public static final String MUSIC_LAST_PLAYED="LAST_PLAYED";
    public static final String MUSIC_FILE="STORED_MUSIC";
    public static final String ARTIST_NAME="ARTIST_NAME";
    public static final String SONG_NAME="SONG_NAME";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind","method");
        return mBinder;
    }
    public class MyBinder extends Binder{
        MusicService getService(){
            return  MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat=new MediaSessionCompat(getBaseContext(),"My Audio");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("service",-1);
        int rr = intent.getIntExtra("rr",0);
        String actionName=intent.getStringExtra("ActionName");
        //                    Log.e("play",listSongs.size()+"");

        if(mediaPlayer==null){
            createMediaPlayer(myPosition);

        }
        if(rr!=-1){
            playMedia(myPosition);
        }

        if (actionName!=null){
            switch (actionName){
                        case "playPause":
//                    Log.e("play",listSongs.size()+"");
                    Log.e("noti",actionName+position);
                    if(actionPlaying!=null){
                        actionPlaying.playPauseClicked();
                    }


                    break;
                case "next":
                    Log.e("noti",actionName);

                    if(actionPlaying!=null){
                        actionPlaying.nextClicked();
                    }
                    break;
                case "previous":
                    Log.e("noti",actionName);

                    if(actionPlaying!=null){
                        actionPlaying.prevClicked();
                    }
                    break;

            }
        }
//        saveData();
        return START_STICKY;
    }

    public void playMedia(int StartPosition) {
        musicFiles =listSongs;
        if(listSongs==null){
            Log.e("yesin","yesssss");
        }
        position= StartPosition;
        if(mediaPlayer!=null){
            this.stop();
            this.release();
            if(musicFiles!=null){
                this.createMediaPlayer(position);
                this.start();
            }
        }else{
            this.createMediaPlayer(position);
            this.start();
        }
        this.saveData();

    }

    void  start(){
        mediaPlayer.start();
    }
    boolean  isPlaying(){
        return mediaPlayer.isPlaying();
    }
    void stop(){
        mediaPlayer.stop();
    }
    void pause(){
        mediaPlayer.pause();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration(){
        return mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int positionInner){
        if(listSongs==null){
            Log.e("yes","yesssss");
        }

        musicFiles=listSongs;
        position=positionInner;
        if(position==-1){
            SharedPreferences s = getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
            position =s.getInt("POSITION",0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            uri = Uri.parse(musicFiles.get(position).getUri());
        }
        else{
            uri =Uri.parse( musicFiles.get(position).getPath());
        }
        mediaPlayer=MediaPlayer.create(getBaseContext(),uri);
        saveData();
    }

    private void saveData() {
        SharedPreferences.Editor editor= getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE,uri.toString());
        editor.putInt("POSITION",position);
        editor.putString(SONG_NAME,musicFiles.get(position).getTitle());
        editor.putString(ARTIST_NAME,musicFiles.get(position).getArtist());
        editor.apply();
    }

    int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    void onCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(actionPlaying!=null){

            actionPlaying.nextClicked();
            if(mediaPlayer!=null){
                onCompleted();

            }
        }
    }
    void showNotification(int playPauseBtn){
        Intent intent =new Intent(this,PlayerActivity.class);
        PendingIntent contentIntent =PendingIntent.getBroadcast(this
                ,0,intent,0);

        Intent preIntent =new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPendingIntent  =PendingIntent.getBroadcast(this,0
                ,preIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent =new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PLAY);

        PendingIntent pausePendingIntent  =PendingIntent.getBroadcast(this
                ,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent =new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent  =PendingIntent.getBroadcast(this,
                0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap image = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.e("TAGinini", "set image!"+position+" "+musicFiles.size());

            try {
                image = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(musicFiles.get(position).getUri()), new Size(640, 480), null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

            byte[] image1 = getAlbumArt(musicFiles.get(position).getPath());
            image= BitmapFactory.decodeByteArray(image1,0,image1.length);

        }

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(image)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.prev,"Previous",prevPendingIntent)
                .addAction(playPauseBtn,"Pause",pausePendingIntent)
                .addAction(R.drawable.next,"Next",nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
//       startForeground(2,notification);
        NotificationManager notificationManager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art =retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying=actionPlaying;
    }
}
