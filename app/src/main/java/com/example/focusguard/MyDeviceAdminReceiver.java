package com.example.focusguard;

// --- MUDANÇA: Importações necessárias adicionadas ---
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
// --- FIM DA MUDANÇA ---

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        // Usamos um Log para confirmar que a permissão foi ativada
        Log.d("DeviceAdminReceiver", "Permissão de Admin ativada.");
        // Você também pode mostrar um Toast para o usuário
        Toast.makeText(context, "FocusGuard Modo Hardcore Ativado!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        // E aqui confirmamos que foi desativada
        Log.d("DeviceAdminReceiver", "Permissão de Admin desativada.");
        Toast.makeText(context, "FocusGuard Modo Hardcore Desativado.", Toast.LENGTH_SHORT).show();
    }
}