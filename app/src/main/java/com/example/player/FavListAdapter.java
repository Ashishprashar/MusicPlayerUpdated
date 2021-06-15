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
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavListAdapter extends RecyclerView.Adapter<FavListAdapter.MyViewHolder> implements SectionIndexer {

    Context mContext;
    static  ArrayList<MusicFiles> mFiles;
    //    final private ListItemClickListener mOnClickListener ;mOnClickListener
    int heartFlag=0;
    private ArrayList<Integer> mSectionPositions;



    FavListAdapter(Context context, ArrayList<MusicFiles> mFiles/*, ListItemClickListener mOnClickListener*/){
        this.mContext = context;
        this.mFiles = mFiles;
//        this.mOnClickListener = mOnClickListener;
    }



    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
//
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int i) {
        return mSectionPositions.get(i);
    }

    @Override
    public int getSectionForPosition(int i) {
        return i;
    }


    interface ListItemClickListener{
        void onListItemClick(int position);
    }
    @NonNull
    @Override
    public FavListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_file,parent,false);

        return new FavListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavListAdapter.MyViewHolder holder, final int position) {
        holder.title.setText(mFiles.get(position).getTitle());
        holder.artist.setText(mFiles.get(position).getArtist());


        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                Bitmap image = mContext.getContentResolver().loadThumbnail(Uri.parse(mFiles.get(position).getUri()), new Size(640, 480), null);
                if (image!=null){
                    Log.e("TAG", "set image!");

                    Glide.with(mContext).asBitmap().load(image).into(holder.songImage);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(mContext).load(R.drawable.music).into(holder.songImage);
                }
            }else{

                byte[] image1 = getAlbumArt(mFiles.get(position).getPath());
                if (image1!=null){
                    Log.e("TAG", "set image!");

                    Glide.with(mContext).asBitmap().load(image1).into(holder.songImage);
                }
                else{
                    Log.e("TAG", ":/!");

                    Glide.with(mContext).load(R.drawable.music).into(holder.songImage);
                }
            }
//            Log.e("TAG", "Clicked!"+image);

        }catch(Exception e){
            Log.e("error",e+"");
        }


    }
    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView title,artist;
        CircleImageView songImage;
        LinearLayout linearLayout;
        ImageView heart;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            songImage=itemView.findViewById(R.id.songImage);
            artist =itemView.findViewById(R.id.artist);
            linearLayout=itemView.findViewById(R.id.info);
            heart=itemView.findViewById(R.id.heart);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int posi = getAdapterPosition();
            Intent i =new Intent(mContext,PlayerActivity.class);
            i.putExtra("sender","favList");
            i.putExtra("position",posi);
            mContext.startActivity(i);
//            Uri uri =Uri.parse(mFiles.get(posi).getPath());
//            MediaPlayer mediaPlayer =MediaPlayer.create(mContext,uri);
//            mediaPlayer.start();
//               viewPager.setCurrentItem(1);

        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art =retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles=new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}