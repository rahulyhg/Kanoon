package com.rvsoftlab.kanoon.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by RVishwakarma on 1/17/2018.
 */

public class PermissionUtil {
    private static final int REQUEST_PERMISSION_SETTING = 1;
    private Activity mActivity;
    private PermissionAskListener permissionListener;
    private SessionManager session;
    private int REQUEST_CODE;

    public PermissionUtil(Activity activity){
        mActivity = activity;
        session = new SessionManager(activity);
    }

    private boolean isPermissionNeeded(){
        return (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(Context context, String permission){
        return !isPermissionNeeded() || (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    public void checkAndAskPermission(String permission, int requestCode, PermissionAskListener permissionListener){
        this.permissionListener = permissionListener;
        REQUEST_CODE = requestCode;
        if (!hasPermission(mActivity,permission)){
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,permission)){
                /**
                 * Show information about why permission needed
                 */
                ActivityCompat.requestPermissions(mActivity,new String[]{permission},requestCode);
            }else if (session.getPermissionStatus(permission)){
                /**
                 * Previously Permission Request was canceled with 'Don't ask again'
                 * Redirect to setting after showing Information about why permission needed
                 */
                new AlertDialog.Builder(mActivity)
                        .setTitle("Permission Needed!")
                        .setMessage("Permission needed to perform this action, Please go to Setting and enable permission")
                        .setPositiveButton("GO TO SETTING", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                                intent.setData(uri);
                                mActivity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }else {
                /**
                 * Just Request Normal Permission
                 */
                ActivityCompat.requestPermissions(mActivity,new String[]{permission},requestCode);
                session.putPermissionStatus(permission,true);
            }
        }else {
            this.permissionListener.onPermissionGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(REQUEST_CODE==requestCode){
            boolean valid = true;
            for (int grantResult : grantResults) {
                valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (valid) {
                permissionListener.onPermissionGranted();
            }else {
                permissionListener.onPermissionDenied();
            }
        }
    }

    public interface PermissionAskListener{
        /**
         * Callback on Permission Granted
         */
        void onPermissionGranted();

        /**
         * Callback on Permission Denied
         */
        void onPermissionDenied();

    }
}
