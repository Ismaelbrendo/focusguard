package com.example.focusguard;

import android.graphics.drawable.Drawable;

// Esta é uma classe POJO (Plain Old Java Object).
// Seu único propósito é guardar os dados de forma organizada para cada
// item da nossa lista.
public class AppUsageInfo {

    // Campos para guardar as informações de cada app
    private final Drawable appIcon;
    private final String appName;
    private final String usageTime;
    private final long totalTimeInMillis; // Guardamos o tempo bruto para ordenação

    // Construtor para inicializar os dados
    public AppUsageInfo(Drawable appIcon, String appName, String usageTime, long totalTimeInMillis) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.usageTime = usageTime;
        this.totalTimeInMillis = totalTimeInMillis;
    }

    // Métodos "Getter" para que o Adapter possa acessar os dados
    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getUsageTime() {
        return usageTime;
    }

    public long getTotalTimeInMillis() {
        return totalTimeInMillis;
    }
}
