package com.example.prateekkesarwani.listviewinfinitedemo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.prateekkesarwani.listviewinfinitedemo", appContext.getPackageName());
    }

    int[] arr = new int[]{0, 1, 2, 3, 4, 5, 5, 4, 1};
    int current = 0;

    @Test
    public void testCache() {

        LruCache<Integer, String> cache = new LruCache<>(2);

        String str = "This is a very large string";
        String[] list = str.split(" ");

        Log.e("Prateek", str);

        while (current < arr.length) {

            int next = getNextInt();

            if (next < 0 || next > 5) {
                continue;
            }

            String value = cache.get(next);

            if (!TextUtils.isEmpty(value)) {
                Log.e("Prateek", "Next: " + next + ", value: " + value + ", from cache");
            } else {
                Log.e("Prateek", "Next: " + next + ", value: " + value + ", from list");
                cache.put(next, list[next]);
            }
        }

    }

    private int getNextInt() {
        return arr[current++];
    }



}
