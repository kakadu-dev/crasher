package bz.kakadu.crasher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class CrasherActivity extends Activity {
    private static final String EXTRA_STACK_TRACE = "stackTrace";

    static void start(Context context, Throwable throwable) {
        Intent intent = new Intent(context, CrasherActivity.class);
        intent.putExtra(EXTRA_STACK_TRACE, Log.getStackTraceString(throwable));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, 0);
        super.onCreate(savedInstanceState);
        int dialogTheme = Build.VERSION.SDK_INT >= 23 ? android.R.style.Theme_DeviceDefault_Light_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        AlertDialog.Builder adb = new AlertDialog.Builder(this, dialogTheme);
        adb.setTitle(getSystemString("error_message_title", "Error"))
                .setMessage(getIntent().getStringExtra(EXTRA_STACK_TRACE))
                .setPositiveButton(getSystemString("ime_action_send", "Send"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEmail();
                    }
                });
        AlertDialog alert = adb.show();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        CharSequence name = getPackageName();
        int appVersion = 0;
        String appVersionName = "?";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            name = appInfo.loadLabel(getPackageManager());
            appVersion = pkgInfo.versionCode;
            appVersionName = pkgInfo.versionName;
        } catch (Exception ignored) {
        }
        String data = "Device: " + Build.BRAND + " " + Build.MODEL + " " + Build.DEVICE
                + "\nSDK: " + Build.VERSION.SDK_INT
                + "\n\n--------------- App ---------------"
                + "\npackage: " + getPackageName()
                + "\nversion: " + appVersionName + " (" + appVersion + ")"
                + "\n-----------------------------------\n"
                + getIntent().getStringExtra(EXTRA_STACK_TRACE);
        intent.putExtra(Intent.EXTRA_SUBJECT, getSystemString("aerr_application", "Crash in " + name, name));
        intent.putExtra(Intent.EXTRA_TEXT, data);
        intent.setData(Uri.parse("mailto:" + Crasher.instance.email));
        startActivity(intent);
    }

    private String getSystemString(String systemResName, String defaultString, Object... args) {
        int resId = getResources().getIdentifier(systemResName, "string", "android");
        if (resId != 0) {
            try {
                return getString(resId, args);
            } catch (Exception ignore) {
            }
        }
        return defaultString;
    }
}
