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

public class BlockingFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<AppInfo> appList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blocking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.blocking_recycler_view);
        appList = new ArrayList<>();

        loadLaunchableApps();

        // O getContext() aqui é seguro pois onViewCreated é chamado após o fragment ser anexado.
        BlockingAdapter adapter = new BlockingAdapter(appList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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
}
