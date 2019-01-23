package com.rvsoftlab.kanoon.helper;

/**
 * Created by ravik on 14-01-2018.
 */

public class Constants {
    public static final String SMS_SENDER = "KNAPPS";

    public interface PERMISSION{
        int SMS = 100;
        int CAMERA = 101;
        int RECORD = 102;
        int STORAGE = 103;
    }

    public interface POST_TYPE{
        int TEXT = 0;
        int IMAGE = 1;
    }

    public interface DATE_TIME_FORMAT {
        String dateFormat = "yyyy-MM-dd";
        String timeFormat = "HH:mm";
    }

    public interface FIRESTORE_NODES{
        String USERS = "user";
        String POSTS = "posts";
    }

    // API URL
    public static final String API = "http://rvsoft.esy.es/Android/kanoon/index.php";
}
