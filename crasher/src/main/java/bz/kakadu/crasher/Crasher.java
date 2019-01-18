package bz.kakadu.crasher;

import android.app.Application;
import android.os.Process;
import android.util.Log;

/**
 * Created by Roman Tsarou on 18.01.2019.
 */
public final class Crasher {
    static Crasher instance;
    final String email;
    private final Application app;
    private final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            String stacktrace = Log.getStackTraceString(e);
            Log.e("Crasher", stacktrace);
            CrasherActivity.start(app, e);
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    };

    private Crasher(Application app, String email) {
        this.app = app;
        this.email = email;
        Log.i("rom", "default: " + Thread.getDefaultUncaughtExceptionHandler());
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    }

    public static void init(Application app, String email) {
        if (instance == null) {
            instance = new Crasher(app, email);
        }
    }
}
