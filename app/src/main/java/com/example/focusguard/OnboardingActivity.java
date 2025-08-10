package com.example.focusguard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class OnboardingActivity extends AppCompatActivity {
    private Button letsDoit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.onboarding_01);

        letsDoit = findViewById(R.id.btn_lets_doit);
        letsDoit.setOnClickListener(v -> goToPermissionsActivity());

    }

    private void goToPermissionsActivity() {
        Intent intent = new Intent(this, PermissionsActivity.class);
        startActivity(intent);
        finish();
    }


}