package com.example.focusguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockingAdapter extends RecyclerView.Adapter<BlockingAdapter.ViewHolder> {

    private final List<AppInfo> appList;
    private final SharedPreferences sharedPreferences;
    private final List<BlockingRule> rules;
    private OnAppClickListener clickListener;

    public interface OnAppClickListener {
        void onAppClicked(AppInfo app);
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        this.clickListener = listener;
    }

    public BlockingAdapter(List<AppInfo> appList, Context context) {
        this.appList = appList;
        this.sharedPreferences = context.getSharedPreferences("BlockedApps", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("rules_list", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<BlockingRule>>() {}.getType();
            this.rules = gson.fromJson(json, type);
        } else {
            this.rules = new ArrayList<>();
        }
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

        // MUDANÇA: Adiciona o feedback visual
        if (doesRuleExist(currentApp.getPackageName())) {
            holder.itemView.setAlpha(0.5f); // Deixa o item com 50% de transparência
        } else {
            holder.itemView.setAlpha(1.0f); // Garante a aparência normal
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAppClicked(currentApp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    private void saveRules() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(rules);
        editor.putString("rules_list", json);
        editor.apply();
    }

    private boolean doesRuleExist(String packageName) {
        for (BlockingRule rule : rules) {
            if (rule.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public void addOrUpdateRule(BlockingRule newRule) {
        removeRule(newRule.getPackageName());
        rules.add(newRule);
        saveRules();
    }

    public void removeRule(String packageName) {
        Iterator<BlockingRule> iterator = rules.iterator();
        while (iterator.hasNext()) {
            BlockingRule rule = iterator.next();
            if (rule.getPackageName().equals(packageName)) {
                iterator.remove();
                saveRules();
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        // MUDANÇA: SwitchCompat removido daqui

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon_blocking);
            appName = itemView.findViewById(R.id.app_name_blocking);
            // MUDANÇA: e daqui
        }
    }
}