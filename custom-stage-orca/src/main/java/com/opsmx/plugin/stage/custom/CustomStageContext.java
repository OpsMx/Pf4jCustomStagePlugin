package com.opsmx.plugin.stage.custom;

public class CustomStageContext {

    public String getVmDetails() {
        return vmDetails;
    }

    public void setVmDetails(String vmDetails) {
        this.vmDetails = vmDetails;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String vmDetails;

    private String filename;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    private String payload;

    public CustomStageContext(){}

    public CustomStageContext(String vmDetails, String filename, String payload) {
        this.vmDetails = vmDetails;
        this.filename = filename;
        this.payload = payload;
    }
}
