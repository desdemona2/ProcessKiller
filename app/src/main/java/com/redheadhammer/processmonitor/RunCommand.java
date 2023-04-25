package com.redheadhammer.processmonitor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.topjohnwu.superuser.Shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunCommand {
    private static final String TAG = "ReadScript";

    // This method will read whatever we have in our script file on path fileName;
    private static String readScript(Context context, String fileName){
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
        } catch (IOException ex) {
            Log.d(TAG, "Error in getScript: " + ex.getClass().getSimpleName());
            ex.printStackTrace();
        }
        String script = null;
        try {
            if (inputStream != null) {
                script = readStream(inputStream);
            }
        } catch (IOException ex) {
            Log.d(TAG, "IOException in getScript: " + ex.getClass().getSimpleName());
            ex.printStackTrace();
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return script;
    }

    // Helper method to read the contents of an input stream into a string
    private static String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString("UTF-8");
    }

    public static List<ProcessInfo> getProcesses(Context context, String filePath) {
        List<ProcessInfo> processes = new ArrayList<>();

//        String content = readScript(context, filePath);
        String content;
        if (filePath.contains("mem")) {
            content = "ps -A -o pid,name,%cpu,%mem --sort=-%mem | grep -v system | grep -v root | head -n 21";
        } else {
            content = "ps -A -o pid,name,%cpu,%mem --sort=-%cpu | grep -v system | grep -v root | head -n 21";
        }

        List<String> result = runScript(content);
        for (int i  = 1; i < result.size(); i++) {
            String proc = result.get(i).trim();
            String[] curr = proc.split("[ ]+");
            String appName = getAppNameFromPkgName(context, curr[1]);
            Log.d(TAG, "getProcesses: " + Arrays.toString(curr));
            processes.add(
                    new ProcessInfo(
                            curr[0],
                            appName,
                            "cpu: " + curr[2],
                            "mem: " + curr[3],
                            curr[1]
                    )
            );
        }
        return processes;
    }

    public static String getAppNameFromPkgName(Context context, String Packagename) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(Packagename, PackageManager.GET_META_DATA);
            return (String) packageManager.getApplicationLabel(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Packagename;
        }
    }

    public static void killProcess(Context context, String filePath, String pid) {
//        String content = readScript(context, filePath);
        String script = "kill -9 " + pid;
        Shell.Result result = Shell.cmd(script).exec();
        Toast.makeText(context, "Process killed: " + pid + " " + (result.getCode() == 0), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "killProcess: " + result.getErr());
    }

    private static List<String> runScript(String script) {
        List<String> output = new ArrayList<>();
        Shell.cmd(script).to(output).exec();

        return output;
    }
}
