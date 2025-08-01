package com.example.focusguard;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private final Drawable icon;
    private final String appName;
    private final String packageName;

    public AppInfo(Drawable icon, String appName, String packageName) {
        this.icon = icon;
        this.appName = appName;
        this.packageName = packageName;
    }

    public Drawable getIcon() { return icon; }
    public String getAppName() { return appName; }
    public String getPackageName() { return packageName; }
}
