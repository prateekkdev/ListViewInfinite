package com.example.prateekkesarwani.listviewinfinitedemo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DCIM;

public class MainActivity extends AppCompatActivity {

    private RecyclerView photosRecyclerView;

    private PhotosAdapter photosAdapter;

    private final static int TOTAL_ITEMS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        photosRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerview_photos);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        /*
        ArrayList<Integer> list = new ArrayList<>();

        for (int index = 1; index <= TOTAL_ITEMS; index++) {
            list.add(index * 10);
        }
        */

        // DIRECTORY_PICTURE / DIRECTORY_DCIM
        File camFile = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);

        File[] files = camFile.listFiles();

        ArrayList<String> uriList = new ArrayList<>();

        for (File file : files) {
            String uri = file.getAbsolutePath();
            uriList.add(uri);
        }

        photosAdapter = new PhotosAdapter(uriList);
        photosRecyclerView.setAdapter(photosAdapter);


    }
}