package com.asyncsite.locks.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "asyncsite.lock")
public class LockProperties {
    private boolean enabled = true;
    private String prefix = "asyncsite:lock";
    private long defaultWaitMs = 3000;
    private long defaultLeaseMs = 3000;
    private long backoffMs = 50;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public long getDefaultWaitMs() { return defaultWaitMs; }
    public void setDefaultWaitMs(long defaultWaitMs) { this.defaultWaitMs = defaultWaitMs; }
    public long getDefaultLeaseMs() { return defaultLeaseMs; }
    public void setDefaultLeaseMs(long defaultLeaseMs) { this.defaultLeaseMs = defaultLeaseMs; }
    public long getBackoffMs() { return backoffMs; }
    public void setBackoffMs(long backoffMs) { this.backoffMs = backoffMs; }
}
