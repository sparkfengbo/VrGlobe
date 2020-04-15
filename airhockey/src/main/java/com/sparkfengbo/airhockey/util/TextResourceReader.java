package com.sparkfengbo.airhockey.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * http://media.pragprog.com/titles/kbogla/code/AirHockey1/src/com/airhockey/android/util/TextResourceReader.java
 */
public class TextResourceReader {

    public static String readTextFileFromResource(Context context, int resId) {
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resId, nfe);
        }

        return body.toString();
    }
}
