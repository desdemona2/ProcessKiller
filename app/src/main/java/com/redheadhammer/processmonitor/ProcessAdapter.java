package com.redheadhammer.processmonitor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.redheadhammer.processmonitor.databinding.ProcessItemBinding;

import java.util.List;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ItemView> {
    private ProcessItemBinding binding;
    private List<ProcessInfo> processesList;

    public ProcessAdapter(List<ProcessInfo> processesList) {
        this.processesList = processesList;
    }

    @NonNull
    @Override
    public ProcessAdapter.ItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ProcessItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemView(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemView holder, int position) {
        ProcessInfo item = processesList.get(position);
        holder.fullItem.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Warning");
            builder.setMessage("Do you really want to kill the package: " + item.getPackageName());
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.setPositiveButton("Yes", (dialogInterface, i) -> RunCommand.killProcess(binding.getRoot().getContext(), "kill_process.sh", item.getPid()));
            builder.show();
        });
        holder.processId.setText(String.format("pid: %s", item.getPid()));
        holder.cpuUsage.setText(item.getCpuUsage());
        holder.memoryUsage.setText(item.getMemUsage());
        holder.processName.setText(item.getAppName());


        // TODO: This making app slow so need to find a way to make it simple and faster
        /*
        Drawable icon = getIcon(binding.getRoot().getContext(), item.getPackageName());
        if (icon != null) {
            holder.icon.setImageDrawable(icon);
        }
        */
    }

    @Override
    public int getItemCount() {
        return processesList.size();
    }

    public static class ItemView extends RecyclerView.ViewHolder {
        ConstraintLayout fullItem;
        TextView processId, cpuUsage, memoryUsage, processName;
        ImageView icon;
        public ItemView(ProcessItemBinding binding) {
            super(binding.getRoot());
            fullItem = binding.fullItem;
            processId = binding.processId;
            cpuUsage = binding.cpuUsage;
            memoryUsage = binding.memoryUsage;
            processName = binding.processName;
            icon = binding.appIcon;
        }
    }

    public void setData(List<ProcessInfo> processes) {
        this.processesList = processes;
    }

    private Drawable getIcon(Context context, String packageName) {
        // Get the PackageManager
        PackageManager pm = context.getPackageManager();

        // Get the ApplicationInfo for the package name
        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Drawable appIcon = null;
        if (info != null) {
            appIcon = pm.getApplicationIcon(info);
        }

        return appIcon;
    }
}
