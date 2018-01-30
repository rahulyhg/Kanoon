package com.rvsoftlab.kanoon.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import com.android.volley.VolleyError;

import java.io.File;

/**
 * Created by ravik on 14-01-2018.
 */

public class Helper {
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
}
