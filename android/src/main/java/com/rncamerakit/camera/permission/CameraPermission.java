package com.rncamerakit.camera.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import java.util.Arrays;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.facebook.react.bridge.Promise;
import com.rncamerakit.SharedPrefs;

public class CameraPermission {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private static final int PERMISSION_GRANTED = 1;
    private static final int PERMISSION_NOT_DETERMINED = -1;
    private static final int PERMISSION_DENIED = 0;

    private Promise requestAccessPromise;

    public void requestAccess(final Activity activity, Promise promise, String rationals) {
        if (isPermissionGranted(activity)) {
            promise.resolve(true);
        }
        requestAccessPromise = promise;

        String[] keys = null;
        if (rationals != null && rationals.length() > 0) {
            keys = rationals.split(",");
        }

        if(keys.length == 4 && "1".equals(keys[0])) {
           AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
           alertBuilder.setCancelable(false);
           alertBuilder.setTitle(keys[1]);
           alertBuilder.setMessage(keys[2]);
           alertBuilder.setPositiveButton(keys[3], new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   permissionRequested(activity, Manifest.permission.CAMERA);
                      ActivityCompat.requestPermissions(activity,
                      new String[]{Manifest.permission.CAMERA},
                     CAMERA_PERMISSION_REQUEST_CODE);
               }
        });
           AlertDialog alert = alertBuilder.create();
           alert.setCanceledOnTouchOutside(false);
           alert.show();
        } else {
            permissionRequested(activity, Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.CAMERA},
            CAMERA_PERMISSION_REQUEST_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (isCameraPermission(requestCode, permissions)) {
            if (requestAccessPromise != null) {
                requestAccessPromise.resolve(grantResults[0] == PermissionChecker.PERMISSION_GRANTED);
                requestAccessPromise = null;
            }
        }
    }

    private boolean isCameraPermission(int requestCode, String[] permissions) {
        if (permissions.length > 0) {
            return requestCode == CAMERA_PERMISSION_REQUEST_CODE &&
                    Manifest.permission.CAMERA.equals(permissions[0]);
        }
        return false;
    }

    public int checkAuthorizationStatus(Activity activity) {
        final int statusCode = PermissionChecker.checkCallingOrSelfPermission(activity, Manifest.permission.CAMERA);
        if (statusCode == PermissionChecker.PERMISSION_GRANTED) {
            return PERMISSION_GRANTED;
        }
        if (requestingPermissionForFirstTime(activity, Manifest.permission.CAMERA)) {
            return PERMISSION_NOT_DETERMINED;
        }
        return PERMISSION_DENIED;
    }

    private boolean requestingPermissionForFirstTime(Activity activity, String permissionName) {
        return !SharedPrefs.getBoolean(activity, permissionName);
    }

    private void permissionRequested(Activity activity, String permissionName) {
        SharedPrefs.putBoolean(activity, permissionName, true);
    }

    private boolean isPermissionGranted(Activity activity) {
        return checkAuthorizationStatus(activity) == PermissionChecker.PERMISSION_GRANTED;
    }
}
