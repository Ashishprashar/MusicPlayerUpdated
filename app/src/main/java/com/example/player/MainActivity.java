package com.example.player;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.player.PlayerActivity.listSongs;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_PERMISSION = 1;
    public static boolean SHOW_MINI_PLAYER = false;
    ImageView on;
    //    private  int REQUEST_CODE = 0;
    public static ArrayList<MusicFiles> favList=new ArrayList<>();
    static boolean shuffleButton=false,repeatButton=false;
    public static ViewPager viewPager;
    public static ArrayList<MusicFiles> musicFiles=new ArrayList<>();
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    TabLayout tabLayout;
    Dialog dialog;
    SearchView searchView;
    public static final String MUSIC_LAST_PLAYED="LAST_PLAYED";
    public static final String MUSIC_FILE="STORED_MUSIC";
    public static String PATH_TO_FRAG=null;
    public static String PLAYING_NAME=null;
    public static String PLAYING_ARTIST=null;
    public static final String ARTIST_NAME="ARTIST_NAME";
    public static final String SONG_NAME="SONG_NAME";
//    FrameLayout bottom_player;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        getActionBar().hide();

        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            dialog.dismiss();
            return;
        }
        try {

        }catch (Exception e){

        }
        permission();
        loadData();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
        NotificationManager notificationManager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPreferences= getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
        String value = sharedPreferences.getString(MUSIC_FILE,null);
        if(value!=null){
            SHOW_MINI_PLAYER =true;
            PATH_TO_FRAG=value;
            PLAYING_ARTIST=sharedPreferences.getString(ARTIST_NAME,null);
            PLAYING_NAME=sharedPreferences.getString(SONG_NAME,null);
        }
        else{
            SHOW_MINI_PLAYER =false;
            PATH_TO_FRAG=null;

        }


    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson =new Gson();
        String json = gson.toJson(listSongs);

        editor.putString("saved",json);
        editor.apply();
        Log.e("save","happed"+json);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences",MODE_PRIVATE);
        Gson gson =new Gson();
        String json =sharedPreferences.getString("saved",null);
        String json1 =sharedPreferences.getString("listPlay",null);
        Type type =new TypeToken<ArrayList<MusicFiles>>() {}.getType();
        if(json1!=null) {

            listSongs = gson.fromJson(json1, type);
        }
        Log.e("palue",listSongs.size()+"");

        musicFiles =gson.fromJson(json,type);
//        FavList.adapter.notifyDataSetChanged();


        if(musicFiles==null){
            musicFiles=getMusicFiles(this);
        }
        for(int i=0;i<musicFiles.size();i++){
            if(musicFiles.get(i).getFav()==1){
                favList.add(musicFiles.get(i));
            }
        }
//        Log.e("load","happed"+musicFiles.get(0).getFav());

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("should","work");
                // Permission granted.
            } else {
                // User refused to grant permission.
            }

        }
    }
    private void permission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.e("tag","permission");
                        musicFiles=getMusicFiles(MainActivity.this);
                        fragSetUp();


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();

    }

    private ArrayList<MusicFiles> getMusicFiles(Context context) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        ArrayList<String> duplicate=new ArrayList<>();

        Uri uri ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME+"";


        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID,

        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String path = cursor.getString(3);
                long duration = cursor.getLong(4);
                int id = cursor.getInt(5);
                MusicFiles musicFiles;
                Log.e("lag",duration+"");
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                musicFiles = new MusicFiles(path, title, artist, album, (int) duration,contentUri.toString());

                tempAudioList.add(musicFiles);
                if(!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioList;

    }

    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
//        bottom_player =findViewById(R.id.relative_layout);
    }
    private void fragSetUp (){
        ViewPagerAdapter viewPagerAdapter =new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new CurrentList(),"current song");
//        viewPagerAdapter.addFragments(new NowPlaying(),"playing");
        viewPagerAdapter.addFragments(new FavList(),"Favorite");
        viewPagerAdapter.addFragments(new AlbumsFragment(),"Album");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.cur_playlist);
//        tabLayout.getTabAt(1).setIcon(R.drawable.playing);
        tabLayout.getTabAt(1).setIcon(R.drawable.fav_list);
        tabLayout.getTabAt(2).setIcon(R.drawable.album);

    }



    public static class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        void addFragments(Fragment fragment ,String title){
            fragments.add(fragment);
            titles.add(title);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search,menu);
//        MenuItem menuItem= menu.findItem(R.id.search_option);
//        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)menuItem.getActionView();
//        searchView.setOnQueryTextListener(this);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        String userInput = newText.toLowerCase();
//        ArrayList<MusicFiles> myMusic= new ArrayList<>();
//        for(MusicFiles m:musicFiles){
//            if(m.getTitle().toLowerCase().contains(userInput)){
//                myMusic.add(m);
//            }
//        }
//        CurrentList.adapter.updateList(myMusic);
//
//
//        return true;
//    }
}