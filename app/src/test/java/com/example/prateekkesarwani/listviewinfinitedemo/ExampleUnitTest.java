package com.example.prateekkesarwani.listviewinfinitedemo;

import android.text.TextUtils;
import android.util.LruCache;

import org.junit.Test;

import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    public String expand(String str) {

        Stack<Character> stack = new Stack<>();

        StringBuilder builderMain = new StringBuilder();

        for (int index = 0; index < str.length(); ) {
            if (str.charAt(index) > '0' && str.charAt(index) < '9') {

                if (str.charAt(index + 1) == '(') {

                }

            }


        }

        return builderMain.toString();
    }

    public int findClosure(String str, int start) {

        Stack stack = new Stack();
        stack.push('(');

        while (stack.size() > 0) {
            if (str.charAt(start) == '(') {
                stack.push('(');
            }

            if (str.charAt(start) == '(') {
                stack.pop();
            }
            start++;
        }

        return 0;

    }

    @Test
    public void testNestedExpand() {

        assertEquals("abbbcd", expand("ab3cd"));

    }

    public static void main() {

    }

    @Test
    public void testCache() {

        LruCache<Integer, String> cache = new LruCache<>(2);

        String str = "This is a very large string";
        String[] list = str.split(" ");

        System.out.println(str);

        while (current < arr.length) {

            int next = getNextInt();

            if (next < 0 || next > 5) {
                continue;
            }

            String value = cache.get(next);

            if (!TextUtils.isEmpty(value)) {
                System.out.print("Next: " + next + ", value: " + value + ", from cache");
            } else {
                cache.put(next, list[next]);
            }
        }

    }

    int[] arr = new int[]{0, 1, 2, 3, 4, 5, 3, 4, 1};
    int current = 0;

    private int getNextInt() {
        return arr[current++];
    }


    @Test
    public void rxTest() {


        Observable.just(1, 2, 3, 4, 5)
                .flatMap(value -> Observable.just(value))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });


    }
}