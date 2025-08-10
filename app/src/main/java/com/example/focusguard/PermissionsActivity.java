package com.example.focusguard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionsActivity extends AppCompatActivity {
    private Button usageButton, accessibilityButton, overlayButton, batteryButton, miuiButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // A verificação inicial continua aqui.
        if (PermissionChecker.areAllPermissionsGranted(this)) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_onboarding);
        usageButton = findViewById(R.id.btn_permission_usage);
        accessibilityButton = findViewById(R.id.btn_permission_accessibility);
        overlayButton = findViewById(R.id.btn_permission_overlay);
        batteryButton = findViewById(R.id.btn_permission_battery);
        miuiButton = findViewById(R.id.btn_permission_autostart_miui); // Novo botão



        // Mostra o botão da MIUI apenas se o dispositivo for da Xiaomi
        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
            miuiButton.setVisibility(View.VISIBLE);
        }

        usageButton.setOnClickListener(v -> PermissionChecker.requestUsageStatsPermission(this));
        accessibilityButton.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        overlayButton.setOnClickListener(v -> PermissionChecker.requestDrawOverlaysPermission(this));
        batteryButton.setOnClickListener(v -> PermissionChecker.requestIgnoreBatteryOptimizations(this));
        miuiButton.setOnClickListener(v -> PermissionChecker.requestMIUIAutostartPermission(this));
    }

    @Override
    protected void onResume() {

        super.onResume();
        updateButtonStates();

        if (PermissionChecker.areAllPermissionsGranted(this)) {
            goToMainActivity();
        }
    }

    private void updateButtonStates() {
        if (PermissionChecker.hasUsageStatsPermission(this)) {
            setButtonGranted(usageButton, "1. Acesso ao Uso Concedido");
        }
        if (PermissionChecker.isAccessibilityServiceEnabled(this, Accessibility.class)) {
            setButtonGranted(accessibilityButton, "2. Acessibilidade Concedida");
        }
        if (PermissionChecker.canDrawOverlays(this)) {
            setButtonGranted(overlayButton, "3. Sobreposição Concedida");
        }
        // MUDANÇA: A verificação de bateria agora também atualiza o botão
        if (PermissionChecker.isIgnoringBatteryOptimizations(this)) {
            setButtonGranted(batteryButton, "4. Bateria Sem Restrições");
        }
    }

    private void setButtonGranted(Button button, String text) {
        button.setText(text + " ✓");
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGranted));
        button.setEnabled(false);
    }


    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
