package com.example.focusguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashSet;
import java.util.Set;

public class Accessibility extends AccessibilityService {

    private Set<String> blacklist;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // Carrega as preferências quando o serviço é conectado
        sharedPreferences = getSharedPreferences("BlockedApps", MODE_PRIVATE);
        loadBlacklist();
        Log.d("AccessService", "Serviço de Acessibilidade conectado e blacklist carregada!");
    }

    private void loadBlacklist() {
        // Carrega o Set de strings do SharedPreferences. Se não houver nada, cria um Set vazio.
        blacklist = new HashSet<>(sharedPreferences.getStringSet("blacklist", new HashSet<>()));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Recarrega a blacklist a cada evento para garantir que está sempre atualizada
        loadBlacklist();

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo sourceNode = event.getSource();
            if (sourceNode == null) {
                return;
            }

            CharSequence packageName = sourceNode.getPackageName();

            if (packageName != null) {
                String packageNameString = packageName.toString();
                Log.d("AccessService", "App em primeiro plano: " + packageNameString);

                if (packageNameString.equals(getPackageName())) {
                    sourceNode.recycle();
                    return;
                }

                if (blacklist != null && blacklist.contains(packageNameString)) {
                    Intent intent = new Intent(this, BlockingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    Log.d("AccessService", "BLOQUEANDO: " + packageNameString);
                }
            }
            sourceNode.recycle();
        }
    }

    @Override
    public void onInterrupt() {
        Log.d("AccessService", "Serviço interrompido.");
    }
}
