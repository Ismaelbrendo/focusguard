package com.example.focusguard;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SiteBlockingFragment extends Fragment {

    private Button toggleVpnButton;
    private static final int VPN_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_site_blocking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleVpnButton = view.findViewById(R.id.btn_toggle_vpn);

        toggleVpnButton.setOnClickListener(v -> {
            if (FocusGuardVpnService.isRunning) {
                stopVpnService();
            } else {
                promptForVpnPermission();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Atualiza a aparência do botão sempre que o ecrã fica visível
        updateButtonState();
    }

    private void updateButtonState() {
        if (toggleVpnButton == null) return;
        if (FocusGuardVpnService.isRunning) {
            toggleVpnButton.setText("Desativar Bloqueio de Sites");
        } else {
            toggleVpnButton.setText("Ativar Bloqueio de Sites");
        }
    }

    private void promptForVpnPermission() {
        if (getContext() == null) return;
        Intent vpnIntent = VpnService.prepare(getContext());
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        } else {
            // Permissão já concedida, podemos iniciar o serviço
            startVpnService();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startVpnService();
        } else {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Permissão de VPN negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startVpnService() {
        if (getActivity() == null) return;
        Intent serviceIntent = new Intent(getActivity(), FocusGuardVpnService.class);
        getActivity().startService(serviceIntent);
        updateButtonState();
        Toast.makeText(getActivity(), "Serviço de Bloqueio iniciado!", Toast.LENGTH_SHORT).show();
    }

    private void stopVpnService() {
        if (getActivity() == null) return;
        Intent serviceIntent = new Intent(getActivity(), FocusGuardVpnService.class);
        getActivity().stopService(serviceIntent);
        updateButtonState();
        Toast.makeText(getActivity(), "Serviço de Bloqueio parado.", Toast.LENGTH_SHORT).show();
    }
}
