package com.example.focusguard;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class PermissionChecker {

    // ... (métodos hasUsageStatsPermission, requestUsageStatsPermission, etc. continuam aqui) ...
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

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static void requestDrawOverlaysPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

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


    // --- MUDANÇA: Novo método para verificar a permissão de bateria ---
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return pm.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true; // Não aplicável em versões antigas
    }

    public static void requestIgnoreBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    // --- MUDANÇA: Novo método para lidar com a permissão de Autostart da MIUI ---
    public static void requestMIUIAutostartPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Não foi possível abrir a tela de Autostart.", Toast.LENGTH_LONG).show();
        }
    }

    // Em PermissionChecker.java
    public static boolean areAllPermissionsGranted(Context context) {
        boolean usageGranted = PermissionChecker.hasUsageStatsPermission(context);
        boolean accessibilityGranted = PermissionChecker.isAccessibilityServiceEnabled(context, Accessibility.class);
        boolean overlayGranted = PermissionChecker.canDrawOverlays(context);
        boolean batteryGranted = PermissionChecker.isIgnoringBatteryOptimizations(context);

        // O Log continua útil aqui
        Log.d("PermissionCheck", "Status -> Uso: " + usageGranted + ", Acessibilidade: " + accessibilityGranted + ", Sobreposição: " + overlayGranted + ", Bateria: " + batteryGranted);

        return usageGranted && accessibilityGranted && overlayGranted && batteryGranted;
    }

}