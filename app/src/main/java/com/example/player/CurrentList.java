package com.example.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

import static com.example.player.MainActivity.musicFiles;
import static com.example.player.MainActivity.shuffleButton;

//import static com.example.player.MainActivity.musicFiles;


public class CurrentList extends Fragment {
    RecyclerView recyclerView;
//    ListAdapter adapter;
    @SuppressLint("StaticFieldLeak")
     MusicAdapter adapter;
    ListView listView;
    ImageView sh;
    TextView count,sh_on;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_list, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        sh =view.findViewById(R.id.playAll);
        count =view.findViewById(R.id.size);
        sh_on = view.findViewById(R.id.playAll1);
        count.setText(musicFiles.size()+"");
        sh_on.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(),PlayerActivity.class);
            shuffleButton=true;
            i.putExtra("position",getRandom(musicFiles.size()-1));

            startActivity(i);
        });
        sh.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(),PlayerActivity.class);

            i.putExtra("position",getRandom(musicFiles.size()-1));
            shuffleButton=true;

            startActivity(i);
        });
        recyclerView.setHasFixedSize(true);

//        Log.e("tag 9", musicFiles + "");
//        listView =view.findViewById(R.id.listView);
//        if(musicFiles!=null){
//            adapter = new ListAdapter(getContext(),R.layout.music_file,musicFiles);
//            listView.setAdapter(adapter);
//        }
        if (musicFiles != null) {
            adapter = new MusicAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        }
        return view;

    }

    private int getRandom(int i) {
        Random random =new Random();
        return random.nextInt(i+1);

    }
}