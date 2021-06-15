package com.example.player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.player.MainActivity.musicFiles;

//import static com.example.player.MainActivity.musicFiles;


public class CurrentList extends Fragment {
    RecyclerView recyclerView;
//    ListAdapter adapter;
    @SuppressLint("StaticFieldLeak")
     MusicAdapter adapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_list, container, false);
        recyclerView = view.findViewById(R.id.recycler);
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
}