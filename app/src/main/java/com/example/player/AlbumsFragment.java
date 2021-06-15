package com.example.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.player.MainActivity.albums;


public class AlbumsFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_albums, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        if (albums != null) {
            adapter = new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemViewCacheSize(30);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        }
        return view;
    }
}