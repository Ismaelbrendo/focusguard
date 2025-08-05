package com.example.focusguard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfigureRuleDialogFragment extends DialogFragment {

    public interface OnRuleSetListener {
        void onRuleSaved(BlockingRule rule);
        void onRuleRemoved(String packageName);
    }

    private OnRuleSetListener listener;

    private ImageView appIcon;
    private TextView appName;
    private RadioGroup radioGroupBlockType;
    private RadioButton radioAlways, radioSchedule, radioUsageLimit;
    private LinearLayout scheduleOptionsLayout, usageLimitOptionsLayout;
    private EditText editStartTime, editEndTime, editTimeLimit;
    private Button btnRemove, btnSave;

    private String packageName;

    public static ConfigureRuleDialogFragment newInstance(String packageName) {
        ConfigureRuleDialogFragment fragment = new ConfigureRuleDialogFragment();
        Bundle args = new Bundle();
        args.putString("packageName", packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageName = getArguments().getString("packageName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_configure_rule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroupBlockType = view.findViewById(R.id.radiogroup_block_type);
        scheduleOptionsLayout = view.findViewById(R.id.schedule_options_layout);
        usageLimitOptionsLayout = view.findViewById(R.id.usage_limit_options_layout);
        btnSave = view.findViewById(R.id.btn_save);
        btnRemove = view.findViewById(R.id.btn_remove_rule);
        radioAlways = view.findViewById(R.id.radio_always);

        // TODO: Encontrar os outros IDs (appIcon, appName, EditTexts, etc.)

        setupListeners();
        // Marca "Sempre Bloqueado" como padrão ao abrir
        radioAlways.setChecked(true);
    }

    private void setupListeners() {
        radioGroupBlockType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_schedule) {
                scheduleOptionsLayout.setVisibility(View.VISIBLE);
                usageLimitOptionsLayout.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_usage_limit) {
                scheduleOptionsLayout.setVisibility(View.GONE);
                usageLimitOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                scheduleOptionsLayout.setVisibility(View.GONE);
                usageLimitOptionsLayout.setVisibility(View.GONE);
            }
        });

        // MUDANÇA: Lógica do botão salvar foi completada
        btnSave.setOnClickListener(v -> {
            BlockingRule rule = null;
            int checkedRadioButtonId = radioGroupBlockType.getCheckedRadioButtonId();

            if (checkedRadioButtonId == R.id.radio_always) {
                rule = new BlockingRule(BlockingType.ALWAYS, packageName, 0, null);
            } else if (checkedRadioButtonId == R.id.radio_schedule) {
                // TODO: Ler e validar os valores dos EditTexts de horário
                String scheduleData = "A SER IMPLEMENTADO";
                rule = new BlockingRule(BlockingType.SCHEDULE, packageName, 0, scheduleData);
            } else if (checkedRadioButtonId == R.id.radio_usage_limit) {
                // TODO: Ler e validar o valor do EditText de limite de tempo
                long timeLimit = 0; // Converter o texto para long
                rule = new BlockingRule(BlockingType.USAGE_LIMIT, packageName, timeLimit, null);
            }

            if (rule != null) {
                if (listener != null) {
                    listener.onRuleSaved(rule);
                }
            } else {
                // Caso nenhum radio button esteja selecionado (improvável, mas seguro)
                Toast.makeText(getContext(), "Por favor, selecione um tipo de bloqueio.", Toast.LENGTH_SHORT).show();
                return; // Não fecha o diálogo
            }
            dismiss();
        });

        btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRuleRemoved(packageName);
            }
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Tenta obter o listener a partir do Fragmento pai
        if (getParentFragment() instanceof OnRuleSetListener) {
            listener = (OnRuleSetListener) getParentFragment();
        } else {
            // Se o fragmento pai não implementar, lança um erro para nos avisar do problema.
            throw new RuntimeException(getParentFragment().toString()
                    + " must implement OnRuleSetListener");
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        // Pega a janela do diálogo
        android.view.Window window = getDialog().getWindow();
        if (window != null) {
            // Força o diálogo a ter a largura máxima da tela (MATCH_PARENT)
            // e a altura necessária para caber todo o conteúdo (WRAP_CONTENT).
            // A altura WRAP_CONTENT é a chave para resolver o problema.
            window.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}