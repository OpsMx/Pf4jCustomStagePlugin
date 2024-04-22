package com.opsmx.plugin.stage.custom.monitor;

public interface MonitorClient {
    static MonitorClient create(String datadogApiKey, String datadogAppKey){
        return new MonitorClientImpl(datadogApiKey,datadogAppKey);
    }

}
