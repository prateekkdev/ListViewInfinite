package com.example.prateekkesarwani.listviewinfinitedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by prateek.kesarwani on 07/07/17.
 */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private ArrayList<String> camImgUriList;

    private LruCache<Integer, Bitmap> photosCache;

    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 4;
    private Object pair;

    PublishSubject<Pair<ViewHolder, Integer>> subject;

    void initCache() {

        if (photosCache != null) {
            return;
        }

        photosCache = new LruCache(10) {
            @Override
            protected void entryRemoved(boolean evicted, Object key, Object oldValue, Object newValue) {
                Bitmap bitmap = ((Bitmap) oldValue);
                if (bitmap != null && !bitmap.isRecycled()) {
                    // Recycling key
                    Log.e("Prateek", "Bitmap, Recycling when removal " + key);
                    bitmap.recycle();
                } else {
                    Log.e("Prateek", "Bitmap, Already recycled " + key);
                }
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {

//        if (holder.bitmap != null) {
//            holder.bitmap.recycle();
//            holder.bitmap = null;

        // photosCache
//        }
        super.onViewRecycled(holder);
    }

    public PhotosAdapter(ArrayList<String> camImgUriList) {
        this.camImgUriList = camImgUriList;

        initCache();

        subject = PublishSubject.create();

        subject.observeOn(Schedulers.io())
                .flatMap(pair -> {
                    int position = pair.second;

                    Bitmap img = photosCache.get(position);

                    if (img != null) {
                        Log.e("Prateek", "Bitmap, found in cache, position " + position);
                    } else {
                        img = BitmapFactory.decodeFile(camImgUriList.get(position));
                        // img = camImgUriList.get(position);
                        if (img == null) {
                            // Some decoding issue.
                            return Observable.just(new Pair(null, null));
                        }
                        photosCache.put(position, img);
                        Log.e("Prateek", "Bitmap, not found in cache, position" + position);
                    }

                    // Null aren't allowed in RxJava 2.0. So need to implement onError, if null is released.
                    if (img != null && !img.isRecycled()) {
                        // e.onNext(img);
                        // Log.e("Prateek", "Max-Memory mb: " + maxMemory);
                        // Log.e("Prateek", "Free-Memory: " + availableMemory);
                    }

                    Pair<ViewHolder, Bitmap> returnPair = new Pair<>(pair.first, img);

                    return Observable.just(returnPair);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {

                    // pair cannot be null, anything being passed around observables shouldn'g be null

                    if (pair.first == null || pair.second == null) {
                        return;
                    }

                    ((ViewHolder) pair.first).imgItem.setImageBitmap((Bitmap) pair.second);
                });
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtItem.setText("Value: " + position);
        subject.onNext(new Pair<>(holder, position));
        // holder.imgItem.setBackground(ContextCompat.getDrawable(holder.imgItem.getContext(), R.drawable.img_placeholder));
    }

    @Override
    public int getItemCount() {
        return camImgUriList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtItem;
        private ImageView imgItem;

        public TextView getTxtItem() {
            return txtItem;
        }

        public ImageView getImgItem() {
            return imgItem;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            txtItem = (TextView) itemView.findViewById(R.id.item_text);
            imgItem = (ImageView) itemView.findViewById(R.id.item_img);
        }
    }
}
