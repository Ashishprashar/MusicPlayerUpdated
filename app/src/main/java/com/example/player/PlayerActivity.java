package com.example.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.Size;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

import static com.example.player.AlbumDetailAdapter.albumFiles;
import static com.example.player.FavListAdapter.mFiles;
import static com.example.player.MainActivity.musicFiles;
import static com.example.player.MainActivity.repeatButton;
import static com.example.player.MainActivity.shuffleButton;
import static com.example.player.NowPlayingFragment.songArtist1;
import static com.example.player.NowPlayingFragment.songImage1;
import static com.example.player.NowPlayingFragment.songName1;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {
     ImageView songImage,chevron,menu,prev,shuffle,repeat,next,playPause,fav,backward,forward;
     SeekBar seekBar;
     TextView duration, total,songName,artistName;
    Uri uri;
    int position=-1;
//    static MediaPlayer mediaPlayer;
    Thread playPauseThread,prevThread,nextThread;
    Handler handler =new Handler();
    RelativeLayout layout;
     MusicService musicService = null;
    int flag;
    static  ArrayList<MusicFiles> listSongs =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setContentView(R.layout.activity_player);
//        getSupportActionBar().hide();
        initView();
//        Intent intent =new Intent(this,MusicService.class);
//        bindService(intent,this,BIND_AUTO_CREATE);
        getIntentMethod();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b && musicService != null) {
                    Log.e("playing on",""+i);
                    musicService.seekTo(i * 1000);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    try {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                        duration.setText(formated(currentPosition));

                    if(musicService.isPlaying()){
                        playPause.setImageResource(R.drawable.pause);
                    }
                    else{
                        playPause.setImageResource(R.drawable.play);

                    }
                    }catch (Exception e){

                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffle.setOnClickListener(view -> {
            if (!shuffleButton) {
                shuffleButton = true;
                shuffle.setImageResource(R.drawable.shuffle_on);
            } else {
                shuffleButton = false;
                shuffle.setImageResource(R.drawable.shuffle);
            }
        });
        repeat.setOnClickListener(view -> {
            if (!repeatButton) {
                repeatButton = true;
                repeat.setImageResource(R.drawable.repeate_on);

            } else {
                repeatButton = false;
                repeat.setImageResource(R.drawable.ic_baseline_repeat_24);

            }
//            viewPager.getAdapter().notifyDataSetChanged();
        });

        chevron.setOnClickListener(view -> {
            finish();
        });
        try {

            fav.setOnClickListener(view -> {
                if (listSongs.get(position).getFav() == 1) {
                    fav.setImageResource(R.drawable.empty_heart);

                    for(int i =0;i<musicFiles.size();i++){
                        if(musicFiles.get(i)==listSongs.get(position)){
                            musicFiles.get(i).setFav(0);

                        }
                    }

                    mFiles.remove(listSongs.get(position));

                } else {
                    for(int i =0;i<musicFiles.size();i++){
                        if(musicFiles.get(i)==listSongs.get(position)){
                            musicFiles.get(i).setFav(1);

                        }
                    }
                    mFiles.add(listSongs.get(position));
                    fav.setImageResource(R.drawable.favorite);

                }
                saveData();
                FavList.adapter.notifyItemChanged(position);
                FavList.adapter.notifyDataSetChanged();
            });
        }catch (Exception e){

        }
    }

    private void setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson =new Gson();
        String json = gson.toJson(musicFiles);
        String json1 = gson.toJson(mFiles);
        String json2 =gson.toJson(listSongs);
        editor.putString("saved",json);
        editor.putString("fav",json1);
        Log.e("save1234","happed"+json2);

        editor.putString("listPlay",json2);
        editor.apply();
        Log.e("save","happed"+json);
    }
    private String  formated(int currentPosition) {
        float duration = currentPosition;
        String totalout;
        String totalnew;
        int seconds = (int)duration%60;
        int min = (int)duration/60;
        totalout = min + ":" + seconds;
        totalnew = min + ":"+"0"+seconds;
//        Log.e("check",""+min+""+seconds);
        if(seconds<10){
            return totalnew;
        }
        else{
            return  totalout;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i =new Intent(this,MainActivity.class);
        startActivity(i);
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position",-1);
        flag = getIntent().getIntExtra("rrr",0);
        String sender = getIntent().getStringExtra("sender");
        Log.e("sender",sender+""+position);

        if(sender!=null && sender.equals("albumDetails")){
            listSongs =albumFiles;

        }
        else if(sender!=null && sender.equals("favList")) {
            listSongs=mFiles;
        }
        else{
            listSongs = musicFiles;
        }



        Intent intent =new Intent(this,MusicService.class);
        intent.putExtra("service",position);
        startService(intent);


        setTexts();
        setBackGround();
    }
    void play(){
        if(musicService!=null){
            musicService.stop();
            musicService.release();
        }


        musicService.createMediaPlayer(position);
        musicService.start();

        seekBar.setMax(musicService.getDuration()/1000);
        total.setText(listSongs.get(position).getDuration());
        if(listSongs.get(position).getFav()==1){
            fav.setImageResource(R.drawable.favorite);
        }
        else{
            fav.setImageResource(R.drawable.empty_heart);

        }
        setTexts();
        setBackGround();
        musicService.onCompleted();


    }
    private static final float BLUR_RADIUS = 12f;

    public Bitmap blur(Bitmap image) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(this);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
    private void setBackGround() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("back", "set image!"+position+" "+listSongs.size());

                Bitmap image = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(listSongs.get(position).getUri()), new Size(640, 480), null);
                Bitmap blurredBitmap = blur(image);
                Matrix matrix = new Matrix();
                matrix.postScale(.7f, .5f);
