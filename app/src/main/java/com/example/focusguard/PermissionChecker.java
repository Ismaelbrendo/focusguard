package com.example.focusguard;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;

public class PermissionChecker {

    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static void requestUsageStatsPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
    }

    // --- MÉTODO NOVO PARA VERIFICAR A PERMISSÃO DE SOBREPOSIÇÃO ---
    public static boolean canDrawOverlays(Context context) {
        // A partir do Android 6.0 (API 23), esta verificação é necessária.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        // Em versões mais antigas, a permissão é concedida na instalação.
        return true;
    }

    // --- MÉTODO NOVO PARA PEDIR A PERMISSÃO DE SOBREPOSIÇÃO ---
    public static void requestDrawOverlaysPermission(Context context) {
        // A partir do Android 6.0 (API 23), precisamos de um Intent especial.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
    // Adicione este método à sua classe PermissionChecker.java

    public static void requestIgnoreBatteryOptimizations(Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
    // Adicione este método à sua classe PermissionChecker.java

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> serviceClass) {
        String serviceId = context.getPackageName() + "/" + serviceClass.getName();
        try {
            int accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);

            if (accessibilityEnabled == 1) {
                String settingValue = Settings.Secure.getString(
                        context.getApplicationContext().getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (settingValue != null) {
                    return settingValue.contains(serviceId);
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            // Log do erro, se necessário
        }
        return false;
    }


}
