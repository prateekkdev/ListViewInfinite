package com.example.prateekkesarwani.listviewinfinitedemo;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private LruCache<String, Bitmap> photosCache;

    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 4;
    private Object pair;

    PublishSubject<Pair<ViewHolder, Integer>> subject;

    public static long getUsedMemorySize() {

        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedSize;

    }

    public static long getTotalMemorySize() {
        long totalSize = 0L;
        try {
            Runtime info = Runtime.getRuntime();
            totalSize = info.totalMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    void initCache() {

        if (photosCache != null) {
            return;
        }

        int totalMemory = (int) getTotalMemorySize();
        Log.e("Prateek", "Available mem: " + totalMemory);
        // use 1/8th of the available memory for this memory cache
        final int cacheSize = totalMemory / 8;

        /**
         * @param cacheSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */

        photosCache = new LruCache<String, Bitmap>(cacheSize) {


            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                Log.e("Prateek", "Getting size of: " + key + ", " + bitmap.getByteCount());
                return bitmap.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldBitmap, Bitmap newBitmap) {
                Log.e("Prateek", "Removing " + key + ", " + oldBitmap.getByteCount());
                // Log.e("Prateek", "Adding: " + key + ", " + newBitmap.getByteCount());
                oldBitmap.recycle();
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

    Bitmap getBitmapFromPicasso(String path) {
        try {
            return Picasso.with(mContext).load(path).resize(20, 20).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Bitmap getBitmap(String path) {

        Log.e("Prateek", "Getting bitmap: " + path);
        Bitmap bitmap = null;
        File f = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        Log.e("Prateek", "Height: " + height);
        Log.e("Prateek", "Width: " + width);


        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    Context mContext;

    public PhotosAdapter(ArrayList<String> camImgUriList, Context context) {
        this.mContext = context;
        this.camImgUriList = camImgUriList;

        initCache();

        subject = PublishSubject.create();

        subject.observeOn(Schedulers.io())
                .flatMap(pair -> {
                    int position = pair.second;

                    Bitmap img = photosCache.get("" + position);

                    if (img != null) {
                        Log.e("Prateek", "Bitmap, found in cache, position " + position);
                    } else {
                        img = decodeSampledBitmapFromResource(camImgUriList.get(position), 50, 50);
                        // img = camImgUriList.get(position);
                        if (img == null) {
                            // Some decoding issue.
                            return Observable.just(new Pair(null, null));
                        }

                        Log.e("Prateek", "Size: " + img.getByteCount() / (1024 * 1024));


                        photosCache.put(position + "", img);
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
