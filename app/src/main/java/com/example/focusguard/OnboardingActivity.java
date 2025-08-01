package com.example.focusguard;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log; // Importante adicionar o Log
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class OnboardingActivity extends AppCompatActivity {

    private Button usageButton;
    private Button accessibilityButton;
    private Button overlayButton;
    private Button batteryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // A verificação inicial continua aqui.
        if (areAllPermissionsGranted()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        usageButton = findViewById(R.id.btn_permission_usage);
        accessibilityButton = findViewById(R.id.btn_permission_accessibility);
        overlayButton = findViewById(R.id.btn_permission_overlay);
        batteryButton = findViewById(R.id.btn_permission_battery);

        usageButton.setOnClickListener(v -> PermissionChecker.requestUsageStatsPermission(this));
        accessibilityButton.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        overlayButton.setOnClickListener(v -> PermissionChecker.requestDrawOverlaysPermission(this));
        batteryButton.setOnClickListener(v -> PermissionChecker.requestIgnoreBatteryOptimizations(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // A lógica principal agora acontece aqui.
        // Primeiro, atualizamos a aparência dos botões com base no status atual.
        updateButtonStates();

        // DEPOIS, verificamos se TUDO foi concedido para então avançar.
        if (areAllPermissionsGranted()) {
            goToMainActivity();
        }
    }

    private void updateButtonStates() {
        // --- Botão de Acesso ao Uso ---
        if (PermissionChecker.hasUsageStatsPermission(this)) {
            setButtonGranted(usageButton, "1. Acesso ao Uso Concedido");
        }

        // --- Botão de Acessibilidade ---
        if (PermissionChecker.isAccessibilityServiceEnabled(this, Accessibility.class)) {
            setButtonGranted(accessibilityButton, "2. Acessibilidade Concedida");
        }

        // --- Botão de Sobreposição ---
        if (PermissionChecker.canDrawOverlays(this)) {
            setButtonGranted(overlayButton, "3. Sobreposição Concedida");
        }
    }

    private void setButtonGranted(Button button, String text) {
        button.setText(text + " ✓");
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGranted));
        button.setEnabled(false);
    }

    private boolean areAllPermissionsGranted() {
        // Separamos cada verificação em uma variável para o log.
        boolean usageGranted = PermissionChecker.hasUsageStatsPermission(this);
        boolean accessibilityGranted = PermissionChecker.isAccessibilityServiceEnabled(this, Accessibility.class);
        boolean overlayGranted = PermissionChecker.canDrawOverlays(this);

        // --- LOG DE DIAGNÓSTICO ---
        // Esta linha nos dirá no Logcat o status de cada permissão.
        Log.d("OnboardingCheck", "Status das Permissões -> Uso: " + usageGranted + ", Acessibilidade: " + accessibilityGranted + ", Sobreposição: " + overlayGranted);

        // A condição final permanece a mesma, mas agora podemos ver os valores individuais.
        return usageGranted && accessibilityGranted && overlayGranted;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
