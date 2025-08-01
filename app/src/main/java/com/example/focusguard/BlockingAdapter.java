package com.example.focusguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockingAdapter extends RecyclerView.Adapter<BlockingAdapter.ViewHolder> {

    private final List<AppInfo> appList;
    private final SharedPreferences sharedPreferences;
    private final Set<String> blockedApps;

    public BlockingAdapter(List<AppInfo> appList, Context context) {
        this.appList = appList;
        // Acessa o arquivo de SharedPreferences para salvar/ler a lista de bloqueio
        this.sharedPreferences = context.getSharedPreferences("BlockedApps", Context.MODE_PRIVATE);
        // Carrega a lista de apps bloqueados já salva
        this.blockedApps = new HashSet<>(sharedPreferences.getStringSet("blacklist", new HashSet<>()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blocking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo currentApp = appList.get(position);
        holder.appName.setText(currentApp.getAppName());
        holder.appIcon.setImageDrawable(currentApp.getIcon());

        // Define o estado inicial do switch baseado na lista salva
        holder.blockSwitch.setChecked(blockedApps.contains(currentApp.getPackageName()));

        // Adiciona um "ouvinte" para quando o switch for alterado
        holder.blockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Se o switch foi ligado, adiciona o pacote à lista
                blockedApps.add(currentApp.getPackageName());
            } else {
                // Se foi desligado, remove o pacote
                blockedApps.remove(currentApp.getPackageName());
            }
            // Salva a lista atualizada no SharedPreferences
            saveBlockedApps();
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    private void saveBlockedApps() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("blacklist", blockedApps);
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        SwitchCompat blockSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon_blocking);
            appName = itemView.findViewById(R.id.app_name_blocking);
            blockSwitch = itemView.findViewById(R.id.block_switch);
        }
    }
}
