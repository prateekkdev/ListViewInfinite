package com.example.prateekkesarwani.listviewinfinitedemo;

import org.junit.Test;

import java.util.Stack;

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

}