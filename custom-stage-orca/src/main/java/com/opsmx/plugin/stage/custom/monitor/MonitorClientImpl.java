package com.opsmx.plugin.stage.custom.monitor;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.v1.api.MonitorsApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static com.datadog.api.client.ApiClient.getDefaultApiClient;


public class MonitorClientImpl implements  MonitorClient {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MonitorsApi apiClient;

    public MonitorClientImpl(String datadogApiKey, String datadogAppKey){
        this(datadogApiKey, datadogAppKey, true);
    }
    public MonitorClientImpl(String datadogApiKey, String datadogAppKey, boolean isPrivateLink){
       this(datadogApiKey, datadogAppKey, "https://api.datadoghq.com");
    }

    protected MonitorClientImpl(String datadogApiKey, String datadogAppKey, String basePath){
        log.info("Start of the MonitorClientImpl constructor");
        HashMap secrets = new HashMap();
        secrets.put("apiKeyAuth", datadogApiKey);
        secrets.put("appKeyAuth",datadogAppKey);
        log.info("Before getting the DefaultApiClient");
        ApiClient defaultClient = getDefaultApiClient();
        log.info("After getting the DefaultApiClient");
        defaultClient.configureApiKeys(secrets);
        defaultClient.setBasePath(basePath);
        apiClient = new MonitorsApi(defaultClient);
        log.info("End of the MonitorClientImpl constructor");
    }

    public MonitorsApi getApiClient() {
        return apiClient;
    }
}
