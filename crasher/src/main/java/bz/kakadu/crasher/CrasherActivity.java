package bz.kakadu.crasher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
        AlertDialog.Builder adb = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        adb.setTitle("Ошибка")
                .setMessage(getIntent().getStringExtra(EXTRA_STACK_TRACE))
                .setPositiveButton("Отправить разработчику", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEmail();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                })
                .show();
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        CharSequence name = getPackageName();
        try {
            name = getPackageManager().getApplicationInfo(getPackageName(), 0).loadLabel(getPackageManager());
        } catch (Exception ignored) {
        }
        String data = "Device: " + Build.BRAND + " " + Build.MODEL + " " + Build.DEVICE
                + "\nSDK: " + Build.VERSION.SDK_INT
                + "\nApp package: " + getPackageName() + "\n\n"
                + getIntent().getStringExtra(EXTRA_STACK_TRACE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ошибка в " + name);
        intent.putExtra(Intent.EXTRA_TEXT, data);
        intent.setData(Uri.parse("mailto:" + Crasher.instance.email));
        startActivity(intent);
    }
}
