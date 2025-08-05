package com.example.focusguard;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class Accessibility extends AccessibilityService {
    // Em Accessibility.java
    private static final String NOTIFICATION_CHANNEL_ID = "FocusGuardServiceChannel";
    private static final int NOTIFICATION_ID = 123; // Um número qualquer

    // Em Accessibility.java
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel serviceChannel = new android.app.NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "FocusGuard Active",
                    android.app.NotificationManager.IMPORTANCE_LOW // Usamos baixa importância para não fazer som
            );
            android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    // Em Accessibility.java
    private android.app.Notification createNotification() {
        // Um Intent que será disparado se o utilizador tocar na notificação (abre a MainActivity)
        android.content.Intent notificationIntent = new android.content.Intent(this, MainActivity.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(this, 0, notificationIntent, android.app.PendingIntent.FLAG_IMMUTABLE);

        // Constrói a notificação
        return new androidx.core.app.NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("FocusGuard Está Ativo")
                .setContentText("A proteger o seu foco.")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Use um ícone seu
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Torna a notificação não dispensável pelo utilizador
                .build();
    }


    // Em Accessibility.java
    @SuppressLint("ForegroundServiceType")
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("AccessService", "Serviço de Acessibilidade conectado!");

        // --- INÍCIO DA NOVA LÓGICA ---
        createNotificationChannel();
        android.app.Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
        // --- FIM DA NOVA LÓGICA ---
    }

    // MUDANÇA: Método para carregar a nova lista de regras em JSON
    private List<BlockingRule> loadRules() {
        SharedPreferences sharedPreferences = getSharedPreferences("BlockedApps", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("rules_list", null);
        List<BlockingRule> rules = new ArrayList<>();

        if (json != null) {
            Type type = new TypeToken<ArrayList<BlockingRule>>() {}.getType();
            rules = gson.fromJson(json, type);
        }
        return rules;
    }

    // Não se esqueça de adicionar o Set de browserPackages no início da sua classe
    private final Set<String> browserPackages = new HashSet<>(Arrays.asList(
            "com.android.chrome", "org.mozilla.firefox", "com.opera.browser",
            "com.sec.android.app.sbrowser", "com.brave.browser", "com.microsoft.emmx"
    ));

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Usamos TYPE_WINDOW_CONTENT_CHANGED também, pois a URL muda sem a janela mudar
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return;
        }

        AccessibilityNodeInfo sourceNode = event.getSource();
        if (sourceNode == null) {
            return;
        }

        String packageName = (sourceNode.getPackageName() != null) ? sourceNode.getPackageName().toString() : null;
        if (packageName == null) {
            sourceNode.recycle();
            return;
        }

        // --- NOVA LÓGICA DE BLOQUEIO DE SITES ---
        if (browserPackages.contains(packageName)) {
            String url = getUrlFromBrowser(sourceNode);
            if (url != null) {
                Log.d("AccessService", "URL detetada: " + url);
                // TODO: Aqui você vai chamar a sua lógica para verificar se a URL está na lista de bloqueio de sites.
                // Exemplo:
                // List<String> blockedSites = loadBlockedSitesRules();
                // for (String blockedSite : blockedSites) {
                //     if (url.contains(blockedSite)) {
                //         launchBlockingActivity(packageName + " (Site Bloqueado)");
                //         sourceNode.recycle();
                //         return;
                //     }
                // }
            }
        }

        // --- LÓGICA ANTIGA E AINDA VÁLIDA PARA BLOQUEIO DE APPS ---
        // (A sua lógica de autoproteção das configurações pode vir aqui também)

        // Ignora o nosso próprio app
        if (packageName.equals(getPackageName())) {
            sourceNode.recycle();
            return;
        }

        List<BlockingRule> rules = loadRules(); // O seu método para carregar regras de apps
        for (BlockingRule rule : rules) {
            if (rule.getPackageName().equals(packageName)) {
                if (rule.getBlockingType() == BlockingType.ALWAYS) {
                    launchBlockingActivity(packageName);
                    sourceNode.recycle();
                    return;
                }
                // TODO: Lógica para outros tipos de bloqueio
            }
        }

        sourceNode.recycle();
    }


    // Método auxiliar para extrair a URL (coloque-o na sua classe também)
    private String getUrlFromBrowser(AccessibilityNodeInfo sourceNode) {
        if (sourceNode == null) return null;

        List<String> urlBarIds = Arrays.asList(
                "com.android.chrome:id/url_bar",
                "org.mozilla.firefox:id/url_bar_title",
                "com.sec.android.app.sbrowser:id/location_bar_edit_text",
                "com.opera.browser:id/url_field",
                "com.brave.browser:id/url_bar",
                "com.microsoft.emmx:id/url_bar"
        );

        for (String id : urlBarIds) {
            List<AccessibilityNodeInfo> nodes = sourceNode.findAccessibilityNodeInfosByViewId(id);
            if (nodes != null && !nodes.isEmpty()) {
                AccessibilityNodeInfo urlNode = nodes.get(0);
                if (urlNode != null && urlNode.getText() != null) {
                    String url = urlNode.getText().toString();
                    // Limpeza básica para garantir que obtemos o domínio principal
                    url = url.replace("https://", "").replace("http://", "");
                    if (url.contains("/")) {
                        url = url.substring(0, url.indexOf('/'));
                    }
                    urlNode.recycle();
                    nodes.forEach(AccessibilityNodeInfo::recycle);
                    return url;
                }
            }
        }
        return null;
    }

    private void launchBlockingActivity(String packageName) {
        Intent intent = new Intent(this, BlockingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        Log.d("AccessService", "BLOQUEANDO: " + packageName);
    }


    @Override
    public void onInterrupt() {
        Log.d("AccessService", "Serviço interrompido.");
    }
}