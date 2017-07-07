package com.example.prateekkesarwani.listviewinfinitedemo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView photosRecyclerView;

    private PhotosAdapter photosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        photosRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerview_photos);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        photosAdapter = new PhotosAdapter(list);
        photosRecyclerView.setAdapter(photosAdapter);


    }
}