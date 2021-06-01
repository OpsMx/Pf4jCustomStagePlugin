package com.opsmx.plugin.stage.custom;

public class VerificationGateContext {

    private String gateUrl;
    private String lifeTimeHours;
    private Integer minimumCanaryResult;
    private Integer canaryResultScore;
    private String gateName;
    private String imageIds;
    private Boolean logAnalysis;
    private Boolean metricAnalysis;
    private String baselineStartTime;
    private String canaryStartTime;

    public VerificationGateContext(String gateUrl, String lifeTimeHours, Integer minimumCanaryResult, Integer canaryResultScore, String gateName, String imageIds, Boolean logAnalysis, Boolean metricAnalysis, String baselineStartTime, String canaryStartTime) {
        this.gateUrl = gateUrl;
        this.lifeTimeHours = lifeTimeHours;
        this.minimumCanaryResult = minimumCanaryResult;
        this.canaryResultScore = canaryResultScore;
        this.gateName = gateName;
        this.imageIds = imageIds;
        this.logAnalysis = logAnalysis;
        this.metricAnalysis = metricAnalysis;
        this.baselineStartTime = baselineStartTime;
        this.canaryStartTime = canaryStartTime;
    }

    public String getGateUrl() {
        return gateUrl;
    }

    public void setGateUrl(String gateUrl) {
        this.gateUrl = gateUrl;
    }

    public String getLifeTimeHours() {
        return lifeTimeHours;
    }

    public void setLifeTimeHours(String lifeTimeHours) {
        this.lifeTimeHours = lifeTimeHours;
    }

    public Integer getMinimumCanaryResult() {
        return minimumCanaryResult;
    }

    public void setMinimumCanaryResult(Integer minimumCanaryResult) {
        this.minimumCanaryResult = minimumCanaryResult;
    }

    public Integer getCanaryResultScore() {
        return canaryResultScore;
    }

    public void setCanaryResultScore(Integer canaryResultScore) {
        this.canaryResultScore = canaryResultScore;
    }

    public String getGateName() {
        return gateName;
    }

    public void setGateName(String gateName) {
        this.gateName = gateName;
    }

    public String getImageIds() {
        return imageIds;
    }

    public void setImageIds(String imageIds) {
        this.imageIds = imageIds;
    }

    public Boolean getLogAnalysis() {
        return logAnalysis;
    }

    public void setLogAnalysis(Boolean logAnalysis) {
        this.logAnalysis = logAnalysis;
    }

    public Boolean getMetricAnalysis() {
        return metricAnalysis;
    }

    public void setMetricAnalysis(Boolean metricAnalysis) {
        this.metricAnalysis = metricAnalysis;
    }

    public String getBaselineStartTime() {
        return baselineStartTime;
    }

    public void setBaselineStartTime(String baselineStartTime) {
        this.baselineStartTime = baselineStartTime;
    }

    public String getCanaryStartTime() {
        return canaryStartTime;
    }

    public void setCanaryStartTime(String canaryStartTime) {
        this.canaryStartTime = canaryStartTime;
    }
}
