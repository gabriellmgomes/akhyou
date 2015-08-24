package dulleh.akhyou;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application{
    public static int RED_ACCENT_RGB = 16777215;

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher (Context context) {
        return ((MainApplication) context.getApplicationContext()).refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        refWatcher = LeakCanary.install(this);
    }
}
