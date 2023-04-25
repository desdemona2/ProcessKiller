package com.redheadhammer.processmonitor;

public class ProcessInfo {
    private final String pid;
    private final String appName;
    private final String cpuUsage;
    private final String memUsage;
    private final String packageName;

    public ProcessInfo(String pid, String appName, String cpuUsage, String memUsage, String packageName) {
        this.pid = pid;
        this.appName = appName;
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.packageName = packageName;
    }

    public String getPid() {
        return pid;
    }

    public String getAppName() {
        return appName;
    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public String getMemUsage() {
        return memUsage;
    }

    public String getPackageName() {
        return packageName;
    }
}

