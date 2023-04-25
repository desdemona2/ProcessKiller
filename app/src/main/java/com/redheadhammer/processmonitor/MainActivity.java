package com.redheadhammer.processmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.redheadhammer.processmonitor.databinding.ActivityMainBinding;
import com.topjohnwu.superuser.NoShellException;
import com.topjohnwu.superuser.Shell;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private boolean PRODUCTION = false;

    private ActivityMainBinding binding;
    private SharedPreferences preferences;
    private static final String TAG = "MainActivity";
    static {
        // Set settings before the main shell can be created
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setStatusActionBar();

        setContentView(binding.getRoot());

        preferences = getSharedPreferences("ROOT", MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean("FIRST_RUN", true);


        if (isFirstRun || PRODUCTION) {
            binding.rootImage.setOnClickListener(this::requestRoot);
            binding.noRootText.setOnClickListener(this::setNoRoot);
            Dialog.createDialog(this, "Info", getString(R.string.note_msg), "OK", 1);
        } else {
            launchProcesses();
        }
    }

    private void disableFirstRun() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FIRST_RUN", false);
        editor.apply();
    }

    private void setNoRoot(View view) {
        disableFirstRun();
        launchProcesses();
    }

    private void launchProcesses() {
        Intent intent = new Intent(MainActivity.this, Processes.class);
        startActivity(intent);
        finish();
    }

    private void requestRoot(View view) {
        disableFirstRun();
        try {
            Shell.Result result = Shell.cmd("echo Hello World!").exec();
            if (result.isSuccess()) {
                launchProcesses();
            } else {
                Toast.makeText(this, "Root permission Failed!", Toast.LENGTH_SHORT).show();
            }
        } catch (NoShellException e) {
            Log.d(TAG, "requestRoot: No Response on request root permission: " + e);
        }

    }

    private void setStatusActionBar() {
        Objects.requireNonNull(getSupportActionBar()).hide();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(
                android.R.color.transparent,
                getTheme()
        ));
    }
}