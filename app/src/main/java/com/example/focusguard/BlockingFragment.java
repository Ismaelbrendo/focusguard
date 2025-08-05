package com.example.focusguard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import java.util.Collections;
import java.util.List;

public class BlockingFragment extends Fragment implements ConfigureRuleDialogFragment.OnRuleSetListener {

    private RecyclerView recyclerView;
    private List<AppInfo> appList;
    private BlockingAdapter adapter; // <<< ADICIONE ESTA LINHA

    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.blocking_recycler_view);
        appList = new ArrayList<>();

        // Configure the adapter with an empty list initially
        adapter = new BlockingAdapter(appList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Set up the click listener as before
        adapter.setOnAppClickListener(app -> {
            ConfigureRuleDialogFragment dialog = ConfigureRuleDialogFragment.newInstance(app.getPackageName());
            dialog.show(getChildFragmentManager(), "ConfigureRuleDialog");
        });

        // MUDANÇA: Start loading the apps in a background thread
        loadAppsInBackground();
    }

    private void loadAppsInBackground() {
        new Thread(() -> {
            // This code runs on a background thread
            PackageManager pm = getActivity().getPackageManager();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> allApps = pm.queryIntentActivities(mainIntent, 0);

            // Create a temporary list to hold the loaded apps
            List<AppInfo> loadedApps = new ArrayList<>();

            for (ResolveInfo ri : allApps) {
                if (!ri.activityInfo.packageName.equals(getContext().getPackageName())) {
                    AppInfo app = new AppInfo(
                            ri.loadIcon(pm),
                            ri.loadLabel(pm).toString(),
                            ri.activityInfo.packageName
                    );
                    loadedApps.add(app);
                }
            }

            // Sort the temporary list
            loadedApps.sort((a1, a2) -> a1.getAppName().compareTo(a2.getAppName()));

            // When done, post the result back to the main UI thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // This code runs on the UI thread
                    appList.clear();
                    appList.addAll(loadedApps);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }





    private void loadLaunchableApps() {
        PackageManager pm = getActivity().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(mainIntent, 0);
        appList.clear(); // Limpa a lista antes de adicionar novos itens

        for (ResolveInfo ri : allApps) {
            // Ignora o nosso próprio app da lista de bloqueio
            if (!ri.activityInfo.packageName.equals(getContext().getPackageName())) {
                AppInfo app = new AppInfo(
                        ri.loadIcon(pm),
                        ri.loadLabel(pm).toString(),
                        ri.activityInfo.packageName
                );
                appList.add(app);
            }
        }

        // Ordena a lista de apps em ordem alfabética
        appList.sort((a1, a2) -> a1.getAppName().compareTo(a2.getAppName()));
    }

    @Override
    public void onRuleSaved(BlockingRule rule) {
        // Avisa o adapter para adicionar ou atualizar a regra
        adapter.addOrUpdateRule(rule);
        // Notifica o adapter que os dados mudaram para que ele atualize a UI
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onRuleRemoved(String packageName) {
        // Avisa o adapter para remover a regra
        adapter.removeRule(packageName);
        // Notifica o adapter que os dados mudaram
        adapter.notifyDataSetChanged();

    }
}
