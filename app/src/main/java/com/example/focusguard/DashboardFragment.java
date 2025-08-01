package com.example.focusguard;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsageStatsAdapter adapter;
    private final List<AppUsageInfo> appUsageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.usage_stats_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsageStatsAdapter(appUsageList);
        recyclerView.setAdapter(adapter);

        loadUsageStats();
    }

    private void loadUsageStats() {
        Context context = getContext();
        if (context == null) return;

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager pm = context.getPackageManager();

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long beginTime = calendar.getTimeInMillis();

        // --- MUDANÇA PRINCIPAL AQUI ---
        // Usamos o método 'queryAndAggregateUsageStats' que é mais preciso.
        // Ele já retorna um Mapa com os dados agregados corretamente.
        Map<String, UsageStats> aggregatedStatsMap = usm.queryAndAggregateUsageStats(beginTime, endTime);

        appUsageList.clear();

        // Agora, iteramos sobre os valores do mapa.
        for (UsageStats usageStats : aggregatedStatsMap.values()) {
            long timeInForeground = usageStats.getTotalTimeInForeground();

            if (timeInForeground > 0) {
                try {
                    String packageName = usageStats.getPackageName();
                    Drawable icon = pm.getApplicationIcon(packageName);
                    String appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();

                    long minutes = (timeInForeground / (1000 * 60)) % 60;
                    long hours = (timeInForeground / (1000 * 60 * 60));
                    String usageTime = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";

                    appUsageList.add(new AppUsageInfo(icon, appName, usageTime, timeInForeground));
                } catch (PackageManager.NameNotFoundException e) {
                    // Ignora apps que foram desinstalados ou não podem ser encontrados
                }
            }
        }

        appUsageList.sort((o1, o2) -> Long.compare(o2.getTotalTimeInMillis(), o1.getTotalTimeInMillis()));

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }
    }
}
