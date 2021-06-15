package com.example.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.player.MainActivity.favList;

public class FavList extends Fragment {
    RecyclerView recyclerView;
    static FavListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_fav_list, container, false);
        recyclerView = view.findViewById(R.id.fav_recycler);
        recyclerView.setHasFixedSize(true);


//        Log.e("tag 9", musicFiles + "");
//        listView =view.findViewById(R.id.listView);
//        if(musicFiles!=null){
//            adapter = new ListAdapter(getContext(),R.layout.music_file,musicFiles);
//            listView.setAdapter(adapter);
//        }
        load();
        if (favList != null) {
            adapter = new FavListAdapter(getContext(), favList);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemViewCacheSize(30);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        }

        return view;
    }

    private void load() {

    }
}