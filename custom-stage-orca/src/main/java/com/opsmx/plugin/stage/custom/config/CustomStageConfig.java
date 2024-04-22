package com.opsmx.plugin.stage.custom.config;

import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration;

@PluginConfiguration
public class CustomStageConfig {

    private String datadogApiKey;

    private String datadogAppKey;

    public CustomStageConfig(){}
    public CustomStageConfig(String datadogApiKey, String datadogAppKey) {
        this.datadogApiKey = datadogApiKey;
        this.datadogAppKey = datadogAppKey;
    }

    public String getDatadogApiKey() {
        return datadogApiKey;
    }

    public void setDatadogApiKey(String datadogApiKey) {
        this.datadogApiKey = datadogApiKey;
    }

    public String getDatadogAppKey() {
        return datadogAppKey;
    }

    public void setDatadogAppKey(String datadogAppKey) {
        this.datadogAppKey = datadogAppKey;
    }
}
