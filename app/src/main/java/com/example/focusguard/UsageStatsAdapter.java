package com.example.focusguard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UsageStatsAdapter extends RecyclerView.Adapter<UsageStatsAdapter.ViewHolder> {

    private final List<AppUsageInfo> appUsageList;

    // O construtor do Adapter recebe a lista de dados que ele vai exibir.
    public UsageStatsAdapter(List<AppUsageInfo> appUsageList) {
        this.appUsageList = appUsageList;
    }

    // Este método é chamado para criar uma nova "linha" (ViewHolder) na tela.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Inflamos" o nosso layout de item XML para transformá-lo em uma View.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usage_stats_item, parent, false);
        return new ViewHolder(view);
    }

    // Este método é chamado para preencher uma linha com os dados de uma posição específica.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Pegamos o objeto de dados da posição atual.
        AppUsageInfo currentApp = appUsageList.get(position);

        // Usamos os dados do objeto para preencher as Views do ViewHolder.
        holder.appIcon.setImageDrawable(currentApp.getAppIcon());
        holder.appName.setText(currentApp.getAppName());
        holder.usageTime.setText(currentApp.getUsageTime());
    }

    // Este método simplesmente informa ao RecyclerView quantos itens existem na lista.
    @Override
    public int getItemCount() {
        return appUsageList.size();
    }


    // ViewHolder: Representa uma única linha da nossa lista.
    // Ele "segura" as Views (ImageView, TextViews) para que não precisemos
    // chamar findViewById repetidamente, o que otimiza a performance.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView usageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            usageTime = itemView.findViewById(R.id.usage_time);
        }
    }
}
