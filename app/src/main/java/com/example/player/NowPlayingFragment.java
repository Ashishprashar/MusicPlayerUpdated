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

import static com.example.player.MainActivity.MUSIC_LAST_PLAYED;
import static com.example.player.MainActivity.PATH_TO_FRAG;
import static com.example.player.MainActivity.PLAYING_ARTIST;
import static com.example.player.MainActivity.PLAYING_NAME;
import static com.example.player.MainActivity.SHOW_MINI_PLAYER;

//import static com.example.player.MusicService.mediaPlayer;

public class NowPlayingFragment extends Fragment {
    static TextView songName1,songArtist1;
    static ImageView songImage1,next1,playPause1;
    View view;
    int pos;
    MediaSessionCompat mediaSessionCompat;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        songName1=view.findViewById(R.id.name);
        songArtist1 =view.findViewById(R.id.artist);
        songImage1 = view.findViewById(R.id.songImage);
        next1 =view.findViewById(R.id.next);
        playPause1 = view.findViewById(R.id.play_pause);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MUSIC_LAST_PLAYED,getContext().MODE_PRIVATE);
        pos=sharedPreferences.getInt("POSITION",-1);
        mediaSessionCompat=new MediaSessionCompat(getContext(),"My Audio");
        view.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(),PlayerActivity.class);

            i.putExtra("position",sharedPreferences.getInt("POSITION",0));
            i.putExtra("rrr",-1);
            startActivity(i);
            getContext().startActivity(i);

        });
        return view;
    }


    @Override
    public void onResume() {

        super.onResume();
        setSet();
    }



    private void setSet() {
        if(SHOW_MINI_PLAYER){
            if(PLAYING_ARTIST!=null){
                songArtist1.setText(PLAYING_ARTIST);

            }
            else{
                songArtist1.setText("Song Not PLaying");

            }
            if(PLAYING_NAME!=null){
                songName1.setText(PLAYING_NAME);

            }
            else{
                songName1.setText("Song Not PLaying");

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