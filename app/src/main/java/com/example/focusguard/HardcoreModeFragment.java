package com.example.focusguard;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class HardcoreModeFragment extends Fragment {
    private Button btnActivate;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminComponent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Carrega o nosso novo layout
        return inflater.inflate(R.layout.fragment_hardcore_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa o botão da nossa UI
        btnActivate = view.findViewById(R.id.btn_activate_hardcore);

        // Garante que o contexto não seja nulo antes de usar
        Context context = getActivity();
        if (context == null) return;

        // Inicializa os componentes do sistema necessários para a verificação
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminComponent = new ComponentName(context, MyDeviceAdminReceiver.class);

        // Configura o listener de clique do botão
        setupClickListener();
    }

    private boolean isAdminActive() {
        // Pergunta ao sistema se nosso componente é um administrador ativo
        if (devicePolicyManager != null && deviceAdminComponent != null) {
            return devicePolicyManager.isAdminActive(deviceAdminComponent);
        }
        return false;
    }

    private void updateButtonState() {
        // Garante que o contexto e o botão não sejam nulos
        if (getContext() == null || btnActivate == null) {
            return;
        }

        if (isAdminActive()) {
            // Se estiver ativo, deixa o botão verde, muda o texto e o desativa
            btnActivate.setText("Modo Hardcore Ativado ✓");
            btnActivate.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGranted)); // Supondo que você tenha essa cor
            btnActivate.setEnabled(false);
        } else {
            // Se não, garante que ele esteja no estado normal
            btnActivate.setText("Ativar Modo Hardcore");
            // Para resetar a cor, precisamos de uma referência à cor original ou definimos uma padrão
            btnActivate.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.android.material.R.color.design_default_color_primary));
            btnActivate.setEnabled(true);
        }
    }

    private void setupClickListener() {
        btnActivate.setOnClickListener(v -> {
            // Se o admin NÃO estiver ativo, pedimos a permissão
            if (!isAdminActive()) {
                // Cria um Intent para a tela de ativação de admin do sistema
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

                // Informa ao sistema qual é o nosso receiver de administração
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent);

                // Adiciona uma explicação para o usuário na tela de permissão
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Ativar esta permissão impede a desinstalação do FocusGuard, ajudando a manter seu foco.");

                // Inicia a tela de permissão do sistema
                startActivity(intent);
            }
        });
    } // FIM DO MÉTODO setupClickListener

    // MUDANÇA: O método onResume foi movido para fora do setupClickListener,
    // para o nível da classe, que é o lugar correto.
    @Override
    public void onResume() {
        super.onResume();
        // Sempre que o fragmento se torna visível, atualizamos o estado do botão
        updateButtonState();
    }
}