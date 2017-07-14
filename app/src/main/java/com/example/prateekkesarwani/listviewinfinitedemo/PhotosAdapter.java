package com.example.prateekkesarwani.listviewinfinitedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by prateek.kesarwani on 07/07/17.
 */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private ArrayList<String> camImgUriList;

    private LruCache<Integer, String> photosCache;

    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 4;

    void initCache() {

        if (photosCache != null) {
            return;
        }

        photosCache = new LruCache<>(10);
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
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtItem.setText("Value: " + position);

        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {

                displayLruState();

                // String url = camImgUriList.get(position);

                initCache();

                String cacheUrl = photosCache.get(position);

                if (!TextUtils.isEmpty(cacheUrl)) {
                    Log.e("Prateek", "Bitmap, found in cache, position " + position);
                } else {
                    cacheUrl = camImgUriList.get(position);
                    photosCache.put(position, cacheUrl);
                    Log.e("Prateek", "Bitmap, not found in cache, position" + position);
                }

                Bitmap bitmap = BitmapFactory.decodeFile(camImgUriList.get(position));

                // Null aren't allowed in RxJava 2.0. So need to implement onError, if null is released.
                if (bitmap != null) {
                    e.onNext(bitmap);
                    // Log.e("Prateek", "Max-Memory mb: " + maxMemory);
                    // Log.e("Prateek", "Free-Memory: " + availableMemory);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        holder.imgItem.setImageBitmap(bitmap);
                    }
                });

        // holder.imgItem.setBackground(ContextCompat.getDrawable(holder.imgItem.getContext(), R.drawable.img_placeholder));
    }

    private void displayLruState() {


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
