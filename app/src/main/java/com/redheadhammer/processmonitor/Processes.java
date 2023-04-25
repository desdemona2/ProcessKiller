package com.redheadhammer.processmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.redheadhammer.processmonitor.databinding.ActivityProcessesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Processes extends AppCompatActivity {

    private List<ProcessInfo> processes = new ArrayList<>(20);
    private ActivityProcessesBinding binding;
    private ProcessAdapter adapter;
    private boolean running = true;
    private boolean sortByCpu = true;
    private Menu menuToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProcessesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Dialog.createDialog(this, "Warning", getString(R.string.warn_kill), "Proceed", 3);

        setAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
    }

    private void startUpdateTask() {
        // TODO: Stop UpdateTask when app goes in background;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (running) {
                        updateList();
                    }
                });
            }
        }, 0, 5000); // 1000 milliseconds = 1 second
    }

    private void setAdapter() {
        adapter = new ProcessAdapter(processes);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);
        startUpdateTask();
    }

    private void updateList() {
        // Get the updated process list
        if (sortByCpu) {
            processes = RunCommand.getProcesses(this, "scripts/cpu_usage.sh");
        } else {
            processes = RunCommand.getProcesses(this, "scripts/mem_usage.sh");
        }
        // Update the adapter data
        adapter.setData(processes);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toggle, menu);
        menuToggle = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.by_cpu) {
            if (sortByCpu) {
                menuToggle.getItem(0).setTitle("Sort By CPU Usage");
            } else {
                menuToggle.getItem(0).setTitle("Sort By Memory Usage");
            }
            sortByCpu = !sortByCpu;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}