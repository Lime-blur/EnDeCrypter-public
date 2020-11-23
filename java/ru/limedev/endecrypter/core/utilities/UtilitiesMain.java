package ru.limedev.endecrypter.core.utilities;

/*
 * MIT License
 *
 * Copyright (c) 2020 Tim Meleshko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static ru.limedev.endecrypter.core.Constants.*;

public class UtilitiesMain {

    public static File changeFileExtension(File f, String newExtension) {
        int i = f.getName().lastIndexOf('.');
        String name = f.getName().substring(0, i);
        return new File(f.getParent() + "/" + name + newExtension);
    }

    public static byte[] readTextFile(Context context, Uri uri) {
        byte[] read = null;
        try {
            InputStream stream = Objects.requireNonNull(context.getContentResolver().openInputStream(uri));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            read = buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = context.getContentResolver().query(
                    uri, null, null, null, null)
            ) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }

    public static boolean isExternalStorageWritable() {
        return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
    }

    public static boolean checkAppPermission(Context context) {
        int check = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        return (check == PackageManager.PERMISSION_DENIED);
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        if (map != null && value != null) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private static boolean checkSpaceAndNull(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }
        return !word.contains(" ");
    }

    public static boolean isValidFilename(String filename) {
        if (checkSpaceAndNull(filename)) {
            return filename.matches(VALID_FILENAME_REGEX);
        } else {
            return false;
        }
    }

    public static boolean isValidEnglishWord(String word) {
        if (checkSpaceAndNull(word)) {
            return word.matches(VALID_WORD_REGEX);
        } else {
            return false;
        }
    }

    public static boolean isValidBase64String(String textString) {
        if (checkSpaceAndNull(textString)) {
            return textString.matches(BASE64_REGEX);
        } else {
            return false;
        }
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertUnixDate(int unixDate) {
        Date date = new java.util.Date(unixDate * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        return sdf.format(date);
    }
}
