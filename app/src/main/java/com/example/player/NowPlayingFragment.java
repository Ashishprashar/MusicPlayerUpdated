package com.example.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Random;

import static com.example.player.MainActivity.ARTIST_NAME;
import static com.example.player.MainActivity.MUSIC_FILE;
import static com.example.player.MainActivity.MUSIC_LAST_PLAYED;
import static com.example.player.MainActivity.PATH_TO_FRAG;
import static com.example.player.MainActivity.PLAYING_ARTIST;
import static com.example.player.MainActivity.PLAYING_NAME;
import static com.example.player.MainActivity.SHOW_MINI_PLAYER;
import static com.example.player.MainActivity.SONG_NAME;
import static com.example.player.MainActivity.repeatButton;
import static com.example.player.MainActivity.shuffleButton;
import static com.example.player.MusicService.mediaPlayer;
import static com.example.player.PlayerActivity.listSongs;

public class NowPlayingFragment extends Fragment {
    TextView songName,songArtist;
    static ImageView songImage1,next1,playPause1;
    View view;
    int pos;
    MediaSessionCompat mediaSessionCompat;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_now_playing, container, false);
        songName=view.findViewById(R.id.name);
        songArtist =view.findViewById(R.id.artist);
        songImage1 = view.findViewById(R.id.songImage);
        next1 =view.findViewById(R.id.next);
        playPause1 = view.findViewById(R.id.play_pause);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MUSIC_LAST_PLAYED,getContext().MODE_PRIVATE);
        pos=sharedPreferences.getInt("POSITION",-1);
        Intent serviceIntent =new Intent(getContext(),MusicService.class);
        mediaSessionCompat=new MediaSessionCompat(getContext(),"My Audio");

//        view.setOnClickListener(view1 -> {
//            Intent i = new Intent(getContext(),PlayerActivity.class);
//            if(mediaPlayer==null){
//
//                i.putExtra("position",pos);
//            }
//            getContext().startActivity(i);
//
//        });
//        Intent serviceIntent =new Intent(getContext(),MusicService.class);
//
//        SharedPreferences sharedPreferences= getContext().getSharedPreferences(MUSIC_LAST_PLAYED,getContext().MODE_PRIVATE);
//        next1.setOnClickListener(view1 -> {
//            String value = sharedPreferences.getString(MUSIC_FILE,null);
//            if(value!=null){
//                SHOW_MINI_PLAYER =true;
//                PATH_TO_FRAG=value;
//                PLAYING_ARTIST=sharedPreferences.getString(ARTIST_NAME,null);
//                PLAYING_NAME=sharedPreferences.getString(SONG_NAME,null);
//            }
//            else{
//                SHOW_MINI_PLAYER =false;
//                PATH_TO_FRAG=null;
//
//            }
//            setSet();
//
//            serviceIntent.putExtra("ActionName","next");
//
//            getContext().startService(serviceIntent);
//        });
//
//
//        playPause1.setOnClickListener(view1 -> {
//
//            if(mediaPlayer!=null){
//                if(mediaPlayer.isPlaying()){
//                    playPause1.setImageResource(R.drawable.pause);
//                    mediaPlayer.pause();
//                }else{
//                    playPause1.setImageResource(R.drawable.play);
//
//                }
//            }
//            else{
//                serviceIntent.putExtra("service",sharedPreferences.getInt("POSITION",-1));
////                Log.e("pla///y",listSongs.size()+"");
//
//            }
//            serviceIntent.putExtra("ActionName","playPause");
//            getContext().startService(serviceIntent);
//        });
        playPause1.setOnClickListener(view1 -> {
                    if(mediaPlayer==null ){
                        playPause1.setImageResource(R.drawable.pause);

//                        showNotification(R.drawable.pause);
                        Intent i = new Intent(getContext(),MusicService.class);
                        i.putExtra("service",pos);
                        getContext().startService(i);

                    }
                    else if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        playPause1.setImageResource(R.drawable.play);
//                        showNotification(R.drawable.play);

                    }else{
                        mediaPlayer.start();
                        playPause1.setImageResource(R.drawable.pause);
//                        showNotification(R.drawable.pause);


                    }
        });
        next1.setOnClickListener(view1 -> {
            String value = sharedPreferences.getString(MUSIC_FILE,null);
            if(value!=null){
                SHOW_MINI_PLAYER =true;
                PATH_TO_FRAG=value;
                PLAYING_ARTIST=sharedPreferences.getString(ARTIST_NAME,null);
                PLAYING_NAME=sharedPreferences.getString(SONG_NAME,null);
            }
            else{
                SHOW_MINI_PLAYER =false;
                PATH_TO_FRAG=null;

            }
            if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                playPause1.setImageResource(R.drawable.pause);
                if(shuffleButton && !repeatButton){

                    pos=getRandom(listSongs.size()-1);
                    Log.e("errorNR",pos+"");

                }else if(!shuffleButton && !repeatButton){
                    pos=(pos+1)%(listSongs.size());
                    Log.e("errorN",pos+"");
                }
            }
            else{
                if(shuffleButton && !repeatButton){

                    pos=getRandom(listSongs.size()-1);
                    Log.e("errorNR",pos+"");

                }else if(!shuffleButton && !repeatButton){
                    pos=(pos+1)%(listSongs.size());
                    Log.e("errorN",pos+"");
                }
                playPause1.setImageResource(R.drawable.pause);
            }
//            showNotification(R.drawable.pause);
            setSet1(pos);

            serviceIntent.putExtra("ActionName","next");
            serviceIntent.putExtra("service",pos);
            getContext().startService(serviceIntent);
        });
        return  view;
    }

    private int getRandom(int i) {
        Random random =new Random();
        return random.nextInt(i+1);

    }
