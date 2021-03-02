package com.opsmx.plugin.stage.custom;

import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration;

@PluginConfiguration
public class CustomStageConfig {

    public String getDefaultVmDetails() {
        return defaultVmDetails;
    }

    public void setDefaultVmDetails(String defaultVmDetails) {
        this.defaultVmDetails = defaultVmDetails;
    }

    public String getDefaultFilename() {
        return defaultFilename;
    }

    public void setDefaultFilename(String defaultFilename) {
        this.defaultFilename = defaultFilename;
    }

    public String getDefaultGitAccount() {
        return defaultGitAccount;
    }

    public void setDefaultGitAccount(String defaultGitAccount) {
        this.defaultGitAccount = defaultGitAccount;
    }

    private String defaultVmDetails;

    private String defaultFilename;

    private String defaultGitAccount;

    public CustomStageConfig(){}

    public CustomStageConfig(String vmDetails, String filename, String gitAccount) {
        this.defaultVmDetails = vmDetails;
        this.defaultFilename = filename;
        this.defaultGitAccount = gitAccount;
    }
}