//                Bitmap croppedBitmap = Bitmap.createBitmap(blurredBitmap, 100, 100,100, 100, matrix, true);
                BitmapDrawable ob = new BitmapDrawable(getResources(), blurredBitmap);
                ob.setColorFilter(0xff555555, PorterDuff.Mode.MULTIPLY);
                layout.setBackground(ob);

//
//                if (image!=null){
//                    Log.e("TAG", "set image!");
//                    Blurry.with(getApplicationContext()).from(image).into(layout);
//
//                }
//                else{
//                    Log.e("TAG", ":/!");
//
//                    Glide.with(getApplicationContext()).load(R.drawable.music).into(songImage);
//                }
            }else{

                byte[] image1 = getAlbumArt(listSongs.get(position).getPath());
                Bitmap bitmap = BitmapFactory.decodeByteArray(image1, 0, image1.length);
                Bitmap blurredBitmap = blur(bitmap);
                Matrix matrix = new Matrix();
                matrix.postScale(0.5f, 0.5f);
                BitmapDrawable ob = new BitmapDrawable(getResources(), blurredBitmap);
                ob.setColorFilter(0xff555555, PorterDuff.Mode.MULTIPLY);
                layout.setBackground(ob);
            }
//            Log.e("TAG", "Clicked!"+image);

        }catch(Exception e){
            Log.e("error",e+"");
        }
    }

    private void setTexts() {
        Log.e("Textinini", "set image!"+position+" "+listSongs.size());
        setImage();
        setBackGround();
        songName.setText(listSongs.get(position).getTitle());
        artistName.setText(listSongs.get(position).getArtist());
    }

    @Override
    protected void onResume() {
        Intent intent =new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        plaBtn();
        prevBtn();
        nextBtn();
        backwardBtn();
        forwardBtn();
        super.onResume();
    }



    private void forwardBtn() {
        forward.setOnClickListener(view -> {
            int curr = musicService.getCurrentPosition();
            Log.e("currrrr",""+curr);
            seekBar.setProgress(curr/1000+5);
            musicService.seekTo(curr+5000);
            Log.e("currrrr",""+curr);

        });
    }

    private void backwardBtn() {
        backward.setOnClickListener(view -> {
            int curr = musicService.getCurrentPosition();
            Log.e("currrrr",""+curr);
            seekBar.setProgress(curr/1000-5);
            musicService.seekTo(curr-5000);
            Log.e("currrrr",""+curr);

        });
    }

    private void nextBtn() {
        nextThread = new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                super.run();
                next.setOnClickListener(view -> {
                    nextClicked();
                });
            }
        };
        nextThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void nextClicked() {
        saveData();
        if(musicService.isPlaying()){
//            mediaPlayer.stop();
//            mediaPlayer.release();
            playPause.setImageResource(R.drawable.pause);
            if(shuffleButton && !repeatButton){

                position=getRandom(listSongs.size()-1);
                Log.e("errorNR",position+"");

            }else if(!shuffleButton && !repeatButton){
                position=(position+1)%(listSongs.size());
                Log.e("errorN",position+"");
            }
            play();
            musicService.showNotification(R.drawable.pause);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int currentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();

        }
        else{
//            mediaPlayer.stop();
//            mediaPlayer.release();
            if(shuffleButton && !repeatButton){

                position=getRandom(listSongs.size()-1);
                Log.e("errorNR",position+"");

            }else if(!shuffleButton && !repeatButton){
                position=(position+1)%(listSongs.size());
                Log.e("errorN",position+"");



            }
            play();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int currentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.pause);

            playPause.setImageResource(R.drawable.pause);
        }

        songArtist1.setText(listSongs.get(position).getArtist());
        songName1.setText(listSongs.get(position).getTitle());
    }

    private int getRandom(int i) {
        Random random =new Random();
        return random.nextInt(i+1);

    }

    private void prevBtn() {
        prevThread = new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                super.run();
                prev.setOnClickListener(view -> {
                    prevClicked();
                });
            }
        };
        prevThread.start();
    }

    public void prevClicked() {
        saveData();
        if(musicService.isPlaying()){
//            mediaPlayer.stop();
//            mediaPlayer.release();

            if(shuffleButton && !repeatButton){

                position=getRandom(listSongs.size()-1);
                Log.e("errorPR",position+"");

            }else if(!shuffleButton && !repeatButton){



                if(position==0){
                    position=musicFiles.size()-1;
                }
                else {
                    position -= 1;
                }
                Log.e("errorP",position+"");
            }
            play();
            musicService.showNotification(R.drawable.pause);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int currentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });

        }
        else{
//            mediaPlayer.stop();
//            mediaPlayer.release();
            if(shuffleButton && !repeatButton){

                position=getRandom(listSongs.size()-1);
                Log.e("errorPR",position+"");

            }else if(!shuffleButton && !repeatButton){


                if(position==0){
                    position=musicFiles.size()-1;
                }
                else {
                    position -= 1;
                }



                Log.e("errorP",position+"");

            }            play();
            musicService.showNotification(R.drawable.pause);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                uri = listSongs.get(position).getUri();
//            }
//            else{
//                uri =Uri.parse( listSongs.get(position).getPath());
//            }
//            setImage();
//            setTexts();
//            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int currentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });

        }
        songArtist1.setText(listSongs.get(position).getArtist());
        songName1.setText(listSongs.get(position).getTitle());
        musicService.onCompleted();
        playPause.setImageResource(R.drawable.pause);

    }

    private void plaBtn() {
        playPauseThread = new Thread(){
            @Override
            public void run() {
                super.run();
                playPause.setOnClickListener(view -> {
                    playPauseClicked();
                });
            }
        };
        playPauseThread.start();
    }

    public void playPauseClicked() {
        saveData();

        if(musicService.isPlaying()){
            playPause.setImageResource(R.drawable.play);
            musicService.showNotification(R.drawable.play);

            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int currentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else{
            playPause.setImageResource(R.drawable.pause);
            musicService.showNotification(R.drawable.pause);

            musicService.start();
            seekBar.setMax(musicService.getDuration()/1000 );
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null){
                        int currentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private void setImage() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

                Bitmap image = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(listSongs.get(position).getUri()), new Size(640, 480), null);
                if (image!=null){
                    Log.e("TAG", "set image!");
                    ImageAnimation(this,songImage,image);
                    Glide.with(getApplicationContext()).load(image).into(songImage1);

                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(getApplicationContext()).load(R.drawable.music).into(songImage);
                    Glide.with(getApplicationContext()).load(R.drawable.music).into(songImage1);

                }
            }else{
                Log.e("TAGinini", "set image!"+position+" "+listSongs.size());

                byte[] image1 = getAlbumArt(listSongs.get(position).getPath());
                Bitmap image=BitmapFactory.decodeByteArray(image1,0,image1.length);
                if (image1!=null){
                    Log.e("TAG", "set image!");
                    Glide.with(getApplicationContext()).load(image).into(songImage1);

                    ImageAnimation(this,songImage,image);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(getApplicationContext()).load(R.drawable.music).into(songImage);
                    Glide.with(getApplicationContext()).load(R.drawable.music).into(songImage1);

                }
            }
//            Log.e("TAG", "Clicked!"+image);

        }catch(Exception e){
            Log.e("error",e+"");
        }
    }
    public void ImageAnimation(Context context ,ImageView imageView ,Bitmap bitmap){
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

    private void initView() {
        songImage = findViewById(R.id.song_art);
        chevron = findViewById(R.id.back_button);
        prev  =findViewById(R.id.prev);
        shuffle = findViewById(R.id.shuffle);
        repeat =findViewById(R.id.repeat);
        next = findViewById(R.id.next);
        playPause = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekbar);
        duration = findViewById(R.id.duration);
        total = findViewById(R.id.total);
        songName = findViewById(R.id.song_name);
        artistName  =findViewById(R.id.artist);
        layout = findViewById(R.id.mainPlayer);
        fav = findViewById(R.id.fav);
        backward = findViewById(R.id.backward);
        forward = findViewById(R.id.forward);
        songName.setSelected(true);
    }



    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder)iBinder;
        musicService = myBinder.getService();
        musicService.setCallBack(this);


////        Toast.makeText(this,"Connected"+musicService,Toast.LENGTH_SHORT).show();
//        play();
        musicService.playMedia(position);

        musicService.showNotification(R.drawable.pause);

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService=null;
    }

}