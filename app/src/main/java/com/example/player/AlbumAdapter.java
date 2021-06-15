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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    Context mContext;

    ArrayList<MusicFiles> albumFiles;
    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }
    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ablum_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.albumName.setText(albumFiles.get(position).getAlbum());
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
            Intent intent = new Intent (mContext,AlbumDetails.class);
            intent.putExtra("albumName",albumFiles.get(position).getAlbum());
            mContext.startActivity(intent);
//            View vi=view.getRootView();
//            AlbumDetailFragment ad =new AlbumDetailFragment();
//            Bundle b =new Bundle();
//            b.putString("albumName",albumFiles.get(position).getAlbum());
//            ad.setArguments(b);
//            ((FragmentActivity)mContext).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.album_, ad)
//                            .commit();
//            ((FragmentActivity)mContext).getSupportFragmentManager()
//                        .popBackStack();
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {
        ImageView albumImg;
        TextView albumName;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            albumName =itemView.findViewById(R.id.album_name);
            albumImg = itemView.findViewById(R.id.album_image);

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
