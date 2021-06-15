package com.example.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.player.MainActivity.musicFiles;

public class AlbumDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumImage;
    String albumName;
    ImageView back,playImg;
    TextView aName,nameDetail,artistDetail,size,playAll;
    AlbumDetailAdapter albumDetailAdapter ;
    ArrayList<MusicFiles> albumSongs =new ArrayList<>();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView =findViewById(R.id.recyclerView);
        albumImage = findViewById(R.id.album_image);
        playImg=findViewById(R.id.playAll);
        playAll=findViewById(R.id.playAll1);
        size=findViewById(R.id.size);
        albumName = getIntent().getStringExtra("albumName");
        back =findViewById(R.id.back_button);
        back.setOnClickListener(view1 -> {
            finish();
        });
        aName=findViewById(R.id.aName);
        nameDetail =findViewById(R.id.name_detail);
        artistDetail =findViewById(R.id.artist_detail);

        for (int i=0;i<musicFiles.size();i++){
            if(albumName.equals(musicFiles.get(i).getAlbum())){
                albumSongs.add(musicFiles.get(i));

            }
        }
        size.setText(""+albumSongs.size());
        playAll.setOnClickListener(view -> {
            Intent i =new Intent(getApplicationContext(),PlayerActivity.class);
            i.putExtra("sender","albumDetails");
            i.putExtra("position",0);
            startActivity(i);
        });
        playImg.setOnClickListener(view -> {
            Intent i =new Intent(getApplicationContext(),PlayerActivity.class);
            i.putExtra("sender","albumDetails");
            i.putExtra("position",0);
            startActivity(i);
        });
        aName.setText(albumSongs.get(0).getAlbum());
        nameDetail.setText(albumSongs.get(0).getAlbum());
        artistDetail.setText(albumSongs.get(0).getArtist());

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                Bitmap image = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(albumSongs.get(0).getUri()), new Size(640, 480), null);
                if (image!=null){
                    Log.e("TAG", "set image! in album");

                    Glide.with(getApplicationContext()).asBitmap().load(image).into(albumImage);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(getApplicationContext()).load(R.drawable.music).into(albumImage);
                }
            }else{

                byte[] image1 = getAlbumArt(albumSongs.get(0).getPath());
                if (image1!=null){
                    Log.e("TAG", "set image! in album");

                    Glide.with(getApplicationContext()).asBitmap().load(image1).into(albumImage);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(getApplicationContext()).load(R.drawable.music).into(albumImage);
                }
            }
//            Log.e("TAG", "Clicked!"+image);

        }catch(Exception e){
            Log.e("error",e+"");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumSongs.size()<1)){
            albumDetailAdapter = new AlbumDetailAdapter(this,albumSongs);
            recyclerView.setAdapter(albumDetailAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this
                    ,RecyclerView.VERTICAL,false));
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art =retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}