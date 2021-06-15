package com.example.player;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.player.MainActivity.musicFiles;

public class AlbumDetailFragment extends Fragment {
    ImageView back;
    TextView aName,nameDetail;
    RecyclerView recyclerView;
    ImageView albumImage;
    String albumName;
    AlbumDetailAdapter albumDetailAdapter ;
    ArrayList<MusicFiles> albumSongs =new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_album_details,container,false);
        albumName=getArguments().getString("albumName");
        Log.e("al",albumName+"");
        back = view.findViewById(R.id.back_button);
        back.setOnClickListener(view1 -> {
            ((FragmentActivity)getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.re1, new AlbumsFragment())
                    .commit();
            ((FragmentActivity)getContext()).getSupportFragmentManager().popBackStack();
        });
        recyclerView =view.findViewById(R.id.recyclerView);
        albumImage = view.findViewById(R.id.album_image);
        aName=view.findViewById(R.id.aName);
        nameDetail =view.findViewById(R.id.name_detail);

        for (int i=0;i<musicFiles.size();i++){
            if(albumName.equals(musicFiles.get(i).getAlbum())){
                albumSongs.add(musicFiles.get(i));

            }
        }
        aName.setText(albumSongs.get(0).getAlbum());
        nameDetail.setText(albumSongs.get(0).getAlbum()+"\n"+albumSongs.get(0).getArtist());
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                Bitmap image = getContext().getContentResolver().loadThumbnail(Uri.parse(albumSongs.get(0).getUri()), new Size(640, 480), null);
                if (image!=null){
                    Log.e("TAG", "set image! in album");

                    Glide.with(getContext()).asBitmap().load(image).into(albumImage);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(getContext()).load(R.drawable.music).into(albumImage);
                }
            }else{

                byte[] image1 = getAlbumArt(albumSongs.get(0).getPath());
                if (image1!=null){
                    Log.e("TAG", "set image! in album");

                    Glide.with(getContext()).asBitmap().load(image1).into(albumImage);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(getContext()).load(R.drawable.music).into(albumImage);
                }
            }
//            Log.e("TAG", "Clicked!"+image);

        }catch(Exception e){
            Log.e("error",e+"");
        }
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.e("backP","press");
                if(i==KeyEvent.KEYCODE_BACK){
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!(albumSongs.size()<1)){
            albumDetailAdapter = new AlbumDetailAdapter(getContext(),albumSongs);
            recyclerView.setAdapter(albumDetailAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()
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
