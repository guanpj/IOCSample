package com.me.guanpj.ioclib.runtime;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;

public class ActivityBuilder {
    public static final String BUILDER_NAME_POSIX = "Builder";

    public static final ActivityBuilder INSTANCE = new ActivityBuilder();

    private Application application;

    public void init(Context context) {
        if (this.application == null) {
            this.application = (Application) context.getApplicationContext();
            this.application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
    }

    public void startActivity(Context context, Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @NonNull Bundle savedInstanceState) {
            performInject(activity, savedInstanceState);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            performSaveState(activity, outState);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };

    private void performInject(Activity activity, Bundle savedInstanceState) {
        try {
            Class.forName(activity.getClass().getName() + BUILDER_NAME_POSIX)
                    .getDeclaredMethod("inject", Activity.class, Bundle.class)
                    .invoke(null, activity, savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSaveState(Activity activity, Bundle outState) {
        try {
            Class.forName(activity.getClass().getName() + BUILDER_NAME_POSIX)
                    .getDeclaredMethod("saveState", Activity.class, Bundle.class)
                    .invoke(null, activity, outState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
