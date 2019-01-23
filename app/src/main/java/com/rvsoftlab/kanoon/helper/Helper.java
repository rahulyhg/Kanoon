package com.rvsoftlab.kanoon.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ravik on 14-01-2018.
 */

public class Helper {
    public static final int OPENFILE_REQUEST = 300;

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else
        {
            if(connectivity != null)
            {

                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {

                    for (int i = 0; i < info.length; i++) {

                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                            return true;

                        }

                    }
                }
            }

        }

        return false;
    }

    public static void requestErrorHandling(Context context, VolleyError error){
        error.printStackTrace();
    }

    public static File getDownloadDirectory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public static String getDataDirectory(Context context){
        return Environment.getDataDirectory()+"/data/"+context.getPackageName()+"/files/";
    }

    /**
     * Opens File Downloaded from DropBox
     *
     * @param context Activity || Context
     * @param file    file object
     */
    public static void openDownloadedFile(Activity context, File file) {
        //File file = new File(App.DIR.DATA_DIR+context.getPackageName()+"/files/"+fileName);
        try {
            Intent openFile = new Intent();
            openFile.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                uri = Uri.fromFile(file);
            else
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            //BuildConfig.APPLICATION_ID+".provider"
            openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openFile.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getName()));
            //context.startActivity(openFile);
            context.startActivityForResult(openFile, OPENFILE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * After lollipop 5.1 Android Changed their File Communication method
     * so for getting file from SDCard or Phone Memory this getPath method works
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @SuppressLint("SimpleDateFormat")
    public static String currentDate() {
        Calendar now = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT.dateFormat);
        return dateFormat.format(now.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String currentTime() {
        Calendar now = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT.timeFormat);
        return dateFormat.format(now.getTime());
    }

    /**
     * Check if Variable/String Empty or Null
     *
     * @param s
     * @return true or false
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.contains("null") || s.equalsIgnoreCase("") || TextUtils.isEmpty(s) || s.equalsIgnoreCase(" ") || TextUtils.isEmpty(s);
    }

}
