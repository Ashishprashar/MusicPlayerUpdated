package com.example.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.MyViewHolder> {
    Context mContext;

   static   ArrayList<MusicFiles> albumFiles;
    public AlbumDetailAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }
    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_file,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.albumName.setText(albumFiles.get(position).getTitle());
        holder.artistName.setText(albumFiles.get(position).getArtist());
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                Bitmap image = mContext.getContentResolver().loadThumbnail(Uri.parse(albumFiles.get(position).getUri()), new Size(640, 480), null);
                if (image!=null){
                    Log.e("TAG", "set image! in album");

                    Glide.with(mContext).asBitmap().load(image).into(holder.albumImg);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(mContext).load(R.drawable.music).into(holder.albumImg);
                }
            }else{

                byte[] image1 = getAlbumArt(albumFiles.get(position).getPath());
                if (image1!=null){
                    Log.e("TAG", "set image! in album");

                    Glide.with(mContext).asBitmap().load(image1).into(holder.albumImg);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(mContext).load(R.drawable.music).into(holder.albumImg);
                }
            }
//            Log.e("TAG", "Clicked!"+image);

        }catch(Exception e){
            Log.e("error",e+"");
        }
        holder.itemView.setOnClickListener(view -> {
            Intent i =new Intent(mContext,PlayerActivity.class);
            i.putExtra("sender","albumDetails");
            i.putExtra("position",position);
            mContext.startActivity(i);
             });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {
        CircleImageView albumImg;
        TextView albumName,artistName;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            albumName =itemView.findViewById(R.id.title);
            albumImg = itemView.findViewById(R.id.songImage);
            artistName=itemView.findViewById(R.id.artist);

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