//    void showNotification(int playPauseBtn){
//        Intent intent =new Intent(getContext(),PlayerActivity.class);
//        PendingIntent contentIntent =PendingIntent.getBroadcast(getContext()
//                ,0,intent,0);
//
//        Intent preIntent =new Intent(getContext(),NotificationReceiver.class)
//                .setAction(ACTION_PREVIOUS);
//        PendingIntent prevPendingIntent  =PendingIntent.getBroadcast(getContext(),0
//                ,preIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent pauseIntent =new Intent(getContext(),NotificationReceiver.class)
//                .setAction(ACTION_PLAY);
//
//        PendingIntent pausePendingIntent  =PendingIntent.getBroadcast(getContext()
//                ,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent nextIntent =new Intent(getContext(),NotificationReceiver.class)
//                .setAction(ACTION_NEXT);
//        PendingIntent nextPendingIntent  =PendingIntent.getBroadcast(getContext(),
//                0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//        Bitmap image = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Log.e("TAGinini", "set image!"+pos+" "+listSongs.size());
//
//            try {
//                image = getContext().getContentResolver().loadThumbnail(Uri.parse(listSongs.get(pos).getUri()), new Size(640, 480), null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }else{
//            Log.e("TAGinini", "set image!"+pos+" "+listSongs.size());
//
//            byte[] image1 = getAlbumArt(listSongs.get(pos).getPath());
//            image= BitmapFactory.decodeByteArray(image1,0,image1.length);
//
//        }
//
//        Notification notification=new NotificationCompat.Builder(getContext(),CHANNEL_ID_2)
//                .setSmallIcon(playPauseBtn)
//                .setLargeIcon(image)
//                .setContentTitle(listSongs.get(pos).getTitle())
//                .setContentText(listSongs.get(pos).getArtist())
//                .addAction(R.drawable.prev,"Previous",prevPendingIntent)
//                .addAction(playPauseBtn,"Pause",pausePendingIntent)
//                .addAction(R.drawable.next,"Next",nextPendingIntent)
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSessionCompat.getSessionToken()))
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .setOnlyAlertOnce(true)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .build();
////       startForeground(2,notification);
//        NotificationManager notificationManager =(NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notification);
//
//    }

    @Override
    public void onResume() {

        super.onResume();
        setSet();
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            playPause1.setImageResource(R.drawable.pause);
        }
        else{
            playPause1.setImageResource(R.drawable.play);
        }



    }


    private void setSet1(int pos) {
        if(SHOW_MINI_PLAYER){
            if(PLAYING_ARTIST!=null){
                songArtist.setText(listSongs.get(pos).getArtist());

            }
            else{
                songArtist.setText("Song Not PLaying");

            }
            if(PLAYING_NAME!=null){
                songName.setText(listSongs.get(pos).getTitle());

            }
            else{
                songName.setText("Song Not PLaying");

            }
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

                        Bitmap image = getContext().getContentResolver().loadThumbnail(Uri.parse(listSongs.get(pos).getUri()), new Size(640, 480), null);
                        if (image!=null){
                            Log.e("TAG", "set image!");
                            ImageAnimation(getContext(),songImage1,image);
                        }
                        else{
                            Log.e("TAG", ":/!");

                            Glide.with(getContext()).load(R.drawable.music).into(songImage1);
                        }
                    }else{
//                        Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

                        byte[] image1 = getAlbumArt(listSongs.get(pos).getPath());
                        Bitmap image= BitmapFactory.decodeByteArray(image1,0,image1.length);
                        if (image1!=null){
                            Log.e("TAG", "set image!");
                            ImageAnimation(getContext(),songImage1,image);

                        }
                        else{
                            Log.e("TAG", ":/!");

                            Glide.with(getContext()).load(R.drawable.music).into(songImage1);
                        }
                    }
//            Log.e("TAG", "Clicked!"+image);

                }catch(Exception e){
                    Log.e("error",e+"");
                }

        }
    }

    private void setSet() {
        if(SHOW_MINI_PLAYER){
            if(PLAYING_ARTIST!=null){
                songArtist.setText(PLAYING_ARTIST);

            }
            else{
                songArtist.setText("Song Not PLaying");

            }
            if(PLAYING_NAME!=null){
                songName.setText(PLAYING_NAME);

            }
            else{
                songName.setText("Song Not PLaying");

            }
            if(PATH_TO_FRAG!=null){
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

                        Bitmap image = getContext().getContentResolver().loadThumbnail(Uri.parse(PATH_TO_FRAG), new Size(640, 480), null);
                        if (image!=null){
                            Log.e("TAG", "set image!");
                            ImageAnimation(getContext(),songImage1,image);
                        }
                        else{
                            Log.e("TAG", ":/!");

                            Glide.with(getContext()).load(R.drawable.music).into(songImage1);
                        }
                    }else{
//                        Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

                        byte[] image1 = getAlbumArt(PATH_TO_FRAG);
                        Bitmap image= BitmapFactory.decodeByteArray(image1,0,image1.length);
                        if (image1!=null){
                            Log.e("TAG", "set image!");
                            ImageAnimation(getContext(),songImage1,image);

                        }
                        else{
                            Log.e("TAG", ":/!");

                            Glide.with(getContext()).load(R.drawable.music).into(songImage1);
                        }
                    }
//            Log.e("TAG", "Clicked!"+image);

                }catch(Exception e){
                    Log.e("error",e+"");
                }
            }
        }
    }

    public void ImageAnimation(Context context , ImageView imageView , Bitmap bitmap){
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn= AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).asBitmap().load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);

    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art =retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}